package org.example.cinema_reservation_system.service.ai;

import java.util.List;

public interface LLMClient {
    String chatCompletion(List<String> messages, double temperature, int maxTokens) throws Exception;
}












