package com.ub19.adapters.lucene;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import net.jqwik.api.Assume;
import net.jqwik.api.ForAll;

import net.jqwik.api.Property;

import net.jqwik.api.constraints.IntRange;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHits;

/**
 * Property-based tests for ranking fusion.
 */
public class LuceneServicePropertyTest {

    @Property
    void consensusDocOutranksSingle(@ForAll @IntRange(min = 0, max = 20) int commonRank,
                                    @ForAll @IntRange(min = 0, max = 20) int singleRank) throws IOException {
        Assume.that(singleRank >= commonRank);
        TopDocs bm25 = topDocsWithTwo(commonRank, singleRank);
        TopDocs knn = topDocsWithOne(commonRank);
        Path dir = Files.createTempDirectory("lucene");
        try (LuceneService service = new LuceneService(dir)) {
            TopDocs fused = service.rrfFuse(bm25, knn, 2);
            assertEquals(1, fused.scoreDocs[0].doc);
        }
    }

    private TopDocs topDocsWithOne(int rank) {
        int size = rank + 1;
        ScoreDoc[] arr = new ScoreDoc[size];
        for (int i = 0; i < size; i++) {
            arr[i] = new ScoreDoc(100 + i, 1f);
        }
        arr[rank] = new ScoreDoc(1, 1f);
        return new TopDocs(new TotalHits(size, TotalHits.Relation.EQUAL_TO), arr);
    }

    private TopDocs topDocsWithTwo(int rank1, int rank2) {
        int size = Math.max(rank1, rank2) + 1;
        ScoreDoc[] arr = new ScoreDoc[size];
        for (int i = 0; i < size; i++) {
            arr[i] = new ScoreDoc(200 + i, 1f);
        }
        arr[rank1] = new ScoreDoc(1, 1f);
        arr[rank2] = new ScoreDoc(2, 1f);
        return new TopDocs(new TotalHits(size, TotalHits.Relation.EQUAL_TO), arr);
    }
}
