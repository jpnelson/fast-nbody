package gui;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.SwingWorker;

import particles.ParticleList;

import math.Complex;


public class CalculationTask extends SwingWorker<Void,Void>{
	ArrayList<Double> charges;
	int width,height;
	ParticleList particleList;
	GUI gui;
	public CalculationTask(GUI gui, ParticleList particleList, int width, int height)
	{
		super();
		this.gui = gui;
		this.charges = new ArrayList<Double>();
		this.width = width;
		this.height = height;
		this.particleList = particleList;
	}
	@Override
	protected Void doInBackground() throws Exception {
		setProgress(0);
		int progress = 0;
		//Calculate the charges
		for (int y=0;y<height;y++) {
			for (int x=0;x<width;x++) {
				double chargeAtIJ = particleList.charge(new Complex(x,y));
				charges.add(chargeAtIJ);
			}
			progress = (int) (((double)y+1)/((double)height) * 100);
			setProgress(Math.min(progress, 100));
		}
        return null;
	}
	
    @Override
    public void done() {
		gui.visualiseCharges(charges);
    	System.out.println("Calculation worker completed");
        Toolkit.getDefaultToolkit().beep();
    }
}
