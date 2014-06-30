package xtvapps.retrobox.atari800;

import retrobox.vinput.overlay.Overlay;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import com.tvi910.android.core.VirtualController;
import com.tvi910.android.sdl.SDLInterface;

public class ExtraButtonsController extends VirtualController {

	ExtraButtonsView extraButtonsView;
	
	protected ExtraButtonsController(Context context, ExtraButtonsView extraButtonsView) {
		super(context);
		this.extraButtonsView = extraButtonsView;
	}

	@Override
	protected void privActivate() {
		extraButtonsView.showPanel();
	}

	@Override
	protected void privDeactivate() {
		extraButtonsView.hidePanel();
	}

    public boolean onTouchEvent(MotionEvent event) {

        final int action = event.getAction();
		final int pointerId = ((event.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT);
		int xp = (int)event.getX(pointerId);
        int yp = (int)event.getY(pointerId);

        Log.d("EXTRA", "Touch event " + xp + "," + yp + "  pid:" + pointerId + " mask:" + (action & MotionEvent.ACTION_MASK));
        
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
            	if (Overlay.onExtraButtonPress(pointerId, xp, yp)) return true;
            	break;
            }
            case MotionEvent.ACTION_UP: {
            	if (Overlay.onExtraButtonRelease(pointerId)) return true;
            	break;
            }
        }

        return false;
    }

	@Override
	public boolean isSticky() {
		return false;
	}
}
