package carear.document.be.os.search.service;

import org.springframework.stereotype.Service;

import carear.document.be.dto.ApiResponseDto;
import carear.document.be.os.search.dto.SearchFormDto;
import carear.document.be.os.search.dto.SearchRequestDto;



@Service
public abstract class SearchService {
    public abstract String getSearchType();
    public abstract ApiResponseDto search(SearchFormDto searchFormDto,SearchRequestDto requestDto);
    public abstract ApiResponseDto msearch(SearchFormDto searchFormDto,SearchRequestDto requestDto);
}
