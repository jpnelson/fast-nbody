package gui;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JCheckBoxMenuItem;
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
import pme.SPMEList;

public class GUI implements ActionListener, PropertyChangeListener{
	//Window
	static Dimension WINDOW_SIZE = new Dimension(512,512);
	static int RANDOM_PARTICLE_NUMBER = 50; //The number of particles to distribute randomly by default
	
	//Benchmarking
	static int BENCHMARK_INCREMENT = 1000;
	static int BENCHMARK_MAXIMUM = 20000;
	static int BENCHMARK_MINIMUM = 0;
	TimingTask currentBenchmarkingTask;
	Class<? extends ParticleList> benchmarkClass;
	String currentBenchmarkLogFileName;
	
	JFrame jFrame;
	JMenuBar jMenuBar;
	
	JMenu timingMenu;
	JMenu timingOnceSubMenu;
	JMenu timingBenchmarkSubMenu;
	
	JMenu potentialsMenu;
	JMenu particlesMenu;
	JMenuItem calculateChargesItem;
	JMenuItem calculateFMAItem;
	JMenuItem calculatePMEItem;
	JMenuItem clearParticlesItem;
	JMenuItem distributeRandomlyItem;
	JMenuItem distributeNItem;
	JMenuItem distributeRegularlyItem;
	JCheckBoxMenuItem requireNeutralItem;
	
	JMenuItem timeBasicItem;
	JMenuItem timeFMAItem;
	JMenuItem timePMEItem;
	JMenuItem benchmarkBasicItem;
	JMenuItem benchmarkPMEItem;
	JMenuItem benchmarkFMAItem;
	
	JProgressBar progressBar;
	//Graphics
	SimulationCanvas simulationCanvas;
	static float BRIGHTNESS = 0.5f;
	//Simulation
	public ArrayList<Particle> particles = new ArrayList<Particle>(); //This lists job is to keep track of mouse clicks and charges
	CalculationTask calcTask;
	TimingTask timeTask;
	private boolean requireNeutralSystem = false;

	
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
		timingOnceSubMenu = new JMenu("Time on this configuration");
		timingBenchmarkSubMenu = new JMenu("Benchmark");
		
		calculateChargesItem = new JMenuItem("Calculate potentials (Basic algorithm)");
		calculateChargesItem.addActionListener(this);
		
		calculateFMAItem = new JMenuItem("Calculate potentials (Fast multipole algorithm)");
		calculateFMAItem.addActionListener(this);
		
		calculatePMEItem = new JMenuItem("Calculate potentials (Particle mesh ewald method)");
		calculatePMEItem.addActionListener(this);
		
		requireNeutralItem = new JCheckBoxMenuItem("Require neutral distribution",requireNeutralSystem);
		requireNeutralItem.addActionListener(this);

		distributeRandomlyItem = new JMenuItem("Distribute "+RANDOM_PARTICLE_NUMBER+" particles randomly");
		distributeRandomlyItem.addActionListener(this);
		
		distributeNItem = new JMenuItem("Distribute N particles randomly");
		distributeNItem.addActionListener(this);
		
		distributeRegularlyItem = new JMenuItem("Distribute particles regularly");
		distributeRegularlyItem.addActionListener(this);
		
		
		//Timing menu
		timeBasicItem = new JMenuItem("Time the Basic algorithm");
		timeBasicItem.addActionListener(this);
		
		timeFMAItem = new JMenuItem("Time the Fast multipole algorithm");
		timeFMAItem.addActionListener(this);
		
		timePMEItem = new JMenuItem("Time the Particle mesh ewald method");
		timePMEItem.addActionListener(this);
		
		benchmarkFMAItem = new JMenuItem("Benchmark the Fast multipole algorithm");
		benchmarkFMAItem.addActionListener(this);
		
		benchmarkBasicItem = new JMenuItem("Benchmark the Basic algorithm");
		benchmarkBasicItem.addActionListener(this);

		benchmarkPMEItem = new JMenuItem("Benchmark the Particle mesh ewald method");
		benchmarkPMEItem.addActionListener(this);

		clearParticlesItem = new JMenuItem("Clear particles");
		clearParticlesItem.addActionListener(this);

		potentialsMenu.add(calculateChargesItem);
		potentialsMenu.add(calculateFMAItem);
		potentialsMenu.add(calculatePMEItem);

		particlesMenu.add(distributeRandomlyItem);
		particlesMenu.add(distributeNItem);
		particlesMenu.add(distributeRegularlyItem);
		particlesMenu.add(requireNeutralItem);
		particlesMenu.add(clearParticlesItem);
		
		
		timingOnceSubMenu.add(timeBasicItem);
		timingOnceSubMenu.add(timeFMAItem);
		timingOnceSubMenu.add(timePMEItem);
		
		timingBenchmarkSubMenu.add(benchmarkBasicItem);
		timingBenchmarkSubMenu.add(benchmarkFMAItem);
		timingBenchmarkSubMenu.add(benchmarkPMEItem);
		
		timingMenu.add(timingOnceSubMenu);
		timingMenu.add(timingBenchmarkSubMenu);

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
		System.out.println("[GUI] ___________________");
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
		if (e.getSource() == distributeNItem)
			distributeN();
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
		if (e.getSource() == benchmarkBasicItem)
			benchmarkList(NSquaredList.class);
		if (e.getSource() == benchmarkFMAItem)
			benchmarkList(FastMultipoleList.class);
		if (e.getSource() == benchmarkPMEItem)
			benchmarkList(SPMEList.class);
		if (e.getSource() == requireNeutralItem)
			requireNeutralSystem = !requireNeutralSystem;
	}
	//Button actions

	
	private void calculateList(ParticleList pl)
	{
		printSeparator();
		System.out.println("[GUI] Calculating potentials");
		
		int height = simulationCanvas.getHeight();
		int width = simulationCanvas.getWidth();
		
		calcTask = new CalculationTask(this,pl,width,height);
		calcTask.addPropertyChangeListener(this);
		calcTask.execute();
	}
	
	/*----------Timing and benchmarking----------*/
	private void timeList(ParticleList pl)
	{
		printSeparator();

		int height = simulationCanvas.getHeight();
		int width = simulationCanvas.getWidth();
		
		timeTask = new TimingTask(this,pl,width,height);
		timeTask.addPropertyChangeListener(this);
		timeTask.execute();
	}
	
	/*Do one iteration of the benchmarking process (calculate once)
	 * when the currentBenchmarkingTask execution is complete, the GUI will receive a call at the property change event
	 * This enables the interface to be responsive during benchmarking iterations.
	 */
	private void doBenchmarkIteration(Class<? extends ParticleList> c)
	{
		distributeN(BENCHMARK_INCREMENT); //Start filling up the particles array list
		ParticleList pl = new NSquaredList(); //Only initialise it if we have to. If it didn't get initialised, the method fails.
		
		//Create a particle list from the class object we're given
		try {
			pl = c.getConstructor(new Class[]{ArrayList.class}).newInstance(particles);
		} catch (Exception e){
			//Assume we used the wrong constructor, not a basic list
			try {
				pl = c.getConstructor(new Class[]{ArrayList.class,SpaceSize.class}).newInstance(particles,
						new SpaceSize(simulationCanvas.canvasSize.width,simulationCanvas.canvasSize.height));
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		
		//The actual work to be timed
		int height = simulationCanvas.getHeight();
		int width = simulationCanvas.getWidth();
		currentBenchmarkingTask = new TimingTask(this,pl,width,height,currentBenchmarkLogFileName);
		currentBenchmarkingTask.addPropertyChangeListener(this);
		currentBenchmarkingTask.execute();
	}
	
	private static String getNewLogFileName(Class<? extends ParticleList> c)
	{
		//Check change the variable name if it already exists. Naming scheme like: benchmark_list(1).csv
		boolean fileExists = true;
		int num=0;
		String fileName="";
		
		while(fileExists){
			if(num==0){
				fileName = "benchmarks/benchmark_"+c.getSimpleName()+".csv";
			}else{
				fileName = "benchmarks/benchmark_"+c.getSimpleName()+"("+num+")"+".csv";
			}
			File f = new File(fileName);
			fileExists = f.exists();
			num++;
		}
		File f = new File("benchmarks");
		f.mkdirs(); //Ensure the folder is there
		return fileName;
	}
	
	private void benchmarkList(Class<? extends ParticleList> c)
	{
		currentBenchmarkLogFileName = getNewLogFileName(c); //The name of the log file for this benchmark
		benchmarkClass = c; //Save this in the GUI so we know which class we're benchmarking at the moment
		clearParticles();
		distributeN(BENCHMARK_MINIMUM); //Make sure we start at the minimum
		doBenchmarkIteration(c);					
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
	
	private static double calculateChargeSum(ArrayList<Particle> list)
	{
		double sum=0;
		for(Particle p : list){
			sum += p.getCharge();
		}
		return sum;
	}
	
	public void calculatePME()
	{
		if(calculateChargeSum(particles) != 0)
			JOptionPane.showMessageDialog(jFrame,
					"Warning: The particle mesh ewald method is only effective for neutral systems, but charge sum="+calculateChargeSum(particles),
					"Non neutral system", JOptionPane.WARNING_MESSAGE);
		calculateList(new SPMEList(particles,new SpaceSize(simulationCanvas.canvasSize.width,simulationCanvas.canvasSize.height)));
	}
	
	public void calculateFMA()
	{
		calculateList(new FastMultipoleList(particles,new SpaceSize(simulationCanvas.canvasSize.width,simulationCanvas.canvasSize.height)));		
	}
	private void distributeN(int N)
	{
		if(requireNeutralSystem && N%2==1)
			N++;//Don't allow odd number of particles in a neutral system
		for(int i = 0; i < N; i++)
		{
			Particle p;
			double chance = requireNeutralSystem ? i % 2 : Math.random() * 2; //Alternate if we require a neutral system
			int charge =chance < 0.5? -Particle.DEFAULT_CHARGE:Particle.DEFAULT_CHARGE;
			p = new Particle(Math.random()*simulationCanvas.getWidth(),Math.random()*simulationCanvas.getWidth(),Particle.DEFAULT_MASS,charge);
			particles.add(p);
		}
		simulationCanvas.repaint();
	}
	private void distributeN()
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
		System.out.println("[GUI] Clearing particles");
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
        //If we're benchmarking the next iteration
        if(currentBenchmarkingTask != null){
	        if ("state" == e.getPropertyName() && e.getSource().equals(currentBenchmarkingTask) && e.getNewValue().equals(SwingWorker.StateValue.DONE))
	        {
	        	if(particles.size() < BENCHMARK_MAXIMUM){
	        		doBenchmarkIteration(benchmarkClass);
	        	}else{
	        		//We've benchmarked the last iteration
	        		System.out.println("[GUI] Benchmarking complete");
	        		benchmarkClass = null; //reset if for safety
	        		currentBenchmarkingTask = null; //we use whether it is null or not as a way of telling if we're benchmarking at the moment
	        	}
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
		System.out.println("[GUI] Max charge is "+maxCharge);
		
		
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
