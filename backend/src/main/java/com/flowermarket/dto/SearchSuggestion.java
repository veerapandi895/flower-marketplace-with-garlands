package com.flowermarket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchSuggestion {
    private String type;   // FLOWER, GARLAND, CATEGORY, GARLAND_CATEGORY, SHOP
    private Long id;
    private String name;
    private String link;
}
