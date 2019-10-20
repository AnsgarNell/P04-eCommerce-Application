package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void list_all_items() throws Exception {
        Item item = getItem();
        when(itemRepository.findAll()).thenReturn(Collections.singletonList(item));

        final ResponseEntity<List<Item>> responseEntity = itemController.getItems();

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

        Item responseItem = responseEntity.getBody().get(0);
        assertNotNull(responseItem);
        assertEquals("Test item description", responseItem.getDescription());
        assertEquals((Long) 0L, responseItem.getId());
        assertEquals("Test Item Name", responseItem.getName());
        assertEquals(new BigDecimal(2.99), responseItem.getPrice());
    }

    private Item getItem() {
        Item item = new Item();
        item.setDescription("Test item description");
        item.setId(0L);
        item.setName("Test Item Name");
        item.setPrice(new BigDecimal(2.99));
        return item;
    }
}
