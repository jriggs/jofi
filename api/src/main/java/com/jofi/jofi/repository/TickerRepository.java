package com.jofi.jofi.repository;

import com.jofi.jofi.model.Ticker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TickerRepository extends JpaRepository<Ticker, Long> {
    List<Ticker> findByAggressiveLevel(String aggressiveLevel);
    
    @Query("SELECT DISTINCT t.aggressiveLevel FROM Ticker t")
    List<String> findDistinctAggressiveLevel();
}
