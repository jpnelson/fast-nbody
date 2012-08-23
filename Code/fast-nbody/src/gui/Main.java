package gui;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics2D;

import javax.swing.JFrame;

public class Main {
	//Window
	static Dimension windowSize = new Dimension(800,600);
	JFrame jFrame;
	//Graphics
	Graphics2D g;
	Canvas jCanvas;
	public Main()
	{
		jFrame = new JFrame("Fast N body simulation");
		jCanvas = new Canvas();
		jFrame.getContentPane().add(jCanvas);
		
		//Done adding components, pack the jFrame
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.pack();
		jFrame.setVisible(true);
		
	}

}
