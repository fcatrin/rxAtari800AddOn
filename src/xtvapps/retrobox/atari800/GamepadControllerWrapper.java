package xtvapps.retrobox.atari800;

import retrobox.vinput.overlay.GamepadController;
import retrobox.vinput.overlay.GamepadView;
import android.content.Context;
import android.view.MotionEvent;

import com.tvi910.android.core.VirtualController;

public class GamepadControllerWrapper extends VirtualController {

	GamepadView view;
	GamepadController controller;
	
	protected GamepadControllerWrapper(Context context, GamepadController controller, GamepadView view) {
		super(context);
		this.view = view;
		this.controller = controller;
	}

	@Override
	protected void privActivate() {
		view.showPanel();
	}

	@Override
	protected void privDeactivate() {
		view.hidePanel();
	}

    public boolean onTouchEvent(MotionEvent event) {
        return controller.onTouchEvent(event);
    }

}
