package com.example.recipeservice.recipe.repository.recipe;

import com.example.recipeservice.recipe.dtos.PagingRecipeResponse;
import com.example.recipeservice.recipe.dtos.RecipeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecipeRepositoryQuery {
    Page<PagingRecipeResponse> getPopularRecipes(Pageable pageable);

    Page<PagingRecipeResponse> getAllRecipes(Pageable pageable);

    Page<PagingRecipeResponse> getSearchedRecipes(String keyword, Pageable pageable);

    RecipeDto getRecipe(Long recipeId);

    Page<PagingRecipeResponse> getMyRecipes(Pageable pageable, String username);
}
