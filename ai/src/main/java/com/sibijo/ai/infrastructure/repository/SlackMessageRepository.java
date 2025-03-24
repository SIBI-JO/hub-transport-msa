package com.sibijo.ai.infrastructure.repository;

import com.sibijo.ai.domain.entity.SlackMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SlackMessageRepository extends JpaRepository<SlackMessage, UUID> {

}
