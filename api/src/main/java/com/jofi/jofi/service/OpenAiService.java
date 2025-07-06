package com.jofi.jofi.service;

import com.jofi.jofi.model.StockMetric;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

@Service
public class OpenAiService {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    @Value("${openai.api.key}")
    private String apiKey;

    public String analyzeMetrics(String aggressiveLevel, List<StockMetric> metrics) throws IOException, InterruptedException {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Consider the following stock metrics for someone whose investment preference is ")
              .append(aggressiveLevel)
              .append(" risk.\n")
              .append("Should any stock be removed? Keep the response under 200 words.\n\n");

        StockMetric allSymbols = metrics.stream()
                .filter(m -> "ALLSYMBOLS".equals(m.getSymbol()))
                .findFirst()
                .orElse(null);
        // If found, append its details
        if (allSymbols != null) {
            prompt.append("Aggregate Metrics:\n")
                  .append(String.format("- High: %.2f, Low: %.2f, First: %.2f, Last: %.2f, Return: %.2f%%\n",
                          allSymbols.getHigh(), allSymbols.getLow(), allSymbols.getFirst(), allSymbols.getLast(), allSymbols.getPercentReturn() * 100));
                metrics.remove(allSymbols);
                } 


        for (StockMetric m : metrics) {
            prompt.append(String.format("- %s: High=%.2f, Low=%.2f, First=%.2f, Last=%.2f, Return=%.2f%%\n",
                    m.getSymbol(), m.getHigh(), m.getLow(), m.getFirst(), m.getLast(), m.getPercentReturn() * 100));
        }

        // Escape the prompt for JSON
        String escapedPrompt = prompt.toString().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");

        String requestBody = "{"
                + "\"model\": \"gpt-3.5-turbo\","
                + "\"messages\": ["
                + "  {\"role\": \"system\", \"content\": \"You are a helpful financial assistant.\"},"
                + "  {\"role\": \"user\", \"content\": \"" + escapedPrompt + "\"}"
                + "]"
                + "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        // Parse the assistant's reply
        JSONObject json = new JSONObject(response.body());
        JSONArray choices = json.getJSONArray("choices");
        JSONObject message = choices.getJSONObject(0).getJSONObject("message");
        String content = message.getString("content").trim();

        return escapeHtml(content);
    }

    private String escapeHtml(String input) {
        return input.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("'", "&#39;");
    }
}
