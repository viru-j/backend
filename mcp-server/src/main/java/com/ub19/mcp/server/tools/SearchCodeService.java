package com.ub19.mcp.server.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ub19.adapters.lucene.LuceneService;
import com.ub19.mcp.server.embed.Embedder;
import com.ub19.shared.model.dto.CodeHit;
import com.ub19.shared.model.error.ApiError;
import com.ub19.shared.model.error.ApiException;

/**
 * Performs hybrid Lucene searches combining BM25 and vector similarity.
 */
@Service
public class SearchCodeService {

    private final LuceneService luceneService;
    private final Embedder embedder;
    private static final int MAX_SNIPPET = 200;

    public SearchCodeService(LuceneService luceneService, Embedder embedder) {
        this.luceneService = luceneService;
        this.embedder = embedder;
    }

    public List<CodeHit> search(String query, int topK) {
        try {
            float[] vector = embedder.embed(query);
            TopDocs bm25 = luceneService.searchBM25(query, topK);
            TopDocs knn = luceneService.searchKNN(vector, topK);
            TopDocs fused = luceneService.rrfFuse(bm25, knn, topK);
            List<CodeHit> hits = new ArrayList<>();
            for (ScoreDoc sd : fused.scoreDocs) {
                Document doc = luceneService.doc(sd.doc);
                String filePath = doc.get("file_path");
                int lineStart = Integer.parseInt(doc.get("line_start"));
                int lineEnd = Integer.parseInt(doc.get("line_end"));
                String snippet = trimSnippet(doc.get("contents"));
                hits.add(new CodeHit(filePath, lineStart, lineEnd, snippet, sd.score));
            }
            return hits;
        } catch (IOException | ParseException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    new ApiError("SEARCH_FAILED", "Search execution failed"));
        }
    }

    private String trimSnippet(String snippet) {
        if (snippet == null) {
            return "";
        }
        if (snippet.length() <= MAX_SNIPPET) {
            return snippet;
        }
        return snippet.substring(0, MAX_SNIPPET - 3) + "...";
    }
}

