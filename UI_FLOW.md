# UI/UX Flow - MMO 2D LibGDX Game

## Screen Descriptions and Visual Layout

### 1. Home Screen

```
╔═══════════════════════════════════════════════════════╗
║                                                       ║
║                   MMO 2D GAME                        ║
║                   (Gold Text)                        ║
║                                                       ║
║                                                       ║
║              ┌─────────────────┐                     ║
║              │     LOGIN       │  (Blue Box)         ║
║              └─────────────────┘                     ║
║                                                       ║
║              ┌─────────────────┐                     ║
║              │    REGISTER     │  (Blue Box)         ║
║              └─────────────────┘                     ║
║                                                       ║
║              ┌─────────────────┐                     ║
║              │      EXIT       │  (Blue Box)         ║
║              └─────────────────┘                     ║
║                                                       ║
╚═══════════════════════════════════════════════════════╝
```

**Features:**
- Dark blue gradient background
- Gold title text with large font
- Three interactive buttons with hover effects
- Mouse click to navigate

---

### 2. Login Screen

```
╔═══════════════════════════════════════════════════════╗
║                      LOGIN                           ║
║                   (Gold Text)                        ║
║                                                       ║
║     Username: test_                (Yellow = Active) ║
║                                                       ║
║     Password: ****                 (White = Inactive)║
║                                                       ║
║     Status: [Success/Error Message]                  ║
║                                                       ║
║     TAB: Switch field | ENTER: Login | ESC: Back     ║
║                                                       ║
╚═══════════════════════════════════════════════════════╝
```

**Features:**
- Keyboard-only input (no mouse)
- Active field highlighted in yellow with cursor
- Password masked with asterisks
- Real-time status messages
- Auto-connect to localhost server

---

### 3. Register Screen

```
╔═══════════════════════════════════════════════════════╗
║                    REGISTER                          ║
║                   (Gold Text)                        ║
║                                                       ║
║     Username: newuser_            (Yellow = Active)  ║
║                                                       ║
║     Password: ********            (White = Inactive) ║
║                                                       ║
║     Email: test@test.com          (White = Inactive) ║
║                                                       ║
║     Status: [Success/Error Message]                  ║
║                                                       ║
║     TAB: Switch | ENTER: Register | ESC: Back        ║
║                                                       ║
╚═══════════════════════════════════════════════════════╝
```

**Features:**
- Three input fields
- TAB to cycle through fields
- Email validation
- Success redirects to login after 2s

---

### 4. Character Selection Screen

```
╔═══════════════════════════════════════════════════════╗
║               SELECT CHARACTER                       ║
║                  (Gold Text)                         ║
║                                                       ║
║     → TestWarrior - Level 1 Warrior  (Yellow)       ║
║       MageChar - Level 5 Mage        (White)        ║
║       ArcherJoe - Level 3 Archer     (White)        ║
║                                                       ║
║     Character Details:                               ║
║     HP: 150/150                                      ║
║     MP: 50/50                                        ║
║     Attack: 25                                       ║
║     Defense: 30                                      ║
║                                                       ║
║     UP/DOWN: Select | ENTER: Choose | C: Create      ║
║     ESC: Logout                                      ║
║                                                       ║
╚═══════════════════════════════════════════════════════╝
```

**Features:**
- List of existing characters
- Arrow key navigation
- Selected character highlighted in yellow
- Real-time stats display
- Press C to create new character

---

### 5. Character Creation Screen

```
╔═══════════════════════════════════════════════════════╗
║              CREATE CHARACTER                        ║
║                 (Gold Text)                          ║
║                                                       ║
║     Name: MyWarrior_              (Yellow Input)     ║
║                                                       ║
║     Select Class:                                    ║
║     → Warrior                      (Yellow)          ║
║       Mage                         (Gray)            ║
║       Archer                       (Gray)            ║
║       Rogue                        (Gray)            ║
║       Cleric                       (Gray)            ║
║                                                       ║
║     A mighty melee fighter with high health          ║
║     and defense                    (Cyan Desc)       ║
║                                                       ║
║     Health: 150  |  Mana: 50                        ║
║     Attack: 25   |  Defense: 30                     ║
║                                                       ║
║     Abilities:                     (Gold)            ║
║     1. Slash - A powerful sword attack              ║
║     2. Shield Bash - Stun enemies with shield       ║
║     3. War Cry - Boost team morale                  ║
║     4. Charge - Rush to target                      ║
║                                                       ║
║     LEFT/RIGHT: Class | ENTER: Create | ESC: Back   ║
║                                                       ║
╚═══════════════════════════════════════════════════════╝
```

**Features:**
- Character name input
- Visual class browser (LEFT/RIGHT arrows)
- Real-time class description
- Base stats preview
- All 4 abilities shown with descriptions
- Color-coded information

---

### 6. Game Screen (Open World)

```
╔═══════════════════════════════════════════════════════╗
║ TestWarrior              [World View - Camera Follow]║
║ Level: 1                        ┌─────┬─────┬─────┐ ║
║ HP: 150/150                     │     │ ◉   │     │ ║
║ MP: 50/50                       ├─────┼─────┼─────┤ ║
║                                 │     │  ●  │     │ ║
║ Abilities:                      ├─────┼─────┼─────┤ ║
║ 1. Slash                        │ ◉   │     │ ◉   │ ║
║ 2. Shield Bash                  └─────┴─────┴─────┘ ║
║ 3. War Cry                                          ║
║ 4. Charge                  ● = You (Blue)           ║
║                            ◉ = Other Players (Red)  ║
║                            Grid = World Tiles       ║
║                                                      ║
║ Chat History:                                       ║
║ System: Welcome to the world, TestWarrior!         ║
║ PlayerOne: Hello everyone!                          ║
║ TestWarrior: Hi there!                             ║
║                                                      ║
║ [Chat Input Active]                                 ║
║ Say: Hello world_              (Yellow)             ║
║                                                      ║
║ WASD: Move | 1-4: Abilities | ENTER: Chat          ║
║ ESC: Exit                                           ║
╚═══════════════════════════════════════════════════════╝
```

**Features:**
- Split screen UI:
  - Left: Character stats and abilities
  - Center: World view with tile grid
  - Bottom: Chat system
- Camera follows player
- Other players visible in real-time
- Name tags above all players
- Green grass background
- Grid overlay for world tiles
- Trees/decorations (dark green circles)

---

## Color Scheme

| Element            | Color         | Usage                    |
|-------------------|---------------|--------------------------|
| Background        | Dark Blue     | Main screens             |
| World Background  | Green         | Grass/terrain            |
| Title Text        | Gold          | Screen headers           |
| Active Input      | Yellow        | Selected input field     |
| Inactive Input    | White         | Normal text              |
| Success Message   | Green         | Positive feedback        |
| Error Message     | Red           | Negative feedback        |
| Info Message      | Light Gray    | Instructions             |
| Player (Self)     | Blue          | Local player circle      |
| Player (Others)   | Red           | Remote player circles    |
| Name Tags         | Cyan/White    | Above characters         |
| Button Default    | Dark Blue     | Unselected state         |
| Button Hover      | Lighter Blue  | Mouse over               |
| Abilities         | Gold          | Ability headers          |
| Description       | Cyan          | Class descriptions       |
| World Grid        | Light Green   | Tile boundaries          |
| Decorations       | Dark Green    | Trees, rocks             |

---

## Interaction Flow Chart

```
START
  │
  ├──► Home Screen
  │      │
  │      ├──► Login Screen
  │      │      │
  │      │      └──► Authentication
  │      │            │
  │      └──► Register Screen
  │             │
  │             └──► Create Account ──► Redirect to Login
  │                                          │
  └─────────────────────────────────────────┘
                                             │
                                             ▼
                                    Character Selection
                                             │
                                ┌────────────┴────────────┐
                                │                         │
                                ▼                         ▼
                       Character Creation          Select Existing
                                │                         │
                                └────────┬────────────────┘
                                         │
                                         ▼
                                    Game Screen
                                   (Open World)
                                         │
                                         ├──► Move Around
                                         ├──► Use Abilities
                                         ├──► Chat
                                         └──► See Other Players
                                              │
                                              ▼
                                         ESC to Exit
                                              │
                                              ▼
                                    Character Selection
```

---

## Animation & Effects

### Implemented Visual Feedback

1. **Button Hover**: Color changes when mouse hovers
2. **Cursor Blink**: Active input fields show blinking cursor (_)
3. **Player Movement**: Smooth position updates (60 FPS)
4. **Camera Follow**: Smooth camera tracking of player
5. **Message Fading**: Chat messages persist in history
6. **Status Colors**: Green for success, Red for errors, Yellow for info

### Future Enhancements

- Particle effects for abilities
- Attack animations
- Damage numbers floating
- Health bar animations
- XP gain animations
- Level up effects
- Character sprite animations
- Weather effects
- Day/night cycle

---

## Responsive Design

### Window Size: 1280x720 (Default)

All UI elements scale appropriately:
- Centered buttons
- Responsive text positioning
- Camera viewport adjusts
- Grid rendering optimizes for visible area

### Keyboard Shortcuts

| Screen             | Controls                              |
|-------------------|---------------------------------------|
| Home              | Mouse click only                      |
| Login             | TAB, ENTER, ESC, A-Z, 0-9            |
| Register          | TAB, ENTER, ESC, A-Z, 0-9, @, .      |
| Char Select       | UP, DOWN, ENTER, C, ESC              |
| Char Create       | LEFT, RIGHT, ENTER, ESC, A-Z, 0-9    |
| Game              | WASD, 1-4, ENTER, ESC, A-Z, 0-9      |

---

## Accessibility Features

- Large, readable fonts (1.5x scale for main text)
- Clear visual feedback for all actions
- Keyboard-only navigation option
- High contrast color scheme
- Status messages for all operations
- Consistent navigation patterns

---

## Visual Hierarchy

1. **Primary**: Screen title (Gold, largest)
2. **Secondary**: Active input/selection (Yellow)
3. **Tertiary**: Regular content (White)
4. **Quaternary**: Instructions (Light Gray)
5. **Feedback**: Status messages (Green/Red/Yellow)

This creates a clear path for user attention and intuitive navigation through the game interface.
