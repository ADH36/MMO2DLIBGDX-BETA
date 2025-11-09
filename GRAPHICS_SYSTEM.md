# Graphics and Animation System

This document describes the enhanced graphics and animation system implemented for the MMO game.

## Overview

The graphics system provides immersive visual feedback for all game actions including player movement, combat, abilities, and environmental interactions. The system is designed to be lightweight and efficient while providing rich visual experiences.

## Components

### 1. Particle System

**Location**: `core/src/main/java/com/mmo/graphics/ParticleSystem.java`

The particle system manages visual effects throughout the game:

#### Features
- **Burst Effects**: Radial particle explosions for impacts
- **Spray Effects**: Directional particle streams for projectiles
- **Hit Effects**: Combat damage visualization (normal and critical)
- **Heal Effects**: Rising particles for healing effects
- **Movement Trails**: Subtle particles following moving characters

#### Usage
```java
particleSystem.createHitEffect(x, y, isCritical);
particleSystem.createHealEffect(x, y);
particleSystem.createMovementTrail(x, y, color);
```

### 2. Player Animations

**Location**: `core/src/main/java/com/mmo/graphics/PlayerAnimation.java`

Animated player sprites with multiple states:

#### Animation States
- **IDLE**: Gentle bobbing motion (1-3 pixels)
- **WALKING**: Larger bobbing with direction indicator
- **ATTACKING**: Attack arc visual effect

#### Visual Elements
- Direction indicator showing facing direction
- Bob offset for natural movement
- Attack arc on ability use
- Motion lines during movement

### 3. Ability Visual Effects

**Location**: `core/src/main/java/com/mmo/graphics/AbilityEffect.java`

Specialized visual effects for character abilities:

#### Effect Types

**PROJECTILE**
- Travels from caster to target
- Leaves a fading trail
- Color-coded by ability type

**BEAM**
- Instant line effect between caster and target
- Pulsing animation
- Used for channeled abilities

**AREA**
- Expanding circle effect
- Used for AoE abilities like Meteor Storm
- Fading outer ring

**BUFF**
- Rotating particles around character
- Used for buffs and shields
- Persistent during effect duration

#### Color Coding
- **Fire/Meteor**: Orange
- **Ice/Frost**: Cyan
- **Holy/Heal**: Yellow
- **Shadow/Poison**: Purple
- **Lightning**: Sky Blue
- **Default**: White

### 4. Enhanced World Rendering

**Location**: `core/src/main/java/com/mmo/world/WorldRenderer.java`

Procedurally generated world with rich environmental details:

#### Terrain Types
- **Grass**: Two shades for variation (dark and light green)
- **Water**: Found near world edges with two-tone coloring
- **Paths**: Brown pathways at grid intersections

#### Environmental Objects

**Trees**
- Brown trunk with green foliage
- Multiple foliage circles for depth
- Placed at regular intervals using hash function

**Rocks**
- Gray with shading
- Varying sizes
- Random placement for natural look

**Flowers**
- Random colors (red, yellow, purple)
- Small accent details
- Scattered throughout grass areas

#### Rendering Optimization
- Viewport culling (only render visible tiles)
- Procedural generation using hash functions
- Consistent placement across sessions

### 5. Enhanced UI Elements

#### Health Bars
- **Gradient colors** based on health percentage:
  - Green (>60%)
  - Yellow (30-60%)
  - Red (<30%)
- **Shadow effect** for depth
- **Highlight line** on top
- **Black border** for clarity

#### Visual Feedback
- **Combat messages** with 3-second display
- **Screen shake** on damage (stronger for crits)
- **Animated cooldown** indicators
- **Item usage effects** (green for health, cyan for mana)

## Performance Considerations

### Optimizations
1. **Particle Pooling**: Particles are created and removed as needed
2. **Viewport Culling**: Only visible elements are rendered
3. **Simple Shapes**: Using ShapeRenderer for efficiency
4. **Minimal State Changes**: Batched rendering where possible

### Performance Impact
- Particle count limited by lifetime
- Effects automatically clean up
- No texture loading required (shape-based)
- Low memory footprint

## Integration

The graphics system is integrated into `GameScreen.java`:

```java
// Initialization
particleSystem = new ParticleSystem();
playerAnimation = new PlayerAnimation();
abilityEffect = new AbilityEffect();

// Update loop
particleSystem.update(delta);
playerAnimation.update(delta, isMoving, velocityX, velocityY);
abilityEffect.update(delta);

// Render loop
particleSystem.render(shapeRenderer);
abilityEffect.render(shapeRenderer);
playerAnimation.render(shapeRenderer, x, y, color, radius);
```

## Camera Effects

### Screen Shake
- Triggered on taking damage
- Intensity based on damage type:
  - Normal hit: 8 units
  - Critical hit: 15 units
- Smooth decay over time
- Applied as camera offset

### Smooth Follow
- Camera follows player position
- Shake offset added for impact
- Maintains smooth movement

## Future Enhancements

Potential improvements for the graphics system:

1. **Texture Support**: Add sprite sheets for more detailed graphics
2. **Particle Pooling**: Reuse particle objects for better performance
3. **Advanced Animations**: Frame-based sprite animations
4. **Lighting System**: Dynamic lighting and shadows
5. **Weather Effects**: Rain, snow, fog
6. **Day/Night Cycle**: Time-based lighting changes
7. **Advanced Particles**: Support for particle physics
8. **Sound Integration**: Sync visual effects with audio

## Technical Details

### Coordinate System
- World coordinates for gameplay
- Screen coordinates for UI
- Camera matrix for transformations

### Rendering Order
1. Background (sky)
2. World tiles
3. Environmental objects
4. Particles (under players)
5. Ability effects
6. Players (with animations)
7. Health bars
8. UI elements (fixed position)

### Color Management
All colors are managed using LibGDX Color class with RGBA values for transparency support.

## Troubleshooting

### Common Issues

**Particles not showing**
- Ensure update() is called each frame
- Check particle lifetime values
- Verify rendering order

**Animation glitches**
- Verify delta time is correct
- Check animation state transitions
- Ensure update is called before render

**Performance issues**
- Reduce particle count in effects
- Check viewport culling is working
- Monitor particle cleanup

## Credits

Graphics system designed for the MMO2DLIBGDX-BETA project using LibGDX framework.
