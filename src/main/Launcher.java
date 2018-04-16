package main;

import javax.swing.*;

public class Launcher
{
	public static void main(String[] args)
	{
		Application app = new Application("Encryptor", 640, 480);
		app.start();

		try
		{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
		{
			e.printStackTrace();
		}
	}
}
