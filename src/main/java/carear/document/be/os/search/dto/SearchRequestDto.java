package carear.document.be.os.search.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class SearchRequestDto {
    private String query = "";
    private String searchCondition = "partialMatch";
    private String searchField = "all";
    private boolean isReSearch = false;
    private String reSearchQuery = "";
    private String startDate = "";
    private String endDate = "";
    private String aggrField = "";
    private int page = 1;
    private int size = 10;
}
