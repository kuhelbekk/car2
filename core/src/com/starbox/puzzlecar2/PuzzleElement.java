package com.starbox.puzzlecar2;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
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

public class PuzzleElement {

	public Image image;
	//public Image shadow;
	public Rectangle tagert;
	public Vector2 endPoint;
	public int Zindex;
	public int index;
	public boolean isAttach = false;
	public TextureRegion startRegion;
	public TextureRegion endRegion;	
	public PuzzleScene parent;
	public int currentAction;
	public boolean elementMounted = false;
	
	protected Sound sSuccess;
	private String sSuccessName;

	public PuzzleElement(PuzzleScene parent, TextureRegion startRegion, int i,
			int endX, int endY,int endZ) {
		sSuccessName = "success.mp3";
		PuzzleElementA(parent, startRegion, startRegion, i, endX, endY, endZ);
	}

	public PuzzleElement(PuzzleScene parent, TextureRegion startRegion,
			TextureRegion endRegion, int i, int endX, int endY,int endZ) {
		sSuccessName = "success.mp3";
		PuzzleElementA(parent, startRegion, endRegion, i, endX, endY, endZ);
	}

	public PuzzleElement(PuzzleScene parent, TextureRegion startRegion, int i,
			int endX, int endY,int endZ , String sName) {

		if (parent.game.settings.isVoice()) {
			setSound(sName + parent.game.getLangStr()+".mp3");			
		} else {
			sSuccessName = "success.mp3";
		}
		PuzzleElementA(parent, startRegion, startRegion, i, endX, endY, endZ);
	}

	public PuzzleElement(PuzzleScene parent, TextureRegion startRegion,
			TextureRegion endRegion, int i, int endX, int endY,int endZ, String sName) {

		if (parent.game.settings.isVoice()) {			
			setSound(sName + parent.game.getLangStr()+".mp3");	
		} else {
			sSuccessName = "success.mp3";
		}
		PuzzleElementA(parent, startRegion, endRegion, i, endX, endY, endZ);
	}

	public void PuzzleElementA(final PuzzleScene parent,
			TextureRegion strtRegion, TextureRegion eRegion, int i, int endX,int endY,int endZ) {
		this.parent = parent;
		index = i;
		startRegion = strtRegion;
		endRegion = eRegion;
		image = new Image(startRegion);	
		endPoint = new Vector2(endX , endY - startRegion.getRegionHeight());
		Zindex = endZ;
		
		parent.puzzleGroup.addActor(image);
		setPosToStartPoint(2);
		
		image.addListener(new ClickListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				if ((button != 0) || (pointer != 0))
					return false;
				// Gdx.app.log("touchDown","index=" +index+"  y=" +y );
				if (isAttach)
					return false;
				
				if (parent.puzzleGroup.removeActor(image))	parent.onTopGroup.addActor(image);
				image.setZIndex(2);
				image.setScale(image.getHeight()/ startRegion.getRegionHeight());
				image.setPosition(image.getX()- (startRegion.getRegionWidth() - image.getWidth()) / 2,
						          image.getY()- (startRegion.getRegionHeight() - image.getHeight()) / 2);
				image.setSize(startRegion.getRegionWidth(),
						startRegion.getRegionHeight());
				image.setOrigin(image.getWidth() / 2, image.getHeight() / 2);
				currentAction = 0;
				Array<Action> aa = image.getActions();
				Iterator<Action> ia = aa.iterator();
				while (ia.hasNext()) {
					ia.next();
					ia.remove();
				}
				image.addAction(Actions.parallel(
						Actions.scaleTo(1, 1, 0.15f),
						Actions.moveTo(event.getStageX()
								- (image.getWidth() / 2), event.getStageY()
								- (image.getHeight() / 2), 0.2f),
						Actions.rotateTo(0, 0.2f)));
				return true;
			}

			public void touchDragged(InputEvent event, float x, float y,
					int pointer) {
				if (pointer != 0)	return;
				if (isAttach)	return;
				image.addAction(Actions.moveTo(
						event.getStageX() - (image.getWidth() / 2),
						event.getStageY() - (image.getHeight() / 2), 0.05f));
				// image.setPosition(event.getStageX()-(image.getWidth()/2),
				// event.getStageY()-(image.getHeight()/2)) ;
				if (hit() != null) {
					image.setColor(3.1f, 1.1f, 1.1f, 1);
					setAction(3);
				} else {
					image.setColor(0.9f, 0.9f, 0.9f, 1f);
					setAction(0);
				}
			}

			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				if ((button != 0) || (pointer != 0))	return;				
				float px = image.getX();
				float py = image.getY();
				PuzzleElement pe = hit();
				if (pe != null) {					
					if (pe.image != image) {
						int i = index;
						index = pe.index;
						pe.index = i;
						image.setColor(1, 1, 1, 1);
						setPosToStartPoint(2);
					}	
					
					pe.fixing(true, px, py);
					parent.stars.play(event.getStageX(), event.getStageY());
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
	//	Gdx.app.log("Sound = ", name);
		sSuccessName = name;
	}

	private PuzzleElement hit() {		
		
		if(image != null) {
			if ((Math.abs(endPoint.x - image.getX()) < parent.game.accuracy)
					& (Math.abs(endPoint.y - image.getY()) < parent.game.accuracy)) {
				return this;
			} else {			
				return parent.sameElements.hitSameElement(this,image.getX(),image.getY(),parent.game.accuracy);			
			}
		}		
		return null;
	}

	public void fixing(boolean PlaySound, float  px, float  py) {		
		image.remove();
		image = null;
		image = new Image(endRegion);		
		image.setZIndex(Zindex);		
		image.setColor(1, 1, 1, 1);			
		image.setPosition(px, py);		
		image.addAction(Actions.sequence(
							Actions.parallel(				
								Actions.moveTo(endPoint.x - image.getWidth()/8, endPoint.y - image.getHeight()/8, 0.1f),
							    Actions.scaleTo(1.25f, 1.25f, 0.1f)),
							Actions.parallel(		
								Actions.moveTo(endPoint.x, endPoint.y, 0.05f),
		 					    Actions.scaleTo(1, 1, 0.05f))));
		
		parent.puzzleGroup.addActor(image);

		isAttach = true;
		//parent.btnBack.setZIndex(10);
		if ((PlaySound)&(sSuccess != null)) {
			sSuccess.play(1f);
		}
	  //stars.play();
	  //parent.sameElements.RemoveElement(this);
		parent.elementMounted(this);

		
	}

	public void setPosToStartPoint(int type) {
		if (parent.onTopGroup.removeActor(image))	parent.puzzleGroup.addActor(image);
		if (image.getWidth() > image.getHeight()) {
			if (image.getWidth() > 200) {
				image.setSize(200, image.getHeight() / (image.getWidth() / 200));
			}
		} else {
			if (image.getHeight() > 155)
				image.setSize(image.getWidth() / (image.getHeight() / 160), 160);
		}
		setAction(1);
		//image.setColor(0.9f, 0.9f, 0.9f, 1f);
		image.setColor(0.9f, 0.9f, 0.9f, 1f);
		image.setOrigin(image.getWidth() / 2, image.getHeight() / 2);
		
		int dx =(int) (parent.screenWidth - parent.game.maxWidht)/ 2;		
		int dy =(int) (parent.screenHeight - parent.game.maxHeight)/2;	
		
		switch (type) {
		case 0: // / сдвиг после попадания
			image.addAction(Actions.moveTo(330+dx-image.getWidth()/2,  (index * 170)+dy-40 -image.getHeight()/2 , 0.2f));
			break;
		case 1:// // возврат на место детали
			float scale = startRegion.getRegionHeight() / image.getHeight();
			image.setPosition(image.getX() + (startRegion.getRegionWidth() - image.getWidth())/ 2,
						      image.getY() + (startRegion.getRegionHeight() - image.getHeight()) / 2);
			image.setScale(scale);
			image.addAction(Actions.parallel(Actions.moveTo(330+dx-image.getWidth()/2,(index* 170)+dy-40 -image.getHeight()/2 , 0.2f),	Actions.scaleTo(1, 1, 0.2f)));

			break;
		case 2:// / поставить на место без анимации
			image.setPosition(330+dx-image.getWidth()/2,	(index * 170)+dy -40 -image.getHeight()/2);
			break;
		}
		
		if (index<5){
			if (!image.isVisible()) {
				 image.setColor(1, 1, 1,0);			
				 image.setVisible(true);
				 image.addAction(Actions.alpha(1, 0.5f));
			}
		}else{
			image.setVisible(false);
		}
		
		
	}


	public void setAction(int i) {

		if (currentAction == i)
			return; // в начальное положение
		currentAction = i;
		Array<Action> aa = image.getActions();
		Iterator<Action> ia = aa.iterator();
		while (ia.hasNext()) {
			ia.next();
			ia.remove();
		}
		image.setRotation(0);
		switch (i) {
		case 0:
			break;
		case 1: // ждем на полке
			image.addAction(Actions.sequence(Actions.rotateBy(-10, 0.5f),
					Actions.forever(Actions.sequence(
							Actions.rotateBy(20, 1, Interpolation.sine),
							Actions.rotateBy(-20, 1, Interpolation.sine)))));
			break;
		case 3:  ////прицелились
			/*image.addAction(Actions.forever(Actions.sequence(
					Actions.scaleTo(1.01f, 1.01f, 0.15f, Interpolation.sine),
					Actions.scaleTo(0.98f, 0.99f, 0.06f))));*/
			float derree = 0.7f+(150f/image.getWidth());
			image.addAction(Actions.forever	(Actions.sequence(
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
		if (image!=null) image.setZIndex(Zindex);
		//shadow.setZIndex(Zindex-count-1);
		
	}
}
