package carear.document.be.os.search.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;


@Component
public class SearchServiceFactory {
    private final Map<String, SearchService> searchServiceMap = new HashMap<String, SearchService>();
	
	public SearchServiceFactory(List<SearchService> searchServices) {
		searchServices.forEach(s -> searchServiceMap.put(s.getSearchType(), s));
	}
	
	public SearchService getSearchService(String searchType) {
		return searchServiceMap.get(searchType);
	}
}
