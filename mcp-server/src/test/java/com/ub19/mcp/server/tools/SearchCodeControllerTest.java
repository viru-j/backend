package com.ub19.mcp.server.tools;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.ub19.adapters.lucene.LuceneService;
import com.ub19.mcp.server.embed.Embedder;
import com.ub19.mcp.server.embed.FixedEmbedder;

@WebMvcTest(SearchCodeController.class)
@Import({SearchCodeService.class, FixedEmbedder.class, SearchCodeControllerTest.TestConfig.class})
class SearchCodeControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    LuceneService luceneService;

    static Path indexDir;

    static class TestConfig {
        @Bean(destroyMethod = "close")
        LuceneService luceneService(Embedder embedder) throws Exception {
            indexDir = Files.createTempDirectory("lucene-controller-test");
            LuceneService service = new LuceneService(indexDir);
            float[] vec = embedder.embed("hello");
            service.addOrUpdateDocument("repo", "F.java", "s1", "hello world", "1", 1, 2, List.of(), vec);
            return service;
        }
    }

    @AfterEach
    void cleanup() throws Exception {
        luceneService.close();
        Files.walk(indexDir).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
    }

    @Test
    void postReturnsHits() throws Exception {
        mvc.perform(post("/tools/search_code")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"query\":\"hello\",\"topK\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hits[0].filePath").value("F.java"));
    }
}

