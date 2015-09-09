package com.starbox.puzzlecar2;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MainClass extends Game {

	public Menu2d menu2d;
	public TextureAtlas commonAtlas;
	public Settings settings;
	public Skin commonSkin;
	private static boolean premium = false;
	private static boolean StartErrorinQueryInventory = false;
	public int accuracy = 50;
	final int maxHeight =800;
	final int maxWidht =1450;

	public PayCar2 payFrame;

	public MainClass(PayCar2 payFrame) {
		// TODO Auto-generated constructor stub
		this.payFrame = payFrame;
	}

	@Override
	public void create() {
		Gdx.app.log("PuzzleToy", "10");
		settings = new Settings();
		settings.loadSettings();
		commonAtlas = new TextureAtlas("data/common.atlas");
		commonSkin = new Skin(commonAtlas);		
		menu2d = new Menu2d(this);
		setScreen(menu2d);		
		Gdx.input.setCatchBackKey(true);
		accuracy = payFrame.getAccuracy()+50;		
		if (premium) {
			settings.setPremium(payFrame.getAId());
		} else {
			if (StartErrorinQueryInventory)
				ErrorinQueryInventory();
		}

	}

	
	public TextureRegion[] GetAnimFrames(TextureRegion tr, int width, int height) {	
		return GetAnimFrames( tr,  width,  height, 0);		
	}
	// /// common functions
	public TextureRegion[] GetAnimFrames(TextureRegion tr, int width, int height, int count) {		
		
		 int wdth = width;
		 int hght = height;
		 
		if (tr.getRegionWidth() < wdth) wdth = tr.getRegionWidth() ;
		if (tr.getRegionHeight() < hght) hght = tr.getRegionHeight() ;
		int w = tr.getRegionWidth() / wdth;
		int h = tr.getRegionHeight() / hght;
		
		int k = 0;
		if (count==0) count=w*h;
		TextureRegion[] Frames = new TextureRegion[count];
		for (int j = 0; j < (h); j++)
			for (int i = 0; i < (w); i++) {
				if (k==count) break;
				Frames[k] = new TextureRegion(tr.getTexture(), tr.getRegionX()	+ i * wdth, tr.getRegionY() + j * hght, wdth,hght);
				++k;		
			}
		return Frames;
	}

	public boolean isPremium() {
		return premium;
	}

	public void setPremium(boolean pr) {		
		premium = pr;
		if (menu2d != null)
			menu2d.setPremium(pr);		
		if ((settings != null) & (pr))
			if (!settings.isPremium(payFrame.getAId()))
				settings.setPremium(payFrame.getAId());
	}

	public void ErrorinQueryInventory() {
		if (settings == null) {
			StartErrorinQueryInventory = true;
			return;
		}
		if (settings.isPremium(payFrame.getAId())) {
			Gdx.app.log("PuzzleCar", "Premium be lasted");
			setPremium(true);
		}
	}

	public String getLangStr() {
		switch (settings.getLangID()) {
		case 0:
			return "_en";
		case 1:
			return "_ru";
		case 2:
			return "_fr";
		case 3:
			return "_de";	
		default:
			return "_en";
		} 
	}

	public String getExitText() {
		switch (settings.getLangID()) {
		case 0:
			return "To exit, press twice.";
		case 1:
			return "Для выхода, нажмите дважды";
		case 2:
			return "Pour quitter, appuyez deux fois.";
		case 3:
			return "Zum Beenden drücken Sie zweimal.";	
		default:
			return "To exit, press twice.";
		} 
	}

	public void nextLevel(String xmlName) {
		menu2d.nextLevel(xmlName);
		//setScreen(game.menu2d)
		
	}

	public void nextStikerLevel() {
		menu2d.nextStickerLevel();
		
	}

}
