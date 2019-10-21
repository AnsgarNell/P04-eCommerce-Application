package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;

    private CartRepository cartRepository = mock(CartRepository.class);

    private UserRepository userRepository = mock(UserRepository.class);

    private ItemRepository itemRepository = mock(ItemRepository.class);

    private User user;
    private Item item;

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
        user = UserControllerTest.getUser();
        item = ItemControllerTest.getItem();
    }

    @Test
    public void add_to_cart_happy_path() throws Exception {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(itemRepository.findById(0L)).thenReturn(java.util.Optional.of(item));

        user.setCart(new Cart());
        user.getCart().setUser(user);

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(user.getUsername());
        modifyCartRequest.setItemId(item.getId());
        modifyCartRequest.setQuantity(3);

        final ResponseEntity<Cart> responseEntity = cartController.addTocart(modifyCartRequest);

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

        Cart responseCart = responseEntity.getBody();
        assertNotNull(responseCart);
        assertEquals(3, responseCart.getItems().size());
        BigDecimal totalPrice = item.getPrice().multiply(new BigDecimal(3));
        assertEquals(totalPrice, responseCart.getTotal());
        assertEquals(user, responseCart.getUser());
    }

    @Test
    public void add_to_cart_unexisting_user() throws Exception {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(user.getUsername());

        final ResponseEntity<Cart> responseEntity = cartController.addTocart(modifyCartRequest);

        assertNotNull(responseEntity);
        assertEquals(404, responseEntity.getStatusCodeValue());
    }

    @Test
    public void add_to_cart_unexisting_item() throws Exception {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(user.getUsername());
        modifyCartRequest.setItemId(item.getId());

        final ResponseEntity<Cart> responseEntity = cartController.addTocart(modifyCartRequest);

        assertNotNull(responseEntity);
        assertEquals(404, responseEntity.getStatusCodeValue());
    }

    @Test
    public void remove_from_cart_happy_path() throws Exception {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(itemRepository.findById(0L)).thenReturn(java.util.Optional.of(item));

        user.setCart(new Cart());
        user.getCart().setUser(user);
        user.getCart().addItem(item);
        user.getCart().addItem(item);

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(user.getUsername());
        modifyCartRequest.setItemId(item.getId());
        modifyCartRequest.setQuantity(1);

        final ResponseEntity<Cart> responseEntity = cartController.removeFromcart(modifyCartRequest);

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

        Cart responseCart = responseEntity.getBody();
        assertNotNull(responseCart);
        assertEquals(1, responseCart.getItems().size());
        assertEquals(item.getPrice(), responseCart.getTotal());
        assertEquals(user, responseCart.getUser());
    }

    @Test
    public void remove_from_cart_unexisting_user() throws Exception {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(user.getUsername());

        final ResponseEntity<Cart> responseEntity = cartController.removeFromcart(modifyCartRequest);

        assertNotNull(responseEntity);
        assertEquals(404, responseEntity.getStatusCodeValue());
    }

    @Test
    public void remove_from_cart_unexisting_item() throws Exception {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(user.getUsername());
        modifyCartRequest.setItemId(item.getId());

        final ResponseEntity<Cart> responseEntity = cartController.removeFromcart(modifyCartRequest);

        assertNotNull(responseEntity);
        assertEquals(404, responseEntity.getStatusCodeValue());
    }
}
