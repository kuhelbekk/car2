package com.starbox.puzzlecar2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;

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

public class StickerScene implements Screen {
	int screenHeight;
	int screenWidth;
	public int realHeight;
	public int realWidth;
	private int pfdx, pfdy; // смещение  пазла на экране
	
	private Viewport viewport;
	protected SpriteBatch batch;
	protected TextureAtlas textureAtlas;
	protected MainClass game;
	protected Sticker selectedStiker;
	protected ArrayList<Sticker> stikerList;
	public Stars fStars;/// искры при попадании детали
	protected Button btnDn, btnBack;
	protected Stage stage;
	protected Music mFon;
	protected Sound sCandy;
	protected ArrayList<Candy> listCandy;
	protected ArrayList<Integer> useGroup;
	
	//protected Image finishRays;
	protected Background bg;	
	long timeToFonMusic=0;
	float fonSoundLevel = 1;
	
	protected boolean finishAction;
	protected Button nextLevelButton;
	protected World world;	
	
	float PIXELS_TO_METERS =31;
	int stickerWidth = 146;
	int stickrHeight = 146;
	
	
	protected Group bgFrontGroup,
					bgBackGroup,
					puzzleGroup,
					finishSceneGroup,
					onTopGroup;
	
	
	public StickerScene(MainClass game) {
		super();
		this.game = game;
		this.createScene();		
	}	
	
	
	
	private boolean getElementCoord(Rectangle stickerFrame, Vector2 point){
		boolean overlap = false;
		int attempt=0;
		while (true){
			attempt++;
			overlap = false;
			point.x= (int) (Math.random()*(stickerFrame.width-stickerWidth)+stickerFrame.x);
			point.y= (int) (Math.random()*(stickerFrame.height-stickrHeight)+stickerFrame.y);
			Rectangle newElement = new Rectangle(point.x, point.y,stickerWidth,stickrHeight);
			
			for (int i =0 ; i< stikerList.size(); i++){			
				Rectangle element = new Rectangle(stikerList.get(i).endPoint.x , stikerList.get(i).endPoint.y ,stickerWidth,stickrHeight);	
				Gdx.app.log("overlap", "newElement x = " + newElement.x+ "    y = "+	newElement.y );
				Gdx.app.log("overlap", " element = " + element.x+ "    y = "+	element.y);
				if (newElement.overlaps(element)){
					Gdx.app.log("overlap", " overlap = true");
					overlap=  true;
					break;
				}
			}
			if (overlap){
				Gdx.app.log("overlap", " overlap = false");
				if (attempt>100)break;
			}else{
				break;
			}
		}	
		return !overlap;
	}
	
	public void createScene() {		
		finishAction = false;
		int fz=0;
		stikerList = new ArrayList<Sticker>();
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
		useGroup = new ArrayList<Integer>();
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
		finishSceneGroup = new Group();
		onTopGroup = new Group();
		
		stage.addActor(bgBackGroup);
		stage.addActor(puzzleGroup);		
		stage.addActor(finishSceneGroup);
		stage.addActor(onTopGroup);
		
		world = new World(new Vector2(0, -2.5f), true); ////box2d///////////*		
		
		//740*720		
		pfdx =(int) (screenWidth-game.maxWidht)/ 2;		
		pfdy =(int) (screenHeight -game.maxHeight)/2;	
		pfdx+=225+210;				
		
		////////
				
		try {		    
			XmlReader xmlReader = new XmlReader();	
		    XmlReader.Element root = xmlReader.parse(Gdx.files.internal("xml/stickers.xml")); 
		    XmlReader.Element atlasnameE = root.getChildByName("atlas");
			textureAtlas = new TextureAtlas(atlasnameE.get("name"));
		    XmlReader.Element bgnamexmlE = root.getChildByName("bg");
		    int ec =(int) Math.round(Math.random()*(bgnamexmlE.getChildCount()-1));
		    
		   	bg = new Background( bgBackGroup, game, bgnamexmlE.getChild(ec).getText() , screenWidth, screenHeight, true );
		    
		    fz=puzzleGroup.getChildren().size-1;
		    Gdx.app.log("fz","fz="+ fz); 
		   	
		   	XmlReader.Element element ;	    
		    XmlReader.Element elements = root.getChildByName("stikers");
		    int stikersCount = elements.getChildCount();
		    
		    
		    int[] randomArray = new int[stikersCount];
		    for (int i=0; i<(stikersCount-1);i++ )randomArray[i]=i;		    
		    for (int i=0; i<(stikersCount-1);i++ ){		    	
		    		int rnd = (int)(Math.random()*(stikersCount));		                  		           
		            int temp = randomArray[i];
		            randomArray[i] = randomArray[rnd];
		            randomArray[rnd] = temp;		            
		        }		    
		    Rectangle stickerFrame  = bg.getStikerField();
		    
		    Gdx.app.log("stickerFrame","stickerFrame="+ stickerFrame);
		    
		    int i=0;
		    int ind=0;
		    while (true){
		    	/// читаем xml
		    	element = elements.getChild(randomArray[i]);
		    	String nameReg=element.get("id");		    	
		    	String soundName = element.get("s");	
		    	int group =  element.getInt("group",0);	
		    	/// вставка элемента если влезет
		    	Vector2 point = new Vector2(0, 0);
		    	if (group>0) {
		    		if(useGroup.indexOf(group)==-1){
		    			useGroup.add(group);
		    		}else{
		    			i++;
		    			continue;
		    		}
		    	}
		    	if (getElementCoord( stickerFrame ,point)){
		    		Gdx.app.log("getElementCoord","x = "+point.x+ "  y = "+point.y);
			    	fz+=2;
			    	Sticker stick = new Sticker(this, textureAtlas, nameReg, ind+1 , (int)point.x, (int)point.y ,fz, soundName);   	
			    	stikerList.add(stick);		    	
			    	stick.imageSticker.setName("I"+nameReg);
			    	ind++;
			    	i++;
		    	}else break;
		    	    	
		    } 				    
		    		    
		  //  puzzleFrame.setZIndex(ecount+fz+1);
		  //  bg.drawFront(stikersCount*2+fz+1);
		 //   refreshZindElements();
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
		
		
		
		timeToFonMusic = TimeUtils.millis()-1000;	
		
		
		//showCandy();
		
		if (game.settings.isVoice()) {
			fonSoundLevel=0.6f;
		}else{
			fonSoundLevel=1f;
		}

		
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
		for (Sticker p : stikerList) {
			if(p != null) p.dispose();
		}
		
		
		world.dispose();
		batch.dispose();
		textureAtlas.dispose();
		stage.dispose();
	}
	

	public void elementMounted(Sticker pe) {
		int i = pe.index;
		pe.index = 0;
		pe.elementMounted = true;
		refreshZindElements();
		boolean fEndScene = true;
		for (Sticker p : stikerList) {
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
		}
	}
	
	
	
	private void refreshZindElements() {	
		Gdx.app.log("Game", "refreshZindElements");
		for (Sticker p : stikerList) {
			p.refreshZindex(stikerList.size());
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
			listCandy.add(b); // куча пузырей
			
			float x = (float)Math.random() * (screenWidth );
			if (x>(screenWidth/2) )
				x+=screenWidth/4;
				else x-=screenWidth/4;
				 
			b.setPosition(x,(float)(-Math.random()*400-100	)); // начальная позиция			
			b.setZIndex(300);
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


	private void showFinishButton() {
		//finishRays.setColor(1, 1, 1, 0);	
	//	finishRays.setVisible(true);
		// пошли лучики 
	/*	finishRays.addAction(Actions.sequence(
				Actions.alpha(0,1.3f),
				Actions.alpha(1, 0.7f,Interpolation.sineOut)
				));
				*/
		fStars = new Stars(finishSceneGroup, game.commonAtlas,true);
		fStars.playFinishStar((screenWidth)/2,(screenHeight)/2);
		
		
		
		/// пошла кнопка
		ButtonStyle bs = new ButtonStyle();
		bs.up = game.commonSkin.getDrawable("btn_next_up");
		bs.down = game.commonSkin.getDrawable("btn_next_dn");
		nextLevelButton = new Button(bs);		
		nextLevelButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.log("EndScene", "EndStikerScene");			
				game.nextStikerLevel();
				dispose();
				
			}
		});		
		
		nextLevelButton.setColor(1, 1, 1, 0.01f);	
		float brSize = 186;
		nextLevelButton.setSize(brSize*1.2f, brSize*1.2f); // *1.2
		nextLevelButton.setPosition(screenWidth/2 - (brSize*0.6f), screenHeight/2 - (brSize*0.6f));
		/// кнопка  со звездами
		nextLevelButton.addAction(Actions.sequence(
							Actions.alpha(0,0.1f),
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
		//if (waitingFinishAnimate<TimeUtils.millis()){
			showFinishButton();											
		} else { // ///// показываем конфеты			
			if (nextLevelButton.getActions().size==0) {
				if (listCandy==null) {					
					showCandy();	
				}
			} 
		}
	}

	public void BackClick() {
		Gdx.app.log("BtnClick", "exit StikerScene");
		game.setScreen(game.menu2d);
		dispose();
		
		
	}	
	
}
