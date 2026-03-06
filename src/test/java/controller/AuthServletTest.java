package controller;

import dao.IUserDao;
import exception.AuthException;
import model.userModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.AuthService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    IUserDao userDao;

    AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userDao);
    }

    // ── authenticate ─────────────────────────────────────────────────────────

    @Test
    void authenticate_throwsAuthException_whenUsernameIsBlank() {
        AuthException ex = assertThrows(AuthException.class,
                () -> authService.authenticate("  ", "password"));
        assertEquals("Username and password are required", ex.getMessage());
        verifyNoInteractions(userDao);
    }

    @Test
    void authenticate_throwsAuthException_whenPasswordIsBlank() {
        AuthException ex = assertThrows(AuthException.class,
                () -> authService.authenticate("user1", ""));
        assertEquals("Username and password are required", ex.getMessage());
        verifyNoInteractions(userDao);
    }

    @Test
    void authenticate_throwsAuthException_whenUsernameIsNull() {
        AuthException ex = assertThrows(AuthException.class,
                () -> authService.authenticate(null, "password"));
        assertEquals("Username and password are required", ex.getMessage());
        verifyNoInteractions(userDao);
    }

    @Test
    void authenticate_throwsAuthException_whenUserNotFound() {
        when(userDao.findByUsername("ghost")).thenReturn(null);
        AuthException ex = assertThrows(AuthException.class,
                () -> authService.authenticate("ghost", "password"));
        assertEquals("Invalid credentials", ex.getMessage());
    }

    @Test
    void authenticate_throwsAuthException_whenPasswordDoesNotMatch() {
        String hash = BCrypt.hashpw("correctPassword", BCrypt.gensalt());
        userModel user = new userModel(1L, "user1", hash);
        when(userDao.findByUsername("user1")).thenReturn(user);

        AuthException ex = assertThrows(AuthException.class,
                () -> authService.authenticate("user1", "wrongPassword"));
        assertEquals("Invalid credentials", ex.getMessage());
    }

    @Test
    void authenticate_returnsUser_whenCredentialsAreValid() {
        String hash = BCrypt.hashpw("correctPassword", BCrypt.gensalt());
        userModel user = new userModel(1L, "user1", hash);
        when(userDao.findByUsername("user1")).thenReturn(user);

        userModel result = authService.authenticate("user1", "correctPassword");

        assertNotNull(result);
        assertEquals("user1", result.getUsername());
        assertEquals(1L, result.getId());
    }

    @Test
    void authenticate_trimsUsername_beforeLookup() {
        String hash = BCrypt.hashpw("pass", BCrypt.gensalt());
        userModel user = new userModel(2L, "admin", hash);
        when(userDao.findByUsername("admin")).thenReturn(user);

        userModel result = authService.authenticate("  admin  ", "pass");
        assertNotNull(result);
        verify(userDao).findByUsername("admin");
    }
}