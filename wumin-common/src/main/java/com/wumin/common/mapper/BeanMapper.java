package com.wumin.common.mapper;

import com.wumin.common.collection.ListUtil;
import com.wumin.common.collection.MapUtil;
import ma.glasnost.orika.*;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeFactory;

import java.util.List;
import java.util.Map;

/**
 * 简单封装orika, 实现深度的BeanOfClasssA<->BeanOfClassB复制
 * 不要是用Apache Common BeanUtils进行类复制，每次就行反射查询对象的属性列表, 非常缓慢.
 * 注意: 需要参考本模块的POM文件，显式引用orika.
 *
 * @author walker
 */
public class BeanMapper {

  private static MapperFactory mapperFactory;
  private static MapperFacade mapper;

  static {
    mapperFactory = new DefaultMapperFactory.Builder().build();
    mapper = mapperFactory.getMapperFacade();
  }

  public static <S, D> void registerClassMap(Class<S> sourceClass, Class<D> destinationClass, boolean mapNulls, boolean mapNullsInReverse) {
    registerClassMap(sourceClass, destinationClass, MapUtil.<String, String>emptyMap(), ListUtil.<String>emptyList(), null, mapNulls, mapNullsInReverse);
  }

  public static <S, D> void registerClassMap(Class<S> sourceClass, Class<D> destinationClass, Map<String, String> mapFields) {
    registerClassMap(sourceClass, destinationClass, mapFields, ListUtil.<String>emptyList(), null, true, true);
  }

  public static <S, D> void registerClassMap(Class<S> sourceClass, Class<D> destinationClass, FieldsMapper<S, D> fieldsMapper) {
    registerClassMap(sourceClass, destinationClass, MapUtil.<String, String>emptyMap(), ListUtil.<String>emptyList(), fieldsMapper, true, true);
  }

  public static <S, D> void registerClassMap(Class<S> sourceClass, Class<D> destinationClass, Map<String, String> mapFields, FieldsMapper<S, D> fieldsMapper) {
    registerClassMap(sourceClass, destinationClass, mapFields, ListUtil.<String>emptyList(), fieldsMapper, true, true);
  }

  public static <S, D> void registerClassMap(Class<S> sourceClass, Class<D> destinationClass, List<String> excludeFields, FieldsMapper<S, D> fieldsMapper) {
    registerClassMap(sourceClass, destinationClass, MapUtil.<String, String>emptyMap(), excludeFields, fieldsMapper, true, true);
  }

  public static <S, D> void registerClassMap(Class<S> sourceClass, Class<D> destinationClass, Map<String, String> mapFields, List<String> excludeFields, final FieldsMapper<S, D> fieldsMapper, boolean mapNulls, boolean mapNullsInReverse) {
    if (!mapperFactory.existsRegisteredMapper(TypeFactory.valueOf(sourceClass), TypeFactory.valueOf(destinationClass), false)) {
      ClassMapBuilder<S, D> builder = mapperFactory.classMap(sourceClass, destinationClass)
        .mapNulls(mapNulls).mapNullsInReverse(mapNullsInReverse);
      for (Map.Entry<String, String> entry : mapFields.entrySet()) {
        builder.field(entry.getKey(), entry.getValue());
      }
      for (String field : excludeFields) {
        builder.exclude(field);
      }
      if(fieldsMapper != null) {
        builder.customize(new CustomMapper<S, D>() {
          @Override
          public void mapAtoB(S source, D destination, MappingContext context) {
            super.mapAtoB(source, destination, context);
            fieldsMapper.map(source, destination);
          }
          @Override
          public void mapBtoA(D destination, S source, MappingContext context) {
            super.mapBtoA(destination, source, context);
            fieldsMapper.reverseMap(destination, source);
          }
        });
      }
      builder.byDefault().register();
    }
  }

  /**
   * 简单的复制出新类型对象.
   * <p>
   * 通过source.getClass() 获得源Class
   */
  public static <S, D> D map(S source, Class<D> destinationClass) {
    return mapper.map(source, destinationClass);
  }

  /**
   * 极致性能的复制出新类型对象.
   * <p>
   * 预先通过BeanMapper.getType() 静态获取并缓存Type类型，在此处传入
   */
  public static <S, D> D map(S source, Type<S> sourceType, Type<D> destinationType) {
    return mapper.map(source, sourceType, destinationType);
  }

  /**
   * 简单的复制属性到其他对象.
   */
  public static <S, D> D map(S source, D destination, Class<S> sourceClass, Class<D> destinationClass) {
    BoundMapperFacade<S, D> boundMapper = mapperFactory.getMapperFacade(TypeFactory.valueOf(sourceClass), TypeFactory.valueOf(destinationClass));
    return boundMapper.map(source, destination);
  }

  /**
   * 简单的复制出新对象列表到ArrayList
   * <p>
   * 不建议使用mapper.mapAsList(Iterable<S>,Class<D>)接口, sourceClass需要反射，实在有点慢
   */
  public static <S, D> List<D> mapList(Iterable<S> sourceList, Class<S> sourceClass, Class<D> destinationClass) {
    return mapper.mapAsList(sourceList, TypeFactory.valueOf(sourceClass), TypeFactory.valueOf(destinationClass));
  }

  /**
   * 极致性能的复制出新类型对象到ArrayList.
   * <p>
   * 预先通过BeanMapper.getType() 静态获取并缓存Type类型，在此处传入
   */
  public static <S, D> List<D> mapList(Iterable<S> sourceList, Type<S> sourceType, Type<D> destinationType) {
    return mapper.mapAsList(sourceList, sourceType, destinationType);
  }

  /**
   * 简单复制出新对象列表到数组
   * <p>
   * 通过source.getComponentType() 获得源Class
   */
  public static <S, D> D[] mapArray(final D[] destination, final S[] source, final Class<D> destinationClass) {
    return mapper.mapAsArray(destination, source, destinationClass);
  }

  /**
   * 极致性能的复制出新类型对象到数组
   * <p>
   * 预先通过BeanMapper.getType() 静态获取并缓存Type类型，在此处传入
   */
  public static <S, D> D[] mapArray(D[] destination, S[] source, Type<S> sourceType, Type<D> destinationType) {
    return mapper.mapAsArray(destination, source, sourceType, destinationType);
  }

  /**
   * 预先获取orika转换所需要的Type，避免每次转换.
   */
  public static <E> Type<E> getType(final Class<E> rawType) {
    return TypeFactory.valueOf(rawType);
  }

}