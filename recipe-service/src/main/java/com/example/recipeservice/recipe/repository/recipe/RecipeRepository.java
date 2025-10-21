package com.example.recipeservice.recipe.repository.recipe;

import com.example.recipeservice.recipe.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> , RecipeRepositoryQuery{

    Optional<Recipe> findById(Long id);
}
