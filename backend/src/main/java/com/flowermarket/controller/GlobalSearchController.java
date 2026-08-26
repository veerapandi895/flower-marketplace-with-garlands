package com.flowermarket.controller;

import com.flowermarket.dto.SearchSuggestion;
import com.flowermarket.entity.Flower;
import com.flowermarket.entity.Garland;
import com.flowermarket.entity.GarlandCategory;
import com.flowermarket.entity.Shop;
import com.flowermarket.service.CategoryService;
import com.flowermarket.service.FlowerService;
import com.flowermarket.service.GarlandCategoryService;
import com.flowermarket.service.GarlandService;
import com.flowermarket.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Phase 7: unified search - flowers, garlands, categories, sellers - plus lightweight autocomplete. */
@RestController
@RequestMapping("/api/public/search")
@RequiredArgsConstructor
public class GlobalSearchController {

    private final FlowerService flowerService;
    private final GarlandService garlandService;
    private final CategoryService categoryService;
    private final GarlandCategoryService garlandCategoryService;
    private final ShopService shopService;

    /** Fast, name-only suggestions for a search-as-you-type dropdown. */
    @GetMapping("/autocomplete")
    public ResponseEntity<List<SearchSuggestion>> autocomplete(@RequestParam String query,
                                                                @RequestParam(defaultValue = "8") int limit) {
        String needle = query.toLowerCase(Locale.ROOT).trim();
        List<SearchSuggestion> suggestions = new ArrayList<>();

        if (needle.isEmpty()) return ResponseEntity.ok(suggestions);

        for (Flower f : flowerService.search(query)) {
            suggestions.add(new SearchSuggestion("FLOWER", f.getId(), f.getName(), "/flowers/" + f.getId()));
        }
        for (Garland g : garlandService.search(query)) {
            suggestions.add(new SearchSuggestion("GARLAND", g.getId(), g.getName(), "/garlands/" + g.getId()));
        }
        for (var c : categoryService.getAll()) {
            if (c.getName().toLowerCase(Locale.ROOT).contains(needle)) {
                suggestions.add(new SearchSuggestion("CATEGORY", c.getId(), c.getName(), "/browse?categoryId=" + c.getId()));
            }
        }
        for (GarlandCategory c : garlandCategoryService.getAll()) {
            if (c.getName().toLowerCase(Locale.ROOT).contains(needle)) {
                suggestions.add(new SearchSuggestion("GARLAND_CATEGORY", c.getId(), c.getName(), "/garlands?categoryId=" + c.getId()));
            }
        }
        for (Shop shop : shopService.getAllShops()) {
            if (shop.getShopName() != null && shop.getShopName().toLowerCase(Locale.ROOT).contains(needle)) {
                suggestions.add(new SearchSuggestion("SHOP", shop.getSeller().getId(), shop.getShopName(), "/shops/" + shop.getSeller().getId()));
            }
        }

        return ResponseEntity.ok(suggestions.stream().limit(limit).toList());
    }

    /** Full search results page - flowers + garlands matching the query. */
    @GetMapping
    public ResponseEntity<SearchResults> search(@RequestParam String query) {
        var flowers = flowerService.search(query).stream().map(flowerService::toResponse).toList();
        var garlands = garlandService.search(query).stream().map(garlandService::toResponse).toList();
        return ResponseEntity.ok(new SearchResults(flowers, garlands));
    }

    public record SearchResults(List<?> flowers, List<?> garlands) {}
}
