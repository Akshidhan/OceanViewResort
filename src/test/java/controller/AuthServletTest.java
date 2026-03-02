package controller;

import dao.UserDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.AuthService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    UserDao userDao;

    @InjectMocks
    AuthService authService;

//    @Test
//    void login_returnsFalse_forBlankUsername() {
//        assertFalse(authService.login(" ", "pw"));
//        verifyNoInteractions(userDao);
//    }
//
//    @Test
//    void login_returnsTrue_whenDaoValidates() {
//        when(userDao.isValidCredentials("admin", "123")).thenReturn(true);
//
//        boolean ok = authService.login("admin", "123");
//
//        assertTrue(ok);
//        verify(userDao).isValidCredentials("admin", "123");
//    }
}