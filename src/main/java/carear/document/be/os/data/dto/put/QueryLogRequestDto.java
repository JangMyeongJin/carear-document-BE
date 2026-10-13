package carear.document.be.os.data.dto.put;

import carear.document.be.os.data.dto.DataRequestDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class QueryLogRequestDto extends DataRequestDto {
	private String id;
	private String timestamp;
	private String query;
	private String stack;
	private String startDate;
	private String endDate;
	private String important;
}
