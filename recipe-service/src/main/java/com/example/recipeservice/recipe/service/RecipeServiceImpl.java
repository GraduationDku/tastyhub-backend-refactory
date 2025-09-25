package com.example.recipeservice.recipe.service;

import com.example.recipeservice.recipe.dtos.PagingRecipeResponse;
import com.example.recipeservice.recipe.dtos.RecipeDto;
import com.example.recipeservice.recipe.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;

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

    @Override
    public RecipeDto getRecipe(Long recipeId) {
        return null;
    }
}
