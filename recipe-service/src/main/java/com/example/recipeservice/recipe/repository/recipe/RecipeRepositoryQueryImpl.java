package com.example.recipeservice.recipe.repository.recipe;

import com.example.recipeservice.recipe.dtos.PagingRecipeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class RecipeRepositoryQueryImpl implements RecipeRepositoryQuery{
    @Override
    public Page<PagingRecipeResponse> getPopularRecipes(Pageable pageable) {
        return null;
    }

    @Override
    public Page<PagingRecipeResponse> getAllRecipes(Pageable pageable) {
        return null;
    }

    @Override
    public Page<PagingRecipeResponse> getSearchedRecipes(String keyword, Pageable pageable) {
        return null;
    }
}
