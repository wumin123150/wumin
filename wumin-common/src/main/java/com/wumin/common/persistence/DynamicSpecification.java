package com.wumin.common.persistence;

import com.wumin.common.collection.CollectionUtil;
import com.wumin.common.collection.ListUtil;
import com.wumin.common.entity.DeleteRecord;
import com.wumin.common.persistence.search.*;
import com.wumin.common.time.DateFormatUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import javax.persistence.criteria.CriteriaBuilder.In;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class DynamicSpecification {
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static <T> Specification<T> bySearchRequest(final SearchRequest searchRequest, final Class<T> entityClazz) {
		return new Specification<T>() {
			public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				Predicate predicate = filterTo(root, builder, searchRequest.getOperator(), searchRequest.getSearchFilters());
				if(searchRequest.hasDelFlagFilter() && DeleteRecord.class.isAssignableFrom(entityClazz)) {
					Predicate delFlagPredicate = builder.equal(root.get("delFlag").as(Boolean.class), false);
					return predicate != null ? builder.and(delFlagPredicate, predicate): delFlagPredicate;
				}
				return predicate;
			}
		};
	}

	public static <T> Predicate filterTo(Root<T> root, CriteriaBuilder builder, BooleanOperator operator, List<SearchFilter> searchFilters) {
		List<Predicate> predicates = ListUtil.newArrayList();

		if (CollectionUtil.isNotEmpty(searchFilters)) {
			for (SearchFilter searchFilter : searchFilters) {
				if (searchFilter instanceof CompoundFilter) {
					CompoundFilter filter = (CompoundFilter) searchFilter;
					Predicate predicate = filterTo(root, builder, filter.getOperator(), filter.getSearchFilters());
					if (predicate != null) {
						predicates.add(predicate);
					}
				} else {
					SimpleFilter filter = (SimpleFilter) searchFilter;

					// nested path translate, 如Task的名为"user.name"的filedName, 转换为Task.user.name属性
					String[] names = StringUtils.split(filter.getPropertyName(), ".");
					Path expression = root.get(names[0]);
					for (int i = 1; i < names.length; i++) {
						expression = expression.get(names[i]);
					}

					Object value = null;

					// logic operator
					switch (filter.getOperator()) {
						case EQ:
							value = stringTo(filter.getValue(), expression.getJavaType());
							predicates.add(builder.equal(expression, value));
							break;
						case NE:
							value = stringTo(filter.getValue(), expression.getJavaType());
							predicates.add(builder.notEqual(expression, value));
							break;
						case LT:
							value = stringTo(filter.getValue(), expression.getJavaType());
							predicates.add(builder.lessThan(expression, (Comparable) value));
							break;
						case LE:
							value = stringTo(filter.getValue(), expression.getJavaType());
							predicates.add(builder.lessThanOrEqualTo(expression, (Comparable) value));
							break;
						case GT:
							value = stringTo(filter.getValue(), expression.getJavaType());
							predicates.add(builder.greaterThan(expression, (Comparable) value));
							break;
						case GE:
							value = stringTo(filter.getValue(), expression.getJavaType());
							predicates.add(builder.greaterThanOrEqualTo(expression, (Comparable) value));
							break;
						case SW:
							predicates.add(builder.like(expression, filter.getValue() + "%"));
							break;
						case SN:
							predicates.add(builder.notLike(expression, filter.getValue() + "%"));
							break;
						case EW:
							predicates.add(builder.like(expression, "%" + filter.getValue()));
							break;
						case EN:
							predicates.add(builder.notLike(expression, "%" + filter.getValue()));
							break;
						case CN:
							predicates.add(builder.like(expression, "%" + filter.getValue() + "%"));
							break;
						case NC:
							predicates.add(builder.notLike(expression, "%" + filter.getValue() + "%"));
							break;
						case NU:
							predicates.add(builder.isNull(expression));
							break;
						case NN:
							predicates.add(builder.isNotNull(expression));
							break;
						case IN:
							In in = builder.in(expression);
							for (Object val : (List) filter.getValue()) {
								in.value(stringTo(val, expression.getJavaType()));
							}
							predicates.add(in);
							break;
						case NI:
							In nin = builder.in(expression);
							for (Object val : (List) filter.getValue()) {
								nin.value(stringTo(val, expression.getJavaType()));
							}
							predicates.add(builder.not(nin));
							break;
						default:
							break;
					}
				}
			}
		}

		// 将所有条件用 and 或者 or 联合起来
		if (!predicates.isEmpty()) {
			if(operator == BooleanOperator.OR) {
				return builder.or(predicates.toArray(new Predicate[predicates.size()]));
			} else {
				return builder.and(predicates.toArray(new Predicate[predicates.size()]));
			}
		}

		return null;
	}
	
	private static Object stringTo(Object value, final Class clazz) {
        if(value instanceof String) {
            if(Boolean.class.isAssignableFrom(clazz)) {
                return Boolean.valueOf((String)value);
            }
            if(Short.class.isAssignableFrom(clazz)) {
                return Short.valueOf((String)value);
            }
            if(Integer.class.isAssignableFrom(clazz)) {
                return Integer.valueOf((String)value);
            }
            if(Long.class.isAssignableFrom(clazz)) {
                return Long.valueOf((String)value);
            }
            if(Float.class.isAssignableFrom(clazz)) {
                return Float.valueOf((String)value);
            }
            if(Double.class.isAssignableFrom(clazz)) {
                return Double.valueOf((String)value);
            }
            if(BigDecimal.class.isAssignableFrom(clazz)) {
                return new BigDecimal((String)value);
            }
            if(Date.class.isAssignableFrom(clazz)) {
                return DateFormatUtil.parseShortDate((String)value);
            }
            if(String.class.isAssignableFrom(clazz)) {
                return StringUtils.trim((String)value);
            }
        }
        return value;
    }
}
