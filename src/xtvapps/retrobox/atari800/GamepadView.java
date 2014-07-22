package xtvapps.retrobox.atari800;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;

public class GamepadView extends View {

	private ViewGroup _viewGroup;
	
	public GamepadView(Context context) {
		super(context);
	}

	int fs = 0;
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		MainActivity.overlay.draw(canvas);
	}
	
    public void addToLayout(ViewGroup viewGroup) {
        if (_viewGroup == null) {
            _viewGroup = viewGroup;
        }
    }
    
    public void hidePanel() {
        if (_viewGroup != null) {
            _viewGroup.removeView(this);
        }
    }
    public void showPanel() {
        if (_viewGroup != null) {
            if (getParent() != null) _viewGroup.removeView(this);
            _viewGroup.addView(this);
        }
    }

    public boolean isVisible() {
        return (null != getParent());
    }


}
