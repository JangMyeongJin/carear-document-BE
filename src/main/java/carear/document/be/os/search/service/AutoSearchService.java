package carear.document.be.os.search.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import carear.document.be.dto.ApiResponseDto;
import carear.document.be.dto.PagingDto;
import carear.document.be.os.AutoProperties;
import carear.document.be.os.search.Search;
import carear.document.be.os.search.dto.SearchFormDto;
import carear.document.be.os.search.dto.SearchRequestDto;
import carear.document.be.os.search.dto.SearchResponseDto;
import carear.document.be.util.StringUtil;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AutoSearchService extends SearchService {

	private String SEARCHTYPE = "auto";
	
	public String getSearchType() {
		return SEARCHTYPE;
	} 

	private final Search SEARCH;

	@Autowired
	private AutoProperties AUTOPROPERTIES;

    public ApiResponseDto msearch(SearchFormDto searchFormDto, SearchRequestDto requestDto) {
		return ApiResponseDto.success(null);
	}

	public ApiResponseDto search(SearchFormDto searchFormDto, SearchRequestDto requestDto) {

		SearchResponseDto responseDto = new SearchResponseDto();
		
		int totalCount = 0;
		
		SearchResponse<Map> searchResponse = SEARCH.search(searchFormDto);

		responseDto.setResult(
			searchResponse.hits().hits().stream()
				.map(hit -> {
					Map<String, Object> result = new HashMap<>();
					if (hit.source() != null) {
						result.putAll(hit.source());
					}
						// 하이라이트 ngram, 기본필드, exact 순으로 추출
					if (hit.highlight() != null) {
						List<String> defaultFields = AUTOPROPERTIES.getDefaultField(hit.index());
						Map<String, List<String>> highlight = hit.highlight();
						Map<String, List<String>> mergedHighlight = new HashMap<>();
							for(String field : defaultFields) {
							if(field.contains(StringUtil.SLASH)) {
								field = field.replace(StringUtil.SLASH, ".");
							}
							if(highlight.containsKey(field + ".ngram")) {
								mergedHighlight.put(field, highlight.get(field + ".ngram"));
							} else if(highlight.containsKey(field)) {
								mergedHighlight.put(field, highlight.get(field));
							} else if(highlight.containsKey(field + ".exact")) {
								mergedHighlight.put(field, highlight.get(field + ".exact"));
							}
						}
						result.put("highlight", mergedHighlight);
					}
					return result;
				})
				.collect(Collectors.toList())
		);
		totalCount = Long.valueOf(searchResponse.hits().total().value()).intValue();
			
		
		PagingDto pagingDto = PagingDto.builder()
				.page(searchFormDto.getPage())
				.pageSize(searchFormDto.getSize())
				.build();
		pagingDto.setPaging(totalCount);
			
		responseDto.setPage(pagingDto);
		responseDto.setRequest(requestDto);
		
		return ApiResponseDto.success(responseDto);
	}
}
