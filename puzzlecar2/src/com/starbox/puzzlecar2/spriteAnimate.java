package com.starbox.puzzlecar2;

import java.util.ArrayList;
import java.util.Iterator;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

public class spriteAnimate extends Image {

	private AnimationDrawable drawable;
	private bgImgActivate imgActivate;
	boolean activate = false;
	
	private Sound snd;
	

	@Override
	public void act(float delta) {
		drawable.act(delta);		
		super.act(delta);			
		if (activate)			
			if (drawable.isEndAnim())
				if (isVisible()){
					Gdx.app.log("act","1");	
					setVisible(false);
					imgActivate.setVisible(true);
					imgActivate.resetAndPlay();	
				}else{
					if (imgActivate.endAnim()){
						Gdx.app.log("act","2");
						imgActivate.setVisible(false);
						setVisible(true);
						drawable.loop=true;						
						drawable.resetAndPlay();	
						activate=false;
					}
				}
	}

	
	
	
	public spriteAnimate(AnimationDrawable drawableStart, AnimationDrawable drawableActivate , String sn, int waiting, Stage stage, int x, int y, int z, boolean loop, boolean playAfterWait) {		
		super(drawableStart);
		//snd = s;		
		this.drawable = drawableStart;		
		drawable.loop=loop;
		drawable.waiting = waiting;
		
		if (playAfterWait){
			drawable.playAfterWait();
		}else{
			drawable.play();
		}
		//drawable.play();		
		setPosition(x, y);
		setZIndex(z);
		if (drawableActivate!=null){
			imgActivate = new bgImgActivate(drawableActivate);
			imgActivate.setPosition(x, y);
			imgActivate.setZIndex(z+1);
			stage.addActor(imgActivate);
			imgActivate.setVisible(false);			
			addListener(new ClickListener() {
				public boolean touchDown(InputEvent event, float x, float y,
						int pointer, int button) {
					Gdx.app.log("clickImg","clickImg");	
					if (imgActivate!=null){
						activate = true;
						drawable.loop=false;
					}					
					return true;
				}
			});
		}
		
		
		
		
	}


	
	
	
}
