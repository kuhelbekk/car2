package com.starbox.puzzlecar2;

import java.util.ArrayList;


import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class Baloon extends Image {
	private Sound snd;
	private boolean isShowConfetti;
	ArrayList<Image> imagesConfetti;
	boolean Finished;
	public Body body;
	;
	
	public boolean isFinished() {

		return Finished;
	}

	@Override
	public void act(float delta) {

		if(!Finished){
			if(getActions().size==0){
				showConfetti();
				Finished=true;
			}else{
				if (getY()>850){
					Finished=true;
				}
			}

		}

		super.act(delta);

	}

	public Baloon( Sound s, TextureAtlas commonAtlas, Group group ,int screenWidth) {
		super(commonAtlas.findRegion("ball"+(int)(Math.random() *3)));
		group.addActor(this);
		snd = s;
		Finished=false;
		float x = (float)Math.random() * (screenWidth-150) +50;
		setPosition(x, (float) ((Math.random() * (-200)) - 300)); // начальная позиция
		float degree = 10f+ (float)(Math.random()*5);
		setOrigin(getWidth() / 2, getHeight() / 2);
		rotateBy(0.5f * degree);


		addAction(
				Actions.forever(Actions.sequence(
								Actions.parallel(
										Actions.rotateBy(-degree, 1f, Interpolation.sine),
										Actions.moveBy((-5f * degree), 0, 1, Interpolation.fade),//pow2
										Actions.moveBy(0, 120, 1)
								),
								Actions.parallel(
										Actions.rotateBy(degree, 1f, Interpolation.sine),
										Actions.moveBy(5f * degree, 0, 1, Interpolation.fade),
										Actions.moveBy(0, 120, 1)
								)
						)
				)
		);



		imagesConfetti= new ArrayList<Image>();		
		for (int i=0; i<40; i++){			
			Image im = new Image(commonAtlas.findRegion("p"+(int)(Math.random()*8+1) ));
			im.setVisible(false);
			float rnd = (float)(Math.random()/3+0.7);
			im.setScale(rnd);
			im.setOrigin(im.getWidth()/2, im.getHeight()/2);
			imagesConfetti.add(im);
			group.addActor(im);
		}

		addListener(new ClickListener() { // попадание по пузырю
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				((Baloon) (event.getListenerActor())).clickBaloon();
				return true;
			}});
		
		
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


	public void clickBaloon() {

		clearActions();
		addAction(Actions.sequence(
						Actions.scaleTo(0.9f, 0.9f, 0.1f),
						Actions.parallel(
								Actions.scaleTo(1.3f, 1.3f, 0.1f, Interpolation.pow2),
								Actions.alpha(0, 0.1f, Interpolation.pow2)
						)
				)
		);


	}
}
