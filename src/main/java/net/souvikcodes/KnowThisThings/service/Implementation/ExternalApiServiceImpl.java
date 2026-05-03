package net.souvikcodes.KnowThisThings.service.Implementation;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import net.souvikcodes.KnowThisThings.dto.JournalEntryDto;
import net.souvikcodes.KnowThisThings.dto.ExternalApiPojo.DadJoke;
import net.souvikcodes.KnowThisThings.dto.ExternalApiPojo.ElevenLabsRequest;
import net.souvikcodes.KnowThisThings.service.IExternalApiService;
import net.souvikcodes.KnowThisThings.service.IJournalEntryService;

@Service
@Slf4j
public class ExternalApiServiceImpl implements IExternalApiService {

    @Value("${api.ninja.key}")
    private String apiNinjaKey;

    @Value("${api.ninja.key.header}")
    private String apiNinjaKeyHeader;

    @Value("${api.ninja.dadjokes.url}")
    private String apiNinjaDadJokesUrl;

    @Value("${api.ElevenLabs.key}")
    private String elevenLabsKey;

    @Value("${api.ElevenLabs.modelId}")
    private String elevenLabsModelId;

    @Value("${api.ElevenLabs.key.header}")
    private String elevenLabsKeyHeader;

    @Value("${api.ElevenLabs.voiceId}")
    private String elevenLabsVoiceId;

    @Value("${api.ElevenLabs.outputFormatKey}")
    private String elevenLabsOutputFormatKey;

    @Value("${api.ElevenLabs.outputFormatValue}")
    private String elevenLabsOutputFormatValue;

    @Value("${api.ElevenLabs.textToSpeech.url}")
    private String elevenLabsTextToSpeechUrl;

    private RestTemplate restTemplate;
    private IJournalEntryService journalEntryService;

    public ExternalApiServiceImpl(RestTemplate restTemplate, IJournalEntryService journalEntryService) {
        this.restTemplate = restTemplate;
        this.journalEntryService = journalEntryService;
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
            throw new RuntimeException(errorMessage);
        } catch (RestClientException e) {
            String errorMessage = "Request failed : " + e.getMessage();
            log.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }

    @Override
    public byte[] convertTextToSpeech(String id, String username) {
        JournalEntryDto journalEntry = journalEntryService.getJournalEntryById(id, username);

        String text = journalEntry.getContent();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(elevenLabsKeyHeader, elevenLabsKey);
        headers.setAccept(Collections.singletonList(
                MediaType.APPLICATION_OCTET_STREAM));
        ElevenLabsRequest requestPayload = new ElevenLabsRequest(text, elevenLabsModelId);

        HttpEntity<ElevenLabsRequest> entity = new HttpEntity<>(requestPayload, headers);

        String url = elevenLabsTextToSpeechUrl + "/" + elevenLabsVoiceId;
        Map<String, String> params = new HashMap<>();
        params.put(elevenLabsOutputFormatKey, elevenLabsOutputFormatValue);

        try {
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, byte[].class, params);

            return response.getBody();
        } catch (HttpClientErrorException e) {
            String errorMessage = "API error : " + e.getStatusCode();
            log.error(errorMessage);
            throw new RuntimeException(errorMessage);
        } catch (RestClientException e) {
            String errorMessage = "Request failed : " + e.getMessage();
            log.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }
}
