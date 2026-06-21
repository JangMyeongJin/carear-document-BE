package carear.document.be.os;

import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.util.Timeout;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.OpenSearchTransport;
import org.opensearch.client.transport.httpclient5.ApacheHttpClient5TransportBuilder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Client {
	
	@Value("${opensearch.protocol}")
    private String protocol;

    @Value("${opensearch.host}")
    private String host;

    @Value("${opensearch.port}")
    private int port;

    @Value("${opensearch.username}")
    private String username;

    @Value("${opensearch.password}")
    private String password;
    
    @Bean
    public OpenSearchClient openSearchClient() {
        try {
            // OpenSearch 접속에 사용할 HttpHost
            final HttpHost httpHost = new HttpHost(protocol, host, port);
            
            // 인증 정보를 설정
            BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(
                new AuthScope(null, -1),
                new UsernamePasswordCredentials(username, password.toCharArray())
            );

            // OpenSearch와 통신하기 위한 OpenSearchTransport 객체를 생성
            ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setConnectTimeout(Timeout.ofMilliseconds(30000))
                .build();

            RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(30000))
                .setResponseTimeout(Timeout.ofMilliseconds(30000))
                .build();

            OpenSearchTransport transport = ApacheHttpClient5TransportBuilder.builder(httpHost)
                .setHttpClientConfigCallback(httpClientBuilder ->
                    httpClientBuilder
                        .setDefaultCredentialsProvider(credentialsProvider)
                        .setConnectionManager(
                            PoolingAsyncClientConnectionManagerBuilder.create()
                                .setDefaultConnectionConfig(connectionConfig)
                                .build())
                        .setDefaultRequestConfig(requestConfig))
                .build();

            // OpenSearchClient 생성 및 반환
            return new OpenSearchClient(transport);
        } catch (Exception e) {
            throw new RuntimeException("OpenSearch 클라이언트 생성 실패: " + e.getMessage(), e);
        }
    }


}
