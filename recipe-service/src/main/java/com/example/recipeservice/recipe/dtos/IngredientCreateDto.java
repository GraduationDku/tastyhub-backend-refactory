package com.example.recipeservice.recipe.dtos;

import com.example.recipeservice.recipe.entity.Ingredient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredientCreateDto {

    private String ingredientName;
    private String amount;

    public Ingredient toEntity(){
        return new Ingredient(this.ingredientName, this.amount);
    }


}