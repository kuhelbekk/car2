package com.starbox.puzzlecar2;

import java.util.Locale;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class Settings {

	private boolean sound = true;
	private boolean music = true;
	private boolean voice = true;

	private int langId = 0; //  0-en 1-ru 2-fr 3-de
	private String premium = "";

	private static final String key = "C5M4#@KDE90FM*%";

	public boolean isSound() {
		return sound;
	}

	public void setSound(boolean sound) {
		this.sound = sound;
		saveSettings();
	}

	public boolean isVoice() {
		return voice;
	}

	public void setVoice(boolean voice) {
		this.voice = voice;
		saveSettings();
	}	

	public boolean isMusic() {
		return music;
	}

	public void setMusic(boolean music) {
		this.music = music;
		saveSettings();
	}
//  0-en 1-ru 2-fr
	public int getLangID() {
		if (langId>1)return 0; //костыль
		return langId;
	}

	public void setLangID(int langID) {
		this.langId = langID;
		saveSettings();
	}

	public void saveSettings() {
		Preferences prefs = Gdx.app.getPreferences("ToyPrefs");
		prefs.putBoolean("isVoice", isVoice());
		prefs.putBoolean("isMusic", isMusic());
		prefs.putInteger("LangID", getLangID());
		prefs.putBoolean("isSound", isSound());
		prefs.putString("ID", premium);
		prefs.flush();
	}

	public void loadSettings() {
		Preferences prefs = Gdx.app.getPreferences("ToyPrefs");
		this.sound = prefs.getBoolean("isSound", true);
		this.music = prefs.getBoolean("isMusic", true);		
		this.voice = prefs.getBoolean("isVoice", true);		
		int defLang=0; // установка языка по настройкам андройда
		
		Gdx.app.log("defLang", "defLang = "+Locale.getDefault().getDisplayLanguage());
		if (Locale.getDefault().getDisplayLanguage().equals("русский")) defLang=1;
		if (Locale.getDefault().getDisplayLanguage().equals("français")) defLang=2;	
		//if (Locale.getDefault().getDisplayLanguage().equals("Deutsch")) defLang=3;
	    this.langId = prefs.getInteger("LangID", defLang);		
		premium = prefs.getString("ID", "");
	}

	public boolean isPremium(String id) {
		boolean r = false;
		if (id.length() > 5) {
			if (premium.length() > 5) {
				r = premium.equals(encode(id, key));
			}
		}
		return r;
	}

	public void setPremium(String id) {
		if (id.length() > 5) {
			premium = encode(id, key);
			saveSettings();
		}
	}

	public static String encode(String pText, String pKey) {
		byte[] txt = pText.getBytes();
		byte[] key = pKey.getBytes();
		byte[] res = new byte[pText.length()];
		for (int i = 0; i < txt.length; i++) {
			res[i] = (byte) (txt[i] ^ key[i % key.length]);
		}
		return new String(res);
	}

}
