package com.example.recipeservice.recipe.dtos;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IngredientCreateDto {

    private String ingredientName;
    private String amount;

}