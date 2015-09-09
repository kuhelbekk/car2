package com.starbox.puzzlecar2;


import com.badlogic.gdx.Gdx;
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
	public boolean waitPlaying=false;
	
	public boolean isEndAnim() {
		return endAnim;
	}

	public AnimationDrawable(Animation anim) {
		this.anim = anim;
		setMinWidth(anim.getKeyFrame(0).getRegionWidth());
		setMinHeight(anim.getKeyFrame(0).getRegionHeight());
	}

	public void act(float delta) {
		
		if ((playing)&&(!waitPlaying)) {				
			stateTime += delta;
		}
	}

	public void reset() {
		stateTime = 0;
	}

	@Override
	public void draw(Batch batch, float x, float y, float width,float height) {
		animPos = anim.getKeyFrameIndex(stateTime);
		//Gdx.app.log("animPos","= "+animPos  +"  anim.isAnimationFinished(stateTime) "+anim.isAnimationFinished(stateTime));	
		if (playing && (anim.isAnimationFinished(stateTime) || waitPlaying )) {			
			if (loop||waitPlaying){
				if (waittime>0){
					if (waittime<TimeUtils.millis())  {						
						waitPlaying = false;
						waittime=0;
						stateTime=0;
					}
				}else{					
					waittime=TimeUtils.millis()+waiting;
				}				
			}else{				
				if (!waitPlaying){
					Gdx.app.log("act","3 stateTime- "+ stateTime);	
					endAnim = true;
					playing = false;
				}
					
			}
			
		}else{
			//Gdx.app.log("draw","draw beep");	
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
	
	public void playAfterWait() {		
		if (!(playing || endAnim)) {
			endAnim = false;
			playing = true;
			waitPlaying = true;
			stateTime = 0f;
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