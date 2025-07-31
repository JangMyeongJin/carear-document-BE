package carear.document.be.os;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import carear.document.be.util.StringUtil;


@Component
public class Properties {
	private String[] INDEXES = {
		"project"
	};

    /*
	 *  '^' boost 적용 필드
	 *  '.' field 구분
	 *  '/' nested 구분
	 */
	private String[] SEARCHFIELD = {
		"title,title.ngram,title.exact,body,body.ngram,body.exact,stack"
	};

    private String[] KEYWORDSEARCHFIELD = {
		"title,body"
	};

    private String[] HIGHLIGHTFIELD = {
		"title,title.ngram,title.exact,body,body.ngram,body.exact,stack"
	};

    private String[] DEFAULTSEARCHFIELD = {
		"title,body"
	};

	private String[] DEFAULTFIELD = {
		"title,body,startDate,endDate,role,features,stack",
	};

    private Map<String, List<String>> INDEX_SEARCHFIELD = new HashMap<>();
	private Map<String, List<String>> INDEX_HIGHLIGHTFIELD = new HashMap<>();
	private Map<String, List<String>> INDEX_KEYWORDSEARCHFIELD = new HashMap<>();
	private Map<String, List<String>> INDEX_DEFAULTSEARCHFIELD = new HashMap<>();
	private Map<String, List<String>> INDEX_DEFAULTFIELD = new HashMap<>();

    public Properties() {
		for(int i = 0; i < INDEXES.length; i++) {
			INDEX_SEARCHFIELD.put(INDEXES[i], Arrays.asList(SEARCHFIELD[i].split(StringUtil.COMMA)));
			INDEX_HIGHLIGHTFIELD.put(INDEXES[i], Arrays.asList(HIGHLIGHTFIELD[i].split(StringUtil.COMMA)));
			INDEX_KEYWORDSEARCHFIELD.put(INDEXES[i], Arrays.asList(KEYWORDSEARCHFIELD[i].split(StringUtil.COMMA)));
			INDEX_DEFAULTSEARCHFIELD.put(INDEXES[i], Arrays.asList(DEFAULTSEARCHFIELD[i].split(StringUtil.COMMA)));
			INDEX_DEFAULTFIELD.put(INDEXES[i], Arrays.asList(DEFAULTFIELD[i].split(StringUtil.COMMA)));
		}
	}

	public List<String> getSearchField(String index) {
		return INDEX_SEARCHFIELD.get(index);
	}

	public List<String> getHighlightField(String index) {
		return INDEX_HIGHLIGHTFIELD.get(index);
	}

	public List<String> getKeywordSearchField(String index) {
		return INDEX_KEYWORDSEARCHFIELD.get(index);
	}

	public List<String> getDefaultSearchField(String index) {
		return INDEX_DEFAULTSEARCHFIELD.get(index);
	}

	public List<String> getDefaultField(String index) {
		return INDEX_DEFAULTFIELD.get(index);
	}

	public String[] getIndex() {
		return INDEXES;
	}

}
