package com.starbox.puzzlecar2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MenuLevelButton{
	String xmlName;
	Menu2d parent;
	Boolean lock;

	Button btn;
	
	public MenuLevelButton(ButtonStyle bs,String xmlname, final Menu2d parent, boolean lock) {
		btn = new Button(bs);
		this.lock =  lock;
		//btn.addListener(this);
		btn.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				if (parent.blockButton)
					return;
				if ((!parent.game.isPremium())&&(btn.isDisabled())) {
					parent.showPayFrame();
				}else {
					parent.newGame(xmlName, event);
				}

			}
		});
		this.xmlName = xmlname;
		this.parent = parent;
	}


	
	public Button getButton() {
	
		return btn;
	}

	public String getXmlName() {
		return xmlName;
	}

	public boolean isLock(){
		return lock;
	}
}
