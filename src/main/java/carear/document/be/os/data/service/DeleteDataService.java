package carear.document.be.os.data.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import carear.document.be.dto.ApiResponseDto;
import carear.document.be.os.data.Data;
import carear.document.be.os.data.dto.DataResponseDto;

@Service
public class DeleteDataService {

    @Autowired
    private Data DATA;

    public ApiResponseDto deleteData(String id, String indexName) {

        String resultId = DATA.delete(id, indexName);

        DataResponseDto dataResponseDto = new DataResponseDto();
        dataResponseDto.setId(resultId);
        dataResponseDto.setIndexName(indexName);
        dataResponseDto.setType("delete");
        
        if (!resultId.equals("")) {
            return ApiResponseDto.success(dataResponseDto);
        }else{
            return ApiResponseDto.fail("fail");
        }

    }
}
