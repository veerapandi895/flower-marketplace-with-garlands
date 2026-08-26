package com.flowermarket.repository;

import com.flowermarket.entity.PriceHistory;
import com.flowermarket.enums.PricedItemType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {
    List<PriceHistory> findByItemTypeAndItemIdOrderByRecordedAtDesc(PricedItemType itemType, Long itemId);
}
