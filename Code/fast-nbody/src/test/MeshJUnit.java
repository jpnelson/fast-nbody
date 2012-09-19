package test;

import static org.junit.Assert.*;

import java.awt.Dimension;

import math.Complex;

import org.junit.Test;

import fma.Cell;
import fma.Mesh;
import gui.SpaceSize;

public class MeshJUnit {
	Mesh testMesh = new Mesh(8,3,new SpaceSize(512,512));
	
	@Test
	public void testInteractionList()
	{
		assertEquals(testMesh.getInteractionList(4,2).size(),27);
		assertEquals(testMesh.getInteractionList(4,0).size(),18);

	}
	
	@Test
	public void testCellCenter()
	{
		assertTrue(Math.abs(
				testMesh.getCellCenter(1, 0).sub(
						new Complex(512/8 + 512/8/2,512/8/2)).re()) <= 0.001);
	}
}
