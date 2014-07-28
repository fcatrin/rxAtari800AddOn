package xtvapps.retrobox.atari800;

import retrobox.vinput.overlay.ExtraButtonsController;
import retrobox.vinput.overlay.ExtraButtonsView;
import android.content.Context;
import android.view.MotionEvent;

import com.tvi910.android.core.VirtualController;

public class ExtraButtonsControllerWrapper extends VirtualController {

	ExtraButtonsView view;
	ExtraButtonsController controller;
	
	protected ExtraButtonsControllerWrapper(Context context, ExtraButtonsController controller, ExtraButtonsView view) {
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

	@Override
	public boolean isSticky() {
		return false;
	}
}
