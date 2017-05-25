package com.fidget.spinner;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Scroller;

public class MainActivity extends AppCompatActivity {
ImageView spinner;
    GestureDetectorCompat gestureDetector;
    private Scroller mScroller;
    private ValueAnimator mScrollAnimator;
    int spinnerRotation;

    public static final int FLING_VELOCITY_DOWNSCALE = 6;


    public static final int AUTOCENTER_ANIM_DURATION = 250;
    private ObjectAnimator mAutoCenterAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RotateAnimation rotateAnimation;
       spinner = (ImageView) findViewById(R.id.spinner);
        GestureListener mListener = new GestureListener();
        gestureDetector = new GestureDetectorCompat(this,mListener);
        spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                   gestureDetector.onTouchEvent(event);
                return true;
            }
        });
        mScroller = new Scroller(this, new AnticipateOvershootInterpolator(), true);
        mScrollAnimator = ValueAnimator.ofFloat(0,1);
        mScrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (mScroller.computeScrollOffset()) {
                   System.out.println(mScroller.getFinalY() );
                    spinner.setRotation(mScroller.getCurrY());
                } else {
                    mScrollAnimator.cancel();
                }
            }
        });


    }


    class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float scrollTheta = vectorToScalarScroll(
                    velocityX,
                    velocityY,
                    e2.getX() - spinner.getWidth()/2,
                    e2.getY() - spinner.getHeight()/2);

            mScroller.fling(0,(int) getPieRotation(), 0, (int) scrollTheta / FLING_VELOCITY_DOWNSCALE, 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
            mScroller.setFriction(0.001f);
            if (Build.VERSION.SDK_INT >= 11) {
                mScrollAnimator.setDuration(mScroller.getDuration());
                mScrollAnimator.start();
            }
            return true;
        }

       /* @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float scrollTheta = vectorToScalarScroll(
                    distanceX,
                    distanceY,
                    e2.getX() - spinner.getWidth()/2 ,
                    e2.getY() - spinner.getHeight()/2);
            setPieRotation(getPieRotation() - (int) distanceY / FLING_VELOCITY_DOWNSCALE);
            return true;
        }*/

    }
    public int getPieRotation() {
        return spinnerRotation;
    }
    public void setPieRotation(int rotation) {
        rotation = (rotation % 360 + 360) % 360;

        spinner.setRotation(rotation);

        //calcCurrentItem();
    }
    private static float vectorToScalarScroll(float dx, float dy, float x, float y) {
        // get the length of the vector
        float l = (float) Math.sqrt(dx * dx + dy * dy);

        // decide if the scalar should be negative or positive by finding
        // the dot product of the vector perpendicular to (x,y).
        float crossX = -y;
        float crossY = x;

        float dot = (crossX * dx + crossY * dy);
        float sign = Math.signum(dot);

        return l * sign;
    }

}
