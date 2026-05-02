package net.souvikcodes.KnowThisThings.service.Implementation;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import net.souvikcodes.KnowThisThings.dto.ExternalApiPojo.DadJoke;
import net.souvikcodes.KnowThisThings.service.IExternalApiService;

@Service
@Slf4j
public class ExternalApiServiceImpl implements IExternalApiService {

    @Value("${api.ninja.key}")
    private String apiNinjaKey;
    @Value("${api.ninja.key.header}")
    private String apiNinjaKeyHeader;
    @Value("${api.ninja.dadjokes.url}")
    private String apiNinjaDadJokesUrl;

    private RestTemplate restTemplate;

    public ExternalApiServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String getDadJokeOfTheDay() {

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set(apiNinjaKeyHeader, apiNinjaKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<List<DadJoke>> response = restTemplate.exchange(
                    apiNinjaDadJokesUrl, HttpMethod.GET, entity, new ParameterizedTypeReference<List<DadJoke>>() {
                    });

            return (response != null && !response.getBody().isEmpty()) ? response.getBody().get(0).getJoke() : null;
        } catch (HttpClientErrorException e) {
            String errorMessage = "API error : " + e.getStatusCode();
            log.error(errorMessage);
            throw new RuntimeException("API error : " + e.getStatusCode());
        } catch (RestClientException e) {
            String errorMessage = "Request failed : " + e.getMessage();
            log.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }
}
