package com.sibijo.user.domain.repository;

import com.sibijo.user.domain.enums.DeliveryType;
import com.sibijo.user.domain.model.DeliveryAgent;
import com.sibijo.user.domain.model.User;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface DeliveryAgentRepository extends JpaRepository<DeliveryAgent, Long> {


    @Query("SELECT MAX(d.deliveryOrder) FROM DeliveryAgent d WHERE d.hubId = :hubId AND d.deliveryType = :deliveryType")
    Optional<Integer> findMaxDeliveryOrderByHubIdAndType(UUID hubId, DeliveryType deliveryType);

    @Query("SELECT MAX(d.deliveryOrder) FROM DeliveryAgent d WHERE d.deliveryType = :deliveryType")
    Optional<Integer> findMaxDeliveryOrderByType(DeliveryType deliveryType);

    Page<DeliveryAgent> findAllBydeliveryTypeContains(String deliveryType, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM DeliveryAgent d WHERE d.id = :id")
    Optional<DeliveryAgent> findByIdForUpdate(Long id);
}
