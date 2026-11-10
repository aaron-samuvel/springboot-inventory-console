package dev.aaron.inventory.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductCreateRequest(
        @NotBlank @Size(max = 32) @Pattern(regexp = "[A-Za-z0-9._-]+") String sku,
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Size(max = 80) String category,
        @NotNull @DecimalMin("0.01") @Digits(integer = 10, fraction = 2) BigDecimal price,
        @Min(0) @Max(1_000_000) int quantity) {
}
