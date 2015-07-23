package com.starbox.puzzlecar2;


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
		this.drawable = drawableActivate;					
	}

	public void resetAndPlay() {
		drawable.resetAndPlay();
	}

	public boolean endAnim() {		
		return drawable.isEndAnim();
	}
	
	

	
}
