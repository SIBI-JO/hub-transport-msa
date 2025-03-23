package com.sibijo.hub.infrastructure.persistence;

import static com.sibijo.hub.domain.model.QHubEntity.hubEntity;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sibijo.hub.domain.model.HubEntity;
import com.sibijo.hub.domain.model.HubType;
import com.sibijo.hub.presentation.dto.HubResponseDto;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QueryDslHubRepositoryImpl implements QueryDslHubRepository {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * @param hubName
     * @param hubLocation
     * @param hubType
     * @param pageable
     * @return
     */
    @Override
    public Page<HubResponseDto> searchHubs(String hubName, String hubLocation, HubType hubType,
            Pageable pageable) {

        List<OrderSpecifier<?>> orders = getAllOrderSpecifiers(pageable);
        QueryResults<HubEntity> results = jpaQueryFactory
                .selectFrom(hubEntity)
                .where(
                        hubNameContains(hubName),
                        hubLocationContains(hubLocation),
                        hubTypeContains(hubType)
                )
                .orderBy(orders.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<HubResponseDto> content = results.getResults().stream()
                .map(hubEntity -> new HubResponseDto(
                        hubEntity.getId(),
                        hubEntity.getHubName(),
                        hubEntity.getHubLocation(),
                        hubEntity.getLatitude(),
                        hubEntity.getLongitude(),
                        hubEntity.getHubType().getHubTypeName()
                ))
                .collect(Collectors.toList());
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression hubNameContains(String hubName) {
        if (hubName == null || hubName.isEmpty()) {
            return null;
        }
        return hubEntity.hubName.containsIgnoreCase(hubName);
    }

    private BooleanExpression hubLocationContains(String hubLocation) {
        if (hubLocation == null || hubLocation.isEmpty()) {
            return null;
        }
        return hubEntity.hubLocation.containsIgnoreCase(hubLocation);
    }

    private BooleanExpression hubTypeContains(HubType hubType) {

        return hubType != null ? hubEntity.hubType.eq(hubType) : null;
    }

    private List<OrderSpecifier<?>> getAllOrderSpecifiers(Pageable pageable) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if (pageable.getSort() != null) {
            for (Sort.Order sortOrder : pageable.getSort()) {
                com.querydsl.core.types.Order direction =
                        sortOrder.isAscending() ? Order.ASC : Order.DESC;
                switch (sortOrder.getProperty()) {
                    case "hubName":
                        orders.add(new OrderSpecifier<>(direction, hubEntity.hubName));
                        break;
                    case "hubLocation":
                        orders.add(new OrderSpecifier<>(direction, hubEntity.hubLocation));
                        break;
                    case "hubTypeName":
                        orders.add(new OrderSpecifier<>(direction, hubEntity.hubType));
                        break;
                    case "createdAt":
                        orders.add(new OrderSpecifier<>(direction, hubEntity.createdAt));
                        break;
                    case "updatedAt":
                        orders.add(new OrderSpecifier<>(direction, hubEntity.updatedAt));
                        break;
                    default:
                        break;
                }
            }
        }
        return orders;
    }
}
