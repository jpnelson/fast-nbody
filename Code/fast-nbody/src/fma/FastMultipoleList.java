package fma;

import gui.SpaceSize;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import particles.Particle;
import particles.ParticleList;


import math.Complex;

public class FastMultipoleList extends ParticleList{
	public static int EXPANSION_TERMS = 20;
	public static int LEVEL_COUNT = 5; //should be about log_4(N) (G&R)
	SpaceSize windowSize;
	Mesh lowestLevelMesh;
	Mesh[] meshes = new Mesh[LEVEL_COUNT+1];
	
	public FastMultipoleList(ArrayList<Particle> particles, SpaceSize windowSize) {
		super(particles);
		this.windowSize = windowSize;
		
		init();

	}
	
	@Override
	public void init()
	{
		initialised = true;
		int lowestLevelBoxCount = (int)Math.pow(4.0, LEVEL_COUNT);
		//2^n is the number of times we've split horizontally. (2^n)^2 = 4^n
		int boxesOnSide = (int)Math.pow(2.0, LEVEL_COUNT);
		
		//Allocate each particle to it's appropriate lowest level box
		lowestLevelMesh = new Mesh(boxesOnSide,LEVEL_COUNT,EXPANSION_TERMS,windowSize);
		for(Particle p : this)
		{
			lowestLevelMesh.add(p);
		}
		
		//Form multipole expansions
		lowestLevelMesh.formMultipoleExpansions(EXPANSION_TERMS);
		
		//Save all the coarser meshes
		int l = lowestLevelMesh.level;
		meshes[l] = lowestLevelMesh;
		while(l > 0)
		{
			meshes[l-1] = meshes[l].makeCoarserMesh();
			l--;
		}
		

		/*if(c.multipoleExpansion.getNumerators().get(0).re() != 0.0)
			System.out.println(l+" sum: "+x + " "+y + " " + c.getX() + " "+ c.getY() + "| "+c.multipoleExpansion+",...");*/
		
		//Find the centers for each box
		meshes[1].meshCells[0][0].psiBar = new LocalExpansion(EXPANSION_TERMS, meshes[1].getCellCenter(0, 0));
		meshes[1].meshCells[0][1].psiBar = new LocalExpansion(EXPANSION_TERMS, meshes[1].getCellCenter(0, 1));
		meshes[1].meshCells[1][0].psiBar = new LocalExpansion(EXPANSION_TERMS, meshes[1].getCellCenter(1, 0));
		meshes[1].meshCells[1][1].psiBar = new LocalExpansion(EXPANSION_TERMS, meshes[1].getCellCenter(1, 1));
		for(l = 1; l < LEVEL_COUNT; l++) //don't do == LEVEL_COUNT, the last one //reuse l
		{
			for(int x = 0; x < meshes[l].boxesOnSide; x++)
			{
				for(int y = 0; y < meshes[l].boxesOnSide; y++)
				{
					Cell thisCell = meshes[l].meshCells[x][y];
					ArrayList<Cell> interactionList = meshes[l].getInteractionList(x,y);
					Complex thisCellCenter = meshes[l].getCellCenter(x, y);
					LocalExpansion sum = new LocalExpansion(EXPANSION_TERMS,thisCellCenter);
					
					for(Cell c : interactionList)
					{
						Complex cCenter = meshes[l].getCellCenter(c.getX(), c.getY());//meshes[l] meshes[LEVEL_COUNT]
						LocalExpansion cLocalExpand = new LocalExpansion(c.multipoleExpansion,cCenter.sub(thisCellCenter),EXPANSION_TERMS);
						sum = sum.add(cLocalExpand);//was thisCellCenter (20/9/12) TODO: check this line. Copied like Step 4
					}
					
					thisCell.psi = sum.add(thisCell.psiBar);
				}
			}
			for(int x = 0; x < meshes[l].boxesOnSide; x++)
			{
				for(int y = 0; y < meshes[l].boxesOnSide; y++)
				{
					Cell thisCell = meshes[l].meshCells[x][y];
					Cell childTopLeft = meshes[l+1].meshCells[x*2][y*2];
					Cell childTopRight = meshes[l+1].meshCells[x*2+1][y*2];
					Cell childBotLeft = meshes[l+1].meshCells[x*2][y*2+1];
					Cell childBotRight = meshes[l+1].meshCells[x*2+1][y*2+1];
					childTopLeft.psiBar = thisCell.psi.shift(meshes[l+1].getCellCenter(x*2  ,y*2));
					childTopRight.psiBar = thisCell.psi.shift(meshes[l+1].getCellCenter(x*2+1,y*2));
					childBotLeft.psiBar = thisCell.psi.shift(meshes[l+1].getCellCenter(x*2  ,y*2+1));
					childBotRight.psiBar = thisCell.psi.shift(meshes[l+1].getCellCenter(x*2+1,y*2+1));
				}
			}
			
		}
		
		//Step 4 (G&R)
		//Compute interactions at the finest mesh level
		for(int x = 0; x < meshes[LEVEL_COUNT].boxesOnSide; x++)
		{
			for(int y = 0; y < meshes[LEVEL_COUNT].boxesOnSide; y++)
			{
				Cell thisCell = meshes[LEVEL_COUNT].meshCells[x][y];
				ArrayList<Cell> interactionList = meshes[LEVEL_COUNT].getInteractionList(x,y);
				Complex thisCellCenter = meshes[LEVEL_COUNT].getCellCenter(x, y);
				LocalExpansion sum = new LocalExpansion(EXPANSION_TERMS,thisCellCenter);
				
				for(Cell c : interactionList)
				{
					Complex cCenter = meshes[LEVEL_COUNT].getCellCenter(c.getX(), c.getY());
					LocalExpansion cLocalExpansion = new LocalExpansion(c.multipoleExpansion,cCenter.sub(thisCellCenter),EXPANSION_TERMS);//was thisCellCenter
					sum = sum.add(cLocalExpansion);
				}
				sum = sum.add(thisCell.psiBar);
				thisCell.psi = sum;

			}
		}
	}
	
	public void debugDraw(Graphics g)
	{
		lowestLevelMesh.draw(g);
	}

	public void draw(Graphics2D g)
	{
		
		for(Particle p : this)
		{
			p.draw(g);
		}
	}
	
	//The actual procedure for calculating charges
	@Override
	public double potential(Complex position) {
		if(!initialised) init();
		double x = position.re();
		double y = position.im();
		int xIndex = (int) Math.floor(meshes[LEVEL_COUNT].boxesOnSide* (x / (double)(meshes[LEVEL_COUNT].meshSize.getWidth())));
		int yIndex = (int) Math.floor(meshes[LEVEL_COUNT].boxesOnSide* (y / (double)(meshes[LEVEL_COUNT].meshSize.getHeight())));
		Cell positionCell = meshes[LEVEL_COUNT].meshCells[xIndex][yIndex];
		double charge = positionCell.psi.potential(position);
		//Do all the near ones manually
		for(int i = -1; i <= 1 ; i++)
			for (int j = -1; j <= 1; j++)
		        charge += chargeFromBox(xIndex+i,yIndex+j,position);
	
		
		return charge;
	}
	
	private double chargeFromBox(int x, int y, Complex position)
	{
		if(x >= 0 && y >= 0 && x < meshes[LEVEL_COUNT].boxesOnSide && y < meshes[LEVEL_COUNT].boxesOnSide)
		{
			return meshes[LEVEL_COUNT].meshCells[x][y].particles.potential(position); //The old way
		}else{
			return 0;
		}
	}
}
