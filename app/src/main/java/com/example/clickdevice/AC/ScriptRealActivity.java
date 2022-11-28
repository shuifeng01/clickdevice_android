package com.example.clickdevice.AC;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;

import com.Ohuang.ilivedata.LiveDataBus;
import com.example.clickdevice.MyService;
import com.example.clickdevice.PowerKeyObserver;
import com.example.clickdevice.R;
import com.example.clickdevice.ScriptExecutor;
import com.example.clickdevice.SmallWindowView;
import com.example.clickdevice.Util;
import com.example.clickdevice.bean.ScriptCmdBean;
import com.example.clickdevice.dialog.DialogHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScriptRealActivity extends AppCompatActivity implements ScriptExecutor.ScriptInterFace {
    private int OVERLAY_PERMISSION_REQ_CODE = 2;
    private WindowManager.LayoutParams btn_layoutParams;
    private SmallWindowView btn_windowView;
    private Button btn_script_openScript;

    /* access modifiers changed from: private */
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                ScriptRealActivity.this.isRun = false;
                //ScriptRealActivity.this.tv_bw.setText("开始");
            }
        }
    };
    /* access modifiers changed from: private */
    public volatile boolean isRun = false;
    private boolean isShow = false;
    /* access modifiers changed from: private */
    public List<ScriptCmdBean> mData;
    private MyService myService;
    /* access modifiers changed from: private */
    public int num;
    private String thisPkgName = "";
    private String pkgNameNow = "";
    private boolean checkAppChange = false;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            for (int j = 0; j < ScriptRealActivity.this.num; j++) {

                scriptExecutor.run(mData);

                int i2 = 0;
                while (i2 < ScriptRealActivity.this.time / 100) {
                    if (ScriptRealActivity.this.isRun) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e2) {
                            e2.printStackTrace();
                        }
                        i2++;
                    } else {
                        return;
                    }
                }
                try {
                    Thread.sleep((long) (ScriptRealActivity.this.time % 100));
                } catch (InterruptedException e3) {
                    e3.printStackTrace();
                }
            }
            ScriptRealActivity.this.handler.sendEmptyMessage(0);
        }
    };

    /* access modifiers changed from: private */
    public ScriptExecutor scriptExecutor;
    private ExecutorService singleThreadExecutor;
    /* access modifiers changed from: private */
    public int time;
    /* access modifiers changed from: private */
    private WindowManager wm;
    private String json;
    private PowerKeyObserver powerKeyObserver;//检测电源键是否被按下
    private Observer<String> observer;

    /* access modifiers changed from: protected */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_real_script);
        initSmallViewLayout();
        this.singleThreadExecutor = Executors.newSingleThreadExecutor();
        if (this.btn_windowView != null) {
            initBtnWindowsView();
        }
        btn_script_openScript = findViewById(R.id.btn_script_openScript);
        this.scriptExecutor = new ScriptExecutor(this);
        initEvent();

        powerKeyObserver = new PowerKeyObserver(this);
        powerKeyObserver.startListen();//h开始注册广播
        powerKeyObserver.setHomeKeyListener(new PowerKeyObserver.OnPowerKeyListener() {
            @Override
            public void onPowerKeyPressed() {
                handler.sendEmptyMessage(0);
            }
        });
    }

    private void initEvent() {
        LiveDataBus.get().with("json", String.class).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

            }
        });
        LiveDataBus.get().with("scriptName", String.class).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (TextUtils.isEmpty(s)) {
                } else {
                }
            }
        });
        if (MyService.isStart()) {
            observer= s -> {
                Log.d("TAG123", "onChanged: " + s);
                pkgNameNow = s;
                if (checkAppChange) {
                    if (!pkgNameNow.equals(thisPkgName)) {
                        if (isRun) {
                            isRun = false;
                            handler.sendEmptyMessage(0);
                        }
                    }
                }
            };
            MyService.myService.pkgNameMutableLiveData.observeForever(observer);
        }
    }

    public void startScriptWindow(View view) {
        if (!this.isShow) {
            showFloatWindows(btn_script_openScript);
        } else {
            hideFloatWindows(btn_script_openScript);
        }
    }

    private void showFloatWindows(Button button) {
        alertWindow();
        this.isShow = true;
        button.setText("关闭悬浮窗");
    }

    private void hideFloatWindows(Button button) {
        this.isShow = false;
        button.setText("打开脚本");
        dismissWindow();
    }

    public void selectScript(View view) {
        startActivity(new Intent(this, ScriptListActivity.class));
        hideFloatWindows(btn_script_openScript);
    }

    private void initBtnWindowsView() {
        TextView recordList = (TextView) this.btn_windowView.findViewById(R.id.tv_record_list);
        TextView record = (TextView) this.btn_windowView.findViewById(R.id.tv_record);
        recordList.setOnClickListener(v -> {
            DialogHelper.RecordListDialogShow(this, v1 -> {
                //test--         start

            });
        });
        record.setOnClickListener(v -> {
            Toast.makeText(this,"开始录制",Toast.LENGTH_SHORT).show();
        });
    }


    public void alertWindow() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (Build.VERSION.SDK_INT >= 26) {
                this.btn_layoutParams.type = 2038;
            }
            requestDrawOverLays();
        } else if (Build.VERSION.SDK_INT >= 21) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.SYSTEM_ALERT_WINDOW"}, 1);
        }
    }

    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestDrawOverLays() {
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "can not DrawOverlays", 0).show();
            startActivityForResult(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + getPackageName())), this.OVERLAY_PERMISSION_REQ_CODE);
            return;
        }
        showWindow();

    }

    public void showWindow() {
        if (this.wm != null && this.btn_windowView.getWindowId() == null) {
            this.wm.addView(this.btn_windowView, this.btn_layoutParams);
        }

    }

    public void dismissWindow() {
        LinearLayout linearLayout;
        if (this.wm != null && (linearLayout = this.btn_windowView) != null && linearLayout.getWindowId() != null) {
            this.wm.removeView(this.btn_windowView);
        }
    }

    @SuppressLint("WrongConstant")
    public void initSmallViewLayout() {
        this.btn_windowView = (SmallWindowView) LayoutInflater.from(this).inflate(R.layout.window_record, (ViewGroup) null);
        this.wm = (WindowManager) getSystemService("window");
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-2, -2, 2003, 8, -3);
        this.btn_layoutParams = layoutParams;
        layoutParams.gravity = 49;
        this.btn_windowView.setWm(this.wm);
        this.btn_windowView.setWmParams(this.btn_layoutParams);
    }

    @Override
    public void delayedCmd(int delayed) throws InterruptedException {
        for (int i = 0; i < delayed / 10 && this.isRun; i++) {
            Thread.sleep(10);
        }
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void clickCMD(int x0, int y0, int duration) throws InterruptedException {
        if (this.isRun) {
            if (this.myService == null) {
                this.myService = MyService.myService;
            }
            MyService myService2 = this.myService;
            if (myService2 == null) {
                return;
            }
            if (duration < 50) {
                myService2.dispatchGestureClick((float) x0, (float) y0);
                Thread.sleep(50);
            } else if (duration < 30000) {
                myService2.dispatchGestureClick((float) x0, (float) y0, duration);
                Thread.sleep(duration);
            } else {
                myService2.dispatchGestureClick((float) x0, (float) y0, 30000);
                Thread.sleep(30000);
            }
        }
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void gestureCMD(int x0, int y0, int x1, int y1, int duration) throws InterruptedException {
        if (this.isRun) {
            if (this.myService == null) {
                this.myService = MyService.myService;
            }
            MyService myService2 = this.myService;
            if (myService2 == null) {
                return;
            }
            if (duration > 30000) {
                myService2.dispatchGesture((float) x0, (float) y0, (float) x1, (float) y1, 30000);
                Thread.sleep(30000);
                return;
            }
            if (duration < 200) {
                myService2.dispatchGesture((float) x0, (float) y0, (float) x1, (float) y1, 200);
                Thread.sleep(200);
                return;
            }
            myService2.dispatchGesture((float) x0, (float) y0, (float) x1, (float) y1, duration);
            Thread.sleep(duration);
        }
    }

    /* access modifiers changed from: protected */
    @Override
    public void onDestroy() {
        if(MyService.isStart()) {
            MyService.myService.pkgNameMutableLiveData.removeObserver(observer);
        }
        powerKeyObserver.stopListen();
        super.onDestroy();
    }
}