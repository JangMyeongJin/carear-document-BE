package carear.document.be.os.search.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.opensearch.client.opensearch.core.MsearchResponse;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.stereotype.Service;

import carear.document.be.dto.ApiResponseDto;
import carear.document.be.dto.PagingDto;
import carear.document.be.os.Properties;
import carear.document.be.os.search.Search;
import carear.document.be.os.search.dto.SearchFormDto;
import carear.document.be.os.search.dto.SearchRequestDto;
import carear.document.be.os.search.dto.SearchResponseDto;
import carear.document.be.util.StringUtil;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class QuerySearchService extends SearchService {

	private String SEARCHTYPE = "query";
	
	public String getSearchType() {
		return SEARCHTYPE;
	} 

	private final Search SEARCH;

	private static Properties PROPERTIES = new Properties();

    public ApiResponseDto msearch(SearchFormDto searchFormDto, SearchRequestDto requestDto) {
        SearchResponseDto responseDto = new SearchResponseDto();
		
		int totalCount = 0;
		
		// 통합검색
		if(searchFormDto.getIndexes().length > 0) {
			MsearchResponse<Map> searchResponse = SEARCH.msearch(searchFormDto);

			List<Map<String, Object>> mresults = new ArrayList<>();
		   	String[] indexes = searchFormDto.getIndexes();

			for (int i = 0; i < searchResponse.responses().size(); i++) {
				// 성공한 경우에만 처리
				if (searchResponse.responses().get(i).result() != null) {
					SearchResponse<Map> singleResponse = searchResponse.responses().get(i).result();
			
					List<Map<String, Object>> hitsList = singleResponse.hits().hits().stream()
						.map(hit -> {
							Map<String, Object> result = new HashMap<>();
							if (hit.source() != null) {
								result.putAll(hit.source());
							}
							if (hit.highlight() != null) {
								List<String> defaultFields = PROPERTIES.getDefaultField(hit.index());
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
						.collect(Collectors.toList());
			
					// 인덱스별 결과 Map 생성
					Map<String, Object> indexResult = new HashMap<>();
					indexResult.put("index", indexes[i]);
					indexResult.put("count", singleResponse.hits().total() != null ? singleResponse.hits().total().value() : 0);
					indexResult.put("data", hitsList);
			
					mresults.add(indexResult);
			
					totalCount += Long.valueOf(indexResult.get("count").toString());
				} else {
					// 실패한 경우
					Map<String, Object> errorResult = new HashMap<>();
					errorResult.put("index", indexes[i]);
					errorResult.put("count", 0);
					errorResult.put("data", new ArrayList<>());
					errorResult.put("error", "error");
					mresults.add(errorResult);
				}
			}

			responseDto.setResult(mresults);
		}
		
		PagingDto pagingDto = PagingDto.builder()
				.page(searchFormDto.getPage())
				.pageSize(searchFormDto.getSize())
				.build();
			pagingDto.setPaging(totalCount);
			
		responseDto.setPage(pagingDto);
		responseDto.setRequest(requestDto);
		
		return ApiResponseDto.success(responseDto);
    }

	public ApiResponseDto search(SearchFormDto searchFormDto, SearchRequestDto requestDto) {
		return ApiResponseDto.success(null);
	}
}
