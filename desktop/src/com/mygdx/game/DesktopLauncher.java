package com.mygdx.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setResizable(false);
		config.setWindowedMode(Const.width,Const.height);
		config.setForegroundFPS(Const.FPS);
		config.setTitle("My GDX Game");
		new Lwjgl3Application(new MyGdxGame(), config);
	}
}
