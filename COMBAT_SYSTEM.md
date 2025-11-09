# Combat System Guide

## Overview
The MMO game now features a complete combat system with skills, attacks, damage calculation, and player-vs-player combat.

## Combat Controls

### Targeting
- **TAB** - Cycle through nearby players to select target
- **T** - Auto-target nearest player
- Yellow circle appears around selected target
- Distance to target is displayed in UI

### Abilities
- **1** - Use Ability 1 (varies by class)
- **2** - Use Ability 2
- **3** - Use Ability 3
- **4** - Use Ability 4

Abilities are automatically directed at your selected target. If no target is selected, healing abilities will target yourself.

## Combat Mechanics

### Damage Calculation
```
Base Damage = Ability Damage + Character Attack
Actual Damage = Base Damage - (Target Defense / 2)
Minimum Damage = 1 (always deal at least 1 damage)
```

### Critical Hits
- 15% chance to land a critical hit
- Critical hits deal 150% damage
- Indicated by "(CRITICAL!)" in combat messages

### Mana System
- Each ability costs mana to use
- Mana regenerates automatically at 5 MP every 2 seconds
- Abilities cannot be used without sufficient mana
- Current mana shown in UI: "MP: X/Y"

### Cooldowns
- Each ability has a cooldown period after use
- Cooldowns prevent ability spam
- UI shows cooldown timer: "Ability Name (Xs)"
- Abilities on cooldown appear grayed out

### Range System
- Each ability has a maximum range
- Abilities cannot be used if target is out of range
- Range increases with higher-tier abilities
- Distance to target shown in UI

## Visual Indicators

### Health Bars
- Green/red bar above each player
- Shows current HP / max HP
- Updates in real-time during combat

### Target Highlighting
- Selected target has yellow circle outline
- Makes target tracking easier during combat

### Ability Status
- **Green/White** - Ready to use
- **Gray** - On cooldown (shows remaining seconds)
- **Dark Gray** - Not enough mana

### Combat Feedback
- Large text in center of screen for important events
- "CRITICAL!" for critical hits
- "YOU DIED!" when killed
- "ENEMY SLAIN!" when you kill an enemy
- "RESPAWNED" when respawning

## Death and Respawn

### Death
- Occurs when health reaches 0
- Death message broadcast to all players
- Killer receives credit

### Respawn
- Automatic after 3 seconds
- Respawn at starting location (100, 100)
- Full health and mana restored
- All cooldowns reset

## Character Classes

Each class has unique abilities with different effects:

### Warrior
- High health (150) and defense (30)
- Abilities: Slash, Shield Bash, War Cry, Charge
- Best for tanking and melee combat

### Mage
- High mana (150) and attack (35)
- Abilities: Fireball, Ice Lance, Teleport, Meteor Storm
- Best for high burst damage

### Archer
- Balanced stats with good agility
- Abilities: Power Shot, Multi-Shot, Trap, Eagle Eye
- Best for ranged combat

### Rogue
- High attack (28) and critical damage
- Abilities: Backstab, Vanish, Poison Blade, Shadow Step
- Best for assassination and burst damage

### Cleric
- High mana (120) and healing abilities
- Abilities: Heal, Holy Shield, Smite, Divine Blessing
- Best for support and healing

## Combat Tips

1. **Select Targets Early** - Use TAB or T to select targets before engaging
2. **Watch Your Mana** - Don't spam abilities, mana regenerates slowly
3. **Mind the Range** - Check distance before using abilities
4. **Use Cooldowns Wisely** - Plan your ability rotation
5. **Monitor Health** - Watch health bars to know when to retreat
6. **Healing** - Clerics can heal themselves or allies
7. **Positioning** - Stay within range but avoid clustering

## Combat Example

```
1. Press T to target nearest enemy
2. Check distance in UI (should be < 100-200 depending on ability)
3. Press 1 to use first ability
4. Watch combat message: "You hit PlayerName with Slash for 25 damage!"
5. Wait for cooldown (shown in UI)
6. Press 2 for second ability when ready
7. Continue until enemy is defeated or you need to retreat
```

## Troubleshooting

**"Ability is on cooldown"**
- Wait for the cooldown timer to reach 0
- Shown in UI next to ability name

**"Not enough mana"**
- Wait for mana regeneration
- Mana regenerates at 5 MP every 2 seconds

**"Target out of range"**
- Move closer to target
- Check distance in UI
- Higher-tier abilities have longer range

**Ability does nothing**
- Make sure you have a target selected (TAB or T)
- Check that you have enough mana
- Verify ability is not on cooldown

## Server-Side Features

The combat system includes server-side validation:
- Range checking
- Mana validation
- Cooldown enforcement
- Damage calculation
- Death detection
- Automatic respawn scheduling

All combat is validated on the server to prevent cheating.
