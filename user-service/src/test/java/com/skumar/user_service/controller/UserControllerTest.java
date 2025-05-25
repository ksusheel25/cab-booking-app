package com.skumar.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skumar.user_service.dto.PasswordUpdateRequest;
import com.skumar.user_service.dto.UserDto;
import com.skumar.user_service.entity.Role;
import com.skumar.user_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserDto testUser;
    private PasswordUpdateRequest passwordRequest;

    @BeforeEach
    void setUp() {
        testUser = new UserDto();
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setRoles(Set.of(Role.USER));

        passwordRequest = new PasswordUpdateRequest();
        passwordRequest.setOldPassword("oldPassword");
        passwordRequest.setNewPassword("newPassword");
        passwordRequest.setConfirmPassword("newPassword");
    }

    @Test
    void testRegisterUser() throws Exception {
        when(userService.createUser(any(UserDto.class))).thenReturn(testUser);

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(testUser.getEmail()));
    }

    @Test
    @WithMockUser(authorities = "ADMIN_CREATE")
    void testRegisterAdminUser() throws Exception {
        testUser.setRoles(Set.of(Role.ADMIN));
        when(userService.createUser(any(UserDto.class))).thenReturn(testUser);

        mockMvc.perform(post("/api/users/register/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(testUser.getEmail()));
    }

    @Test
    @WithMockUser(authorities = "USER_READ")
    void testGetUserProfile() throws Exception {
        when(userService.getUserByEmail(eq("test@example.com"))).thenReturn(testUser);

        mockMvc.perform(get("/api/users/profile")
                .header("X-User-Email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(testUser.getEmail()));
    }

    @Test
    @WithMockUser(authorities = "USER_UPDATE")
    void testUpdateProfile() throws Exception {
        when(userService.updateProfile(eq("test@example.com"), any(UserDto.class)))
                .thenReturn(testUser);

        mockMvc.perform(put("/api/users/profile")
                .header("X-User-Email", "test@example.com")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(testUser.getEmail()));
    }

    @Test
    @WithMockUser(authorities = "USER_UPDATE")
    void testChangePassword() throws Exception {
        when(userService.changePassword(eq("test@example.com"), 
                eq(passwordRequest.getOldPassword()), 
                eq(passwordRequest.getNewPassword())))
                .thenReturn(testUser);

        mockMvc.perform(put("/api/users/change-password")
                .header("X-User-Email", "test@example.com")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "ADMIN_UPDATE")
    void testToggleUserStatus() throws Exception {
        when(userService.toggleUserAccount(eq("test@example.com"), eq(false)))
                .thenReturn(testUser);

        mockMvc.perform(put("/api/users/test@example.com/toggle-status")
                .param("enabled", "false"))
                .andExpect(status().isOk());
    }

    @Test
    void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "USER_READ")
    void testInsufficientPrivileges() throws Exception {
        mockMvc.perform(get("/api/users")) // requires ADMIN_READ
                .andExpect(status().isForbidden());
    }
}