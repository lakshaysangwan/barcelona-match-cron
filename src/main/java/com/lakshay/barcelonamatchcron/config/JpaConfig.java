package com.lakshay.barcelonamatchcron.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaConfig {
    // This empty class is needed to enable JPA auditing
}
