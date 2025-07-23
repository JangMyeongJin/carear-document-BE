package carear.document.be.os.data.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import carear.document.be.dto.ApiResponseDto;
import carear.document.be.os.data.Data;

@Service
public class DeleteDataService {

    @Autowired
    private Data DATA;

    public ApiResponseDto deleteData(String id, String indexName) {

        String result = DATA.delete(id, indexName);
        
        if (!result.equals("")) {
            return ApiResponseDto.success(result);
        }else{
            return ApiResponseDto.fail("fail");
        }

    }
}
