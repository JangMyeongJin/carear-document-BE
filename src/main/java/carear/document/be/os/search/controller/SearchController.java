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
	private Properties PROPERTIES;

	@Autowired
	private AutoProperties AUTOPROPERTIES;

    @PostMapping("/query/{indexName}")
    public ResponseEntity<?> querySearch(@RequestBody SearchRequestDto requestDto, @PathVariable String indexName) {
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

		searchFormDto.setPage(requestDto.getPage());
		searchFormDto.setSize(requestDto.getSize());

		log.info("[QuerySearch] searchFormDto : " + searchFormDto);
			
		return ResponseEntity.ok(searchServiceFactory.getSearchService("query").search(searchFormDto, requestDto));
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
				searchFormDto.setAggrField(Map.of(indexName, List.of("aggrName" + StringUtil.SLASH + requestDto.getAggrField() + StringUtil.SLASH + "10")));
			}
			
			searchFormDto.setPage(requestDto.getPage());
			searchFormDto.setSize(requestDto.getSize());

			log.info("[AggregationSearch] searchFormDto : " + searchFormDto);
				
			return ResponseEntity.ok(searchServiceFactory.getSearchService("aggregation").search(searchFormDto, requestDto));
	    }

}
