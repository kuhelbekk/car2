package com.starbox.puzzlecar2;

///<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/> 



import java.util.Arrays;
import java.util.Locale;

import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.DisplayMetrics;
import android.util.Log;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.starbox.puzzlecar2.MainClass;


import org.onepf.oms.OpenIabHelper;
import org.onepf.oms.appstore.googleUtils.IabException;
import org.onepf.oms.appstore.googleUtils.IabHelper;
import org.onepf.oms.appstore.googleUtils.IabResult;
import org.onepf.oms.appstore.googleUtils.Inventory;
import org.onepf.oms.appstore.googleUtils.Purchase;


public class AndroidLauncher extends AndroidApplication  {
	
	static MainClass mc;	
	// Debug tag, for logging
    static final String TAG = "PuzzleToy D";
    // Does the user have the premium upgrade?
    boolean mIsPremium = false;      
    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001; 
    // The helper object
    OpenIabHelper mHelper;
  
    private Boolean setupDone;
    
    private AndroidOutput androidOutput;
     
 // Listener that's called when we finish querying the items and subscriptions we own
    private IabHelper.QueryInventoryFinishedListener mGotInventoryListener =
            new IabHelper.QueryInventoryFinishedListener() {
                @Override
                public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                    Log.d(TAG, "Query inventory finished.");
                    if (result.isFailure()) {                    	
                    	Log.d(TAG, "result = isFailure ");  
                    	mc.ErrorinQueryInventory();   
                    	String[] skus = { Config.SKU_PREMIUM};                     	
						try {
							Inventory inv;
							inv = mHelper.queryInventory(false , Arrays.asList(skus));
							if (inv!=null){
	                    		Purchase premiumPurchase = inv.getPurchase(Config.SKU_PREMIUM);
	                    		mIsPremium = premiumPurchase != null;
	                    		mc.setPremium(mIsPremium);
	                    	}
						} catch (IabException e) {							
							Log.d(TAG,"Error2 "+ e.getMessage()); 
						}                    	
                    	//ошибка  при  получении данных                    	
                        return;
                    }
                    Log.d(TAG, "Query inventory was successful.");        
                    // Do we have the premium upgrade?
                    Purchase premiumPurchase = inventory.getPurchase(Config.SKU_PREMIUM);
                    mIsPremium = premiumPurchase != null;
                    Log.d(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));
                    mc.setPremium(mIsPremium);     //1               
                    Log.d(TAG, "Initial inventory query finished; enabling main UI.");
                }
            };


    
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState); 
        androidOutput = new AndroidOutput(this,this);        
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
      //  getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);                
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useImmersiveMode = true  ;
        cfg.hideStatusBar = true;
      // cfg.touchSleepTime = 2*60*1000;
        cfg.useGLSurfaceView20API18= true;
        cfg.useWakelock = true;                     
        mc = new MainClass(androidOutput);
        initialize(mc, cfg);       
        mc.ErrorinQueryInventory();  
       // mc.setPremium(true); 
        try{
        	 OpenIabHelper.Options.Builder builder = new OpenIabHelper.Options.Builder()
             .setVerifyMode(OpenIabHelper.Options.VERIFY_EVERYTHING)
             .addStoreKeys(Config.STORE_KEYS_MAP);
        	 mHelper = new OpenIabHelper(this, builder.build());     
        	 Log.d(TAG, "Starting setup.");
        	 mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
        		 public void onIabSetupFinished(IabResult result) {
        			 Log.d(TAG, "Setup finished.");
        			 if (!result.isSuccess()) {
        				 // Oh noes, there was a problem.
        				 setupDone = false;
        				 complain("Problem setting up in-app billing: " + result);
        				 return;
        			 }
             // Hooray, IAB is fully set up. Now, let's get an inventory of stuff we own.
        			 Log.d(TAG, "Setup successful. Querying inventory.");
        			 setupDone = true;
        			 String[] skus = { Config.SKU_PREMIUM};        				 
        			 mHelper.queryInventoryAsync(true,  Arrays.asList(skus), mGotInventoryListener);
        		 }
        	 });       	
        	
        }catch (Exception e) { 
        	Log.e(TAG, "billing Error." + e.getMessage());
            mHelper=null;
        }         
           
    }
	
	
	 // User clicked the "Upgrade to Premium" button.
    public void onUpgradeClicked() {
        Log.d(TAG, "Upgrade button clicked; launching purchase flow for upgrade.");    
        String payload = ""; 
        
        
        if (setupDone == null) {
            complain("Billing Setup is not completed yet");
            return;
        }
        if (!setupDone) {
            complain("Billing Setup failed");
            return;
        }
       
        
        if(mHelper!=null)
        	mHelper.launchPurchaseFlow(this, Config.SKU_PREMIUM, RC_REQUEST,
                mPurchaseFinishedListener, payload);       	
        
    }
	
	
   
	



	  @Override
	    public void startActivityForResult(Intent intent, int requestCode) {
	        Log.d(TAG, "startActivityForResult() intent: " + intent + " requestCode: " + requestCode);
	        super.startActivityForResult(intent, requestCode);
	    }

	

	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;
        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }   
    }
	
	

	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("onDestroy","onDestroy Start");
		if (mHelper != null) {
            mHelper.dispose();           
        }		
		mHelper = null;
	}
	  

	// Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);
            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
            	
                complain("Error purchasing: " + result);
                if (result.getResponse()==7){
                	 mIsPremium = true;
                     mc.setPremium(mIsPremium);
                }               
                return;
            }            
            Log.d(TAG, "Purchase successful.");
            if (purchase.getSku().equals(Config.SKU_PREMIUM)) {                
                Log.d(TAG, "Purchase is premium upgrade. Congratulating user.");                
                alert();
                mIsPremium = true;
                mc.setPremium(mIsPremium);	//2              
            }
        }

		
    };
    

    
    void complain(String message) {
        Log.e(TAG, "*PToy Err: " + message);
    }

	protected void alert() {		
	    AlertDialog.Builder bld = new AlertDialog.Builder(this);
	    String button1String, button2String;
	    
	    if (Locale.getDefault().getDisplayLanguage().equals("русский")){
	    	bld.setTitle("—пасибо за покупку полной версии!"); 
	        bld.setMessage("ќцените пожалуйста эту игру");
	        button1String="ќценить";
	        button2String="ќтмена";
	    }else{	    	
	    	bld.setTitle("Thank you for upgrading to premium!"); 
	        bld.setMessage("Please, rate this game");
	        button1String="Rate";
	        button2String="Cancel";
	    	}	    
	    
	    bld.setNegativeButton(button2String, new OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                
            }
        }); 
        bld.setPositiveButton(button1String, new OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
            	rateClick();  
            }
        });
        
        bld.setCancelable(true);
        bld.create().show(); 
	}


	public void rateClick() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("market://details?id=com.starbox.puzzletoy"));
		startActivity(intent);	

	}

	public String getAId() {
		return Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);		
	}

	
	public int getAccuracy() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);	
		int widthPixels = dm.widthPixels;
		int heightPixels = dm.heightPixels;		
		float widthDpi = dm.xdpi;
		float heightDpi = dm.ydpi;
		float widthInches = widthPixels / widthDpi;
		float heightInches = heightPixels / heightDpi;
		double diagonalInches = Math.sqrt(
			    (widthInches * widthInches) 
			    + (heightInches * heightInches));
		Log.v("diagonalInches", "diagonalInches = " +diagonalInches);
		if (diagonalInches<5) return 25;
		if (diagonalInches<6) return 10;
		if (diagonalInches<8) return 5;
		return 0;
	}
}