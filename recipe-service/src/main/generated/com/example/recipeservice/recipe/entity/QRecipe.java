package com.example.recipeservice.recipe.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRecipe is a Querydsl query type for Recipe
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRecipe extends EntityPathBase<Recipe> {

    private static final long serialVersionUID = -918239887L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRecipe recipe = new QRecipe("recipe");

    public final ListPath<CookStep, QCookStep> cookSteps = this.<CookStep, QCookStep>createList("cookSteps", CookStep.class, QCookStep.class, PathInits.DIRECT2);

    public final QFoodInformation foodInformation;

    public final StringPath foodName = createString("foodName");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<Ingredient, QIngredient> ingredients = this.<Ingredient, QIngredient>createList("ingredients", Ingredient.class, QIngredient.class, PathInits.DIRECT2);

    public final ListPath<Like, QLike> likes = this.<Like, QLike>createList("likes", Like.class, QLike.class, PathInits.DIRECT2);

    public final StringPath recipeImgUrl = createString("recipeImgUrl");

    public final EnumPath<Recipe.RecipeType> recipeType = createEnum("recipeType", Recipe.RecipeType.class);

    public final StringPath recipeVideoUrl = createString("recipeVideoUrl");

    public final StringPath username = createString("username");

    public QRecipe(String variable) {
        this(Recipe.class, forVariable(variable), INITS);
    }

    public QRecipe(Path<? extends Recipe> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRecipe(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRecipe(PathMetadata metadata, PathInits inits) {
        this(Recipe.class, metadata, inits);
    }

    public QRecipe(Class<? extends Recipe> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.foodInformation = inits.isInitialized("foodInformation") ? new QFoodInformation(forProperty("foodInformation"), inits.get("foodInformation")) : null;
    }

}

