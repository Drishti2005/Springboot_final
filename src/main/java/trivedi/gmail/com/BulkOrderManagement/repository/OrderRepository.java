package trivedi.gmail.com.BulkOrderManagement.repository;

import trivedi.gmail.com.BulkOrderManagement.entity.Order;
import trivedi.gmail.com.BulkOrderManagement.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByRetailerIdOrderByOrderDateDesc(Long retailerId);

    List<Order> findByRetailerIdAndStatus(Long retailerId, OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.retailer.id = :retailerId ORDER BY o.orderDate DESC LIMIT 1")
    Optional<Order> findLastOrderByRetailerId(@Param("retailerId") Long retailerId);
}
