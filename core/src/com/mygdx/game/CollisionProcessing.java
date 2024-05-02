package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.*;

import java.util.ArrayList;

public class CollisionProcessing implements ContactListener {
    public static ArrayList<Body> deleteList = new ArrayList<>();
    public static ArrayList<Body> damageList = new ArrayList<>();
    @Override
    public void beginContact(Contact contact) {
        Fixture A = contact.getFixtureA();
        Fixture B = contact.getFixtureB();
        if (A.getUserData().equals(3)) {
            deleteList.add(A.getBody());
        }
        if (B.getUserData().equals(3)) {
            deleteList.add(B.getBody());
        }
        if (A.getUserData().equals(2) && B.getUserData().equals(3)) {
            damageList.add(A.getBody());
        }
        if (B.getUserData().equals(2) && A.getUserData().equals(3)) {
            damageList.add(B.getBody());
        }
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {

    }
    public static void clearDeleteList() {
        deleteList.clear();
    }
    public static void clearDamageList() {
        damageList.clear();
    }
}
