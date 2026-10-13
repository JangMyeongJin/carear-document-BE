package carear.document.be.os.search.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.opensearch.client.opensearch.core.SearchResponse;

import carear.document.be.os.search.Search;
import carear.document.be.os.search.dto.SearchFormDto;
import carear.document.be.os.search.dto.SearchRequestDto;
import carear.document.be.os.search.dto.SearchResponseDto;

class QuerySearchServiceTests {
    @Test
    void highlightsOverwriteMatchingFieldsAndKeepUnmatchedSource() {
        Search search = mock(Search.class);
        SearchFormDto form = new SearchFormDto();
        form.setPage(1);
        form.setSize(6);
        Map<String, Object> source = Map.of("title", "원본", "description", "개요", "role", "개발");
        SearchResponse<Map> response = new SearchResponse.Builder<Map>()
            .took(1).timedOut(false)
            .shards(shards -> shards.total(1).successful(1).failed(0))
            .hits(hits -> hits.total(total -> total.value(1).relation(org.opensearch.client.opensearch.core.search.TotalHitsRelation.Eq))
                .hits(hit -> hit.index("project").id("p1").source(source)
                    .highlight(Map.of("title", List.of("<span class='highLight'>검색</span>"),
                        "description.ngram", List.of("ngram"), "description", List.of("기본"),
                        "description.exact", List.of("exact"))))).build();
        when(search.search(form)).thenReturn(response);

        SearchResponseDto data = (SearchResponseDto) new QuerySearchService(search)
            .search(form, new SearchRequestDto()).getData();
        Map<String, Object> result = data.getResult().get(0);
        assertEquals("<span class='highLight'>검색</span>", result.get("title"));
        assertEquals("ngram", result.get("description"));
        assertEquals("개발", result.get("role"));
        assertFalse(result.containsKey("highlight"));
        assertEquals("원본", source.get("title"));
    }
}
