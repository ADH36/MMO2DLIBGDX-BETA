# Inventory System Documentation

## Overview

The MMO game now features a complete item and inventory system that allows players to collect, manage, and use items. The system supports multiple item types, rarity levels, stackable items, and a gold currency system.

## Features

### Item Types

The game supports five different item types:

1. **WEAPON** - Swords, bows, staffs, daggers (provide attack bonuses)
2. **ARMOR** - Helmets, chest pieces, boots (provide defense and health bonuses)
3. **CONSUMABLE** - Potions, food, scrolls (can be used for immediate effects)
4. **MATERIAL** - Crafting materials, ores, wood (stackable resources)
5. **QUEST** - Quest-specific items (future implementation)
6. **MISC** - Miscellaneous items

### Item Rarity

Items are categorized by rarity, which determines their color in the UI:

- **COMMON** (White) - Basic items
- **UNCOMMON** (Green) - Better quality items
- **RARE** (Blue) - Rare finds
- **EPIC** (Purple) - Very rare items
- **LEGENDARY** (Orange) - Extremely rare and powerful items

### Inventory System

- **20 Slots**: Each character has a 20-slot inventory (4 columns x 5 rows)
- **Stackable Items**: Consumables and materials can stack (up to 99 or 999 depending on type)
- **Gold Currency**: Players have a gold balance for future trading
- **Starter Items**: New characters begin with:
  - 5x Health Potions (restore 50 HP)
  - 5x Mana Potions (restore 30 MP)
  - 1x Iron Sword (basic weapon)
  - 1x Leather Armor (basic armor)
  - 100 Gold

## Item Database

The game includes a predefined item database with the following items:

### Consumables
- **Health Potion** (Common) - Restores 50 HP, 10g
- **Mana Potion** (Common) - Restores 30 MP, 10g
- **Greater Health Potion** (Uncommon) - Restores 100 HP, 25g
- **Greater Mana Potion** (Uncommon) - Restores 60 MP, 25g

### Weapons
- **Iron Sword** (Common) - +10 Attack, Level 1, 50g
- **Steel Sword** (Uncommon) - +20 Attack, Level 5, 150g
- **Legendary Blade** (Legendary) - +50 Attack, Level 10, 1000g

### Armor
- **Leather Armor** (Common) - +5 Defense, Level 1, 30g
- **Chain Mail** (Uncommon) - +10 HP, +15 Defense, Level 5, 100g
- **Dragon Scale Armor** (Legendary) - +50 HP, +50 Defense, Level 10, 2000g

### Materials
- **Wood** (Common) - Stackable up to 999, 1g
- **Iron Ore** (Common) - Stackable up to 999, 5g
- **Gold Ore** (Rare) - Stackable up to 999, 20g

## Usage

### Opening the Inventory

Press the **I** key to toggle the inventory window.

### Using Items

1. Open the inventory with **I**
2. Press the number key (0-9) corresponding to the slot containing the item you want to use
3. Only consumable items can be used this way
4. The item will be consumed and its effects applied immediately
5. Health and mana restoration effects are capped at your maximum values

### Item Effects

Consumable items can provide the following effects:
- **Health Restoration**: Restores HP up to maximum health
- **Mana Restoration**: Restores MP up to maximum mana

## Technical Implementation

### Client-Side

The inventory UI is rendered in `GameScreen.java`:
- Semi-transparent black background with gold border
- 4x5 grid of inventory slots
- Item names displayed with rarity-based colors
- Quantity shown for stackable items
- Slot numbers (0-9) displayed on first 10 slots
- Gold balance shown in top-right corner

### Server-Side

The server validates all inventory operations in `MMOServer.java`:
- Verifies item exists in the specified slot
- Checks if the item type is consumable (for use requests)
- Applies item effects (health/mana restoration)
- Removes consumed items from inventory
- Sends response with updated stats to client

### Network Protocol

Three new message types handle inventory operations:

1. **UseItemRequest** (Client → Server)
   - `slotIndex`: The inventory slot to use

2. **UseItemResponse** (Server → Client)
   - `success`: Whether the operation succeeded
   - `message`: Feedback message
   - `healthRestored`: Amount of HP restored
   - `manaRestored`: Amount of MP restored

3. **AddItemRequest** (Reserved for future use)
4. **RemoveItemRequest** (Reserved for future use)

## Data Models

### Item
```java
- id: long
- name: String
- description: String
- type: ItemType
- rarity: ItemRarity
- value: int (gold value)
- stackable: boolean
- maxStack: int
- levelRequirement: int
- healthBonus: int (equipment)
- manaBonus: int (equipment)
- attackBonus: int (equipment)
- defenseBonus: int (equipment)
- healthRestore: int (consumable)
- manaRestore: int (consumable)
```

### InventoryItem
```java
- item: Item
- quantity: int
- slotIndex: int
```

### Inventory
```java
- maxSize: int (default 20)
- items: List<InventoryItem>
- gold: int
```

## Future Enhancements

The current implementation provides a foundation for future features:

1. **Equipment System**: Ability to equip weapons and armor
2. **Item Trading**: Trade items between players
3. **Item Drops**: Items dropped by enemies or found in the world
4. **Crafting System**: Combine materials to create new items
5. **Shops/Vendors**: NPCs that buy and sell items
6. **Item Durability**: Equipment that degrades over time
7. **Quest Items**: Special items for quest completion
8. **Item Tooltips**: Detailed item information on hover
9. **Drag-and-Drop**: Move items between inventory slots
10. **Item Sorting**: Automatic inventory organization

## Controls Reference

| Key | Action |
|-----|--------|
| **I** | Open/close inventory |
| **0-9** | Use item in corresponding slot (consumables only) |
| **ESC** | Close inventory |

## Examples

### Using a Health Potion

1. Press **I** to open inventory
2. Locate a Health Potion in slot 0
3. Press **0** to use it
4. See "+50 HP" feedback message
5. Your health is restored by 50 points (up to maximum)
6. The potion quantity decreases by 1

### Checking Your Gold

1. Press **I** to open inventory
2. Look at the top-right corner
3. "Gold: 100" shows your current balance

## Code Examples

### Adding an Item to Inventory (Server-side)
```java
Item healthPotion = ItemDatabase.getItemCopy(1);
character.getInventory().addItem(healthPotion, 5);
```

### Using an Item (Client-side)
```java
Network.UseItemRequest request = new Network.UseItemRequest();
request.slotIndex = 0;
game.client.sendTCP(request);
```

### Creating a New Item Type
```java
Item customItem = new Item(nextId++, "Magic Scroll", 
    "A mysterious scroll", ItemType.CONSUMABLE, ItemRarity.RARE);
customItem.setValue(50);
customItem.setStackable(true);
customItem.setMaxStack(20);
customItem.setManaRestore(100);
```

## Troubleshooting

### Item Not Being Used
- Ensure the item is consumable (weapons/armor cannot be used)
- Check that the slot contains an item
- Verify you're pressing the correct number key (0-9)
- Make sure the inventory is open when pressing the number key

### Visual Issues
- If inventory doesn't display, check console for errors
- Ensure LibGDX rendering pipeline is working correctly
- Verify all item assets are loaded properly

## Performance Considerations

- Inventory operations are validated server-side to prevent cheating
- Item data is serialized efficiently with Kryo
- Inventory UI only renders when open (no performance impact when closed)
- Stackable items reduce memory usage and network traffic
