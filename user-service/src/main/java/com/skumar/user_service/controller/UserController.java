package com.skumar.user_service.controller;

import com.skumar.user_service.dto.*;
import com.skumar.user_service.entity.Role;
import com.skumar.user_service.exception.UserServiceException;
import com.skumar.user_service.service.JwtService;
import com.skumar.user_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    // Public endpoints
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserDto userDto) {
        UserDto createdUser = userService.createUser(userDto);
        return ResponseEntity.ok(mapToUserResponse(createdUser));
    }

    @PostMapping("/register/admin")
    @PreAuthorize("hasAuthority('ADMIN_CREATE')")
    public ResponseEntity<UserResponse> registerAdminUser(@Valid @RequestBody UserDto userDto) {
        if (userDto.getRoles() == null || userDto.getRoles().isEmpty()) {
            userDto.setRoles(Set.of(Role.ADMIN));
        }
        UserDto createdUser = userService.createUser(userDto);
        return ResponseEntity.ok(mapToUserResponse(createdUser));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            UserDto user = userService.getUserByEmail(loginRequest.getEmail());
            
            if (!user.isEmailVerified()) {
                throw new UserServiceException("Email not verified. Please verify your email first.");
            }

            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                userService.handleFailedLogin(loginRequest.getEmail());
                throw new UserServiceException("Invalid credentials");
            }

            if (!user.isEnabled()) {
                throw new UserServiceException("Account is disabled");
            }

            if (!user.isAccountNonLocked()) {
                throw new UserServiceException("Account is locked");
            }

            userService.handleSuccessfulLogin(loginRequest.getEmail());
            String token = jwtService.generateToken(loginRequest.getEmail());
            
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("email", user.getEmail());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new UserServiceException("Login failed: " + e.getMessage());
        }
    }

    @GetMapping("/verify-email")
    public ResponseEntity<Map<String, String>> verifyEmail(@RequestParam String token) {
        boolean verified = userService.verifyEmail(token);
        Map<String, String> response = new HashMap<>();
        response.put("message", verified ? "Email verified successfully" : "Email verification failed");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<Map<String, String>> resendVerification(@RequestParam String email) {
        userService.sendVerificationEmail(email);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Verification email sent successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestParam String email) {
        userService.initiatePasswordReset(email);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password reset email sent successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @RequestParam String token,
            @Valid @RequestBody PasswordUpdateRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new UserServiceException("Passwords don't match");
        }
        
        userService.resetPassword(token, request.getNewPassword());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password reset successfully");
        return ResponseEntity.ok(response);
    }

    // User endpoints
    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<UserResponse> getUserProfile(@RequestHeader("X-User-Email") String email) {
        UserDto user = userService.getUserByEmail(email);
        return ResponseEntity.ok(mapToUserResponse(user));
    }

    @PutMapping("/profile")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public ResponseEntity<UserResponse> updateProfile(
            @RequestHeader("X-User-Email") String email,
            @Valid @RequestBody UserDto userDto) {
        UserDto updatedUser = userService.updateProfile(email, userDto);
        return ResponseEntity.ok(mapToUserResponse(updatedUser));
    }

    @PutMapping("/change-password")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public ResponseEntity<UserResponse> changePassword(
            @RequestHeader("X-User-Email") String email,
            @Valid @RequestBody PasswordUpdateRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new UserServiceException("New password and confirm password do not match");
        }
        UserDto updatedUser = userService.changePassword(email, request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok(mapToUserResponse(updatedUser));
    }

    // Admin endpoints
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN_READ')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('ADMIN_READ')")
    public ResponseEntity<List<UserResponse>> searchUsers(@RequestParam String keyword) {
        List<UserResponse> users = userService.searchUsers(keyword).stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/role/{role}")
    @PreAuthorize("hasAuthority('ADMIN_READ')")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@PathVariable Role role) {
        List<UserResponse> users = userService.getUsersByRole(role).stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/inactive")
    @PreAuthorize("hasAuthority('ADMIN_READ')")
    public ResponseEntity<List<UserResponse>> getInactiveUsers() {
        List<UserResponse> users = userService.getInactiveUsers().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/recent")
    @PreAuthorize("hasAuthority('ADMIN_READ')")
    public ResponseEntity<List<UserResponse>> getRecentUsers(@RequestParam(defaultValue = "10") int limit) {
        List<UserResponse> users = userService.getRecentlyCreatedUsers(limit).stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{email}/roles")
    @PreAuthorize("hasAuthority('ADMIN_UPDATE')")
    public ResponseEntity<UserResponse> updateUserRoles(
            @PathVariable String email,
            @RequestBody Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            throw new UserServiceException("At least one role must be specified");
        }
        UserDto updatedUser = userService.changeUserRoles(email, roles);
        return ResponseEntity.ok(mapToUserResponse(updatedUser));
    }

    @PutMapping("/{email}/toggle-status")
    @PreAuthorize("hasAuthority('ADMIN_UPDATE')")
    public ResponseEntity<UserResponse> toggleUserStatus(
            @PathVariable String email,
            @RequestParam boolean enabled) {
        UserDto updatedUser = userService.toggleUserAccount(email, enabled);
        return ResponseEntity.ok(mapToUserResponse(updatedUser));
    }

    @PutMapping("/{email}/toggle-lock")
    @PreAuthorize("hasAuthority('ADMIN_UPDATE')")
    public ResponseEntity<UserResponse> toggleUserLock(
            @PathVariable String email,
            @RequestParam boolean locked) {
        UserDto updatedUser = userService.lockUnlockUserAccount(email, locked);
        return ResponseEntity.ok(mapToUserResponse(updatedUser));
    }

    @DeleteMapping("/{email}")
    @PreAuthorize("hasAuthority('ADMIN_DELETE')")
    public ResponseEntity<Void> deleteUser(@PathVariable String email) {
        userService.deleteUser(email);
        return ResponseEntity.noContent().build();
    }

    private UserResponse mapToUserResponse(UserDto userDto) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(userDto.getId());
        userResponse.setName(userDto.getName());
        userResponse.setEmail(userDto.getEmail());
        userResponse.setPhoneNumber(userDto.getPhoneNumber());
        userResponse.setRoles(userDto.getRoles());
        userResponse.setEnabled(userDto.isEnabled());
        userResponse.setEmailVerified(userDto.isEmailVerified());
        userResponse.setAccountNonLocked(userDto.isAccountNonLocked());
        userResponse.setCreatedAt(userDto.getCreatedAt());
        userResponse.setLastModifiedAt(userDto.getLastModifiedAt());
        return userResponse;
    }
}

