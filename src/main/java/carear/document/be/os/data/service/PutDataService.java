package carear.document.be.os.data.service;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import carear.document.be.dto.ApiResponseDto;
import carear.document.be.os.Properties;
import carear.document.be.os.data.Data;
import carear.document.be.os.data.dto.DataRequestDto;

@Service
public class PutDataService {

    private static Properties PROPERTIES = new Properties();
    private Data DATA;

    public ApiResponseDto putData(DataRequestDto requestDto, String indexName) {

        List<String> defaultFields = PROPERTIES.getDefaultField(indexName);

        Map<String, Object> documentMap = new HashMap<>();
        
        for (String fieldName : defaultFields) {
            Object fieldValue = getFieldValue(requestDto, fieldName);
            if (fieldValue != null) {
                documentMap.put(fieldName, fieldValue);
            }
        }

        String result = DATA.put(indexName, documentMap);
        
        return ApiResponseDto.success(result);
    }

    /**
     * 리플렉션을 사용하여 DTO에서 특정 필드값을 가져오기
     */
    private Object getFieldValue(DataRequestDto requestDto, String fieldName) {
        try {
            // 현재 클래스와 부모 클래스의 모든 필드를 확인
            Class<?> currentClass = requestDto.getClass();
            while (currentClass != null && currentClass != Object.class) {
                try {
                    Field field = currentClass.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    return field.get(requestDto);
                } catch (NoSuchFieldException e) {
                    // 현재 클래스에 필드가 없으면 부모 클래스 확인
                    currentClass = currentClass.getSuperclass();
                }
            }
        } catch (Exception e) {
            // 필드 접근 실패 시 null 반환
        }
        return null;
    }
}
