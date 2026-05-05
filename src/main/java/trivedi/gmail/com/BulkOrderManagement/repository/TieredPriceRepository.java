package trivedi.gmail.com.BulkOrderManagement.repository;

import trivedi.gmail.com.BulkOrderManagement.entity.TieredPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TieredPriceRepository extends JpaRepository<TieredPrice, Long> {

    @Query("SELECT t FROM TieredPrice t WHERE t.product.id = :productId ORDER BY t.minQuantity ASC")
    List<TieredPrice> findByProductIdOrderByMinQuantityAsc(@Param("productId") Long productId);

    void deleteByProductId(Long productId);
}
