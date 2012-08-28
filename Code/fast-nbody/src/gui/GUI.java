package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import particles.Particle;
import particles.ParticleList;

public class GUI {
	//Window
	static Dimension windowSize = new Dimension(1024,768);
	JFrame jFrame;
	JMenuBar jMenuBar;
	JMenu chargesMenu;
	JMenuItem calculateChargesItem;
	//Graphics
	Graphics2D graphics;
	SimulationCanvas simulationCanvas;
	//Simulation
	public ParticleList particleList = new ParticleList();
	public GUI()
	{
		//Create components
		jFrame = new JFrame("Fast N body simulation");
		jFrame.setSize(windowSize);
		jFrame.setLayout(new BorderLayout());
		simulationCanvas = new SimulationCanvas(windowSize,this);
		//Menu
		jMenuBar = new JMenuBar();
		chargesMenu = new JMenu("Charges");
		calculateChargesItem = new JMenuItem("Calculate charges");
		chargesMenu.add(calculateChargesItem);
		//Add the menus to the menubar
		jMenuBar.add(chargesMenu);
		
		//Add components
		jFrame.getContentPane().add(simulationCanvas);
		jFrame.getContentPane().add(jMenuBar,BorderLayout.PAGE_START);
		
		
		//Done adding components, pack the jFrame
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.pack();
		jFrame.setVisible(true);
	}
	
	public void start()
	{
		redraw();
	}
	
	public void redraw() {
		simulationCanvas.repaint();

	}
	
	public static void main(String[] args)
	{
		GUI gui = new GUI();
		gui.start();
	}

}
