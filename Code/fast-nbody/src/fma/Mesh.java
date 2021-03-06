package fma;

import gui.SpaceSize;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import math.Complex;

import particles.Particle;

public class Mesh {
	int boxesOnSide;
	final int level;
	final int cellExpansionTerms;
	Cell[][] meshCells;
	SpaceSize meshSize;

	//TODO: this only really makes sense when level=0, otherwise we *could* make a 'wrong' mesh
	public Mesh(int boxesOnSide, int level, int cellExpansionTerms, SpaceSize meshSize)
	{
		this.boxesOnSide = boxesOnSide;
		this.meshSize = meshSize;
		this.level = level;
		this.cellExpansionTerms = cellExpansionTerms;
		meshCells = new Cell[boxesOnSide][boxesOnSide];
		//Initialize the cells to be empty, and the multipole expansions to be empty too
		for(int x = 0; x < boxesOnSide; x++)
		{
			for(int y = 0; y < boxesOnSide; y++)
			{
				Complex cellCenter = getCellCenter(x,y);
				meshCells[x][y] = new Cell(x,y,cellExpansionTerms,cellCenter);
			}
		}
	}
	
	//Allocates a particle to one of the boxes
	public void add(Particle p)
	{
		int xCoordinate = (int) Math.floor(p.getPosition().re() / (meshSize.getWidth() / (double)(boxesOnSide)));
		int yCoordinate = (int) Math.floor(p.getPosition().im() / (meshSize.getHeight() / (double)(boxesOnSide)));
		
		meshCells[xCoordinate][yCoordinate].add(p);
		//System.out.println("[Mesh] "+p.getPosition().toString() + " in cell "+xCoordinate+","+yCoordinate);
	}
	
	//When we're done adding particles we calculate multipole expansions for each box
	public void formMultipoleExpansions(int numberOfTerms)
	{
		for(int x = 0; x < boxesOnSide; x++)
		{
			for(int y = 0; y < boxesOnSide; y++)
			{
				meshCells[x][y].multipoleExpansion = new MultipoleExpansion(meshCells[x][y].particles,getCellCenter(x,y),numberOfTerms);
			}
		}
	}
	//gets the parent cell's coordinates in the parent mesh's coordinate system
	private static Pair<Integer,Integer> getParentCoords(int x, int y)
	{
		int parentX = (x - x%2)/2;
		int parentY = (y - y%2)/2;
		return new Pair<Integer,Integer>(parentX,parentY);
	}
	//Returns a cell's center in pixel like coordinates
	public Complex getCellCenter(int x, int y)
	{
		
		double boxWidth =  (double)(meshSize.getWidth()) / (double)(boxesOnSide);
		double boxHeight =  (double)(meshSize.getHeight()) / (double)(boxesOnSide);
		double xCenter = x*boxWidth + boxWidth/2;
		double yCenter = y*boxWidth + boxHeight/2;

		return new Complex(xCenter,yCenter);
	}
	//for use in the getInteractionList function, to determine if well separated
	private static int distance(int x1, int y1, int x2, int y2)
	{
		return Math.max(Math.abs(x1-x2),Math.abs(y1-y2));
	}
	//Returns boxes that are well separated and children of the parent's nearest neighbours
	public ArrayList<Cell> getInteractionList(int x, int y)
	{
		ArrayList<Cell> interactionList = new ArrayList<Cell>();
		Pair<Integer,Integer> thisParentCoords = getParentCoords(x,y);
		for (int dx=-1; dx<=1;dx++)
		{
			for (int dy=-1; dy<=1;dy++)
			{
				Pair<Integer,Integer> parentNearestNeighbour = new Pair<Integer,Integer>(thisParentCoords.getFirst()+dx,thisParentCoords.getSecond()+dy);
				int parentTopLeftX = parentNearestNeighbour.getFirst()*2; //In this mesh's coordinates
				int parentTopLeftY = parentNearestNeighbour.getSecond()*2; //In this mesh's coordinates
				if(parentTopLeftX+1 <= boxesOnSide && parentTopLeftX >= 0){ //If the parent's children are in this mesh (side cases)
					if(parentTopLeftY+1 <= boxesOnSide && parentTopLeftY >= 0){ //If the parent would be part of this' coarser mesh
						for(int childDX = 0; childDX <= 1; childDX++)
						{
							for(int childDY = 0; childDY <= 1; childDY++)
							{
								int childX = parentTopLeftX + childDX;
								int childY = parentTopLeftY + childDY;
								if(distance(childX,childY,x,y) > 1) //if well separated
								{
									interactionList.add(meshCells[childX][childY]);
								}
							}
						}
						
					}
				}
			}
		}
		
		return interactionList;
	}
	
	
	//Create the next highest mesh, using Lemma 2.3 (G&R)
	public Mesh makeCoarserMesh()
	{
		Mesh coarserMesh = new Mesh(boxesOnSide/2,level-1,cellExpansionTerms,meshSize);
		double coarseBoxWidth = (double) (coarserMesh.meshSize.getWidth()/(double)(coarserMesh.boxesOnSide));
		double coarseBoxHeight = (double) (coarserMesh.meshSize.getHeight()/(double)(coarserMesh.boxesOnSide));
		
		//Don't worry about adding particles, just concerned with the Multipole expansions
		for(int x = 0; x < coarserMesh.boxesOnSide; x++)
		{
			for(int y = 0; y < coarserMesh.boxesOnSide; y++)
			{
				int topLeftX = x*2;
				int topLeftY = y*2;
				//The 4 corners are at topLeftX,topLeftY +0,1
				Complex newCenter = coarserMesh.getCellCenter(x, y);
				MultipoleExpansion combinedMultipoleExpansion = new MultipoleExpansion(meshCells[topLeftX][topLeftY].multipoleExpansion.getNumberOfTerms(),newCenter);
				combinedMultipoleExpansion = combinedMultipoleExpansion.add(meshCells[topLeftX][topLeftY].multipoleExpansion.shift(newCenter));
				combinedMultipoleExpansion = combinedMultipoleExpansion.add(meshCells[topLeftX][topLeftY+1].multipoleExpansion.shift(newCenter));
				combinedMultipoleExpansion = combinedMultipoleExpansion.add(meshCells[topLeftX+1][topLeftY].multipoleExpansion.shift(newCenter));
				combinedMultipoleExpansion = combinedMultipoleExpansion.add(meshCells[topLeftX+1][topLeftY+1].multipoleExpansion.shift(newCenter));
				coarserMesh.meshCells[x][y].multipoleExpansion = combinedMultipoleExpansion;
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
				double boxWidth = meshSize.getWidth() / (double)(boxesOnSide);
				double boxHeight = meshSize.getHeight() / (double)(boxesOnSide);
				g.setColor(Color.white);
				//g.drawRect((int)(x*boxWidth), (int)(y*boxHeight), (int)boxWidth, (int)boxHeight);
			}
		}
	}
	public void print()
	{
		System.out.println("[Mesh] Multipole expansions: ");
		for(int x = 0; x < boxesOnSide; x++)
		{
			for(int y = 0; y < boxesOnSide; y++)
			{
				System.out.println("[Mesh] ("+x+","+y+"): "+meshCells[x][y].multipoleExpansion);
			}
		}
	}
	
}
