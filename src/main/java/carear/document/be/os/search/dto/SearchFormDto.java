package carear.document.be.os.search.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchFormDto {
	private String query = ""; 
	private String index;
	private String[] indexes;
	private Map<String,List<String>> searchField = new HashMap<>();
	private Map<String,List<String>> highlightField = new HashMap<>();
	private Map<String,List<String>> sortField = new HashMap<>();
	private Map<String,List<String>> shouldFilterField = new HashMap<>();
	private Map<String,List<String>> filterField = new HashMap<>();
	private Map<String,List<String>> nestedField = new HashMap<>();
	private Map<String,List<String>> dateField = new HashMap<>();			// dateField / date / operator
	private Map<String,List<String>> includeWord = new HashMap<>();
	private Map<String,List<String>> excludeWord = new HashMap<>();
	private Map<String,List<String>> aggrField = new HashMap<>();			// aggrName / aggrField / size
	private int page;
	private int size;
}
