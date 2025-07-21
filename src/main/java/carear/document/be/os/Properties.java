package carear.document.be.os;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import carear.document.be.util.StringUtil;


public class Properties {
	private String[] INDEXES = {};

    /*
	 *  '^' boost 적용 필드
	 *  '.' field 구분
	 *  '/' nested 구분
	 */
	private String[] SEARCHFIELD = {};

    private String[] KEYWORDSEARCHFIELD = {};

    private String[] HIGHLIGHTFIELD = {};

    private String[] DEFAULTFIELD = {};

    private Map<String, List<String>> INDEX_SEARCHFIELD = new HashMap<>();
	private Map<String, List<String>> INDEX_HIGHLIGHTFIELD = new HashMap<>();
	private Map<String, List<String>> INDEX_KEYWORDSEARCHFIELD = new HashMap<>();
	private Map<String, List<String>> INDEX_DEFAULTFIELD = new HashMap<>();


    public Properties() {
		for(int i = 0; i < INDEXES.length; i++) {
			INDEX_SEARCHFIELD.put(INDEXES[i], Arrays.asList(SEARCHFIELD[i].split(StringUtil.COMMA)));
			INDEX_HIGHLIGHTFIELD.put(INDEXES[i], Arrays.asList(HIGHLIGHTFIELD[i].split(StringUtil.COMMA)));
			INDEX_KEYWORDSEARCHFIELD.put(INDEXES[i], Arrays.asList(KEYWORDSEARCHFIELD[i].split(StringUtil.COMMA)));
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

	public List<String> getDefaultField(String index) {
		return INDEX_DEFAULTFIELD.get(index);
	}

	public String[] getIndex() {
		return INDEXES;
	}

}
