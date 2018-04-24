package com.wumin.common.entity;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;

import javax.annotation.Generated;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

/**
 * QIdEntity is a Querydsl query type for IdEntity
 */
@Generated("com.querydsl.codegen.SupertypeSerializer")
public class QIdEntity extends EntityPathBase<IdEntity> {

    private static final long serialVersionUID = -317884880L;

    public static final QIdEntity idEntity = new QIdEntity("idEntity");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QIdEntity(String variable) {
        super(IdEntity.class, forVariable(variable));
    }

    public QIdEntity(Path<? extends IdEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QIdEntity(PathMetadata metadata) {
        super(IdEntity.class, metadata);
    }

}

