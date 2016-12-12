package me.contrapost.quizAPI.dto;

import me.contrapost.quizImpl.entities.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CategoryConverter {

    private CategoryConverter() {}

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

}
