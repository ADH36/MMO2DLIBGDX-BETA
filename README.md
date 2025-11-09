# MMO2DLIBGDX-BETA

A fully-featured 2D MMO game built with LibGDX, featuring an open world, character classes with abilities, multiplayer networking, and a dedicated server with monitoring capabilities.

## Features

### Client Features
- **Home Screen**: Clean UI with login, register, and exit options
- **Authentication System**: Full sign-in and sign-up functionality
- **Character Management**:
  - Character selection screen
  - Character creation with detailed class selection
  - Support for up to 5 characters per account
- **Character Classes**: 5 unique classes with distinct abilities and stats:
  - **Warrior**: High health and defense melee fighter
  - **Mage**: Powerful spell caster with arcane abilities
  - **Archer**: Precision ranged fighter with agility
  - **Rogue**: Stealthy assassin with critical strikes
  - **Cleric**: Holy warrior with healing and support abilities
- **Ability System**: Each class has 4 unique abilities with detailed descriptions
- **Combat System**: Full PvP combat implementation
  - Target selection (TAB/T keys)
  - Skill-based attacks with damage calculation
  - Critical hits (15% chance, 150% damage)
  - Mana consumption and auto-regeneration
  - Cooldown system
  - Range checking
  - Health bars above players
  - Death and auto-respawn mechanics
- **Open World**: Tile-based world with player movement and multiplayer synchronization
- **Chat System**: Real-time chat with other players
- **Real-time Multiplayer**: See and interact with other players in the world
- **Item and Inventory System**: Full inventory management
  - 20-slot inventory with stackable items
  - Item rarity system (Common, Uncommon, Rare, Epic, Legendary)
  - Consumables, weapons, armor, materials, and quest items
  - Gold currency system
  - Starter items for new characters

### Server Features
- **Authentication Management**: Secure login and registration
- **Character Storage**: Persistent character data
- **World State Synchronization**: Real-time player position updates
- **Combat System**: Server-validated combat mechanics
  - Damage calculation and validation
  - Mana and cooldown enforcement
  - Range checking
  - Death detection and respawn
  - Combat event broadcasting
- **Chat Broadcasting**: Server-side chat message relay
- **Player Management**: Track active players and sessions
- **Monitoring Dashboard**: Real-time server statistics and player information
- **Ability System**: Server-validated ability usage
- **Inventory System**: Server-side inventory management
  - Item usage validation
  - Effect application (health/mana restoration)
  - Inventory state synchronization

## Project Structure

```
MMO2DLIBGDX-BETA/
├── core/               # Shared game logic and models
│   └── src/main/java/com/mmo/
│       ├── game/       # Main game class
│       ├── models/     # Data models (Character, Player, Abilities)
│       ├── network/    # Network protocol definitions
│       ├── screens/    # Game screens (Home, Login, Character, Game)
│       └── world/      # World rendering
├── desktop/            # Desktop client launcher
│   └── src/main/java/com/mmo/desktop/
├── server/             # Dedicated game server
│   └── src/main/java/com/mmo/server/
└── build.gradle        # Build configuration
```

## Getting Started

### Prerequisites
- Java 11 or higher
- Gradle 7.6+ (included via wrapper)

### Building the Project

```bash
# Build all modules
./gradlew build

# Build specific module
./gradlew :core:build
./gradlew :desktop:build
./gradlew :server:build
```

### Running the Server

```bash
# Start the server
./gradlew :server:run

# Or use the distribution
cd server/build/distributions
unzip server.zip
./server/bin/server
```

The server will start on:
- TCP Port: 54555
- UDP Port: 54777

Default test account:
- Username: `test`
- Password: `test`

### Running the Client

```bash
# Run the desktop client
./gradlew :desktop:run
```

## Gameplay Instructions

### Main Menu
- **LOGIN**: Access existing account
- **REGISTER**: Create new account
- **EXIT**: Close the game

### Login/Register
- Use keyboard to type username/password
- **TAB**: Switch between input fields
- **ENTER**: Submit
- **ESC**: Return to main menu

### Character Selection
- **UP/DOWN**: Navigate character list
- **ENTER**: Select character and enter world
- **C**: Create new character
- **ESC**: Logout

### Character Creation
- Type character name
- **LEFT/RIGHT**: Change class selection
- View detailed class stats and abilities
- **ENTER**: Create character
- **ESC**: Return to character selection

### In-Game Controls
- **WASD** or **Arrow Keys**: Move character
- **1-4**: Use abilities (attacks selected target)
- **TAB**: Cycle through nearby targets
- **T**: Target nearest player
- **I**: Open/close inventory
- **0-9**: Use item in inventory slot (when inventory is open)
- **ENTER**: Open chat
- **ESC**: Return to character selection (or close inventory if open)

### Combat System
- **Targeting**: Use TAB to cycle targets or T to target nearest enemy
- **Abilities**: Press 1-4 to use class abilities on selected target
- **Visual Feedback**: Health bars, target highlighting, combat messages
- **Mana Management**: Abilities cost mana, regenerates automatically
- **Cooldowns**: Abilities have cooldown periods to prevent spam
- **Death/Respawn**: Auto-respawn 3 seconds after death with full HP/MP

See [COMBAT_SYSTEM.md](COMBAT_SYSTEM.md) for detailed combat mechanics.

### Inventory System
- Press **I** to open/close inventory
- **20 slots** for items
- Press **0-9** to use consumable items in slots 0-9
- **Item Rarity**: Common (white), Uncommon (green), Rare (blue), Epic (purple), Legendary (orange)
- **Item Types**: Consumables, Weapons, Armor, Materials, Quest items
- **Gold**: Currency system for future trading
- **Starter Items**: New characters begin with health/mana potions, basic weapon and armor

### Chat System
- Press **ENTER** to activate chat
- Type message
- Press **ENTER** to send
- Press **ESC** to cancel

## Character Classes

### Warrior
- **HP**: 150 | **MP**: 50 | **ATK**: 25 | **DEF**: 30
- **Abilities**: Slash, Shield Bash, War Cry, Charge
- **Playstyle**: Tank and melee damage dealer

### Mage
- **HP**: 80 | **MP**: 150 | **ATK**: 35 | **DEF**: 10
- **Abilities**: Fireball, Ice Lance, Teleport, Meteor Storm
- **Playstyle**: High damage spell caster

### Archer
- **HP**: 100 | **MP**: 80 | **ATK**: 22 | **DEF**: 15
- **Abilities**: Power Shot, Multi-Shot, Trap, Eagle Eye
- **Playstyle**: Ranged precision damage

### Rogue
- **HP**: 90 | **MP**: 70 | **ATK**: 28 | **DEF**: 12
- **Abilities**: Backstab, Vanish, Poison Blade, Shadow Step
- **Playstyle**: High critical damage assassin

### Cleric
- **HP**: 110 | **MP**: 120 | **ATK**: 18 | **DEF**: 20
- **Abilities**: Heal, Holy Shield, Smite, Divine Blessing
- **Playstyle**: Support and healing

## Server Monitoring

The server provides real-time monitoring with:
- Active player count
- Total registered accounts
- Character database size
- Player positions and activities
- Status updates every 10 seconds

Type `status` in server console for immediate status report.
Type `exit` to shutdown server gracefully.

## Network Protocol

The game uses KryoNet for efficient client-server communication:
- **TCP**: Authentication, character management, chat
- **UDP**: Real-time player movement and world updates
- **Messages**: Login, Register, Character CRUD, Movement, Chat, Abilities

## Technologies Used

- **LibGDX 1.11.0**: Game framework
- **KryoNet 2.22.0**: Networking library
- **Gradle**: Build system
- **Java 11**: Programming language

## Development

### Adding New Features

1. **New Ability**: Add to `CharacterClass.java` ability arrays
2. **New Character Class**: Add enum value in `CharacterClass.java`
3. **New Network Message**: Add to `Network.java` and register with Kryo
4. **New Screen**: Extend `Screen` interface in `screens/` package

### Testing

```bash
# Build and test
./gradlew test

# Run server
./gradlew :server:run

# Run client (in separate terminal)
./gradlew :desktop:run
```

## Future Enhancements

- Database persistence (currently in-memory)
- ~~Combat system implementation~~ ✅ **COMPLETED**
- ~~Item and inventory system~~ ✅ **COMPLETED**
- Equipment system (equipping weapons/armor)
- Item trading between players
- Quests and NPCs
- Guilds and parties
- PvP and PvE content (PvP combat ✅ completed)
- Mobile client support
- Enhanced graphics and animations

## License

This project is open source and available for educational purposes.

## Contributors

- ADH36 (Project Creator)

---

**Note**: This is a beta version with **fully functional combat system**. The game includes core MMO features: authentication, character management, open world exploration, real-time multiplayer, and **complete PvP combat with skills, damage calculation, and death/respawn mechanics**.
