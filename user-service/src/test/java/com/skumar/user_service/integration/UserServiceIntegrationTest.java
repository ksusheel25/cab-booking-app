package com.skumar.user_service.integration;

import com.skumar.user_service.dto.UserDto;
import com.skumar.user_service.entity.Role;
import com.skumar.user_service.repository.UserRepository;
import com.skumar.user_service.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        testUserDto = new UserDto();
        testUserDto.setName("Integration Test User");
        testUserDto.setEmail("integration.test@example.com");
        testUserDto.setPassword("password123");
        testUserDto.setPhoneNumber("+1234567890");
    }

    @AfterEach
    void cleanup() {
        userRepository.deleteAll();
    }

    @Test
    void createUser_ShouldAssignDefaultRole() {
        UserDto createdUser = userService.createUser(testUserDto);
        
        assertNotNull(createdUser);
        assertEquals(testUserDto.getEmail(), createdUser.getEmail());
        assertTrue(createdUser.getRoles().contains(Role.USER));
        assertFalse(createdUser.getRoles().contains(Role.ADMIN));
    }

    @Test
    void changeUserRoles_ShouldUpdateAuthorities() {
        UserDto createdUser = userService.createUser(testUserDto);
        UserDto updatedUser = userService.changeUserRoles(createdUser.getEmail(), Set.of(Role.ADMIN));
        
        assertTrue(updatedUser.getRoles().contains(Role.ADMIN));
        assertTrue(updatedUser.getAuthorities().containsAll(Role.ADMIN.getAuthorities()));
    }

    @Test
    void toggleUserAccount_ShouldUpdateEnabledStatus() {
        UserDto createdUser = userService.createUser(testUserDto);
        UserDto disabledUser = userService.toggleUserAccount(createdUser.getEmail(), false);
        
        assertFalse(disabledUser.isEnabled());
        
        UserDto reenabledUser = userService.toggleUserAccount(createdUser.getEmail(), true);
        assertTrue(reenabledUser.isEnabled());
    }

    @Test
    void searchUsers_ShouldFindByNameOrEmail() {
        userService.createUser(testUserDto);
        
        var resultsForName = userService.searchUsers("Integration");
        assertFalse(resultsForName.isEmpty());
        assertEquals(1, resultsForName.size());
        
        var resultsForEmail = userService.searchUsers("integration.test");
        assertFalse(resultsForEmail.isEmpty());
        assertEquals(1, resultsForEmail.size());
    }

    @Test
    void getUsersByRole_ShouldReturnCorrectUsers() {
        UserDto user1 = userService.createUser(testUserDto);
        
        UserDto adminDto = new UserDto();
        adminDto.setName("Admin User");
        adminDto.setEmail("admin@example.com");
        adminDto.setPassword("admin123");
        UserDto user2 = userService.createUser(adminDto);
        userService.changeUserRoles(user2.getEmail(), Set.of(Role.ADMIN));
        
        var adminUsers = userService.getUsersByRole(Role.ADMIN);
        var regularUsers = userService.getUsersByRole(Role.USER);
        
        assertEquals(1, adminUsers.size());
        assertEquals(1, regularUsers.size());
        assertEquals("admin@example.com", adminUsers.get(0).getEmail());
        assertEquals("integration.test@example.com", regularUsers.get(0).getEmail());
    }
}