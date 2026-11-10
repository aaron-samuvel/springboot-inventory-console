package dev.aaron.inventory.web;

import dev.aaron.inventory.product.ProductCreateRequest;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class ProductForm {
    @NotBlank @Size(max = 32) @Pattern(regexp = "[A-Za-z0-9._-]+")
    private String sku;
    @NotBlank @Size(max = 120)
    private String name;
    @NotBlank @Size(max = 80)
    private String category;
    @NotNull @DecimalMin("0.01") @Digits(integer = 10, fraction = 2)
    private BigDecimal price;
    @Min(0) @Max(1_000_000)
    private int quantity;

    public ProductCreateRequest toRequest() {
        return new ProductCreateRequest(sku, name, category, price, quantity);
    }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
