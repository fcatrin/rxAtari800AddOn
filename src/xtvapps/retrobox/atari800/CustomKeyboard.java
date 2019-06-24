package xtvapps.retrobox.atari800;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tvi910.android.core.Keymap;
import com.tvi910.android.sdl.SDLInterface;

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
				VirtualEvent event = KeyTranslator.translate(code);
				if (event==null) return;
				
				int keyCode = 0;
				int nativeCode = Keymap.getInstance().translate(event.keyCode);
				if (nativeCode == 0) {
					nativeCode = translate(code); 
				}
				
				if (nativeCode > 0) {
				    keyCode = nativeCode;
				} else {
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
	}
	
	private int translate(String code) {
		if (!atariMap.containsKey(code)) return 0;
		return atariMap.get(code);
	}
}
