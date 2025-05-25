package com.skumar.user_service.service;

import com.skumar.user_service.dto.UserDto;
import com.skumar.user_service.entity.Authority;
import com.skumar.user_service.entity.Role;
import com.skumar.user_service.entity.User;
import com.skumar.user_service.exception.UnauthorizedOperationException;
import com.skumar.user_service.exception.UserNotFoundException;
import com.skumar.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .roles(Set.of(Role.USER))
                .authorities(Role.USER.getAuthorities())
                .enabled(true)
                .accountNonLocked(true)
                .build();

        testUserDto = new UserDto();
        testUserDto.setId(1L);
        testUserDto.setName("Test User");
        testUserDto.setEmail("test@example.com");
        testUserDto.setPassword("password123");
        testUserDto.setRoles(Set.of(Role.USER));
        testUserDto.setAuthorities(Role.USER.getAuthorities());
    }

    @Test
    void createUser_Success() {
        when(modelMapper.map(any(UserDto.class), eq(User.class))).thenReturn(testUser);
        when(modelMapper.map(any(User.class), eq(UserDto.class))).thenReturn(testUserDto);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserDto result = userService.createUser(testUserDto);

        assertNotNull(result);
        assertEquals(testUserDto.getEmail(), result.getEmail());
        assertEquals(Set.of(Role.USER), result.getRoles());
        assertTrue(result.getAuthorities().containsAll(Role.USER.getAuthorities()));
    }

    @Test
    void getUserByEmail_Success() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(modelMapper.map(testUser, UserDto.class)).thenReturn(testUserDto);

        UserDto result = userService.getUserByEmail(testUser.getEmail());

        assertNotNull(result);
        assertEquals(testUserDto.getEmail(), result.getEmail());
    }

    @Test
    void getUserByEmail_NotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> 
            userService.getUserByEmail("nonexistent@example.com")
        );
    }

    @Test
    void changeUserRoles_Success() {
        Set<Role> newRoles = Set.of(Role.ADMIN);
        User updatedUser = User.builder()
                .id(1L)
                .email(testUser.getEmail())
                .roles(newRoles)
                .authorities(Role.ADMIN.getAuthorities())
                .build();

        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(modelMapper.map(updatedUser, UserDto.class)).thenReturn(testUserDto);

        UserDto result = userService.changeUserRoles(testUser.getEmail(), newRoles);

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void toggleUserAccount_Success() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(modelMapper.map(testUser, UserDto.class)).thenReturn(testUserDto);

        UserDto result = userService.toggleUserAccount(testUser.getEmail(), false);

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void changePassword_InvalidOldPassword() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

        assertThrows(UnauthorizedOperationException.class, () ->
            userService.changePassword(testUser.getEmail(), "wrongPassword", "newPassword")
        );
    }

    @Test
    void getUsersByRole_Success() {
        when(userRepository.findByRole(Role.USER)).thenReturn(List.of(testUser));
        when(modelMapper.map(testUser, UserDto.class)).thenReturn(testUserDto);

        List<UserDto> results = userService.getUsersByRole(Role.USER);

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(testUserDto.getEmail(), results.get(0).getEmail());
    }

    @Test
    void searchUsers_Success() {
        String keyword = "test";
        when(userRepository.searchUsers(keyword)).thenReturn(List.of(testUser));
        when(modelMapper.map(testUser, UserDto.class)).thenReturn(testUserDto);

        List<UserDto> results = userService.searchUsers(keyword);

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(testUserDto.getEmail(), results.get(0).getEmail());
    }

    @Test
    void getInactiveUsers_Success() {
        testUser.setEnabled(false);
        when(userRepository.findByEnabled(false)).thenReturn(List.of(testUser));
        when(modelMapper.map(testUser, UserDto.class)).thenReturn(testUserDto);

        List<UserDto> results = userService.getInactiveUsers();

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(testUser);

        assertDoesNotThrow(() -> userService.deleteUser(testUser.getEmail()));
        verify(userRepository).delete(testUser);
    }
}