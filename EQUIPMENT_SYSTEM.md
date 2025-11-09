# Equipment System Documentation

## Overview

The MMO game now features a complete equipment system that allows players to equip weapons and armor to enhance their character's stats. Players can manage their equipment through the inventory interface, gaining stat bonuses from equipped items.

## Features

### Equipment Slots

The game supports two equipment slots:

1. **WEAPON** - Main hand weapon (swords, bows, staffs, daggers)
2. **ARMOR** - Body armor (chest pieces, helmets, full sets)

### Equipping Items

Players can equip items from their inventory:
- Items must be of type WEAPON or ARMOR to be equippable
- Equipping an item removes it from inventory and places it in the appropriate equipment slot
- If a slot already has an item equipped, the old item is automatically swapped back to inventory
- Stat bonuses are immediately applied when an item is equipped

### Stat Bonuses

Equipped items provide the following bonuses:
- **Attack Bonus** - Increases character's attack power (weapons)
- **Defense Bonus** - Increases character's defense (armor)
- **Health Bonus** - Increases character's maximum health (armor)
- **Mana Bonus** - Increases character's maximum mana (some equipment)

### Unequipping Items

Players can unequip items at any time:
- Unequipping removes the item from the equipment slot
- The item is returned to the inventory
- Stat bonuses are removed when an item is unequipped
- Current HP/MP are adjusted if necessary (cannot exceed new maximum)

## Usage

### Opening the Inventory

Press the **I** key to open the inventory window.

### Equipping an Item

1. Open the inventory with **I**
2. Press **E** + number key (0-9) to equip an item from that slot
   - Example: Press **E** then **0** to equip the item in slot 0
3. The item will be equipped if it's a weapon or armor
4. If you already have an item in that slot, it will be automatically swapped

### Unequipping an Item

1. Open the inventory with **I**
2. Press **U** + **W** to unequip your weapon
3. Press **U** + **A** to unequip your armor
4. The item will be returned to your inventory

### Viewing Equipped Items

When the inventory is open, the right side of the screen shows:
- **Equipped Weapon** - Shows weapon name, rarity, and attack bonus
- **Equipped Armor** - Shows armor name, rarity, and defense/health bonuses
- Empty slots show "None"

## Technical Implementation

### Client-Side

The inventory UI in `GameScreen.java` now includes:
- Equipment display panel showing currently equipped items
- Color-coded item names based on rarity
- Stat bonuses displayed for equipped items
- Keyboard controls for equipping/unequipping items
- Visual feedback with particle effects when equipping items

### Server-Side

The server validates all equipment operations in `MMOServer.java`:
- Verifies item exists in the specified slot
- Checks if the item type is equippable
- Handles equipment swapping when slot is occupied
- Applies/removes stat bonuses correctly
- Prevents inventory overflow during swapping
- Sends response with updated character stats

### Network Protocol

Three new message types handle equipment operations:

1. **EquipItemRequest** (Client → Server)
   - `slotIndex`: The inventory slot containing the item to equip

2. **EquipItemResponse** (Server → Client)
   - `success`: Whether the operation succeeded
   - `message`: Feedback message
   - `updatedCharacter`: Character data with new stats

3. **UnequipItemRequest** (Client → Server)
   - `equipmentSlot`: Which equipment slot to unequip (WEAPON or ARMOR)

4. **UnequipItemResponse** (Server → Client)
   - `success`: Whether the operation succeeded
   - `message`: Feedback message
   - `updatedCharacter`: Character data with new stats

## Data Models

### EquipmentSlot (Enum)
```java
- WEAPON    // Main hand weapon
- ARMOR     // Body armor
```

### CharacterData Extensions
```java
- equippedItems: Map<EquipmentSlot, Item>
- getEquippedItem(slot): Item
- equipItem(slot, item): void
- unequipItem(slot): Item
- hasEquippedItem(slot): boolean
```

### Inventory Extensions
```java
- removeItemFromSlot(slotIndex): Item  // For equipping items
```

## Examples

### Equipping a Weapon

1. Open inventory with **I**
2. Look at slot 2 which contains an "Iron Sword"
3. Press **E** then **2**
4. See message: "Equipped Iron Sword"
5. Your attack increases by the weapon's attack bonus
6. The weapon appears in the "Equipped: Weapon" section

### Swapping Equipment

1. You have "Iron Sword" equipped
2. You find a better "Steel Sword" in slot 5
3. Press **E** then **5**
4. The Iron Sword is automatically moved back to inventory
5. The Steel Sword is now equipped
6. Your stats are updated with the new weapon's bonuses

### Unequipping Armor

1. Open inventory with **I**
2. See "Leather Armor" equipped in the Equipped section
3. Press **U** then **A**
4. See message: "Unequipped Leather Armor"
5. Your defense and max health decrease
6. The armor is returned to your inventory

## Stat Bonus Calculations

When equipping an item:
```java
character.maxHealth += item.healthBonus
character.health += item.healthBonus
character.maxMana += item.manaBonus
character.mana += item.manaBonus
character.attack += item.attackBonus
character.defense += item.defenseBonus
```

When unequipping an item:
```java
character.maxHealth -= item.healthBonus
character.health = min(character.health - item.healthBonus, character.maxHealth)
character.maxMana -= item.manaBonus
character.mana = min(character.mana - item.manaBonus, character.maxMana)
character.attack -= item.attackBonus
character.defense -= item.defenseBonus
```

## Available Equipment

Based on the item database, players can find:

### Weapons
- **Iron Sword** (Common) - +10 Attack, Level 1
- **Steel Sword** (Uncommon) - +20 Attack, Level 5
- **Legendary Blade** (Legendary) - +50 Attack, Level 10

### Armor
- **Leather Armor** (Common) - +5 Defense, Level 1
- **Chain Mail** (Uncommon) - +10 HP, +15 Defense, Level 5
- **Dragon Scale Armor** (Legendary) - +50 HP, +50 Defense, Level 10

## Controls Reference

| Key Combination | Action |
|-----------------|--------|
| **I** | Open/close inventory |
| **E + 0-9** | Equip item from slot 0-9 |
| **U + W** | Unequip weapon |
| **U + A** | Unequip armor |
| **0-9** | Use consumable item in slot (when not using E or U) |
| **ESC** | Close inventory |

## Validation and Edge Cases

The equipment system handles several edge cases:

1. **Inventory Full During Swap**: If inventory is full when swapping equipment, the operation fails and the old item remains equipped
2. **Invalid Item Type**: Only WEAPON and ARMOR types can be equipped
3. **Empty Slot**: Attempting to equip from an empty inventory slot shows an error
4. **No Equipped Item**: Attempting to unequip when no item is equipped shows an error
5. **Stat Boundaries**: Current HP/MP cannot exceed maximum values after unequipping items with bonuses

## Future Enhancements

Potential improvements to the equipment system:

1. **More Equipment Slots**: Helmet, gloves, boots, rings, etc.
2. **Equipment Requirements**: Level or stat requirements to equip items
3. **Set Bonuses**: Additional bonuses when wearing matching equipment sets
4. **Equipment Durability**: Items that degrade over time and need repair
5. **Socket System**: Adding gems or enchantments to equipment
6. **Visual Character Changes**: Character appearance changes based on equipped items
7. **Equipment Comparison**: Show stat differences when hovering over items
8. **Quick Swap**: Hotkeys to quickly swap between equipment sets
9. **Bind on Equip**: Items that become bound to character when equipped
10. **Equipment Restrictions**: Class-specific or race-specific equipment

## Troubleshooting

### Item Not Equipping
- Ensure the item is a WEAPON or ARMOR type
- Check that you're pressing E before the number key
- Verify the slot contains an item
- Make sure inventory isn't full (for equipment swapping)

### Stats Not Updating
- The server validates and applies stat changes
- Check the combat feedback message for success/failure
- Stats are sent back from server in the response
- Reconnecting to the server may help if there's a sync issue

### Visual Issues
- If equipped items don't display, close and reopen inventory
- Check console for network errors
- Verify server is running and connected

## Performance Considerations

- Equipment operations are validated server-side to prevent cheating
- Character stats are updated atomically during equip/unequip
- Equipment data is efficiently serialized with Kryo
- Equipment UI only renders when inventory is open
- Stat bonuses are calculated once per equip/unequip operation
