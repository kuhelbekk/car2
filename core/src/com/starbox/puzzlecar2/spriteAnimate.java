package com.starbox.puzzlecar2;

import java.util.ArrayList;
import java.util.Iterator;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

public class spriteAnimate extends Image {

	private AnimationDrawable drawable;
	private bgImgActivate imgActivate;
	private bgImgActivate imgEnd;
	boolean activate = false;
	boolean endImg = false;
	private Sound snd;
	private boolean HideParent;
	

	@Override
	public void act(float delta) {
		drawable.act(delta);		
		super.act(delta);
		if (endImg){
			if (drawable.isEndAnim())
				if (!imgEnd.isVisible()){
					setVisible(!HideParent);
					imgEnd.setVisible(true);
					imgEnd.resetAndPlay();	
					Gdx.app.log("imgEnd","endImg");	
				}
		}
		if (activate){
			if (drawable.isEndAnim()){
				if (isVisible()){
					setVisible(false);
					imgActivate.setVisible(true);
					imgActivate.resetAndPlay();	
				}else{
					if (imgActivate.endAnim()){						
						imgActivate.setVisible(false);
						setVisible(true);
						drawable.loop=true;						
						drawable.resetAndplayAfterWait();
						activate=false;
					}
				}
			}
		}
	}

	
	public spriteAnimate(AnimationDrawable drawableStart,AnimationDrawable drawableEnd, AnimationDrawable drawableActivate, String soundName, int waiting, Group group, int x, int y, int z, boolean loop, boolean playAfterWait) {
		this( drawableStart, drawableEnd,  drawableActivate,  soundName,  waiting,  group,  x,  y,  z,  loop,  playAfterWait, x, y, true) ;
	}
	
	
	
	
	
	
	
	public spriteAnimate(AnimationDrawable drawableStart,AnimationDrawable drawableEnd, AnimationDrawable drawableActivate, String soundName, int waiting, Group group,
							int x, int y, int z, boolean loop, boolean playAfterWait,int xEndAnim, int yEndAnim, boolean hideParent) {		
		super(drawableStart);
		this.HideParent = hideParent;
		snd=null;
		if (! soundName.equals("")){
			snd =  Gdx.audio.newSound(Gdx.files.internal("mfx/"+soundName+".mp3"));
		};
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
		group.addActor(this);
		if (drawableEnd!=null){
			imgEnd = new bgImgActivate(drawableEnd);
			imgEnd.setPosition(xEndAnim, yEndAnim);
			imgEnd.setZIndex(z+1);
			group.addActor(imgEnd);
			imgEnd.setVisible(false);
			endImg=true;
			drawable.loop=false;
			drawableEnd.loop=true;
			
		}
		
		if (drawableActivate!=null){
			imgActivate = new bgImgActivate(drawableActivate);
			imgActivate.setPosition(x, y);
			imgActivate.setZIndex(z+1);
			group.addActor(imgActivate);
			imgActivate.setVisible(false);			
			addListener(new ClickListener() {
				public boolean touchDown(InputEvent event, float x, float y,
						int pointer, int button) {
					Gdx.app.log("clickImg","clickImg");
					if (snd!=null){
						snd.play(1f);
					}
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
