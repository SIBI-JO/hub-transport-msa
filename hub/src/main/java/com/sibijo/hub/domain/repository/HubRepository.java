package com.sibijo.hub.domain.repository;

import com.sibijo.hub.domain.model.HubEntity;
import com.sibijo.hub.domain.model.HubType;
import com.sibijo.hub.presentation.dto.HubResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HubRepository {

    boolean existsByHubNameAndHubLocation(String hubName, String hubLocation);

    HubEntity save(HubEntity hub);

    List<HubEntity> findAll();

    Optional<HubEntity> findById(UUID hubId);

    void delete(HubEntity hub);

    Page<HubResponseDto> searchHubs(String hubName, String hubLocation, HubType hubType,
                                    Pageable pageable);

    boolean existsByHubId(UUID hubId);
}
