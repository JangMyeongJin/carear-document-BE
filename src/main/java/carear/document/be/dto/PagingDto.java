package carear.document.be.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PagingDto {

    /* 페이지번호 */
    private Integer page;
    
    /* 페이지 사이즈 */
    private Integer pageSize;
    
    /* 전체 페이지수 */
    private Integer totalPage;
    
    /* 전체 데이터 카운트 */
    private Integer totalCount;
    
    public PagingDto(Integer page, Integer pageSize) {
        this.page = page;
        this.pageSize = pageSize;
    }
        
    public void setPaging(Integer totalCount) {
        this.totalCount = (totalCount == null) ? 0 : totalCount;
        this.pageSize = (this.pageSize == null) ? 10 : this.pageSize;
        
        Integer tempPage = totalCount / this.pageSize;

        if (totalCount % pageSize != 0) tempPage++;
        this.totalPage = tempPage;
    }

}
