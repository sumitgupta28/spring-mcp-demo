package com.example.springmcpdemo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShoppingCartServiceTest {

    private ShoppingCartService shoppingCartService;

    @BeforeEach
    void setUp() {
        shoppingCartService = new ShoppingCartService();
    }

    @Test
    @DisplayName("Add new item to empty cart")
    void addItem_newItem_success() {
        String result = shoppingCartService.addItem("Apple", 5);

        assertTrue(result.contains("Added Apple"));
        assertTrue(result.contains("quantity: 5"));
    }

    @Test
    @DisplayName("Add quantity to existing item")
    void addItem_existingItem_addsQuantity() {
        shoppingCartService.addItem("Apple", 5);
        String result = shoppingCartService.addItem("Apple", 3);

        assertTrue(result.contains("Total quantity: 8"));
    }

    @Test
    @DisplayName("Add item with null name returns error")
    void addItem_nullName_returnsError() {
        String result = shoppingCartService.addItem(null, 5);

        assertEquals("Error: Item name cannot be empty", result);
    }

    @Test
    @DisplayName("Add item with blank name returns error")
    void addItem_blankName_returnsError() {
        String result = shoppingCartService.addItem("  ", 5);

        assertEquals("Error: Item name cannot be empty", result);
    }

    @Test
    @DisplayName("Add item with zero quantity returns error")
    void addItem_zeroQuantity_returnsError() {
        String result = shoppingCartService.addItem("Apple", 0);

        assertEquals("Error: Quantity must be greater than 0", result);
    }

    @Test
    @DisplayName("Add item with negative quantity returns error")
    void addItem_negativeQuantity_returnsError() {
        String result = shoppingCartService.addItem("Apple", -1);

        assertEquals("Error: Quantity must be greater than 0", result);
    }

    @Test
    @DisplayName("Remove item completely from cart")
    void removeItem_removeAll_success() {
        shoppingCartService.addItem("Apple", 5);
        String result = shoppingCartService.removeItem("Apple", 0);

        assertTrue(result.contains("Removed Apple completely"));
    }

    @Test
    @DisplayName("Remove partial quantity from item")
    void removeItem_partialQuantity_success() {
        shoppingCartService.addItem("Apple", 5);
        String result = shoppingCartService.removeItem("Apple", 2);

        assertTrue(result.contains("Reduced Apple by 2"));
        assertTrue(result.contains("Remaining quantity: 3"));
    }

    @Test
    @DisplayName("Remove more than available removes completely")
    void removeItem_moreThanAvailable_removesCompletely() {
        shoppingCartService.addItem("Apple", 3);
        String result = shoppingCartService.removeItem("Apple", 10);

        assertTrue(result.contains("Removed Apple completely"));
    }

    @Test
    @DisplayName("Remove non-existent item returns error")
    void removeItem_nonExistent_returnsError() {
        String result = shoppingCartService.removeItem("Apple", 1);

        assertEquals("Error: Apple not found in cart", result);
    }

    @Test
    @DisplayName("Remove item with null name returns error")
    void removeItem_nullName_returnsError() {
        String result = shoppingCartService.removeItem(null, 1);

        assertEquals("Error: Item name cannot be empty", result);
    }

    @Test
    @DisplayName("Get cart when empty")
    void getCart_empty_returnsEmptyMessage() {
        String result = shoppingCartService.getCart();

        assertEquals("Shopping cart is empty", result);
    }

    @Test
    @DisplayName("Get cart with items")
    void getCart_withItems_success() {
        shoppingCartService.addItem("Apple", 5);
        shoppingCartService.addItem("Banana", 3);

        String result = shoppingCartService.getCart();

        assertTrue(result.contains("Shopping Cart (2 items)"));
        assertTrue(result.contains("Apple: 5"));
        assertTrue(result.contains("Banana: 3"));
        assertTrue(result.contains("Total items: 8"));
    }

    @Test
    @DisplayName("Clear empty cart")
    void clearCart_empty_returnsAlreadyEmpty() {
        String result = shoppingCartService.clearCart();

        assertEquals("Cart is already empty", result);
    }

    @Test
    @DisplayName("Clear cart with items")
    void clearCart_withItems_success() {
        shoppingCartService.addItem("Apple", 5);
        shoppingCartService.addItem("Banana", 3);

        String result = shoppingCartService.clearCart();

        assertTrue(result.contains("Cleared cart"));
        assertTrue(result.contains("Removed 2 items"));
        assertEquals("Shopping cart is empty", shoppingCartService.getCart());
    }

    @Test
    @DisplayName("Concurrent operations are thread-safe")
    void concurrentOperations_threadSafe() throws InterruptedException {
        int threads = 10;
        int iterations = 100;

        Thread[] threadArray = new Thread[threads];

        for (int i = 0; i < threads; i++) {
            threadArray[i] = new Thread(() -> {
                for (int j = 0; j < iterations; j++) {
                    shoppingCartService.addItem("Item", 1);
                }
            });
        }

        for (Thread t : threadArray) {
            t.start();
        }

        for (Thread t : threadArray) {
            t.join();
        }

        String cart = shoppingCartService.getCart();
        assertTrue(cart.contains("Total items: " + (threads * iterations)));
    }
}