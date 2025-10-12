package com.example.recipeservice.recipe.repository.recipe;

import com.example.recipeservice.recipe.dtos.PagingRecipeResponse;
import com.example.recipeservice.recipe.dtos.RecipeDto;
import com.example.recipeservice.recipe.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> , RecipeRepositoryQuery{
    RecipeDto getRecipe(Long recipeId);

    Page<PagingRecipeResponse> getMyRecipes(Pageable pageable, String username);
}
