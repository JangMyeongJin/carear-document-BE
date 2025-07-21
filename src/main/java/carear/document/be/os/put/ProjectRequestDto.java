package carear.document.be.os.put;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectRequestDto {
	private String id;
	private String roll;
	private String stack;
	private String title;
	private String body;
	private String startDate;
	private String endDate;
}
