package com.example.recipeservice.recipe.repository.recipe;

import com.example.recipeservice.recipe.dtos.PagingRecipeResponse;
import com.example.recipeservice.recipe.dtos.RecipeDto;
import com.example.recipeservice.recipe.entity.QFoodInformation;
import com.example.recipeservice.recipe.entity.Recipe;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.github.classgraph.AnnotationInfoList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.repository.support.QuerydslRepositorySupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.function.Function;

import static com.example.recipeservice.recipe.entity.QFoodInformation.foodInformation;
import static com.example.recipeservice.recipe.entity.QLike.like;
import static com.example.recipeservice.recipe.entity.QRecipe.recipe;


@Slf4j
public class RecipeRepositoryQueryImpl extends QuerydslRepositorySupport implements RecipeRepositoryQuery {

    private JPAQueryFactory jpaQueryFactory;


    public RecipeRepositoryQueryImpl(JPAQueryFactory jpaQueryFactory) {
        super(Recipe.class, jpaQueryFactory);
    }

    @Override
    public Page<PagingRecipeResponse> getPopularRecipes(Pageable pageable) {
        Function<JPAQueryFactory, JPAQuery<PagingRecipeResponse>> contentQuery = factory -> factory.select(Projections.constructor(
                        PagingRecipeResponse.class,
                        recipe.id,
                        recipe.recipeType,
                        recipe.foodName,
                        recipe.recipeImgUrl,
                        foodInformation.id,
                        foodInformation.content,
                        foodInformation.cookingTime,
                        foodInformation.serving,
                        like.id.count()
                )).from(recipe)
                .leftJoin(recipe.foodInformation, foodInformation)
                .leftJoin(recipe.likes, like)
                .groupBy(recipe.id,
                        recipe.recipeType,
                        recipe.foodName,
                        recipe.recipeImgUrl,
                        foodInformation.id,
                        foodInformation.content,
                        foodInformation.cookingTime,
                        foodInformation.serving)
                .orderBy(like.id.count().desc());

        Function<JPAQueryFactory, JPAQuery<Long>> countQuery = factory -> createCountQuery(recipe);

        return applyPagination(pageable, contentQuery, countQuery);
    }

    @Override
    public Page<PagingRecipeResponse> getAllRecipes(Pageable pageable) {

        Function<JPAQueryFactory, JPAQuery<PagingRecipeResponse>> contentQuery = factory -> factory.select(Projections.constructor(PagingRecipeResponse.class,
                        recipe.id,
                        recipe.recipeType,
                        recipe.foodName,
                        recipe.recipeImgUrl,
                        foodInformation.id,
                        foodInformation.content,
                        foodInformation.cookingTime,
                        foodInformation.serving,
                        like.id.count()
                )).from(recipe)
                .leftJoin(recipe.foodInformation, foodInformation)
                .leftJoin(recipe.likes, like)
                .groupBy(recipe.id,
                        recipe.recipeType,
                        recipe.foodName,
                        recipe.recipeImgUrl,
                        foodInformation.id,
                        foodInformation.content,
                        foodInformation.cookingTime,
                        foodInformation.serving)
                .orderBy(recipe.id.asc());

        Function<JPAQueryFactory, JPAQuery<Long>> countQuery = factory -> createCountQuery(recipe);

        return applyPagination(pageable, contentQuery, countQuery);
    }

    @Override
    public Page<PagingRecipeResponse> getSearchedRecipes(String keyword, Pageable pageable) {
        Function<JPAQueryFactory, JPAQuery<PagingRecipeResponse>> contentQuery = facctory -> facctory.select(Projections.constructor(PagingRecipeResponse.class,
                        recipe.id,
                        recipe.recipeType,
                        recipe.foodName,
                        recipe.recipeImgUrl,
                        foodInformation.id,
                        foodInformation.content,
                        foodInformation.cookingTime,
                        foodInformation.serving))
                .from(recipe)
                .where(recipe.foodName.contains(keyword))
                .leftJoin(recipe.foodInformation, foodInformation);
        Function<JPAQueryFactory, JPAQuery<Long>> countQuery = facctory -> createCountQuery(recipe);
        return applyPagination(pageable, contentQuery, countQuery);
    }

    @Override
    public Page<PagingRecipeResponse> getMyRecipes(Pageable pageable, String username) {

        Function<JPAQueryFactory, JPAQuery<PagingRecipeResponse>> contentQuery = factory -> factory.select(Projections.constructor(PagingRecipeResponse.class,
                        recipe.id, recipe.recipeType, recipe.foodName,
                        recipe.recipeImgUrl,
                        foodInformation.id,
                        foodInformation.content, foodInformation.cookingTime, foodInformation.serving))
                .from(recipe)
                .where(recipe.username.eq(username))
                .leftJoin(recipe.foodInformation, foodInformation);

        Function<JPAQueryFactory, JPAQuery<Long>> countQuery = factory -> createCountQuery(recipe);
        return applyPagination(pageable, contentQuery, countQuery);
    }


}
