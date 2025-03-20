package com.sibijo.ai.infrastructure.repository;


import com.sibijo.ai.domain.entity.SlackMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SlackMessageRepository extends JpaRepository<SlackMessage, Long> {
}
