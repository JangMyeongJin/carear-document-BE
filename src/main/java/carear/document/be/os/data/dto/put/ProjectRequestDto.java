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
public class ProjectRequestDto extends DataRequestDto {
	private String roll;
	private String stack;
	private String title;
	private String body;
	private String startDate;
	private String endDate;
}
