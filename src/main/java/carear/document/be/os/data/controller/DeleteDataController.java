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
import carear.document.be.os.data.service.DeleteDataService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/delete")
public class DeleteDataController {

    @Autowired
    private DeleteDataService deleteDataService;

	@PostMapping("/{indexName}")
    public ResponseEntity<?> deleteData(@RequestBody DataRequestDto requestDto, @PathVariable String indexName) {
        
        try {
            
            String id = requestDto.getId();

            ApiResponseDto responseDto = deleteDataService.deleteData(id, indexName);

            log.info("[deleteData] data success : " + responseDto);
            
            // 여기서 requestDto를 사용하여 비즈니스 로직 처리
            return ResponseEntity.ok(responseDto);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error processing request: " + e.getMessage());
        }
    }
}
