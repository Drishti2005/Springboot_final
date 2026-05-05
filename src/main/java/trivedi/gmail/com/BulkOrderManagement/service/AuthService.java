package trivedi.gmail.com.BulkOrderManagement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trivedi.gmail.com.BulkOrderManagement.dto.*;
import trivedi.gmail.com.BulkOrderManagement.entity.*;
import trivedi.gmail.com.BulkOrderManagement.exception.ResourceNotFoundException;
import trivedi.gmail.com.BulkOrderManagement.repository.*;
import trivedi.gmail.com.BulkOrderManagement.security.JwtTokenProvider;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RetailerProfileRepository retailerProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername()))
            throw new IllegalArgumentException("Username already taken: " + request.getUsername());
        if (userRepository.existsByEmail(request.getEmail()))
            throw new IllegalArgumentException("Email already registered: " + request.getEmail());

        User user = User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(request.getRole())
            .gstId(request.getGstId())
            .isApproved(request.getRole() == Role.ADMIN || request.getRole() == Role.WHOLESALER)
            .build();

        user = userRepository.save(user);

        if (user.getRole() == Role.RETAILER) {
            RetailerProfile profile = new RetailerProfile();
            profile.setUser(user);
            profile.setCompanyName(request.getUsername() + " Enterprises");
            profile.setGstId(request.getGstId());
            profile.setCreditLimit(new BigDecimal("50000.00"));
            profile.setOutstandingBalance(BigDecimal.ZERO);
            retailerProfileRepository.save(profile);
        }

        return user;
    }

    public AuthResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        String token = tokenProvider.generateToken(auth);
        User user = (User) auth.getPrincipal();
        return new AuthResponse(token, user.getUsername(), user.getRole().name(), user.isApproved());
    }

    @Transactional
    public void approveRetailer(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        user.setApproved(true);
        userRepository.save(user);
    }
}
