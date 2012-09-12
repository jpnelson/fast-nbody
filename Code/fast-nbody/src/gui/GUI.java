package gui;

import math.Complex;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import particles.Particle;
import particles.ParticleList;

public class GUI implements ActionListener{
	//Window
	static Dimension windowSize = new Dimension(1024,768);
	JFrame jFrame;
	JMenuBar jMenuBar;
	JMenu chargesMenu;
	JMenuItem calculateChargesItem;
	JMenuItem clearParticlesItem;
	//Graphics
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
		calculateChargesItem.addActionListener(this);
		
		clearParticlesItem = new JMenuItem("Clear particles");
		clearParticlesItem.addActionListener(this);

		chargesMenu.add(calculateChargesItem);
		chargesMenu.add(clearParticlesItem);

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
    
    //Button presses
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == calculateChargesItem)
			calculateCharges();
		if (e.getSource() == clearParticlesItem)
			clearParticles();
	}
	
	//Button actions
	public void calculateCharges()
	{
		System.out.println("Calculating charges");
		
		for (int y=0;y<simulationCanvas.getHeight();y++) {
			for (int x=0;x<simulationCanvas.getWidth();x++) {
				double chargeAtIJ = particleList.charge(new Complex(x,y));
				Graphics graphics = simulationCanvas.getGraphics();
				if (chargeAtIJ < 0.0) {
				    graphics.setColor(new Color(Math.max(0.0f,Math.min(1.0f, (float)-chargeAtIJ/20.0f)),0.0f,0.0f));
				} else {
					graphics.setColor(new Color(0.0f,Math.max(0.0f,Math.min(1.0f, (float)chargeAtIJ/20.0f)),0.0f));
				}
				graphics.fillRect(x, y, 1, 1);
			}
		}
	}
	
	public void clearParticles()
	{
		System.out.println("Clearing particles");
		particleList.clear();
		redraw();
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
