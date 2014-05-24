// This string is autogenerated by ChangeAppSettings.sh, do not change spaces amount
package xtvapps.retrobox.atari800;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;

import com.tvi910.android.core.AccelerometerJoystick;
import com.tvi910.android.core.ButtonPanelController;
import com.tvi910.android.core.Keymap;
import com.tvi910.android.core.NullController;
import com.tvi910.android.core.TouchpadJoystick;
import com.tvi910.android.core.VirtualControllerManager;
import com.tvi910.android.core.buttonpanel.ButtonCallback;
import com.tvi910.android.core.buttonpanel.ButtonPanel;
import com.tvi910.android.core.buttonpanel.KeyboardOverlay;
import com.tvi910.android.sdl.AudioThread;
import com.tvi910.android.sdl.LoadLibrary;
import com.tvi910.android.sdl.SDLInterface;
import com.tvi910.android.sdl.SDLKeysym;
import com.tvi910.android.sdl.SDLSurfaceView;

public class MainActivity extends Activity {

    private static MainActivity _instance = null;
    public static Context ctx;
    public static final String TAG = "com.droid800.emulator";
    
	private static String keyNames[] = { 
		"UP", "DOWN", "LEFT", "RIGHT", 
		"BTN_A", "BTN_B", "BTN_X", "BTN_Y", 
		"TL", "TR", "TL2", "TR2",
		"TL3", "TR3", "SELECT", "START"
	};
	
	private static String keyNamesAtari[] = {
		"UP", "DOWN", "LEFT", "RIGHT", 
		"UP", "TRIGGER", "SPACE", "TRIGGER",
		"OPTION", "HELP", "RESET", "QUIT",
		"LOAD_STATE", "SAVE_STATE", "SELECT", "START"
	};
	

    private static class ButtonInfo {
        String name;
        int colspan;
        ButtonCallback callback;

        ButtonInfo(String name, ButtonCallback callback, int colspan) {
            this.name = name;
            this.colspan = colspan;
            this.callback = callback;
        }
    }

    public static class QuitEmulatorCallback implements ButtonCallback {
        QuitEmulatorCallback() {
        }
        public void onButtonUp() {
            MainActivity.getInstance().onQuitEmulator();
        }   
    }

    public static class NormalCallback implements ButtonCallback {
        public int _keyCode;
        public boolean _closePanel;
        NormalCallback(int keyCode, boolean closePanel) {
            _keyCode = keyCode;
            _closePanel = closePanel;
        }
        public void onButtonUp() {
            SDLInterface.nativeKeyCycle(_keyCode);
            if (_closePanel) {
                MainActivity.getInstance().hideControlPanel();
            }
        }   
    }

    public static class DelayCallback implements ButtonCallback {
        public int _keyCode;
        public boolean _closePanel;
        DelayCallback(int keyCode, boolean closePanel) {
            _keyCode = keyCode;
            _closePanel = closePanel;
        }
        public void onButtonUp() {
            SDLInterface.nativeKeyCycle(_keyCode, 200);
            if (_closePanel) {
                MainActivity.getInstance().hideControlPanel();
            }
        }   
    }

    /**
     * list of ButtonPanel button attributes and toggles (if there is one) 
     */
    private static final ButtonInfo[] panelButtons = {
        new ButtonInfo("Reset", new DelayCallback(SDLKeysym.SDLK_F5,false), 1),
        null,
        new ButtonInfo("Option", new DelayCallback(SDLKeysym.SDLK_F2,false), 1),
        null,
        new ButtonInfo("Select", new DelayCallback(SDLKeysym.SDLK_F3,false), 1),
        null,
        new ButtonInfo("Start", new DelayCallback(SDLKeysym.SDLK_F4,true), 1),
        null,
        new ButtonInfo("Quit", new QuitEmulatorCallback(), 1),
        null,
        new ButtonInfo("F1", new NormalCallback(SDLKeysym.SDLK_F1,true), 1),
        null,
        new ButtonInfo("Escape", new NormalCallback(SDLKeysym.SDLK_ESCAPE,true), 1),
        null,
        new ButtonInfo("Break", new NormalCallback(SDLKeysym.SDLK_F7,true), 1),
        null,
        null,
        null
    };

    public static MainActivity getInstance() {
        return _instance;
    }
    
    private void copyAssets() {
        AssetManager assetManager = getAssets();
        Log.d(TAG, "Intentando extraer Atari XL bios");
        String[] files = null;
        try {
            files = assetManager.list("bios");
        } catch (IOException e) {
            Log.e(TAG, "Failed to get asset file list.", e);
        }
        for(String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
              in = assetManager.open("bios/" + filename);
              File outDir = new File( ctx.getApplicationInfo().dataDir + "/bios");
              outDir.mkdir();
              File outFile = new File( outDir,  filename);
              Log.d(TAG, "bios extraida a " + outFile.getAbsolutePath());
              out = new FileOutputStream(outFile);
              copyFile(in, out);
              in.close();
              in = null;
              out.flush();
              out.close();
              out = null;
            } catch(IOException e) {
                Log.e(TAG, "Failed to copy asset file: " + filename, e);
            }       
        }
    }
    
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
          out.write(buffer, 0, read);
        }
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
				
		ctx = this;
        _instance = this;

        _lowerMode = false;

        AtariKeys.init();
		super.onCreate(savedInstanceState);
		
        
        Intent intent = getIntent();
        String romFile = intent.getStringExtra("game");
        String osRom = intent.getStringExtra("osrom");
        String stateDir = intent.getStringExtra("stateDir");
        String stateName = intent.getStringExtra("stateName");
        String sGamepad = intent.getStringExtra("gamepad");
        boolean useGamepad = sGamepad!=null && !sGamepad.equals("NONE");
        
        if (stateDir!=null) new File(stateDir).mkdirs();
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		if (romFile == null) {
	        copyAssets();
		    romFile = "/sdcard/bcquest.atr";
		    osRom = this.getApplicationInfo().dataDir + "/bios/ATARIXL.ROM";
		}
	    String romDirectory = "/sdcard"; 
	    SharedPreferences.Editor editor = preferences.edit();
	    editor.putString("osrom", osRom);
	    editor.putString("romdirectory", romDirectory);
	    editor.putString("romfile", romFile);
	    editor.putString("stateDir", stateDir);
	    editor.putString("stateName", stateName);
	    editor.commit();
	    
		// fullscreen mode
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				   WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // lock orientation 
        boolean landscapeMode =
            PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).getBoolean("landscape", true);
        if (landscapeMode) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        _keymap = Keymap.getInstance();

        if (_keymap.getNumberOfMappedKeys() == 0) {
        	Log.d(TAG, "Using AtariKeys");
            _keymap.reload(PreferenceManager.getDefaultSharedPreferences(this), AtariKeys.getInstance());
            if (useGamepad) loadGamepadFromIntent();
        } else {
        	Log.d(TAG, "NOT Using AtariKeys");
        }

        SDLInterface.setLeftKeycode(SDLKeysym.SDLK_LEFT) ;
        SDLInterface.setRightKeycode(SDLKeysym.SDLK_RIGHT) ;
        SDLInterface.setUpKeycode(SDLKeysym.SDLK_UP);
        SDLInterface.setDownKeycode(SDLKeysym.SDLK_DOWN) ;
        SDLInterface.setTriggerKeycode(SDLKeysym.SDLK_KP_PERIOD) ;

        if (landscapeMode) {
            _buttonPanel = new ButtonPanel(
                this, // context
                null, /*Typeface.createFromAsset(getAssets(), "fonts/ATARCC__.TTF"),*/ // custom font
                4, // number of grid columns
                2, // number of grid rows
                90, // percent of desired width fill
                50, // percent of desired height fill
                50, // x offset (0 = center)
                10, // y offset (0 = center)
                3f, // aspect ratio (1=square)
                2); 
            _buttonPanel.setPadding(.50f);

            for (int col=0; col<4; col++) {
                for (int row=0; row<2; row++) {
                    final int fcol = col;
                    final int frow = row;
                    ButtonInfo bi = panelButtons[((4*row)+col)*2];
                    ButtonInfo tog = panelButtons[(((4*row)+col)*2)+1];
                    if (null != bi) {
                        _buttonPanel.setButton(fcol,frow,
                            Color.argb(192, 38, 38, 38), 
                            Color.argb(192, 228, 228, 228), 
                            bi.name, 
                            bi.callback,
                            bi.colspan);
                    }
                    if (null != tog) {
                        _buttonPanel.setToggle(fcol,frow,
                            Color.argb(192, 38, 38, 38), 
                            Color.argb(192, 228, 228, 228), 
                            tog.name, 
                            tog.callback);
                    }
                }
            }
        }
        else {
            _buttonPanel = new ButtonPanel(
                this, // context
                null, /*Typeface.createFromAsset(getAssets(), "fonts/ATARCC__.TTF"),*/ // custom font
                3, // number of grid columns
                3, // number of grid rows
                95, // percent of desired width fill
                35, // percent of desired height fill
                50, // x offset (0 = center)
                60, // y offset (0 = center)
                2.5f, // aspect ratio (1=square)
                2); 

            _buttonPanel.setPadding(.85f);

            for (int col=0; col<3; col++) {
                for (int row=0; row<3; row++) {
                    final int fcol = col;
                    final int frow = row;
                    ButtonInfo bi = panelButtons[((3*row)+col)*2];
                    ButtonInfo tog = panelButtons[(((3*row)+col)*2)+1];
                    if (null != bi) {
                        _buttonPanel.setButton(fcol,frow,
                            Color.argb(192, 38, 38, 38), 
                            Color.argb(192, 228, 228, 228), 
                            bi.name, 
                            bi.callback,
                            bi.colspan);
                    }
                    if (null != tog) {
                        _buttonPanel.setToggle(fcol,frow,
                            Color.argb(192, 38, 38, 38), 
                            Color.argb(192, 228, 228, 228), 
                            tog.name, 
                            tog.callback);
                    }
                }
            }
        }
        _buttonPanel.setPanelButtonCallback(buttonPanelCallback);

		mLoadLibraryStub = new LoadLibrary();
		mAudioThread = new AudioThread(this);

        initSDL(landscapeMode, useGamepad);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}

	private void loadGamepadFromIntent() {
		for(int i=0; i<keyNames.length; i++) {
			int keyCode = getIntent().getIntExtra("j1" + keyNames[i], 0);
			if (keyCode>0) {
				Integer atariKeyCode = AtariKeys.getInstance().getCode(keyNamesAtari[i]);
				if (atariKeyCode == null) {
					Log.d("REMAP", "Atari key not found: " + keyNamesAtari[i]);
				} else {
					_keymap.setMap(keyCode, atariKeyCode);
				}
			}
		}
	}

	public void initSDL(boolean landscapeMode, boolean useGamepad)
	{
		if(sdlInited)
			return;
		sdlInited = true;

        Display display = getWindowManager().getDefaultDisplay();

        AbsoluteLayout al = new AbsoluteLayout(this);
        setContentView(al);


        // set up the sdl command line args.
        String systemType = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).getString("systemType", "800XL");
        String gameRom = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).getString("romfile","");
        Log.d("com.droid800.emulator", "Archivo a cargar " + gameRom);
        
        if (!systemType.equals("5200")) {
            gameRom = Cartridge.getInstance().prepareCartridge(gameRom);
        }
        String osRom =  PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).getString("osrom","");
        String stateDir =  PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).getString("stateDir",null);
        String stateName =  PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).getString("stateName",null);
        String refreshRate = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).getString("skipFrame", "0");
        boolean showSpeed = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).getBoolean("showspeed", true);
        boolean enableSound = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).getBoolean("sound", true);
        boolean showBorder = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).getBoolean("showBorder", true);
        String sampleRate = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).getString("sampleRate", "44100");
        boolean stretchToFit = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).getBoolean("stretchtofit", true);
        boolean ntscMode = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).getString("videoSystem", "NTSC").equals("NTSC");
        String leftController = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).getString("leftControllerId", "Virtual Joystick");
        setupVirtualControllers(landscapeMode);

        ArrayList<String> arglist = new ArrayList<String>();
        arglist.add("atari800");
        if (showSpeed) {
            arglist.add("-showspeed");
        }

        int refreshRateInt = Integer.parseInt(refreshRate);
        if (refreshRateInt > 0) {
            arglist.add("-refresh");
            arglist.add("" + refreshRateInt);
        }

        if (!stretchToFit) arglist.add("-keepaspectratio");
        if (showBorder) arglist.add("-showborder");

        if (!enableSound) {
            arglist.add("-nosound");
        }
        arglist.add("-dsprate");
        arglist.add(sampleRate);
        arglist.add("-audio16");
      
        if (ntscMode) {
            arglist.add("-ntsc");
        }

        // if there is no osRom set - then we launch in "expert" mode.
        // the use the configuration system built into the Atari800
        // emulator
        if (osRom.equals("")) {
            // set the default config file location
            arglist.add("-config");
            arglist.add(android.os.Environment.getDataDirectory() + "/data/com.droid800/atari800.cfg");
        }
        else {
            if (systemType.equals("5200")) {
                arglist.add("-5200");
                arglist.add("-5200_rom");
                arglist.add(osRom);
            }
            else if (systemType.equals("800 (REV A)")) {
                arglist.add("-atari");
                arglist.add("-osa_rom");
                arglist.add(osRom);
            }
            else if (systemType.equals("800 (REV B)")) {
                arglist.add("-atari");
                arglist.add("-osb_rom");
                arglist.add(osRom);
            }
            else {
                if (systemType.equals("800XL")) {
                    arglist.add("-xl");
                }
                else if (systemType.equals("130XE")) {
                    arglist.add("-xe");
                }
                else if (systemType.equals("320XE")) {
                    arglist.add("-320xe");
                }
                else if (systemType.equals("RAMBO")) {
                    arglist.add("-RAMBO");
                }
                arglist.add("-xlxe_rom");
                arglist.add(osRom);
            }

            arglist.add("-basic_rom"); // this is required or the emaulator will
                                       // try and load teh basic rom too
            arglist.add("none");

        }
        
        if (stateDir!=null && stateName!=null) {
        	arglist.add("-state_dir");
        	arglist.add(stateDir);
        	arglist.add("-state_name");
        	arglist.add(stateName);
        }

        if (!gameRom.equals("")) {
            arglist.add(gameRom);
        }

        Log.d(TAG, "args" + arglist);
        
        mGLView = new SDLSurfaceView(this, arglist);
        al.addView(mGLView);
        
        if (!useGamepad)  _touchpadJoystick.addToAbsoluteLayout(al, display);

        _buttonPanel.addToLayout((ViewGroup)al);
        _keyboardOverlay.getButtonPanel().addToLayout((ViewGroup)al);
//        _buttonPanel.showPanel();

		// Receive keyboard events
		mGLView.setFocusableInTouchMode(true);
		mGLView.setFocusable(true);
		mGLView.requestFocus();
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "Droid800 - do not dim screen");

        _virtualControllerManager.setActiveController(leftController);
	}

    private void setupVirtualControllers(boolean landscapeMode) {
        _virtualControllerManager = new VirtualControllerManager();

        _accelerometerJoystick = AccelerometerJoystick.getInstance(this);
//        _touchPaddle = new TouchPaddle(this,getWindowManager().getDefaultDisplay().getHeight());
        setupTouchpadJoystick();
        _buttonPanelController = new ButtonPanelController(this, _buttonPanel);
        _nullController = new NullController(this);


        int keyboardAlpha = PreferenceManager.getDefaultSharedPreferences(
            this.getApplicationContext()).getInt("keyboardAlpha", 192);
        _keyboardOverlay = new KeyboardOverlay(this, landscapeMode, keyboardAlpha);
        _keyboardOverlay.getButtonPanel().setPanelButtonCallback(keyboardSliderCallback);


        _virtualControllerManager.add("Tilt Joystick", _accelerometerJoystick);
        _virtualControllerManager.add("Control Panel", _buttonPanelController);
        _virtualControllerManager.add("Keymap", _nullController);
        _virtualControllerManager.add("Virtual Keyboard", _keyboardOverlay);
        _virtualControllerManager.add("Virtual Joystick", _touchpadJoystick);
    }

    private void setupTouchpadJoystick() {

        Display display = getWindowManager().getDefaultDisplay();

        int layout = Integer.parseInt(
            PreferenceManager.getDefaultSharedPreferences(
                this.getApplicationContext()).getString("controlslayout","0"));
        boolean layoutOnTop = false;
        boolean buttonsOnLeft = true;

        switch (layout) {
            case 0:
                layoutOnTop = false;
                buttonsOnLeft = false;
                break;
            case 1:
                layoutOnTop = false;
                buttonsOnLeft = true;
                break;
            case 2:
                layoutOnTop = true;
                buttonsOnLeft = false;
                break;
            case 3:
                layoutOnTop = true;
                buttonsOnLeft = true;
                break;
            default :
                break;
        }

        int controlsSize = Integer.parseInt(
            PreferenceManager.getDefaultSharedPreferences(
                this.getApplicationContext()).getString("touchpadJoystickSize","1"));

        int sz = TouchpadJoystick.MEDIUM;
        switch (controlsSize) {
            case 0 :
                sz = TouchpadJoystick.SMALLEST;
                break;
            case 1 :
                sz = TouchpadJoystick.SMALL;
                break;
            case 2 :
                sz = TouchpadJoystick.MEDIUM;
                break;
            case 3 :
                sz = TouchpadJoystick.LARGE;
                break;
            default :
                sz = TouchpadJoystick.MEDIUM;
                break;
        }


        _touchpadJoystick  = new TouchpadJoystick(
            this, layoutOnTop, buttonsOnLeft,
            display.getWidth(), display.getHeight(), sz);
    }

	@Override
	protected void onPause() {
        Log.v("com.droid800.MainActivity", "Paused");
		if( wakeLock != null ) {
			wakeLock.release();
        }
		super.onPause();
		if( mGLView != null ) {
			mGLView.onPause();
        }
	}

	@Override
	protected void onResume() {
        Log.v("com.droid800.MainActivity", "Resumed");
		if( wakeLock != null ) {
			wakeLock.acquire();
        }
		super.onResume();
		if( mGLView != null ) {
			mGLView.onResume();
        }
	}

	@Override
	protected void onStop()
	{
        Log.v("com.droid800.MainActivity", "Stopped");

		if( mGLView != null )
			mGLView.exitApp();
		super.onStop();
		finish();
	}
	
	private void sendLoadState(boolean down) {
		SDLInterface.nativeKey(SDLKeysym.SDLK_LALT, down?1:0);
		SDLInterface.nativeKey(SDLKeysym.SDLK_l, down?1:0);
	}

	private void sendSaveState(boolean down) {
		SDLInterface.nativeKey(SDLKeysym.SDLK_LALT, down?1:0);
		SDLInterface.nativeKey(SDLKeysym.SDLK_s, down?1:0);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, final KeyEvent event) {
Log.v("com.droid800.MainActivity", "DOWN keyCode: " + keyCode + ", getUnicodeCHar=" + event.getUnicodeChar());

         final int nativeCode = _keymap.translate(keyCode);
         
         if (nativeCode == SDLKeysym.SDLK_F14) {
        	 sendLoadState(true);
        	 return true;
         }
         if (nativeCode == SDLKeysym.SDLK_F15) {
        	 sendSaveState(true);
        	 return true;
         }
         
         if (nativeCode > 0) {
        	 Log.v("com.droid800.MainActivity", "Send native code " + nativeCode);
             SDLInterface.nativeKey(nativeCode, 1);
             return true;
         }
         else if (keyCode == 4 /*|| keyCode == 84*/) {
            return true;
         }
         else if (keyCode == 67) {
             SDLInterface.nativeKey(8, 1);
             return true;
         }
         else {
        	 Log.v("com.droid800.MainActivity", "autotranslate");
             int charCode = event.getUnicodeChar();
             if (charCode > 0) {
                 if (!_lowerMode) {
                     // clumsy work-around for the weird capslock situation in 
                     // the emulator. This is essentially pressing the "Lowr" 
                     // button on older ataris or toggling caps mode in the newer 
                     // ones.
                     _lowerMode = true;
                     SDLInterface.nativeKeyCycle(SDLKeysym.SDLK_CAPSLOCK);
                     SDLInterface.nativeKeyCycle(SDLKeysym.SDLK_CAPSLOCK);
                 }

                 // flip lower and upper
                 if (charCode >= 65) {
                     if (charCode <= 90) {
                         charCode = charCode + 32;
                     }
                     else if (charCode >= 97 && charCode <= 122) {
                         charCode = charCode - 32;
                     }
                 }

                 switch (charCode) {
                     case (10) : 
                         SDLInterface.nativeKey(13, 1); // sdk return key
                         _lastCharDown = 13;
                         return true;
                     default : 
                         SDLInterface.nativeKey(charCode, 1);
                         _lastCharDown = charCode;
                         return true;
                 }
             }
             else {
                 return false;
             }
         }
	 }

	@Override
	public boolean onKeyUp(int keyCode, final KeyEvent event) {
Log.v("com.droid800.MainActivity", "UP keyCode: " + keyCode + ", getUnicodeCHar=" + event.getUnicodeChar());
         final int nativeCode = _keymap.translate(keyCode);
         
         if (nativeCode == SDLKeysym.SDLK_F14) {
        	 sendLoadState(false);
        	 return true;
         }
         if (nativeCode == SDLKeysym.SDLK_F15) {
        	 sendSaveState(false);
        	 return true;
         }

         if (nativeCode > 0) {
             SDLInterface.nativeKey(nativeCode, 0);
             return true;
         }
         else if (keyCode == 4) {
            if (_keyboardOverlay.getButtonPanel().isVisible()) {
                hideKeyboard();
                return true;
            }
            else {
                if (_buttonPanel.isVisible()) {
                    hideControlPanel();
                    return true;
                }
            }

            onQuitEmulator();
            return true;
         }
         else if (keyCode == 67) {
             SDLInterface.nativeKey(8, 0);
             return true;
         }
         else {
             final int charCode = event.getUnicodeChar();
             if (charCode > 0) {
                 SDLInterface.nativeKey(_lastCharDown, 0);
                 return true;
             }
             else {
                 return false;
             }
         }
//         else if (keyCode == 84) {
//            if (_keyboardOverlay.getButtonPanel().isVisible()) {
//                hideKeyboard();
//                return true;
//            }
//            else {
//                _virtualControllerManager.setActiveController("Virtual Keyboard");
//            }
//            return true;
//         }
	 }

    private void sleep() {
        try {
            Thread.sleep(1);
        }
        catch (Throwable e) {
        }
    }

	@Override
	public boolean dispatchTouchEvent(final MotionEvent ev) {
        if (!super.dispatchTouchEvent(ev)) {
		    if(_touchpadJoystick.getIsActive()) {
			    boolean ret = _touchpadJoystick.onTouchEvent(ev);
                // if we don't sleep here we get way to many motion events.
                sleep();
                return ret;
            }
            else if (_accelerometerJoystick.getIsActive()) {
                final int action = ev.getAction();
                switch (action & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN: {
                        SDLInterface.triggerOn();
                        sleep();
                        return true;
                    }
                    case MotionEvent.ACTION_UP: {   
                        SDLInterface.triggerOff(); 
                        sleep();
                        return true;
                    }
                    default : 
                        return false;
                }       
            }
            else {
                return false;
            }
        }
        else {
            return true;
        }
	}

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * A call back for when the user presses the start button
     */
    ButtonCallback keyboardSliderCallback = new ButtonCallback() {
        public void onButtonUp() {
            if (_keyboardOverlay.getButtonPanel().isVisible()) {
                hideKeyboard();
            }
            else {
                _virtualControllerManager.setActiveController("Virtual Keyboard");
            }
        }
    };

    /**
     * A call back for when the user presses the start button
     */
    ButtonCallback buttonPanelCallback = new ButtonCallback() {
        public void onButtonUp() {
            if (_buttonPanel.isVisible()) {
                hideControlPanel();
            }
            else {
                _virtualControllerManager.setActiveController("Control Panel");
            }
        }
    };

    private void hideControlPanel() {
        _virtualControllerManager.activateLastController();
    }

    private void hideKeyboard() {
        _virtualControllerManager.activateLastController();
    }

    private void onQuitEmulator() {

//        // TODO pause the emulator
//        SDLInterface.nativeKeyCycle(SDLKeysym.SDLK_PAUSE);

        new AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(R.string.quit)
            .setMessage(R.string.really_quit)
            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Stop the activity
                    MainActivity.this.finish();  
                    SDLInterface.nativeQuit();  
                }

             })
            .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
// TODO unpause the emulator
//                    SDLInterface.nativeKeyCycle(SDLKeysym.SDLK_PAUSE);
                }

             })
            .show();
    }

    /**
     * Called when your activity's options menu needs to be created.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the currently selected menu XML resource.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.emulator_menu, menu);
        return true;
    }

    /**
     * Called when your activity's options menu needs to be created.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
    }

    /**
     * Called when a menu item is selected.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	int itemId = item.getItemId();
    	if (itemId == R.id.menu_joystick) {
    		_virtualControllerManager.setActiveController("Virtual Joystick");
            return true;
    	}
        if (itemId == R.id.menu_keyboard) {
        	_virtualControllerManager.setActiveController("Virtual Keyboard");
        	return true;
        }
        if (itemId == R.id.menu_tilt) {
        	_virtualControllerManager.setActiveController("Tilt Joystick");
        	return true;
        }
        if (itemId == R.id.menu_cp) {
        	_virtualControllerManager.setActiveController("Control Panel");
        	return true;
        }
        if (itemId == R.id.menu_reset) {
        	SDLInterface.nativeKeyCycle(SDLKeysym.SDLK_F5, 200);
        	return true;
        }
        if (itemId == R.id.menu_start) {
        	SDLInterface.nativeKeyCycle(SDLKeysym.SDLK_F4, 200);
        	return true;
        }
        return  super.onOptionsItemSelected(item);
    }

    // virtual controllers and manager
    private TouchpadJoystick _touchpadJoystick = null;
//    private TouchPaddle _touchPaddle = null;
    private AccelerometerJoystick _accelerometerJoystick = null;
//    private AtariKeypad _atariKeypad = null;
    private ButtonPanelController _buttonPanelController = null;
    private NullController _nullController = null;
    private KeyboardOverlay _keyboardOverlay;
    private VirtualControllerManager _virtualControllerManager = null;


	private SDLSurfaceView mGLView = null;
	private LoadLibrary mLoadLibraryStub = null;
	private AudioThread mAudioThread = null;
	private PowerManager.WakeLock wakeLock = null;
	private boolean sdlInited = false;
    private ButtonPanel _buttonPanel;
    private Keymap _keymap = null;
    private int _lastCharDown = 0;
    private boolean _lowerMode = false;

}
