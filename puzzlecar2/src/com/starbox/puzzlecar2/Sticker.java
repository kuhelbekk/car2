package com.starbox.puzzlecar2;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

public class Sticker {

	public Image imageSticker, imageSubstrateSticker, imageBlink;
	public Rectangle tagert;
	public Vector2 endPoint;
	public int Zindex;
	public int index;
	public boolean isAttach = false;
	public TextureRegion substrateRegion, blinkRegion, stickerRegion;	
	public StickerScene parent;
	public int currentAction;
	public boolean elementMounted = false;
	
	protected Sound sSuccess;
	private String sSuccessName;

	public Sticker(StickerScene parent, TextureAtlas textureAtlas , String nameRegion, int i, int endX, int endY,int endZ) {
		sSuccessName = "success.mp3";
		StickerElementA(parent, textureAtlas, nameRegion, i, endX, endY, endZ);
	}


	public Sticker(StickerScene parent, TextureAtlas textureAtlas , String nameRegion, int i, int endX, int endY,int endZ , String sName) {

		if (parent.game.settings.isVoice()) {
			setSound(sName + parent.game.getLangStr()+".mp3");			
		} else {
			sSuccessName = "success.mp3";
		}
		StickerElementA(parent, textureAtlas, nameRegion,  i, endX, endY, endZ);
	}
	


	public void StickerElementA(final StickerScene parent, TextureAtlas textureAtlas , String nameRegion, int i, int endX,int endY,int endZ) {
		this.parent = parent;
		index = i;
		Gdx.app.log("nameRegion","nameRegion=" +nameRegion );
		stickerRegion = textureAtlas.findRegion(nameRegion);
		substrateRegion = textureAtlas.findRegion(nameRegion+"2");
		blinkRegion = textureAtlas.findRegion(nameRegion+"3");
		imageSticker = new Image(stickerRegion);
		imageSubstrateSticker = new Image(substrateRegion);
		endPoint = new Vector2(endX , endY );
		Zindex = endZ;		
		imageSubstrateSticker.setPosition(endX, endY);
		imageSubstrateSticker.setZIndex(Zindex-1);
		parent.stage.addActor(imageSubstrateSticker);
		parent.stage.addActor(imageSticker);
		setPosToStartPoint(2);
		
		imageSticker.addListener(new ClickListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				if ((button != 0) || (pointer != 0))
					return false;
				// Gdx.app.log("touchDown","index=" +index+"  y=" +y );
				if (isAttach)
					return false;
				imageSticker.setZIndex(Zindex+160);

				imageSticker.setScale(imageSticker.getHeight()/ stickerRegion.getRegionHeight());
				imageSticker.setPosition(imageSticker.getX() - (stickerRegion.getRegionWidth() - imageSticker.getWidth()) / 2,
										 imageSticker.getY()  -  (stickerRegion.getRegionHeight() - imageSticker.getHeight()) / 2);
				imageSticker.setSize(stickerRegion.getRegionWidth(),	stickerRegion.getRegionHeight());
				imageSticker.setOrigin(imageSticker.getWidth() / 2, imageSticker.getHeight() / 2);
				currentAction = 0;
				Array<Action> aa = imageSticker.getActions();
				Iterator<Action> ia = aa.iterator();
				while (ia.hasNext()) {
					ia.next();
					ia.remove();
				}
				imageSticker.addAction(Actions.parallel(
						Actions.scaleTo(1, 1, 0.15f),
						Actions.moveTo(event.getStageX()
								- (imageSticker.getWidth() / 2), event.getStageY()
								- (imageSticker.getHeight() / 2), 0.2f),
						Actions.rotateTo(0, 0.2f)));
				return true;
			}

			public void touchDragged(InputEvent event, float x, float y,
					int pointer) {
				if (pointer != 0)	return;
				if (isAttach)	return;
				imageSticker.addAction(Actions.moveTo(
						event.getStageX() - (imageSticker.getWidth() / 2),
						event.getStageY() - (imageSticker.getHeight() / 2), 0.05f));
				// image.setPosition(event.getStageX()-(image.getWidth()/2),
				// event.getStageY()-(image.getHeight()/2)) ;
				if (hit() != null) {
					imageSticker.setColor(1.1f, 1.1f, 1.1f, 1);
					setAction(3);
				} else {
					imageSticker.setColor(0.9f, 0.9f, 0.9f, 1f);
					setAction(0);
				}
			}

			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				if ((button != 0) || (pointer != 0))	return;				
				float px = imageSticker.getX();
				float py = imageSticker.getY();
				Sticker pe = hit();
				if (pe != null) {					
					if (pe.imageSticker != imageSticker) {
						int i = index;
						index = pe.index;
						pe.index = i;
						imageSticker.setColor(1, 1, 1, 1);
						setPosToStartPoint(2);
					}	
					
					pe.fixing(true, px, py);
					
				} else {
					setPosToStartPoint(1);
				}
			}
		});
		
		
		
		
		if (parent.game.settings.isSound()) {
			sSuccess = Gdx.audio.newSound(Gdx.files.internal("mfx/"
					+ sSuccessName));
		}
	}

	public void setSound(String name) {
		Gdx.app.log("Sound = ", name);
		sSuccessName = name;
	}

	private Sticker hit() {		
		if(imageSticker != null) {
			if ((Math.abs(endPoint.x - imageSticker.getX()) < parent.game.accuracy)
					& (Math.abs(endPoint.y - imageSticker.getY()) < parent.game.accuracy)) {
				return this;
			} 
		}
		return null;
	}

	public void fixing(boolean PlaySound, float  px, float  py) {
		
		
		
		imageSubstrateSticker.remove();
		imageSubstrateSticker=null;
		setAction(0);
		imageSticker.setZIndex(Zindex+1);		
		imageSticker.setColor(1, 1, 1, 1);			
		imageSticker.setPosition(px, py);		
		imageSticker.addAction(Actions.sequence(
							Actions.parallel(				
								Actions.moveTo(endPoint.x - imageSticker.getWidth()/8, endPoint.y - imageSticker.getHeight()/8, 0.1f),
							    Actions.scaleTo(1.25f, 1.25f, 0.1f)),
							Actions.parallel(		
								Actions.moveTo(endPoint.x, endPoint.y, 0.05f),
		 					    Actions.scaleTo(1, 1, 0.05f))
		 					//Actions.color(new Color(0,0,0,1), 0.09f),
		 					//Actions.color(new Color(1,1,1,1), 0.15f)
		 					    
				));
		
		

		imageBlink = new Image(blinkRegion);
		imageBlink.setColor(1, 1, 1,0);
		imageBlink.setPosition(endPoint.x, endPoint.y);
		imageBlink.addAction(Actions.sequence(
				Actions.alpha(0,0.15f),
				Actions.alpha(1,0.05f),
				Actions.alpha(0,0.15f)
				));
		
		imageBlink.setZIndex(Zindex+2);		
		
		parent.stage.addActor(imageBlink);
		
		isAttach = true;
		
		parent.btnBack.setZIndex(Zindex+25);
		if ((PlaySound)&(sSuccess != null)) {
			sSuccess.play(1f);
		}
	  //stars.play();
	  //parent.sameElements.RemoveElement(this);
		parent.elementMounted(this);
	///	Gdx.app.log("Game", "fixing");
	///	Gdx.app.log("Game", "imageSticker.zind"+imageSticker.getZIndex() +"   Zindex="+Zindex);
	//	Gdx.app.log("Game", "imageBlink.zind"+imageBlink.getZIndex());
		
	}

	public void setPosToStartPoint(int type) {
		imageSticker.setZIndex(Zindex+1);
		if (imageSticker.getWidth() > imageSticker.getHeight()) {
			if (imageSticker.getWidth() > 200) {
				imageSticker.setSize(200, imageSticker.getHeight() / (imageSticker.getWidth() / 200));
			}
		} else {
			if (imageSticker.getHeight() > 155)
				imageSticker.setSize(imageSticker.getWidth() / (imageSticker.getHeight() / 160), 160);
		}
		setAction(1);
		//imageSticker.setColor(0.9f, 0.9f, 0.9f, 1f);
		imageSticker.setColor(0.9f, 0.9f, 0.9f, 1f);
		imageSticker.setOrigin(imageSticker.getWidth() / 2, imageSticker.getHeight() / 2);
		
		int dx =(int) (parent.screenWidth - parent.game.maxWidht)/ 2;		
		int dy =(int) (parent.screenHeight - parent.game.maxHeight)/2;	
		
		switch (type) {
		case 0: // / сдвиг после попадания
			imageSticker.addAction(Actions.moveTo(120-imageSticker.getWidth()/2,parent.screenHeight - (index * 170) -imageSticker.getHeight()/2-dy+40 , 0.2f));
			break;
		case 1:// // возврат на место детали
			float scale = stickerRegion.getRegionHeight() / imageSticker.getHeight();
			imageSticker.setPosition(imageSticker.getX() + (stickerRegion.getRegionWidth() - imageSticker.getWidth())/ 2,
						      imageSticker.getY() + (stickerRegion.getRegionHeight() - imageSticker.getHeight()) / 2);
			imageSticker.setScale(scale);
			imageSticker.addAction(Actions.parallel(Actions.moveTo(120-imageSticker.getWidth()/2, parent.screenHeight - (index* 170)-imageSticker.getHeight()/2-dy+40 , 0.2f),	Actions.scaleTo(1, 1, 0.2f)));

			break;
		case 2:// / поставить на место без анимации
			//Gdx.app.log("StickerElementA","index=" +index+"  x=" +(130-imageSticker.getWidth()/2)+"  y=" +(parent.screenHeight -imageSticker.getHeight()/2- (index* 170)-dy+40) );
			imageSticker.setPosition(120-imageSticker.getWidth()/2,	parent.screenHeight -imageSticker.getHeight()/2- (index* 170)-dy+40 );
			break;
		}
		
		imageSticker.setVisible((index < 5));
		
	}


	public void setAction(int i) {

		if (currentAction == i)
			return; // в начальное положение
		currentAction = i;
		Array<Action> aa = imageSticker.getActions();
		Iterator<Action> ia = aa.iterator();
		while (ia.hasNext()) {
			ia.next();
			ia.remove();
		}
		imageSticker.setRotation(0);
		switch (i) {
		case 0:
			break;
		case 1: // ждем на полке
			imageSticker.addAction(Actions.sequence(Actions.rotateBy(-10, 0.5f),
					Actions.forever(Actions.sequence(
							Actions.rotateBy(20, 1, Interpolation.sine),
							Actions.rotateBy(-20, 1, Interpolation.sine)))));
			break;
		case 3:  ////прицелились
			/*imageSticker.addAction(Actions.forever(Actions.sequence(
					Actions.scaleTo(1.01f, 1.01f, 0.15f, Interpolation.sine),
					Actions.scaleTo(0.98f, 0.99f, 0.06f))));*/
			float derree = 0.7f+(150f/imageSticker.getWidth());
			imageSticker.addAction(Actions.forever	(Actions.sequence(
										Actions.rotateBy(derree, 0.04f),
										Actions.rotateBy(-2*derree, 0.04f),
										Actions.rotateBy(2*derree, 0.04f),
										Actions.rotateBy(-2*derree, 0.04f),
										Actions.rotateBy(derree, 0.07f),
										Actions.rotateBy(0, 0.7f)
										)));
			break;

		}
	}

	public void dispose() {
		if (sSuccess != null) {
			sSuccess.dispose();
		}
	}

	public void refreshZindex(int count){
		if (imageSticker!=null) imageSticker.setZIndex(Zindex);
		
		Gdx.app.log("Game", "imageSticker.zind = "+imageSticker.getZIndex() +"   Zindex = "+Zindex);
		//if (imageBlink!=null) Gdx.app.log("Game", "imageBlink.zind"+imageBlink.getZIndex());
		//shadow.setZIndex(Zindex-count-1);
		
	}
}
