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
public class OpenAIClient implements LLMClient {

    @Value("${ai.openai.apiKey:}")
    private String apiKey;

    @Value("${ai.model:gpt-4o-mini}")
    private String model;

    @Value("${ai.openai.baseUrl:https://api.openai.com}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String chatCompletion(List<String> messages, double temperature, int maxTokens) throws Exception {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("OPENAI_API_KEY is not configured");
        }

        String url = baseUrl + "/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("temperature", temperature);
        body.put("max_tokens", maxTokens);

        // Build messages array with roles
        var msgs = new java.util.ArrayList<Map<String, String>>();
        String systemPrompt = "You are DevCinema AI assistant. Answer in Vietnamese.";
        if (!messages.isEmpty() && messages.get(0).startsWith("##SYSTEM_PROMPT## ")) {
            systemPrompt = messages.get(0).replaceFirst("##SYSTEM_PROMPT## ", "");
            messages = messages.subList(1, messages.size());
        }
        msgs.add(Map.of("role", "system", "content", systemPrompt));
        for (int i = 0; i < messages.size(); i++) {
            String role = (i % 2 == 0) ? "user" : "assistant";
            msgs.add(Map.of("role", role, "content", messages.get(i)));
        }
        body.put("messages", msgs);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("OpenAI error: " + response.getStatusCode());
        }

        Object content = ((Map)((Map)((List) response.getBody().get("choices")).get(0)).get("message")).get("content");
        return content == null ? "" : content.toString();
    }
}


