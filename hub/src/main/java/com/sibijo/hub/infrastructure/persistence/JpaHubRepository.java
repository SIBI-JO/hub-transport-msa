package com.sibijo.hub.infrastructure.persistence;

import com.sibijo.hub.domain.model.HubEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA가 구현체
 */
public interface JpaHubRepository extends JpaRepository<HubEntity, UUID> {

    boolean existsByHubNameAndHubLocation(String hubName, String hubLocation);
}
