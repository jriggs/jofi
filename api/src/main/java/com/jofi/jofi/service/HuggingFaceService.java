package com.jofi.jofi.service;
import com.jofi.jofi.model.StockMetric;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.URI;
import java.lang.InterruptedException;

@Service
public class HuggingFaceService {

private static final String API_URL = "https://api-inference.huggingface.co/models/gpt2";
@Value("${huggingface.api.key}")
    private String apiKey;

    public String analyzeMetrics(String aggressiveLevel, List<StockMetric> metrics) throws IOException, InterruptedException {
        StringBuilder prompt = new StringBuilder("As a financial analyst, consider the following stock metrics for someone who's investment strategy is " + aggressiveLevel + "  :. Should a stock be removed? Maximum 200 words\n");
        for (StockMetric m : metrics) {
            prompt.append(String.format("- %s: High=%.2f, Low=%.2f, First=%.2f, Last=%.2f, Return=%.2f%%\n",
                    m.getSymbol(), m.getHigh(), m.getLow(), m.getFirst(), m.getLast(), m.getPercentReturn() * 100));
        }

        String json = String.format("{\"inputs\": \"%s\"}", prompt.toString().replace("\"", "\\\""));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }
}
