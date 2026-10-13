package carear.document.be.os.search.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import carear.document.be.os.AutoProperties;
import carear.document.be.os.Properties;
import carear.document.be.os.search.dto.SearchFormDto;
import carear.document.be.os.search.dto.SearchRequestDto;
import carear.document.be.os.data.dto.put.QueryLogRequestDto;
import carear.document.be.os.data.service.PutDataService;
import carear.document.be.os.search.service.SearchServiceFactory;
import carear.document.be.os.search.util.SearchFormUtil;
import carear.document.be.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/search")
public class SearchController {
	
	@Autowired
	private SearchServiceFactory searchServiceFactory;

	@Autowired
	private PutDataService putDataService;

	@Autowired
	private Properties PROPERTIES;

	@Autowired
	private AutoProperties AUTOPROPERTIES;

    @PostMapping("/query/{indexName}")
    public ResponseEntity<?> querySearch(@RequestBody SearchRequestDto requestDto, @PathVariable String indexName) {
		SearchFormDto searchFormDto = new SearchFormDto();

		searchFormDto.setQuery(SearchFormUtil.getSearchQuery(requestDto));
		searchFormDto.setIndex(indexName);
		searchFormDto.setIndexes(new String[]{indexName});

		searchFormDto.setSearchField(Map.of(indexName, PROPERTIES.getSearchField(indexName)));
		searchFormDto.setHighlightField(Map.of(indexName, PROPERTIES.getHighlightField(indexName)));

		// dateField 설정
		List<String> dateFields = new ArrayList<>();
		if(!requestDto.getStartDate().isEmpty()) {
			dateFields.add("startDate" + StringUtil.SLASH + requestDto.getStartDate() + StringUtil.SLASH + "gte");
		}

		if(!requestDto.getEndDate().isEmpty()) {
			dateFields.add("endDate" + StringUtil.SLASH + requestDto.getEndDate() + StringUtil.SLASH + "lte");
		}

		if(!dateFields.isEmpty()) {
			searchFormDto.setDateField(Map.of(indexName, dateFields));
		}

		if(!requestDto.getSort().isEmpty()) {
			searchFormDto.setSortField(Map.of(indexName, List.of(requestDto.getSort())));
		}

		if(requestDto.getFilter().equals("Y")) {
			searchFormDto.setFilterField(Map.of(indexName, List.of("important" + StringUtil.SLASH + requestDto.getFilter())));
		}

		searchFormDto.setPage(requestDto.getPage());
		searchFormDto.setSize(requestDto.getSize());

		log.info("[QuerySearch] searchFormDto : " + searchFormDto);
			
		var searchResult = searchServiceFactory.getSearchService("query").search(searchFormDto, requestDto);

		if(!requestDto.getQuery().isEmpty()) {
			// 검색 로그 저장
			try {
				String timestamp = java.time.LocalDateTime.now().toString();

				QueryLogRequestDto logDto = new QueryLogRequestDto();
				logDto.setId(timestamp);
				logDto.setQuery(requestDto.getQuery());
				logDto.setImportant(requestDto.getFilter());
				logDto.setTimestamp(timestamp);

				if(!requestDto.getStartDate().isEmpty()) {
					logDto.setStartDate(requestDto.getStartDate());
				}

				if(!requestDto.getEndDate().isEmpty()) {
					logDto.setEndDate(requestDto.getEndDate());
				}

				putDataService.putData(logDto, "query_log");

				log.info("[QuerySearch] query_log 저장 성공");
			} catch (Exception e) {
				log.warn("query_log 저장 실패", e);
				// 검색 응답은 그대로 반환
			}
		}

		return ResponseEntity.ok(searchResult);
    }

	@PostMapping("/auto")
	public ResponseEntity<?> autoSearch(@RequestBody SearchRequestDto requestDto) {
		SearchFormDto searchFormDto = new SearchFormDto();

		String indexName = "auto_search";

		searchFormDto.setQuery(requestDto.getQuery());
		searchFormDto.setIndex(indexName);
		searchFormDto.setIndexes(new String[]{indexName});

		searchFormDto.setSearchField(Map.of(indexName, AUTOPROPERTIES.getSearchField(indexName)));
		searchFormDto.setHighlightField(Map.of(indexName, AUTOPROPERTIES.getHighlightField(indexName)));
		
		searchFormDto.setPage(requestDto.getPage());
		searchFormDto.setSize(requestDto.getSize());

		log.info("[AutoSearch] searchFormDto : " + searchFormDto);
			
		return ResponseEntity.ok(searchServiceFactory.getSearchService("auto").search(searchFormDto, requestDto));
    }

	@PostMapping("/aggregation/{indexName}")
	    public ResponseEntity<?> aggregationSearch(@RequestBody SearchRequestDto requestDto, @PathVariable String indexName) {
			SearchFormDto searchFormDto = new SearchFormDto();

			searchFormDto.setQuery(SearchFormUtil.getSearchQuery(requestDto));
			searchFormDto.setIndex(indexName);
			searchFormDto.setIndexes(new String[]{indexName});

			if(requestDto.getSearchField().equals("all")) {
				searchFormDto.setSearchField(Map.of(indexName, PROPERTIES.getSearchField(indexName)));
			}else {
				searchFormDto.setSearchField(Map.of(indexName, List.of(requestDto.getSearchField().split(StringUtil.COMMA))));
			}

			if(!requestDto.getStartDate().equals("")) {
				searchFormDto.setDateField(Map.of(indexName, List.of("startDate" + StringUtil.SLASH + requestDto.getStartDate() + StringUtil.SLASH + "gte")));
			}

			if(!requestDto.getEndDate().equals("")) {
				searchFormDto.setDateField(Map.of(indexName, List.of("endDate" + StringUtil.SLASH + requestDto.getEndDate() + StringUtil.SLASH + "lte")));
			}

			if(!requestDto.getAggrField().equals("")) {
				searchFormDto.setAggrField(Map.of(indexName, List.of("aggrName" + StringUtil.SLASH + requestDto.getAggrField() + StringUtil.SLASH + requestDto.getSize() )));
			}

			if(requestDto.getFilter().equals("Y")) {
				searchFormDto.setFilterField(Map.of(indexName, List.of("important" + StringUtil.SLASH + requestDto.getFilter())));
			}
			
			searchFormDto.setPage(requestDto.getPage());
			searchFormDto.setSize(0);

			log.info("[AggregationSearch] searchFormDto : " + searchFormDto);
				
			return ResponseEntity.ok(searchServiceFactory.getSearchService("aggregation").search(searchFormDto, requestDto));
	    }

}
