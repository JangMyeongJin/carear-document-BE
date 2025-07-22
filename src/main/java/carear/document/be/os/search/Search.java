package carear.document.be.os.search;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.MsearchRequest;
import org.opensearch.client.opensearch.core.MsearchResponse;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.msearch.MultisearchBody;
import org.opensearch.client.opensearch.core.msearch.RequestItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import carear.document.be.os.Properties;
import carear.document.be.os.search.builder.Builder;
import carear.document.be.os.search.dto.SearchFormDto;
import carear.document.be.util.StringUtil;
import jakarta.json.stream.JsonGenerator;


@Component
public class Search {
	private static Properties PROPERTIES = new Properties();
	
	private OpenSearchClient openSearchClient;
	
	@Autowired
	public Search(OpenSearchClient openSearchClient) {
        this.openSearchClient = openSearchClient;
    }
	
	public MsearchResponse<Map> msearch(SearchFormDto searchFormDto) {
		String[] indexes = searchFormDto.getIndexes();
		MsearchRequest.Builder msearchBuilder = new MsearchRequest.Builder();
			
		if(indexes.length > 0) {
			try {
				for(String index : indexes) {
					SearchRequest searchRequest = searchRequest(searchFormDto, index);

					MultisearchBody multisearchBody = new MultisearchBody.Builder()
						.query(searchRequest.query())
						.from(searchRequest.from())
						.size(searchRequest.size())
						.sort(searchRequest.sort())
						.highlight(searchRequest.highlight())
						// 필요한 필드가 있으면 추가
						.build();

					RequestItem item = new RequestItem.Builder()
						.header(h -> h.index(index))
						.body(multisearchBody)
						.build();
					msearchBuilder.searches(item);
				}

				MsearchResponse<Map> msearchResponse = openSearchClient.msearch(msearchBuilder.build(),Map.class);

				return msearchResponse;
					
			}catch (IOException e) {
				e.printStackTrace();
				return null;
			}catch (Exception e) {
				e.printStackTrace();
				return null;
			}
				
		}
		return null;
			
	}
	 
	 public SearchRequest searchRequest(SearchFormDto searchFormDTO, String indexName) {

			// 검색 조건
			String QUERY = searchFormDTO.getQuery();
			Map<String,List<String>> SEARCHFIELD = searchFormDTO.getSearchField();
			Map<String,List<String>> HIGHLIGHTFIELD = searchFormDTO.getHighlightField();
			Map<String,List<String>> SHOULDFILTERFIELD = searchFormDTO.getShouldFilterField();
			Map<String,List<String>> FILTERFIELD = searchFormDTO.getFilterField();
			Map<String,List<String>> SORTFIELD = searchFormDTO.getSortField();
			Map<String,List<String>> NESTEDFIELD = searchFormDTO.getNestedField();
			Map<String,List<String>> DATEFIELD = searchFormDTO.getDateField();
			Map<String,List<String>> INCLUDEWORD = searchFormDTO.getIncludeWord();
			Map<String,List<String>> EXCLUDEWORD = searchFormDTO.getExcludeWord();
			int SIZE = searchFormDTO.getSize();
			int page = searchFormDTO.getPage() == 0 ? 1 : searchFormDTO.getPage();
			int FROM = (page - 1) * SIZE;

	        SearchRequest.Builder searchRequestBuilder = new SearchRequest.Builder();

			BoolQuery.Builder shouldBuilder = new BoolQuery.Builder();
			BoolQuery.Builder includeBuilder = new BoolQuery.Builder();
			BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();

			// Properties searchfield 가져오기
			List<String> propertiesSearchField = PROPERTIES.getSearchField(indexName);

			// 검색어
			if(QUERY.equals("")) {
				Builder.getMatchAllQueryBuilder(boolQueryBuilder);
			}else {
				List<String> searchField = SEARCHFIELD.get(indexName);
				
				if(QUERY.indexOf(StringUtil.WILDCARD) == QUERY.length() - 1) {
					Builder.getPrefixQueryBuilder(boolQueryBuilder, QUERY, searchField);
				} else if(QUERY.indexOf(StringUtil.WILDCARD) > -1) {
					Builder.getWildcardQueryBuilder(boolQueryBuilder, QUERY, searchField);
				} else {
					Builder.getQueryBuilder(boolQueryBuilder, QUERY, searchField);
				}
			}

			// nested
			if(NESTEDFIELD.containsKey(indexName)) {
			List<String> nestedField = NESTEDFIELD.get(indexName);
				if(nestedField.size() > 0 && !QUERY.equals("")) {
					Builder.getNestedBuilder(boolQueryBuilder, nestedField, QUERY);
				}
			}
			
			// 포함 단어
			if(INCLUDEWORD.containsKey(indexName)) {
				List<String> includeWord = INCLUDEWORD.get(indexName);
				if(includeWord.size() > 0) {
					Builder.getIncludeWordBuilder(includeBuilder, includeWord, propertiesSearchField);
				}
			}

			// 제외 단어
			if(EXCLUDEWORD.containsKey(indexName)) {
				List<String> excludeWord = EXCLUDEWORD.get(indexName);
				if(excludeWord.size() > 0) {
					Builder.getExcludeWordBuilder(boolQueryBuilder, excludeWord, propertiesSearchField);
				}
			}

			// 날짜
			if(DATEFIELD.containsKey(indexName)) {
				List<String> dateField = DATEFIELD.get(indexName);
				if(dateField.size() > 0) {
					Builder.getDateBuilder(boolQueryBuilder, dateField);
				}
			}

			// 필터
			if(FILTERFIELD.containsKey(indexName)) {
				List<String> filterField = FILTERFIELD.get(indexName);
				if(filterField.size() > 0) {
					Builder.getMustFilterBuilder(boolQueryBuilder, filterField);
				}
			}

			// should 조건 (category)
			if(SHOULDFILTERFIELD.containsKey(indexName)) {
				List<String> shouldFilterField = SHOULDFILTERFIELD.get(indexName);
				if(shouldFilterField.size() > 0) {
					Builder.getShouldFilterBuilder(shouldBuilder, shouldFilterField);
				}
			}

			BoolQuery includeQuery = includeBuilder.build();
			if (includeQuery.should() != null && !includeQuery.should().isEmpty()) {
				boolQueryBuilder.must(
					new Query.Builder().bool(includeQuery).build()
				);
			}
			
			BoolQuery shouldQuery = shouldBuilder.build();
			if (shouldQuery.should() != null && !shouldQuery.should().isEmpty()) {
				boolQueryBuilder.must(
					new Query.Builder().bool(shouldQuery).build()
				);
			}
	 
			searchRequestBuilder.query(
				new Query.Builder().bool(boolQueryBuilder.build()).build()
			);
			
			if(SORTFIELD.containsKey(indexName)) {
				List<String> orderFields = SORTFIELD.get(indexName);
				if(orderFields.size() > 0) {
					Builder.getSortBuilder(searchRequestBuilder, orderFields);
				}
			}

			if(HIGHLIGHTFIELD.containsKey(indexName)) {
				List<String> highlightField = HIGHLIGHTFIELD.get(indexName);
				if(highlightField.size() > 0) {
					Builder.getHighlightBuilder(searchRequestBuilder, highlightField);
				}
			}
			
			searchRequestBuilder.from(FROM);
			searchRequestBuilder.size(SIZE);

			searchRequestBuilder.index(indexName);
			
			// SearchRequest 생성
			SearchRequest searchRequest = searchRequestBuilder.build();

			System.out.println("GET " + searchRequest.index().get(0) + "/_search " +  searchRequestWriter(searchRequest));
			
	        return searchRequest;
	    }

	    /*
	     *  SearchRequest를 String으로 변환
	     */
	    private StringWriter searchRequestWriter(SearchRequest searchRequest) {
			JacksonJsonpMapper mapper = new JacksonJsonpMapper();
			StringWriter writer = new StringWriter();
			JsonGenerator generator = mapper.jsonProvider().createGenerator(writer);
			searchRequest.serialize(generator, mapper);
			generator.close();
			
			return writer;
		}

}
