package com.mmo.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.mmo.models.CharacterClass;
import com.mmo.models.CharacterData;
import com.mmo.models.PlayerData;
import com.mmo.network.Network;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * MMO Game Server - handles all game logic, authentication, and world state
 */
public class MMOServer {
    private Server server;
    private Map<String, UserAccount> accounts;
    private Map<String, String> sessionTokens; // token -> username
    private Map<Long, CharacterData> characters;
    private Map<Connection, PlayerData> activePlayers;
    private AtomicLong characterIdCounter;
    private AtomicLong playerIdCounter;
    
    private static final int WORLD_UPDATE_INTERVAL = 50; // ms
    private Timer worldUpdateTimer;
    
    public MMOServer() {
        server = new Server(16384, 8192);
        accounts = new ConcurrentHashMap<>();
        sessionTokens = new ConcurrentHashMap<>();
        characters = new ConcurrentHashMap<>();
        activePlayers = new ConcurrentHashMap<>();
        characterIdCounter = new AtomicLong(1000);
        playerIdCounter = new AtomicLong(1);
        
        Network.register(server);
        setupListeners();
        createDefaultAccounts();
    }
    
    private void createDefaultAccounts() {
        // Create a default test account
        UserAccount testAccount = new UserAccount("test", "test", "test@test.com");
        accounts.put("test", testAccount);
        
        // Create a default character for testing
        CharacterData testChar = new CharacterData(1001, "TestWarrior", CharacterClass.WARRIOR);
        characters.put(1001L, testChar);
        testAccount.characterIds.add(1001L);
        
        System.out.println("Created default test account (username: test, password: test)");
    }
    
    private void setupListeners() {
        server.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                System.out.println("Client connected: " + connection.getID());
            }
            
            @Override
            public void disconnected(Connection connection) {
                System.out.println("Client disconnected: " + connection.getID());
                handleDisconnect(connection);
            }
            
            @Override
            public void received(Connection connection, Object object) {
                handleMessage(connection, object);
            }
        });
    }
    
    private void handleMessage(Connection connection, Object object) {
        if (object instanceof Network.LoginRequest) {
            handleLogin(connection, (Network.LoginRequest) object);
        } else if (object instanceof Network.RegisterRequest) {
            handleRegister(connection, (Network.RegisterRequest) object);
        } else if (object instanceof Network.CharacterListRequest) {
            handleCharacterList(connection, (Network.CharacterListRequest) object);
        } else if (object instanceof Network.CreateCharacterRequest) {
            handleCreateCharacter(connection, (Network.CreateCharacterRequest) object);
        } else if (object instanceof Network.SelectCharacterRequest) {
            handleSelectCharacter(connection, (Network.SelectCharacterRequest) object);
        } else if (object instanceof Network.PlayerMoveRequest) {
            handlePlayerMove(connection, (Network.PlayerMoveRequest) object);
        } else if (object instanceof Network.ChatMessage) {
            handleChatMessage(connection, (Network.ChatMessage) object);
        } else if (object instanceof Network.UseAbilityRequest) {
            handleUseAbility(connection, (Network.UseAbilityRequest) object);
        }
    }
    
    private void handleLogin(Connection connection, Network.LoginRequest request) {
        Network.LoginResponse response = new Network.LoginResponse();
        
        UserAccount account = accounts.get(request.username);
        if (account != null && account.password.equals(request.password)) {
            String token = UUID.randomUUID().toString();
            sessionTokens.put(token, request.username);
            
            response.success = true;
            response.message = "Login successful";
            response.token = token;
            
            System.out.println("User logged in: " + request.username);
        } else {
            response.success = false;
            response.message = "Invalid username or password";
        }
        
        connection.sendTCP(response);
    }
    
    private void handleRegister(Connection connection, Network.RegisterRequest request) {
        Network.RegisterResponse response = new Network.RegisterResponse();
        
        if (accounts.containsKey(request.username)) {
            response.success = false;
            response.message = "Username already exists";
        } else if (request.username.length() < 3) {
            response.success = false;
            response.message = "Username must be at least 3 characters";
        } else if (request.password.length() < 3) {
            response.success = false;
            response.message = "Password must be at least 3 characters";
        } else {
            UserAccount newAccount = new UserAccount(request.username, request.password, request.email);
            accounts.put(request.username, newAccount);
            
            response.success = true;
            response.message = "Account created successfully";
            
            System.out.println("New account registered: " + request.username);
        }
        
        connection.sendTCP(response);
    }
    
    private void handleCharacterList(Connection connection, Network.CharacterListRequest request) {
        Network.CharacterListResponse response = new Network.CharacterListResponse();
        
        String username = sessionTokens.get(request.token);
        if (username != null) {
            UserAccount account = accounts.get(username);
            if (account != null) {
                List<CharacterData> charList = new ArrayList<>();
                for (Long charId : account.characterIds) {
                    CharacterData character = characters.get(charId);
                    if (character != null) {
                        charList.add(character);
                    }
                }
                
                response.success = true;
                response.characters = charList.toArray(new CharacterData[0]);
                
                System.out.println("Sent character list to " + username + " (" + charList.size() + " characters)");
            }
        } else {
            response.success = false;
            response.characters = new CharacterData[0];
        }
        
        connection.sendTCP(response);
    }
    
    private void handleCreateCharacter(Connection connection, Network.CreateCharacterRequest request) {
        Network.CreateCharacterResponse response = new Network.CreateCharacterResponse();
        
        String username = sessionTokens.get(request.token);
        if (username != null) {
            UserAccount account = accounts.get(username);
            if (account != null) {
                if (account.characterIds.size() >= 5) {
                    response.success = false;
                    response.message = "Maximum 5 characters per account";
                } else {
                    long charId = characterIdCounter.incrementAndGet();
                    CharacterData newCharacter = new CharacterData(charId, request.characterName, request.characterClass);
                    
                    characters.put(charId, newCharacter);
                    account.characterIds.add(charId);
                    
                    response.success = true;
                    response.message = "Character created successfully";
                    response.character = newCharacter;
                    
                    System.out.println("Character created: " + request.characterName + " for user " + username);
                }
            }
        } else {
            response.success = false;
            response.message = "Invalid session";
        }
        
        connection.sendTCP(response);
    }
    
    private void handleSelectCharacter(Connection connection, Network.SelectCharacterRequest request) {
        Network.SelectCharacterResponse response = new Network.SelectCharacterResponse();
        
        String username = sessionTokens.get(request.token);
        if (username != null) {
            CharacterData character = characters.get(request.characterId);
            if (character != null) {
                long playerId = playerIdCounter.incrementAndGet();
                PlayerData playerData = new PlayerData(playerId, username, character);
                
                activePlayers.put(connection, playerData);
                
                response.success = true;
                response.message = "Character selected";
                response.playerData = playerData;
                
                System.out.println("Player " + username + " entered world as " + character.getName());
            } else {
                response.success = false;
                response.message = "Character not found";
            }
        } else {
            response.success = false;
            response.message = "Invalid session";
        }
        
        connection.sendTCP(response);
    }
    
    private void handlePlayerMove(Connection connection, Network.PlayerMoveRequest request) {
        PlayerData playerData = activePlayers.get(connection);
        if (playerData != null) {
            playerData.getCharacter().setX(request.x);
            playerData.getCharacter().setY(request.y);
            playerData.updateActivity();
        }
    }
    
    private void handleChatMessage(Connection connection, Network.ChatMessage message) {
        PlayerData playerData = activePlayers.get(connection);
        if (playerData != null) {
            System.out.println("Chat from " + message.sender + ": " + message.message);
            
            // Broadcast to all connected players
            for (Connection conn : activePlayers.keySet()) {
                conn.sendTCP(message);
            }
        }
    }
    
    private void handleUseAbility(Connection connection, Network.UseAbilityRequest request) {
        Network.UseAbilityResponse response = new Network.UseAbilityResponse();
        
        PlayerData playerData = activePlayers.get(connection);
        if (playerData != null) {
            if (request.abilityIndex >= 0 && 
                request.abilityIndex < playerData.getCharacter().getAbilities().size()) {
                
                response.success = true;
                response.message = "Ability used successfully";
                
                System.out.println(playerData.getCharacter().getName() + " used ability " + request.abilityIndex);
            } else {
                response.success = false;
                response.message = "Invalid ability index";
            }
        } else {
            response.success = false;
            response.message = "Player not found";
        }
        
        connection.sendTCP(response);
    }
    
    private void handleDisconnect(Connection connection) {
        PlayerData playerData = activePlayers.remove(connection);
        if (playerData != null) {
            System.out.println("Player " + playerData.getCharacter().getName() + " left the world");
        }
    }
    
    private void startWorldUpdates() {
        worldUpdateTimer = new Timer();
        worldUpdateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                broadcastWorldUpdate();
            }
        }, 0, WORLD_UPDATE_INTERVAL);
    }
    
    private void broadcastWorldUpdate() {
        if (activePlayers.isEmpty()) return;
        
        Network.WorldUpdate update = new Network.WorldUpdate();
        List<Network.PlayerUpdate> playerUpdates = new ArrayList<>();
        
        for (PlayerData playerData : activePlayers.values()) {
            Network.PlayerUpdate playerUpdate = new Network.PlayerUpdate();
            playerUpdate.playerId = playerData.getPlayerId();
            playerUpdate.x = playerData.getCharacter().getX();
            playerUpdate.y = playerData.getCharacter().getY();
            playerUpdate.name = playerData.getCharacter().getName();
            playerUpdate.level = playerData.getCharacter().getLevel();
            playerUpdates.add(playerUpdate);
        }
        
        update.players = playerUpdates.toArray(new Network.PlayerUpdate[0]);
        
        // Broadcast to all players
        for (Connection connection : activePlayers.keySet()) {
            connection.sendUDP(update);
        }
    }
    
    public void start() {
        try {
            server.bind(Network.TCP_PORT, Network.UDP_PORT);
            server.start();
            
            System.out.println("==============================================");
            System.out.println("MMO Server Started");
            System.out.println("==============================================");
            System.out.println("TCP Port: " + Network.TCP_PORT);
            System.out.println("UDP Port: " + Network.UDP_PORT);
            System.out.println("==============================================");
            System.out.println("Monitoring:");
            System.out.println("- Registered accounts: " + accounts.size());
            System.out.println("- Active players: " + activePlayers.size());
            System.out.println("==============================================");
            
            startWorldUpdates();
            startMonitoring();
            
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void startMonitoring() {
        Timer monitorTimer = new Timer();
        monitorTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                printServerStatus();
            }
        }, 10000, 10000); // Every 10 seconds
    }
    
    private void printServerStatus() {
        System.out.println("\n=== Server Status ===");
        System.out.println("Active Players: " + activePlayers.size());
        System.out.println("Total Accounts: " + accounts.size());
        System.out.println("Total Characters: " + characters.size());
        System.out.println("Active Sessions: " + sessionTokens.size());
        
        if (!activePlayers.isEmpty()) {
            System.out.println("\nOnline Players:");
            for (PlayerData player : activePlayers.values()) {
                System.out.println("  - " + player.getCharacter().getName() + 
                                 " (Level " + player.getCharacter().getLevel() + 
                                 ") at (" + player.getCharacter().getX() + ", " + 
                                 player.getCharacter().getY() + ")");
            }
        }
        System.out.println("====================\n");
    }
    
    public void stop() {
        if (worldUpdateTimer != null) {
            worldUpdateTimer.cancel();
        }
        server.stop();
        System.out.println("Server stopped");
    }
    
    public static void main(String[] args) {
        MMOServer server = new MMOServer();
        server.start();
        
        // Keep server running
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nType 'exit' to stop the server");
        while (true) {
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("exit")) {
                server.stop();
                scanner.close();
                System.exit(0);
                break;
            } else if (input.equalsIgnoreCase("status")) {
                server.printServerStatus();
            }
        }
    }
    
    // User account class
    private static class UserAccount {
        String username;
        String password;
        String email;
        List<Long> characterIds;
        long createdAt;
        
        UserAccount(String username, String password, String email) {
            this.username = username;
            this.password = password;
            this.email = email;
            this.characterIds = new ArrayList<>();
            this.createdAt = System.currentTimeMillis();
        }
    }
}
