package com.lemoninc.nimbusrun.Sprites;

/*********************************
 * FILENAME : GameMap.java
 * DESCRIPTION : Makes the map where game is hosted and allows interaction between players
 * PUBLIC FUNCTIONS :
 *       --INITIALISATION--
 *       GameMap(client)
 *       GameMap(server)
 *       void initCommon()
 *       void initPlayers()
 *       void passHUD()
 *       HUD getHud()

 *      --SETUP ENVIRONMENT--
 *      void setMapData()
 *      void createEnv()
 *      void setFinishLine()
 *      void makePlatformsBG()
 *      TextureAtlas getImg()
 *      String getCharacterSkil()
 *      String getCharacterType()

 *      --NETWORK & INTERACTION--
 *      void onConnect()
 *      void clientSendMessage()
 *      boolean onPlayerAttack()
 *      void addPlayer (synchronised)()
 *      void removePlayer (synchronised)()
 *      MapDataPacket getMapDataPacket()()
 *      void declareCharacter()
 *      void setCharacter()
 *      boolean allDummyReady()
 *      void playerMoved (synchronised)()
 *      void playerAttacked()
 *      void stunExceptId()
 *      void poisonExceptId()
 *      void reverseExceptId()
 *      void terrorExceptId()
 *      void flashExceptId()
 *      void confuseExceptId()
 *      void boolean isFlashed()
*
 *      --getMethods()--
 *      Player getPlayerById
 *      DummyPlayer getDummyById
 *      Map<Integer,Player> getPlayers()
 *      World getWorld()
 *      boolean getGameMapReadyForHUD
 *      Viewport getGameport()
 *      boolean getAllFinished
 *
 *      --libgdx methods--
 *      void update (synchronised) ()
 *      void render (synchronised) ()
 *      void resize()
 *      void logInfo()
 *      void dispose()
 *      void onDisconnect()


 * NOTES :
 * LAST UPDATED: 23/4/2016 08:20
 *
 * ********************************/

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.minlog.Log;
import com.lemoninc.nimbusrun.Networking.Client.TapTapClient;
import com.lemoninc.nimbusrun.Networking.Network;
import com.lemoninc.nimbusrun.Networking.Server.TapTapServer;
import com.lemoninc.nimbusrun.NimbusRun;
import com.lemoninc.nimbusrun.scenes.HUD;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameMap{

    private TapTapClient client; // only if I'm the client
    private TapTapServer server; // only if I'm internal to the server

    private boolean isClient;

    private Map<Integer, Player> players = new ConcurrentHashMap<Integer, Player>(); //playerId, Player
    private Map<Integer, DummyPlayer> dummyPlayers = new ConcurrentHashMap<Integer, DummyPlayer>(); //playerId, Player
    private boolean gameMapReadyForHUD;
    private HUD hud;

    private OrthographicCamera gamecam;
    private Viewport gameport;

    private SpriteBatch batch;
    private TextureAtlas img;
    private Texture bgTexture;
    private Sprite bgSprite;
    private float bgHeight, bgWidth, bgStartX, bgStartY;

    private Texture bgTextureFlat, bgTextureMountain, bgTexturePit, bgTexturePlateau;
    private List<Sprite> bgPlatformSprites;

    private Sprite finishLine;

    private World world;
    private Box2DDebugRenderer b2dr;
    private Ground ground;
    private Ceiling ceiling;
    private StartWall startWall;
    private EndWall endWall;
    private int[] mapData;
    public static final int NUMPLATFORMS = 8;

    public Player playerLocal;
    private DummyPlayer dummyLocal;

    public int noPowerUps;
    public String globalStatus;

    public float powerUpDistance;

    long timeStamp;



    /*//////////////////////////////
     //                           //
     // Initialise client, server,//
     // world, camera, box2d,     //
     // debugRenderer             //
     //                           //
     /////////////////////////////*/

    public GameMap(TapTapClient client, int[] mapData) {
        // called inside TapTapClient
        this.client = client;
        this.isClient = true;
        this.mapData = mapData;

        //for HUD
        noPowerUps=0;
        globalStatus = "";

        float worldLength = 300f;
        powerUpDistance=worldLength/4; //distance needed to cover to have a power up

        //instantiate HUD, GameSounds, BitmapFont, Camera, SpriteBatch ...
        initCommon();
        gameMapReadyForHUD = false;

        //set starting pos of bgSprites after setting cam
        bgStartX = -gameport.getWorldWidth() * 1.5f;
        bgStartY = -gameport.getWorldHeight() * 1.5f;
        batch = new SpriteBatch();

        // initialise all background sprites
        bgTexture = new Texture("4_PlayScreen/bg_dark.png");
        bgTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        bgSprite = new Sprite(new TextureRegion(bgTexture, bgTexture.getWidth()*19, bgTexture.getHeight()*3));
        bgWidth = bgTexture.getWidth() / NimbusRun.PPM * 1.5f * 19;
        bgHeight = bgTexture.getHeight() / NimbusRun.PPM * 1.5f * 3;
        bgSprite.setX(bgStartX);
        bgSprite.setY(bgStartY);
        bgSprite.setSize(bgWidth, bgHeight);

        bgTextureFlat = new Texture("4_PlayScreen/platform_flat.png");
        bgTexturePlateau = new Texture("4_PlayScreen/platform_plateau.png");
        bgTextureMountain = new Texture("4_PlayScreen/platform_mountain.png");
        bgTexturePit = new Texture("4_PlayScreen/platform_pit.png");

        bgPlatformSprites = new ArrayList<Sprite>();

        finishLine = new Sprite(new Texture("4_PlayScreen/finishLine.png"));

    }

    public GameMap(TapTapServer server, int[] mapData) {
        // called inside TapTapServer
        this.server = server;
        this.isClient = false;
        this.mapData = mapData;

        initCommon();

        Gdx.app.log("GDX GameMap", "GameMap instantiated in Server");
    }

    private void initCommon(){
        world = new World(new Vector2(0, -10), true); //box2d world with gravity
        b2dr = new Box2DDebugRenderer();

        gamecam = new OrthographicCamera();
        gameport = new FitViewport(NimbusRun.V_WIDTH * 2f / NimbusRun.PPM, NimbusRun.V_HEIGHT * 2f / NimbusRun.PPM, gamecam);
    }

    public void initPlayers() {
        //create Players from dummyPlayers
        for (Map.Entry<Integer, DummyPlayer> playerEntry : dummyPlayers.entrySet()) {
            DummyPlayer curPlayer = playerEntry.getValue();
            if (curPlayer.isLocal) {
                playerLocal = new Player(this, getImg(curPlayer.character), curPlayer.x, curPlayer.y, true, curPlayer.character);
                playerLocal.setId(curPlayer.playerID);
                playerLocal.setName(curPlayer.playerName);
                players.put(curPlayer.playerID, playerLocal);
            }
            else {
                Player newPlayer= new Player(this, getImg(curPlayer.character), curPlayer.x, curPlayer.y, false, curPlayer.character);
                newPlayer.setId(curPlayer.playerID);
                newPlayer.setName(curPlayer.playerName);
                players.put(curPlayer.playerID, newPlayer);
            }

        }
        gameMapReadyForHUD = true;
    }

    public void passHUD(HUD hud){
        this.hud = hud;
    }
    public HUD getHud(){
        return this.hud;
    }



    /*/////////////////////////////
     //                          //
     //     Set up Environment   //
     //                          //
     ////////////////////////////*/

    public void setMapData(int[] mapData) {
        // HAS TO BE CALLED BEFORE GROUND is instantiated
        this.mapData = mapData;
    }

    public void createEnv() {
        //add these sprites to the world
        ground = new Ground(this, mapData, NUMPLATFORMS);
        ceiling = new Ceiling(this);
        startWall = new StartWall(this);
        endWall = new EndWall(this);
        setFinishLine();
    }

    private void setFinishLine(){
        finishLine.setPosition(gameport.getWorldWidth() * 18.5f, 0);
        finishLine.setSize(finishLine.getWidth() * 0.01f, finishLine.getHeight() * 0.01f);
    }

    public void makePlatformsBG(float startX, float endX, char type){
        Sprite sprite;
        float width = endX-startX;
        float height;

        switch(type){
            case 'F': sprite = new Sprite(bgTextureFlat);
                height = width/1000*390;
                sprite.setPosition(startX, -height);
                sprite.setSize(width, height);
                bgPlatformSprites.add(sprite); break;

            case 'P': sprite = new Sprite(bgTexturePlateau);
                height = width/1000*789;
                sprite.setPosition(startX, -height*0.7366f);
                sprite.setSize(width, height);
                bgPlatformSprites.add(sprite); break;

            case 'M': sprite = new Sprite(bgTextureMountain);
                height = width/1000*869;
                sprite.setPosition(startX, -height*0.473f);
                sprite.setSize(width, height);
                bgPlatformSprites.add(sprite); break;

            case 'T': sprite = new Sprite(bgTexturePit);
                height = width/1000*605;
                sprite.setPosition(startX, -height);
                sprite.setSize(width, height);
                bgPlatformSprites.add(sprite); break;
        }
    }

    private TextureAtlas getImg(int character) {
        // Load up all sprites into spriteMap from textureAtlas
        switch(character){
            // 1. LAUGHING BUDDHA
            // 2. SHESHNAH WITH KRISHNA
            // 3. NINE-TAILED FOX
            // 4. KAPPA
            // 5. PONTIANAK
            // 6. MADAME WHITE SNAKE
            case 1: img = new TextureAtlas(Gdx.files.internal("Spritesheets/LBspritesheet.atlas")); break;
            case 2: img = new TextureAtlas(Gdx.files.internal("Spritesheets/SKspritesheet.atlas")); break;
            case 3: img = new TextureAtlas(Gdx.files.internal("Spritesheets/FXspritesheet.atlas")); break;
            case 4: img = new TextureAtlas(Gdx.files.internal("Spritesheets/KPspritesheet.atlas")); break;
            case 5: img = new TextureAtlas(Gdx.files.internal("Spritesheets/PTspritesheet.atlas")); break;
            case 6: img = new TextureAtlas(Gdx.files.internal("Spritesheets/MWSspritesheet.atlas")); break;
            default: img = new TextureAtlas(Gdx.files.internal("Spritesheets/PTspritesheet.atlas")); break;
        }
        return img;
    }

    private String getCharacterSkil(int character) {

        String characterSkill;

        switch(character){
            // 1. LAUGHING BUDDHA
            // 2. SHESHNAH WITH KRISHNA
            // 3. NINE-TAILED FOX
            // 4. KAPPA
            // 5. PONTIANAK
            // 6. MADAME WHITE SNAKE
            case 1: characterSkill = "stunned"; break;
            case 2: characterSkill = "flashed"; break;
            case 3: characterSkill = "charmed"; break;
            case 4: characterSkill = "tide-shifted"; break;
            case 5: characterSkill = "terror'd"; break;
            case 6: characterSkill = "poisoned"; break;
            default: characterSkill = "stunned"; break;
        }

        return characterSkill;
    }

    private String getCharacterType(int character) {
        String type;
        switch(character){
            // 1. LAUGHING BUDDHA
            // 2. SHESHNAH WITH KRISHNA
            // 3. NINE-TAILED FOX
            // 4. KAPPA
            // 5. PONTIANAK
            // 6. MADAME WHITE SNAKE
            case 1: type = "Buddha"; break;
            case 2: type = "Krishna"; break;
            case 3: type = "Foxy"; break;
            case 4: type = "Kappa"; break;
            case 5: type = "Pontianak"; break;
            case 6: type = "Madame"; break;
            default: type = "Buddha"; break;
        }
        return type;
    }



    /*/////////////////////////////
     //                          //
     //   Network & Interaction  //
     //   send packets to track  //
     //   movements and attacks  //
     //                          //
     ////////////////////////////*/

    public void onConnect(Network.PlayerJoinLeave msg) {
        // Client receives PlayerJoinLeave from server containing player ID, name, initial x and y
        if (this.dummyLocal == null) {
            dummyLocal = new DummyPlayer(client.id, msg.name, msg.initial_x, msg.initial_y, true);
            dummyPlayers.put(dummyLocal.playerID, dummyLocal);
            //Gdx.app.log("GDX GameMap", "local player created at "+msg.initial_x+" "+msg.initial_y);
        } else {
            //Gdx.app.log("GDX GameMap onConnect", "setNetworkClient called twice");
        }
    }

    public void clientSendMessage(Object msg) {
        client.sendMessage(msg);
    }

    public boolean onPlayerAttack(Network.PlayerAttack msg) {

        Player player = getPlayerById(msg.id);

        if (player != null) {
            if (player.attack()) {
                if (client != null) { //only for client
                    timeStamp = System.currentTimeMillis();
                    globalStatus = getCharacterType(msg.character)+" "+player.getName()+" "+getCharacterSkil(msg.character)+" you";
                    playerLocal.attackSoundPlay(msg.character); // playerlocal is called just  to borrow its method
                }
                return true;
            } else {
                return false;
            }
        }
        else {
            //Gdx.app.log("GDX GameMap onPlayerAttack", "player was null");
        }
        return true;
    }

    /**
     * This method is only called in Character Selection screen
     */
    public void addPlayer(Network.PlayerJoinLeave msg) {
        //create new player from msg
        DummyPlayer newDummy = new DummyPlayer(msg.playerId, msg.name, msg.initial_x, msg.initial_y, false);
        dummyPlayers.put(newDummy.playerID, newDummy);
    }

    /**
     * Destroy the disconnected player's body from world
     * Remove disconnected player from players
     * Can be called from both CS screen and PlayScreen
     *
     * @param msg
     */
    public void removePlayer(Network.PlayerJoinLeave msg) {
        dummyPlayers.remove(msg.playerId);

        if (players.get(msg.playerId) != null) {
            world.destroyBody(players.get(msg.playerId).b2body);
            players.remove(msg.playerId);
        }
    }


    public Network.MapDataPacket getMapDataPacket() {
        return new Network.MapDataPacket(mapData);
    }

    public void declareCharacter(int charactername) {
        dummyLocal.setCharacter(charactername);
    }

    public void setCharacter(int playerId, int charactername) {
        dummyPlayers.get(playerId).setCharacter(charactername);
    }

    public boolean allDummyReady() {
        for (Map.Entry<Integer, DummyPlayer> playerEntry : dummyPlayers.entrySet()) {
            DummyPlayer curPlayer = playerEntry.getValue();
            if (!curPlayer.isReady()) {
                Gdx.app.log("GDX GameMap allDummyReady", "Hi from "+curPlayer.playerName);
                return false;
            }
        }
        return true;
    }

    public void playerMoved(Network.MovementState msg) {
        Player player = players.get(msg.playerId);
        if (player != null) {
            //Gdx.app.log("GDX GameMap", "Player "+player.getName()+" moved");
            Gdx.app.log("GDX GameMap", "Player " + player.getName() + " moved");
            synchronized (this) {
                player.setMovementState(msg);
            }
        }
    }

    public void playerAttacked(Network.PlayerAttack msg) {

        //apply effect of the attack on every other players
        switch(msg.character) {
            case 1: stunExceptId(msg.id); break; // 1. LAUGHING BUDDHA
            case 2: flashExceptId(msg.id); break; // 2. SHESHNAH WITH KRISHNA
            case 3: confuseExceptId(msg.id); break; // 3. NINE-TAILED FOX
            case 4: reverseExceptId(msg.id); break; // 4. KAPPA
            case 5: terrorExceptId(msg.id); break; // 5. PONTIANAK
            case 6: poisonExceptId(msg.id); break; // 6. MADAME WHITE SNAKE
            default: stunExceptId(msg.id); break;
        }
    }

    public void stunExceptId(int id) {
        for (Map.Entry<Integer, Player> playerEntry : players.entrySet()) {
            Player curPlayer = playerEntry.getValue();
            if (curPlayer.getId() != id) {
                curPlayer.stun();
            }
        }
    }

    public void poisonExceptId(int id) {
        for (Map.Entry<Integer, Player> playerEntry : players.entrySet()) {
            Player curPlayer = playerEntry.getValue();
            if (curPlayer.getId() != id) {
                curPlayer.poison();
            }
        }
    }

    public void reverseExceptId(int id) {
        for (Map.Entry<Integer, Player> playerEntry : players.entrySet()) {
            Player curPlayer = playerEntry.getValue();
            if (curPlayer.getId() != id) {
                curPlayer.reverse();
            }
        }
    }

    public void terrorExceptId(int id) {
        for (Map.Entry<Integer, Player> playerEntry : players.entrySet()) {
            Player curPlayer = playerEntry.getValue();
            if (curPlayer.getId() != id) {
                curPlayer.terror();
            }
        }
    }

    public void flashExceptId(int id) {
        for (Map.Entry<Integer, Player> playerEntry : players.entrySet()) {
            Player curPlayer = playerEntry.getValue();
            if (curPlayer.getId() != id) {
                Gdx.app.log("GDX GameMap flashExceptId", "Flash on curPlayer "+curPlayer.getName());
                curPlayer.flash(); //curPlayer does not have flash sound file
            }
        }
    }

    public void confuseExceptId(int id) {
        for (Map.Entry<Integer, Player> playerEntry : players.entrySet()) {
            Player curPlayer = playerEntry.getValue();
            if (curPlayer.getId() != id) {
                curPlayer.confuse();
            }
        }
    }

    public boolean isFlashed() {
        return playerLocal.isFlashed();
    }



     /*//////////////////////////
     //                        //
     //      getMethods()      //
     //                        //
     ////////////////////////////*/


    public Player getPlayerById(int id){
        return players.get(id);
    }
    public DummyPlayer getDummyById(int id) {
        return dummyPlayers.get(id);
    }
    public Map<Integer, Player> getPlayers(){
        return players;
    }

    public World getWorld(){
        return this.world;
    }

    public boolean getGameMapReadyForHUD() { return gameMapReadyForHUD; }

    public Viewport getGameport() { return this.gameport; }

    public boolean getAllFinished(){
        int maxPlayers = players.size();
        for (Map.Entry<Integer, Player> playerEntry : players.entrySet()) {
            Player curPlayer = playerEntry.getValue();
            if (client != null && curPlayer != null) {
                if (curPlayer.isFinished()){
                    maxPlayers--;
                }
            }
        }
        if (maxPlayers == 0){
            return true;
        } else {
            return false;
        }
    }



    /*///////////////////////////
     //                        //
     //    libGDX methods()    //
     //      only called on    //
     //      client's side     //
     //                        //
     //////////////////////////*/

    public synchronized void update(float delta) {
        //If client is created and local player has spawned
        if (client != null && playerLocal != null) {
            if (playerLocal.handleInput()) { // (arrow key has been pressed by player)
                client.sendMessageUDP(playerLocal.getMovementState()); //send movement state to server
                Gdx.app.log("GDX GameMap", "Sent MovementState to Server");
            }
            //gamecam constantly to follow playerLocal
            gamecam.position.set(playerLocal.getX(), playerLocal.getY(), 0);
            gamecam.update();

            //update client's number of power ups
            if(playerLocal.getX()/powerUpDistance>=1) { //player's x position is beyond the power up distance
                powerUpDistance=powerUpDistance+75f;
                if (noPowerUps == 0) {
                    noPowerUps++;
                }
                else{
                    noPowerUps=1;
                }
            }
            //update globalStatus
            if (System.currentTimeMillis() > timeStamp + 3000) {
                globalStatus = "";
            }

        }
        //Update player
        for (Map.Entry<Integer, Player> playerEntry : players.entrySet()) {
            Player curPlayer = playerEntry.getValue();
            if (client!=null && curPlayer != null) {
                curPlayer.update(delta);
            }
        }
    }

    public void render() {
        //clears screen first, set color to black
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        b2dr.render(world, gamecam.combined);

        batch.setProjectionMatrix(gamecam.combined);
        batch.begin();

        // Render seamless bg and platforms
        bgSprite.draw(batch);
        for (Sprite sprite : bgPlatformSprites) {
            sprite.draw(batch);
        }
        // finishLine sprite rendered after background & platforms but before players
        finishLine.draw(batch);
        // Render Players
        for (Map.Entry<Integer, Player> playerEntry : players.entrySet()) {
            Player curPlayer = playerEntry.getValue();
            //  Render every other players
            if (curPlayer.getId() != playerLocal.getId()) {
                curPlayer.draw(batch);
            }
        }
        if (playerLocal.isFlashed()) {
            Gdx.gl.glClearColor(1, 1, 1, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        } else {
            playerLocal.draw(batch);
        }
        batch.end();

        synchronized (this) {
            world.step(1 / 60f, 6, 2);
        }
    }

    public void resize(int width, int height) {
        gameport.update(width, height);
        gamecam.position.set(gamecam.viewportWidth / 2, gamecam.viewportHeight / 2, 0);
    }

    public void dispose() {
        world.dispose();
        b2dr.dispose();
        img.dispose();
    }

    public void onDisconnect() {
        this.client = null;
        this.dummyPlayers.clear();
        this.players.clear();
    }

    public class DummyPlayer {
        private int playerID;
        public String playerName;
        public float x;
        public float y;
        public boolean isLocal;
        private int character = 99;

        private DummyPlayer(int playerID, String playerName, float x, float y, boolean isLocal) {
            this.playerID = playerID;
            this.playerName = playerName;
            this.x = x;
            this.y = y;
            this.isLocal = isLocal;
        }

        public void setCharacter(int character) {
            this.character = character;
        }

        public boolean isReady() {
            if (this.character != 99) {
                return true;
            }
            else {
                return false;
            }
        }
    }
}
