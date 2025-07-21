package carear.document.be.os.search.builder;

import java.util.ArrayList;
import java.util.List;

import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.SortOptions;
import org.opensearch.client.opensearch._types.SortOrder;
import org.opensearch.client.opensearch._types.aggregations.Aggregation;
import org.opensearch.client.opensearch._types.aggregations.TermsAggregation;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.MatchAllQuery;
import org.opensearch.client.opensearch._types.query_dsl.MatchQuery;
import org.opensearch.client.opensearch._types.query_dsl.MultiMatchQuery;
import org.opensearch.client.opensearch._types.query_dsl.NestedQuery;
import org.opensearch.client.opensearch._types.query_dsl.Operator;
import org.opensearch.client.opensearch._types.query_dsl.PrefixQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch._types.query_dsl.RangeQuery;
import org.opensearch.client.opensearch._types.query_dsl.TermQuery;
import org.opensearch.client.opensearch._types.query_dsl.WildcardQuery;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.search.BuiltinHighlighterType;
import org.opensearch.client.opensearch.core.search.Highlight;
import org.opensearch.client.opensearch.core.search.HighlightField;
import org.opensearch.client.opensearch.core.search.HighlighterFragmenter;
import org.opensearch.client.opensearch.core.search.HighlighterType;

import carear.document.be.util.StringUtil;


public class Builder {
	/*
	 * 검색어 생성
	 * 
	 * @param boolQueryBuilder
	 * @param query
	 * @param searchField (boost는 ^ 표시)
	 */
	public static void getQueryBuilder(BoolQuery.Builder boolQueryBuilder, String query, List<String> searchField) {
		
		Operator op = Operator.And;

		List<String> searchFields = new ArrayList<>();
		List<String> searchNestedFields = new ArrayList<>();

		for(String field : searchField) {
			if(field.indexOf(StringUtil.SLASH) > -1) {
				searchNestedFields.add(field);
			}else {
				searchFields.add(field);
			}
		}
	
		if(searchFields.size() > 0) {
				MultiMatchQuery multiMatchQuery = new MultiMatchQuery.Builder()
					.fields(searchFields)
					.query(query)        // 검색어
					.operator(op)
					.build();

				Query queryBuilder = new Query.Builder()
					.multiMatch(multiMatchQuery)
					.build();

				boolQueryBuilder.should(queryBuilder).minimumShouldMatch("1");

		}

		if(searchNestedFields.size() > 0) {
			for(String field : searchNestedFields) {
				String[] fieldList = field.split(StringUtil.SLASH);
				String path = fieldList[0];
				String path2 = fieldList[1];
	
				if (query.contains("*")) {
					// WildcardQuery for nested field
					WildcardQuery wildcardQuery = new WildcardQuery.Builder()
						.field(path + "." + path2)
						.value(query)
						.build();

					Query queryBuilder = new Query.Builder()
						.wildcard(wildcardQuery)
						.build();

					NestedQuery nestedQuery = new NestedQuery.Builder()
						.path(path)
						.query(queryBuilder)
						.build();

					Query nestedQueryBuilder = new Query.Builder()
						.nested(nestedQuery)
						.build();

					boolQueryBuilder.should(nestedQueryBuilder);
				} else {
					NestedQuery nestedQuery = new NestedQuery.Builder()
						.path(path)
						.query(new Query.Builder()
							.match(new MatchQuery.Builder()
								.field(path + "." + path2)
								.query(FieldValue.of(query))
								.build())
							.build()
						)
						.build();
	
					Query queryBuilder = new Query.Builder()
						.nested(nestedQuery)
						.build();
	
					boolQueryBuilder.should(queryBuilder);
				}
			}
		}
	}

	/**
	 *  startWith 검색
	 * 
	 * @param boolQueryBuilder
	 * @param query
	 * @param searchField
	 */
	public static void getPrefixQueryBuilder(BoolQuery.Builder boolQueryBuilder, String query, List<String> searchField) {
		query = query.replace(StringUtil.WILDCARD, "");
		
		for(String field : searchField) {
			if(field.indexOf(StringUtil.SLASH) > -1) {
				field = field.replaceAll(StringUtil.SLASH, StringUtil.DOT);
			}

			PrefixQuery prefixQuery = new PrefixQuery.Builder()
				.field(field)
				.value(query)
				.build();

			Query queryBuilder = new Query.Builder()
				.prefix(prefixQuery)
				.build();

			boolQueryBuilder.should(queryBuilder).minimumShouldMatch("1");
		}
	}

	/*
	 * endWith 검색
	 * 
	 * @param boolQueryBuilder
	 * @param query
	 * @param searchField
	 */
	public static void getWildcardQueryBuilder(BoolQuery.Builder boolQueryBuilder, String query, List<String> searchField) {
		
		for(String field : searchField) {
			if(field.indexOf(StringUtil.SLASH) > -1) {
				field = field.replaceAll(StringUtil.SLASH, StringUtil.DOT);
			}

			WildcardQuery wildcardQuery = new WildcardQuery.Builder()
				.field(field)
				.value(query)
				.build();

			Query queryBuilder = new Query.Builder()
				.wildcard(wildcardQuery)
				.build();

			boolQueryBuilder.should(queryBuilder).minimumShouldMatch("1");
		}
	}

	/**
	 * 모든 필드 검색
	 * 
	 * @param boolQueryBuilder
	 */
	public static void getMatchAllQueryBuilder(BoolQuery.Builder boolQueryBuilder) {
		MatchAllQuery matchAllQuery = new MatchAllQuery.Builder()
			.build();

		Query queryBuilder = new Query.Builder()
			.matchAll(matchAllQuery)
			.build();

		boolQueryBuilder.should(queryBuilder).minimumShouldMatch("1");
	}	
	
	/**
	 * 정렬 생성
	 * 
	 * @param searchRequestBuilder
	 * @param fields
	 */
	public static void getSortBuilder(SearchRequest.Builder searchRequestBuilder, List<String> fields) {
		List<SortOptions> sortList = new ArrayList<>();

		for(String field : fields) {
			String sortField = field.split(StringUtil.SLASH)[0];
			String sortDirection = field.split(StringUtil.SLASH)[1];

			SortOrder sortOrder = "desc".equalsIgnoreCase(sortDirection) ? SortOrder.Desc : SortOrder.Asc;

			SortOptions sortOption;
			if("score".equals(sortField)) {
				sortOption = SortOptions.of(s -> s.score(sc -> sc.order(sortOrder)));
			} else {
				sortOption = SortOptions.of(s -> 
					s.field(f -> 
						f.field(sortField)
						.order(sortOrder)
					)
				);
			}

			sortList.add(sortOption);
		}

		if(!sortList.isEmpty()) {
			searchRequestBuilder.sort(sortList);
		}
	}

	/**
	 * 하이라이트 생성
	 * 
	 * @param searchRequestBuilder
	 * @param highlightFields
	 */
	public static void getHighlightBuilder(SearchRequest.Builder searchRequestBuilder, List<String> highlightFields) {
		Highlight.Builder highlightBuilder = new Highlight.Builder();
		for (String field : highlightFields) {
			if(field.indexOf(StringUtil.SLASH) > -1) {
				field = field.replaceAll(StringUtil.SLASH, StringUtil.DOT);
			}
			
			highlightBuilder.fields(field, new HighlightField.Builder()
				.numberOfFragments(0)
				.build()
			);
		}
		highlightBuilder.preTags("<span class='highLight'>");
		highlightBuilder.postTags("</span>");
		highlightBuilder.type(new HighlighterType.Builder().builtin(BuiltinHighlighterType.Unified).build());
		highlightBuilder.fragmenter(HighlighterFragmenter.Span); // "span" or "simple" or "scan"

		searchRequestBuilder.highlight(highlightBuilder.build());
	}

	/**
	 * must 필터 생성
	 * 
	 * @param boolQueryBuilder
	 * @param filterFields
	 */
	public static void getMustFilterBuilder(BoolQuery.Builder boolQueryBuilder, List<String> filterFields) {
		List<Query> queryList = new ArrayList<>();
		
		for(String field : filterFields) {
			String filterField = field.split(StringUtil.SLASH)[0];
			String filterValue = field.split(StringUtil.SLASH)[1];

			TermQuery termQuery = new TermQuery.Builder()
				.field(filterField)
				.value(FieldValue.of(filterValue))
				.build();

				Query query = new Query.Builder()
					.term(termQuery)
					.build();
				
				queryList.add(query);
		}
		
		boolQueryBuilder.must(queryList);
	}

	/**
	 * filter 필터 생성
	 * 
	 * @param boolQueryBuilder
	 * @param filterFields
	 */
	public static void getFilterBuilder(BoolQuery.Builder boolQueryBuilder, List<String> filterFields) {
		List<Query> queryList = new ArrayList<>();
		
		for(String field : filterFields) {
			
			String filterField = field.split(StringUtil.SLASH)[0];
			String filterValue = field.split(StringUtil.SLASH)[1];

			TermQuery termQuery = new TermQuery.Builder()
				.field(filterField)
				.value(FieldValue.of(filterValue))
				.build();

				Query query = new Query.Builder()
					.term(termQuery)
					.build();
				
				queryList.add(query);
		}
		
		boolQueryBuilder.filter(queryList);
	}

	/**
	 * should 필터 생성
	 * 
	 * @param boolQueryBuilder
	 * @param filterFields
	 */
	public static void getShouldFilterBuilder(BoolQuery.Builder boolQueryBuilder, List<String> filterFields) {
		List<Query> queryList = new ArrayList<>();
		
		for(String field : filterFields) {
				String[] fieldList = field.split(StringUtil.COMMA);
				BoolQuery.Builder boolQuery = new BoolQuery.Builder();

				for(String rField : fieldList) {
					String filterField = rField.split(StringUtil.SLASH)[0];
					String filterValue = rField.split(StringUtil.SLASH)[1];

					TermQuery termQuery = new TermQuery.Builder()
						.field(filterField)
						.value(FieldValue.of(filterValue))
						.build();

					Query query = new Query.Builder()
						.term(termQuery)
						.build();

					boolQuery.filter(query);
				}
				Query query = new Query.Builder().bool(boolQuery.build()).build();
				queryList.add(query);
			
		}
		boolQueryBuilder.should(queryList).minimumShouldMatch("1");
	}

	/**
	 * 포함 단어 검색
	 * 
	 * @param boolQueryBuilder
	 * @param includeWords
	 * @param searchField
	 */
	public static void getIncludeWordBuilder(BoolQuery.Builder includeBuilder, List<String> includeWords, List<String> searchField) {
		List<String> includeFields = new ArrayList<>();
		List<String> includeNestedFields = new ArrayList<>();

		for(String field : searchField) {
			if(field.indexOf(StringUtil.SLASH) > -1) {
				includeNestedFields.add(field);
			}else {
				includeFields.add(field);
			}
		}

		if(includeFields.size() > 0) {
			for(String word : includeWords) {
				// 모든 필드에 검색하기 위해 MultiMatchQuery 사용
				MultiMatchQuery multiMatchQuery = new MultiMatchQuery.Builder()
					.fields(includeFields) // 검색할 모든 필드
					.query(word)
					.operator(Operator.And)  // AND 검색 (선택사항)
					.build();

				Query query = new Query.Builder()
					.multiMatch(multiMatchQuery)
					.build();

				includeBuilder.should(query).minimumShouldMatch("1");
			}
		}

		// 검색 필드에 nested 필드가 있을떄
		if(includeNestedFields.size() > 0) {
			List<Query> queryList = new ArrayList<>();
		
			for(String field : includeNestedFields) {
				String[] fieldList = field.split(StringUtil.SLASH);
				String path = fieldList[0];
				String path2 = fieldList[1];

				for(String word : includeWords) {
					NestedQuery nestedQuery = new NestedQuery.Builder()
						.path(path)
						.query(new Query.Builder()
							.match(new MatchQuery.Builder()
								.field(path + "." + path2)
								.query(FieldValue.of(word))
								.build())
							.build()
						)
						.build();

					Query queryBuilder = new Query.Builder()
						.nested(nestedQuery)
						.build();

					// 이렇게 하면 should query가 각각 만들어짐. 합쳐야됨
					queryList.add(queryBuilder);
				}
			}

			if (!queryList.isEmpty()) {
				includeBuilder.should(queryList).minimumShouldMatch("1");
			}
		}
	}

	/**
	 * 제외 단어 검색
	 * 
	 * @param boolQueryBuilder
	 * @param excludeWords
	 * @param searchField
	 */
	public static void getExcludeWordBuilder(BoolQuery.Builder boolQueryBuilder, List<String> excludeWords, List<String> searchField) {
		List<String> excludeFields = new ArrayList<>();
		List<String> excludeNestedFields = new ArrayList<>();

		for(String field : searchField) {
			if(field.indexOf(StringUtil.SLASH) > -1) {
				excludeNestedFields.add(field);
			}else {
				excludeFields.add(field);
			}
		}

		if(excludeFields.size() > 0) {
			
			for(String word : excludeWords) {
				MultiMatchQuery multiMatchQuery = new MultiMatchQuery.Builder()
					.fields(excludeFields) // 검색할 모든 필드
					.query(word)
					.operator(Operator.And)  // AND 검색 (선택사항)
					.build();

				Query query = new Query.Builder()
					.multiMatch(multiMatchQuery)
					.build();
					
				boolQueryBuilder.mustNot(query);
			}
		}

		if(excludeNestedFields.size() > 0) {
			for(String field : excludeNestedFields) {
				String[] fieldList = field.split(StringUtil.SLASH);
				String path = fieldList[0];
				String path2 = fieldList[1];
	
				for(String word : excludeWords) {
					NestedQuery nestedQuery = new NestedQuery.Builder()
						.path(path)
						.query(new Query.Builder()
							.match(new MatchQuery.Builder()
								.field(path + "." + path2)
								.query(FieldValue.of(word))
								.build())
							.build()
						)
						.build();
	
					Query queryBuilder = new Query.Builder()
						.nested(nestedQuery)
						.build();
	
					boolQueryBuilder.mustNot(queryBuilder);
				}
			}
		}
	}

	/**
	 * 날짜 필터 생성
	 * 
	 * @param boolQueryBuilder
	 * @param dateFields
	 */
	public static void getDateBuilder(BoolQuery.Builder boolQueryBuilder, List<String> dateFields) {
		List<Query> queryList = new ArrayList<>();
		for(String field : dateFields) {
			RangeQuery.Builder rangeQuery = new RangeQuery.Builder();
			String dateField = field.split(StringUtil.SLASH)[0];
			String dateValue = field.split(StringUtil.SLASH)[1];
			String dateType = field.split(StringUtil.SLASH)[2];
	
			if(dateType.equals("lte")) {
				rangeQuery.field(dateField).lte(JsonData.of(dateValue));
			} else if(dateType.equals("gte")) {
				rangeQuery.field(dateField).gte(JsonData.of(dateValue));
			}
	
			Query query = new Query.Builder().range(rangeQuery.build()).build();
			queryList.add(query);
		}
		boolQueryBuilder.filter(queryList);
	}

	/**
	 * nested 필터 생성
	 * 
	 * @param boolQueryBuilder
	 * @param nestedFields
	 * @param query
	 */
	public static void getNestedBuilder(BoolQuery.Builder boolQueryBuilder, List<String> nestedFields, String query) {
		for(String field : nestedFields) {
			String[] fieldList = field.split(StringUtil.DOT);
			String path = fieldList[0];

			NestedQuery nestedQuery = new NestedQuery.Builder()
				.path(path)
				.query(new Query.Builder()
					.match(new MatchQuery.Builder()
						.field(field)
						.query(FieldValue.of(query))
						.build())
					.build()
				)
				.build();

			Query queryBuilder = new Query.Builder()
				.nested(nestedQuery)
				.build();

			boolQueryBuilder.should(queryBuilder);
		}
	}

	public static void getTermsAggregationBuilder(SearchRequest.Builder searchRequestBuilder, List<String> fields) {

		for(String field : fields) {

			String[] fieldList = field.split(StringUtil.SLASH);
			String aggrName = fieldList[0];
			String aggrField = fieldList[1];
			int size = Integer.parseInt(fieldList[2]);

			TermsAggregation termsAggregation = new TermsAggregation.Builder()
				.field(aggrField)
				.size(size)
				.build();

			searchRequestBuilder.aggregations(aggrName, new Aggregation.Builder()
				.terms(termsAggregation)
				.build()
			);
		}
	}
}
