package com.flowermarket.repository;

import com.flowermarket.entity.WasteRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface WasteRecordRepository extends JpaRepository<WasteRecord, Long> {
    List<WasteRecord> findByRecordedAtBetween(LocalDateTime start, LocalDateTime end);
}
