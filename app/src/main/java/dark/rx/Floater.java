package dark.rx;

import android.animation.ArgbEvaluator;
import android.animation.TimeAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Floater extends Service {
    public View mFloatingView;
    private Button close;
    private Button kill;
    private LinearLayout mButtonPanel;
    public RelativeLayout mCollapsed;
    public LinearLayout mExpanded;
    private RelativeLayout mRootContainer;
    public WindowManager mWindowManager;
    public WindowManager.LayoutParams params;
    private LinearLayout patches;
    private FrameLayout rootFrame;
    private ImageView startimage;

    private GradientDrawable gdAnimation = new GradientDrawable();

    private native String Icon();

    public native void Changes(int feature, int value, Context ctx);

    private native void StartOptionsDark(Context ctx, TextView tx, TextView tx2);

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startAnimation();
        initFloating();
    }

    private void startAnimation() {
        final int start = Color.RED;
        final int end = Color.BLACK;

        final ArgbEvaluator evaluator = new ArgbEvaluator();
        gdAnimation.setOrientation(GradientDrawable.Orientation.TL_BR);
        final GradientDrawable gradient = gdAnimation;

        ValueAnimator animator = TimeAnimator.ofFloat(0.0f, 1.0f);
        animator.setDuration(5000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Float fraction = valueAnimator.getAnimatedFraction();
                int newStrat = (int) evaluator.evaluate(fraction, start, end);
                int newEnd = (int) evaluator.evaluate(fraction, end, start);
                int[] newArray = {newStrat, newEnd};
                gradient.setColors(newArray);
            }
        });

        animator.start();
    }

    private void initFloating() {
        rootFrame = new FrameLayout(getBaseContext());
        mRootContainer = new RelativeLayout(getBaseContext());
        mCollapsed = new RelativeLayout(getBaseContext());
        mExpanded = new LinearLayout(getBaseContext());
        patches = new LinearLayout(getBaseContext());
        mButtonPanel = new LinearLayout(getBaseContext());

        RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(-2, -1));
        relativeLayout.setPadding(3, 0, 3, 3);
        relativeLayout.setVerticalGravity(16);

        RelativeLayout.LayoutParams DARK = new RelativeLayout.LayoutParams(-2, dp(40));
        kill = new Button(this);
        kill.setBackgroundColor(Color.RED);
        kill.setText("HIDE");
        kill.setTextColor(Color.WHITE);
        kill.setLayoutParams(DARK);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, dp(40));
        layoutParams.addRule(11);


        close = new Button(this);
        close.setBackgroundColor(Color.RED);
        close.setText("CLOSE");
        close.setTextColor(Color.WHITE);
        close.setLayoutParams(layoutParams);

        relativeLayout.addView(kill);
        relativeLayout.addView(close);

        rootFrame.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        mRootContainer.setLayoutParams(new FrameLayout.LayoutParams(-2, -2));
        mCollapsed.setLayoutParams(new RelativeLayout.LayoutParams(-2, -2));
        mCollapsed.setVisibility(View.VISIBLE);
        startimage = new ImageView(getBaseContext());
        startimage.setLayoutParams(new RelativeLayout.LayoutParams(-2, -2));
        int applyDimension = (int) TypedValue.applyDimension(1, (float) 60, getResources().getDisplayMetrics());
        startimage.getLayoutParams().height = applyDimension;
        startimage.getLayoutParams().width = applyDimension;
        startimage.requestLayout();
        startimage.setScaleType(ImageView.ScaleType.FIT_XY);
        byte[] decode = Base64.decode(Icon(), 0);
        startimage.setImageBitmap(BitmapFactory.decodeByteArray(decode, 0, decode.length));
        startimage.setImageAlpha(200);
        ((ViewGroup.MarginLayoutParams) startimage.getLayoutParams()).topMargin = convertDipToPixels(10);

        mExpanded.setVisibility(View.GONE);
        mExpanded.setBackgroundColor(Color.BLACK);
        mExpanded.setAlpha(0.95f);
        mExpanded.setGravity(17);
        mExpanded.setOrientation(LinearLayout.VERTICAL);
        mExpanded.setPadding(0, 0, 0, 0);

        mExpanded.setLayoutParams(new LinearLayout.LayoutParams(dp(210), -2));

        ScrollView scrollView = new ScrollView(getBaseContext());
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(-1, dp(200)));
        scrollView.setBackground(gdAnimation);

        patches.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        patches.setOrientation(LinearLayout.VERTICAL);

        mButtonPanel.setLayoutParams(new LinearLayout.LayoutParams(-2, -2));

        //Title text
        TextView textView = new TextView(getBaseContext());
        textView.setTextColor(Color.RED);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setTextSize(20.0f);
        textView.setPadding(0, 10, 0, 5);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-2, -2);
        layoutParams2.gravity = 17;
        //textView.setLayoutParams(layoutParams2);

        //Heading text
        TextView textView2 = new TextView(getBaseContext());
        textView2.setTextColor(Color.RED);
        textView2.setTypeface(Typeface.DEFAULT_BOLD);
        textView2.setTextSize(10.0f);
        textView2.setPadding(10, 5, 10, 10);

        LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(-2, -2);
        layoutParams3.gravity = 17;
        textView.setLayoutParams(layoutParams2);
        textView2.setLayoutParams(layoutParams3);
        new LinearLayout.LayoutParams(-1, dp(25)).topMargin = dp(2);
        rootFrame.addView(mRootContainer);
        mRootContainer.addView(mCollapsed);
        mRootContainer.addView(mExpanded);

        mCollapsed.addView(startimage);

        mExpanded.addView(textView);
        mExpanded.addView(textView2);
        mExpanded.addView(scrollView);
        scrollView.addView(patches);
        mExpanded.addView(relativeLayout);
        mFloatingView = rootFrame;
        if (Build.VERSION.SDK_INT >= 26) {
            params = new WindowManager.LayoutParams(-2, -2, 2038, 8, -3);
        } else {
            params = new WindowManager.LayoutParams(-2, -2, 2002, 8, -3);
        }
        WindowManager.LayoutParams layoutParams4 = params;
        layoutParams4.gravity = 51;
        layoutParams4.x = 0;
        layoutParams4.y = 100;
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);
        RelativeLayout relativeLayout2 = mCollapsed;
        LinearLayout linearLayout = mExpanded;
        mFloatingView.setOnTouchListener(onTouchListener());
        startimage.setOnTouchListener(onTouchListener());
        initMenuButton(relativeLayout2, linearLayout);
        StartOptionsDark(this, textView, textView2);
    }

    public InterfaceInt INT(int z1){
        return new InterfaceInt() {
            @Override
            public void OnWrite(int i) {
                Changes(z1, i, Floater.this);
            }
        };
    }

    public InterfaceBool BOOL(int z2){
        return new InterfaceBool() {
            @Override
            public void OnWrite(boolean z) {
                Changes(z2, 0, Floater.this);
            }
        };
    }

    public InterfaceBtn BTN(int i1){
        return new InterfaceBtn() {
            @Override
            public void OnWrite() {
                Changes(i1, 0, Floater.this);
            }
        };
    }

    private View.OnTouchListener onTouchListener() {
        return new View.OnTouchListener() {
            final View collapsedView = mCollapsed;
            final View expandedView = mExpanded;
            private float initialTouchX;
            private float initialTouchY;
            private int initialX;
            private int initialY;

            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = motionEvent.getRawX();
                        initialTouchY = motionEvent.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        int rawX = (int) (motionEvent.getRawX() - initialTouchX);
                        int rawY = (int) (motionEvent.getRawY() - initialTouchY);

                        if (rawX < 10 && rawY < 10 && isViewCollapsed()) {
                            collapsedView.setVisibility(View.GONE);
                            expandedView.setVisibility(View.VISIBLE);
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + ((int) (motionEvent.getRawX() - initialTouchX));
                        params.y = initialY + ((int) (motionEvent.getRawY() - initialTouchY));

                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                    default:
                        return false;
                }
            }
        };
    }

    private void initMenuButton(final View view2, final View view3) {
        startimage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                view2.setVisibility(View.GONE);
                view3.setVisibility(View.VISIBLE);
            }
        });
        kill.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Floater.stopSelf();
                // view2.setVisibility(View.VISIBLE)
                view2.setVisibility(View.VISIBLE);
                view2.setAlpha(0);
                view3.setVisibility(View.GONE);
                Toast.makeText(view.getContext(), "Icon hidden. Remember the hidden icon position", Toast.LENGTH_LONG).show();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                view2.setVisibility(View.VISIBLE);
                view2.setAlpha(0.95f);
                view3.setVisibility(View.GONE);
            }
        });
    }

    public void addCategory(String text) {
        TextView textView = new TextView(this);
        textView.setBackgroundColor(0);
        textView.setText(text);
        textView.setGravity(17);
        textView.setTextSize(14.0f);
        textView.setTextColor(Color.WHITE);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setPadding(10, 5, 0, 5);
        patches.addView(textView);
    }

    public void addButton(String feature, final InterfaceBtn interfaceBtn) {
        final Button button = new Button(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -1);
        layoutParams.setMargins(7, 5, 7, 5);
        button.setLayoutParams(layoutParams);
        button.setPadding(10, 5, 10, 5);
        button.setTextSize(13.0f);
        button.setGravity(17);
        button.setTextColor(Color.WHITE);
        button.setBackgroundColor(0);

        button.setText(feature + ": OFF");
        final String feature2 = feature;
        button.setOnClickListener(new View.OnClickListener() {
            private boolean isActive = true;

            public void onClick(View v) {
                interfaceBtn.OnWrite();
                if (isActive) {
                    button.setText(feature2 + ": ON");
                    button.setBackgroundColor(Color.RED);
                    isActive = false;
                    return;
                }
                button.setText(feature2 + ": OFF");
                button.setBackgroundColor(0);
                isActive = true;
            }
        });
        patches.addView(button);
    }

    public void addSwitch(String feature, final InterfaceBool sw) {
        Switch switchR = new Switch(this);
        switchR.setText(Html.fromHtml("<font face='roboto'>" + feature + "</font>"));
        switchR.setTextColor(Color.WHITE);
        switchR.setPadding(10, 5, 0, 5);
        switchR.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                if (z) {
                } else {
                }
                sw.OnWrite(z);
            }
        });
        patches.addView(switchR);
    }

    public void addSeekBar(final String feature, final int prog, int max, final InterfaceInt interInt) {
        LinearLayout linearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -1);
        linearLayout.setPadding(10, 5, 0, 5);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(17);
        linearLayout.setLayoutParams(layoutParams);
        final TextView textView = new TextView(this);
        textView.setText(Html.fromHtml("<font face='roboto'>" + feature + ": <font color='#FF0000'>" + prog + "</font>"));
        textView.setTextColor(Color.parseColor("#DEEDF6"));
        SeekBar seekBar = new SeekBar(this);
        seekBar.setPadding(25, 10, 35, 10);
        seekBar.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        seekBar.setMax(max);
        seekBar.setProgress(prog);

        final TextView textView2 = textView;
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            int l;

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                if (l < i) {
                } else {
                }
                l = i;

                if (i < prog) {
                    seekBar.setProgress(prog);
                    interInt.OnWrite(prog);
                    TextView textView = textView2;
                    textView.setText(Html.fromHtml("<font face='roboto'>" + feature + ": <font color='#FF0000'>" + prog + "</font>"));
                    return;
                }
                interInt.OnWrite(i);
                textView.setText(Html.fromHtml("<font face='roboto'>" + feature + ": <font color='#FF0000'>" + i + "</font>"));
            }
        });

        linearLayout.addView(textView);
        linearLayout.addView(seekBar);
        patches.addView(linearLayout);
    }

    public boolean isViewCollapsed() {
        return mFloatingView == null || mCollapsed.getVisibility() == View.VISIBLE;
    }

    private int convertDipToPixels(int i) {
        return (int) ((((float) i) * getResources().getDisplayMetrics().density) + 0.5f);
    }

    private int dp(int i) {
        return (int) TypedValue.applyDimension(1, (float) i, getResources().getDisplayMetrics());
    }

    public void onDestroy() {
        super.onDestroy();
        View view = mFloatingView;
        if (view != null) {
            mWindowManager.removeView(view);
        }
    }

    public void onTaskRemoved(Intent intent) {
        stopSelf();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.onTaskRemoved(intent);
    }

    private interface InterfaceBtn {
        void OnWrite();
    }

    private interface InterfaceInt {
        void OnWrite(int i);
    }

    private interface InterfaceBool {
        void OnWrite(boolean z);
    }
}