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
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import fma.FastMultipoleList;


import particles.NSquaredList;
import particles.Particle;
import particles.ParticleList;
import pme.PMEList;
import pme.SPMEList;

public class GUI implements ActionListener, PropertyChangeListener{
	//Window
	static Dimension windowSize = new Dimension(512,512);
	JFrame jFrame;
	JMenuBar jMenuBar;
	JMenu chargesMenu;
	JMenu particlesMenu;
	JMenuItem calculateChargesItem;
	JMenuItem calculateFMAItem;
	JMenuItem calculatePMEItem;
	JMenuItem clearParticlesItem;
	JMenuItem distributeRandomlyItem;
	JMenuItem distributeRegularlyItem;
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
		jFrame.setResizable(false);
		simulationCanvas = new SimulationCanvas(windowSize,this);
		//Menu
		jMenuBar = new JMenuBar();
		chargesMenu = new JMenu("Charges");
		particlesMenu = new JMenu("Particles");
		
		calculateChargesItem = new JMenuItem("Calculate charges (Basic algorithm)");
		calculateChargesItem.addActionListener(this);
		
		calculateFMAItem = new JMenuItem("Calculate charges (Fast multipole algorithm)");
		calculateFMAItem.addActionListener(this);
		
		calculatePMEItem = new JMenuItem("Calculate charges (Particle mesh ewald method)");
		calculatePMEItem.addActionListener(this);
		
		distributeRandomlyItem = new JMenuItem("Distribute particles randomly");
		distributeRandomlyItem.addActionListener(this);
		
		distributeRegularlyItem = new JMenuItem("Distribute particles regularly");
		distributeRegularlyItem.addActionListener(this);
		
		clearParticlesItem = new JMenuItem("Clear particles");
		clearParticlesItem.addActionListener(this);

		chargesMenu.add(calculateChargesItem);
		chargesMenu.add(calculateFMAItem);
		chargesMenu.add(calculatePMEItem);

		particlesMenu.add(distributeRandomlyItem);
		particlesMenu.add(distributeRegularlyItem);
		particlesMenu.add(clearParticlesItem);

		//Interface
		progressBar = new JProgressBar(0,100);

		//Add the menus to the menubar
		jMenuBar.add(particlesMenu);
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
	
	private static void printSeparator()
	{
		System.out.println("-------------");
	}
    
    //Button presses
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == calculateChargesItem)
			calculateCharges();
		if (e.getSource() == clearParticlesItem)
			clearParticles();
		if (e.getSource() == calculateFMAItem)
			calculateFMA();
		if (e.getSource() == calculatePMEItem)
			calculatePME();
		if (e.getSource() == distributeRandomlyItem)
			distributeRandomly();
		if (e.getSource() == distributeRegularlyItem)
			distributeRegularly();
	}
	
	//Button actions
	public void calculateCharges()
	{
		printSeparator();
		NSquaredList nsquaredList = new NSquaredList(particles);
		System.out.println("Calculating charges");
		
		int height = simulationCanvas.getHeight();
		int width = simulationCanvas.getWidth();
		
		task = new CalculationTask(this,nsquaredList,width,height);
		task.addPropertyChangeListener(this);
		task.execute();
		
		
	}
	
	public void calculatePME()
	{
		//Copy the ParticleList list
		printSeparator();
		SPMEList pmeList = new SPMEList(particles,new SpaceSize(simulationCanvas.canvasSize.width,simulationCanvas.canvasSize.height));
		System.out.println("Calculating charges using smooth particle mesh ewald method");
		int height = simulationCanvas.getHeight();
		int width = simulationCanvas.getWidth(); 
		
		task = new CalculationTask(this,pmeList,width,height);
		task.addPropertyChangeListener(this);
		task.execute();
		
	}
	
	public void calculateFMA()
	{
		printSeparator();
		//Copy the ParticleList list
		FastMultipoleList fmList = new FastMultipoleList(particles,new SpaceSize(simulationCanvas.canvasSize.width,simulationCanvas.canvasSize.height));
		System.out.println("Calculating charges using Fast Multipole Algorithm");
		
		int height = simulationCanvas.getHeight();
		int width = simulationCanvas.getWidth();
		
		task = new CalculationTask(this,fmList,width,height);
		task.addPropertyChangeListener(this);
		task.execute();
		
		
	}
	public void distributeRandomly()
	{
		for(int i = 0; i < 50; i++)
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
	
	public void distributeRegularly()
	{
		particles.add(new Particle(simulationCanvas.getWidth()/4,simulationCanvas.getHeight()/5,Particle.DEFAULT_MASS,Particle.DEFAULT_CHARGE));
		particles.add(new Particle(simulationCanvas.getWidth()/4,2*simulationCanvas.getHeight()/5,Particle.DEFAULT_MASS,Particle.DEFAULT_CHARGE));
		particles.add(new Particle(simulationCanvas.getWidth()/4,3*simulationCanvas.getHeight()/5,Particle.DEFAULT_MASS,Particle.DEFAULT_CHARGE));

		particles.add(new Particle(3*simulationCanvas.getWidth()/4,simulationCanvas.getHeight()/5,Particle.DEFAULT_MASS,-Particle.DEFAULT_CHARGE));
		particles.add(new Particle(3*simulationCanvas.getWidth()/4,2*simulationCanvas.getHeight()/5,Particle.DEFAULT_MASS,-Particle.DEFAULT_CHARGE));
		particles.add(new Particle(3*simulationCanvas.getWidth()/4,3*simulationCanvas.getHeight()/5,Particle.DEFAULT_MASS,-Particle.DEFAULT_CHARGE));
		
		redraw();
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		//If progress has been made
        if ("progress" == e.getPropertyName()) {
            int progress = (Integer) e.getNewValue();
            progressBar.setValue(progress);        
        }
        //Check to see if the worker failed
        if ("state" == e.getPropertyName() && e.getNewValue().equals(SwingWorker.StateValue.DONE)){
            @SuppressWarnings("unchecked")
			SwingWorker<Void,Void> source = (SwingWorker<Void,Void>)e.getSource();  
            try {
                source.get();  
            } catch (ExecutionException ex) {  
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex.getCause());  
            } catch (InterruptedException ex) {  
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);  
            } catch (Exception ex) {
            	Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);  
            }
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
