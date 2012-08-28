package gui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

import particles.Particle;
import particles.ParticleList;

public class SimulationCanvas extends Canvas implements MouseListener, MouseMotionListener{
	private static final long serialVersionUID = 1536451581174113704L;
	Dimension canvasSize;
    //Simulation
    GUI gui;
    
    public SimulationCanvas(Dimension d, GUI gui) {
    	this.setSize(d);
        this.setPreferredSize(d);
        this.gui = gui;
    	canvasSize = d;
    	
    	this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    
    public void paint(Graphics g) {
		g.setColor(Color.white);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		gui.particleList.draw((Graphics2D) g);
    }
    


	@Override
	public void mouseReleased(MouseEvent e) {
		//Add a particle
		Particle newParticle = new Particle(e.getX(),e.getY());
		gui.particleList.add(newParticle);
		gui.redraw();
		
		System.out.println("Mouse click");		
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
