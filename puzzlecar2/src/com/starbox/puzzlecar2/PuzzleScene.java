package com.starbox.puzzlecar2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.graphics.GL20;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PuzzleScene implements Screen {
	int screenHeight;
	int screenWidth;
	public int realHeight;
	public int realWidth;
	private int pfdx, pfdy; // смещение  пазла на экране
	public Stars stars,fStars;/// искры при попадании детали
	private Viewport viewport;
	protected SpriteBatch batch;
	protected TextureAtlas textureAtlas;
	protected MainClass game;
	protected PuzzleElement selectedElement;
	protected ArrayList<PuzzleElement> elementList;	
	protected ArrayList<Image> tempImageList;
	protected ArrayList<PuzzleElement> tempElementList;
    protected SameElements sameElements;
	protected Button btnDn, btnBack;
	private Stage stage;
	protected Music mFon;
	protected Sound sCandy, sStartSound;
	protected ArrayList<Candy> listCandy;	
	protected Image finishRays;
	protected Image puzzleFrame;
	protected Background bg;	
	XmlReader.Element finishAnimXML;
	long waitingFinishAnimate=1000;
	long waitingForFinishAnimate = 500;
	String xmlFileName;
	long timeToFonMusic=0;
	float fonSoundLevel = 1;
	
	
	protected boolean finishAction;
	protected Button nextLevelButton;
	protected World world;	
	
	float PIXELS_TO_METERS =31;

	
	protected Group bgFrontGroup,
					bgBackGroup,
					puzzleGroup,
					finishSceneGroup,
					onTopGroup;

	
	
	
	public PuzzleScene(MainClass game, String xmlFile) {
		super();
		this.game = game;
		this.createScene(xmlFile);
		this.btnBack.setZIndex(10);
	}	
	
	
	
	
	public void createScene(String xmlFileName) {
		Gdx.app.log("Game", "createScene");
		this.xmlFileName = xmlFileName;	
		finishAction = false;
		elementList = new ArrayList<PuzzleElement>();
		/// расчет смещения пазла и фона
		realWidth = Gdx.graphics.getWidth();
		realHeight = Gdx.graphics.getHeight();
		if ((realHeight > 1200) && (realWidth > 2000)) { // приведение ретин к  нормальному виду															
			realHeight /= 2;
			realWidth /= 2;
		}
		screenWidth = 1450;
		if ((realHeight <= 800) && (realHeight >= 720)) {
			screenHeight = realHeight;
		} else {
			screenHeight = 800;
		}
	      
		float a = (float) realWidth / screenWidth;
		float b = (float) realHeight / screenHeight;		
		if (a < b)
			screenWidth = (screenHeight * realWidth) / realHeight;
		if (screenWidth < 1000)
			screenWidth = 1000;
		if (screenWidth > 1450)
			screenWidth = 1450;
		
		Gdx.app.log("start", "realWidth = " + realWidth + " realHeight = "				+ realHeight);
		Gdx.app.log("start", "screenWidth = " + screenWidth				+ " screenHeight = " + screenHeight);
		
		batch = new SpriteBatch();
		viewport = new FitViewport(screenWidth, screenHeight);
		stage = new Stage(viewport,batch) {
			@Override
			public boolean keyDown(int keyCode) {				
				if (keyCode == Keys.BACK) {
					BackClick();
				}				
				return super.keyDown(keyCode);
			}
		};			
		
		bgBackGroup = new Group();
		puzzleGroup= new Group();
		bgFrontGroup = new Group();
		finishSceneGroup = new Group();
		onTopGroup = new Group();
		
		stage.addActor(bgBackGroup);
		stage.addActor(puzzleGroup);
		stage.addActor(bgFrontGroup);
		stage.addActor(finishSceneGroup);
		stage.addActor(onTopGroup);
		
		
		
		world = new World(new Vector2(0, -2.5f), true); ////box2d///////////*	
		//740*720		
		pfdx =(int) (screenWidth-game.maxWidht)/ 2;		
		pfdy =(int) (screenHeight -game.maxHeight)/2;	
		pfdx+=225+210;
				
		sameElements = new SameElements();
		
		
		////////
		
		
		try {		    
			XmlReader xmlReader = new XmlReader();	
			
		    XmlReader.Element root = xmlReader.parse(Gdx.files.internal(xmlFileName)); 
		    waitingFinishAnimate = root.getInt("waitingfinishanimate",100);	
		    finishAnimXML = root.getChildByName("finanim");	
		    XmlReader.Element atlasnameE = root.getChildByName("atlas");	
			textureAtlas = new TextureAtlas(atlasnameE.get("name"));
		    		   		    
		    XmlReader.Element bgnamexmlE = root.getChildByName("bg");
		   	bg = new Background( bgBackGroup, game, bgnamexmlE.get("name"), screenWidth, screenHeight, false );
		    
		   	String startSoundName = root.get("s");		    
		    if (game.settings.isSound() & game.settings.isVoice() & (!startSoundName.equals(""))) {			    	
				sStartSound = Gdx.audio.newSound(Gdx.files.internal("mfx/"+startSoundName+game.getLangStr()+".mp3"));
			}
		    
		   	
		   	pfdy+=root.getInt("dy",0);
		   	//// рамка
		   	XmlReader.Element element ;
		   	
		   	
			puzzleFrame = new Image(textureAtlas.findRegion("frame"));
			puzzleGroup.addActor(puzzleFrame);  
		    puzzleFrame.setPosition(pfdx,screenHeight- (puzzleFrame.getHeight()+pfdy));
		    puzzleFrame.setName("Frame");	
		    int fz= puzzleFrame.getZIndex();
			
		 // временная рамка изчезает при анимации
		    tempImageList = new ArrayList<Image>();
		    tempElementList = new ArrayList<PuzzleElement>();		    
		    
		 	XmlReader.Element frameE = root.getChildByName("frame");
		 	if (frameE!=null){
		 		for (int i=0; i<(frameE.getChildCount());i++ ){
			    				 		
			    	element = frameE.getChild(i);
			    	String nameE=element.get("id");
			    	int x =element.getInt("x");
			    	int y =element.getInt("y");	
			    	boolean bg = element.getBoolean("bg",false);	
			    	
			    	/// вставка элемента
			    	Image fr = new Image(textureAtlas.findRegion(nameE));
			    	if (bg){			    			    	
				    	bgBackGroup.addActor(fr);
			    	}else{
			    		tempImageList.add(fr);	    	
				    	puzzleGroup.addActor(fr);
			    	}
			    	
			    	fr.setPosition(pfdx+x,screenHeight- (fr.getHeight()+pfdy+y));
			    	fz++;
			    	fr.setZIndex(fz);
			    	fr.setName(nameE);
			    } 	
		 	}
		 	
		 	
		    XmlReader.Element elements = root.getChildByName("assets");
		    int ecount = elements.getChildCount();		   
		   /////////////////
		    int[] randomArray = new int[ecount];
		    for (int i=0; i<(ecount);i++ )randomArray[i]=i+1;		    
		    for (int i=0; i<(ecount);i++ ){		    	
		    		int rnd = (int)(Math.random()*(ecount));		                  		           
		            int temp = randomArray[i];
		            randomArray[i] = randomArray[rnd];
		            randomArray[rnd] = temp;		            
		        }		    
		    
		    for (int i=0; i<(ecount);i++ ){
		    	/// читаем xml
		    	element = elements.getChild(i);
		    	String nameReg=element.get("id");
		    	int x =element.getInt("x");
		    	int y =element.getInt("y");		    	
		    	int se = element.getInt("se", -1);	
		    	String crop = element.get("crop", "");		    	
		    	String soundName = element.get("s");	
		    	boolean finishHide =  element.getBoolean("finishhide",false);
		    	/// вставка элемента
		    	PuzzleElement pe;		    	
		    	if (crop!="") {
		    		pe = new PuzzleElement(this, textureAtlas.findRegion(crop),textureAtlas.findRegion(nameReg), randomArray[i],pfdx+x, screenHeight - y-pfdy,i+1+fz, soundName);
		    	}else {
		    		pe = new PuzzleElement(this, textureAtlas.findRegion(nameReg), randomArray[i],pfdx+x, screenHeight - y-pfdy,i+1+fz, soundName);
		    	}
		    	if(finishHide) tempElementList.add(pe);			    		
		    	
		    	elementList.add(pe);
		    	if (se>=0) {
		    		sameElements.put(se,pe);		    		
		    	}
		    	pe.image.setName("I"+nameReg);	    	
		    } 				    
		    		  
		    bg.drawFront(ecount+fz+2, bgFrontGroup );
		   // refreshZindElements();
		   // if (bgElement>0) peBG.fixing(false);			    
		} catch (IOException e) {			
			e.printStackTrace();
		}

		if (game.settings.isMusic() & game.settings.isSound()) {
			long rnd = Math.round((Math.random() * 2));
			mFon = Gdx.audio.newMusic(Gdx.files.internal("mfx/s" + rnd + ".mp3"));			
			mFon.setVolume(0.01f);			
			mFon.setLooping(true);	
			mFon.play();
		}

		
		
		if (game.settings.isSound()) {			
			sCandy = Gdx.audio.newSound(Gdx.files.internal("mfx/candy.mp3"));			
					
		}

		// /back
		ButtonStyle bs = new ButtonStyle();
		bs.up = game.commonSkin.getDrawable("btn_back_up");
		bs.down = game.commonSkin.getDrawable("btn_back_dn");
		btnBack = new Button(bs);
		btnBack.setPosition(screenWidth-btnBack.getWidth(),screenHeight-btnBack.getHeight());
		onTopGroup.addActor(btnBack);
		btnBack.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				BackClick();
			}
		});
		
		/// анимация старта
		stage.addAction(Actions.parallel(
				       Actions.sequence(Actions.scaleTo(1.5f,1.5f),						   			   Actions.scaleTo(1,1, 0.4f,Interpolation.pow2Out)  ),
				       Actions.sequence(Actions.alpha(0),											   Actions.alpha(1, 0.4f)  ),
				       Actions.sequence(Actions.moveTo(-screenWidth/4, -screenHeight/4), Actions.moveTo(0,0, 0.4f,Interpolation.pow2Out)  )
				));
		
		stars = new Stars(puzzleGroup,game.commonAtlas,false);
		
		if ((sStartSound != null)) {			
			while (sStartSound.play(1f)==-1){
				Gdx.app.log("sStartSound", "sStartSound -1");
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			timeToFonMusic = TimeUtils.millis();			
		}else{
			timeToFonMusic = TimeUtils.millis()-1000;	
		}
		
		//showCandy();
		
		if (game.settings.isVoice()) {
			fonSoundLevel=0.6f;
		}else{
			fonSoundLevel=1f;
		}

		//showFinishScreen();
	}

	@Override
	public void show() {
		Gdx.app.log("Game", "show Scene");
		
		Gdx.input.setInputProcessor(stage);		
	}

	@Override
	public void render(float delta) {		
		/// запустить фоновую музыку после озвучки задания
		bg.render(delta);
		if ((timeToFonMusic>0) & (mFon!=null)){
			if ((timeToFonMusic+2000)<TimeUtils.millis()) {				
					mFon.setVolume(fonSoundLevel);
					timeToFonMusic=0;				
			}else{
				//Gdx.app.log("Game", "setVolume = "+(((float)(TimeUtils.millis()-timeToFonMusic))/2000f));
				 mFon.setVolume((((float)(TimeUtils.millis()-timeToFonMusic))/2000f)*fonSoundLevel);
			}
		}
		
		world.step(1f/60f, 0, 0);  		
		Gdx.gl.glClearColor(1,1,1,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		

		if (finishAction) {//  собрали пазл
			drawFinishAction();
		}	      
	      
		if (listCandy != null) {
			if (!listCandy.isEmpty()) { // убийство лопнутых и улетевших пузырей
				Iterator<Candy> iterator = listCandy.iterator();
				while (iterator.hasNext()) {
					Candy b = (Candy) iterator.next();
					if (b.isFinished()) {
						b.remove();
						iterator.remove();
					}else{
						// box2d	
						b.render(PIXELS_TO_METERS);	
					}
				}
			}				
		}		
	      
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		// Gdx.app.log("Game", "resize Scene Car");
		stage.getViewport().update(width, height,true);
	}

	@Override
	public void hide() {
		Gdx.app.log("Game", "hide Scene ");
	}

	@Override
	public void pause() {
		Gdx.app.log("Game", "pause Scene ");
	}

	@Override
	public void resume() {
		Gdx.app.log("Game", "resume Scene ");
	}

	@Override
	public void dispose() {
		
		Gdx.app.log("Game", "dispose Scene ");
		if (mFon != null){
			mFon.stop();
			mFon.dispose();
		}
		
	
		if (sCandy != null)
			sCandy.dispose();
		for (PuzzleElement p : elementList) {
			if(p != null) p.dispose();
		}
		
		
		world.dispose();
		batch.dispose();
		textureAtlas.dispose();
		stage.dispose();
	}
	

	public void elementMounted(PuzzleElement pe) {
		int i = pe.index;
		pe.index = 0;
		pe.elementMounted = true;
		refreshZindElements();
		boolean fEndScene = true;
		for (PuzzleElement p : elementList) {
			if (p.index > i) {
				--p.index;
				p.setPosToStartPoint(0);
			}
			if (p.index > 0)
				fEndScene = false;
		}
		// конец игры
		if (fEndScene) {
			if (mFon != null)
				if (mFon.isPlaying())
					mFon.stop();
			
			
			
			finishAction = true;
			waitingFinishAnimate+=TimeUtils.millis();
			waitingForFinishAnimate+=TimeUtils.millis();
			
		}
	}
	
	
	
	
	private void refreshZindElements() {	
		
		for (PuzzleElement p : elementList) {
			p.refreshZindex(elementList.size());
		}
		
	}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	protected void showCandy() {
		listCandy = new ArrayList<Candy>();				
		for (int c = 0; c < 32; c++) { // количество конфет
			TextureRegion[] CandyFrames = game.GetAnimFrames(game.commonAtlas.findRegion("candy"+(int)(Math.random()*4)) , 188, 134); // создание массива кадров для анимации
			Animation anim = new Animation(0.04f, CandyFrames); // задание скорости	 анимации
			AnimationDrawable drawable = new AnimationDrawable(anim); // создание отрисовщика
			Candy b = new Candy(drawable, sCandy , game.commonAtlas,finishSceneGroup); // / компонент пузыря
			listCandy.add(b); 
			
			float x = (float)Math.random() * (screenWidth );
			if (x>(screenWidth/2) )
				x+=screenWidth/4;
				else x-=screenWidth/4;
				 
			b.setPosition(x,(float)(-Math.random()*400-100	)); // начальная позиция			
			//b.setZIndex(300);
			//b.setScale(0.3f + (float) (Math.random() / 2));
			finishSceneGroup.addActor(b);
			b.addListener(new ClickListener() { // попадание по пузырю
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
					//((Candy) (event.getListenerActor())).body.applyLinearImpulse(0f, 0.5f, ((Candy) (event.getListenerActor())).body.getPosition().x,  
					//																	   ((Candy) (event.getListenerActor())).body.getPosition().y,true);
					((Candy) (event.getListenerActor())).clickCandy();
					return true;
				}
			});		

			
			/// box2d			
			BodyDef bodyDef = new BodyDef();
		    bodyDef.type = BodyDef.BodyType.DynamicBody;		     
		    bodyDef.position.set((b.getX() + 188/2) /PIXELS_TO_METERS,
		    					(b.getY() + 134/2) / PIXELS_TO_METERS);		    
		    b.body = world.createBody(bodyDef);
		    PolygonShape shape = new PolygonShape();
		    shape.setAsBox((9) / PIXELS_TO_METERS, (7) / PIXELS_TO_METERS);
		    FixtureDef fixtureDef = new FixtureDef();
	        fixtureDef.shape = shape;	        
	        fixtureDef.density = 0.1f;
	        b.body.createFixture(fixtureDef);
	        shape.dispose();
	        b.body.applyTorque(((float)Math.random()*0.2f)-0.1f, true);
	        
	        b.body.applyLinearImpulse( ((b.getX()-(screenWidth/2))*((float)Math.random())*(-0.01f))/PIXELS_TO_METERS ,(float)Math.random()*0.1f+0.25f, b.body.getPosition().x,  b.body.getPosition().y,true);
	      //  b.body.applyForce(10, 10,b.body.getPosition().x, b.body.getPosition().y, true);
	   
		}
		
		 
	}


	
	public int getPFDx() {
		return pfdx;
	}

	public int getPFDy() {
		return pfdy;
	}
	

	private void showFinishScreen() {
		Gdx.app.log("Game", "showFinishScreen");
		// тушим лишний фон
		for (Image ifr: tempImageList) 	ifr.setVisible(false);
		for (PuzzleElement pe: tempElementList) 	pe.image.setVisible(false);
			
		//
		
		
		
		int zind=puzzleGroup.getChildren().size;
		XmlReader.Element img;
		Image image;
		for (int i=0; i<(finishAnimXML.getChildCount());i++ ){
	    	/// читаем xml
			img = finishAnimXML.getChild(i);
	    	String nameImg=img.get("id");
	    	int x = img.getInt("x");
	    	int y = img.getInt("y");	
	    	int w = img.getInt("w",0);
	    	int h = img.getInt("h",0);
	    	int z = img.getInt("z", 0)+zind;
	    	int speedAnim = img.getInt("speed", 5);
	    	int waiting = img.getInt("waiting", 0);	    	    	
	    	int animFarame = img.getInt("anim", 0);	    			    	
	    	String soundName = img.get("s","");	
	    	int typeAnim = img.getInt("typeanim",0);	  	///0-NoAnim; 1-Sprite ; 2-rotate	    	
	    	boolean loopAnim = img.getBoolean("loop",false);
	    	
	    	
	    	switch (typeAnim) {
	    	case 0:///NoAnim
	    		image = new Image( textureAtlas.findRegion(nameImg));				
				image.setPosition(pfdx+x, 800-image.getHeight()-y -pfdy);
				image.setZIndex(z);
				puzzleGroup.addActor(image);	
	    		break;
			case 1: //Sprite
				//AnimationDrawable drawableActivate = null;		   	
		    	/// вставка элемента      	
				Gdx.app.log("anim","drawElement-" + nameImg);
				AnimationDrawable drawableEnd = null;
				XmlReader.Element  nextanim = img.getChildByName("nextanim"); 
				
				TextureRegion tr = textureAtlas.findRegion(nameImg);
		    	TextureRegion[] Frames = game.GetAnimFrames(tr,w, h, animFarame); // создание массива кадров для анимации
				Animation animate = new Animation(speedAnim*0.01f, Frames); // задание скорости	 анимации				
				AnimationDrawable drawable = new AnimationDrawable(animate); // создание отрисовщика
				spriteAnimate aImg = null;
				if (nextanim!=null){						
		    		TextureRegion[] frames = game.GetAnimFrames(textureAtlas.findRegion(nextanim.get("name")),nextanim.getInt("w",0), nextanim.getInt("h",0), nextanim.getInt("anim")); // создание массива кадров для анимации
					Animation animateAct = new Animation(nextanim.getInt("speed")*0.01f, frames); // задание скорости	 анимации				
					drawableEnd = new AnimationDrawable(animateAct); // создание отрисовщика 					
					aImg = new spriteAnimate(drawable, drawableEnd, null, soundName ,waiting, puzzleGroup, pfdx+x, screenHeight-y-h -pfdy,z, loopAnim, waiting>1,
											pfdx+ nextanim.getInt("x"),screenHeight- nextanim.getInt("h",0) -pfdy- nextanim.getInt("y"),nextanim.getBoolean("hideparent")); // 						
		    	}else{		    		
					aImg = new spriteAnimate(drawable, drawableEnd, null, soundName ,waiting, puzzleGroup, pfdx+x, screenHeight-y-h -pfdy,z, loopAnim, waiting>1); // 								
		    	}
				
				//puzzleGroup.addActor(aImg);
		    						
				break;
			case 2: //rotate				
				image = new Image( textureAtlas.findRegion(nameImg));				
				image.setPosition(pfdx+x, 800-image.getHeight()-y -pfdy);
				image.setZIndex(z);
				image.setOrigin(image.getWidth()/2, image.getHeight()/2);
				image.addAction(Actions.forever(Actions.rotateBy(90, speedAnim)));
				puzzleGroup.addActor(image);
				break;
			case 7: //particle emmiter.
				ParticleEffect pe;
				ParticleEffectActor pea;
				pe = new ParticleEffect();
				Gdx.app.log("particle","nameImg- " + nameImg);
				pe.load(Gdx.files.internal(nameImg),Gdx.files.internal(""));
			    pe.getEmitters().first().setPosition( pfdx+x, screenHeight-y -pfdy);
			    pe.start();
			    
				pea = new ParticleEffectActor(pe);	
				pea.setPosition(pfdx+x, screenHeight-y -pfdy);
				puzzleGroup.addActor(pea);
				
				
				break;			 
			case 9: //evakuator
				image = new Image( textureAtlas.findRegion(nameImg));				
				image.setPosition(screenWidth-20, screenHeight-(image.getHeight()+pfdy+y));
				image.addAction(Actions.sequence(
						Actions.moveTo( pfdx+x, screenHeight-(image.getHeight()+pfdy+y), 1f, Interpolation.pow2Out),
						Actions.alpha(1,2f),
						Actions.alpha(0,0.001f)
						));
				bgBackGroup.addActor(image);				
				break;
			case 10: //crane				
				image = new Image( textureAtlas.findRegion(nameImg));				
				image.setPosition(pfdx+x, screenHeight-(image.getHeight()+pfdy+y));
				image.addAction(Actions.sequence(
						Actions.moveTo( pfdx+x, screenHeight-(image.getHeight()+pfdy+y-10), 0.5f,Interpolation.pow2Out),
						Actions.moveTo( pfdx+x, screenHeight-(image.getHeight()+pfdy+y+110), 4f,Interpolation.pow2Out)
						));
				bgBackGroup.addActor(image);
				break;
			}  
 		
		}		
		
		finishAnimXML=null;		
		
		
	}

	private void showFinishButton() {
		Gdx.app.log("showFinishButton", "showFinishButton!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		fStars = new Stars(finishSceneGroup, game.commonAtlas,true);
		fStars.playFinishStar((screenWidth)/2,(screenHeight)/2);		
		/// пошла кнопка
		ButtonStyle bs = new ButtonStyle();
		bs.up = game.commonSkin.getDrawable("btn_next_up");
		bs.down = game.commonSkin.getDrawable("btn_next_dn");
		nextLevelButton = new Button(bs);		
		nextLevelButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.log("EndScene", "EndPuzzleCar");
				game.nextLevel(xmlFileName);
				dispose();
				
			}
		});		
		
		nextLevelButton.setColor(1, 1, 1, 0.01f);	
		float brSize = 186;
		nextLevelButton.setSize(brSize*1.2f, brSize*1.2f); // *1.2
		nextLevelButton.setPosition(screenWidth/2 - (brSize*0.6f), screenHeight/2 - (brSize*0.6f));
		/// кнопка  со звездами
		nextLevelButton.addAction(Actions.sequence(
							Actions.parallel(
									Actions.alpha(0.5f,0.15f),
									Actions.sizeTo(brSize*0.9f, brSize*0.9f, 0.15f),
									Actions.moveTo(screenWidth/2 - (brSize*0.45f), screenHeight/2 - (brSize*0.45f) , 0.15f)),
							Actions.parallel(
									Actions.alpha(1,0.05f),
									Actions.sizeTo(brSize, brSize, 0.05f),
									Actions.moveTo(screenWidth/2 - brSize/2, screenHeight/2 - brSize/2 , 0.05f))
							));	
		
		finishSceneGroup.addActor(nextLevelButton);
		
		if (game.settings.isMusic() & game.settings.isSound()) {
			mFon.stop();
			mFon.dispose();			
			mFon = Gdx.audio.newMusic(Gdx.files.internal("mfx/fon_finish.mp3"));			
			mFon.setVolume(0.01f);			
			mFon.setLooping(true);	
			mFon.play();		
			timeToFonMusic = TimeUtils.millis();	
		}
	}
	
	private void drawFinishAction() {		
		//анимация собранного пазла	
		if (nextLevelButton == null) { // / фары не отмигали
			
			if(finishAnimXML==null){
				if (waitingFinishAnimate<TimeUtils.millis()){
					showFinishButton();
				}
			}else{
				if (waitingForFinishAnimate<TimeUtils.millis()){
					showFinishScreen();	
				}
			}
			
											
		} else { // ///// показываем конфеты			
			if (nextLevelButton.getActions().size==0) {
				if (listCandy==null) {					
					showCandy();	
				}
			} else {				

			}

		}
	}

	public void BackClick() {
		Gdx.app.log("BtnClick", "exit PuzzleScene");
		game.setScreen(game.menu2d);
		dispose();
		
	
	}	
	
}
