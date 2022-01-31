package xtvapps.retrobox.atari800;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.tvi910.android.sdl.SDLInterface;
import com.tvi910.android.sdl.SDLKeysym;

import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import retrobox.keyboard.KeyboardView;
import retrobox.keyboard.VirtualKeyListener;
import retrobox.keyboard.layouts.Atari800KeyboardLayout;
import retrobox.vinput.KeyTranslator;
import retrobox.vinput.VirtualEvent;
import xtvapps.core.SimpleCallback;
import xtvapps.retrobox.v2.atari800.R;

public class CustomKeyboard {
	
	// keys from retrobox.vinput.KeyTranslator
	// special keys can be added with retrobox.vinput.KeyTranslator.addTranslation
	
	List<Map<String, String>> keymaps = new ArrayList<Map<String, String>>();
	
	private KeyboardView kb;

	public CustomKeyboard(Activity activity) {
		kb = (KeyboardView)activity.findViewById(R.id.keyboard_view);
		
		Atari800KeyboardLayout kl = new Atari800KeyboardLayout();
		kb.init(activity, kl.getKeyboardLayout());
		
		kb.setOnVirtualKeyListener(new VirtualKeyListener(){

			@Override
			public void onKeyPressed(String code) {
				VirtualEvent event = KeyTranslator.translate(code);
				
				int keyCode = event == null ? 0: event.keyCode;
				
				Log.d("KEY", "send " + code + " as keyCode " + keyCode);
				if (keyCode!=0) {
					SDLInterface.nativeKeyCycle(keyCode, 50);
				}
			}
		});
		
		kb.setOnTogglePositionCallback(new SimpleCallback() {
			
			@Override
			public void onResult() {
				FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)kb.getLayoutParams();
				layoutParams.gravity = layoutParams.gravity == Gravity.TOP ? Gravity.BOTTOM : Gravity.TOP;
				kb.requestLayout();
			}
		});
	}
	
	public void open() {
		kb.setVisibility(View.VISIBLE);
		kb.requestFocus();
	}
	
	public void close() {
		kb.setVisibility(View.GONE);
	}
	
	public boolean isVisible() {
		return kb.getVisibility() == View.VISIBLE;
	}
	
	public static void initKeyMap() {
		KeyTranslator.addTranslation("ATR_KEY_LEFT",  SDLKeysym.SDLK_LEFT);
		KeyTranslator.addTranslation("ATR_KEY_RIGHT", SDLKeysym.SDLK_RIGHT);
		KeyTranslator.addTranslation("ATR_KEY_UP",    SDLKeysym.SDLK_UP);
		KeyTranslator.addTranslation("ATR_KEY_DOWN",  SDLKeysym.SDLK_DOWN);

        KeyTranslator.addTranslation("ATR_LEFT",    SDLKeysym.SDLK_JOY_0_LEFT);
        KeyTranslator.addTranslation("ATR_RIGHT",   SDLKeysym.SDLK_JOY_0_RIGHT);
        KeyTranslator.addTranslation("ATR_UP",      SDLKeysym.SDLK_JOY_0_UP);
        KeyTranslator.addTranslation("ATR_DOWN",    SDLKeysym.SDLK_JOY_0_DOWN);
        KeyTranslator.addTranslation("ATR_TRIGGER", SDLKeysym.SDLK_JOY_0_TRIGGER);
        
        KeyTranslator.addTranslation("ATR_RESET",  SDLKeysym.SDLK_F5);
        KeyTranslator.addTranslation("ATR_OPTION", SDLKeysym.SDLK_F2);
        KeyTranslator.addTranslation("ATR_SELECT", SDLKeysym.SDLK_F3);
        KeyTranslator.addTranslation("ATR_START",  SDLKeysym.SDLK_F4);
        KeyTranslator.addTranslation("ATR_HELP",   SDLKeysym.SDLK_F6);
        KeyTranslator.addTranslation("ATR_SPACE",  SDLKeysym.SDLK_SPACE);
        KeyTranslator.addTranslation("ATR_ESCAPE", SDLKeysym.SDLK_ESCAPE);
		KeyTranslator.addTranslation("ATR_RETURN", SDLKeysym.SDLK_RETURN);
		
        // second player
        KeyTranslator.addTranslation("ATR_LEFT2",    SDLKeysym.SDLK_JOY_1_LEFT);
        KeyTranslator.addTranslation("ATR_RIGHT2",   SDLKeysym.SDLK_JOY_1_RIGHT);
        KeyTranslator.addTranslation("ATR_UP2",      SDLKeysym.SDLK_JOY_1_UP);
        KeyTranslator.addTranslation("ATR_DOWN2",    SDLKeysym.SDLK_JOY_1_DOWN);
        KeyTranslator.addTranslation("ATR_TRIGGER2", SDLKeysym.SDLK_JOY_1_TRIGGER);
        
        KeyTranslator.addTranslation("ATR_ESCAPE",     SDLKeysym.SDLK_ESCAPE);
        KeyTranslator.addTranslation("ATR_LESS_THAN",  SDLKeysym.SDLK_LESS);
        KeyTranslator.addTranslation("ATR_MORE_THAN",  SDLKeysym.SDLK_GREATER);
        KeyTranslator.addTranslation("ATR_BACKSPACE",  SDLKeysym.SDLK_BACKSPACE);
        KeyTranslator.addTranslation("ATR_BREAK",      SDLKeysym.SDLK_F7);
        KeyTranslator.addTranslation("ATR_TAB",        SDLKeysym.SDLK_TAB);
        KeyTranslator.addTranslation("ATR_MINUS",      SDLKeysym.SDLK_MINUS);
        KeyTranslator.addTranslation("ATR_EQUALS",     SDLKeysym.SDLK_EQUALS);
        KeyTranslator.addTranslation("ATR_CTRL",       SDLKeysym.SDLK_LCTRL);
        KeyTranslator.addTranslation("ATR_SEMICOLON",  SDLKeysym.SDLK_SEMICOLON);
        KeyTranslator.addTranslation("ATR_PLUS",       SDLKeysym.SDLK_PLUS);
        KeyTranslator.addTranslation("ATR_STAR",       SDLKeysym.SDLK_ASTERISK);
        KeyTranslator.addTranslation("ATR_LEFTSHIFT",  SDLKeysym.SDLK_LSHIFT);
        KeyTranslator.addTranslation("ATR_COMMA",      SDLKeysym.SDLK_COMMA);
        KeyTranslator.addTranslation("ATR_DOT",        SDLKeysym.SDLK_PERIOD);
        KeyTranslator.addTranslation("ATR_SLASH",      SDLKeysym.SDLK_SLASH);
        KeyTranslator.addTranslation("ATR_RIGHTSHIFT", SDLKeysym.SDLK_RSHIFT);
        KeyTranslator.addTranslation("ATR_SPACE",      SDLKeysym.SDLK_SPACE);

        // haremos esto mejor en otra vida
        for(int i=SDLKeysym.SDLK_a; i<=SDLKeysym.SDLK_z; i++) {
        	String c = new String(new byte[] {(byte)(i)}).toUpperCase(Locale.US);
        	String atariKey = "ATR_" + c;
        	KeyTranslator.addTranslation(atariKey, i);
        	KeyTranslator.addTranslation(c, i);
        	
        }
        for(int i=SDLKeysym.SDLK_0; i<=SDLKeysym.SDLK_9; i++) {
        	String c = new String(new byte[] {(byte)(i)}).toUpperCase(Locale.US);
        	String atariKey = "ATR_" + c;
        	KeyTranslator.addTranslation(atariKey, i);
        	KeyTranslator.addTranslation(c, i);
        }

	}

}
