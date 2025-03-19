package com.sibijo.product.infrastructure.repository;


import com.sibijo.product.domain.entity.SlackMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SlackMessageRepository extends JpaRepository<SlackMessage, Long> {
}
