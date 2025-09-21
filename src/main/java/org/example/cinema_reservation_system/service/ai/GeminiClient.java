package org.example.cinema_reservation_system.service.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GeminiClient implements LLMClient {

    @Value("${ai.gemini.apiKey:}")
    private String apiKey;

    @Value("${ai.model:gemini-1.5-flash}")
    private String model;

    @Value("${ai.gemini.baseUrl:https://generativelanguage.googleapis.com}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String chatCompletion(List<String> messages, double temperature, int maxTokens) throws Exception {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("GEMINI_API_KEY is not configured");
        }

        String url = baseUrl + "/v1beta/models/" + model + ":generateContent?key=" + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        Map<String, Object> genConfig = new HashMap<>();
        genConfig.put("temperature", temperature);
        genConfig.put("maxOutputTokens", maxTokens);
        body.put("generationConfig", genConfig);

        var parts = new java.util.ArrayList<Map<String, String>>();
        String systemPrompt = "You are DevCinema AI assistant. Answer in Vietnamese.";
        if (!messages.isEmpty() && messages.get(0).startsWith("##SYSTEM_PROMPT## ")) {
            systemPrompt = messages.get(0).replaceFirst("##SYSTEM_PROMPT## ", "");
            messages = messages.subList(1, messages.size());
        }
        parts.add(Map.of("text", systemPrompt));
        for (String m : messages) {
            parts.add(Map.of("text", m));
        }

        var contents = java.util.List.of(Map.of("parts", parts));
        body.put("contents", contents);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Gemini error: " + response.getStatusCode());
        }

        Map bodyMap = response.getBody();
        List candidates = (List) bodyMap.get("candidates");
        if (candidates == null || candidates.isEmpty()) return "";
        Map cand0 = (Map) candidates.get(0);
        Map content = (Map) cand0.get("content");
        if (content == null) return "";
        List partsList = (List) content.get("parts");
        if (partsList == null || partsList.isEmpty()) return "";
        Map part0 = (Map) partsList.get(0);
        Object text = part0.get("text");
        return text == null ? "" : text.toString();
    }
}


