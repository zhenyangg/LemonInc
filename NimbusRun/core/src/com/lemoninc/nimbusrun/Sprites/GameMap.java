package com.lemoninc.nimbusrun.Sprites;

/*********************************
 * FILENAME : GameMap.java
 * DESCRIPTION :
 * PUBLIC FUNCTIONS :
 *       public synchronized void addPlayer(Network.PlayerJoinLeave msg)
 *       private    void initCommon()
 *       private    void handleInput()
 *       void       update(float delta)
 *       World      getWorld()
 *       Viewport   getGamePort()
 *       public synchronized void logInfo(String string)
 * NOTES :
 * LAST UPDATED: 14/4/2016 23:59
 *
 * ********************************/

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
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
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lemoninc.nimbusrun.Networking.Client.TapTapClient;
import com.lemoninc.nimbusrun.Networking.Network;
import com.lemoninc.nimbusrun.Networking.Server.TapTapServer;
import com.lemoninc.nimbusrun.NimbusRun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameMap{

    private TapTapClient client; // only if I'm the client
    private TapTapServer server; // only if I'm internal to the server

    private boolean isClient;

    private Map<Integer, Player> players = new HashMap<Integer, Player>(); //playerId, Player
    private Map<Integer, DummyPlayer> dummyPlayers = new HashMap<Integer, DummyPlayer>(); //playerId, Player


    private OrthographicCamera gamecam;
    private Viewport gameport;

    private SpriteBatch batch;
    private Texture bgTexture;
    private Sprite bgSprite;
    private float bgHeight, bgWidth, bgStartX, bgStartY;

    private Texture bgTextureFlat, bgTextureMountain, bgTexturePit, bgTexturePlateau;
    private List<Sprite> bgPlatformSprites;
    private float bgFlatHeight, bgFlatWidth;
    private float bgMountainHeight, bgMountainWidth;
    private float bgPlateauHeight, bgPlateauWidth;
    private float bgPitHeight, bgPitWidth;

    private World world;
    private Box2DDebugRenderer b2dr;
    private Player player1, player2, player3, player4;
    private Ground ground;
    private Ceiling ceiling;
    private StartWall startWall;
    private EndWall endWall;


    private Player playerLocal;
    private DummyPlayer dummyLocal;
    private TextureAtlas img;
    private int sourceX;

    /**
     * This constructor is called inside TapTapClient
     */
    public GameMap(TapTapClient client) {

        this.client = client;
        this.isClient = true;

        //instantiate HUD, GameSounds, BitmapFont, Camera, SpriteBatch ...
        gamecam = new OrthographicCamera();
        gameport = new FitViewport(NimbusRun.V_WIDTH * 1.5f / NimbusRun.PPM, NimbusRun.V_HEIGHT * 1.5f / NimbusRun.PPM, gamecam);

        //set starting pos of bgSprites after setting cam
        bgStartX = -gameport.getWorldWidth() * 1.5f;
        bgStartY = -gameport.getWorldHeight() * 1.5f;
//        Log.info(bgStartY + " y pos");
        batch = new SpriteBatch();

        //TODO: 5 refers to the character selected at the main menu
        initCommon(5);

        //add these sprites to the world
        ground = new Ground(this);
        ceiling = new Ceiling(this);
        startWall = new StartWall(this);
        endWall = new EndWall(this);

//        logInfo("GameMap initialised");
        Gdx.app.log("GameMap", "GameMap instantiated in Client");

    }

    /**
     * This constructor is called inside TapTapServer
     * //TODO: make a constructor for server that does not create World, and box2d related stuff but still can store who is connected, who is where, etc
     */
    public GameMap(TapTapServer server) {
        this.server = server;
        this.isClient = false;

        initCommon(5);

//        logInfo("GameMap initialised");
        Gdx.app.log("GameMap", "GameMap instantiated in Server");


    }

    public void initPlay() {
        //create Players from dummyPlayers
        for (Map.Entry<Integer, DummyPlayer> playerEntry : dummyPlayers.entrySet()) {
            DummyPlayer curPlayer = playerEntry.getValue();
            if (curPlayer.isLocal) {
                playerLocal = new Player(this, getImg(curPlayer.character), curPlayer.x, curPlayer.y, true);
                players.put(curPlayer.playerID, playerLocal);
            }
            else {
                players.put(curPlayer.playerID, new Player(this, getImg(curPlayer.character), curPlayer.x, curPlayer.y, false));
            }


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
            case 1: img = new TextureAtlas(Gdx.files.internal("spritesheets/LBspritesheet.atlas")); break;
            case 2: img = new TextureAtlas(Gdx.files.internal("spritesheets/SKspritesheet.atlas")); break;
            case 3: img = new TextureAtlas(Gdx.files.internal("spritesheets/FXspritesheet.atlas")); break;
            case 4: img = new TextureAtlas(Gdx.files.internal("spritesheets/KPspritesheet.atlas")); break;
            case 5: img = new TextureAtlas(Gdx.files.internal("spritesheets/PTspritesheet.atlas")); break;
            case 6: img = new TextureAtlas(Gdx.files.internal("spritesheets/MWSspritesheet.atlas")); break;
            default: img = new TextureAtlas(Gdx.files.internal("spritesheets/PTspritesheet.atlas")); break;
        }
        return img;
    }

    private void initCommon(int whichCharacter){
        //TODO: server needs textureAtls for hwat?

        world = new World(new Vector2(0, -10), true); //box2d world with gravity
        b2dr = new Box2DDebugRenderer();



        // initialise all background sprites
        bgTexture = new Texture("PlayScreen/bg.png");
        bgTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        bgSprite = new Sprite(new TextureRegion(bgTexture, bgTexture.getWidth()*11, bgTexture.getHeight()*2));
        bgWidth = bgTexture.getWidth() / NimbusRun.PPM * 1.4f * 11;
        bgHeight = bgTexture.getHeight() / NimbusRun.PPM * 1.4f * 2;
        bgSprite.setX(bgStartX);
        bgSprite.setY(bgStartY);
        bgSprite.setSize(bgWidth, bgHeight);

        bgTextureMountain = new Texture("PlayScreen/platform_mountain.png");
        bgMountainWidth = bgTextureMountain.getWidth() / NimbusRun.PPM * 2.2f;
        bgMountainHeight = bgTextureMountain.getHeight() / NimbusRun.PPM * 2.2f;

        bgPlatformSprites = new ArrayList<Sprite>();

    }

    /**
     * Client receives PlayerJoinLeave from server containing player ID, name, initial x and y
     * @param msg
     */
    public void onConnect(Network.PlayerJoinLeave msg) {

        if (this.dummyLocal == null) {
            // TODO Server should spawn localPlayer too
//            playerLocal = new Player(this, img, msg.initial_x, msg.initial_y, true);
            dummyLocal = new DummyPlayer(client.id, msg.name, msg.initial_x, msg.initial_y, true);
            dummyPlayers.put(dummyLocal.playerID, dummyLocal);
//            this.playerLocal.setId(client.id);
//            this.playerLocal.setName(msg.name);
//            players.put(client.id, playerLocal);
            //hud.setPlayerLocal(playerLocal);
            //setStatus("Connected to " + client.remoteIP);
            Gdx.app.log("GameMap", "local player created at "+msg.initial_x+" "+msg.initial_y);
        } else {
//            logInfo("setNetworkClient called twice");
        }
    }

    /**
     * This method is only called in Character Selection screen
     * @param msg
     */
    public synchronized void addPlayer(Network.PlayerJoinLeave msg) {
        //create new player from msg
        DummyPlayer newDummy = new DummyPlayer(msg.playerId, msg.name, msg.initial_x, msg.initial_y, false);
//        Player newPlayer = new Player(this, img, msg.initial_x, msg.initial_y, false);
//        newPlayer.setId(msg.playerId);
//        newPlayer.setName(msg.name);

        dummyPlayers.put(newDummy.playerID, newDummy);
//        players.put(msg.playerId, newPlayer);
//        logInfo("Player " +msg.playerId+" added to players!");
    }

    public synchronized void playerMoved(Network.MovementState msg) {
        //TODO: players should be ConcurrentHashMap?
        Player player = players.get(msg.playerId);
        if (player != null) {
            player.setMovementState(msg);
        }
    }

    /**
     * Destroy the disconnected player's body from world
     * Remove disconnected player from players
     * Can be called from both CS screen and PlayScreen
     *
     * @param msg
     */
    public synchronized void removePlayer(Network.PlayerJoinLeave msg) {
        dummyPlayers.remove(msg.playerId);

        if (players.get(msg.playerId) != null) {
            world.destroyBody(players.get(msg.playerId).b2body);
            players.remove(msg.playerId);
        }
    }


    public synchronized Player getPlayerById(int id){
        return players.get(id);
    }

    public synchronized DummyPlayer getDummyById(int id) {
        return dummyPlayers.get(id);
    }

    public boolean allDummyReady() {
        for (Map.Entry<Integer, DummyPlayer> playerEntry : dummyPlayers.entrySet()) {
            DummyPlayer curPlayer = playerEntry.getValue();
            if (!curPlayer.isReady()) {
                return false;
            }
        }
        return true;
    }

    public void declareCharacter(int charactername) {
        dummyLocal.setCharacter(charactername);
    }

    public void setCharacter(int playerId, int charactername) {
        dummyPlayers.get(playerId).setCharacter(charactername);
    }

    public World getWorld(){
        return this.world;
    }

    public Viewport getGameport() { return this.gameport; }

    /**
     * Update GameMap's state.
     *
     * Should the box world be rendered here?
     *
     * @param delta
     */
    public void update(float delta) {
        //If client is created and local player has spawned
        if (client != null && playerLocal != null) {
            if (playerLocal.handleInput()) { // (arrow key has been pressed by player)
                client.sendMessageUDP(playerLocal.getMovementState()); //send movement state to server
            }

            //gamecam constantly to follow playerLocal
            gamecam.position.set(playerLocal.getX(), playerLocal.getY(), 0);
            gamecam.update();
        }

        //Update player
        //TODO: should the box2d world be rendered here?
        for (Map.Entry<Integer, Player> playerEntry : players.entrySet()) {
            Player curPlayer = playerEntry.getValue();
            curPlayer.update(delta);
            //if(curPlayer != playerLocal) curPlayer.renderNameTag(spriteBatch, fontNameTag);
        }

    }

    public synchronized void render() {
        //clears screen first, set color to black
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //--------------START batch
        batch.setProjectionMatrix(gamecam.combined);
        batch.begin();

        // Render seamless bg and platforms
        bgSprite.draw(batch);
        for (Sprite sprite : bgPlatformSprites) {
            sprite.draw(batch);
        }

        // Render Players
        for (Map.Entry<Integer, Player> playerEntry : players.entrySet()) {
            Player curPlayer = playerEntry.getValue();
            curPlayer.draw(batch);
            //if(curPlayer != playerLocal) curPlayer.renderNameTag(spriteBatch, fontNameTag);
        }

        //----------------END batch
        batch.end();

        b2dr.render(world, gamecam.combined);

        //steps box2d world
        world.step(1 / 60f, 6, 2);
    }

    public void makePlatformsBG(float startX, float endX, char type){
        Sprite sprite;
        switch(type){
            case 'M': sprite = new Sprite(bgTextureMountain);
                sprite.setPosition(startX, -bgMountainHeight*0.4f);
                sprite.setSize(bgMountainWidth, bgMountainHeight);
//                Log.info("Mountain made at: " + startX);
                bgPlatformSprites.add(sprite); break;
        }

    }



    public synchronized void logInfo(String string) {
       // Log.info("[GameMap]: " + (isClient ? "[Client] " : "[Server] ") + string);
    }

    public void resize(int width, int height) {
        gameport.update(width, height);
        gamecam.position.set(gamecam.viewportWidth / 2, gamecam.viewportHeight / 2, 0);
    }

    public void dispose() {
        world.dispose();
        b2dr.dispose();
        batch.dispose();
        //dispose textures
        img.dispose();
        //TODO:friendly players textures?

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
