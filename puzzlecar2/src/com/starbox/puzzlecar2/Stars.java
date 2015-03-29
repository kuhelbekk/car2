package com.starbox.puzzlecar2;

import java.util.ArrayList;


import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Interpolation.ExpOut;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Stars{
	ArrayList<Image> starImages ;

	public Stars(Stage stage,TextureAtlas atlas, boolean finishStar) {		
		starImages= new ArrayList<Image>();		
		for (int i=0; i<50; i++){	
			Image im ;
			if (finishStar) {
				im = new Image(atlas.findRegion("whitestar"));
			} else {
				im = new Image(atlas.findRegion("star"+(int)(Math.random()*3+1) ));			
				float rnd = (float)(Math.random()/3+0.6);
				im.setScale(rnd);
			}
			im.setVisible(false);			
			im.setOrigin(im.getWidth()/2, im.getHeight()/2);
			starImages.add(im);
			stage.addActor(im);
		}
	}
	
	public void play(float x, float y){	
		for ( Image im:starImages){			
			im.setPosition(x-im.getWidth(), y-im.getHeight());
			im.setColor(1,1,1,1);
			float f =(float) (Math.random()/1.5+0.3);
			im.addAction(Actions.parallel(
					Actions.alpha(0, 1f,Interpolation.pow2Out),
					Actions.moveBy((float)(Math.random()*500)-250, (float)(Math.random()*400)-200, f,Interpolation.pow2Out),
					Actions.rotateBy((float)(Math.random()*600)-300,f,Interpolation.pow2Out)
					));
			im.setVisible(true);
		}
	}
	


	
	public void playFinishStar(float x, float y){	
		for ( Image im:starImages){			
			im.setPosition(x-im.getWidth(), y-im.getHeight());
			im.setColor(1,1,1,0);
			float f =(float) (Math.random()*2+6);
			im.addAction(Actions.sequence(
					Actions.alpha(0,3),
					Actions.alpha(1,0f),
					Actions.parallel(
						Actions.alpha(0, f,Interpolation.exp10In),
						Actions.moveBy((float)(Math.random()*600)-300, (float)(Math.random()*600)-300, f,new ExpOut(2, 25)),//Interpolation.bounceOut),
						Actions.rotateBy((float)(Math.random()*1200)-600,f,Interpolation.pow2Out)
						
					)));
			im.setVisible(true);
		}
	}
}