package gui;

import math.Complex;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JProgressBar;

import particles.FastMultipoleList;
import particles.NSquaredList;
import particles.Particle;
import particles.ParticleList;

public class GUI implements ActionListener, PropertyChangeListener{
	//Window
	static Dimension windowSize = new Dimension(1024,768);
	JFrame jFrame;
	JMenuBar jMenuBar;
	JMenu chargesMenu;
	JMenuItem calculateChargesItem;
	JMenuItem calculateFMAItem;
	JMenuItem clearParticlesItem;
	JMenuItem distributeRandomlyItem;
	JProgressBar progressBar;
	//Graphics
	SimulationCanvas simulationCanvas;
	static float BRIGHTNESS = 0.5f;
	//Simulation
	public ArrayList<Particle> particles = new ArrayList<Particle>(); //This lists job is to keep track of mouse clicks and charges
	CalculationTask task;
	
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
		
		calculateChargesItem = new JMenuItem("Calculate charges (Basic algorithm)");
		calculateChargesItem.addActionListener(this);
		
		calculateFMAItem = new JMenuItem("Calculate charges (Fast multipole algorithm)");
		calculateFMAItem.addActionListener(this);
		
		distributeRandomlyItem = new JMenuItem("Distribute particles randomly");
		distributeRandomlyItem.addActionListener(this);
		
		clearParticlesItem = new JMenuItem("Clear particles");
		clearParticlesItem.addActionListener(this);

		chargesMenu.add(calculateChargesItem);
		chargesMenu.add(calculateFMAItem);
		chargesMenu.add(clearParticlesItem);
		chargesMenu.add(distributeRandomlyItem);
		
		//Interface
		progressBar = new JProgressBar(0,100);

		//Add the menus to the menubar
		jMenuBar.add(chargesMenu);
		
		//Add components
		jFrame.getContentPane().add(simulationCanvas);
		jFrame.getContentPane().add(jMenuBar,BorderLayout.PAGE_START);
		jFrame.getContentPane().add(progressBar,BorderLayout.PAGE_END);
		
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
		if (e.getSource() == calculateFMAItem)
			calculateFMA();
		if (e.getSource() == distributeRandomlyItem)
			distributeRandomly();
	}
	
	//Button actions
	public void calculateCharges()
	{
		NSquaredList nsquaredList = new NSquaredList(particles);
		System.out.println("Calculating charges");
		Graphics graphics = simulationCanvas.getGraphics();
		ArrayList<Double> charges = new ArrayList<Double>();
		
		int height = simulationCanvas.getHeight();
		int width = simulationCanvas.getWidth();
		
		task = new CalculationTask(this,nsquaredList,width,height);
		task.addPropertyChangeListener(this);
		task.execute();
		
		
	}
	public void calculateFMA()
	{
		//Copy the ParticleList list
		FastMultipoleList fmList = new FastMultipoleList(particles);
		System.out.println("Calculating charges using Fast Multipole Algorithm");
		Graphics graphics = simulationCanvas.getGraphics();
		
		int height = simulationCanvas.getHeight();
		int width = simulationCanvas.getWidth();
		
		task = new CalculationTask(this,fmList,width,height);
		task.addPropertyChangeListener(this);
		task.execute();
		
		
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent e) {
        if ("progress" == e.getPropertyName()) {
            int progress = (Integer) e.getNewValue();
            progressBar.setValue(progress);        
        }
	}
	
	public void visualiseCharges(ArrayList<Double> charges)
	{
		
		int height = simulationCanvas.getHeight();
		int width = simulationCanvas.getWidth();
		
		Graphics graphics = simulationCanvas.getGraphics();
		
		double maxCharge = 0;
		
		for(Double d : charges)
		{
			if(Math.abs(d) > maxCharge && !Double.isInfinite(d))
				maxCharge = Math.abs(d);
		}
		System.out.println("Max charge is "+maxCharge);
		
		
		for (int y=0;y<height;y++) {
			for (int x=0;x<width;x++) {
				double chargeAtIJ = charges.get(y*width+x);
				if (chargeAtIJ < 0.0) {
				    graphics.setColor(new Color(Math.max(0.0f,Math.min(1.0f, (float)(-chargeAtIJ*BRIGHTNESS/maxCharge))),0.0f,0.0f));
				} else {
					graphics.setColor(new Color(0.0f,Math.max(0.0f,Math.min(1.0f, (float)(chargeAtIJ*BRIGHTNESS/maxCharge))),0.0f));
				}
				graphics.fillRect(x, y, 1, 1);
			}
		}
	}
	
	public void distributeRandomly()
	{
		for(int i = 0; i < 20; i++)
		{
			Particle p;
			int charge = Math.random() < 0.5? -Particle.DEFAULT_CHARGE:Particle.DEFAULT_CHARGE;
			p = new Particle(Math.random()*simulationCanvas.getWidth(),Math.random()*simulationCanvas.getWidth(),Particle.DEFAULT_MASS,charge);
			particles.add(p);
		}
		simulationCanvas.repaint();
	}
	
	public void clearParticles()
	{
		System.out.println("Clearing particles");
		particles.clear();
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
