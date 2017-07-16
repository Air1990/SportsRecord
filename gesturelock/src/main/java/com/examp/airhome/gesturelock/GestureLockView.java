package com.examp.airhome.gesturelock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by airhome on 2015/11/30.
 */
public class GestureLockView extends View {
    private int mSetLockTime = 1;
    private String mFirstLock;

    private float mCircleRadius;
    private float mCircleWidth;
    private float mPointRadius;
    private float mLineWidth;

    private boolean isInit = false;
    private Point[][] mPoint = new Point[3][3];

    private Paint mNormalCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mSelectedCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mErrorCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint mSelectedPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mErrorPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint mSelectedLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mErrorLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Point mCurrentPoint;
    private boolean isDraw = false;
    private List<Point> mSelectedPoints = new ArrayList<>();

    private LockState mLockState;
    private OnGestureLockListener mLockListener;

    public enum LockState {SET_LOCK, UNLOCK}

    public GestureLockView(Context context) {
        super(context);
        initSize();
    }

    public void setGestureListener(OnGestureLockListener listener) {
        mLockListener = listener;
    }

    public GestureLockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSize();
    }

    public GestureLockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSize();
    }

    public interface OnGestureLockListener {
        void updateLockState(String message, int color);

        void unLock();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isInit) {
            initView();
            mLockState = Preferences.getLockState(getContext());
            if (mLockState == LockState.SET_LOCK) {
                mLockListener.updateLockState("请设置手势密码", R.color.normal);
            } else {
                mLockListener.updateLockState("请绘制解锁密码", R.color.normal);
            }
            isInit = true;
        }
        drawCircles(canvas);
        int num = mSelectedPoints.size();
        if (num > 0) {
            Point a = mSelectedPoints.get(0);
            drawPoint(canvas, a);
            for (int i = 1; i < num; i++) {
                Point b = mSelectedPoints.get(i);
                drawLine(canvas, a, b);
                drawPoint(canvas, b);
                a = b;
            }
            drawLine(canvas, a, mCurrentPoint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Point point;
        mCurrentPoint = new Point(event.getX(), event.getY());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                point = getSelectPoint(mCurrentPoint.x, mCurrentPoint.y);
                if (point != null) {
                    isDraw = true;
                    point.state = Point.State.SELECTED;
                    mSelectedPoints.add(point);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isDraw) {
                    point = getSelectPoint(mCurrentPoint.x, mCurrentPoint.y);
                    if (!(point == null || mSelectedPoints.contains(point))) {
                        point.state = Point.State.SELECTED;
                        mSelectedPoints.add(point);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                isDraw = false;
                if (mSelectedPoints.size() > 0) {
                    if (mLockState == LockState.SET_LOCK) {
                        setLock();
                    } else {
                        unlock();
                    }
                }
                break;
        }
        invalidate();
        return true;
    }

    private void drawCircles(Canvas canvas) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Point point = mPoint[i][j];
                if (point.state == Point.State.NORMAL) {
                    canvas.drawCircle(point.x, point.y, mCircleRadius, mNormalCirclePaint);
                } else if (point.state == Point.State.SELECTED) {
                    canvas.drawCircle(point.x, point.y, mCircleRadius, mSelectedCirclePaint);
                } else {
                    canvas.drawCircle(point.x, point.y, mCircleRadius, mErrorCirclePaint);

                }
            }
        }
    }

    private void drawLine(Canvas canvas, Point a, Point b) {
        if (a.state == Point.State.SELECTED) {
            canvas.drawLine(a.x, a.y, b.x, b.y, mSelectedLinePaint);
        } else if (a.state == Point.State.ERROR) {
            canvas.drawLine(a.x, a.y, b.x, b.y, mErrorLinePaint);
        }
    }

    private void drawPoint(Canvas canvas, Point a) {
        if (a.state == Point.State.SELECTED) {
            canvas.drawCircle(a.x, a.y, mPointRadius, mSelectedPointPaint);
        } else if (a.state == Point.State.ERROR) {
            canvas.drawCircle(a.x, a.y, mPointRadius, mErrorPointPaint);
        }
    }

    private Point getSelectPoint(float x, float y) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Point point = mPoint[i][j];
                if (Math.sqrt((point.x - x) * (point.x - x) + (point.y - y) * (point.y - y)) <= mCircleRadius) {
                    return point;
                }
            }
        }
        return null;
    }

    private void initSize() {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        float density = displayMetrics.density;
        mCircleRadius = (0.7f * screenWidth / 2 / 4);
        mPointRadius = mCircleRadius / 4;
        mLineWidth = mPointRadius / 2;
        mCircleWidth = mLineWidth / 2;
        Log.i("====", String.valueOf(screenWidth));
        Log.i("==densityDpi==", String.valueOf(density));
        Log.i("==mCircleRadius==", String.valueOf(mCircleRadius));
        Log.i("==mPointRadius==", String.valueOf(mPointRadius));
        Log.i("==mLineWidth==", String.valueOf(mLineWidth));
        Log.i("==mCircleWidth==", String.valueOf(mCircleWidth));
    }

    private void initView() {
        mNormalCirclePaint.setColor(getResources().getColor(R.color.normal));
        mNormalCirclePaint.setStrokeWidth(mCircleWidth);
        mNormalCirclePaint.setStyle(Paint.Style.STROKE);

        mSelectedCirclePaint.setColor(getResources().getColor(R.color.select));
        mSelectedCirclePaint.setStrokeWidth(mCircleWidth);
        mSelectedCirclePaint.setStyle(Paint.Style.STROKE);

        mErrorCirclePaint.setColor(getResources().getColor(R.color.error));
        mErrorCirclePaint.setStrokeWidth(mCircleWidth);
        mErrorCirclePaint.setStyle(Paint.Style.STROKE);

        mSelectedLinePaint.setColor(getResources().getColor(R.color.select));
        mSelectedLinePaint.setStrokeWidth(mLineWidth);
        mErrorLinePaint.setColor(getResources().getColor(R.color.select));
        mErrorLinePaint.setStrokeWidth(mLineWidth);

        mSelectedPointPaint.setColor(getResources().getColor(R.color.select));
        mErrorPointPaint.setColor(getResources().getColor(R.color.error));

        int width = getWidth();
        int offset = (getHeight() - width) / 2;
        int space = width / 4;
        mPoint[0][0] = new Point(space, offset + space);
        mPoint[0][1] = new Point(space * 2, offset + space);
        mPoint[0][2] = new Point(space * 3, offset + space);

        mPoint[1][0] = new Point(space, offset + space * 2);
        mPoint[1][1] = new Point(space * 2, offset + space * 2);
        mPoint[1][2] = new Point(space * 3, offset + space * 2);

        mPoint[2][0] = new Point(space, offset + space * 3);
        mPoint[2][1] = new Point(space * 2, offset + space * 3);
        mPoint[2][2] = new Point(space * 3, offset + space * 3);
    }

    private void setLock() {
        String input = getInputPassword();
        if (mSetLockTime == 1) {
            if (mSelectedPoints.size() < 4) {
                mLockListener.updateLockState("手势密码最少四位！", R.color.error);
            } else {
                mFirstLock = input;
                mLockListener.updateLockState("请再次绘制密码！", R.color.normal);
                mSetLockTime++;
            }
        } else {
            if (input.equals(mFirstLock)) {
                Preferences.savePassword(getContext(), MD5Utils.md5(input));
                Preferences.setLockState(getContext(), true);
                mLockState = LockState.UNLOCK;
                Toast.makeText(getContext(), "设置成功", Toast.LENGTH_SHORT).show();
                mLockListener.updateLockState("请绘制解锁密码", R.color.select);
            } else {
                mLockListener.updateLockState("绘制与上次不一致", R.color.error);
            }
        }
        resetPoints();
    }

    private void unlock() {
        String input = getInputPassword();
        if (MD5Utils.md5(input).equals(Preferences.getPassword(getContext()))) {
            mLockListener.updateLockState("解锁成功", R.color.select);
            mLockListener.unLock();
        } else {
            mLockListener.updateLockState("密码绘制错误", R.color.error);
        }
        resetPoints();
    }

    private String getInputPassword() {
        StringBuilder sb = new StringBuilder();
        for (Point point : mSelectedPoints) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (point.x == mPoint[i][j].x && point.y == mPoint[i][j].y) {
                        sb.append(String.valueOf(i * 3 + j));
                    }
                }
            }
        }
        Log.i("===输入密码===", sb.toString());
        Log.i("===加密密码===", MD5Utils.md5(sb.toString()));
        return sb.toString();
    }

    private void resetPoints() {
        mSelectedPoints.clear();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                mPoint[i][j].state = Point.State.NORMAL;
            }
        }
        invalidate();
    }
}
