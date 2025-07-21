package carear.document.be.os.search.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import carear.document.be.os.search.dto.SearchFormDto;
import carear.document.be.os.search.dto.SearchRequestDto;
import carear.document.be.os.search.service.SearchServiceFactory;
import carear.document.be.os.search.util.SearchFormUtil;
import carear.document.be.util.StringUtil;



@RestController
@RequestMapping("/search")
public class QuerySearchController {
	
	 	@Autowired
		private SearchServiceFactory searchServiceFactory;

	    @GetMapping("/query")
	    public ResponseEntity<?> search(@RequestBody SearchRequestDto requestDto) {
	        SearchFormDto searchFormDto = new SearchFormDto();

	        searchFormDto.setQuery(SearchFormUtil.getSearchQuery(requestDto));
	        searchFormDto.setIndexes(requestDto.getIndex().split(StringUtil.COMMA));
	        
	        return ResponseEntity.ok(searchServiceFactory.getSearchService("query").msearch(searchFormDto, requestDto));
	    }

}
