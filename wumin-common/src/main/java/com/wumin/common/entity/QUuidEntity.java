package com.wumin.common.entity;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.StringPath;

import javax.annotation.Generated;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


/**
 * QUuidEntity is a Querydsl query type for UuidEntity
 */
@Generated("com.querydsl.codegen.SupertypeSerializer")
public class QUuidEntity extends EntityPathBase<UuidEntity> {

    private static final long serialVersionUID = 1715722320L;

    public static final QUuidEntity uuidEntity = new QUuidEntity("uuidEntity");

    public final StringPath id = createString("id");

    public QUuidEntity(String variable) {
        super(UuidEntity.class, forVariable(variable));
    }

    public QUuidEntity(Path<? extends UuidEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUuidEntity(PathMetadata metadata) {
        super(UuidEntity.class, metadata);
    }

}

