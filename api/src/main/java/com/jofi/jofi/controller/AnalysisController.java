package com.jofi.jofi.controller;
import com.jofi.jofi.service.OpenAiService;
import com.jofi.jofi.model.StockMetric;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.io.IOException;

@RestController
@RequestMapping("/api/analysis")
@CrossOrigin(origins = "*")
public class AnalysisController {

    private final OpenAiService openAiService;

    public AnalysisController(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    @PostMapping("/run")
    public ResponseEntity<String> analyze(@RequestParam String aggressiveLevel, @RequestBody List<StockMetric> metrics) throws IOException, InterruptedException {
        String result = openAiService.analyzeMetrics(aggressiveLevel, metrics);
        return ResponseEntity.ok(result);
    }
}
