package carear.document.be.os.search.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.opensearch.client.opensearch._types.aggregations.AggregateVariant;
import org.opensearch.client.opensearch._types.aggregations.StringTermsAggregate;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.stereotype.Service;

import carear.document.be.dto.ApiResponseDto;
import carear.document.be.dto.PagingDto;
import carear.document.be.os.search.Search;
import carear.document.be.os.search.dto.SearchFormDto;
import carear.document.be.os.search.dto.SearchRequestDto;
import carear.document.be.os.search.dto.SearchResponseDto;
import carear.document.be.util.StringUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AggregationSearchService extends SearchService {

    private String SEARCHTYPE = "aggregation";

    public String getSearchType() {
        return SEARCHTYPE;
    }

    private final Search SEARCH;

    public ApiResponseDto msearch(SearchFormDto searchFormDto, SearchRequestDto requestDto) {
		return ApiResponseDto.success(null);
	}

    public ApiResponseDto search (SearchFormDto searchFormDto, SearchRequestDto requestDTO) {
		SearchResponseDto responseDTO = new SearchResponseDto();
		
		int totalCount = 0;
		
		SearchResponse<Map> searchResponse = SEARCH.search(searchFormDto);
		String index = searchFormDto.getIndex();
		String aggrName = searchFormDto.getAggrField().get(index).get(0).split(StringUtil.SLASH)[0];

        // 집계 결과에서 buckets 추출
		AggregateVariant aggVariant = searchResponse.aggregations().get(aggrName)._get();

		if (aggVariant instanceof StringTermsAggregate) {
			StringTermsAggregate termsAgg = (StringTermsAggregate) aggVariant;
					
			List<Map<String,Object>> results = termsAgg.buckets().array().stream()
					.map(bucket -> {
						Map<String, Object> bucketResult = new HashMap<>();
                           bucketResult.put("key", bucket.key());
						bucketResult.put("count", bucket.docCount());
						return bucketResult;
					})
					.collect(Collectors.toList());
			responseDTO.setResult(results);
		}
		totalCount = Long.valueOf(searchResponse.hits().total().value()).intValue();
		
		
		PagingDto pagingDto = PagingDto.builder()
				.page(searchFormDto.getPage())
				.pageSize(1) 		// size 0 일 경우 1로 설정
				.build();
		pagingDto.setPaging(totalCount);
			
		responseDTO.setPage(pagingDto);
		responseDTO.setRequest(requestDTO);
		
		return ApiResponseDto.success(responseDTO);
	}
}
