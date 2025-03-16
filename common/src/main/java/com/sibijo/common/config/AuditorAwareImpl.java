package com.sibijo.common.config;

import java.util.Optional;
import org.springframework.data.domain.AuditorAware;

public class AuditorAwareImpl implements AuditorAware<Integer> {

    @Override
    public Optional<Integer> getCurrentAuditor() {
        Integer userId = 1;
        return Optional.of(userId);
    }
}
