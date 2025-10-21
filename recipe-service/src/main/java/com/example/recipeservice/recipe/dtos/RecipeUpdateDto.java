package com.example.recipeservice.recipe.dtos;


import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RecipeUpdateDto {

    private String foodName;

    private String foodVideoUrl;

    private FoodInformationDto foodInformation;

    private List<IngredientDto> ingredients;

    private List<CookStepDto> cookSteps;

}