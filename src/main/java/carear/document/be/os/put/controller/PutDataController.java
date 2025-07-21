package carear.document.be.os.put.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import carear.document.be.os.put.ProjectRequestDto;
import carear.document.be.os.search.dto.SearchFormDto;
import carear.document.be.os.search.dto.SearchRequestDto;
import carear.document.be.os.search.util.SearchFormUtil;
import carear.document.be.util.StringUtil;

@RestController
@RequestMapping("/put")
public class PutDataController {

	@GetMapping("/{type}")
    public ResponseEntity<?> putProject(@RequestBody ProjectRequestDto requestDto) {
        SearchFormDto searchFormDto = new SearchFormDto();

        searchFormDto.setQuery(SearchFormUtil.getSearchQuery(requestDto));
        searchFormDto.setIndexes(requestDto.getIndex().split(StringUtil.COMMA));
        
        return ResponseEntity.ok(searchServiceFactory.getSearchService("query").msearch(searchFormDto, requestDto));
    }
}
