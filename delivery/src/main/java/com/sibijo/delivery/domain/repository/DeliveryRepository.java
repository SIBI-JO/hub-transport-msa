package com.sibijo.delivery.domain.repository;

import com.sibijo.delivery.domain.entity.Delivery;
import com.sibijo.delivery.infrastructure.repository.DeliveryRepositoryCustom;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface DeliveryRepository extends JpaRepository<Delivery, UUID>,
        DeliveryRepositoryCustom {

    List<Delivery> findAll();

    Optional<Delivery> findById(UUID id);

    Delivery save(Delivery delivery);

    Page<Delivery> findAllByDeletedAtIsNull(Pageable pageable);

    // 배송 담당자(DELIVERY) - 본인의 허브와 관련된 주문 중 COMPLETED 상태인 것만 조회
    Page<Delivery> findByDeliveryManagerIdAndDeletedAtIsNull(Long deliveryManagerId, Pageable pageable);

    // 업체 담당자(COMPANY) - 본인의 업체가 포함된 주문 중 COMPLETED 상태인 것만 조회
    Page<Delivery> findByRecipientsIdAndDeletedAtIsNull(UUID companyId, Pageable pageable);


}
