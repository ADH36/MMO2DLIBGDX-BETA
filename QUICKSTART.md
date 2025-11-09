# Quick Start Guide - MMO 2D LibGDX Game

## Prerequisites
- Java 11 or higher installed
- Terminal/Command Prompt access

## Step 1: Build the Project

Open terminal in the project root directory and run:

```bash
./gradlew build
```

This will download dependencies and compile all modules.

## Step 2: Start the Server

In one terminal window, start the game server:

```bash
./gradlew :server:run
```

You should see:
```
==============================================
MMO Server Started
==============================================
TCP Port: 54555
UDP Port: 54777
==============================================
```

Keep this terminal open - this is your game server.

## Step 3: Start the Client

In a **separate** terminal window, start the game client:

```bash
./gradlew :desktop:run
```

A game window will open showing the home screen.

## Step 4: Login or Register

### Option A: Use Test Account
- Click "LOGIN"
- Username: `test`
- Password: `test`
- Press ENTER

### Option B: Create New Account
- Click "REGISTER"
- Enter username, password, and email
- Press TAB to switch fields
- Press ENTER to create account
- You'll be redirected to login

## Step 5: Create or Select Character

### If You Have No Characters:
- Press `C` to create a character
- Type your character name
- Use LEFT/RIGHT arrows to browse classes
- Review abilities and stats
- Press ENTER to create

### If You Have Characters:
- Use UP/DOWN arrows to select
- Press ENTER to enter the world

## Step 6: Play the Game!

### Movement
- Use **WASD** or **Arrow Keys** to move around
- Your character is the **blue** circle
- Other players appear as **red** circles

### Abilities
- Press **1**, **2**, **3**, or **4** to use abilities
- Each class has unique abilities

### Chat
- Press **ENTER** to open chat
- Type your message
- Press **ENTER** to send
- Press **ESC** to cancel

### Exit
- Press **ESC** to return to character selection
- Press **ESC** again to logout

## Testing Multiplayer

To see multiplayer in action:

1. Keep the server running
2. Start a second client in another terminal:
   ```bash
   ./gradlew :desktop:run
   ```
3. Login with a different account or create a new one
4. Create a character and enter the world
5. Move around and see both players on screen!

## Server Commands

While the server is running, you can type:
- `status` - View current server status and active players
- `exit` - Shutdown the server gracefully

## Troubleshooting

### "Could not connect to server"
- Make sure the server is running first
- Check if port 54555 is available

### Build Fails
- Ensure Java 11+ is installed: `java -version`
- Try: `./gradlew clean build`

### Game Window Doesn't Open
- Check if another instance is running
- Verify graphics drivers are up to date

## Character Classes Overview

Choose your playstyle:

| Class   | HP  | MP  | ATK | DEF | Playstyle           |
|---------|-----|-----|-----|-----|---------------------|
| Warrior | 150 | 50  | 25  | 30  | Tank & Melee        |
| Mage    | 80  | 150 | 35  | 10  | High Magic Damage   |
| Archer  | 100 | 80  | 22  | 15  | Ranged Precision    |
| Rogue   | 90  | 70  | 28  | 12  | Stealth & Crits     |
| Cleric  | 110 | 120 | 18  | 20  | Healing & Support   |

## Next Steps

- Explore the open world
- Test all character classes
- Chat with other players
- Try all 4 abilities for each class
- Monitor server statistics

Enjoy your MMO adventure! 🎮
