package carear.document.be.os.search.dto;

import java.util.List;
import java.util.Map;

import carear.document.be.dto.PagingDto;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper=false)
public class SearchResponseDto {
    /* 검색 정보 */
    private List<Map<String,Object>> result;

    /* Request 정보 */
    private SearchRequestDto request;

    /* 페이지 정보 */
    private PagingDto page; 

}
