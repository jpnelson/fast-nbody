package math;

public class Vector {
	public double x,y,z;
	public Vector(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector(double x, double y)
	{
		this.x = x;
		this.y = y;
		this.z = 0;
	}
	
	public Vector cross(Vector other)
	{
		return new Vector(y*other.z - z*other.y,z*other.x - x*other.z,x*other.y - y*other.x);
	}
	
	public double dot(Vector other)
	{	
		return x*other.x+y*other.y+z*other.z;
	}
	
	public double mag()
	{	
		return Math.sqrt(x*x+y*y+z*z);
	}
	
	public Vector scale(double c){
		return new Vector(x*c,y*c,z*c);
	}
	
	public Vector sub(Vector other){
		return new Vector(x-other.x,y-other.y,z-other.z);
	}
	
	@Override
	public String toString(){
		return "("+x+", "+y+", "+z+")";
	}
}
