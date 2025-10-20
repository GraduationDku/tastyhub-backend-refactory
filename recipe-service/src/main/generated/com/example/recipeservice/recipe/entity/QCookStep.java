package com.example.recipeservice.recipe.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCookStep is a Querydsl query type for CookStep
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCookStep extends EntityPathBase<CookStep> {

    private static final long serialVersionUID = -1702934185L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCookStep cookStep = new QCookStep("cookStep");

    public final org.example.timeStamp.QTimeStamp _super = new org.example.timeStamp.QTimeStamp(this);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final QRecipe recipe;

    public final StringPath stepImgUrl = createString("stepImgUrl");

    public final NumberPath<Long> stepNumber = createNumber("stepNumber", Long.class);

    public final StringPath timeline = createString("timeline");

    public QCookStep(String variable) {
        this(CookStep.class, forVariable(variable), INITS);
    }

    public QCookStep(Path<? extends CookStep> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCookStep(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCookStep(PathMetadata metadata, PathInits inits) {
        this(CookStep.class, metadata, inits);
    }

    public QCookStep(Class<? extends CookStep> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.recipe = inits.isInitialized("recipe") ? new QRecipe(forProperty("recipe"), inits.get("recipe")) : null;
    }

}

