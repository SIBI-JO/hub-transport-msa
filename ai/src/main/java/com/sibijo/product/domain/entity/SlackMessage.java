package com.sibijo.product.domain.entity;



import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "slack_messages")
@Data
public class SlackMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 메시지를 보낸 대상 (Slack User ID)
    private String recipientSlackId;

    // 전송된 메시지 내용
    @Column(columnDefinition = "TEXT")
    private String message;

    // 실제 발송 시각
    private LocalDateTime sentAt;
}
