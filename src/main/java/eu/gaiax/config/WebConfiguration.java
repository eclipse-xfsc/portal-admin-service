package eu.gaiax.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class WebConfiguration {
    @Bean(name = "demoSrv")
    public WebClient demoSrv(@Value("${services.identity.uri.internal}") final String extURI) {
        return WebClient.builder()
                .baseUrl(extURI)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean(name = "notarizationSrv")
    public WebClient notarizationSrv(@Value("${services.identity.uri.internal}") final String extURI) {
        return WebClient.builder()
                .baseUrl(extURI)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean(name = "ocmSrv")
    public WebClient ocmSrv(@Value("${services.identity.uri.internal}") final String extURI) {
        return WebClient.builder()
                .baseUrl(extURI)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean(name = "sdSrv")
    public WebClient sdSrv(@Value("${services.identity.uri.internal}") final String extURI) {
        return WebClient.builder()
                .baseUrl(extURI)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
