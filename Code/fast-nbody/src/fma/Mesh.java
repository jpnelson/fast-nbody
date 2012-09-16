package fma;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import math.Complex;

import particles.Particle;

public class Mesh {
	int boxesOnSide;
	final int level;
	ArrayList<Particle>[][] meshBoxes;
	Dimension meshSize;
	MultipoleExpansion[][] multipoleExpansions;


	public Mesh(int boxesOnSide, int level, Dimension meshSize)
	{
		this.boxesOnSide = boxesOnSide;
		this.meshSize = meshSize;
		this.level = level;
		meshBoxes = new ArrayList[boxesOnSide][boxesOnSide];
		//Initialize the boxes to be empty, and the multipole expansions to be empty too
		for(int x = 0; x < boxesOnSide; x++)
		{
			for(int y = 0; y < boxesOnSide; y++)
			{
				meshBoxes[x][y] = new ArrayList<Particle>();
				multipoleExpansions[x][y] = new MultipoleExpansion();
			}
		}
	}
	
	//Allocates a particle to one of the boxes
	public void add(Particle p)
	{
		int xCoordinate = (int) Math.floor(p.getPosition().re() / (meshSize.width / boxesOnSide));
		int yCoordinate = (int) Math.floor(p.getPosition().im() / (meshSize.height / boxesOnSide));
		
		meshBoxes[xCoordinate][yCoordinate].add(p);
		System.out.println(p.getPosition().toString() + " in box "+xCoordinate+","+yCoordinate);
	}
	
	//When we're done adding particles we calculate multipole expansions for each box
	public void formMultipoleExpansions(int numberOfTerms)
	{
		for(int x = 0; x < boxesOnSide; x++)
		{
			for(int y = 0; y < boxesOnSide; y++)
			{
				int boxWidth = (meshSize.width/boxesOnSide);
				int boxHeight = (meshSize.height/boxesOnSide);
				int centerX = x * boxWidth + (boxWidth/2);
				int centerY = y * boxHeight + (boxHeight/2);
				Complex center = new Complex(centerX,centerY);
				multipoleExpansions[x][y] = new MultipoleExpansion(meshBoxes[x][y],center,numberOfTerms);
			}
		}
	}
	
	//Create the next highest mesh, using Lemma 2.3 (G&R)
	public Mesh makeCoarserMesh()
	{
		Mesh coarserMesh = new Mesh(boxesOnSide/2,level-1,meshSize);
		//Don't worry about adding particles, just concerned with the Multipole expansions
		for(int x = 0; x < coarserMesh.boxesOnSide; x++)
		{
			for(int y = 0; y < coarserMesh.boxesOnSide; y++)
			{
				int topLeftX = x*2;
				int topLeftY = y*2;
				//The 4 corners are at topLeftX,topLeftY +0,1
				//multipoleExpansions[topLeftX][topLeftY].shift
			}
		}
		
		
		return coarserMesh;
	}
	
	
	public void draw(Graphics g)
	{
		//Called once at the end of calculation
		for(int x = 0; x < boxesOnSide; x++)
		{
			for(int y = 0; y < boxesOnSide; y++)
			{
				double boxWidth = meshSize.width / boxesOnSide;
				double boxHeight = meshSize.height / boxesOnSide;
				g.setColor(Color.white);
				g.drawRect((int)(x*boxWidth), (int)(y*boxHeight), (int)boxWidth, (int)boxHeight);
			}
		}
	}
}
