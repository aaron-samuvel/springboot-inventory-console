package dev.aaron.inventory.product;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {
    private static final int PAGE_SIZE = 25;
    private static final int MAX_PAGE = 10_000;

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Slice<Product> findPage(String query, int requestedPage) {
        int page = Math.max(0, Math.min(requestedPage, MAX_PAGE));
        String prefix = query == null ? "" : query.trim();
        if (prefix.length() > 80) {
            prefix = prefix.substring(0, 80);
        }
        PageRequest request = PageRequest.of(page, PAGE_SIZE, Sort.by("sku").ascending());
        if (prefix.isEmpty()) {
            return repository.findSlice(request);
        }
        String pattern = prefix.toLowerCase(java.util.Locale.ROOT)
                .replace("!", "!!")
                .replace("%", "!%")
                .replace("_", "!_") + "%";
        return repository.searchByPrefix(pattern, request);
    }

    @Transactional
    public Product create(ProductCreateRequest request) {
        Product product = new Product(request.sku(), request.name(), request.category(),
                request.price(), request.quantity());
        return repository.save(product);
    }
}
