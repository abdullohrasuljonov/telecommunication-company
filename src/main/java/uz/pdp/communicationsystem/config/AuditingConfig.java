package uz.pdp.communicationsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;

import java.util.UUID;

public class AuditingConfig {

    @Bean
    AuditorAware<UUID> auditorAware(){
        return new SpringSecurityAuditAwareImpl();
    }
}
