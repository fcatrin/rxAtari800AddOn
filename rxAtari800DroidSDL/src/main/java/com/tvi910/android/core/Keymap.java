package com.tvi910.android.core;

import android.content.SharedPreferences;
import android.view.KeyEvent;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class is for translating android keycodes to the SDL codes used
 * by the emulator. The emulator has default keycodes assigned for actions.
 * This class is used to map android keycodes to emulator defaults in order
 * to customize key bindings.
 */
public final class Keymap {

    private int[] _lookupTable;
    private int _numberOfMappedKeys;

    private static final Keymap _instance = new Keymap();

    /**
     * Get the singleton.
     */
    public static Keymap getInstance() {
        return _instance;
    }

    private Keymap() {
        // initialize the lookup table, by default all keys map to 0
        _lookupTable = new int[AndroidKeys.KEYMAP_SIZE];
        for (int i=0; i<AndroidKeys.KEYMAP_SIZE; i++) {
            _lookupTable[i] = 0;
        }
        _numberOfMappedKeys = 0;
    }

    public void reset() {
        for (int i=0; i<AndroidKeys.KEYMAP_SIZE; i++) {
            _lookupTable[i] = 0;
        }
    }

    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    /**
     * Translate the android keycode to an SDL keycode
     */
    public int translate(int androidCode) {
    Log.v("Keymap", "Translating keycode=" + androidCode + ", sdl code=" + _lookupTable[androidCode]);
// living dangerously - expect no keycodes to be > KeyEvent.getMaxKeyCode()
        return _lookupTable[androidCode];
    }

    public void setMap(int androidCode, int atariCode) {
        if (androidCode < AndroidKeys.KEYMAP_SIZE && androidCode > -1) {
            _lookupTable[androidCode] = atariCode;
            _numberOfMappedKeys++;
        }
    }
    
    public void setMap(String androidName, String atariName, ConsoleKeys consoleKeys) {
    	Integer androidCode = AndroidKeys.getCode(androidName);
    	Integer atariCode = consoleKeys.getCode(atariName);
    	Log.d("Keymap", "androidCode " + androidCode);
    	Log.d("Keymap", "atariCode " + atariCode);
    	if (androidCode != null && atariCode!=null) setMap(androidCode, atariCode);
    }

    public int getNumberOfMappedKeys() {
        return _numberOfMappedKeys;
    }

    /**
     * Reload the key mappings. Walk through each defined atari key in
     * AtariKeys and look up its mapped value in the shared preferences. The
     * resulting mapping is from android key to android key.
     */
    public void reload(SharedPreferences prefs, ConsoleKeys consoleKeys) {
        this.reset();
        for (String atariString : consoleKeys.getNames()) {
        	Log.d("Keymap", "Mapping atari key " + atariString);
            String androidString = prefs.getString(atariString, null);
        	Log.d("Keymap", "androidString is " + androidString);
            if (null != androidString && androidString.length() > 0) {
            	setMap(androidString, atariString, consoleKeys);
            }
        }
    }
}
