package dev.aaron.inventory.product;

import java.math.BigDecimal;

public record ProductResponse(Long id, String sku, String name, String category,
                              BigDecimal price, int quantity, long version) {
    static ProductResponse from(Product product) {
        return new ProductResponse(product.getId(), product.getSku(), product.getName(),
                product.getCategory(), product.getPrice(), product.getQuantity(), product.getVersion());
    }
}
