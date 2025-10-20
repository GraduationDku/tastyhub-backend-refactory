package com.example.recipeservice.recipe.repository.recipe;

import com.example.recipeservice.recipe.dtos.PagingRecipeResponse;
import com.example.recipeservice.recipe.dtos.RecipeDto;
import com.example.recipeservice.recipe.entity.Recipe;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.repository.support.QuerydslRepositorySupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public class RecipeRepositoryQueryImpl extends QuerydslRepositorySupport implements RecipeRepositoryQuery {

    private JPAQueryFactory jpaQueryFactory;


    public RecipeRepositoryQueryImpl(JPAQueryFactory jpaQueryFactory) {
        super(Recipe.class, jpaQueryFactory);
    }

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



}
