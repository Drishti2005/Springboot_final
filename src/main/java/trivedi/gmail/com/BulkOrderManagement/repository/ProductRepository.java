package trivedi.gmail.com.BulkOrderManagement.repository;

import trivedi.gmail.com.BulkOrderManagement.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);

    @Query("SELECT p FROM Product p WHERE p.stockQuantity > 0")
    List<Product> findAllInStock();

    List<Product> findBySkuIn(List<String> skus);
}
