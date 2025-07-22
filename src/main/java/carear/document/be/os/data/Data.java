package carear.document.be.os.data;

import java.io.IOException;
import java.util.Map;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.OpenSearchException;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.core.IndexResponse;
import org.springframework.beans.factory.annotation.Autowired;

public class Data {

    private OpenSearchClient openSearchClient;
	
	@Autowired
	public Data(OpenSearchClient openSearchClient) {
        this.openSearchClient = openSearchClient;
    }

    /**
     * 데이터 추가
     * @param indexName
     * @param documentMap
     * @return
     */
    public String put(String indexName, Map<String, Object> documentMap) {
        IndexRequest<Map<String, Object>> indexRequest = new IndexRequest.Builder<Map<String, Object>>()
                .index(indexName)
                .id(documentMap.get("id").toString())
                .document(documentMap)
                .build();

        try {
            IndexResponse response = openSearchClient.index(indexRequest);

            if (response.result().name().equals("Created") || response.result().name().equals("Updated")) {
                return "success";
            } else {
                return "fail";
            }
            
        } catch (OpenSearchException | IOException e) {
            e.printStackTrace();
            return "fail";
        }

    }

}
