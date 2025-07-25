package carear.document.be.os.data;

import java.io.IOException;
import java.util.Map;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.DeleteRequest;
import org.opensearch.client.opensearch.core.DeleteResponse;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.core.IndexResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Data {

    private OpenSearchClient OPENSEARCHCLIENT;
	
	@Autowired
	public Data(OpenSearchClient openSearchClient) {
        this.OPENSEARCHCLIENT = openSearchClient;
    }

    /**
     * 데이터 추가
     * @param documentMap
     * @param indexName
     * @return
     */
    public String put(Map<String, Object> documentMap, String indexName) {
        try {
            String id = documentMap.get("id").toString();

            IndexRequest<Map<String, Object>> indexRequest = new IndexRequest.Builder<Map<String, Object>>()
                .id(id)
                .index(indexName)
                .document(documentMap)
                .build();

            IndexResponse response = OPENSEARCHCLIENT.index(indexRequest);

            if (response.result().name().equals("Created") || response.result().name().equals("Updated")) {
                return id;
            } else {
                return "";
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    /**
     * 데이터 삭제
     * @param id
     * @param indexName
     * @return
     */
    public String delete(String id, String indexName) {
        try {
            DeleteRequest deleteRequest = new DeleteRequest.Builder()
                .id(id)
                .index(indexName)
                .build();

            DeleteResponse response = OPENSEARCHCLIENT.delete(deleteRequest);

            if (response.result().name().equals("Deleted")) {
                return id;
            } else {
                return "";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
