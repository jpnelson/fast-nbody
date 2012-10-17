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
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import fma.FastMultipoleList;


import particles.NSquaredList;
import particles.Particle;
import particles.ParticleList;
import pme.PMEList;
import pme.SPME3DList;
import pme.SPMEList;

public class GUI implements ActionListener, PropertyChangeListener{
	//Window
	static Dimension WINDOW_SIZE = new Dimension(512,512);
	static int RANDOM_PARTICLE_NUMBER = 50; //The number of particles to distribute randomly by default
	JFrame jFrame;
	JMenuBar jMenuBar;
	JMenu timingMenu;
	JMenu potentialsMenu;
	JMenu particlesMenu;
	JMenuItem calculateChargesItem;
	JMenuItem calculateFMAItem;
	JMenuItem calculatePMEItem;
	JMenuItem clearParticlesItem;
	JMenuItem distributeRandomlyItem;
	JMenuItem distributeNRandomlyItem;
	JMenuItem distributeRegularlyItem;
	
	JMenuItem timeBasicItem;
	JMenuItem timeFMAItem;
	JMenuItem timePMEItem;
	
	JProgressBar progressBar;
	//Graphics
	SimulationCanvas simulationCanvas;
	static float BRIGHTNESS = 0.5f;
	//Simulation
	public ArrayList<Particle> particles = new ArrayList<Particle>(); //This lists job is to keep track of mouse clicks and charges
	CalculationTask calcTask;
	TimingTask timeTask;

	
	public GUI()
	{
		//Create components
		jFrame = new JFrame("Fast N body simulation");
		jFrame.setSize(WINDOW_SIZE);
		jFrame.setLayout(new BorderLayout());
		jFrame.setResizable(false);
		simulationCanvas = new SimulationCanvas(WINDOW_SIZE,this);
		//Menu
		jMenuBar = new JMenuBar();
		potentialsMenu = new JMenu("Potentials");
		particlesMenu = new JMenu("Particles");
		timingMenu = new JMenu("Timings");
		
		calculateChargesItem = new JMenuItem("Calculate potentials (Basic algorithm)");
		calculateChargesItem.addActionListener(this);
		
		calculateFMAItem = new JMenuItem("Calculate potentials (Fast multipole algorithm)");
		calculateFMAItem.addActionListener(this);
		
		calculatePMEItem = new JMenuItem("Calculate potentials (Particle mesh ewald method)");
		calculatePMEItem.addActionListener(this);
		
		distributeRandomlyItem = new JMenuItem("Distribute "+RANDOM_PARTICLE_NUMBER+" particles randomly");
		distributeRandomlyItem.addActionListener(this);
		
		distributeNRandomlyItem = new JMenuItem("Distribute N particles randomly");
		distributeNRandomlyItem.addActionListener(this);
		
		distributeRegularlyItem = new JMenuItem("Distribute particles regularly");
		distributeRegularlyItem.addActionListener(this);
		
		timeBasicItem = new JMenuItem("Time the Basic algorithm");
		timeBasicItem.addActionListener(this);
		
		timeFMAItem = new JMenuItem("Time the Fast multipole algorithm");
		timeFMAItem.addActionListener(this);
		
		timePMEItem = new JMenuItem("Time the Particle mesh ewald method");
		timePMEItem.addActionListener(this);
		
		
		clearParticlesItem = new JMenuItem("Clear particles");
		clearParticlesItem.addActionListener(this);

		potentialsMenu.add(calculateChargesItem);
		potentialsMenu.add(calculateFMAItem);
		potentialsMenu.add(calculatePMEItem);

		particlesMenu.add(distributeRandomlyItem);
		particlesMenu.add(distributeNRandomlyItem);
		particlesMenu.add(distributeRegularlyItem);
		particlesMenu.add(clearParticlesItem);
		
		timingMenu.add(timeBasicItem);
		timingMenu.add(timeFMAItem);
		timingMenu.add(timePMEItem);

		//Interface
		progressBar = new JProgressBar(0,100);

		//Add the menus to the menubar
		jMenuBar.add(particlesMenu);
		jMenuBar.add(potentialsMenu);
		jMenuBar.add(timingMenu);
		
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
		System.out.println("___________________");
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
		if (e.getSource() == distributeNRandomlyItem)
			distributeNRandomly();
		if (e.getSource() == distributeRandomlyItem)
			distributeRandomly();
		if (e.getSource() == distributeRegularlyItem)
			distributeRegularly();
		if (e.getSource() == timeBasicItem)
			timeBasic();
		if (e.getSource() == timeFMAItem)
			timeFMA();
		if (e.getSource() == timePMEItem)
			timePME();
	}
	//Button actions
	private void timeList(ParticleList pl)
	{
		printSeparator();
		System.out.println("Timing basic algorithm");
		
		int height = simulationCanvas.getHeight();
		int width = simulationCanvas.getWidth();
		
		timeTask = new TimingTask(this,pl,width,height);
		timeTask.addPropertyChangeListener(this);
		timeTask.execute();
	}
	
	private void calculateList(ParticleList pl)
	{
		printSeparator();
		System.out.println("Calculating charges");
		
		int height = simulationCanvas.getHeight();
		int width = simulationCanvas.getWidth();
		
		calcTask = new CalculationTask(this,pl,width,height);
		calcTask.addPropertyChangeListener(this);
		calcTask.execute();
	}
	private void timeBasic()
	{
		timeList(new NSquaredList(particles));
	}
	
	private void timeFMA()
	{
		timeList(new FastMultipoleList(particles,new SpaceSize(simulationCanvas.canvasSize.width,simulationCanvas.canvasSize.height)));
	}
	
	private void timePME()
	{
		timeList(new SPMEList(particles,new SpaceSize(simulationCanvas.canvasSize.width,simulationCanvas.canvasSize.height)));
	}
	public void calculateCharges()
	{
		calculateList(new NSquaredList(particles));
	}
	
	public void calculatePME()
	{
		calculateList(new SPMEList(particles,new SpaceSize(simulationCanvas.canvasSize.width,simulationCanvas.canvasSize.height)));
	}
	
	public void calculateFMA()
	{
		calculateList(new FastMultipoleList(particles,new SpaceSize(simulationCanvas.canvasSize.width,simulationCanvas.canvasSize.height)));		
	}
	private void distributeN(int N)
	{
		for(int i = 0; i < N; i++)
		{
			Particle p;
			int charge = Math.random() < 0.5? -Particle.DEFAULT_CHARGE:Particle.DEFAULT_CHARGE;
			p = new Particle(Math.random()*simulationCanvas.getWidth(),Math.random()*simulationCanvas.getWidth(),Particle.DEFAULT_MASS,charge);
			particles.add(p);
		}
		simulationCanvas.repaint();
	}
	public void distributeNRandomly()
	{
		try{
			int N = Integer.parseInt(JOptionPane.showInputDialog(jFrame,"Number of particles to distribute"));
			distributeN(N);
			simulationCanvas.repaint();
		}catch(NumberFormatException e){
			JOptionPane.showMessageDialog(jFrame, "Invalid integer");
		}
	}
	public void distributeRandomly()
	{
		distributeN(RANDOM_PARTICLE_NUMBER);
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
