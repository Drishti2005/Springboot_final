package trivedi.gmail.com.BulkOrderManagement.repository;

import trivedi.gmail.com.BulkOrderManagement.entity.RetailerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RetailerProfileRepository extends JpaRepository<RetailerProfile, Long> {

    Optional<RetailerProfile> findByUserId(Long userId);

    Optional<RetailerProfile> findByGstId(String gstId);
}
