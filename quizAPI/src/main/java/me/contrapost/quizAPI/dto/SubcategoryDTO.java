package me.contrapost.quizAPI.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("A subcategory")
public class SubcategoryDTO {

    @ApiModelProperty("The id of the subcategory")
    public String id;

    @ApiModelProperty("The title of the subcategory")
    public String title;

    @ApiModelProperty("The root category the subcategory belongs to")
    public String rootCategoryId;

    public SubcategoryDTO(){}

    public SubcategoryDTO(String id, String title, String parentCategoryId) {
        this.id = id;
        this.title = title;
        this.rootCategoryId = parentCategoryId;
    }

}
