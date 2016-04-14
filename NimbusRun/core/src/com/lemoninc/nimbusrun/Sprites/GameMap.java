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
import com.esotericsoftware.minlog.Log;
import com.lemoninc.nimbusrun.Networking.Client.TapTapClient;
import com.lemoninc.nimbusrun.Networking.Network;
import com.lemoninc.nimbusrun.Networking.Server.TapTapServer;
import com.lemoninc.nimbusrun.NimbusRun;

import java.util.HashMap;
import java.util.Map;

public class GameMap{

    private TapTapClient client; // only if I'm the client
    private TapTapServer server; // only if I'm internal to the server

    private boolean isClient;

    private Map<Integer, Player> players = new HashMap<Integer, Player>(); //playerId, Player

    private OrthographicCamera gamecam;
    private Viewport gameport;

    private SpriteBatch batch;
    private Texture bgTexture;
    private float bgHeight, bgWidth;
    private Sprite bgSpriteA1, bgSpriteA2, bgSpriteB1, bgSpriteB2;
    private float bgStartX, bgStartY;

    private World world;
    private Box2DDebugRenderer b2dr;
    private Player player1, player2, player3, player4;
    private Ground ground;
    private Ceiling ceiling;
    private StartWall startWall;
    private EndWall endWall;


    private Player playerLocal;
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

    }

    private void initCommon(int whichCharacter){
        //TODO: server needs textureAtls for hwat?

        world = new World(new Vector2(0, -10), true); //box2d world with gravity
        b2dr = new Box2DDebugRenderer();

        // Load up all sprites into spriteMap from textureAtlas
        switch(whichCharacter){
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

        // initialise all background sprites
        bgTexture = new Texture("PlayScreen/bg.png");
        bgTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        bgSpriteA1 = new Sprite(new TextureRegion(bgTexture, bgTexture.getWidth()*11, bgTexture.getHeight()*2));
        bgWidth = bgTexture.getWidth() / NimbusRun.PPM * 1.4f * 11;
        bgHeight = bgTexture.getHeight() / NimbusRun.PPM * 1.4f * 2;
        bgSpriteA1.setX(bgStartX);
        bgSpriteA1.setY(bgStartY);
        bgSpriteA1.setSize(bgWidth, bgHeight);
        //bgSpriteA2 = new Sprite(bgSpriteA1);

    }



    //called by server to add a new player into its GameMap
    public synchronized void addPlayer(Network.PlayerJoinLeave msg) {
        //create new player from msg

        Player newPlayer = new Player(this, img, msg.initial_x, msg.initial_y, false);
        newPlayer.setId(msg.playerId);
        newPlayer.setName(msg.name);

        players.put(msg.playerId, newPlayer);
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
     * @param msg
     */
    public synchronized void removePlayer(Network.PlayerJoinLeave msg) {
        world.destroyBody(players.get(msg.playerId).b2body);
        players.remove(msg.playerId);
    }


    /**
     * Upon client-server connection, client calls map.onConnect(name) where a local Player is instantiated and stored in client's map's players
     * @param name
     */
    public void onConnect(String name) {

        if (this.playerLocal == null) {
            // TODO Server should spawn localPlayer too
            playerLocal = new Player(this, img, Network.SPAWN_X, Network.SPAWN_Y, true);
            this.playerLocal.setId(client.id);
            this.playerLocal.setName(name);
            players.put(client.id, playerLocal);
            //hud.setPlayerLocal(playerLocal);
            //setStatus("Connected to " + client.remoteIP);
        } else {
//            logInfo("setNetworkClient called twice");
        }
    }

    public synchronized Player getPlayerById(int id){
        return players.get(id);
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

    }

    public synchronized void render() {
        //clears screen first, set color to black
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //--------------START batch
        batch.setProjectionMatrix(gamecam.combined);
        batch.begin();

        // Update background sprite positions and draw anew
        /*
        if(gamecam.position.x -bgWidth/2> bgSprite2.getX()){
            bgSprite1.setX(bgSprite2.getX());
            bgSprite2.setX(bgSprite1.getX()+bgWidth); }
        bgSprite1.draw(batch);
        bgSprite2.draw(batch);
        */

        bgSpriteA1.draw(batch);
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
        //gamecam constantly follows player1
//        gamecam.position.set(playerLocal.getX(), playerLocal.getY(), 0);
//        gamecam.update();
    }



//    public synchronized void logInfo(String string) {
//        Log.info("[GameMap]: " + (isClient ? "[Client] " : "[Server] ") + string);
//    }

    public void resize(int width, int height) {
        gameport.update(width, height);
        gamecam.position.set(gamecam.viewportWidth / 2, gamecam.viewportHeight / 2, 0);
    }

    public void dispose() {
        world.dispose();
        b2dr.dispose();

        //dispose textures
        img.dispose();
        //TODO:friendly players textures?

    }

    public void onDisconnect() {
        this.client = null;
        this.players.clear();
//        logInfo("on DIsconnection, clear the players Map");
    }

}
