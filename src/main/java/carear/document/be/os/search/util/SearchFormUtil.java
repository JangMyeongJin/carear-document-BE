package carear.document.be.os.search.util;

import carear.document.be.os.search.dto.SearchRequestDto;

public class SearchFormUtil {
	
	public static String getSearchQuery(SearchRequestDto requestDto) {
		
		String query = requestDto.getQuery();

		if(requestDto.isReSearch()) {
			query += " " + requestDto.getReSearchQuery();
			return query;
		}
	
		String searchCondition = requestDto.getSearchCondition();

		if(query.equals("")) {
			searchCondition = "partialMatch";
		}

		if(searchCondition.equals("startWith")) {
			query = query + "*";
		}else if(searchCondition.equals("endWith")) {
			query = "*" + query;
		}

		return query;
	}

}
