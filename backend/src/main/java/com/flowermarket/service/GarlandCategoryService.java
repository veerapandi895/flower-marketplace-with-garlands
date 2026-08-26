package com.flowermarket.service;

import com.flowermarket.dto.GarlandCategoryRequest;
import com.flowermarket.entity.GarlandCategory;
import com.flowermarket.exception.BadRequestException;
import com.flowermarket.exception.ResourceNotFoundException;
import com.flowermarket.repository.GarlandCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GarlandCategoryService {

    private final GarlandCategoryRepository garlandCategoryRepository;

    public GarlandCategory create(GarlandCategoryRequest req) {
        if (garlandCategoryRepository.existsByNameIgnoreCase(req.getName())) {
            throw new BadRequestException("A garland category with this name already exists");
        }
        GarlandCategory category = GarlandCategory.builder()
                .name(req.getName())
                .description(req.getDescription())
                .imageUrl(req.getImageUrl())
                .build();
        return garlandCategoryRepository.save(category);
    }

    public GarlandCategory update(Long id, GarlandCategoryRequest req) {
        GarlandCategory category = getById(id);
        category.setName(req.getName());
        category.setDescription(req.getDescription());
        category.setImageUrl(req.getImageUrl());
        return garlandCategoryRepository.save(category);
    }

    public void delete(Long id) {
        GarlandCategory category = getById(id);
        garlandCategoryRepository.delete(category);
    }

    public GarlandCategory getById(Long id) {
        return garlandCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Garland category not found"));
    }

    public List<GarlandCategory> getAll() {
        return garlandCategoryRepository.findAll();
    }
}
