package com.ub19.mcp.server.lucene;

import java.io.IOException;
import java.nio.file.Paths;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ub19.adapters.lucene.LuceneService;

/**
 * Creates a {@link LuceneService} bean backed by the configured index path.
 */
@Configuration
@EnableConfigurationProperties(LuceneProperties.class)
public class LuceneConfig {

    @Bean(destroyMethod = "close")
    public LuceneService luceneService(LuceneProperties properties) throws IOException {
        return new LuceneService(Paths.get(properties.getIndexPath()));
    }
}

