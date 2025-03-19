package com.sibijo.user.domain.model;

import com.sibijo.common.entity.BaseEntity;
import com.sibijo.user.domain.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.util.StringUtils;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "p_users")
@SQLRestriction("is_deleted = false")
@SQLDelete(sql = "UPDATE p_users SET is_deleted = true WHERE id = ?")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String slackId;

    @Column
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column
    private UUID hubId;

    @Column
    private UUID companyId;

    private User(String username, String password, String slackId, Role role, UUID hubId,
            UUID companyId) {
        this.username = username;
        this.slackId = slackId;
        this.password = password;
        this.role = role;
        this.hubId = hubId;
        this.companyId = companyId;
    }

    public static User of(String username, String password, String slackId, Role role, UUID hubId,
            UUID companyId) {
        return new User(
                username,
                password,
                slackId,
                role,
                hubId,
                companyId
        );
    }

    public void updateUser(String username, String slackId, String password) {
        this.username = StringUtils.hasText(username) ? username : this.username;
        this.slackId = StringUtils.hasText(slackId) ? slackId : this.slackId;
        this.password = StringUtils.hasText(password) ? password : this.password;
    }
}
