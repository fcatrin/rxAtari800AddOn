package xtvapps.retrobox.atari800;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tvi910.android.core.Keymap;
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
	
	Map<String, Integer> atariMap = new HashMap<String, Integer>();
	
	private KeyboardView kb;

	public CustomKeyboard(Activity activity) {
		initMap();

		kb = (KeyboardView)activity.findViewById(R.id.keyboard_view);
		
		Atari800KeyboardLayout kl = new Atari800KeyboardLayout();
		kb.init(activity, kl.getKeyboardLayout());
		
		kb.setOnVirtualKeyListener(new VirtualKeyListener(){

			@Override
			public void onKeyPressed(String code) {
				int keyCode = translate(code);
				
				if (keyCode == 0) {
					try {
						keyCode = autoTranslate(code);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				
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
	
	protected int autoTranslate(String code) throws UnsupportedEncodingException {
		int charCode = 0;
		byte[] bytes = code.getBytes("ASCII");
		
		int p = code.indexOf("KEY_");
		if (p<0) {
			charCode = bytes[0];
		} else {
			charCode = bytes[p+"KEY_".length()];
		}
		
        if (charCode > 0) {
            // flip lower and upper
            if (charCode >= 65 && charCode <= 90) {
                charCode = charCode + 32;
            } else if (charCode >= 97 && charCode <= 122) {
                charCode = charCode - 32;
            }

            if (charCode == 10) charCode = 13;
        }
		return charCode;
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
	
	private void initMap() {
		atariMap.put("ATR_RETURN", 13);
		atariMap.put("ATR_KEY_LEFT",  SDLKeysym.SDLK_LEFT);
		atariMap.put("ATR_KEY_RIGHT", SDLKeysym.SDLK_RIGHT);
		atariMap.put("ATR_KEY_UP",    SDLKeysym.SDLK_UP);
		atariMap.put("ATR_KEY_DOWN",  SDLKeysym.SDLK_DOWN);

        atariMap.put("ATR_LEFT",    SDLKeysym.SDLK_JOY_0_LEFT);
        atariMap.put("ATR_RIGHT",   SDLKeysym.SDLK_JOY_0_RIGHT);
        atariMap.put("ATR_UP",      SDLKeysym.SDLK_JOY_0_UP);
        atariMap.put("ATR_DOWN",    SDLKeysym.SDLK_JOY_0_DOWN);
        atariMap.put("ATR_TRIGGER", SDLKeysym.SDLK_JOY_0_TRIGGER);
        
        atariMap.put("ATR_RESET",  SDLKeysym.SDLK_F5);
        atariMap.put("ATR_OPTION", SDLKeysym.SDLK_F2);
        atariMap.put("ATR_SELECT", SDLKeysym.SDLK_F3);
        atariMap.put("ATR_START",  SDLKeysym.SDLK_F4);
        atariMap.put("ATR_HELP",   SDLKeysym.SDLK_F6);
        atariMap.put("ATR_SPACE",  SDLKeysym.SDLK_SPACE);
        atariMap.put("ATR_ESCAPE", SDLKeysym.SDLK_ESCAPE);
        atariMap.put("ATR_RETURN", SDLKeysym.SDLK_RETURN);
        // second player
        atariMap.put("ATR_LEFT2",    SDLKeysym.SDLK_JOY_1_LEFT);
        atariMap.put("ATR_RIGHT2",   SDLKeysym.SDLK_JOY_1_RIGHT);
        atariMap.put("ATR_UP2",      SDLKeysym.SDLK_JOY_1_UP);
        atariMap.put("ATR_DOWN2",    SDLKeysym.SDLK_JOY_1_DOWN);
        atariMap.put("ATR_TRIGGER2", SDLKeysym.SDLK_JOY_1_TRIGGER);
        
        atariMap.put("ATR_ESCAPE",     SDLKeysym.SDLK_ESCAPE);
        atariMap.put("ATR_LESS_THAN",  SDLKeysym.SDLK_LESS);
        atariMap.put("ATR_MORE_THAN",  SDLKeysym.SDLK_GREATER);
        atariMap.put("ATR_BACKSPACE",  SDLKeysym.SDLK_BACKSPACE);
        atariMap.put("ATR_BREAK",      SDLKeysym.SDLK_F7);
        atariMap.put("ATR_TAB",        SDLKeysym.SDLK_TAB);
        atariMap.put("ATR_MINUS",      SDLKeysym.SDLK_MINUS);
        atariMap.put("ATR_EQUALS",     SDLKeysym.SDLK_EQUALS);
        atariMap.put("ATR_CTRL",       SDLKeysym.SDLK_LCTRL);
        atariMap.put("ATR_SEMICOLON",  SDLKeysym.SDLK_SEMICOLON);
        atariMap.put("ATR_PLUS",       SDLKeysym.SDLK_PLUS);
        atariMap.put("ATR_STAR",       SDLKeysym.SDLK_ASTERISK);
        atariMap.put("ATR_LEFTSHIFT",  SDLKeysym.SDLK_LSHIFT);
        atariMap.put("ATR_COMMA",      SDLKeysym.SDLK_COMMA);
        atariMap.put("ATR_DOT",        SDLKeysym.SDLK_PERIOD);
        atariMap.put("ATR_SLASH",      SDLKeysym.SDLK_SLASH);
        atariMap.put("ATR_RIGHTSHIFT", SDLKeysym.SDLK_RSHIFT);
        atariMap.put("ATR_SPACE",      SDLKeysym.SDLK_SPACE);
        
	}
	
	private int translate(String code) {
		if (!atariMap.containsKey(code)) return 0;
		return atariMap.get(code);
	}
}
