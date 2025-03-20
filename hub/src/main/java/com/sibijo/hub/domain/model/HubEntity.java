package com.sibijo.hub.domain.model;


import com.sibijo.common.entity.BaseEntity;
import com.sibijo.common.exception.CustomException;
import com.sibijo.hub.exception.domain.HubDomainExceptionCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "p_hub")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_deleted = false")
@SQLDelete(sql = "UPDATE p_hub SET is_deleted = true WHERE hub_id = ?")
/**
 *  허브 서비스 엔티티 클래스
 *
 *  허브의 고유 ID, 이름, 위치, 좌표, 타입
 */
public class HubEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "hub_id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "hub_name", nullable = false)
    private String hubName;

    @Column(name = "hub_location", nullable = false)
    private String hubLocation;

    /**
     * 허브의 위도 최대 10자리 숫자와 소수점 7자리까지 허용
     */
    @Column(name = "latitude", precision = 10, scale = 7, nullable = false)
    private BigDecimal latitude;

    /**
     * 허브의 경도 최대 10자리 숫자와 소수점 7자리까지 허용
     */
    @Column(name = "longitude", precision = 10, scale = 7, nullable = false)
    private BigDecimal longitude;

    /**
     * 허브의 종류 enumType
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "hub_type", nullable = false)
    private HubType hubType;

    @Builder
    private HubEntity(
            String hubName,
            String hubLocation,
            BigDecimal latitude,
            BigDecimal longitude,
            HubType hubType) {
        this.hubName = hubName;
        this.hubLocation = hubLocation;
        this.latitude = latitude;
        this.longitude = longitude;
        this.hubType = hubType;
    }

    protected void updateHub(
            String hubName,
            String hubLocation,
            BigDecimal latitude,
            BigDecimal longitude,
            HubType hubType
    ) {
        this.hubName = hubName;
        this.hubLocation = hubLocation;
        this.latitude = latitude;
        this.longitude = longitude;
        this.hubType = hubType;
    }

    public void updateHubName(String hubName) {
        if (hubName != null && !hubName.trim().isEmpty()) {
            this.hubName = hubName;
        } else {
            throw new CustomException(HubDomainExceptionCode.INVALID_HUB_NAME);
        }
    }

    public void updateHubLocation(String hubLocation) {
        if (hubLocation != null && !hubLocation.trim().isEmpty()) {
            this.hubLocation = hubLocation;
        } else {
            throw new CustomException(HubDomainExceptionCode.INVALID_HUB_LOCATION);
        }
    }

    public void updateHubType(HubType hubType) {
        if (hubType != null) {
            this.hubType = hubType;
        }
    }
}
