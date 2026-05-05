package trivedi.gmail.com.BulkOrderManagement.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import trivedi.gmail.com.BulkOrderManagement.entity.Role;

@Data
public class RegisterRequest {

    @NotBlank
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotNull
    private Role role;

    /** Required for Retailers and Wholesalers. */
    @NotBlank
    private String gstId;
}
