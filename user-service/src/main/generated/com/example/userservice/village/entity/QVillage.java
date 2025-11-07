package com.example.userservice.village.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QVillage is a Querydsl query type for Village
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QVillage extends BeanPath<Village> {

    private static final long serialVersionUID = -756082056L;

    public static final QVillage village = new QVillage("village");

    public final StringPath addressTownName = createString("addressTownName");

    public final NumberPath<Double> lat = createNumber("lat", Double.class);

    public final NumberPath<Double> lng = createNumber("lng", Double.class);

    public QVillage(String variable) {
        super(Village.class, forVariable(variable));
    }

    public QVillage(Path<? extends Village> path) {
        super(path.getType(), path.getMetadata());
    }

    public QVillage(PathMetadata metadata) {
        super(Village.class, metadata);
    }

}

