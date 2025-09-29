package com.example.recipeservice.recipe.service;

import com.example.recipeservice.recipe.dtos.PagingRecipeResponse;
import com.example.recipeservice.recipe.dtos.RecipeCreateDto;
import com.example.recipeservice.recipe.dtos.RecipeDto;
import com.example.recipeservice.recipe.dtos.RecipeUpdateDto;
import com.example.recipeservice.recipe.repository.recipe.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    @Override
    public Page<PagingRecipeResponse> getMyRecipes(Pageable pageable, String username) {
        return null;
    }

    @Override
    public void createRecipe(RecipeCreateDto recipeCreateDto, MultipartFile recipeImg, List<MultipartFile> cookStepImgs, String username) {

    }

    @Override
    public void updateRecipe(Long recipeId, MultipartFile img, String username, RecipeUpdateDto recipeUpdateDto) {

    }

    @Override
    public void deleteRecipe(Long recipeId, String username) {

    }
}
