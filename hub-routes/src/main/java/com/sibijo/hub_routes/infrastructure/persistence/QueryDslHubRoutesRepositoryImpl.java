package com.sibijo.hub_routes.infrastructure.persistence;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sibijo.hub_routes.domain.model.HubRoutesEntity;
import com.sibijo.hub_routes.presentation.dto.HubRoutesResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.sibijo.hub_routes.domain.model.QHubRoutesEntity.hubRoutesEntity;

@Repository
@RequiredArgsConstructor
public class QueryDslHubRoutesRepositoryImpl implements QueryDslHubRoutesRepository {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * @param validatedPageable
     * @return
     */
    @Override
    public Page<HubRoutesResponseDto> searchHubRoutes(Pageable validatedPageable) {

        List<OrderSpecifier<?>> orders = getAllOrderSpecifiers(validatedPageable);

        QueryResults<HubRoutesEntity> results = jpaQueryFactory
                .selectFrom(hubRoutesEntity)
                .where(
                )
                .orderBy(orders.toArray(new OrderSpecifier[0]))
                .offset(validatedPageable.getOffset())
                .limit(validatedPageable.getPageSize())
                .fetchResults();

        List<HubRoutesResponseDto> content = results.getResults().stream()
                .map(hubRoutesEntity -> new HubRoutesResponseDto(
                        hubRoutesEntity.getId(),
                        hubRoutesEntity.getDepartureId(),
                        hubRoutesEntity.getDestinationId(),
                        hubRoutesEntity.getDistance(),
                        hubRoutesEntity.getEstimatedTime(),
                        hubRoutesEntity.getSequence(),
                        hubRoutesEntity.getHashSequence()
                ))
                .collect(Collectors.toList());
        long total = results.getTotal();
        return new PageImpl<>(content, validatedPageable, total);
    }

    private List<OrderSpecifier<?>> getAllOrderSpecifiers(Pageable validatedPageable) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if (validatedPageable.getSort() != null) {
            for (Sort.Order sortOrder : validatedPageable.getSort()) {
                com.querydsl.core.types.Order direction =
                        sortOrder.isAscending() ? Order.ASC : Order.DESC;
                switch (sortOrder.getProperty()) {
                    case "createdAt":
                        orders.add(new OrderSpecifier<>(direction, hubRoutesEntity.createdAt));
                        break;
                    case "updatedAt":
                        orders.add(new OrderSpecifier<>(direction, hubRoutesEntity.updatedAt));
                        break;
                    default:
                        break;
                }
            }
        }
        return orders;
    }
}
