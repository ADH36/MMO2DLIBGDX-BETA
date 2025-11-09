# Technical Architecture - MMO 2D LibGDX Game

## System Overview

This MMO game follows a client-server architecture with real-time networking capabilities.

```
┌─────────────┐         TCP/UDP          ┌─────────────┐
│   Client    │◄────────────────────────►│   Server    │
│  (LibGDX)   │   KryoNet Protocol       │  (Java)     │
└─────────────┘                          └─────────────┘
      │                                         │
      │                                         │
   Renders                                   Manages
   World &                                   Game State
   Handles                                   & Players
   Input                                         │
      │                                         │
      └──────────── Synchronized ──────────────┘
```

## Module Structure

### 1. Core Module
**Purpose**: Shared game logic and data models  
**Dependencies**: LibGDX core, KryoNet

**Key Components**:
- `MMOGame.java`: Main game lifecycle manager
- `Network.java`: Protocol definitions and message types
- Character system models (CharacterClass, CharacterData, Ability)
- Screen implementations (Home, Login, Register, Character, Game)
- World rendering system

### 2. Desktop Module
**Purpose**: Desktop client launcher  
**Dependencies**: Core module, LWJGL3 backend

**Key Components**:
- `DesktopLauncher.java`: Application entry point with window configuration

### 3. Server Module
**Purpose**: Dedicated game server  
**Dependencies**: Core module, KryoNet

**Key Components**:
- `MMOServer.java`: Server lifecycle, networking, game state management
- Authentication system
- Session management
- World state synchronization
- Monitoring and logging

## Network Architecture

### Protocol: KryoNet
- **TCP Port**: 54555 (Reliable messages)
- **UDP Port**: 54777 (Fast, unreliable updates)

### Message Flow

#### Authentication Flow
```
Client                          Server
  │                               │
  ├─LoginRequest─────────────────►│
  │                               ├─Validate credentials
  │                               ├─Generate token
  │◄─────────────LoginResponse───┤
  │                               │
```

#### Character Creation Flow
```
Client                          Server
  │                               │
  ├─CreateCharacterRequest───────►│
  │                               ├─Validate name
  │                               ├─Create character
  │                               ├─Store in DB
  │◄────CreateCharacterResponse──┤
  │                               │
```

#### Game World Flow (Real-time)
```
Client                          Server
  │                               │
  ├─PlayerMoveRequest (UDP)──────►│
  │                               ├─Update player position
  │                               ├─Broadcast to all
  │◄────────WorldUpdate (UDP)────┤
  │                               │
  ├─ChatMessage (TCP)────────────►│
  │                               ├─Broadcast to all
  │◄────────ChatMessage (TCP)────┤
  │                               │
```

## Data Models

### CharacterClass (Enum)
- WARRIOR, MAGE, ARCHER, ROGUE, CLERIC
- Each with unique:
  - Base stats (HP, MP, ATK, DEF)
  - 4 abilities with descriptions
  - Playstyle characteristics

### CharacterData
```java
- id: long
- name: String
- characterClass: CharacterClass
- level: int
- experience: int
- health, maxHealth: int
- mana, maxMana: int
- attack, defense: int
- x, y: float (position)
- abilities: List<Ability>
```

### PlayerData
```java
- playerId: long
- username: String
- character: CharacterData
- online: boolean
- lastActivity: long
```

### Ability
```java
- name: String
- description: String
- manaCost: int
- cooldown: int
- damage: int
- healing: int
- range: float
- effect: String
```

## Screen Flow

```
HomeScreen
    │
    ├─► LoginScreen ──────────► CharacterSelectionScreen
    │                                      │
    └─► RegisterScreen ─────┐              │
                            │              │
                            └──────────────┤
                                           │
                                           ├─► CharacterCreationScreen
                                           │              │
                                           │              │
                                           │◄─────────────┘
                                           │
                                           └─► GameScreen (Open World)
```

## Game Loop

### Client-Side (60 FPS)
```java
render(delta):
    1. Update input handling
    2. Update player position
    3. Send position to server (if moved)
    4. Receive world updates from server
    5. Update other player positions
    6. Render world
    7. Render players
    8. Render UI
```

### Server-Side (20 Hz = 50ms intervals)
```java
worldUpdateTimer:
    1. Collect all active player states
    2. Create WorldUpdate message
    3. Broadcast to all connected clients (UDP)

messageHandler:
    1. Receive client messages
    2. Process based on type
    3. Update server state
    4. Send responses
```

## World System

### Tile-based Grid
- Tile size: 50x50 pixels
- World size: 100x100 tiles (5000x5000 pixels)
- Grid rendering optimized for camera viewport

### Camera System
- Follows player character
- Only renders visible tiles + 1 tile buffer
- Smooth movement tracking

### Player Rendering
- Local player: Blue circle (20px radius)
- Other players: Red circles (20px radius)
- Name tags above each player
- Level display

## State Management

### Client State
- `authToken`: Session authentication token
- `playerData`: Current player and character data
- `otherPlayers`: Map of visible players
- `chatHistory`: Recent chat messages

### Server State
- `accounts`: Map<username, UserAccount>
- `sessionTokens`: Map<token, username>
- `characters`: Map<characterId, CharacterData>
- `activePlayers`: Map<Connection, PlayerData>

## Security Considerations

### Implemented
- Session token authentication
- Server-side validation for all actions
- Password transmission (plaintext - for demo)
- Character ownership validation
- Input sanitization for character names

### Recommended Enhancements
- Password hashing (bcrypt/argon2)
- SSL/TLS encryption
- Rate limiting
- Input validation hardening
- Anti-cheat measures

## Performance Optimizations

### Client
- Viewport culling for world rendering
- Batch rendering for sprites
- Shape renderer for simple graphics
- Font caching

### Server
- UDP for real-time updates
- TCP for reliable messages
- Concurrent data structures
- Timer-based world updates
- Efficient serialization with Kryo

### Network
- Small message sizes
- Binary protocol (Kryo)
- UDP for position updates
- Connection pooling

## Scalability Considerations

### Current Design
- Single server instance
- In-memory data storage
- ~100 concurrent players (estimated)

### Future Improvements
- Database persistence (PostgreSQL/MongoDB)
- Multiple server instances
- Load balancing
- Region/zone partitioning
- Caching layer (Redis)
- Message queue (RabbitMQ/Kafka)

## Monitoring & Observability

### Server Metrics
- Active player count
- Total accounts
- Character count
- Session count
- Player positions
- Connection events

### Logging
- Authentication events
- Character operations
- Chat messages
- Player actions
- Error tracking

## Technology Stack

| Component      | Technology        | Version |
|---------------|-------------------|---------|
| Game Engine   | LibGDX            | 1.11.0  |
| Networking    | KryoNet           | 2.22.0  |
| Build System  | Gradle            | 7.6     |
| Language      | Java              | 11+     |
| Backend       | LWJGL3            | -       |
| Serialization | Kryo              | -       |

## File Statistics

- **Total Java Files**: 15
- **Total Lines of Code**: ~2,333
- **Core Module**: 13 files
- **Desktop Module**: 1 file
- **Server Module**: 1 file

## Extension Points

### Adding New Features

1. **New Character Class**
   - Add enum to `CharacterClass.java`
   - Define abilities and stats
   - No code changes needed elsewhere

2. **New Ability**
   - Extend ability arrays in `CharacterClass`
   - Implement server-side logic in `MMOServer`

3. **New Screen**
   - Implement `Screen` interface
   - Add navigation from existing screens

4. **New Network Message**
   - Add class to `Network.java`
   - Register with Kryo
   - Implement handler in server/client

5. **Persistence Layer**
   - Add database dependency
   - Create DAO layer
   - Replace in-memory maps
   - Add data migration

## Testing Strategy

### Manual Testing
- Server start/stop
- Client connection
- Authentication flow
- Character CRUD operations
- Multiplayer interaction
- Chat functionality
- Ability usage

### Recommended Automated Tests
- Unit tests for game logic
- Integration tests for networking
- Load testing for server
- UI tests for screens

## Deployment

### Server Deployment
```bash
# Build distribution
./gradlew :server:distZip

# Extract and run
unzip server/build/distributions/server.zip
cd server/bin
./server
```

### Client Distribution
```bash
# Build desktop JAR
./gradlew :desktop:dist

# Distribute desktop/build/libs/desktop-1.0.jar
```

## Conclusion

This architecture provides a solid foundation for an MMO game with:
- Clean separation of concerns
- Extensible design
- Real-time multiplayer capabilities
- Monitoring and observability
- Room for growth and enhancement
