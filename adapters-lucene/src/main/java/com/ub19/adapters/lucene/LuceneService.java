package com.ub19.adapters.lucene;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.KnnVectorField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.KnnVectorQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHits;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;

/**
 * Service wrapper around Lucene providing indexing and search utilities
 * for BM25 and KNN based retrieval.
 */
public class LuceneService implements Closeable {

    private static final String FIELD_REPO_PATH = "repo_path";
    private static final String FIELD_FILE_PATH = "file_path";
    private static final String FIELD_SYMBOL_FQN = "symbol_fqn";
    private static final String FIELD_CONTENTS = "contents";
    private static final String FIELD_CHUNK_ID = "chunk_id";
    private static final String FIELD_LINE_START = "line_start";
    private static final String FIELD_LINE_END = "line_end";
    private static final String FIELD_TAGS = "tags";
    private static final String FIELD_VECTOR = "vector";

    private final Directory directory;
    private final Analyzer analyzer;
    private final IndexWriter indexWriter;

    public LuceneService(Path indexPath) throws IOException {
        this(indexPath, new StandardAnalyzer());
    }

    public LuceneService(Path indexPath, Analyzer analyzer) throws IOException {
        Objects.requireNonNull(indexPath, "indexPath");
        this.analyzer = Objects.requireNonNull(analyzer, "analyzer");
        this.directory = FSDirectory.open(indexPath);
        IndexWriterConfig config = new IndexWriterConfig(this.analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        this.indexWriter = new IndexWriter(directory, config);
    }

    /**
     * Adds or updates a document in the index identified by its chunk id.
     */
    public void addOrUpdateDocument(String repoPath,
                                    String filePath,
                                    String symbolFqn,
                                    String contents,
                                    String chunkId,
                                    int lineStart,
                                    int lineEnd,
                                    Collection<String> tags,
                                    float[] vector) throws IOException {
        Document doc = new Document();
        doc.add(new StringField(FIELD_REPO_PATH, repoPath, Field.Store.NO));
        doc.add(new StringField(FIELD_FILE_PATH, filePath, Field.Store.YES));
        doc.add(new StringField(FIELD_SYMBOL_FQN, nullSafe(symbolFqn), Field.Store.NO));
        doc.add(new TextField(FIELD_CONTENTS, contents, Field.Store.YES));
        doc.add(new StringField(FIELD_CHUNK_ID, chunkId, Field.Store.YES));
        doc.add(new IntPoint(FIELD_LINE_START, lineStart));
        doc.add(new StoredField(FIELD_LINE_START, lineStart));
        doc.add(new IntPoint(FIELD_LINE_END, lineEnd));
        doc.add(new StoredField(FIELD_LINE_END, lineEnd));
        if (tags != null) {
            for (String tag : tags) {
                doc.add(new StringField(FIELD_TAGS, tag, Field.Store.NO));
            }
        }
        if (vector != null) {
            doc.add(new KnnVectorField(FIELD_VECTOR, vector));
        }
        indexWriter.updateDocument(new Term(FIELD_CHUNK_ID, chunkId), doc);
        indexWriter.commit();
    }

    /**
     * Executes a BM25 search using the contents field.
     */
    public TopDocs searchBM25(String queryText, int topK) throws IOException, ParseException {
        try (DirectoryReader reader = DirectoryReader.open(indexWriter)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            QueryParser parser = new QueryParser(FIELD_CONTENTS, analyzer);
            Query query = parser.parse(queryText);
            return searcher.search(query, topK);
        }
    }

    /**
     * Executes a KNN vector search on the vector field.
     */
    public TopDocs searchKNN(float[] embedding, int k) throws IOException {
        try (DirectoryReader reader = DirectoryReader.open(indexWriter)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            Query query = new KnnVectorQuery(FIELD_VECTOR, embedding, k);
            return searcher.search(query, k);
        }
    }

    /**
     * Retrieves a stored document by its Lucene id.
     */
    public Document doc(int docId) throws IOException {
        try (DirectoryReader reader = DirectoryReader.open(indexWriter)) {
            return reader.document(docId);
        }
    }

    /**
     * Fuses two TopDocs results using Reciprocal Rank Fusion and returns the top k results.
     */
    public TopDocs rrfFuse(TopDocs bm25, TopDocs knn, int k) {
        Map<Integer, Double> scores = new HashMap<>();
        accumulate(scores, bm25);
        accumulate(scores, knn);
        List<ScoreDoc> fused = scores.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(k)
                .map(e -> new ScoreDoc(e.getKey(), e.getValue().floatValue()))
                .collect(Collectors.toList());
        ScoreDoc[] array = fused.toArray(new ScoreDoc[0]);
        TotalHits totalHits = new TotalHits(array.length, TotalHits.Relation.EQUAL_TO);
        return new TopDocs(totalHits, array);
    }

    private void accumulate(Map<Integer, Double> scores, TopDocs docs) {
        int c = 60;
        ScoreDoc[] sd = docs.scoreDocs;
        for (int i = 0; i < sd.length; i++) {
            int docId = sd[i].doc;
            double score = 1.0 / (c + i + 1);
            scores.merge(docId, score, Double::sum);
        }
    }

    private static String nullSafe(String value) {
        return value == null ? "" : value;
    }

    @Override
    public void close() throws IOException {
        indexWriter.close();
        directory.close();
    }
}
