package com.ub19.mcp.server.tools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHits;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.ub19.adapters.lucene.LuceneService;
import com.ub19.mcp.server.embed.Embedder;
import com.ub19.shared.model.dto.CodeHit;

class SearchCodeServiceTest {

    @Test
    void searchReturnsHits() throws Exception {
        LuceneService lucene = Mockito.mock(LuceneService.class);
        Embedder embedder = text -> new float[] {0f};

        TopDocs bm25 = new TopDocs(new TotalHits(1, TotalHits.Relation.EQUAL_TO), new ScoreDoc[] { new ScoreDoc(1, 1f) });
        TopDocs knn = new TopDocs(new TotalHits(1, TotalHits.Relation.EQUAL_TO), new ScoreDoc[] { new ScoreDoc(1, 1f) });
        when(lucene.searchBM25("q", 1)).thenReturn(bm25);
        when(lucene.searchKNN(any(float[].class), eq(1))).thenReturn(knn);
        when(lucene.rrfFuse(bm25, knn, 1)).thenReturn(bm25);
        Document doc = new Document();
        doc.add(new StringField("file_path", "F.java", Field.Store.YES));
        doc.add(new StoredField("line_start", 1));
        doc.add(new StoredField("line_end", 2));
        doc.add(new StoredField("contents", "snippet"));
        when(lucene.doc(1)).thenReturn(doc);

        SearchCodeService service = new SearchCodeService(lucene, embedder);
        List<CodeHit> hits = service.search("q", 1);
        assertEquals(1, hits.size());
        CodeHit hit = hits.get(0);
        assertEquals("F.java", hit.filePath());
        assertEquals(1, hit.lineStart());
        assertEquals(2, hit.lineEnd());
        assertEquals("snippet", hit.snippet());
    }
}

