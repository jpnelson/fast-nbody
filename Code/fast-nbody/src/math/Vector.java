package math;

public class Vector {
	public double x,y,z;
	public Vector(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector cross(Vector other)
	{
		return new Vector(y*other.z - z*other.y,z*other.x - x*other.z,x*other.y - y*other.x);
	}
	
	public double dot(Vector other)
	{	
		return x*other.x+y*other.y+z*other.z;
	}
}
