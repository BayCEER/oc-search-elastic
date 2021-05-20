package de.unibayreuth.bayceer.oc;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticConfig {
	
    @Value("${ELASTIC_HOST:localhost}")
    private String Host;
    
    @Value("${ELASTIC_PORT:9200}")
    private int Port;
    
       
    @Bean 
    RestHighLevelClient client() {     
    	// https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high.html
    	return new RestHighLevelClient(RestClient.builder(new HttpHost(Host, Port, "http")));    	
    }
                   
}
