package com.starbox.puzzlecar2;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;



public class SameElements {
	ArrayList<SameElement> elements;
	public SameElements(){
		elements = new ArrayList<SameElement>();
	}
	public void put(int sameIndex, PuzzleElement pe){
		elements.add(new SameElement(sameIndex, pe));
	}
	
	public PuzzleElement hitSameElement(PuzzleElement puzzleElement,float x, float y, int accuracy) {	
		SameElement se = getElement(puzzleElement);
		if (se!=null) 			
			for(SameElement se2:elements)
				if((se2.sameIndex==se.sameIndex)&&(se2.pe!=se.pe)&&(!se2.pe.isAttach))  /// находим все элементы с таким же ключем										
						if (  (Math.abs(se2.pe.endPoint.x - x) < accuracy)&(Math.abs(se2.pe.endPoint.y - y) < accuracy)){							
							return se2.pe;
						}				
		return null;		
	}
	public SameElement getElement(PuzzleElement pe){
		for (SameElement se:elements){
			if (se.pe == pe) return se;
		}
		return null;
	}
	public void RemoveElement(PuzzleElement pe){
		//int ind=-1;
		for (SameElement se:elements){
			if (se.pe == pe) {
				elements.remove(se);
				break;
			}
		}
		
	}
}
