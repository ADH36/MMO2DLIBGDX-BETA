package com.mmo.models;

/**
 * Represents an item in the inventory with quantity
 */
public class InventoryItem {
    private Item item;
    private int quantity;
    private int slotIndex; // Position in inventory
    
    public InventoryItem() {
        // Default constructor for Kryo
    }
    
    public InventoryItem(Item item, int quantity, int slotIndex) {
        this.item = item;
        this.quantity = quantity;
        this.slotIndex = slotIndex;
    }
    
    /**
     * Check if more items can be added to this stack
     */
    public boolean canStack(int amount) {
        if (!item.isStackable()) {
            return false;
        }
        return quantity + amount <= item.getMaxStack();
    }
    
    /**
     * Add items to this stack
     * @return true if successful, false if stack is full
     */
    public boolean addQuantity(int amount) {
        if (!canStack(amount)) {
            return false;
        }
        quantity += amount;
        return true;
    }
    
    /**
     * Remove items from this stack
     * @return true if successful, false if not enough items
     */
    public boolean removeQuantity(int amount) {
        if (quantity < amount) {
            return false;
        }
        quantity -= amount;
        return true;
    }
    
    // Getters and setters
    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public int getSlotIndex() { return slotIndex; }
    public void setSlotIndex(int slotIndex) { this.slotIndex = slotIndex; }
}
