package com.lemoninc.nimbusrun.Screens;

/*********************************
 * FILENAME : MenuScreen.java
 * DESCRIPTION : Main page of the game. Displays buttons that connect to
 *               WaitScreen, TutorialScreen and CharDescrScreen.
 * PUBLIC FUNCTIONS :
 *
 --LIBGDX METHODS--
 *      void    show
 *      void    render
 *      void    resize
 *      void    pause
 *      void    resume
 *      void    hide
 *      void    dispose

 * NOTES : Enters from SplashScreen, exits to WaitScreen, TutorialScreen or CharDescrScreen
 * LAST UPDATED: 23/4/2016 09:09
 *
 * ********************************/

import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lemoninc.nimbusrun.NimbusRun;

public class MenuScreen implements Screen {
    private Viewport viewport;
    private Camera camera;
    private float gameWidth;
    private float gameHeight;
    private float BUTTON_WIDTH;
    private float BUTTON_HEIGHT;

    private SpriteBatch batch;
    private Texture background;
    private Sprite sprite;

    private TextButton.TextButtonStyle style;

    private Stage stage;

    private TextButton buttonStory;
    private TextButton buttonCharDescr;
    private TextButton buttonChooseCharacter;
    private TextButton buttonPlay;
    private TextButton buttonTutorial;

    private Music music;
    private NimbusRun game;
    public Boolean playmusic;
    Sound soundclick;


    public MenuScreen(NimbusRun game,SpriteBatch batch, float gameWidth,float gameHeight){
        this.game=game;
        this.batch = batch;
        this.gameWidth=gameWidth;
        this.gameHeight=gameHeight;

        playmusic=true;

        BUTTON_HEIGHT=60;
        BUTTON_WIDTH=130;

        music=Gdx.audio.newMusic(Gdx.files.internal("Sounds/menuscreenmusic.mp3"));
        music.play();
        music.setVolume(0.5f);                 // sets the volume to half the maximum volume
        music.setLooping(true);


        soundclick=Gdx.audio.newSound(Gdx.files.internal("Sounds/click.mp3"));


        style=new TextButton.TextButtonStyle();
        style.font=new BitmapFont(Gdx.files.internal("Fonts/crimesFont48Black.fnt"));
        style.font.setColor(Color.RED);
        style.font.getData().setScale(0.7f, 0.7f);
        style.up=new TextureRegionDrawable(new TextureRegion(new Texture("1_MenuScreen/button_up.png")));
        style.down=new TextureRegionDrawable(new TextureRegion(new Texture("1_MenuScreen/button_down.png")));
        style.over=new TextureRegionDrawable(new TextureRegion(new Texture("1_MenuScreen/button_down.png")));
        
        camera=new PerspectiveCamera();
        viewport=new FitViewport(gameWidth,gameHeight,camera);
        stage= new Stage(new ExtendViewport(gameWidth,gameHeight));
        buttonPlay=new TextButton("Start Play",style);
        buttonTutorial=new TextButton("Tutorial",style);
        buttonCharDescr=new TextButton("Characters", style);

        show();

    }

    @Override
    public void show() {
        background= new Texture("1_MenuScreen/bg.png");
        background.setFilter(Texture.TextureFilter.Linear,Texture.TextureFilter.Linear);
        sprite=new Sprite(background);
        sprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        buttonPlay.setSize(this.BUTTON_WIDTH, this.BUTTON_HEIGHT);
        buttonPlay.setPosition(this.gameWidth*0.8f, this.gameHeight*0.50f, Align.center);
        stage.addActor(buttonPlay);

        buttonTutorial.setSize(this.BUTTON_WIDTH, this.BUTTON_HEIGHT);
        buttonTutorial.setPosition(this.gameWidth*0.8f, this.gameHeight*0.37f, Align.center);
        stage.addActor(buttonTutorial);

        buttonCharDescr.setSize(this.BUTTON_WIDTH, this.BUTTON_HEIGHT);
        buttonCharDescr.setPosition(this.gameWidth*0.8f, this.gameHeight*0.24f, Align.center);
        stage.addActor(buttonCharDescr);

        buttonPlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                soundclick.play();
                if (!music.isPlaying()){
                    playmusic=false;
                }
                music.stop();
                game.setScreen(new WaitScreen(game,batch,playmusic));

            }
        });
        buttonTutorial.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                soundclick.play();
                game.setScreen(new TutorialScreen(game,batch,gameWidth, gameHeight));
            }
        });

        buttonCharDescr.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                soundclick.play();
                game.setScreen(new CharDescrScreen(game, batch, gameWidth,gameHeight));
            }
        });

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        sprite.draw(batch);
        batch.end();

        stage.act();
        stage.draw();


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
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
        music.dispose();
        background.dispose();
        sprite.getTexture().dispose();
        soundclick.dispose();
    }

}
