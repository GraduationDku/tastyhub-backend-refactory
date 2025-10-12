package com.example.recipeservice.recipe.dtos;

import com.example.recipeservice.recipe.entity.Ingredient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class IngredientDto {

    private Long ingredientId;
    private String ingredientName;
    private String amount;

    public IngredientDto(Ingredient ingredient) {
        this.ingredientId = ingredient.getId();
        this.ingredientName = ingredient.getIngredientName();
        this.amount = ingredient.getAmount();
    }
    public Ingredient toEntity() {
        return new Ingredient(this);
    }
}