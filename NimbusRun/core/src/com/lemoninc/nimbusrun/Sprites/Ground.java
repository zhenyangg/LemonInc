package com.lemoninc.nimbusrun.Sprites;

/*********************************
 * FILENAME : Ground.java
 * DESCRIPTION : An edgeShape (box2D line) to represent ground,
 *               has random map generating function of 10 screens long
 * PUBLIC FUNCTIONS :
 *       none
 * NOTES :
 * LAST UPDATED: 23/4/2016 08:22
 *
 * ********************************/

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class Ground {
    public World world;
    public Body b2body;
    private GameMap gameMap;
    private float startX, endX;
    private int[] mapData;

    public Ground(GameMap gameMap, int[] mapData, int numPlatforms) {
        this.gameMap = gameMap;
        this.world = gameMap.getWorld();
        this.mapData = mapData;

        startX = -gameMap.getGameport().getWorldWidth();
        endX = gameMap.getGameport().getWorldWidth();

        makeFlatGround(startX, endX);
        startX = endX;
        endX += gameMap.getGameport().getWorldWidth()*2;

        for (int i = 0; i < numPlatforms; i++){
            int choice = mapData[i];
            if (choice == 0){
                makePlateau(startX, endX);
            } else if (choice == 1) {
                makeMountain(startX, endX);
            } else {
                makePit(startX, endX);
            }
            startX = endX;
            endX += gameMap.getGameport().getWorldWidth()*2;
        }
        makeFlatGround(startX, endX);
        startX = endX;
        endX += gameMap.getGameport().getWorldWidth()*2;
        makeFlatGround(startX, endX);
    }

    private void makeFlatGround(float x1, float x2) {
        float y1 = 0, y2 = 0;

        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        b2body = world.createBody(bdef);
        gameMap.makePlatformsBG(startX , endX, 'F');

        FixtureDef fdef = new FixtureDef();
        EdgeShape edgeShape = new EdgeShape();
        edgeShape.set(x1, y1, x2, y2);
        fdef.shape = edgeShape;
        b2body.createFixture(fdef);
        edgeShape.dispose();
    }

    private void makePlateau(float startX, float endX) {
        float y1 = 0, y2 = 0;
        float segment = (endX - startX) * 0.125f;
        float x1 = startX, x2 = startX + segment;
        gameMap.makePlatformsBG(startX, endX, 'P');

        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        EdgeShape edgeShape = new EdgeShape();

        //flat plain
        edgeShape.set(x1, y1, x2, y2);
        fdef.shape = edgeShape;
        b2body.createFixture(fdef);

        //straight up
        x1 = x2;
        y2 += gameMap.getGameport().getWorldHeight()*0.65;
        edgeShape.set(x1, y1, x2, y2);
        fdef.shape = edgeShape;
        b2body.createFixture(fdef);

        //flat plain
        x1 = x2;
        y1 = y2;
        x2 += segment*6;
        edgeShape.set(x1, y1, x2, y2);
        fdef.shape = edgeShape;
        b2body.createFixture(fdef);

        //straight drop
        x1 = x2;
        y2 -= gameMap.getGameport().getWorldHeight()*0.65f;
        edgeShape.set(x1, y1, x2, y2);
        fdef.shape = edgeShape;
        b2body.createFixture(fdef);

        //flat plain
        x1 = x2;
        y1 = y2;
        x2 += segment;
        edgeShape.set(x1, y1, x2, y2);
        fdef.shape = edgeShape;
        b2body.createFixture(fdef);
    }

    private void makeMountain(float startX, float endX) {
        float y1 = 0, y2 = 0;
        float segment = (endX - startX) * 0.125f;
        float x1 = startX, x2 = startX + segment;
        gameMap.makePlatformsBG(startX , endX, 'M');

        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        EdgeShape edgeShape = new EdgeShape();

        //flat plain
        edgeShape.set(x1, y1, x2, y2);
        fdef.shape = edgeShape;
        b2body.createFixture(fdef);

        //up-slope
        x1 = x2;
        x2 += segment;
        y2 += gameMap.getGameport().getWorldHeight()*0.75f;
        edgeShape.set(x1, y1, x2, y2);
        fdef.shape = edgeShape;
        b2body.createFixture(fdef);

        //flat plain
        x1 = x2;
        y1 = y2;
        x2 += segment;
        edgeShape.set(x1, y1, x2, y2);
        fdef.shape = edgeShape;
        b2body.createFixture(fdef);

        //up-slope
        x1 = x2;
        x2 += segment;
        y2 += gameMap.getGameport().getWorldHeight()*0.75f;
        edgeShape.set(x1, y1, x2, y2);
        fdef.shape = edgeShape;
        b2body.createFixture(fdef);

        //down-slope
        x1 = x2;
        x2 += segment;
        y1 = y2;
        y2 -= gameMap.getGameport().getWorldHeight()*0.75f;
        edgeShape.set(x1, y1, x2, y2);
        fdef.shape = edgeShape;
        b2body.createFixture(fdef);

        //flat plain
        x1 = x2;
        y1 = y2;
        x2 += segment;
        edgeShape.set(x1, y1, x2, y2);
        fdef.shape = edgeShape;
        b2body.createFixture(fdef);

        //down-slope
        x1 = x2;
        x2 += segment;
        y1 = y2;
        y2 -= gameMap.getGameport().getWorldHeight()*0.75f;
        edgeShape.set(x1, y1, x2, y2);
        fdef.shape = edgeShape;
        b2body.createFixture(fdef);

        //flat plain
        x1 = x2;
        y1 = y2;
        x2 += segment;
        edgeShape.set(x1, y1, x2, y2);
        fdef.shape = edgeShape;
        b2body.createFixture(fdef);

    }

    private void makePit(float startX, float endX) {
        float y1 = 0, y2 = 0;
        float segment = (endX - startX) * 0.125f;
        float x1 = startX, x2 = startX + segment;
        gameMap.makePlatformsBG(startX , endX, 'T');

        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        EdgeShape edgeShape = new EdgeShape();

        //flat plain
        edgeShape.set(x1, y1, x2, y2);
        fdef.shape = edgeShape;
        b2body.createFixture(fdef);

        //down-slope
        x1 = x2;
        x2 += segment;
        y2 -= gameMap.getGameport().getWorldHeight()*.75f;
        edgeShape.set(x1, y1, x2, y2);
        fdef.shape = edgeShape;
        b2body.createFixture(fdef);

        //flat plain
        x1 = x2;
        y1 = y2;
        x2 += segment;
        edgeShape.set(x1, y1, x2, y2);
        fdef.shape = edgeShape;
        b2body.createFixture(fdef);

        //straight drop
        x1 = x2;
        y2 -= gameMap.getGameport().getWorldHeight()*0.4f;
        edgeShape.set(x1, y1, x2, y2);
        fdef.shape = edgeShape;
        b2body.createFixture(fdef);

        //flat plain
        x1 = x2;
        y1 = y2;
        x2 += segment*2;
        edgeShape.set(x1, y1, x2, y2);
        fdef.shape = edgeShape;
        b2body.createFixture(fdef);

        //straight up
        x1 = x2;
        y2 += gameMap.getGameport().getWorldHeight()*0.4f;
        edgeShape.set(x1, y1, x2, y2);
        fdef.shape = edgeShape;
        b2body.createFixture(fdef);

        //flat plain
        x1 = x2;
        y1 = y2;
        x2 += segment;
        edgeShape.set(x1, y1, x2, y2);
        fdef.shape = edgeShape;
        b2body.createFixture(fdef);

        //up-slope
        x1 = x2;
        x2 += segment;
        y2 += gameMap.getGameport().getWorldHeight()*.75f;
        edgeShape.set(x1, y1, x2, y2);
        fdef.shape = edgeShape;
        b2body.createFixture(fdef);

        //flat plain
        x1 = x2;
        y1 = y2;
        x2 += segment;
        edgeShape.set(x1, y1, x2, y2);
        fdef.shape = edgeShape;
        b2body.createFixture(fdef);
    }
}