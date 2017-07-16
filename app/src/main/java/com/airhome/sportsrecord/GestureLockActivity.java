package com.airhome.sportsrecord;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.examp.airhome.gesturelock.GestureLockView;

public class GestureLockActivity extends AppCompatActivity implements GestureLockView.OnGestureLockListener {
    private GestureLockView mGestureLock;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture);
        mGestureLock = (GestureLockView) findViewById(R.id.lock);
        mGestureLock.setGestureListener(this);
        mTextView = (TextView) findViewById(R.id.msg);
    }

    @Override
    public void updateLockState(String message, int color) {
        mTextView.setText(message);
        mTextView.setTextColor(getResources().getColor(color));
    }

    @Override
    public void unLock() {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Preferences.setLockState(GestureLockActivity.this, false);
    }
}
