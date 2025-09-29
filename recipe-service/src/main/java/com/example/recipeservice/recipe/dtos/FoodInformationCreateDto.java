package com.example.recipeservice.recipe.dtos;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FoodInformationCreateDto {

    private String content;

    private Long cookingTime;

    private String serving;

}