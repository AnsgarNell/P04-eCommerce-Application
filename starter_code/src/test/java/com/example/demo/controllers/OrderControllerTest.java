package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;

    private OrderRepository orderRepository = mock(OrderRepository.class);

    private UserRepository userRepository = mock(UserRepository.class);

    private User user;
    private Item item;

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
        user = UserControllerTest.getUser();
        item = ItemControllerTest.getItem();
    }

    @Test
    public void submit_order_happy_path() throws Exception {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        user.setCart(new Cart());
        user.getCart().setUser(user);
        user.getCart().addItem(item);
        user.getCart().addItem(item);

        final ResponseEntity<UserOrder> responseEntity = orderController.submit(user.getUsername());

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

        UserOrder responseUserOrder = responseEntity.getBody();
        assertNotNull(responseUserOrder);
        assertEquals(2, responseUserOrder.getItems().size());
        BigDecimal totalPrice = item.getPrice().multiply(new BigDecimal(2));
        assertEquals(totalPrice, responseUserOrder.getTotal());
        assertEquals(user, responseUserOrder.getUser());
    }

    @Test
    public void submit_order_unexisting_user() throws Exception {
        final ResponseEntity<UserOrder> responseEntity = orderController.submit(user.getUsername());

        assertNotNull(responseEntity);
        assertEquals(404, responseEntity.getStatusCodeValue());
    }

    @Test
    public void get_order_for_user_happy_path() throws Exception {
        user.setCart(new Cart());
        user.getCart().setUser(user);
        user.getCart().addItem(item);
        user.getCart().addItem(item);
        UserOrder userOrder = UserOrder.createFromCart(user.getCart());

        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(Collections.singletonList(userOrder));

        final ResponseEntity<List<UserOrder>> responseEntity = orderController.getOrdersForUser(user.getUsername());

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

        UserOrder responseUserOrder = responseEntity.getBody().get(0);
        assertNotNull(responseUserOrder);
        assertEquals(2, responseUserOrder.getItems().size());
        BigDecimal totalPrice = item.getPrice().multiply(new BigDecimal(2));
        assertEquals(totalPrice, responseUserOrder.getTotal());
        assertEquals(user, responseUserOrder.getUser());
    }

    @Test
    public void get_order_for_user_unexisting_user() throws Exception {
        final ResponseEntity<List<UserOrder>> responseEntity = orderController.getOrdersForUser(user.getUsername());

        assertNotNull(responseEntity);
        assertEquals(404, responseEntity.getStatusCodeValue());
    }
}
