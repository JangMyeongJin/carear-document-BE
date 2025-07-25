package carear.document.be.os;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import carear.document.be.util.StringUtil;


@Component
public class AutoProperties {
	private String[] INDEXES = {
		"auto_search"
	};

    /*
	 *  '^' boost 적용 필드
	 *  '.' field 구분
	 *  '/' nested 구분
	 */
	private String[] SEARCHFIELD = {
		"word,word.ngram,word.keyword,word.exact,initial"
	};

    private String[] HIGHLIGHTFIELD = {
		"word,word.ngram,word.keyword,word.exact,initial"
	};

    private String[] DEFAULTSEARCHFIELD = {
		"word,initial"
	};

	private String[] DEFAULTFIELD = {
		"id,word,initial",
	};

    private Map<String, List<String>> INDEX_SEARCHFIELD = new HashMap<>();
	private Map<String, List<String>> INDEX_HIGHLIGHTFIELD = new HashMap<>();
	private Map<String, List<String>> INDEX_DEFAULTSEARCHFIELD = new HashMap<>();
	private Map<String, List<String>> INDEX_DEFAULTFIELD = new HashMap<>();

    public AutoProperties() {
		for(int i = 0; i < INDEXES.length; i++) {
			INDEX_SEARCHFIELD.put(INDEXES[i], Arrays.asList(SEARCHFIELD[i].split(StringUtil.COMMA)));
			INDEX_HIGHLIGHTFIELD.put(INDEXES[i], Arrays.asList(HIGHLIGHTFIELD[i].split(StringUtil.COMMA)));
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
