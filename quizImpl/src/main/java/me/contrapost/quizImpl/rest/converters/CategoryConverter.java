package me.contrapost.quizImpl.rest.converters;

import me.contrapost.quizAPI.dto.CategoryDTO;
import me.contrapost.quizImpl.entities.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CategoryConverter {

    private CategoryConverter() {}

    public static CategoryDTO transform(Category rootCategory) {
        Objects.requireNonNull(rootCategory);

        CategoryDTO dto = new CategoryDTO();
        dto.id = String.valueOf(rootCategory.getId());
        dto.title = rootCategory.getTitle();

        return dto;
    }

    public static CategoryDTO transform(Category category, boolean expand) {
        Objects.requireNonNull(category);

        CategoryDTO dto = new CategoryDTO();
        dto.id = String.valueOf(category.getId());
        dto.title = category.getTitle();

        if(expand) {
            dto.subcategories = new ArrayList<>();
            category.getSubcategories().values().stream()
                    .map(SubcategoryConverter::transform)
                    .forEach(subcategoryDTO -> dto.subcategories.add(subcategoryDTO));
        }

        return dto;
    }

    public static List<CategoryDTO> transform(List<Category> entities, Boolean expand){
        Objects.requireNonNull(entities);

        return entities.stream()
                .map(category -> transform(category, expand))
                .collect(Collectors.toList());
    }
}
