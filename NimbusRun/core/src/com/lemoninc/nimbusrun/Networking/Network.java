package com.lemoninc.nimbusrun.Networking;

/*********************************
 * FILENAME : Network.java
 * DESCRIPTION :
 * FUNCTIONS :
 *       void    registerClasses(EndPoint endPoint)
 *       private static void logInfo(String string)
 *
 * CLASSES :
 *      static public class Login {}
 *      static public class PlayerJoinLeave{}
 *      static public class MovementState{}
 *      static public class GameRoomFull {}
 *      static public class Ready {}
 *      static public class GameReady{}
 *      static public class MapDataPacket {}
 *      static public class PlayerAttack {}
 *
 * NOTES :
 * LAST UPDATED: 24/4/2016 15:24
 *
 * ********************************/

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.minlog.Log;

public class Network {

    public static int PORT = 12080;
    public static int PORTUDP = 12082;
    /**
     * the classes that are going to be sent over the network must be registered for both server and client
     *
     */
    public static void registerClasses(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();

        kryo.register(Network.Login.class);
        kryo.register(Network.PlayerJoinLeave.class);
        kryo.register(Network.MovementState.class);
        kryo.register(Vector2.class);
        kryo.register(GameRoomFull.class);
        kryo.register(Ready.class);
        kryo.register(GameReady.class);
        kryo.register(MapDataPacket.class);
        kryo.register(int[].class);
        kryo.register(PlayerAttack.class);

    }

    static public class Login {
        public String name;

        public Login() {
        }

        public Login(String name) {
            this.name = name;

            Network.logInfo("Login initialised by Client "+name);
        }
    }

    /**
     * message packet that describes the player to be added to the GameMap.
     * the 'joined' boolean variable indicates if the player is to be added to or deleted from the GameMap
     *
     */
    static public class PlayerJoinLeave {
        public int playerId;
        public String name;
        public boolean hasJoined;
        public float initial_x, initial_y;

        public PlayerJoinLeave() {}

        public PlayerJoinLeave(int playerId, String playerName, boolean joined, float initial_x, float initial_y) {
            this.playerId = playerId;
            name = playerName;
            hasJoined = joined;
            this.initial_x = initial_x;
            this.initial_y = initial_y;

            Network.logInfo("PlayerJoinLeave initialised by Server for Client "+playerId+" "+playerName);
        }
    }

    /**
     * Contains the linear Velocity of the Player
     */
    static public class MovementState {

        public int playerId;
        public Vector2 position;
        public Vector2 linearVelocity;

        public MovementState() {}

        public MovementState(int id, Vector2 position, Vector2 linearVelocity) {

            this.position = position;
            this.playerId = id;
            this.linearVelocity = linearVelocity;
        }
    }

    private static void logInfo(String string) {
        Log.info("[Network]: " + string);
    }

    static public class GameRoomFull {

        public GameRoomFull() {}

    }

    static public class Ready {

        public int charactername;
        public int playerId;

        public Ready() {}

        public Ready(int charactername) {
            this.charactername = charactername;
        }

        public void setPlayerId(int playerId) {
            this.playerId = playerId;
        }
    }

    static public class GameReady {

        public GameReady() {}
    }

    static public class MapDataPacket {
        public int[] mapData;

        public MapDataPacket() {}

        public MapDataPacket(int[] mapData) {
            this.mapData = mapData;
        }
    }

    static public class PlayerAttack {
        public int id;
        public int character;

        public PlayerAttack() {}

        public PlayerAttack(int id, int character) {
            this.id = id;
            this.character = character;
        }
    }
}