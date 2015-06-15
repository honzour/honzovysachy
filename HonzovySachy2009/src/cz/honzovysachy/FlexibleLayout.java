package cz.honzovysachy;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class FlexibleLayout extends LinearLayout {

	public FlexibleLayout(Context context) {
		super(context);
	}
	
	public FlexibleLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	protected void onSizeChanged(final int w, final int h, int oldw, int oldh) {
		new Handler().post(new Runnable() {
			public void run() {
				setOrientation(w > h ? HORIZONTAL : VERTICAL);
			}
    	});
    }

}
