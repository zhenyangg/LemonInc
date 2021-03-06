package com.lemoninc.nimbusrun.Screens;

/*********************************
 * FILENAME : WaitScreen.java
 * DESCRIPTION :
 * PUBLIC FUNCTIONS :
 *       void    playGame()
 *       void    show()
 *       void    update(float delta)
 *       void    render(float delta)
 *       void    resize(int width, int height)
 *       void    pause()
 *       void    resume()
 *       void    hide()
 *       void    dispose()
 * NOTES :
 * LAST UPDATED: 8/4/2016 09:00
 *
 * ********************************/

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lemoninc.nimbusrun.NimbusRun;
import java.util.Random;

public class WaitScreen implements Screen{
    private NimbusRun game;
    private Camera gamecam;
    private Viewport gameport;

    private float BUTTON_WIDTH;
    private float BUTTON_HEIGHT;

    private SpriteBatch batch;

    private Stage stage;
    private Skin skin;
    private TextButton hostbutton;
    private TextButton clientbutton;

    private TextButton.TextButtonStyle style;
    private float gameWidth;
    private float gameHeight;
    private TextField playername;
    private Random random = new Random();
    private TextButton backbutton;

    Label labeltitle;
    Preferences preferences;

    Boolean playmusic;
    Music music;
    Sound soundclick;
    /**
     * This constructor instantiates the Sprites, Viewport, Camera, etc
     * @param game The Game object
     */
    public WaitScreen(NimbusRun game,SpriteBatch batch, Boolean playmusic) {
        this.game = game;
        this.batch = batch;
        this.gameHeight=game.V_HEIGHT;
        this.gameWidth=game.V_WIDTH;
        this.playmusic=playmusic;

        preferences = Gdx.app.getPreferences("NimbusRun_Network");
        soundclick=Gdx.audio.newSound(Gdx.files.internal("Sounds/click.mp3"));
        music=Gdx.audio.newMusic(Gdx.files.internal("Sounds/waitscreen.mp3"));
        music.setVolume(0.5f);                 // sets the volume to half the maximum volume
        music.setLooping(true);
        if(playmusic){
            music.play();
        }

        BUTTON_HEIGHT=50;
        BUTTON_WIDTH=125;

        style=new TextButton.TextButtonStyle();
        style.font=new BitmapFont(Gdx.files.internal("Fonts/crimesFont48Black.fnt"));
        style.font.setColor(Color.RED);
        style.font.getData().setScale(0.65f, 0.65f);
        style.up=new TextureRegionDrawable(new TextureRegion(new Texture("3_CharSelScreen/button_up1.png")));
        style.down=new TextureRegionDrawable(new TextureRegion(new Texture("3_CharSelScreen/button_down1.png")));
        style.over=new TextureRegionDrawable(new TextureRegion(new Texture("3_CharSelScreen/button_down1.png")));

        gamecam=new PerspectiveCamera();
        gameport=new FitViewport(gameWidth,gameHeight,gamecam);
        stage= new Stage(new ExtendViewport(gameWidth,gameHeight));
        hostbutton=new TextButton("Join as Host",style);
        clientbutton=new TextButton("Join as Client",style);
        backbutton=new TextButton("Go Back",style);

        Gdx.app.log("GDX WaitScreen", "Finished connecting & configuring events");
    }


    /**
     * UI stuff to go in here
     */
    @Override
    public void show() {

        skin = new Skin(Gdx.files.internal("data/uiskin.json"));

        // Clear background with WHITE rather than render a white image for performance

        // Create UI elements

        labeltitle=new Label("Nimbus Run",new Label.LabelStyle(new BitmapFont(Gdx.files.internal("Fonts/crimesFont48Black.fnt")), Color.DARK_GRAY));
        labeltitle.setPosition(this.gameWidth / 2, this.gameHeight - this.gameHeight / 4, Align.center);
        labeltitle.setSize(400, 200);
        stage.addActor(labeltitle);

        playername=new TextField(preferences.getString("name"),skin);
        playername.setSize(150, 50);
        playername.setPosition(this.gameWidth / 2, 330, Align.center);
        playername.setMessageText("Enter your name");
        stage.getKeyboardFocus();
        stage.addActor(playername);

        hostbutton.setSize(this.BUTTON_WIDTH, this.BUTTON_HEIGHT);
        hostbutton.setPosition(this.gameWidth / 2, 200, Align.bottomLeft);
        stage.addActor(hostbutton);

        clientbutton.setSize(this.BUTTON_WIDTH, this.BUTTON_HEIGHT);
        clientbutton.setPosition(this.gameWidth / 2, 200, Align.bottomRight);
        stage.addActor(clientbutton);

        backbutton.setSize(this.BUTTON_WIDTH, this.BUTTON_HEIGHT);
        backbutton.setPosition(this.gameWidth*0.8f, 150, Align.bottomRight);
        stage.addActor(backbutton);

        playername.setTextFieldListener(new TextField.TextFieldListener() {
            public void keyTyped(TextField textField, char key) {
                if (key == '\n') textField.getOnscreenKeyboard().show(false);
            }
        });

        hostbutton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //game.setScreen(new TutorialScreen(game, gameWidth, gameHeight));
                soundclick.play();
                hostGame();
            }
        });

        clientbutton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // game.setScreen(new StoryLineScreen(game, gameWidth, gameHeight));
                soundclick.play();
                joinGame();
            }
        });
        backbutton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // game.setScreen(new StoryLineScreen(game, gameWidth, gameHeight));
                soundclick.play();
                game.setScreen(new MenuScreen(game,batch,gameWidth,gameHeight));
            }
        });

        Gdx.input.setInputProcessor(stage);
    }

    public void update(float dt) {
        gamecam.update();
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

//        batch.setProjectionMatrix(gamecam.combined);
        batch.begin();
        batch.end();

        stage.act();
        stage.draw();
    }

    /**
     * join a game room
     */
    private void joinGame(){
        game.setScreen(new CharacterSelectionScreen(game, batch, false, getName(), playmusic));
        savePrefs();
    }

    /**
     * play game as a host
     */
    private void hostGame(){
        game.setScreen(new CharacterSelectionScreen(game, batch, true, getName(), playmusic));
        savePrefs();
    }

    /**
     * This method gets the name of the player from the name textfield.
     * If the name is empty, the player is assigned a random name. This random name is then saved in the textfield.
     *
     * @return player's name
     */
    private String getName(){
        String name =playername.getText();
        if (name.isEmpty()) {
            name = "Player" + random.nextInt(10000);
        }
        playername.setText(name);
        return name;
    }

    private void savePrefs(){
        preferences.putString("name", getName());
        preferences.flush();
    }

    @Override
    public void resize(int width, int height) {
        gameport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        music.stop();
        dispose();
    }

    @Override
    public void dispose() {
        Gdx.input.setInputProcessor(null);
        skin.dispose();
        stage.dispose();
        music.dispose();
        soundclick.dispose();
    }


}