package xtvapps.retrobox.atari800;

public class NativeInterface {
    public static native void saveState(int slot);
    public static native void loadState(int slot);
}
