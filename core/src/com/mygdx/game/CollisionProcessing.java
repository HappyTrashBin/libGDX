package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.*;

import java.util.ArrayList;

public class CollisionProcessing implements ContactListener {
    public static ArrayList<Body> deleteList = new ArrayList<>();


    @Override
    public void beginContact(Contact contact) {
        Fixture A = contact.getFixtureA();
        Fixture B = contact.getFixtureB();
        if (A.getUserData().equals(3)) {
            //System.out.println("Bullet Body collision");
            deleteList.add(A.getBody());
        }
        if (B.getUserData().equals(3)) {
            //System.out.println("Body Bullet collision");
            deleteList.add(B.getBody());
        }
        if (A.getUserData().equals(1)) {
            //System.out.println("Enemy");
        }
        if (B.getUserData().equals(1)) {
            //System.out.println("Enemy");
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
    public static ArrayList<Body> getDeleteList() {
        return deleteList;
    }
    public static void clearDeleteList() {
        deleteList.clear();
    }
}
