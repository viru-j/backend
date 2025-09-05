package com.ub19.indexer;

import com.ub19.adapters.lucene.LuceneService;
import com.ub19.indexer.AstExtractor.MethodInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Command-line runner that executes the indexing pipeline.
 */
@Component
public class IndexerRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(IndexerRunner.class);

    private final RepoScanner scanner = new RepoScanner();
    private final AstExtractor extractor = new AstExtractor();
    private final Embedder embedder = new FixedEmbedder();

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String repoArg = args.getOptionValues("repo") != null ? args.getOptionValues("repo").get(0) : null;
        String indexArg = args.getOptionValues("index") != null ? args.getOptionValues("index").get(0) : null;
        if (repoArg == null || indexArg == null) {
            log.error("Usage: --repo=<path> --index=<path>");
            return;
        }
        Path repo = Path.of(repoArg);
        Path index = Path.of(indexArg);
        try (LuceneService lucene = new LuceneService(index)) {
            execute(repo, lucene);
        }
    }

    private void execute(Path repo, LuceneService lucene) throws IOException {
        List<Path> files = scanner.scan(repo);
        AtomicInteger docs = new AtomicInteger();
        AtomicInteger methods = new AtomicInteger();
        AtomicInteger sections = new AtomicInteger();
        for (Path file : files) {
            String lower = file.getFileName().toString().toLowerCase();
            Path relative = repo.relativize(file);
            if (lower.endsWith(".java")) {
                List<MethodInfo> infos = extractor.extract(file);
                for (MethodInfo info : infos) {
                    String snippet = readSnippet(file, info.lineStart(), info.lineEnd());
                    float[] vec = embedder.embed(snippet);
                    lucene.addOrUpdateDocument(repo.toString(), relative.toString(), info.methodFqn(), snippet,
                            info.methodFqn(), info.lineStart(), info.lineEnd(), info.endpointAnnotations(), vec);
                    docs.incrementAndGet();
                    methods.incrementAndGet();
                }
            } else {
                String content = Files.readString(file);
                float[] vec = embedder.embed(content);
                String chunkId = relative.toString();
                lucene.addOrUpdateDocument(repo.toString(), relative.toString(), "", content,
                        chunkId, 1, countLines(content), List.of(), vec);
                docs.incrementAndGet();
                sections.incrementAndGet();
            }
        }
        log.info("Indexed {} documents ({} methods, {} sections)", docs.get(), methods.get(), sections.get());
    }

    private static String readSnippet(Path file, int start, int end) throws IOException {
        List<String> lines = Files.readAllLines(file);
        StringBuilder sb = new StringBuilder();
        for (int i = start - 1; i < end && i < lines.size(); i++) {
            sb.append(lines.get(i)).append("\n");
        }
        return sb.toString();
    }

    private static int countLines(String content) {
        return (int) content.lines().count();
    }
}
