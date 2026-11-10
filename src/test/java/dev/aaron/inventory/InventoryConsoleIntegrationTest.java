package dev.aaron.inventory;

import dev.aaron.inventory.product.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(properties = {
        "app.security.admin.username=integration-admin",
        "app.security.admin.password=integration-only-secret"
})
@AutoConfigureMockMvc
class InventoryConsoleIntegrationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17-alpine");

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ProductRepository productRepository;

    @BeforeEach
    void clearProducts() {
        productRepository.deleteAll();
    }

    @Test
    void searchesLiteralPrefixesAndReturnsBoundedSlices() throws Exception {
        createProduct("BEAR-100", "20% Genuine Bearing");
        createProduct("BEAR-200", "Bearing Housing");

        mockMvc.perform(get("/api/v1/products").param("q", "%").with(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].sku").value("BEAR-100"));

        mockMvc.perform(get("/api/v1/products").param("q", "bear").with(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));
    }

    @Test
    void validatesRequestsAndReportsDuplicateSkuAsConflict() throws Exception {
        String validProduct = """
                {"sku":"PUMP-100","name":"Industrial Pump","category":"Pumps","price":125.00,"quantity":4}
                """;
        mockMvc.perform(post("/api/v1/products").with(admin()).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(validProduct))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/products").with(admin()).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(validProduct))
                .andExpect(status().isConflict());

        String invalidProduct = """
                {"sku":"PUMP-101","name":"Industrial Pump","category":"Pumps","price":0,"quantity":4}
                """;
        mockMvc.perform(post("/api/v1/products").with(admin()).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(invalidProduct))
                .andExpect(status().isBadRequest());
    }

    private void createProduct(String sku, String name) throws Exception {
        String body = """
                {"sku":"%s","name":"%s","category":"Bearings","price":42.50,"quantity":20}
                """.formatted(sku, name);
        mockMvc.perform(post("/api/v1/products").with(admin()).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
    }

    private RequestPostProcessor admin() {
        return user("integration-admin").roles("ADMIN");
    }
}
