package com.mmo.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a character's inventory
 */
public class Inventory {
    private static final int DEFAULT_SIZE = 20; // 20 slots by default
    
    private int maxSize;
    private List<InventoryItem> items;
    private int gold;
    
    public Inventory() {
        // Default constructor for Kryo
        this.maxSize = DEFAULT_SIZE;
        this.items = new ArrayList<>();
        this.gold = 0;
    }
    
    public Inventory(int maxSize) {
        this.maxSize = maxSize;
        this.items = new ArrayList<>();
        this.gold = 0;
    }
    
    /**
     * Add an item to the inventory
     * @return true if successful, false if inventory is full
     */
    public boolean addItem(Item item, int quantity) {
        // If item is stackable, try to stack with existing items first
        if (item.isStackable()) {
            for (InventoryItem invItem : items) {
                if (invItem.getItem().getId() == item.getId() && invItem.canStack(quantity)) {
                    invItem.addQuantity(quantity);
                    return true;
                }
            }
        }
        
        // Check if inventory has space
        if (items.size() >= maxSize) {
            return false;
        }
        
        // Add new item
        int slotIndex = findNextFreeSlot();
        items.add(new InventoryItem(item, quantity, slotIndex));
        return true;
    }
    
    /**
     * Remove an item from the inventory
     * @return true if successful, false if item not found or not enough quantity
     */
    public boolean removeItem(long itemId, int quantity) {
        InventoryItem itemToRemove = null;
        
        for (InventoryItem invItem : items) {
            if (invItem.getItem().getId() == itemId) {
                if (invItem.getQuantity() >= quantity) {
                    if (invItem.removeQuantity(quantity)) {
                        // If quantity is now 0, mark for removal
                        if (invItem.getQuantity() == 0) {
                            itemToRemove = invItem;
                        }
                        break;
                    }
                }
            }
        }
        
        if (itemToRemove != null) {
            items.remove(itemToRemove);
        }
        
        return itemToRemove != null || items.stream()
            .anyMatch(i -> i.getItem().getId() == itemId && i.getQuantity() > 0);
    }
    
    /**
     * Get an item by slot index
     */
    public InventoryItem getItemAtSlot(int slotIndex) {
        for (InventoryItem item : items) {
            if (item.getSlotIndex() == slotIndex) {
                return item;
            }
        }
        return null;
    }
    
    /**
     * Use a consumable item
     * @return true if item was used successfully
     */
    public boolean useItem(int slotIndex) {
        InventoryItem invItem = getItemAtSlot(slotIndex);
        if (invItem == null) {
            return false;
        }
        
        // Only consumables can be used
        if (invItem.getItem().getType() != ItemType.CONSUMABLE) {
            return false;
        }
        
        // Remove one from stack
        return invItem.removeQuantity(1);
    }
    
    /**
     * Find the next free slot index
     */
    private int findNextFreeSlot() {
        for (int i = 0; i < maxSize; i++) {
            final int slot = i;
            if (items.stream().noneMatch(item -> item.getSlotIndex() == slot)) {
                return i;
            }
        }
        return items.size();
    }
    
    /**
     * Check if inventory has space for an item
     */
    public boolean hasSpace() {
        return items.size() < maxSize;
    }
    
    /**
     * Get item count for a specific item
     */
    public int getItemCount(long itemId) {
        return items.stream()
            .filter(i -> i.getItem().getId() == itemId)
            .mapToInt(InventoryItem::getQuantity)
            .sum();
    }
    
    // Getters and setters
    public int getMaxSize() { return maxSize; }
    public void setMaxSize(int maxSize) { this.maxSize = maxSize; }
    
    public List<InventoryItem> getItems() { return items; }
    public void setItems(List<InventoryItem> items) { this.items = items; }
    
    public int getGold() { return gold; }
    public void setGold(int gold) { this.gold = gold; }
    
    public void addGold(int amount) { this.gold += amount; }
    public boolean removeGold(int amount) {
        if (gold >= amount) {
            gold -= amount;
            return true;
        }
        return false;
    }
}
