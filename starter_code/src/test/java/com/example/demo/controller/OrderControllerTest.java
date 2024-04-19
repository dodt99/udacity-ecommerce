package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.OrderController;
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
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;
    private final OrderRepository orderRepo = mock(OrderRepository.class);
    private final UserRepository userRepo = mock(UserRepository.class);

    @Before
    public void setUp(){
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "orderRepository", orderRepo);
        TestUtils.injectObjects(orderController, "userRepository", userRepo);

        Item item = new Item();
        item.setId(1L);
        item.setName("Item 1");
        BigDecimal price = BigDecimal.valueOf(2.99);
        item.setPrice(price);
        item.setDescription("Item 1 Desc");
        List<Item> items = new ArrayList<>();
        items.add(item);

        User user = new User();
        Cart cart = new Cart();
        user.setId(0);
        user.setUsername("username123");
        user.setPassword("password123");
        cart.setId(0L);
        cart.setUser(user);
        cart.setItems(items);
        BigDecimal total = BigDecimal.valueOf(2.99);
        cart.setTotal(total);
        user.setCart(cart);
        when(userRepo.findByUsername("username123")).thenReturn(user);
        when(userRepo.findByUsername("username404")).thenReturn(null);
    }

    @Test
    public void submitOrder() {
        ResponseEntity<UserOrder> response = orderController.submit("username123");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        UserOrder order = response.getBody();
        assertNotNull(order);
        assertEquals(1, order.getItems().size());
    }


    @Test
    public void submitOrderFailUserNotFound() {
        ResponseEntity<UserOrder> response = orderController.submit("404user");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void getOrdersForUser() {
        ResponseEntity<List<UserOrder>> ordersForUser = orderController.getOrdersForUser("username123");
        assertNotNull(ordersForUser);
        assertEquals(200, ordersForUser.getStatusCodeValue());
        List<UserOrder> orders = ordersForUser.getBody();
        assertNotNull(orders);
    }

    @Test
    public void getOrdersForUserFailNotFound() {
        ResponseEntity<List<UserOrder>> ordersForUser = orderController.getOrdersForUser("404user");
        assertNotNull(ordersForUser);
        assertEquals(404, ordersForUser.getStatusCodeValue());

    }
}
