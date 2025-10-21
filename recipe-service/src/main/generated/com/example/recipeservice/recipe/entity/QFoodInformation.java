package com.example.recipeservice.recipe.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFoodInformation is a Querydsl query type for FoodInformation
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFoodInformation extends EntityPathBase<FoodInformation> {

    private static final long serialVersionUID = -1283913365L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFoodInformation foodInformation = new QFoodInformation("foodInformation");

    public final org.example.timeStamp.QTimeStamp _super = new org.example.timeStamp.QTimeStamp(this);

    public final StringPath content = createString("content");

    public final NumberPath<Long> cookingTime = createNumber("cookingTime", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final QRecipe recipe;

    public final StringPath serving = createString("serving");

    public QFoodInformation(String variable) {
        this(FoodInformation.class, forVariable(variable), INITS);
    }

    public QFoodInformation(Path<? extends FoodInformation> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFoodInformation(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFoodInformation(PathMetadata metadata, PathInits inits) {
        this(FoodInformation.class, metadata, inits);
    }

    public QFoodInformation(Class<? extends FoodInformation> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.recipe = inits.isInitialized("recipe") ? new QRecipe(forProperty("recipe"), inits.get("recipe")) : null;
    }

}

