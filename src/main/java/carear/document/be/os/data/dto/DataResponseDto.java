package carear.document.be.os.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataResponseDto {
	private String id;
	private String indexName;
	private String type;
}
