package com.starbox.puzzlecar2;

import java.io.IOException;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Menu2d implements Screen {
	MainClass game;
	private int screenHeight;
	private int screenWidth;
	private int realHeight;
	private int realWidth;
	private int dx; // dx fon
	private SpriteBatch batch;
	private TextureAtlas textureAtlas;
	public ArrayList<Button> PremiumButtons;

	private Image bgPay, PayForm, lablePay;
	private Image btnPay;
	private Button btnClosePay;
	public boolean blockButton = false;

	private Viewport viewport;
	private Stage stage;
	private Skin skin;
	private Image gameName,rays;
	
	private Button btnPuzzle, btnStikers;
	private Table table11,tableSettings, tableLangs;
	private Button btnBack;
	protected Sound sButton;
	protected Button  btnSpeech,btnMusic,btnLangRu,btnLangEn,btnLangFr,btnLangDe,btnLang;
	protected Button  btnYTlink;
	protected Image arrow;
	private long BackClickDnTime;
	private int BackClickCount;

	private boolean closeLangTable = false;
	protected Music mFon;
	long timeToFonMusic=0;
	
	public ArrayList<Button> btnList;
	public ArrayList<MenuLevelButton>  levelButtons;
	private boolean startGame;

	public Menu2d(MainClass game) {
		super();
		this.game = game;
		this.createScene();
	}

	public void createScene() {
		
		Gdx.app.log("Game", "createSceneMenu");
		BackClickDnTime = 0;
		BackClickCount = 0;
		textureAtlas = new TextureAtlas("data/menu.atlas");
		realWidth = Gdx.graphics.getWidth();
		realHeight = Gdx.graphics.getHeight();
		if ((realHeight > 1200) && (realWidth > 2000)) { // retina
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
		if (screenWidth < 980)
			screenWidth = 980;
		if (screenWidth > 1450)
			screenWidth = 1450;

		dx = (screenWidth - 1450) / 2;
		Gdx.app.log("start", "realWidth = " + realWidth + " realHeight = "				+ realHeight);
		Gdx.app.log("start", "screenWidth = " + screenWidth				+ " screenHeight = " + screenHeight);
		
		
	/*	batch = new SpriteBatch();		
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
		
		*/
		
		batch = new SpriteBatch();
		viewport = new FitViewport(screenWidth, screenHeight);
		stage = new Stage(viewport,batch) {
			@Override
			public boolean keyDown(int keyCode) {				
				if (keyCode == Keys.BACK) {
					BackClickDn(false);
				}				
				return super.keyDown(keyCode);
			}
			@Override
			public boolean keyUp(int keyCode) {
				if (keyCode == Keys.BACK) {
					BackClickUp(false);
				}
				return super.keyUp(keyCode);
			}
		};		
		
		
		skin = new Skin(textureAtlas);
		btnList = new ArrayList<Button>();
		levelButtons = new ArrayList<MenuLevelButton>() ;
		Image bg = new Image(textureAtlas.findRegion("bg"));
		bg.setPosition(dx, 0);
		stage.addActor(bg);
		Button btn;
		// // menu level 1
		
		rays = new Image(game.commonAtlas.findRegion("rays"));
		rays.setScale(1.6f);
		rays.setPosition(screenWidth / 2 - rays.getWidth() / 2, screenHeight - 250 - rays.getHeight() / 2);
		
		rays.setOrigin(rays.getWidth() / 2, rays.getHeight() / 2);
		stage.addActor(rays);
		
		
		gameName = new Image(textureAtlas.findRegion("gameName"));
		gameName.setPosition(screenWidth / 2 - gameName.getWidth() / 2, screenHeight - 180 - gameName.getHeight() / 2);
		gameName.setOrigin(gameName.getWidth() / 2, gameName.getHeight() / 2);
		stage.addActor(gameName);
		
		
		btnPuzzle = addBtnOnMenu("btn_1_up", "btn_1_dn","","",screenWidth/2-362-40, screenHeight-620,true);
		btnPuzzle.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				if (game.settings.isSound())
					sButton.play();				
				showSelectLevelScreen();
			}
		});
		
		btnStikers = addBtnOnMenu("btn_2_up", "btn_2_dn","","",screenWidth/2+40, screenHeight-620,true);
		btnStikers.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {

				blockButton = true;
				Screen screen = new StickerScene(game);
				if (event.getButton() > 0)
					return;
				if (game.settings.isSound())
					sButton.play();

				game.setScreen(screen);
			}
		});
		
		
		try {
			XmlReader xmlReader = new XmlReader();			
		    XmlReader.Element root = xmlReader.parse(Gdx.files.internal("xml/menu.xml")); 
		    int cols = root.getInt("cols",4);
		    int rows = root.getInt("rows",4);	    
		    XmlReader.Element btnsE = root.getChildByName("btns");	  
		    XmlReader.Element btnE; 
		    if (btnsE != null){	    	
		    	// // menu level 11
		    	//Gdx.app.log("menu xml", "img_index = " + realWidth + " realHeight = "				+ realHeight);
				table11 = new Table();			
				table11.setBounds(screenWidth/2-500, screenHeight/2-360,1000, 720);
				PremiumButtons = new ArrayList<Button>();
				for (int i = 0; i< rows; i++){
					for (int j = 0; j< cols ; j++){
						if (((i*cols)+j)<btnsE.getChildCount()){
							Gdx.app.log("menu xml", "get btn - " + ((i*cols)+j) );
							btnE = btnsE.getChild((i*cols)+j);
							Gdx.app.log("start", "screenWidth = " + screenWidth				+ " screenHeight = " + screenHeight);
							boolean lock = btnE.getBoolean("premium");

							btn = addLevelBtn(btnE.get("img_index"),btnE.get("xml_name"), lock);
							if (lock) PremiumButtons.add(btn);
							table11.add(btn).expand();
							}
													
						}	
					table11.row();
					}					
				}
			
		}catch (IOException e) {			
			e.printStackTrace();
		}
		
		table11.setOrigin(table11.getWidth() / 2, table11.getHeight() / 2);
		table11.setVisible(false);
		stage.addActor(table11);



		
		tableLangs = new Table();
		tableLangs.setBounds(screenWidth-97, 67 , 87, 116);			//////////////////////////////////////////////////
		
		tableSettings = new Table(); 
		tableSettings.setBounds(screenWidth/2-190, 3, 380, 92);					/////////////////////////////////////////////
		tableSettings.addAction(Actions.sequence(Actions.alpha(0f, 0),
				Actions.alpha(1f, 0.5f)));
		
		
		stage.addActor(tableLangs);
		stage.addActor(tableSettings);
		
		
		
		
/*
		Gdx.app.log("Settings", "Sound:" + game.settings.isSound());
		Gdx.app.log("Settings", "LangID:" + game.settings.getLangID());
		Gdx.app.log("Settings", "isMusic:" + game.settings.isMusic());
		Gdx.app.log("Settings", "isVoice:" + game.settings.isVoice());*/

		// // sound
		btn = addBtnOnMenu("snd_on", "snd_dn", "snd_na", "snd_off");
		btn.setChecked(!game.settings.isSound());
		btn.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				sButton.play();
				boolean c = ((Button) event.getListenerActor()).isChecked();
				Gdx.app.log("Settings", "Sound:" + !c);
				game.settings.setSound(!c);
				btnSpeech.setDisabled(c);
				btnMusic.setDisabled(c);
				btnLangFr.setDisabled(c | !game.settings.isVoice());
				btnLangDe.setDisabled(c | !game.settings.isVoice());
				btnLangRu.setDisabled(c | !game.settings.isVoice());
				btnLangEn.setDisabled(c | !game.settings.isVoice());
				btnLang.setDisabled(c | !game.settings.isVoice());
				checkedLang();		
				if (game.settings.isMusic() & game.settings.isSound()) {		
					mFon.play();
					mFon.setVolume(0.01f);
					timeToFonMusic = TimeUtils.millis();	
				}else{
					mFon.stop();
				}
			}

		});
		tableSettings.add(btn).expand();;

		// // music
		btnMusic = addBtnOnMenu("music_on", "music_dn", "music_na", "music_off");
		btnMusic.setChecked(!game.settings.isMusic());
		btnMusic.setDisabled(!game.settings.isSound());
		btnMusic.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				if (!((Button) event.getListenerActor()).isDisabled()) {
					sButton.play();
					boolean c = ((Button) event.getListenerActor()).isChecked();
					game.settings.setMusic(!c);
					Gdx.app.log("Settings", "Music:" + !c);
					if (game.settings.isMusic() & game.settings.isSound()) {
						mFon.play();
						mFon.setVolume(0.01f);
						timeToFonMusic = TimeUtils.millis();
					} else {
						mFon.stop();
					}
				}
			}

		});
		tableSettings.add(btnMusic).expand();

		//// speach
		btnSpeech = addBtnOnMenu("speech_on", "speech_dn", "speech_na", "speech_off");
		btnSpeech.setChecked(!game.settings.isVoice());
		btnSpeech.setDisabled(!game.settings.isSound());
		btnSpeech.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				if (!((Button) event.getListenerActor()).isDisabled()) {
					sButton.play();
					boolean c = ((Button) event.getListenerActor()).isChecked();
					game.settings.setVoice(!c);
					btnLangRu.setDisabled(c);
					btnLangDe.setDisabled(c);
					btnLangEn.setDisabled(c);
					btnLangFr.setDisabled(c);
					btnLang.setDisabled(c);
					checkedLang();
					Gdx.app.log("Settings", "Speech:" + !c);
				}
			}

		});
		tableSettings.add(btnSpeech).expand();
		/// lang button
		
		
		createLangButton();		
		
		// // lang
		btnLangRu = addBtnOnMenu("ru_up", "ru_up", "ru_na", "ru_act");
		btnLangRu.setDisabled((!game.settings.isVoice()) || (!game.settings.isSound()));
		btnLangRu.setChecked(game.settings.getLangID() == 1);
		btnLangRu.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				if (!((Button) event.getListenerActor()).isDisabled()) {
					changeLang(1);
				}
			}
		});
		tableLangs.add(btnLangRu).expand().row();
		
		
		btnLangEn = addBtnOnMenu("en_up", "en_up", "en_na", "en_act");
		btnLangEn.setDisabled((!game.settings.isVoice())
				|| (!game.settings.isSound()));
		btnLangEn.setChecked(game.settings.getLangID() == 0);
		btnLangEn.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				if (!((Button) event.getListenerActor()).isDisabled()) {
					changeLang(0);
				}
			}
		});
		tableLangs.add(btnLangEn).expand().row();

		btnLangFr = addBtnOnMenu("fr_up", "fr_up", "fr_na", "fr_act");
		btnLangFr.setVisible(false);
		/*btnLangFr.setDisabled((!game.settings.isVoice())
				|| (!game.settings.isSound()));
		btnLangFr.setChecked(game.settings.getLangID()==2);
		btnLangFr.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				if (!((Button) event.getListenerActor()).isDisabled()) {
					changeLang(2);	
				}
			}
		});
		tableLangs.add(btnLangFr).expand().row();*/
		
		
		btnLangDe = addBtnOnMenu("de_up", "de_up", "de_na", "de_act");
		btnLangDe.setVisible(false);
	/*	btnLangDe.setDisabled((!game.settings.isVoice())
				|| (!game.settings.isSound()));
		btnLangDe.setChecked(game.settings.getLangID()==2);
		btnLangDe.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				if (!((Button) event.getListenerActor()).isDisabled()) {
					changeLang(3);	
				}
			}
		});
		tableLangs.add(btnLangDe).expand().row();*/
		
		ButtonStyle bs = new ButtonStyle();		
		// //// back
		bs = new ButtonStyle();
		bs.up = game.commonSkin.getDrawable("btn_back_up");
		bs.down = game.commonSkin.getDrawable("btn_back_dn");
		btnBack = new Button(bs);
		btnBack.setPosition(screenWidth-btnBack.getWidth(),screenHeight-btnBack.getHeight());
		btnList.add(btnBack);
		stage.addActor(btnBack);
		btnBack.addListener(new ClickListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button){
				BackClickDn(true);
				return true;
			}
			public void touchUp (InputEvent event, float x, float y, int pointer, int button){
				BackClickUp(true);
			}
		});
		btnBack.setZIndex(115);

		for (Button bn : btnList) {
			bn.addListener(new ClickListener() {
				public boolean touchDown(InputEvent event, float x, float y,int pointer, int button) {
					btnsTochDisable(Touchable.disabled,	(Button) event.getListenerActor());
					return true;
				}
				public void touchUp(InputEvent event, float x, float y,	int pointer, int button) {
					btnsTochDisable(Touchable.enabled,	(Button) event.getListenerActor());
				}
			});
		}
		
		sButton = Gdx.audio.newSound(Gdx.files.internal("mfx/button.mp3"));		
	    createPayFrame();	    
		mFon = Gdx.audio.newMusic(Gdx.files.internal("mfx/fon_finish.mp3"));		
		mFon.setLooping(true);
		
		startGame = true;    
	}




	private void changeLang(int idLang) {
		sButton.play();						
		Gdx.app.log("Settings", "changeLang");			
		game.settings.setLangID(idLang);		
		btnLangRu.setChecked(false);
		btnLangEn.setChecked(false);
		btnLangFr.setChecked(false);
		btnLangDe.setChecked(false);
		switch (idLang) {
		case 0:
			btnLangEn.setChecked(true);
			break;
		case 1:
			btnLangRu.setChecked(true);
			break;
		case 2:
			btnLangFr.setChecked(true);
			break;		
		case 3:
			btnLangDe.setChecked(true);
			break;	
		}		
		createLangButton();		
		
	}
	
	
	private void createLangButton(){
		/// lang buttton
		if (btnLang!=null){				
			btnLang.remove();
			closeLangTable=true;
			tableLangs.setColor(1, 1, 1, 1);
			tableLangs.addAction(Actions.alpha(0,1  ,Interpolation.pow4In));
			arrow.remove();
			lablePay.remove();
		}

		if (btnYTlink!=null){
			btnYTlink.remove();
		}

		String langStr = "en";
		switch (game.settings.getLangID()) {
		case 1:
			langStr="ru";
			break;
		case 2:
			langStr="fr";
			break;
		case 3:
			langStr="de";
			break;	
		default:
			langStr="en";
			break;
		}


		btnLang = addBtnOnMenu(langStr+"_up", langStr+"_act", langStr+"_na", "", screenWidth-97, 10, true );
		btnLang.setDisabled((!game.settings.isVoice()) || (!game.settings.isSound()));
		btnLang.setChecked(false);
		btnLang.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				if (!btnLang.isDisabled()) {
					clickLang();					
				}
			}			
		});
			
		arrow = new Image(textureAtlas.findRegion("arrow_dn"));
		arrow.setPosition(screenWidth-35, 31);
		arrow.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				if (!btnLang.isDisabled()) {
					clickLang();					
				}
			}			
		});		
		stage.addActor(arrow);

		if (game.getLangStr().equals("_de")) lablePay = new Image(textureAtlas.findRegion("by_txt_en"));  	/// костыль
			else lablePay = new Image(textureAtlas.findRegion("by_txt"+game.getLangStr()));
		if (PayForm!=null){
			lablePay.setPosition(PayForm.getX() + PayForm.getWidth() / 2 - lablePay.getWidth()/ 2, PayForm.getY() + PayForm.getHeight() - 20	- lablePay.getHeight());
		}
		lablePay.setZIndex(180);
		lablePay.setVisible(false);
		stage.addActor(lablePay);

		if (btnYTlink!=null){
			btnYTlink.remove();
		}
		btnYTlink = addBtnOnMenu("logo"+game.getLangStr(),"logo"+game.getLangStr(),"", "",10,  screenHeight - 115, true );
		btnYTlink.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				game.payFrame.youTubeClick(game.getLangStr());
			}
		});
	}
	
	
	protected void clickLang() {
		if (tableLangs.isVisible()){
			tableLangs.setVisible(false);
			sButton.play();	
		}else{								
			tableLangs.setVisible(true);
			tableLangs.setColor(1, 1, 1, 0f);
			tableLangs.addAction(Actions.alpha(1f, 0.4f));
			sButton.play();		
		}
	}

	@Override
	public void render(float delta) {
		
		if ((timeToFonMusic>0)){
			if ((timeToFonMusic+2000)<TimeUtils.millis()) {				
				mFon.setVolume(0.7f);
				timeToFonMusic=0;
			}else{				
				 mFon.setVolume(((float)(TimeUtils.millis()-timeToFonMusic))/2900f);
			}
		}
		
		
		if (closeLangTable)
			if (tableLangs.getActions().size==0){
				tableLangs.setVisible(false);
				closeLangTable=false;
			}		
		
		Gdx.gl.glClearColor(0.788f, 0.65f, 0.435f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(delta);
		stage.draw();


		if ((BackClickDnTime >10)&(BackClickDnTime < TimeUtils.millis())){
			if(btnBack.isPressed()) {
				Gdx.app.exit();
			}else{
				BackClickDnTime=0;
			}
		}

	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height,true);
	}

	@Override
	public void show() {
		
		
		Gdx.app.log("Game", "Show SceneMenu");
		Gdx.input.setInputProcessor(stage);	
		if (game.settings.isMusic() & game.settings.isSound()) {		
			mFon.play();
			mFon.setVolume(0.01f);
			timeToFonMusic = TimeUtils.millis();	
		}			
		blockButton = false;		
		if (startGame ){  // непонятные рандомные костыли
			startGame = false;
			showFirstScreen(true);	
		}
	}

	@Override
	public void hide() {
		Gdx.app.log("Game", "Hide SceneMenu");
		mFon.stop();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		Gdx.app.log("Game", "Pause SceneMenu");
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		Gdx.app.log("Game", "resume SceneMenu");
	}

	@Override
	public void dispose() {
		Gdx.app.log("Game", "dispose SceneMenu");
		textureAtlas.dispose();
		skin.dispose();
		stage.dispose();
		
		if (mFon != null)
			mFon.dispose();
	}

	

	
	
	private Button addBtnOnMenu(String sUp, String sDn ) {
		return addBtnOnMenu(sUp, sDn, "" , "", 0, 0, true );
	}
	private Button addBtnOnMenu(String sUp, String sDn, String sDs,  String sCheck ) {
		return addBtnOnMenu(sUp, sDn, sDs, sCheck, 0, 0, true);
	}
	
	private Button addBtnOnMenu(String sUp, String sDn, String sDs,  String sCheck, float px, float py,
			boolean visible) {
		ButtonStyle bs = new ButtonStyle();
		bs.up = skin.getDrawable(sUp);
		bs.down = skin.getDrawable(sDn);
		if (!sDs.equals(""))
			bs.disabled = skin.getDrawable(sDs);
		if (!sCheck.equals(""))
			bs.checked = skin.getDrawable(sCheck);
		Button btn = new Button(bs);
		btn.setVisible(visible);
		btn.setPosition(px, py);
		btnList.add(btn);
		stage.addActor(btn);
		
		
		return btn;
	}

	private Button addLevelBtn(String index, String xmlname, boolean lock) {
		
		Gdx.app.log("addLevelBtn", "index - " + index + "  xmlname - " + xmlname);
		ButtonStyle bs = new ButtonStyle();
		bs.up = skin.getDrawable(index+"_up");
		bs.down = skin.getDrawable(index+"_dn");
		bs.disabled = skin.getDrawable(index+"_na");

		MenuLevelButton mBtn = new MenuLevelButton(bs,xmlname, this, lock);
		Button btn = mBtn.getButton();
		levelButtons.add(mBtn);
		btn.setPosition(0, 0);
		btn.setVisible(true);
		btnList.add(btn);
		stage.addActor(btn);		
		return btn;
	}
	
	
	
	private void createPayFrame() {
		bgPay = new Image(textureAtlas.findRegion("bgPay"));
		bgPay.setZIndex(109);
		stage.addActor(bgPay);

		PayForm = new Image(textureAtlas.findRegion("by_form_bg"));
		PayForm.setPosition(screenWidth / 2 - PayForm.getWidth() / 2,
				screenHeight / 2 - PayForm.getHeight() / 2);
		PayForm.setZIndex(110);
		stage.addActor(PayForm);

		btnPay = new Image(textureAtlas.findRegion("btn_byup"));
		btnPay.setPosition(
				PayForm.getX() + PayForm.getWidth() / 2 - btnPay.getWidth() / 2,
				PayForm.getY() + 30);

		btnPay.setOrigin(btnPay.getWidth() / 2, btnPay.getHeight() / 2);
		btnPay.addAction(Actions.forever(Actions.sequence(
				Actions.scaleTo(1.05f, 1.05f, 0.60f, Interpolation.fade),
				Actions.scaleTo(0.98f, 0.99f, 0.40f))));
		btnPay.setZIndex(120);
		btnPay.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				if (game.payFrame != null) {
					game.payFrame.payClick();
				}
				hidePayFrame();
			}
		});
		stage.addActor(btnPay);

		btnClosePay = addBtnOnMenu("btn_close_by_up", "btn_close_by_dn");
		btnClosePay.setPosition(PayForm.getWidth() + PayForm.getX()
				- btnClosePay.getWidth() / 2,
				PayForm.getY() + PayForm.getHeight() - btnClosePay.getHeight()
						/ 2);
		btnClosePay.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				hidePayFrame();
			}
		});

		
		lablePay.setPosition(
				PayForm.getX() + PayForm.getWidth() / 2 - lablePay.getWidth()
						/ 2, PayForm.getY() + PayForm.getHeight() - 20
						- lablePay.getHeight());
		lablePay.setZIndex(180);
		hidePayFrame();
	}

	public void showPayFrame() {
		if (game.isPremium())
			return;
		bgPay.setVisible(true);
		PayForm.setVisible(true);
		btnPay.setVisible(true);
		btnClosePay.setVisible(true);
		lablePay.setVisible(true);
	}

	public void hidePayFrame() {
		bgPay.setVisible(false);
		PayForm.setVisible(false);
		btnPay.setVisible(false);
		btnClosePay.setVisible(false);
		lablePay.setVisible(false);
	}

	public void setPremium(boolean isPremium) {
		Gdx.app.log("setPrenium", "isPremium=" + isPremium);
		for (Button b : PremiumButtons) {
			b.setDisabled(!isPremium);
		}
			
		
		}

	public void BackClickDn(boolean isButtonInGame) {
		Gdx.app.log("MainMenu", "Back Click Dn");
		if (tableSettings.isVisible()) {
			if (isButtonInGame){
				BackClickDnTime = TimeUtils.millis()+1000;
				game.payFrame.showToast(game.getExitText());
				if (game.settings.isSound())
					sButton.play();
				BackClickCount=0;
			}else{
				if(BackClickCount == 0){
					if (game.settings.isSound())
						sButton.play();
					game.payFrame.showToast(game.getExitText());
				}
				if (BackClickCount>20) Gdx.app.exit();
				BackClickCount++;
			}
		} else {
			if (game.settings.isSound())
				sButton.play();
			if (bgPay.isVisible()) {
				hidePayFrame();
			} else {
				showFirstScreen(false);
			}
		}
	}

	public void BackClickUp(boolean isButtonInGame) {
		Gdx.app.log("MainMenu", "Back Click Up");
			BackClickDnTime = 0;
			BackClickCount = 0;
		}

	public void newGame(String xmlName, InputEvent event) {		
		blockButton = true;
		Screen screen = new PuzzleScene(game,xmlName);		
		if (event.getButton() > 0)
			return;
		if (game.settings.isSound())
			sButton.play();
		Gdx.app.log("BtnClick", screen.getClass().getName());
		game.setScreen(screen);
		
	}

	private void btnsTochDisable(Touchable tch, Button parent) {
		for (Button btn : btnList)
			if (parent != btn)
				btn.setTouchable(tch);
	}
	
	
	private void showFirstScreen(boolean startGame){
		float delay=0.1f;
		table11.setVisible(false);
		tableLangs.setVisible(false);
		rays.setColor(1, 1, 1, 0);
		rays.clearActions();

		rays.addAction(Actions.sequence(Actions.alpha(0, delay),
				Actions.parallel(Actions.alpha(1, 1f, Interpolation.pow2In),
						Actions.forever(Actions.rotateBy(12f, 1)))));
		gameName.setColor(1, 1, 1, 0.1f);
		gameName.setScale(1.2f);
		gameName.addAction(Actions.sequence(Actions.alpha(0, delay),
				Actions.parallel(Actions.alpha(1, 0.5f),
						Actions.scaleTo(1, 1, 0.5f, Interpolation.pow2Out)
				)));
		
		
		
		
		btnPuzzle.setColor(1, 1, 1, 0f);
		btnPuzzle.setScale(1.2f);
		btnPuzzle.setPosition(screenWidth / 2 - 402 - 40, screenHeight - 620);
		btnPuzzle.addAction(Actions.sequence(Actions.alpha(0, delay + 0.7f),
				Actions.parallel(
						Actions.alpha(1, 0.5f),
						Actions.scaleTo(1, 1, 0.5f),
						Actions.moveTo(screenWidth / 2 - 362 - 40, screenHeight - 620, 0.5f)
				)));
		
		btnStikers.setColor(1, 1, 1, 0f);
		btnStikers.setScale(1.2f);
		btnStikers.setPosition(screenWidth / 2 + 80, screenHeight - 620);
		btnStikers.addAction(Actions.sequence(Actions.alpha(0, delay + 0.7f),
				Actions.parallel(
						Actions.alpha(1, 0.5f),
						Actions.scaleTo(1, 1, 0.5f),
						Actions.moveTo(screenWidth / 2 + 40, screenHeight - 620, 0.5f)
				)));
		btnYTlink.setColor(1, 1, 1, 0f);
		btnYTlink.setScale(1.2f);
		btnYTlink.setPosition(0, screenHeight - 115);
		btnYTlink.addAction(Actions.sequence(Actions.alpha(0, delay + 0.7f),
				Actions.parallel(
						Actions.alpha(1, 0.5f),
						Actions.scaleTo(1, 1, 0.5f),
						Actions.moveTo(10, screenHeight - 115, 0.5f)
				)));
		tableSettings.setColor(1, 1, 1, 0f);
		tableSettings.addAction(Actions.sequence(Actions.alpha(0f , delay+1f),	Actions.alpha(1f, 0.3f)));
		btnLang.setColor(1, 1, 1, 0f);
		btnLang.addAction(Actions.sequence(Actions.alpha(0f , delay+1f),	Actions.alpha(1f, 0.3f)));
		arrow.setColor(1, 1, 1, 0f);
		arrow.addAction(Actions.sequence(Actions.alpha(0f , delay+1f),	Actions.alpha(1f, 0.3f)));
		
		table11.setVisible(false);
		tableSettings.setVisible(true);
		btnLang.setVisible(true);
		arrow.setVisible(true);
		btnYTlink.setVisible(true);
		btnPuzzle.setVisible(true);
		btnStikers.setVisible(true);
		gameName.setVisible(true);
		rays.setVisible(true);
		
	}
	
	
	private void showSelectLevelScreen(){
		setPremium(game.isPremium());
		tableSettings.setVisible(false);
		btnLang.setVisible(false);
		btnYTlink.setVisible(false);
		arrow.setVisible(false);
		tableLangs.setVisible(false);
		btnPuzzle.setVisible(false);
		btnStikers.setVisible(false);
		gameName.setVisible(false);
		rays.setVisible(false);
		table11.setVisible(true);		
		table11.addAction(Actions.parallel(
				Actions.sequence(Actions.moveTo(table11.getX() - 0.15f * table11.getWidth(), table11.getY() - 0.05f * table11.getHeight()), Actions.moveTo(table11.getX(), table11.getY(), 0.7f, Interpolation.pow3Out)),
				Actions.sequence(Actions.sizeTo(1.3f * table11.getWidth(), 1.1f * table11.getHeight()), Actions.sizeTo(table11.getWidth(), table11.getHeight(), 0.7f, Interpolation.pow3Out)),
				Actions.sequence(Actions.alpha(0f, 0), Actions.alpha(1f, 0.5f))
		));
	}
	
	private void checkedLang(){
	 //  0-en 1-ru 2-fr 
		btnLangEn.setChecked(game.settings.getLangID()==0);
		btnLangRu.setChecked(game.settings.getLangID()==1);				
		btnLangFr.setChecked(game.settings.getLangID()==2);
		btnLangDe.setChecked(game.settings.getLangID()==3);
	}

	public void nextLevel(String xmlName) {
		
		for (MenuLevelButton levelBtn : levelButtons) {
			if (xmlName.equals(levelBtn.getXmlName())) {
				MenuLevelButton btnNextLevel = levelButtons.get(0);
				if (levelButtons.indexOf(levelBtn)<(levelButtons.size()-1)) {
					btnNextLevel = levelButtons.get((levelButtons.indexOf(levelBtn))+1);
				}
				if ((btnNextLevel.isLock())&&(!game.isPremium())){
					game.setScreen(game.menu2d);
					showPayFrame();
				}else {
					Screen screen = new PuzzleScene(game, btnNextLevel.getXmlName());
					game.setScreen(screen);
				}
			}
			
		}
		
		
	}

	public void nextStickerLevel() {
		Screen screen = new StickerScene(game);		
		game.setScreen(screen);	
		
	}
}
