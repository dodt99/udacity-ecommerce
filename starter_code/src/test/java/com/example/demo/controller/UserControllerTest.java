package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;
    private final UserRepository userRepo = mock(UserRepository.class);
    private final CartRepository cartRepo = mock(CartRepository.class);
    private final BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepo);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);

        User user = new User();
        Cart cart = new Cart();
        user.setId(0);
        user.setUsername("username123");
        user.setPassword("password123");
        user.setCart(cart);
        when(userRepo.findByUsername("username123")).thenReturn(user);
        when(userRepo.findById(0L)).thenReturn(java.util.Optional.of(user));
        when(userRepo.findByUsername("404user")).thenReturn(null);

    }

    @Test
    public void createUser() {
        when(encoder.encode("password123")).thenReturn("thisIsHashed");
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("username123");
        r.setPassword("password123");
        r.setConfirmPassword("password123");
        ResponseEntity<User> response = userController.createUser(r);
        assertNotNull(response);
        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("username123", u.getUsername());
        assertEquals("thisIsHashed", u.getPassword());
    }

    @Test
    public void createUserFailPassword() {
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("123");
        r.setConfirmPassword("123");
        ResponseEntity<User> response = userController.createUser(r);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void createUserFailConfirmPassword() {
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("12345678");
        r.setConfirmPassword("12345678fail");
        ResponseEntity<User> response = userController.createUser(r);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void findByUserName() {
        ResponseEntity<User> response = userController.findByUserName("username123");
        assertNotNull(response);
        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("username123", u.getUsername());
    }

    @Test
    public void findByUserNameNotFound() {
        ResponseEntity<User> response = userController.findByUserName("404user");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void findById() {
        ResponseEntity<User> response = userController.findById(0L);
        assertNotNull(response);
        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("username123", u.getUsername());
    }

    @Test
    public void findByIdNotFound() {
        ResponseEntity<User> response = userController.findById(1L);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}
