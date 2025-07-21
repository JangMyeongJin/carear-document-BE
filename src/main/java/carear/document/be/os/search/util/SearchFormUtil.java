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
	
		switch (searchCondition) {
			case "startWith":
				query = query + "*";
				break;
			case "endWith":
				query = "*" + query;
				break;
			case "partialMatch":
			default:
				break;
		}
		return query;
	}

}
