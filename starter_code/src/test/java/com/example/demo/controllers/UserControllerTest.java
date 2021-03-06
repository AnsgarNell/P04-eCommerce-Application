package com.example.demo.controllers;

import com.example.demo.TestUtils;
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

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
    }

    @Test
    public void create_user_happy_path() throws Exception{
        when(bCryptPasswordEncoder.encode("testPassword")).thenReturn("thisIsHashed");
        CreateUserRequest createUserRequest = new CreateUserRequest();

        createUserRequest.setUsername("test");
        createUserRequest.setPassword("testPassword");
        createUserRequest.setConfirmPassword("testPassword");

        final ResponseEntity<User> responseEntity = userController.createUser(createUserRequest);

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

        User user = responseEntity.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("thisIsHashed", user.getPassword());
    }

    @Test
    public void create_user_short_password() throws Exception{
        when(bCryptPasswordEncoder.encode("test")).thenReturn("thisIsHashed");
        CreateUserRequest createUserRequest = new CreateUserRequest();

        createUserRequest.setUsername("test");
        createUserRequest.setPassword("test");
        createUserRequest.setConfirmPassword("test");

        final ResponseEntity<User> responseEntity = userController.createUser(createUserRequest);

        assertNotNull(responseEntity);
        assertEquals(400, responseEntity.getStatusCodeValue());
    }

    @Test
    public void create_user_not_confirmed_password() throws Exception{
        when(bCryptPasswordEncoder.encode("test")).thenReturn("thisIsHashed");
        CreateUserRequest createUserRequest = new CreateUserRequest();

        createUserRequest.setUsername("test");
        createUserRequest.setPassword("testPassword");
        createUserRequest.setConfirmPassword("test");

        final ResponseEntity<User> responseEntity = userController.createUser(createUserRequest);

        assertNotNull(responseEntity);
        assertEquals(400, responseEntity.getStatusCodeValue());
    }

    @Test
    public void verify_find_user_by_id() throws Exception{
        User user = getUser();
        when(userRepository.findById(0L)).thenReturn(java.util.Optional.of(user));

        final ResponseEntity<User> responseEntity = userController.findById(0L);

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

        User responseUser = responseEntity.getBody();
        assertNotNull(responseUser);
        assertEquals(0, responseUser.getId());
        assertEquals("test", responseUser.getUsername());
        assertEquals("testPassword", responseUser.getPassword());
    }

    @Test
    public void verify_find_user_by_name() throws Exception{
        User user = getUser();
        when(userRepository.findByUsername("test")).thenReturn(user);

        final ResponseEntity<User> responseEntity = userController.findByUserName("test");

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

        User responseUser = responseEntity.getBody();
        assertNotNull(responseUser);
        assertEquals(0, responseUser.getId());
        assertEquals("test", responseUser.getUsername());
        assertEquals("testPassword", responseUser.getPassword());
    }

    public static User getUser() {
        User user = new User();
        user.setId(0);
        user.setUsername("test");
        user.setPassword("testPassword");
        user.setSalt(1L);
        return user;
    }
}
