package com.starbox.puzzlecar2;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.utils.TimeUtils;


public class AnimationDrawable extends BaseDrawable {
	public final Animation anim;
	private float stateTime = 0;
	public boolean playing = false;
	public int animPos = 0;
	private boolean endAnim = false;
	public float rotate=0;
	public boolean loop = false;
	public float scaleX=1;
	public float scaleY=1;
	private long waittime=0;
	public int waiting =0;
	
	
	public boolean isEndAnim() {
		return endAnim;
	}

	public AnimationDrawable(Animation anim) {
		this.anim = anim;
		setMinWidth(anim.getKeyFrame(0).getRegionWidth());
		setMinHeight(anim.getKeyFrame(0).getRegionHeight());
	}

	public void act(float delta) {
		if (playing) {			
			stateTime += delta;
		}
	}

	public void reset() {
		stateTime = 0;
	}

	@Override
	public void draw(Batch batch, float x, float y, float width,float height) {
		animPos = anim.getKeyFrameIndex(stateTime);
		if (playing & anim.isAnimationFinished(stateTime)) {	
			if (loop){
				if (waittime>0){
					if (waittime<TimeUtils.millis())  {
						waittime=0;
						stateTime=0;
					}
				}else{
					waittime=TimeUtils.millis()+waiting;
				}				
			}else{
				endAnim = true;
				playing = false;	
			}
			
		}
		//batch.draw(anim.getKeyFrame(stateTime), x, y, width, height);
		batch.draw(anim.getKeyFrame(stateTime), x, y,  width/2,height/2, width, height, scaleX, scaleY, rotate);
		
	}
	
	public void play() {		
		if (!(playing || endAnim)) {
			endAnim = false;
			playing = true;
			stateTime = 0;
		}
	}
	
	public void resetAndPlay() {			
			endAnim = false;
			playing = true;
			stateTime = 0;		
	}
	
	public void stop() {		
		endAnim = true;
		playing = false;
	}
}