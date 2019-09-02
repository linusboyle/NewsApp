/*
 * Copyright (c) 2019 Zhilei Han and Yihao Chen.
 * This software falls under the GNU general public license version 3 or later.
 * It comes WITHOUT ANY WARRANTY WHATSOEVER. For details, see the file LICENSE
 * in the root directory or <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package com.example.newsclientapp.ui.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import com.example.newsclientapp.listener.OnRearrangeListener;

import java.util.ArrayList;
import java.util.Collections;

public class DraggableGridView extends ViewGroup implements OnTouchListener, OnClickListener, OnLongClickListener {
	public static float childRatio = 0.9F;
	protected int colCount;
	protected int childSize;
	protected int padding;
	protected int dpi;
	protected int scroll = 0;
	protected float lastDelta = 0.0F;
	protected Handler handler = new Handler();
	protected int dragged = -1;
	protected int lastX = -1;
	protected int lastY = -1;
	protected int lastTarget = -1;
	protected boolean enabled = true;
	protected boolean touching = false;
	public static int animT = 150;
	protected ArrayList<Integer> newPositions = new ArrayList();
	protected OnRearrangeListener onRearrangeListener;
	protected OnClickListener secondaryOnClickListener;
	private OnItemClickListener onItemClickListener;
	protected Runnable updateTask = new Runnable() {
		@Override
		public void run() {
			DraggableGridView var10000;
			if (DraggableGridView.this.dragged != -1) {
				if (DraggableGridView.this.lastY < DraggableGridView.this.padding * 3 && DraggableGridView.this.scroll > 0) {
					var10000 = DraggableGridView.this;
					var10000.scroll -= 20;
				} else if (DraggableGridView.this.lastY > DraggableGridView.this.getBottom() - DraggableGridView.this.getTop() - DraggableGridView.this.padding * 3 && DraggableGridView.this.scroll < DraggableGridView.this.getMaxScroll()) {
					var10000 = DraggableGridView.this;
					var10000.scroll += 20;
				}
			} else if (DraggableGridView.this.lastDelta != 0.0F && !DraggableGridView.this.touching) {
				var10000 = DraggableGridView.this;
				var10000.scroll = (int)((float)var10000.scroll + DraggableGridView.this.lastDelta);
				var10000 = DraggableGridView.this;
				var10000.lastDelta = (float)((double)var10000.lastDelta * 0.9D);
				if ((double)Math.abs(DraggableGridView.this.lastDelta) < 0.25D) {
					DraggableGridView.this.lastDelta = 0.0F;
				}
			}

			DraggableGridView.this.clampScroll();
			DraggableGridView.this.onLayout(true, DraggableGridView.this.getLeft(), DraggableGridView.this.getTop(), DraggableGridView.this.getRight(), DraggableGridView.this.getBottom());
			DraggableGridView.this.handler.postDelayed(this, 25L);
		}
	};

	public DraggableGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setListeners();
		this.handler.removeCallbacks(this.updateTask);
		this.handler.postAtTime(this.updateTask, SystemClock.uptimeMillis() + 500L);
		this.setChildrenDrawingOrderEnabled(true);
		DisplayMetrics metrics = new DisplayMetrics();
		((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
		this.dpi = metrics.densityDpi;
	}

	protected void setListeners() {
		this.setOnTouchListener(this);
		super.setOnClickListener(this);
		this.setOnLongClickListener(this);
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		this.secondaryOnClickListener = l;
	}

	@Override
	public void addView(View child) {
		super.addView(child);
		this.newPositions.add(-1);
	}

	@Override
	public void removeViewAt(int index) {
		super.removeViewAt(index);
		this.newPositions.remove(index);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		float w = (float)(r - l) / ((float)this.dpi / 160.0F);
		this.colCount = 2;
		int sub = 240;

		for(w -= 280.0F; w > 0.0F; sub += 40) {
			++this.colCount;
			w -= (float)sub;
		}

		this.childSize = (r - l) / this.colCount;
		this.childSize = Math.round((float)this.childSize * childRatio);
		this.padding = (r - l - this.childSize * this.colCount) / (this.colCount + 1);

		for(int i = 0; i < this.getChildCount(); ++i) {
			if (i != this.dragged) {
				Point xy = this.getCoorFromIndex(i);
				getChildAt(i).measure(MeasureSpec.makeMeasureSpec(childSize, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(childSize, MeasureSpec.EXACTLY));
				this.getChildAt(i).layout(xy.x, xy.y, xy.x + this.childSize, xy.y + this.childSize);
			}
		}

	}

	@Override
	protected int getChildDrawingOrder(int childCount, int i) {
		if (this.dragged == -1) {
			return i;
		} else if (i == childCount - 1) {
			return this.dragged;
		} else {
			return i >= this.dragged ? i + 1 : i;
		}
	}

	public int getIndexFromCoor(int x, int y) {
		int col = this.getColOrRowFromCoor(x);
		int row = this.getColOrRowFromCoor(y + this.scroll);
		if (col != -1 && row != -1) {
			int index = row * this.colCount + col;
			return index >= this.getChildCount() ? -1 : index;
		} else {
			return -1;
		}
	}

	protected int getColOrRowFromCoor(int coor) {
		coor -= this.padding;

		for(int i = 0; coor > 0; ++i) {
			if (coor < this.childSize) {
				return i;
			}

			coor -= this.childSize + this.padding;
		}

		return -1;
	}

	protected int getTargetFromCoor(int x, int y) {
		if (this.getColOrRowFromCoor(y + this.scroll) == -1) {
			return -1;
		} else {
			int leftPos = this.getIndexFromCoor(x - this.childSize / 4, y);
			int rightPos = this.getIndexFromCoor(x + this.childSize / 4, y);
			if (leftPos == -1 && rightPos == -1) {
				return -1;
			} else if (leftPos == rightPos) {
				return -1;
			} else {
				int target = -1;
				if (rightPos > -1) {
					target = rightPos;
				} else if (leftPos > -1) {
					target = leftPos + 1;
				}

				return this.dragged < target ? target - 1 : target;
			}
		}
	}

	protected Point getCoorFromIndex(int index) {
		int col = index % this.colCount;
		int row = index / this.colCount;
		return new Point(this.padding + (this.childSize + this.padding) * col, this.padding + (this.childSize + this.padding) * row - this.scroll);
	}

	public int getIndexOf(View child) {
		for(int i = 0; i < this.getChildCount(); ++i) {
			if (this.getChildAt(i) == child) {
				return i;
			}
		}

		return -1;
	}

	@Override
	public void onClick(View view) {
		if (this.enabled) {
			if (this.secondaryOnClickListener != null) {
				this.secondaryOnClickListener.onClick(view);
			}

			if (this.onItemClickListener != null && this.getLastIndex() != -1) {
				this.onItemClickListener.onItemClick((AdapterView)null, this.getChildAt(this.getLastIndex()), this.getLastIndex(), (long)(this.getLastIndex() / this.colCount));
			}
		}

	}

	@Override
	public boolean onLongClick(View view) {
		if (!this.enabled) {
			return false;
		} else {
			int index = this.getLastIndex();
			if (index != -1) {
				this.dragged = index;
				this.animateDragged();
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		int action = event.getAction();
		switch(action & 255) {
			case 0:
				this.enabled = true;
				this.lastX = (int)event.getX();
				this.lastY = (int)event.getY();
				this.touching = true;
				break;
			case 1:
				if (this.dragged != -1) {
					View v = this.getChildAt(this.dragged);
					if (this.lastTarget != -1) {
						this.reorderChildren();
					} else {
						Point xy = this.getCoorFromIndex(this.dragged);
						v.layout(xy.x, xy.y, xy.x + this.childSize, xy.y + this.childSize);
					}

					v.clearAnimation();
					if (v instanceof ImageView) {
						((ImageView)v).setAlpha(255);
					}

					this.lastTarget = -1;
					this.dragged = -1;
				}

				this.touching = false;
				break;
			case 2:
				int delta = this.lastY - (int)event.getY();
				if (this.dragged != -1) {
					int x = (int)event.getX();
					int y = (int)event.getY();
					int l = x - 3 * this.childSize / 4;
					int t = y - 3 * this.childSize / 4;
					this.getChildAt(this.dragged).layout(l, t, l + this.childSize * 3 / 2, t + this.childSize * 3 / 2);
					int target = this.getTargetFromCoor(x, y);
					if (this.lastTarget != target && target != -1) {
						this.animateGap(target);
						this.lastTarget = target;
					}
				} else {
					this.scroll += delta;
					this.clampScroll();
					if (Math.abs(delta) > 2) {
						this.enabled = false;
					}

					this.onLayout(true, this.getLeft(), this.getTop(), this.getRight(), this.getBottom());
				}

				this.lastX = (int)event.getX();
				this.lastY = (int)event.getY();
				this.lastDelta = (float)delta;
		}

		return this.dragged != -1;
	}

	protected void animateDragged() {
		View v = this.getChildAt(this.dragged);
		int x = this.getCoorFromIndex(this.dragged).x + this.childSize / 2;
		int y = this.getCoorFromIndex(this.dragged).y + this.childSize / 2;
		int l = x - 3 * this.childSize / 4;
		int t = y - 3 * this.childSize / 4;
		v.layout(l, t, l + this.childSize * 3 / 2, t + this.childSize * 3 / 2);
		AnimationSet animSet = new AnimationSet(true);
		ScaleAnimation scale = new ScaleAnimation(0.667F, 1.0F, 0.667F, 1.0F, (float)(this.childSize * 3 / 4), (float)(this.childSize * 3 / 4));
		scale.setDuration((long)animT);
		AlphaAnimation alpha = new AlphaAnimation(1.0F, 0.5F);
		alpha.setDuration((long)animT);
		animSet.addAnimation(scale);
		animSet.addAnimation(alpha);
		animSet.setFillEnabled(true);
		animSet.setFillAfter(true);
		v.clearAnimation();
		v.startAnimation(animSet);
	}

	protected void animateGap(int target) {
		for(int i = 0; i < this.getChildCount(); ++i) {
			View v = this.getChildAt(i);
			if (i != this.dragged) {
				int newPos = i;
				if (this.dragged < target && i >= this.dragged + 1 && i <= target) {
					newPos = i - 1;
				} else if (target < this.dragged && i >= target && i < this.dragged) {
					newPos = i + 1;
				}

				int oldPos = i;
				if ((Integer)this.newPositions.get(i) != -1) {
					oldPos = (Integer)this.newPositions.get(i);
				}

				if (oldPos != newPos) {
					Point oldXY = this.getCoorFromIndex(oldPos);
					Point newXY = this.getCoorFromIndex(newPos);
					Point oldOffset = new Point(oldXY.x - v.getLeft(), oldXY.y - v.getTop());
					Point newOffset = new Point(newXY.x - v.getLeft(), newXY.y - v.getTop());
					TranslateAnimation translate = new TranslateAnimation(0, (float)oldOffset.x, 0, (float)newOffset.x, 0, (float)oldOffset.y, 0, (float)newOffset.y);
					translate.setDuration((long)animT);
					translate.setFillEnabled(true);
					translate.setFillAfter(true);
					v.clearAnimation();
					v.startAnimation(translate);
					this.newPositions.set(i, newPos);
				}
			}
		}

	}

	protected void reorderChildren() {
		if (this.onRearrangeListener != null) {
			this.onRearrangeListener.onRearrange(this.dragged, this.lastTarget);
		}

		ArrayList<View> children = new ArrayList();

		int i;
		for(i = 0; i < this.getChildCount(); ++i) {
			this.getChildAt(i).clearAnimation();
			children.add(this.getChildAt(i));
		}

		this.removeAllViews();

		while(this.dragged != this.lastTarget) {
			if (this.lastTarget == children.size()) {
				children.add((View)children.remove(this.dragged));
				this.dragged = this.lastTarget;
			} else if (this.dragged < this.lastTarget) {
				Collections.swap(children, this.dragged, this.dragged + 1);
				++this.dragged;
			} else if (this.dragged > this.lastTarget) {
				Collections.swap(children, this.dragged, this.dragged - 1);
				--this.dragged;
			}
		}

		for(i = 0; i < children.size(); ++i) {
			this.newPositions.set(i, -1);
			this.addView((View)children.get(i));
		}

		this.onLayout(true, this.getLeft(), this.getTop(), this.getRight(), this.getBottom());
	}

	public void scrollToTop() {
		this.scroll = 0;
	}

	public void scrollToBottom() {
		this.scroll = 2147483647;
		this.clampScroll();
	}

	protected void clampScroll() {
		int stretch = 3;
		int overreach = this.getHeight() / 2;
		int max = this.getMaxScroll();
		max = Math.max(max, 0);
		if (this.scroll < -overreach) {
			this.scroll = -overreach;
			this.lastDelta = 0.0F;
		} else if (this.scroll > max + overreach) {
			this.scroll = max + overreach;
			this.lastDelta = 0.0F;
		} else if (this.scroll < 0) {
			if (this.scroll >= -stretch) {
				this.scroll = 0;
			} else if (!this.touching) {
				this.scroll -= this.scroll / stretch;
			}
		} else if (this.scroll > max) {
			if (this.scroll <= max + stretch) {
				this.scroll = max;
			} else if (!this.touching) {
				this.scroll += (max - this.scroll) / stretch;
			}
		}

	}

	protected int getMaxScroll() {
		int rowCount = (int)Math.ceil((double)this.getChildCount() / (double)this.colCount);
		int max = rowCount * this.childSize + (rowCount + 1) * this.padding - this.getHeight();
		return max;
	}

	public int getLastIndex() {
		return this.getIndexFromCoor(this.lastX, this.lastY);
	}

	public void setOnRearrangeListener(OnRearrangeListener l) {
		this.onRearrangeListener = l;
	}

	public void setOnItemClickListener(OnItemClickListener l) {
		this.onItemClickListener = l;
	}
}
