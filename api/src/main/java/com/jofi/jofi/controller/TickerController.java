package com.jofi.jofi.controller;

import com.jofi.jofi.model.Ticker;
import com.jofi.jofi.repository.TickerRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tickers")
@CrossOrigin(origins = "*")
public class TickerController {

    private final TickerRepository repository;

    public TickerController(TickerRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Ticker> getAll() {
        return repository.findAll();
    }

    @GetMapping("/aggressive/levels/list")
    public List<String> getDistinctAggressiveLevels() {
        return repository.findDistinctAggressiveLevel();
    }

    @PostMapping
    public Ticker create(@RequestBody Ticker ticker) {
        ticker.setCreatedAt(LocalDateTime.now());
        ticker.setModified(LocalDateTime.now());
        return repository.save(ticker);
    }

    @PutMapping("/{id}")
    public Ticker update(@PathVariable Long id, @RequestBody Ticker updated) {
        return repository.findById(id).map(existing -> {
            existing.setSymbol(updated.getSymbol());
            existing.setName(updated.getName());
            existing.setDescription(updated.getDescription());
            existing.setAggressiveLevel(updated.getAggressiveLevel());
            existing.setModified(LocalDateTime.now());
            return repository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Ticker not found"));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
