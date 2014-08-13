/**
 * 
 */
package com.example.sensorapp.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * @author Administrator
 * 
 */
public class LockImageView extends ImageView {
	private int left = 300, right = 300;// 可拖动的左右边距.
	// private static Paint paint = new Paint();// 画笔
	// private Rect bounds;
	private boolean isMoving = false;
	private int startX;
	private int totalX;
	private int origenalLeft, origenalRight;

	public void setDragBound(int left, int right) {
		this.left = left;
		this.right = right;
	}

	public LockImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int) event.getRawX();
		// int y = (int) event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			isMoving = true;
			startX = x;
			totalX = 0;
			origenalLeft = getLeft();
			origenalRight = getRight();
		}
			break;
		case MotionEvent.ACTION_MOVE: {
			if (isMoving) {
				int deltaX = x - startX;
				startX = x;
				int left = getLeft() + deltaX;
				int right = getRight() + deltaX;

				totalX += deltaX;

				// System.out.println("SSS:" + System.currentTimeMillis());
				// int left = getLeft()+deltaX;
				this.layout(left, getTop(), right, getBottom());

				// System.out.println("EEE:" + System.currentTimeMillis());
			}
			break;
		}
		case MotionEvent.ACTION_UP:
			isMoving = false;
			if ((totalX > 0 && totalX > right)
					|| (totalX < 0 && -totalX > left)) {
				if (unLockListener != null) {
					unLockListener.onUnlocked(this);
				}
			}
			this.layout(origenalLeft, getTop(), origenalRight, getBottom());
			break;
		}
		return true;// 处理了触摸消息，消息不再传递
	}

	private OnUnLockListener unLockListener;

	public void setOnLockListener(OnUnLockListener listener) {
		this.unLockListener = listener;
	}

	public interface OnUnLockListener {
		public void onUnlocked(View v);
	}
}
