package com.starbox.puzzlecar2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;



public class ParticleEffectActor extends Actor {
	   ParticleEffect effect;
	   float px = 0;
	   float py = 0;
	   
	   
	   public ParticleEffectActor(ParticleEffect effect) {
		  super();
	      this.effect = effect;	      
	   }
	   
	   
	   @Override
	   public void draw (Batch batch, float parentAlpha) {		 
		   effect.draw(batch);
		}

	  
	   
	/*  
	   public void draw(SpriteBatch batch, float parentAlpha) {
		   
		   effect.draw(batch); //define behavior when stage calls Actor.draw()
	   }*/
	   
	   @Override
	   public void act(float delta) {
		 
	      super.act(delta);
	      effect.setPosition(px, py); //set to whatever x/y you prefer
	      effect.update(delta); //update it      	      
	      //effect.start(); //need to start the particle spawning
	   }

	   public ParticleEffect getEffect() {
	      return effect;
	   }
	   
	   public void setPosition(float x, float y){
		   px = x;
		   py = y;
	   }
	   
	   public void allowCompletion() {
		   effect.allowCompletion();
		   
	   }
}


	  
	  

	   
	


	 

	
