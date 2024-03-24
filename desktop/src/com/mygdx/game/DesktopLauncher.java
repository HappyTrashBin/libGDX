package com.mygdx.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.mygdx.game.MyGdxGame;
import com.sun.org.apache.xerces.internal.impl.dv.xs.FullDVFactory;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		Const constant = new Const();
		int width = constant.width;
		int height = constant.height;
		config.setResizable(false);
		config.setWindowedMode(width,height);
		config.setForegroundFPS(constant.FPS);
		config.setTitle("My GDX Game");
		new Lwjgl3Application(new MyGdxGame(), config);
	}
}
