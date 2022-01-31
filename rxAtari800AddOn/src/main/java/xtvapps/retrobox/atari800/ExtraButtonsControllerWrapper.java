package xtvapps.retrobox.atari800;

import com.tvi910.android.core.VirtualController;

import android.content.Context;
import android.view.MotionEvent;
import retrobox.vinput.overlay.ExtraButtonsController;
import retrobox.vinput.overlay.ExtraButtonsView;

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
	
	public void invalidate() {
		view.invalidate();
	}
}
