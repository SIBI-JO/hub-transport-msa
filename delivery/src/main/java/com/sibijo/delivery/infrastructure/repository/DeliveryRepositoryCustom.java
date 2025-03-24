package com.sibijo.delivery.infrastructure.repository;

import com.sibijo.delivery.domain.entity.Delivery;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DeliveryRepositoryCustom {

    @Query("SELECT o FROM Delivery o WHERE (o.startHubId = :hubId OR o.endHubId = :hubId) AND o.deletedAt IS NULL")
    Page<Delivery> findDeliveriesByHubId(@Param("hubId") UUID hubId, Pageable pageable);

    @Query("SELECT d FROM Delivery d " +
            "WHERE d.deletedAt IS NULL " +
            "AND (:receiver IS NULL OR LOWER(d.receiver) LIKE LOWER(CONCAT('%', :receiver, '%'))) " +
            "AND (:address IS NULL OR LOWER(d.deliveryAddress) LIKE LOWER(CONCAT('%', :address, '%')))")
    Page<Delivery> searchByReceiverAndAddress(
            @Param("receiver") String receiver,
            @Param("address") String address,
            Pageable pageable);


    @Query("SELECT d FROM Delivery d WHERE d.deletedAt IS NULL " +
            "AND (d.startHubId = :hubId OR d.endHubId = :hubId) " +
            "AND (:receiver IS NULL OR LOWER(d.receiver) LIKE LOWER(CONCAT('%', :receiver, '%'))) " +
            "AND (:address IS NULL OR LOWER(d.deliveryAddress) LIKE LOWER(CONCAT('%', :address, '%')))")
    Page<Delivery> searchByReceiverAndAddressForHubManager(
            @Param("hubId") UUID hubId,
            @Param("receiver") String receiver,
            @Param("address") String address,
            Pageable pageable);

    @Query("SELECT d FROM Delivery d WHERE d.deletedAt IS NULL " +
            "AND d.deliveryManagerId = :managerId " +
            "AND (:receiver IS NULL OR LOWER(d.receiver) LIKE LOWER(CONCAT('%', :receiver, '%'))) " +
            "AND (:address IS NULL OR LOWER(d.deliveryAddress) LIKE LOWER(CONCAT('%', :address, '%')))")
    Page<Delivery> searchByReceiverAndAddressAndManager(
            @Param("managerId") Long managerId,
            @Param("receiver") String receiver,
            @Param("address") String address,
            Pageable pageable);


    @Query("SELECT d FROM Delivery d WHERE d.deletedAt IS NULL " +
            "AND d.recipientsId = :companyId " +
            "AND (:receiver IS NULL OR LOWER(d.receiver) LIKE LOWER(CONCAT('%', :receiver, '%'))) " +
            "AND (:address IS NULL OR LOWER(d.deliveryAddress) LIKE LOWER(CONCAT('%', :address, '%')))")
    Page<Delivery> searchByReceiverAndAddressAndRecipient(
            @Param("companyId") UUID companyId,
            @Param("receiver") String receiver,
            @Param("address") String address,
            Pageable pageable);

}
