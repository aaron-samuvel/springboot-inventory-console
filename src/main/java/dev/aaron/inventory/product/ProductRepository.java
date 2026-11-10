package dev.aaron.inventory.product;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("select p from Product p")
    Slice<Product> findSlice(Pageable pageable);

    @Query("select p from Product p where lower(p.name) like :pattern escape '!' " +
            "or lower(p.sku) like :pattern escape '!'")
    Slice<Product> searchByPrefix(@Param("pattern") String pattern, Pageable pageable);
}
