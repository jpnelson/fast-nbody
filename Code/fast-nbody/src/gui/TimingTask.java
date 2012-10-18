package gui;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import javax.swing.SwingWorker;
import particles.Particle;
import particles.ParticleList;


/*
 * A task for measuring the performance of the various algorithms implemented
 * Also used for the benchmarking of these algorithms over random configurations of particles, of varying sizes, for graphing.
 * In this case it outputs .csv files. 
 */
public class TimingTask extends SwingWorker<Void,Void>{
	int width,height;
	ParticleList particleList;
	GUI gui;
	String outputFile="";
	public TimingTask(GUI gui, ParticleList particleList, int width, int height)
	{
		super();
		this.gui = gui;
		this.width = width;
		this.height = height;
		this.particleList = particleList;
	}
	public TimingTask(GUI gui, ParticleList particleList, int width, int height, String outputFile)
	{
		super();
		this.gui = gui;
		this.width = width;
		this.height = height;
		this.particleList = particleList;
		this.outputFile = outputFile;
	}
	@Override
	protected Void doInBackground() throws Exception {
		setProgress(0);
		int progress = 0;
		//Calculate the charges
		int i=0;
		
		long startTime = System.currentTimeMillis();
		//Initialisation
		particleList.init();
		long endInitTime = System.currentTimeMillis();
		for(Particle p : particleList){
			particleList.potential(p.getPosition());
			i++;
			progress = (int) (100 * (double)i / (double)particleList.size()-1);
			setProgress(Math.max(Math.min(progress, 100), 0));
		}
		long endTime = System.currentTimeMillis();
		
		long initTime = (endInitTime-startTime);
		long totalTime = (endTime-startTime);
		//Output to a file if one was specified, otherwise go to standard output
		if(outputFile != ""){
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outputFile, true)));
			out.println(particleList.size()+", "+totalTime+","+initTime);
			out.close();
		}
		System.out.println("[Timing Task] N="+particleList.size()+"\t Total time: "+totalTime + 
				"\t Initialisation time: "+initTime+" ms");
        return null;
	}
	
    @Override
    public void done() {
    	//Do nothing
    }
}
