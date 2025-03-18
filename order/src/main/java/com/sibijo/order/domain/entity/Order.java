package com.sibijo.order.domain.entity;


import com.sibijo.order.presentation.dto.OrderRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@Table(catalog = "sibijo", name = "p_order")
public class Order {

    @Id
    @GeneratedValue
    @Column(nullable = false)
    private UUID orderId;

    @Column(nullable = false)
    private UUID supplierId;

    @Column(nullable = false)
    private UUID recipientsId;

    @Column(nullable = false)
    private UUID productId;

    @Column(nullable = false)
    private UUID deliveryId;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false)
    private String request;

    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
    private LocalDateTime deletedAt;
    private String deletedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }


    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public static Order createOrder(OrderRequestDto requestDto, UUID deliveryId) {
        return Order.builder()
                .supplierId(requestDto.getSupplierId())
                .recipientsId(requestDto.getRecipientsId())
                .productId(requestDto.getProductId())
                .deliveryId(deliveryId)
                .amount(requestDto.getAmount())
                .request(requestDto.getRequest())
                .build();
    }


//    public void updateOrder(List<Long> orderItemIds, String updatedBy, ) {
//
//        this.updatedBy = updatedBy;
//        this.updatedAt = LocalDateTime.now();
//    }


    public void deleteOrder(String deletedBy) {
        this.deletedBy = deletedBy;
        this.deletedAt = LocalDateTime.now();
    }

}
