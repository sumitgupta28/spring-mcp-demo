package com.example.springmcpdemo.service;

import com.example.springmcpdemo.model.CartItem;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ShoppingCartService {

    private final ConcurrentHashMap<String, CartItem> cart = new ConcurrentHashMap<>();

    @Tool(description = "Add an item to the shopping cart. If the item already exists, the quantity will be added to the existing item.")
    public String addItem(
            @ToolParam(description = "Name of the item to add") String itemName,
            @ToolParam(description = "Quantity to add") int quantity) {

        if (itemName == null || itemName.isBlank()) {
            return "Error: Item name cannot be empty";
        }

        if (quantity <= 0) {
            return "Error: Quantity must be greater than 0";
        }

        cart.compute(itemName, (key, existingItem) -> {
            if (existingItem != null) {
                existingItem.setQuantity(existingItem.getQuantity() + quantity);
                return existingItem;
            }
            return new CartItem(itemName, quantity);
        });

        CartItem item = cart.get(itemName);
        return String.format("Added %s (quantity: %d) to cart. Total quantity: %d", itemName, quantity, item.getQuantity());
    }

    @Tool(description = "Remove an item from the shopping cart, or reduce its quantity.")
    public String removeItem(
            @ToolParam(description = "Name of the item to remove") String itemName,
            @ToolParam(description = "Quantity to remove. If not specified or 0, removes all of the item.") int quantity) {

        if (itemName == null || itemName.isBlank()) {
            return "Error: Item name cannot be empty";
        }

        CartItem existingItem = cart.get(itemName);

        if (existingItem == null) {
            return String.format("Error: %s not found in cart", itemName);
        }

        if (quantity <= 0 || quantity >= existingItem.getQuantity()) {
            cart.remove(itemName);
            return String.format("Removed %s completely from cart", itemName);
        } else {
            existingItem.setQuantity(existingItem.getQuantity() - quantity);
            return String.format("Reduced %s by %d. Remaining quantity: %d", itemName, quantity, existingItem.getQuantity());
        }
    }

    @Tool(description = "Get all items currently in the shopping cart.")
    public String getCart() {
        if (cart.isEmpty()) {
            return "Shopping cart is empty";
        }

        String itemList = cart.values().stream()
                .map(item -> String.format("- %s: %d", item.getItemName(), item.getQuantity()))
                .collect(Collectors.joining("\n"));

        int totalItems = cart.values().stream().mapToInt(CartItem::getQuantity).sum();

        return String.format("Shopping Cart (%d items):\n%s\nTotal items: %d", cart.size(), itemList, totalItems);
    }

    @Tool(description = "Clear all items from the shopping cart.")
    public String clearCart() {
        int count = cart.size();
        if (count == 0) {
            return "Cart is already empty";
        }
        cart.clear();
        return String.format("Cleared cart. Removed %d items", count);
    }
}