package me.contrapost.quizImpl.rest.converters;

import me.contrapost.quizAPI.dto.SubcategoryDTO;
import me.contrapost.quizImpl.entities.Subcategory;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SubcategoryConverter {

    public static SubcategoryDTO transform(Subcategory subCategory) {
        Objects.requireNonNull(subCategory);

        SubcategoryDTO dto = new SubcategoryDTO();
        dto.id = String.valueOf(subCategory.getId());
        dto.title = subCategory.getTitle();
        dto.rootCategoryId = String.valueOf(subCategory.getParentCategory().getId());

        return dto;
    }

    public static List<SubcategoryDTO> transform(List<Subcategory> entities){
        Objects.requireNonNull(entities);

        return entities.stream()
                .map(SubcategoryConverter::transform)
                .collect(Collectors.toList());
    }
}
