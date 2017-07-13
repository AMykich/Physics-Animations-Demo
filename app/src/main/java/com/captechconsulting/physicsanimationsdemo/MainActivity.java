package com.captechconsulting.physicsanimationsdemo;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.animation.DynamicAnimation;
import android.support.animation.FlingAnimation;
import android.support.animation.FloatPropertyCompat;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements OnTouchListener, OnClickListener,
        OnSeekBarChangeListener {

    private View mCircleImageView;
    private TextView mFrictionTextView;
    private TextView mStiffnessTextView;
    private TextView mDampingTextView;
    private SeekBar mFrictionSeekBar;
    private SeekBar mStiffnessSeekBar;
    private SeekBar mDampingSeekBar;

    private VelocityTracker mVelocityTracker;
    private FlingAnimation mFlingXAnimation;
    private FlingAnimation mFlingYAnimation;
    private Rect mCircleHitRect = new Rect();

    private boolean mIsDragging = false;
    private float mDownX;
    private float mDownY;
    private float mOffsetX;
    private float mOffsetY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mVelocityTracker = VelocityTracker.obtain();

        mCircleImageView = findViewById(R.id.circle_image_view);
        mFrictionTextView = (TextView) findViewById(R.id.friction_text_view);
        mStiffnessTextView = (TextView) findViewById(R.id.stiffness_text_view);
        mDampingTextView = (TextView) findViewById(R.id.damping_text_view);
        mFrictionSeekBar = (SeekBar) findViewById(R.id.friction_seek_bar);
        mStiffnessSeekBar = (SeekBar) findViewById(R.id.stiffness_seek_bar);
        mDampingSeekBar = (SeekBar) findViewById(R.id.damping_seek_bar);

        mFrictionTextView.setText(getString(R.string.friction, getFriction()));
        mStiffnessTextView.setText(getString(R.string.stiffness, getStiffness()));
        mDampingTextView.setText(getString(R.string.damping, getDamping()));
        mFrictionSeekBar.setOnSeekBarChangeListener(this);
        mStiffnessSeekBar.setOnSeekBarChangeListener(this);
        mDampingSeekBar.setOnSeekBarChangeListener(this);

        findViewById(R.id.spring_back_button).setOnClickListener(this);
        findViewById(R.id.root_layout).setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mDownX = event.getX();
                mDownY = event.getY();

                mCircleImageView.getHitRect(mCircleHitRect);
                if (mCircleHitRect.contains((int) mDownX, (int) mDownY)) {
                    cancelFlingAnimation();
                    mIsDragging = true;
                    mOffsetX = mCircleImageView.getTranslationX();
                    mOffsetY = mCircleImageView.getTranslationY();
                    mVelocityTracker.addMovement(event);
                }
                return true;
            }

            case MotionEvent.ACTION_MOVE: {
                if (!mIsDragging) {
                    break;
                }

                mCircleImageView.setTranslationX(event.getX() - mDownX + mOffsetX);
                mCircleImageView.setTranslationY(event.getY() - mDownY + mOffsetY);
                mVelocityTracker.addMovement(event);
                return true;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                if (!mIsDragging) {
                    break;
                }
                mIsDragging = false;

                mVelocityTracker.computeCurrentVelocity(1000);
                if (mCircleImageView.getTranslationX() != 0) {
                    mFlingXAnimation = new FlingAnimation(mCircleImageView,
                            DynamicAnimation.TRANSLATION_X)
                            .setFriction(getFriction())
                            .setStartVelocity(mVelocityTracker.getXVelocity());
                    mFlingXAnimation.start();
                }
                if (mCircleImageView.getTranslationY() != 0) {
                    mFlingYAnimation = new FlingAnimation(mCircleImageView,
                            DynamicAnimation.TRANSLATION_Y)
                            .setFriction(getFriction())
                            .setStartVelocity(mVelocityTracker.getYVelocity());
                    mFlingYAnimation.start();
                }
                mVelocityTracker.clear();
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.spring_back_button) {
            cancelFlingAnimation();

            SpringAnimation animX = new SpringAnimation(mCircleImageView,
                    new FloatPropertyCompat<View>("translationX") {
                        @Override
                        public float getValue(View view) {
                            return view.getTranslationX();
                        }

                        @Override
                        public void setValue(View view, float value) {
                            view.setTranslationX(value);
                        }
                    }, 0);
            animX.getSpring().setStiffness(getStiffness());
            animX.getSpring().setDampingRatio(getDamping());
            animX.setStartVelocity(0);
            animX.start();

            SpringAnimation animY = new SpringAnimation(mCircleImageView,
                    new FloatPropertyCompat<View>("translationY") {
                        @Override
                        public float getValue(View view) {
                            return view.getTranslationY();
                        }

                        @Override
                        public void setValue(View view, float value) {
                            view.setTranslationY(value);
                        }
                    }, 0);
            animY.getSpring().setStiffness(getStiffness());
            animY.getSpring().setDampingRatio(getDamping());
            animY.setStartVelocity(0);
            animY.start();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar.getId() == R.id.friction_seek_bar) {
            mFrictionTextView.setText(getString(R.string.friction, getFriction()));
        }
        else if (seekBar.getId() == R.id.stiffness_seek_bar) {
            mStiffnessTextView.setText(getString(R.string.stiffness, getStiffness()));
        }
        else if (seekBar.getId() == R.id.damping_seek_bar) {
            mDampingTextView.setText(getString(R.string.damping, getDamping()));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    private float getFriction() {
        return Math.max(mFrictionSeekBar.getProgress() / 10f, 0.1f);
    }

    private float getStiffness() {
        return Math.max(mStiffnessSeekBar.getProgress(), SpringForce.STIFFNESS_VERY_LOW);
    }

    private float getDamping() {
        return Math.max(mDampingSeekBar.getProgress() / 10f, SpringForce.DAMPING_RATIO_HIGH_BOUNCY);
    }

    private void cancelFlingAnimation() {
        if (mFlingXAnimation != null) {
            mFlingXAnimation.cancel();
        }
        if (mFlingYAnimation != null) {
            mFlingYAnimation.cancel();
        }
    }
}
