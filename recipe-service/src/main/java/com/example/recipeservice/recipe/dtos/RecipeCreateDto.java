package com.example.recipeservice.recipe.dtos;

import com.example.recipeservice.recipe.entity.Recipe;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeCreateDto {

    private Recipe.RecipeType recipeType;

    private String foodName;

    private String foodVideoUrl;

    private FoodInformationCreateDto foodInformation;

    private List<IngredientCreateDto> ingredients;

    private List<CookStepCreateRequest> cookSteps;

}
