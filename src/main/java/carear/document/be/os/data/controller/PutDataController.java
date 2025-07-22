package carear.document.be.os.data.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import carear.document.be.dto.ApiResponseDto;
import carear.document.be.os.data.dto.DataRequestDto;
import carear.document.be.os.data.dto.put.PutRequestDtoFactory;
import carear.document.be.os.data.service.PutDataService;

@RestController
@RequestMapping("/put")
public class PutDataController {

    @Autowired
    private PutDataService putDataService;

	@PostMapping("/{indexName}")
    public ResponseEntity<?> putData(@RequestBody Map<String, Object> requestMap, @PathVariable String indexName) {
        
        try {
            // 팩토리를 사용하여 type에 따라 적절한 DTO 생성
            DataRequestDto requestDto = PutRequestDtoFactory.createDto(indexName, requestMap);

            ApiResponseDto responseDto = putDataService.putData(requestDto, indexName);
            
            // 여기서 requestDto를 사용하여 비즈니스 로직 처리
            return ResponseEntity.ok(responseDto);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error processing request: " + e.getMessage());
        }
    }
}
