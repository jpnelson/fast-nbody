package particles;

import java.awt.Color;
import java.awt.Graphics2D;

import math.Complex;
import math.Vector;

public class Particle3D {
	public static int DEFAULT_CHARGE = 1;
	public static int DEFAULT_MASS = 1;
	Vector position;
	double mass;
	double charge;
	private double forceX = 0;
	private double forceY = 0;
	private double forceZ = 0;
	public Particle3D(double x, double y,double z, double mass, double charge) {
		position = new Vector(x,y,z);
		this.mass = mass;
		this.charge = charge;
	}
	
	public void draw(Graphics2D g)
	{
		if(charge>0)
			g.setColor(Color.BLUE);
		else
			g.setColor(Color.RED);
		g.drawOval((int)position.x, (int)position.y, 6, 6);
	}
	
	public Vector getPosition(){
		return position;
	}
	public double getCharge(){
		return charge;
	}
	public double getMass(){
		return mass;
	}

	public void addToForce(double x, double y, double z) {
		forceX += x;
		forceY += y;
		forceZ += z;
	}
	public Vector getForce()
	{
		return new Vector(forceX,forceY,forceZ);
	}
}
