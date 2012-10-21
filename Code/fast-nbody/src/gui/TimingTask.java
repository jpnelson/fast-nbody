package gui;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import javax.swing.SwingWorker;

import math.Complex;
import particles.Particle;
import particles.ParticleList;


/*
 * A task for measuring the performance of the various algorithms implemented
 * Also used for the benchmarking of these algorithms over random configurations of particles, of varying sizes, for graphing.
 * In this case it outputs .csv files. 
 */
public class TimingTask extends SwingWorker<Void,Void>{
	int width,height;
	static int BENCHMARK_REPS = 1;
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
		boolean benchmarking = outputFile != ""; //This task is used for benchmarking and timing
		int repetitions = benchmarking ? BENCHMARK_REPS : 1; //Only do one rep if we're timing
		long[] initTimes = new long[repetitions];
		long[] totalTimes = new long[repetitions];
		for(int rep = 0; rep < repetitions; rep++){
			setProgress(0);
			int progress = 0;
			//Calculate the charges
			int i=0;
			
			long startTime = System.currentTimeMillis();
			//Initialisation
			particleList.init();
			long endInitTime = System.currentTimeMillis();
			
			//Other
			double totalPotential = 0;
			double[] potentials = new double[particleList.size()];
			for(Particle p : particleList){
				potentials[i] = particleList.potential(p.getPosition().scale(0.99)); //We get infinity if we put it directly on top
				totalPotential += potentials[i];
				i++;
				progress = (int) (100 * (double)i / (double)particleList.size()-1);
				setProgress(Math.max(Math.min(progress, 100), 0)*(rep+1) / repetitions);
			}
			double averagePotential = totalPotential/(double)particleList.size();
			double stdDev = 0;
			for(int j = 0; j<particleList.size(); j++) stdDev += Math.abs(potentials[j]-averagePotential) / (double)(particleList.size());
			System.out.format("[TimingTask] average potential: %.20f %n",averagePotential);
			System.out.println("[TimingTask] std dev potentia: "+stdDev);
			long endTime = System.currentTimeMillis();
			
			long initTime = (endInitTime-startTime);
			long totalTime = (endTime-startTime);
			
			//Save each result in an array
			initTimes[rep] = initTime;
			totalTimes[rep] = totalTime;
			System.out.println("[TimingTask] N="+particleList.size()+": Rep " +(rep+1)+"/"+repetitions);
		}
			//Output to a file if one was specified, otherwise go to standard output
			if(benchmarking){
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outputFile, true)));
				if(repetitions > 1)
				{
					//Calculate the standard deviations for this rep
					double totalStdDev = 0;
					double initStdDev = 0;
					for(int rep = 0; rep < repetitions; rep++)
					{
						totalStdDev += Math.abs(totalTimes[rep] - average(totalTimes)) / (double)repetitions; //Average distance from mean
						initStdDev += Math.abs(initTimes[rep] - average(initTimes)) / (double)repetitions; //Average distance from mean
					}
					out.println(particleList.size()+","+average(totalTimes)+","+totalStdDev+","+average(initTimes)+","+initStdDev); //Only do 2 columns if there was 1 iteration

				}else{
					out.println(particleList.size()+","+totalTimes[0]+","+initTimes[0]); //Only do 2 columns if there was 1 iteration
				}
				out.close();
			}
			System.out.println("[Timing Task] N="+particleList.size()+"\t Total time: "+average(totalTimes) + 
					"\t Initialisation time: "+average(initTimes)+" ms"); //If it's a timing task, the average will be the avg from one array
	        return null;
	}
	
	private static double average(long[] a){
		double average = 0;
		for(int i = 0; i < a.length; i++){
			average += (double)a[i] / (double)(a.length);
		}
		return average;
	}
	
    @Override
    public void done() {
    	//Do nothing
    }
}
