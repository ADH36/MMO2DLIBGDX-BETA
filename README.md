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
- **Open World**: Tile-based world with player movement and multiplayer synchronization
- **Chat System**: Real-time chat with other players
- **Real-time Multiplayer**: See and interact with other players in the world

### Server Features
- **Authentication Management**: Secure login and registration
- **Character Storage**: Persistent character data
- **World State Synchronization**: Real-time player position updates
- **Chat Broadcasting**: Server-side chat message relay
- **Player Management**: Track active players and sessions
- **Monitoring Dashboard**: Real-time server statistics and player information
- **Ability System**: Server-validated ability usage

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
- **1-4**: Use abilities
- **ENTER**: Open chat
- **ESC**: Return to character selection

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
- Combat system implementation
- Item and inventory system
- Quests and NPCs
- Guilds and parties
- PvP and PvE content
- Mobile client support
- Enhanced graphics and animations

## License

This project is open source and available for educational purposes.

## Contributors

- ADH36 (Project Creator)

---

**Note**: This is a beta version. The game is fully functional for demonstration and learning purposes, with core MMO features implemented including authentication, character management, open world exploration, and real-time multiplayer.
