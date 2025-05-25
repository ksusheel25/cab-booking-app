package com.skumar.user_service.service;

import com.skumar.user_service.dto.UserDto;
import com.skumar.user_service.entity.Role;

import java.util.List;
import java.util.Set;

public interface UserService {
    UserDto createUser(UserDto userDto);
    UserDto getUserByEmail(String email);
    UserDto updateUser(String email, UserDto userDto);
    void deleteUser(String email);
    List<UserDto> getAllUsers();
    UserDto changeUserRoles(String email, Set<Role> roles);
    UserDto toggleUserAccount(String email, boolean enabled);
    UserDto lockUnlockUserAccount(String email, boolean locked);
    List<UserDto> getUsersByRole(Role role);
    UserDto updateProfile(String email, UserDto userDto);
    UserDto changePassword(String email, String oldPassword, String newPassword);
    List<UserDto> searchUsers(String keyword);
    List<UserDto> getRecentlyCreatedUsers(int limit);
    List<UserDto> getInactiveUsers();
    boolean existsByEmail(String email);

    // New methods for email verification and password reset
    void sendVerificationEmail(String email);
    boolean verifyEmail(String token);
    void initiatePasswordReset(String email);
    boolean validatePasswordResetToken(String token);
    void resetPassword(String token, String newPassword);
    void handleFailedLogin(String email);
    void handleSuccessfulLogin(String email);
}
