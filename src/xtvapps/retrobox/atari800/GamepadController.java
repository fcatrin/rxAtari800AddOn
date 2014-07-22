package xtvapps.retrobox.atari800;

import retrobox.vinput.overlay.OverlayNew;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import com.tvi910.android.core.VirtualController;

public class GamepadController extends VirtualController {

	GamepadView gamepadView;
	
	protected GamepadController(Context context, GamepadView gamepadView) {
		super(context);
		this.gamepadView = gamepadView;
	}

	@Override
	protected void privActivate() {
		gamepadView.showPanel();
	}

	@Override
	protected void privDeactivate() {
		gamepadView.hidePanel();
	}

    public boolean onTouchEvent(MotionEvent event) {

        final int action = event.getActionMasked();
		final int pointerId = event.getActionIndex();
		int xp = (int)event.getX(pointerId);
        int yp = (int)event.getY(pointerId);

        //Log.d("OVERLAY", "Touch event " + xp + "," + yp + "  pid:" + pointerId + " action:" + action);
        
        switch (action) {
        	case MotionEvent.ACTION_MOVE:
        		if (OverlayNew.onPointerMove(pointerId, xp, yp)) return true;
        		break;
        	case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_DOWN: {
            	if (OverlayNew.onPointerDown(pointerId, xp, yp)) return true;
            	break;
            }
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP: {
            	if (OverlayNew.onPointerUp(pointerId)) return true;
            	break;
            }
        }

        return false;
    }

}
