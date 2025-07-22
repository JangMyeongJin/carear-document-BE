package carear.document.be.os.data.dto.put;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import carear.document.be.os.data.dto.DataRequestDto;

public class PutRequestDtoFactory {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    public static DataRequestDto createDto(String indexName, Map<String, Object> data) {
        try {
            if(indexName.equals("project")) {
                return objectMapper.convertValue(data, ProjectRequestDto.class);
            }else {
                return objectMapper.convertValue(data, DataRequestDto.class);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid DTO indexName: " + indexName, e);
        }
    }
} 