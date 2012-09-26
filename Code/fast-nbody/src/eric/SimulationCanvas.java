package eric;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

/**
 * 
 * SimulationCanvas - This class enables double buffering for a
 * simple simulation.  To use this class construct the canvas of
 * your desired size.  Draw the required fixed background using the Graphics 
 * object obtained from getBackgroundGraphics. Then in the simulation loop:
 *   + clearOffscreen 
 *   + either add images to the offscreen with drawImage or draw using the 
 *     Graphics object obtained via getOffscreenGraphic()
 *   + drawOffscreen
 *   
 * @author Eric McCreath
 * 
 * Copyright 2005, 2007
 *  
 */
public class SimulationCanvas extends JComponent implements MouseListener, MouseMotionListener {

	Dimension dim;
    Integer xdim, ydim; // the size of the Canvas
    private BufferedImage background;
    private BufferedImage offscreen;
    Multipole mp;

    public SimulationCanvas(Dimension d, Multipole m) {
        mp = m;
        dim = d;
        this.setSize(dim);
        this.setPreferredSize(dim);
        background = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_RGB);
        offscreen = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_RGB);
        
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    public void clearOffscreen() {
        Graphics g = offscreen.getGraphics();
        g.drawImage(background,0,0,null);
    }

    public Graphics2D getBackgroundGraphics() {
        return background.createGraphics();
        
    }

    public Graphics2D getOffscreenGraphics() {
        return offscreen.createGraphics();
    }
    
    public void drawImage(Image i, int x, int y) {
        Graphics g = offscreen.getGraphics();
        g.drawImage(i,x,y,null);
    }

    public void drawOffscreen() {
       Graphics g;
       g = this.getGraphics();
       g.drawImage(offscreen,0,0,null);
    }
    
    public void paint(Graphics g) {
    	g.drawImage(offscreen,0,0,null);
    }

	public void mouseClicked(MouseEvent me) {
		System.out.println("mouseClick : " + me.getX() + "," + me.getY() + " " + me.getButton());
		if (me.getButton() == MouseEvent.BUTTON1) {
		mp.p.add(new Particle(new Complex(me.getX(),me.getY()),1.0,1.0));
		mp.redraw();
		} else if (me.getButton() == MouseEvent.BUTTON3) {
			mp.p.add(new Particle(new Complex(me.getX(),me.getY()),-1.0,1.0));
			mp.redraw();
		}else if (me.getButton() == MouseEvent.BUTTON2) {
			double q = mp.p.charge(new Complex(me.getX(),me.getY()));
			double mq = mp.p.mchargeat(new Complex(me.getX(),me.getY()));
			System.out.println(q + " " + mq);
			
			
		}
		
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent arg0) {
	}

	public void mouseReleased(MouseEvent arg0) {
	}

	static int t = 0;
	
	public void mouseDragged(MouseEvent me) {
		if (t == 0)
		System.out.println("{" + me.getX() + ".0," + me.getY() + ".0},");
		t = (t + 1) % 100;
	}

	public void mouseMoved(MouseEvent arg0) {
	}

}