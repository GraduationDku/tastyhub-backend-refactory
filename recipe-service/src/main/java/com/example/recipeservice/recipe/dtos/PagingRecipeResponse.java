package com.example.recipeservice.recipe.dtos;

import com.example.recipeservice.recipe.entity.Recipe;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder

@NoArgsConstructor
@AllArgsConstructor
public class PagingRecipeResponse {

    private Long foodId;
    private Recipe.RecipeType recipeType;
    private String foodName;
    private String foodImgUrl;
    private Long likeCount;

    private FoodInformationDto foodInformationDto;

    @QueryProjection
    public PagingRecipeResponse(Long foodId, Recipe.RecipeType recipeType, String foodName,
                                String foodImgUrl, Long foodInfoId, String foodInfoText,
                                Long foodInfoCookingTime, String foodInfoServing,Long likeCount) {
        this.foodId = foodId;
        this.recipeType = recipeType;
        this.foodName = foodName;
        this.foodImgUrl = foodImgUrl;
        this.likeCount = likeCount;
        this.foodInformationDto = FoodInformationDto.builder().foodInformationId(foodInfoId)
                .content(foodInfoText)
                .cookingTime(foodInfoCookingTime).serving(foodInfoServing).build();
    }
}