package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.*;

public class CollisionProcessing implements ContactListener {
    World world;

    public CollisionProcessing(World world) {
        super();
        this.world = world;
    }
    @Override
    public void beginContact(Contact contact) {
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {
        Fixture A = contact.getFixtureA();
        Fixture B = contact.getFixtureB();
        if (A == null || B == null) return;
        if (A.getUserData() == null || B.getUserData() == null) return;

        if (A.getUserData().equals(3)) {
            System.out.println("Bullet Body collision");
            //A.getBody().setActive(false);
            //world.destroyBody(A.getBody());
        }
        if (B.getUserData().equals(3)) {
            System.out.println("Body Bullet collision");
            //B.getBody().setActive(false);
            //world.destroyBody(B.getBody()); // I tried -B.geB.getBody().destroyFixture(B);- but it does not work too
        }
    }
}
