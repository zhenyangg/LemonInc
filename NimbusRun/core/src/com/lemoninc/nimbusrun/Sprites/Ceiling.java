package com.lemoninc.nimbusrun.Sprites;

/*********************************
 * FILENAME : Ceiling.java
 * DESCRIPTION : An edgeShape (box2D line) to represent ceiling
 * PUBLIC FUNCTIONS :
 *       none
 * NOTES :
 * LAST UPDATED: 19/4/2016 20:16
 *
 * ********************************/

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class Ceiling {
    public World world;
    public Body b2body;
    private GameMap gameMap;

    public Ceiling(GameMap gameMap) {
        this.gameMap = gameMap;
        this.world = gameMap.getWorld();

        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        float x1 = -gameMap.getGameport().getWorldWidth();
        float y1 = gameMap.getGameport().getWorldHeight()*3f;
        float x2 = gameMap.getGameport().getWorldWidth()*19;
        float y2 = gameMap.getGameport().getWorldHeight()*3f;

        FixtureDef fdef = new FixtureDef();
        EdgeShape edgeShape = new EdgeShape();
        edgeShape.set(x1, y1, x2, y2);
        fdef.shape = edgeShape;

        b2body = world.createBody(bdef);
        b2body.createFixture(fdef);
        edgeShape.dispose();

    }
}