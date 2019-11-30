package xtvapps.retrobox.atari800;

import retrobox.vinput.overlay.OverlayGamepadController;
import retrobox.vinput.overlay.OverlayGamepadView;
import android.content.Context;
import android.view.MotionEvent;

import com.tvi910.android.core.VirtualController;

public class OverlayGamepadControllerWrapper extends VirtualController {

	OverlayGamepadView view;
	OverlayGamepadController controller;
	
	protected OverlayGamepadControllerWrapper(Context context, OverlayGamepadController controller, OverlayGamepadView view) {
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
