package com.starbox.puzzlecar2;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.XmlReader;

public  class Background {

	protected Image shelf;
	protected TextureAtlas textureAtlasBG;
	protected ArrayList<Image> trafficLightsList;
	protected HashMap<Image,Integer> trafficLightsMap;
	private byte trafficLightsDX=1;
	protected Image actveTrafficLights;
	protected long  timerTrafficLights;
	protected Sound trafficLightsSnd;
	int screenWidth;
	int screenHeight;
	//Stage stage;
	Group backGroup;
	Group frontGroup;
	MainClass game;
	XmlReader.Element frontE,backE ; 
	int dx, dy =0;
	
	
	
	public Background(Group group, MainClass game, String xmlname, int screenWidth,int screenHeight, boolean stikerScene ) {	
		//this.stage=stage;
		this.backGroup = group;
		this.game =game;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		trafficLightsList=null;
		trafficLightsMap = null;
		actveTrafficLights=null;
		try {		    
			XmlReader xmlReader = new XmlReader();			
		    XmlReader.Element root = xmlReader.parse(Gdx.files.internal(xmlname)); 
		    XmlReader.Element atlasnameE = root.getChildByName("atlas");		    
		   	String atlasname =atlasnameE.get("name");
		   	textureAtlasBG = new TextureAtlas(atlasname);
		   	frontE = root.getChildByName("front");
		   	backE = root.getChildByName("back");
		   	
		   	Image bg = new Image(textureAtlasBG.findRegion("bg"));
		   	backGroup.addActor(bg);				
			dx =(int) (screenWidth-game.maxWidht)/ 2;		
			dy =(int) (screenHeight -game.maxHeight)/2;	
			bg.setPosition(dx,dy);	
			
			int fz = drawBack( bg.getZIndex());			
			shelf = new Image(game.commonAtlas.findRegion("polka"));
			backGroup.addActor(shelf);
			if (stikerScene){
				shelf.setPosition(15, 31 + dy);
				createImgBar("center",280, 37+25, screenWidth-(37+25)-280, screenHeight-(37+25)*2,fz++);
				createImgBar("bar_left",243,37+25, 0, screenHeight-(37+25)*2,fz++);
				createImgBar("bar_right",screenWidth-(37+25), 37+25, 0, screenHeight-(37+25)*2,fz++);

				Image img;
				img = createImgBar("bar_bottom",280, 25 ,   screenWidth-(37+25)-280 , 0   ,fz++);
				Gdx.app.log("createImgBar1111111111","bar_bottom.img.getWidth"+img.getWidth());
				img = createImgBar("bar_top", 280, screenHeight - (37 + 25), screenWidth - (37 + 25) - 280, 0, fz++);
				Gdx.app.log("createImgBar1111111111", "bar_top.img.getWidth" + img.getWidth());



				createImgBar("corner_lt",243, screenHeight-(25+37) ,  0 , 0   ,fz++);
				createImgBar("corner_bl",243, 25 ,  0 , 0   ,fz++);
				createImgBar("corner_rt",screenWidth-(37+25), screenHeight-(25+37) ,  0 , 0   ,fz++);
				createImgBar("corner_br",screenWidth-(37+25), 25 ,  0 , 0   ,fz++);
				
			}else{
				shelf.setPosition(225+dx,31+dy);
			}
			
		} catch (IOException e) {			
			e.printStackTrace();
		}	
		
	}

	
	private Image createImgBar(String imgName, float px, float py, float pWidth, float pHeight, int zind) {
		Image img = new Image(game.commonAtlas.findRegion(imgName));

		img.setPosition(px,py);
		if (pWidth==0) pWidth = img.getWidth();
		if (pHeight==0) pHeight = img.getHeight();
		img.setSize(pWidth, pHeight);
		img.setZIndex(zind);							
		backGroup.addActor(img);
		return img;
		
	}

	public Rectangle getStikerField(){
		Rectangle rec = new Rectangle (280f, 62f, screenWidth-(37+25)-280f, screenHeight-(62f)*2);
		return  rec;
	}

	public int drawBack(int zind){	
		 Gdx.app.log("anim","drawBack");
		 return  drawElements(backE, zind,backGroup);		
	}
	
	
	public void drawFront(int zind,Group group) {	
		 this.frontGroup =group; 
		 Gdx.app.log("anim","drawFront");
		 drawElements(frontE, zind, frontGroup);	
	}
	
	private int drawElements(XmlReader.Element Elem, int zind, Group group){
		
		 XmlReader.Element img;
		 Image image;
		 int res=0;
		 for (int i=0; i<(Elem.getChildCount());i++ ){
	    	/// read xml
			img = Elem.getChild(i);
	    	String nameImg=img.get("id");
	    	int x =img.getInt("x");
	    	int y =img.getInt("y");	
	    	int w =img.getInt("w",0);
	    	int h =img.getInt("h",0);
	    	int z = img.getInt("z", 0)+zind;
	    	if (res<z)res=z;
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
				group.addActorAt(z,image);	
	    		break;
			case 1: //Sprite
				AnimationDrawable drawableActivate = null;
		    	if (img.getChildByName("activate")!=null){		    		
		    		XmlReader.Element imgAct =img.getChildByName("activate");	    		
		    		TextureRegion[] frames = game.GetAnimFrames(textureAtlasBG.findRegion(imgAct.get("name")),w, h, imgAct.getInt("anim")); //create massive
					Animation animateAct = new Animation(imgAct.getInt("speed")*0.01f, frames); // speed
					drawableActivate = new AnimationDrawable(animateAct);
		    	}	    	

		    	TextureRegion[] Frames = game.GetAnimFrames(textureAtlasBG.findRegion(nameImg),w, h, animFarame); // create massive
				Animation animate = new Animation(speedAnim*0.01f, Frames); // speed
				AnimationDrawable drawable = new AnimationDrawable(animate);
				spriteAnimate aImg = new spriteAnimate(drawable,null, drawableActivate, soundName ,waiting,group, dx+x, 800-h-y +dy,z, true, false); // / bubble
				group.addActor(aImg);	    			
		    						
				break;
			case 2: //rotate				
				image = new Image( textureAtlasBG.findRegion(nameImg));				
				image.setPosition(dx+x, 800-image.getHeight()-y +dy);
				image.setZIndex(z);
				image.setOrigin(image.getWidth()/2, image.getHeight()/2);
				image.addAction(Actions.forever(Actions.rotateBy(90, speedAnim)));
				group.addActor(image);
				break;
			case 8 : // svetofor
				if(trafficLightsMap==null) trafficLightsMap = new HashMap<Image,Integer>();
				if(trafficLightsList==null) trafficLightsList = new ArrayList<Image>();
				
				image = new Image( textureAtlasBG.findRegion(nameImg));				
				image.setPosition(dx + x, 800 - image.getHeight() - y + dy);
				image.setZIndex(z);				
				group.addActor(image);
				trafficLightsMap.put(image, waiting);
				trafficLightsList.add(image);
				actveTrafficLights = image;

				if (!soundName.equals("")){
					trafficLightsSnd =  Gdx.audio.newSound(Gdx.files.internal("mfx/"+soundName+".mp3"));
				};
				timerTrafficLights= TimeUtils.millis()+waiting;
				image.addListener(new ClickListener() {
					public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
						timerTrafficLights=TimeUtils.millis()-100;

						if ((trafficLightsSnd!=null)&(game.settings.isSound())){
							trafficLightsSnd.play(1f);
						}
						return true;
					}
				});
				break;
			}
	    } 		
		 if (actveTrafficLights!=null){
			 actveTrafficLights.setVisible(false);	
		 } 
		 return res+zind;
	}


	public void render(float delta) {
		if (actveTrafficLights!=null){			
			if (timerTrafficLights<TimeUtils.millis()){
				actveTrafficLights.setVisible(true);
				int ind = trafficLightsList.indexOf(actveTrafficLights);
				Gdx.app.log("bg","ind1 = "+ind);
				if (((ind+trafficLightsDX)==trafficLightsList.size())||((ind+trafficLightsDX)==-1)){
					trafficLightsDX*=-1;
				}
				ind+=trafficLightsDX;
				Gdx.app.log("bg","ind2 = "+ind);
				actveTrafficLights=trafficLightsList.get(ind);
				Gdx.app.log("bg","timerTrafficLights = "+trafficLightsMap.get(actveTrafficLights));
				timerTrafficLights= TimeUtils.millis()+trafficLightsMap.get(actveTrafficLights);
				actveTrafficLights.setVisible(false);
			}
		}
		
	}

}
