package me.contrapost.quizAPI.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel("A category")
public class CategoryDTO {

    @ApiModelProperty("The id of the category")
    public String id;

    @ApiModelProperty("The title of the category")
    public String title;

    @ApiModelProperty("Subcategories that belong to the category")
    public List<SubcategoryDTO> subcategories;

    public CategoryDTO() {
    }

    public
    CategoryDTO(String id, String title, List<SubcategoryDTO> subcategories) {
        this.id = id;
        this.title = title;
        this.subcategories = subcategories;
    }
}
