package com.starbox.puzzlecar2;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.XmlReader;

public  class Background {

	protected Image shelf; //полка
	protected TextureAtlas textureAtlasBG;
	
	Stage stage;
	MainClass game;
	XmlReader.Element frontE ; 
	int dx, dy =0;
	
	
	
	public Background(Stage stage, MainClass game, String xmlname, int screenWidth,int screenHeight ) {	
		this.stage=stage;
		this.game =game;
		try {		    
			XmlReader xmlReader = new XmlReader();			
		    XmlReader.Element root = xmlReader.parse(Gdx.files.internal(xmlname)); 
		    XmlReader.Element atlasnameE = root.getChildByName("atlas");		    
		   	String atlasname =atlasnameE.get("name");
		   	textureAtlasBG = new TextureAtlas(atlasname);	
		   	
		   	
		   	frontE = root.getChildByName("front");
		   	
		   //	XmlReader.Element backE = root.getChildByName("back");
		   	
		   	
		   	Image bg = new Image(textureAtlasBG.findRegion("bg"));
			stage.addActor(bg);				
			dx =(int) (screenWidth-game.maxWidht)/ 2;		
			dy =(int) (screenHeight -game.maxHeight)/2;	
			bg.setPosition(dx,dy);
			
			
			shelf = new Image(game.commonAtlas.findRegion("polka"));
			stage.addActor(shelf);
			shelf.setPosition(225+dx,31+dy);
			
			/////////1133x580 - координаты зайца. от левого верхнего угла
		    			    
		} catch (IOException e) {			
			e.printStackTrace();
		}
		
		
		
		
		
		

	}

	public void drawFront(int zind) {
		 XmlReader.Element img;
		 Image image;
		 Gdx.app.log("anim","drawFront");
		for (int i=0; i<(frontE.getChildCount());i++ ){
	    	/// читаем xml
			img = frontE.getChild(i);
	    	String nameImg=img.get("id");
	    	int x =img.getInt("x");
	    	int y =img.getInt("y");	
	    	int w =img.getInt("w",0);
	    	int h =img.getInt("h",0);
	    	int z = img.getInt("z", -1);
	    	int speedAnim = img.getInt("speed", 5);
	    	int waiting = img.getInt("waiting", 1);
	    	z=z+zind;
	    	int animFarame = img.getInt("anim", 0);	    			    	
	    	String soundName = img.get("s","");	
	    	
	    	AnimationDrawable drawableActivate = null;
	    	if (img.getChildByName("activate")!=null){
	    		Gdx.app.log("anim","activate!=null");
	    		XmlReader.Element imgAct =img.getChildByName("activate");	    		
	    		TextureRegion[] frames = game.GetAnimFrames(textureAtlasBG.findRegion(imgAct.get("name")),w, h, imgAct.getInt("anim")); // создание массива кадров для анимации
				Animation animateAct = new Animation(imgAct.getInt("speed")*0.01f, frames); // задание скорости	 анимации				
				drawableActivate = new AnimationDrawable(animateAct); // создание отрисовщика  	 
				
	    	}
	    	
	    	/// вставка элемента   
	    	
	    	
	    	if (animFarame>0){	    		
	    		TextureRegion[] Frames = game.GetAnimFrames(textureAtlasBG.findRegion(nameImg),w, h, animFarame); // создание массива кадров для анимации
				Animation animate = new Animation(speedAnim*0.01f, Frames); // задание скорости	 анимации				
				AnimationDrawable drawable = new AnimationDrawable(animate); // создание отрисовщика

				bgAnimate aImg = new bgAnimate(drawable, drawableActivate, soundName ,waiting,stage, dx+x, 800-h-y +dy,z); // / компонент пузыря
				stage.addActor(aImg);
				
				
	    	}else{
	    		image = new Image( textureAtlasBG.findRegion(nameImg));				
				image.setPosition(dx+x, 800-image.getHeight()-y +dy);
				image.setZIndex(z);
				stage.addActor(image);		
	    	}
	    		
	    			
	    	
	    	
	    	
	    	    	
	    } 	
		
	}
	
	

}
