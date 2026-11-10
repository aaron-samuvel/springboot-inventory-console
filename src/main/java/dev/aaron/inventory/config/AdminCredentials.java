package dev.aaron.inventory.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("app.security.admin")
public record AdminCredentials(@NotBlank String username, @NotBlank String password) {
}
