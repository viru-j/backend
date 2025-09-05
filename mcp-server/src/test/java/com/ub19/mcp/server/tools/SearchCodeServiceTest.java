package com.ub19.mcp.server.tools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    @Test
    void searchReturnsEmptyWhenNoHits() throws Exception {
        LuceneService lucene = Mockito.mock(LuceneService.class);
        Embedder embedder = text -> new float[] {0f};
        TopDocs empty = new TopDocs(new TotalHits(0, TotalHits.Relation.EQUAL_TO), new ScoreDoc[0]);
        when(lucene.searchBM25("q", 5)).thenReturn(empty);
        when(lucene.searchKNN(any(float[].class), eq(5))).thenReturn(empty);
        when(lucene.rrfFuse(empty, empty, 5)).thenReturn(empty);

        SearchCodeService service = new SearchCodeService(lucene, embedder);
        List<CodeHit> hits = service.search("q", 5);
        assertTrue(hits.isEmpty());
    }

    @Test
    void searchHandlesOverlappingChunks() throws Exception {
        LuceneService lucene = Mockito.mock(LuceneService.class);
        Embedder embedder = text -> new float[] {0f};
        ScoreDoc sd1 = new ScoreDoc(1, 1f);
        ScoreDoc sd2 = new ScoreDoc(2, 0.9f);
        TopDocs fused = new TopDocs(new TotalHits(2, TotalHits.Relation.EQUAL_TO), new ScoreDoc[] { sd1, sd2 });
        when(lucene.searchBM25("q", 2)).thenReturn(fused);
        when(lucene.searchKNN(any(float[].class), eq(2))).thenReturn(fused);
        when(lucene.rrfFuse(fused, fused, 2)).thenReturn(fused);
        Document d1 = new Document();
        d1.add(new StringField("file_path", "F.java", Field.Store.YES));
        d1.add(new StoredField("line_start", 1));
        d1.add(new StoredField("line_end", 10));
        d1.add(new StoredField("contents", "a"));
        Document d2 = new Document();
        d2.add(new StringField("file_path", "F.java", Field.Store.YES));
        d2.add(new StoredField("line_start", 5));
        d2.add(new StoredField("line_end", 15));
        d2.add(new StoredField("contents", "b"));
        when(lucene.doc(1)).thenReturn(d1);
        when(lucene.doc(2)).thenReturn(d2);

        SearchCodeService service = new SearchCodeService(lucene, embedder);
        List<CodeHit> hits = service.search("q", 2);
        assertEquals(2, hits.size());
        assertEquals(1, hits.get(0).lineStart());
        assertEquals(5, hits.get(1).lineStart());
    }

    @Test
    void searchTrimsLongSnippets() throws Exception {
        LuceneService lucene = Mockito.mock(LuceneService.class);
        Embedder embedder = text -> new float[] {0f};
        ScoreDoc sd = new ScoreDoc(1, 1f);
        TopDocs fused = new TopDocs(new TotalHits(1, TotalHits.Relation.EQUAL_TO), new ScoreDoc[] { sd });
        when(lucene.searchBM25("q", 1)).thenReturn(fused);
        when(lucene.searchKNN(any(float[].class), eq(1))).thenReturn(fused);
        when(lucene.rrfFuse(fused, fused, 1)).thenReturn(fused);
        String longSnippet = "x".repeat(500);
        Document doc = new Document();
        doc.add(new StringField("file_path", "F.java", Field.Store.YES));
        doc.add(new StoredField("line_start", 1));
        doc.add(new StoredField("line_end", 2));
        doc.add(new StoredField("contents", longSnippet));
        when(lucene.doc(1)).thenReturn(doc);

        SearchCodeService service = new SearchCodeService(lucene, embedder);
        List<CodeHit> hits = service.search("q", 1);
        assertEquals(200, hits.get(0).snippet().length());
        assertTrue(hits.get(0).snippet().endsWith("..."));
    }
}

