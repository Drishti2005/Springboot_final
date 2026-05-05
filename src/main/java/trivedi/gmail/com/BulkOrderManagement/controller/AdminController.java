package trivedi.gmail.com.BulkOrderManagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import trivedi.gmail.com.BulkOrderManagement.entity.*;
import trivedi.gmail.com.BulkOrderManagement.repository.UserRepository;
import trivedi.gmail.com.BulkOrderManagement.service.AuthService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Admin-only operations")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @Data
    static class UserDto {
        Long id; String username; String email; String gstId; String role; boolean approved;
        static UserDto from(User u) {
            UserDto d = new UserDto();
            d.id = u.getId(); d.username = u.getUsername(); d.email = u.getEmail();
            d.gstId = u.getGstId(); d.role = u.getRole().name(); d.approved = u.isApproved();
            return d;
        }
    }

    @GetMapping("/pending-retailers")
    @Operation(summary = "List all Retailers pending approval")
    public ResponseEntity<List<UserDto>> pendingRetailers() {
        return ResponseEntity.ok(
            userRepository.findByRoleAndIsApproved(Role.RETAILER, false)
                .stream().map(UserDto::from).collect(Collectors.toList())
        );
    }

    @PatchMapping("/approve/{userId}")
    @Operation(summary = "Approve a Retailer account")
    public ResponseEntity<String> approveRetailer(@PathVariable Long userId) {
        authService.approveRetailer(userId);
        return ResponseEntity.ok("Retailer " + userId + " approved successfully.");
    }
}
