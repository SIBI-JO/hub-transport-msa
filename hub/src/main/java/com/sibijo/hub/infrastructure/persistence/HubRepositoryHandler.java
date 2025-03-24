package com.sibijo.hub.infrastructure.persistence;

import com.sibijo.hub.domain.model.HubEntity;
import com.sibijo.hub.domain.model.HubType;
import com.sibijo.hub.domain.repository.HubRepository;
import com.sibijo.hub.presentation.dto.HubResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HubRepositoryHandler implements HubRepository {

    private final JpaHubRepository jpaHubRepository;
    private final QueryDslHubRepository queryDslHubRepository;


    /**
     * @param hubName
     * @param hubLocation
     * @return
     */
    @Override
    public boolean existsByHubNameAndHubLocation(String hubName, String hubLocation) {
        return jpaHubRepository.existsByHubNameAndHubLocation(hubName, hubLocation);
    }

    /**
     * @param hub
     * @return
     */
    @Override
    public HubEntity save(HubEntity hub) {
        return jpaHubRepository.save(hub);
    }

    /**
     * @param
     * @return
     */
    @Override
    public List<HubEntity> findAll() {
        return jpaHubRepository.findAll();
    }

    /**
     * @param hubId
     * @return
     */
    @Override
    public Optional<HubEntity> findById(UUID hubId) {
        return jpaHubRepository.findById(hubId);
    }

    /**
     * @param hub
     */
    @Override
    public void delete(HubEntity hub) {
        jpaHubRepository.delete(hub);
    }

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
        return queryDslHubRepository.searchHubs(hubName, hubLocation, hubType, pageable);
    }

    /**
     * @param hubId
     * @return
     */
    @Override
    public boolean existsByHubId(UUID hubId) {
        return jpaHubRepository.existsById(hubId);
    }
}
