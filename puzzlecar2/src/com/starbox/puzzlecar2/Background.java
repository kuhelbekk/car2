package com.starbox.puzzlecar2;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.XmlReader;

public  class Background {

	protected Image shelf; //полка
	protected TextureAtlas textureAtlasBG;
	
	Stage stage;
	MainClass game;
	XmlReader.Element frontE,backE ; 
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
		   	backE = root.getChildByName("back");
		   	
		   	Image bg = new Image(textureAtlasBG.findRegion("bg"));
			stage.addActor(bg);				
			dx =(int) (screenWidth-game.maxWidht)/ 2;		
			dy =(int) (screenHeight -game.maxHeight)/2;	
			bg.setPosition(dx,dy);	
			
			drawBack( bg.getZIndex());			
			shelf = new Image(game.commonAtlas.findRegion("polka"));
			stage.addActor(shelf);
			shelf.setPosition(225+dx,31+dy);
			
		} catch (IOException e) {			
			e.printStackTrace();
		}	
		
	}

	
	public void drawBack(int zind){	
		 Gdx.app.log("anim","drawBack");
		 drawElements(backE, zind);		
	}
	
	
	public void drawFront(int zind) {	
		 Gdx.app.log("anim","drawFront");
		 drawElements(frontE, zind);	
	}
	
	private void drawElements(XmlReader.Element Elem, int zind){
		
		 XmlReader.Element img;
		 Image image;
		 for (int i=0; i<(Elem.getChildCount());i++ ){
	    	/// читаем xml
			img = Elem.getChild(i);
	    	String nameImg=img.get("id");
	    	int x =img.getInt("x");
	    	int y =img.getInt("y");	
	    	int w =img.getInt("w",0);
	    	int h =img.getInt("h",0);
	    	int z = img.getInt("z", 0)+zind;
	    	int speedAnim = img.getInt("speed", 5);
	    	int waiting = img.getInt("waiting", 1);	    	    	
	    	int animFarame = img.getInt("anim", 0);	    			    	
	    	String soundName = img.get("s","");	
	    	int typeAnim=img.getInt("typeanim",0);	  	///0-NoAnim; 1-Sprite ; 2-rotate
	    	
	    	
	    	Gdx.app.log("anim","drawElement-" + nameImg);
	    	switch (typeAnim) {
	    	case 0:///NoAnim
	    		image = new Image( textureAtlasBG.findRegion(nameImg));				
				image.setPosition(dx+x, 800-image.getHeight()-y +dy);
				image.setZIndex(z);
				stage.addActor(image);	
	    		break;
			case 1: //Sprite
				AnimationDrawable drawableActivate = null;
		    	if (img.getChildByName("activate")!=null){
		    		Gdx.app.log("anim","activate!=null");
		    		XmlReader.Element imgAct =img.getChildByName("activate");	    		
		    		TextureRegion[] frames = game.GetAnimFrames(textureAtlasBG.findRegion(imgAct.get("name")),w, h, imgAct.getInt("anim")); // создание массива кадров для анимации
					Animation animateAct = new Animation(imgAct.getInt("speed")*0.01f, frames); // задание скорости	 анимации				
					drawableActivate = new AnimationDrawable(animateAct); // создание отрисовщика 				
		    	}	    	
		    	/// вставка элемента      	
		    	TextureRegion[] Frames = game.GetAnimFrames(textureAtlasBG.findRegion(nameImg),w, h, animFarame); // создание массива кадров для анимации
				Animation animate = new Animation(speedAnim*0.01f, Frames); // задание скорости	 анимации				
				AnimationDrawable drawable = new AnimationDrawable(animate); // создание отрисовщика
				bgAnimate aImg = new bgAnimate(drawable, drawableActivate, soundName ,waiting,stage, dx+x, 800-h-y +dy,z); // / компонент пузыря
				stage.addActor(aImg);
	    			
		    						
				break;
			case 2: //rotate
				
				image = new Image( textureAtlasBG.findRegion(nameImg));				
				image.setPosition(dx+x, 800-image.getHeight()-y +dy);
				image.setZIndex(z);
				image.setOrigin(image.getWidth()/2, image.getHeight()/2);
				image.addAction(Actions.forever(Actions.rotateBy(90, speedAnim)));
				stage.addActor(image);
				break;
			
			} 
	    	
	    	   	
	    	    	
	    } 		
	}

}
