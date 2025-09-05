package com.ub19.mcp.server.tools;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ub19.shared.model.dto.CodeHit;
import com.ub19.shared.model.dto.SearchRequest;
import com.ub19.shared.model.dto.SearchResponse;

import jakarta.validation.Valid;

/**
 * REST endpoint exposing the search_code tool.
 */
@RestController
@RequestMapping("/tools")
public class SearchCodeController {

    private final SearchCodeService service;

    public SearchCodeController(SearchCodeService service) {
        this.service = service;
    }

    @PostMapping("/search_code")
    public SearchResponse search(@RequestBody @Valid SearchRequest request) {
        List<CodeHit> hits = service.search(request.query(), request.topK());
        return new SearchResponse(hits);
    }
}

