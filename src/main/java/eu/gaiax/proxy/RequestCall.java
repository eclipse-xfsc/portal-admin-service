package eu.gaiax.proxy;

import eu.gaiax.repo.dto.JsonbSdData;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Slf4j
public class RequestCall {

    public static <T> ResponseEntity<T> doGet(final WebClient srv, final HttpServletRequest request) {
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        request.getParameterMap().forEach((s, strings) -> queryParams.addAll(s, List.of(strings)));

        final WebClient.RequestHeadersSpec<?> callBuilder = srv
                .get()
                .uri(builder ->
                        builder.path(request.getRequestURI())
                                .queryParams(queryParams).build());

        final Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            final String hn = headerNames.nextElement();
            callBuilder.header(hn, request.getHeader(hn));
        }

        return callBuilder.retrieve()
                .toEntity(new ParameterizedTypeReference<T>() {
                    //
                }).block();
    }

    public static <T, R> ResponseEntity<T> doPost(WebClient srv, HttpServletRequest request, R rqBody, String uri) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        request.getParameterMap().forEach((s, strings) -> queryParams.addAll(s, List.of(strings)));

        WebClient.RequestBodySpec prep = srv
                .post()
                .uri(builder ->
                        builder.path(uri)
                                .queryParams(queryParams).build());

        WebClient.RequestHeadersSpec<?> callBuilder = prep;
        if (rqBody != null) {
            callBuilder = prep.bodyValue(rqBody);
        }

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String hn = headerNames.nextElement();
            String header = hn.equalsIgnoreCase("content-type")
                    ? "application/json"
                    : request.getHeader(hn);
            callBuilder.header(hn, header);
        }
        return callBuilder
                .retrieve()
                .toEntity(new ParameterizedTypeReference<T>() {
                })
                .block();
    }

    public static JsonbSdData validateSdData(WebClient webClient, HttpServletRequest request, byte[] arr, String url) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        request.getParameterMap().forEach((s, strings) -> queryParams.addAll(s, List.of(strings)));

        return webClient
                .post()
                .uri((builder -> {
                    URI build = builder.path(url)
                            .queryParams(queryParams).build();
                    return build;
                }
                ))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .bodyValue(arr)
                .retrieve()
                .bodyToMono(JsonbSdData.class)
                .block();
    }

    public static <T> ResponseEntity<T> processAttach(WebClient webClient, HttpServletRequest request, byte[] arr, String url, String filename) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        request.getParameterMap().forEach((s, strings) -> queryParams.addAll(s, List.of(strings)));

        return webClient
                .post()
                .uri((builder -> {
                    URI build = builder.path(url)
                            .queryParams(queryParams).build();
                    return build;
                }
                ))
                .header("input-filename", filename)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .bodyValue(arr)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<T>() {
                })
                .block();
    }
}
