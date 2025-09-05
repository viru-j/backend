package com.ub19.mcp.server.lucene;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for Lucene index location.
 */
@ConfigurationProperties(prefix = "lucene")
public class LuceneProperties {

    private String indexPath;

    public String getIndexPath() {
        return indexPath;
    }

    public void setIndexPath(String indexPath) {
        this.indexPath = indexPath;
    }
}

