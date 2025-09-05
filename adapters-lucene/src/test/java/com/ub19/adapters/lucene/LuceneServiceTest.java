package com.ub19.adapters.lucene;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHits;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LuceneServiceTest {

    private LuceneService service;
    private Path indexPath;

    @BeforeEach
    void setup() throws IOException {
        indexPath = Files.createTempDirectory("lucene-test");
        service = new LuceneService(indexPath);
    }

    @AfterEach
    void cleanup() throws IOException {
        service.close();
        Files.walk(indexPath)
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete);
    }

    @Test
    void searchBM25ReturnsRelevantDoc() throws Exception {
        service.addOrUpdateDocument("repo", "f1", "s1", "hello world", "1", 1, 2, List.of(), null);
        service.addOrUpdateDocument("repo", "f2", "s2", "goodbye world", "2", 1, 2, List.of(), null);

        TopDocs docs = service.searchBM25("hello", 5);
        Document top = service.doc(docs.scoreDocs[0].doc);
        assertEquals("1", top.get("chunk_id"));
    }

    @Test
    void searchKNNReturnsNearestVector() throws Exception {
        service.addOrUpdateDocument("repo", "f1", "s1", "", "1", 1, 2, List.of(), new float[] {1f, 0f});
        service.addOrUpdateDocument("repo", "f2", "s2", "", "2", 1, 2, List.of(), new float[] {0f, 1f});

        TopDocs docs = service.searchKNN(new float[] {1f, 0f}, 1);
        Document top = service.doc(docs.scoreDocs[0].doc);
        assertEquals("1", top.get("chunk_id"));
    }

    @Test
    void rrfFusePrefersConsensus() {
        TopDocs bm25 = new TopDocs(new TotalHits(2, TotalHits.Relation.EQUAL_TO),
                new ScoreDoc[] { new ScoreDoc(1, 2f), new ScoreDoc(2, 1f) });
        TopDocs knn = new TopDocs(new TotalHits(2, TotalHits.Relation.EQUAL_TO),
                new ScoreDoc[] { new ScoreDoc(2, 2f), new ScoreDoc(3, 1f) });

        TopDocs fused = service.rrfFuse(bm25, knn, 3);
        assertEquals(2, fused.scoreDocs[0].doc);
        assertEquals(1, fused.scoreDocs[1].doc);
        assertEquals(3, fused.scoreDocs[2].doc);
    }
}
