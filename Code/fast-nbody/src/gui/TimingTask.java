package gui;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingWorker;

import particles.Particle;
import particles.ParticleList;

import math.Complex;


public class TimingTask extends SwingWorker<Void,Void>{
	int width,height;
	ParticleList particleList;
	GUI gui;
	public TimingTask(GUI gui, ParticleList particleList, int width, int height)
	{
		super();
		this.gui = gui;
		this.width = width;
		this.height = height;
		this.particleList = particleList;
	}
	@Override
	protected Void doInBackground() throws Exception {
		setProgress(0);
		int progress = 0;
		//Calculate the charges
		int i=0;
		for(Particle p : particleList){
			particleList.potential(p.getPosition());
			i++;
			progress = (int) (100 * (double)i / (double)particleList.size()-1);
			setProgress(Math.max(Math.min(progress, 100), 0));
		}
        return null;
	}
	
    @Override
    public void done() {
    	System.out.println("Timing completed");
        Toolkit.getDefaultToolkit().beep();
    }
}
