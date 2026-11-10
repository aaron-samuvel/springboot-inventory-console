package dev.aaron.inventory.product;

import jakarta.validation.Valid;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
public class ProductApiController {
    private final ProductService service;

    public ProductApiController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public Slice<ProductResponse> list(@RequestParam(defaultValue = "") String q,
                                       @RequestParam(defaultValue = "0") int page) {
        return service.findPage(q, page).map(ProductResponse::from);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(@Valid @RequestBody ProductCreateRequest request) {
        return ProductResponse.from(service.create(request));
    }
}
