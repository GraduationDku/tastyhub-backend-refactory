package com.example.recipeservice.recipe.dtos;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.example.recipeservice.recipe.dtos.QPagingRecipeResponse is a Querydsl Projection type for PagingRecipeResponse
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QPagingRecipeResponse extends ConstructorExpression<PagingRecipeResponse> {

    private static final long serialVersionUID = 30055757L;

    public QPagingRecipeResponse(com.querydsl.core.types.Expression<Long> foodId, com.querydsl.core.types.Expression<com.example.recipeservice.recipe.entity.Recipe.RecipeType> recipeType, com.querydsl.core.types.Expression<String> foodName, com.querydsl.core.types.Expression<String> foodImgUrl, com.querydsl.core.types.Expression<Long> foodInfoId, com.querydsl.core.types.Expression<String> foodInfoText, com.querydsl.core.types.Expression<Long> foodInfoCookingTime, com.querydsl.core.types.Expression<String> foodInfoServing, com.querydsl.core.types.Expression<Long> likeCount) {
        super(PagingRecipeResponse.class, new Class<?>[]{long.class, com.example.recipeservice.recipe.entity.Recipe.RecipeType.class, String.class, String.class, long.class, String.class, long.class, String.class, long.class}, foodId, recipeType, foodName, foodImgUrl, foodInfoId, foodInfoText, foodInfoCookingTime, foodInfoServing, likeCount);
    }

}

