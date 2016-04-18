/*********************************
 * FILENAME : CharacterSelectionScreen.java
 * DESCRIPTION :
 * PUBLIC FUNCTIONS :
 *
 * NOTES : The screen takes input from the waitscreen and moves to waitscreen
 * TODO: Create a waiting function for all the players to connect and wait for each other,
 * TODO: get the input in the screen a string of the host ip adress, (right now IP=string localhost)
 * TODO: The wait can be onclick of the joingame button
 * TODO: Implement the counter on the basis of the players connected
 * The function has a string name of the player it has chosen, pass this to the playscreen/player to choose the specific character.
 * LAST UPDATED:
 *
 * ********************************/


package com.lemoninc.nimbusrun.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lemoninc.nimbusrun.Networking.Client.TapTapClient;
import com.lemoninc.nimbusrun.Networking.Network;
import com.lemoninc.nimbusrun.Networking.Server.TapTapServer;
import com.lemoninc.nimbusrun.NimbusRun;
import com.lemoninc.nimbusrun.Sprites.GameMap;

import java.io.IOException;


/**
 * Created by Nikki on 12/4/2016.
 */
public class CharacterSelectionScreen implements Screen{
    private SpriteBatch batcher;
    private Sprite sprite;
    private final NimbusRun game;
    private float gameWidth;
    private float gameHeight;
    private Viewport viewport;
    private Camera camera;
    final Boolean isHost;
    String ipAddress;
    String playername;
    private long startTime;
    private ImageButton Buddha,foxy,kappa,krishna,madame,ponti;
    TextureAtlas atlas1,atlas2,atlas3;
    private Skin skin;
    ImageButton.ImageButtonStyle BuddhabtnStyle,foxybtnStyle,kappabtnStyle,krishnabtnStyle,madamebtnStyle,pontibtnStyle;
    private Stage stage;
    float BUTTON_HEIGHT,BUTTON_WIDTH;
    Label Title;
    Sprite playercharacter;
    TextButton joingame;
    TextButton.TextButtonStyle style;
    private int charactername = 99;
    private String myIP;

    private TapTapClient client;
    private TapTapServer server;
    private GameMap gamemap;

    /**
     *
     * @param game
     * @param isHost
     * @param playerName
     */
    public CharacterSelectionScreen(NimbusRun game, final boolean isHost, String playerName){
        this.game = game;
        this.isHost=isHost;
        this.ipAddress=ipAddress;
        this.playername=playerName;
        this.gameWidth = NimbusRun.V_WIDTH;
        this.gameHeight = NimbusRun.V_HEIGHT;

        charactername=1; //default character is Buddha

        BUTTON_HEIGHT=165;
        BUTTON_WIDTH=140;

        camera=new PerspectiveCamera();
        viewport=new FitViewport(gameWidth,gameHeight,camera);
        skin=new Skin();
        atlas1=new TextureAtlas(Gdx.files.internal("CharSelScreen/charicons.pack"));
        atlas2=new TextureAtlas(Gdx.files.internal("CharSelScreen/zoomicons.pack"));
        atlas3=new TextureAtlas(Gdx.files.internal("buttonsupdown.pack"));
        skin.addRegions(atlas1);
        skin.addRegions(atlas2);
        skin.addRegions(atlas3);

        style=new TextButton.TextButtonStyle();
        style.font=new BitmapFont(Gdx.files.internal("Fonts/crimesFont48Black.fnt"));
        style.font.setColor(Color.DARK_GRAY);
        style.font.getData().setScale(0.65f, 0.65f);
        style.up=skin.getDrawable("button_up");
        style.over=skin.getDrawable("button_down");
        style.down=skin.getDrawable("button_down");
//        style.up=new TextureRegionDrawable(new TextureRegion(new Texture("button_up.png")));
//        style.down=new TextureRegionDrawable(new TextureRegion(new Texture("button_down.png")));

        stage= new Stage(new ExtendViewport(gameWidth,gameHeight));
        stage.clear();

        final Table table = new Table();
        table.right();
        table.setFillParent(true);


        //Title=new Label(String.format("%03d","Choose your Avatar"),new Label.LabelStyle(new BitmapFont(Gdx.files.internal("Fonts/crime.fnt")), Color.DARK_GRAY));
        Title=new Label("Choose Your Character",new Label.LabelStyle(new BitmapFont(Gdx.files.internal("Fonts/crimesFont48Black.fnt")), Color.DARK_GRAY));
        Title.setPosition(250, 400);
        Title.setSize(250,100);
        table.addActor(Title);

        BuddhabtnStyle=new ImageButton.ImageButtonStyle();
        foxybtnStyle=new ImageButton.ImageButtonStyle();
        kappabtnStyle=new ImageButton.ImageButtonStyle();
        krishnabtnStyle=new ImageButton.ImageButtonStyle();
        madamebtnStyle=new ImageButton.ImageButtonStyle();
        pontibtnStyle=new ImageButton.ImageButtonStyle();


        BuddhabtnStyle.imageUp = skin.getDrawable("btn_buddha");
        BuddhabtnStyle.imageOver=skin.getDrawable("btn_buddha_sel");
        BuddhabtnStyle.imageDown = skin.getDrawable("btn_buddha_sel");
        BuddhabtnStyle.imageChecked=skin.getDrawable("btn_buddha_sel");

        foxybtnStyle.imageUp = skin.getDrawable("btn_foxy");
        foxybtnStyle.imageDown = skin.getDrawable("btn_foxy_sel");
        foxybtnStyle.imageOver = skin.getDrawable("btn_foxy_sel");
        foxybtnStyle.imageChecked=skin.getDrawable("btn_foxy_sel");

        kappabtnStyle.imageUp = skin.getDrawable("btn_kappa");
        kappabtnStyle.imageDown = skin.getDrawable("btn_kappa_sel");
        kappabtnStyle.imageOver = skin.getDrawable("btn_kappa_sel");
        kappabtnStyle.imageChecked=skin.getDrawable("btn_kappa_sel");

        krishnabtnStyle.imageUp = skin.getDrawable("btn_krishna");
        krishnabtnStyle.imageDown= skin.getDrawable("btn_krishna_sel");
        krishnabtnStyle.imageOver = skin.getDrawable("btn_krishna_sel");
        krishnabtnStyle.imageChecked=skin.getDrawable("btn_krishna_sel");

        madamebtnStyle.imageUp = skin.getDrawable("btn_madame");
        madamebtnStyle.imageDown = skin.getDrawable("btn_madame_sel");
        madamebtnStyle.imageOver = skin.getDrawable("btn_madame_sel");
        madamebtnStyle.imageChecked=skin.getDrawable("btn_madame_sel");

        pontibtnStyle.imageUp = skin.getDrawable("btn_ponti");
        pontibtnStyle.imageDown = skin.getDrawable("btn_ponti_sel");
        pontibtnStyle.imageOver = skin.getDrawable("btn_ponti_sel");
        pontibtnStyle.imageChecked=skin.getDrawable("btn_ponti_sel");


        Buddha= new ImageButton(BuddhabtnStyle);
        foxy=new ImageButton(foxybtnStyle);
        kappa=new ImageButton(kappabtnStyle);
        krishna=new ImageButton(krishnabtnStyle);
        madame=new ImageButton(madamebtnStyle);
        ponti=new ImageButton(pontibtnStyle);

        Buddha.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        foxy.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        kappa.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        krishna.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        madame.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        ponti.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);

        Buddha.setPosition(viewport.getScreenWidth() / 4, 420, Align.left);
        foxy.setPosition(viewport.getScreenWidth() / 4, 270, Align.left);
        kappa.setPosition(viewport.getScreenWidth() / 4, 100, Align.left);
        krishna.setPosition(viewport.getScreenWidth() / 4 + 275, 380, Align.right);
        madame.setPosition(viewport.getScreenWidth() / 4 + 275, 230, Align.right);
        ponti.setPosition(viewport.getScreenWidth() / 4 + 275, 80, Align.right);

        joingame=new TextButton("Join Game",style);
        joingame.setSize(150,75);
        joingame.setPosition(600,400);

        table.addActor(joingame);
        table.addActor(Buddha);
        table.addActor(foxy);
        table.addActor(kappa);
        table.addActor(krishna);
        table.addActor(madame);
        table.addActor(ponti);
        stage.addActor(table);

        Buddha.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                resetbuttons();
                System.out.println("touched");
                Gdx.app.log("Button pressed", "Buddha Button Pressed");
                playercharacter = skin.getSprite("bg_Buddha");
                playercharacter.setPosition(0, 0);
                playercharacter.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                charactername=1;
                System.out.println("touched");

            }
        });

        foxy.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                resetbuttons();
//                Buddha.clearActions();
//                System.out.println("touched");
//                Gdx.app.log("Button pressed", "Foxy Button Pressed");
                playercharacter= skin.getSprite("bg_Foxy");
                playercharacter.setPosition(0, 0);
                playercharacter.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                charactername=3;
                System.out.println("touched");
            }
        });

        kappa.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                resetbuttons();
                System.out.println("touched");
                Gdx.app.log("Button pressed", "Kappa Button Pressed");
                playercharacter= skin.getSprite("bg_Kappa");
                playercharacter.setPosition(0, 0);
                playercharacter.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                charactername=4;
                System.out.println("touched");
            }
        });
        krishna.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                resetbuttons();
                System.out.println("touched");
                Gdx.app.log("Button pressed", "KrishnaButton Pressed");
                playercharacter= skin.getSprite("bg_Krishna");
                playercharacter.setPosition(0, 0);
                playercharacter.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                charactername=2;
                System.out.println("touched");
            }
        });
        madame.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                resetbuttons();
                System.out.println("touched");
                Gdx.app.log("Button pressed", "madame Button Pressed");
                playercharacter= skin.getSprite("bg_Madame");
                playercharacter.setPosition(0, 0);
                playercharacter.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                charactername=6;
                System.out.println("touched");
            }
        });
        ponti.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                resetbuttons();
                System.out.println("touched");
                Gdx.app.log("Button pressed", "Ponti Button Pressed");
                playercharacter= skin.getSprite("bg_Pontianak");
                playercharacter.setPosition(0, 0);
                playercharacter.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                charactername=5;
                System.out.println("touched");
            }
        });
        joingame.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("CSscreen", "Character " + charactername + " selected for the player");
                //send server charactername packet
                if (charactername != 99) { //if character is chosen
                    Network.Ready ready = new Network.Ready(charactername);
                    client.sendMessage(ready);
                    gamemap.declareCharacter(charactername);
                    Gdx.app.log("CSscreen", "I declared my character to GameMap");

                }
                if (isHost) {
                    //if received charactername from all players, play game
                    if (server.allDummyReady()) {
                        //send to server GameReady
                        Network.GameReady gameready = new Network.GameReady();
                        client.sendMessage(gameready);
                        playGame();
                    }
                    else {
                        Gdx.app.log("CSscreen", "Not all dummies ready");
                    }
                }
            }
        });



    }

    // 1. LAUGHING BUDDHA
    // 2. SHESHNAH WITH KRISHNA
    // 3. NINE-TAILED FOX
    // 4. KAPPA
    // 5. PONTIANAK
    // 6. MADAME WHITE SNAKE
//    public String checkbuttonpress(){
//        if(Buddha.isPressed()) return ("Buddha");
//        else if(krishna.isPressed()) return ("Krishna");
//        else if (foxy.isPressed()) return ("Foxy");
//        else if (kappa.isPressed()) return ("Kappa");
//        else if (ponti.isPressed()) return ("Ponti");
//        else if (madame.isPressed()) return ("Madame");
//        else return ("Buddha");
//    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        sprite = new Sprite(new Texture("whitebackground.png"));
        playercharacter=new Sprite(skin.getSprite("bg_Buddha"));
        sprite.setPosition(0, 0);
        sprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        style.font=new BitmapFont(Gdx.files.internal("Fonts/Basker32.fnt"));
        style.font.setColor(Color.DARK_GRAY);
        batcher = new SpriteBatch();
        startTime = TimeUtils.millis();

        //instnatiate server, client here

        client = new TapTapClient(game, this, playername);
        gamemap = client.getMap();

        if (isHost) {
            //start my server and connect my client to my server
            try {
                server = new TapTapServer();
                client.connect("localhost");
            } catch (IOException e) {
                Gdx.app.log("CSscreen", "Host cannot connect to server, setting to WaitScreen");
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        game.setScreen(new WaitScreen(game));
                    }
                });
            }
        }
        else {
            //client connects to ipAddress
            try {
                Gdx.app.log("CSscreen", "Player connecting to LAN.");
                client.connectLAN();
            } catch (IOException e) {
                Gdx.app.log("CSscreen", "Player cannot connect to server");
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        game.setScreen(new WaitScreen(game));

                    }
                });
            }
        }

        myIP = client.getIP();

    }

    public void resetbuttons() {
        Buddha.setChecked(false);
        foxy.setChecked(false);
        ponti.setChecked(false);
        kappa.setChecked(false);
        krishna.setChecked(false);
        madame.setChecked(false);
    }

    public void playGame(){
        stage.clear();
        game.setScreen(new PlayScreen(game, isHost, playername, client, server));
    }

//    public void goPlayScreen() {
//        game.setScreen(new PlayScreen(game, isHost, playername, client, server));
//
//    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        batcher.begin();
        sprite.draw(batcher);
        if (myIP != null) {
            style.font.draw(batcher, "Host IP address: " + myIP, Gdx.graphics.getWidth()/3+75,80);
        }
        playercharacter.setPosition(75, 50);
        playercharacter.setSize(Gdx.graphics.getWidth() - Gdx.graphics.getWidth() / 8, Gdx.graphics.getHeight() - Gdx.graphics.getWidth() / 8);
        playercharacter.draw(batcher);
        batcher.end();

        stage.act();
        stage.draw();


        //if (TimeUtils.millis()>(startTime+15000)) game.setScreen(new PlayScreen(game,isHost,ipAddress, playername));

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);

    }

    @Override
    public void dispose() {
        atlas1.dispose();
        atlas2.dispose();
        stage.dispose();
        skin.dispose();
        sprite.getTexture().dispose();
        batcher.dispose();
    }
}
