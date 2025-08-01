package com.skumar.user_service.service;

import com.skumar.user_service.dto.UserDto;
import com.skumar.user_service.entity.Role;
import com.skumar.user_service.entity.User;
import com.skumar.user_service.entity.VerificationToken;
import com.skumar.user_service.exception.UnauthorizedOperationException;
import com.skumar.user_service.exception.UserNotFoundException;
import com.skumar.user_service.exception.UserServiceException;
import com.skumar.user_service.repository.UserRepository;
import com.skumar.user_service.repository.VerificationTokenRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                            VerificationTokenRepository tokenRepository,
                            ModelMapper modelMapper,
                            PasswordEncoder passwordEncoder,
                            EmailService emailService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public VerificationTokenRepository getTokenRepository() {
        return tokenRepository;
    }

    public ModelMapper getModelMapper() {
        return modelMapper;
    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public EmailService getEmailService() {
        return emailService;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new UserServiceException("Email already exists: " + userDto.getEmail());
        }

        User user = modelMapper.map(userDto, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Set.of(Role.USER));
        user.getAuthorities().addAll(Role.USER.getAuthorities());
        user.setEnabled(false);
        user.setEmailVerified(false);

        User savedUser = userRepository.save(user);
        sendVerificationEmail(savedUser.getEmail());
        
        return modelMapper.map(savedUser, UserDto.class);
    }

    @Override
    public void sendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        // Delete any existing verification tokens
        tokenRepository.deleteByUserIdAndTokenType(user.getId(), VerificationToken.TokenType.EMAIL_VERIFICATION);

        String token = UUID.randomUUID().toString();
        var verificationToken = VerificationToken.createEmailToken(user, token);
        tokenRepository.save(verificationToken);

        try {
            emailService.sendVerificationEmail(email, token);
        } catch (Exception e) {
            throw new UserServiceException("Failed to send verification email", e);
        }
    }

    @Override
    public boolean verifyEmail(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new UserServiceException("Invalid verification token"));

        if (verificationToken.isExpired()) {
            tokenRepository.delete(verificationToken);
            throw new UserServiceException("Token has expired");
        }

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        user.setEnabled(true);
        userRepository.save(user);
        tokenRepository.delete(verificationToken);

        return true;
    }

    @Override
    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        // Delete any existing reset tokens
        tokenRepository.deleteByUserIdAndTokenType(user.getId(), VerificationToken.TokenType.PASSWORD_RESET);

        String token = UUID.randomUUID().toString();
        var resetToken = VerificationToken.createPasswordResetToken(user, token);
        tokenRepository.save(resetToken);

        try {
            emailService.sendPasswordResetEmail(email, token);
        } catch (Exception e) {
            throw new UserServiceException("Failed to send password reset email", e);
        }
    }

    @Override
    public boolean validatePasswordResetToken(String token) {
        return tokenRepository.findByToken(token)
                .map(resetToken -> !resetToken.isExpired())
                .orElse(false);
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        VerificationToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new UserServiceException("Invalid reset token"));

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new UserServiceException("Reset token has expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.resetFailedLoginAttempts();
        userRepository.save(user);
        tokenRepository.delete(resetToken);
    }

    @Override
    public void handleFailedLogin(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            user.incrementFailedLoginAttempts();
            if (user.getFailedLoginAttempts() >= 5) {
                user.setAccountNonLocked(false);
            }
            userRepository.save(user);
        });
    }

    @Override
    public void handleSuccessfulLogin(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            user.resetFailedLoginAttempts();
            userRepository.save(user);
        });
    }

    @Override
    public UserDto changePassword(String email, String oldPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new UnauthorizedOperationException("Invalid old password");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        return modelMapper.map(userRepository.save(user), UserDto.class);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto updateUser(String email, UserDto userDto) {
        User existing = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        existing.setName(userDto.getName());
        existing.setPhoneNumber(userDto.getPhoneNumber());
        
        return modelMapper.map(userRepository.save(existing), UserDto.class);
    }

    @Override
    public void deleteUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        userRepository.delete(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserDto changeUserRoles(String email, Set<Role> roles) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        
        user.setRoles(roles);
        user.getAuthorities().clear();
        roles.forEach(role -> user.getAuthorities().addAll(role.getAuthorities()));
        
        return modelMapper.map(userRepository.save(user), UserDto.class);
    }

    @Override
    public UserDto toggleUserAccount(String email, boolean enabled) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        
        user.setEnabled(enabled);
        return modelMapper.map(userRepository.save(user), UserDto.class);
    }

    @Override
    public UserDto lockUnlockUserAccount(String email, boolean locked) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        
        user.setAccountNonLocked(!locked);
        return modelMapper.map(userRepository.save(user), UserDto.class);
    }

    @Override
    public List<UserDto> getUsersByRole(Role role) {
        return userRepository.findByRole(role).stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserDto updateProfile(String email, UserDto userDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        // Only allow updating certain fields for profile update
        user.setName(userDto.getName());
        user.setPhoneNumber(userDto.getPhoneNumber());
        
        return modelMapper.map(userRepository.save(user), UserDto.class);
    }

    @Override
    public List<UserDto> searchUsers(String keyword) {
        return userRepository.searchUsers(keyword).stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> getRecentlyCreatedUsers(int limit) {
        return userRepository.findByOrderByCreatedAtDesc().stream()
                .limit(limit)
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> getInactiveUsers() {
        return userRepository.findByEnabled(false).stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}

