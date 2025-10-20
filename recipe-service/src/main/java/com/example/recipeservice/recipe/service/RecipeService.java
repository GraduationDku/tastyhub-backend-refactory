package com.example.recipeservice.recipe.service;

import com.example.recipeservice.recipe.dtos.PagingRecipeResponse;
import com.example.recipeservice.recipe.dtos.RecipeCreateDto;
import com.example.recipeservice.recipe.dtos.RecipeDto;
import com.example.recipeservice.recipe.dtos.RecipeUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface RecipeService {
    Page<PagingRecipeResponse> getPopularRecipes(Pageable pageable);

    Page<PagingRecipeResponse> getAllRecipes(Pageable pageable);

    Page<PagingRecipeResponse> getSearchedRecipes(String keyword, Pageable pageable);

    RecipeDto getRecipe(Long recipeId);

    Page<PagingRecipeResponse> getMyRecipes(Pageable pageable, String username);

    void createRecipe(RecipeCreateDto recipeCreateDto, MultipartFile recipeImg, List<MultipartFile> cookStepImgs, String username);

    void updateRecipe(Long recipeId, MultipartFile img, String username, RecipeUpdateDto recipeUpdateDto) ;

    void deleteRecipe(Long recipeId, String username) ;
}
