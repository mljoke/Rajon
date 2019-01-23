package com.mljoke.rajon.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mljoke.rajon.Core;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Rajon";
		config.fullscreen = false;
		config.width = 1440;
		config.height = 900;
		config.useHDPI = true;
		config.addIcon("icon.png", Files.FileType.Internal);
		//config.samples = 3;
		new LwjglApplication(new Core(), config);
	}
}
