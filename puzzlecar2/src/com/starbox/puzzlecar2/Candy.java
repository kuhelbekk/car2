package com.starbox.puzzlecar2;

import java.util.ArrayList;


import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Candy extends Image {

	private final AnimationDrawable drawable;
	private Sound snd;
	private boolean isShowConfetti;
	ArrayList<Image> imagesConfetti;
	
	public Body body;
	;
	
	public boolean isFinished() {
		if (drawable.isEndAnim() || getY() > 1080)
			return true;
		return false;
	}

	@Override
	public void act(float delta) {
		drawable.act(delta);

		if ((drawable.animPos>4)&(!isShowConfetti)){
			showConfetti();
		}
		super.act(delta);

	}

	public Candy(AnimationDrawable drawable, Sound s, TextureAtlas commonAtlas, Stage stage) {
		
		super(drawable);
	/*	setColor((float) (1 - Math.random() * 0.4),
				(float) (1 - Math.random() * 0.4),
				(float) (1 - Math.random() * 0.4), 1f);*/
		snd = s;
		this.drawable = drawable;	
		
		
	
		imagesConfetti= new ArrayList<Image>();		
		for (int i=0; i<40; i++){			
			Image im = new Image(commonAtlas.findRegion("p"+(int)(Math.random()*8+1) ));
			im.setVisible(false);
			float rnd = (float)(Math.random()/3+0.7);
			im.setScale(rnd);
			im.setOrigin(im.getWidth()/2, im.getHeight()/2);
			imagesConfetti.add(im);
			stage.addActor(im);
		}
		
		
	}

	public void clickCandy() {
		drawable.play();
		isShowConfetti=false;
		

	}
	private void showConfetti(){
		isShowConfetti=true;
		for ( Image im:imagesConfetti){			
			im.setPosition(getX()+ getWidth()/2, getY()+ getHeight()/2);
			im.setColor(1,1,1,1);			
			im.addAction(Actions.parallel(
					Actions.alpha(0, 0.6f,Interpolation.pow2In),
					Actions.moveBy((float)(Math.random()*600)-300, (float)(Math.random()*600)-300,0.6f,Interpolation.pow2Out),
					Actions.rotateBy((float)(Math.random()*600)-300, 0.7f,Interpolation.pow2Out)
					));
			im.setVisible(true);
		}
		if (snd != null)
			snd.play(0.5f);
		
	}

	public void render(float PIXELS_TO_METERS) {
		  
		 setPosition((body.getPosition().x * PIXELS_TO_METERS) - getWidth()/2 ,
                 (body.getPosition().y * PIXELS_TO_METERS) - getHeight()/2);	 
		 drawable.rotate = (float)Math.toDegrees(body.getAngle());		
		
	}	
	
}
