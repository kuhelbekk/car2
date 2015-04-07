package com.starbox.puzzlecar2;

import java.util.ArrayList;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class bgImgActivate extends Image {

	private AnimationDrawable drawable;

	@Override
	public void act(float delta) {
		drawable.act(delta);		
		super.act(delta);

	}

	public bgImgActivate(AnimationDrawable drawableActivate ) {		
		super(drawableActivate);
		//snd = s;		
		this.drawable = drawableActivate;					
	}

	public void resetAndPlay() {
		drawable.resetAndPlay();
	}

	public boolean endAnim() {
		
		return drawable.isEndAnim();
	}
	
	

	
}
