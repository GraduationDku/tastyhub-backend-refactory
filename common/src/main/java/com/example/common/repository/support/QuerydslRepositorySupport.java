package com.example.common.repository.support;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.Assert;

import java.util.List;
import java.util.function.Function;

public abstract class QuerydslRepositorySupport {

    protected final JPAQueryFactory jpaQueryFactory;

    /**
     * 생성자에서 JPAQueryFactory를 직접 주입받습니다.
     */
    public QuerydslRepositorySupport(JPAQueryFactory jpaQueryFactory) {
        Assert.notNull(jpaQueryFactory, "JPAQueryFactory must not be null!");
        this.jpaQueryFactory = jpaQueryFactory;
    }

    protected JPAQuery<Long> createCountQuery(EntityPath<?> from) {
        return jpaQueryFactory.select(Wildcard.count)
                .from(from);
    }

    protected <T> Page<T> applyPagination(Pageable pageable,
                                          Function<JPAQueryFactory, JPAQuery<T>> contentQuery,
                                          Function<JPAQueryFactory, JPAQuery<Long>> countQuery) {

        JPAQuery<T> jpaContentQuery = contentQuery.apply(jpaQueryFactory);

        List<T> content = jpaContentQuery
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> jpaCountQuery = countQuery.apply(jpaQueryFactory);

        return PageableExecutionUtils.getPage(
                content,
                pageable,
                () -> {
                    Long totalCount = jpaCountQuery.fetchOne();
                    return (totalCount != null) ? totalCount : 0L;
                }
        );
    }
}
