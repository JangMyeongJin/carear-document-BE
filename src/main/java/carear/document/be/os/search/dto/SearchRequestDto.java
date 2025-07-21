package carear.document.be.os.search.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class SearchRequestDto {
    private String query;
    private String index;
    private String searchCondition;
    private boolean isReSearch;
    private String reSearchQuery;
}
