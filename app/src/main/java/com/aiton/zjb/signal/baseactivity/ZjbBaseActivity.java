package com.aiton.zjb.signal.baseactivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.telephony.gsm.SmsManager;
import android.view.KeyEvent;

import com.aiton.administrator.shane_library.shane.app.SysApplication;
import com.aiton.administrator.shane_library.shane.utils.NetChecker;
import com.aiton.administrator.shane_library.shane.widget.MyCarDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.umeng.analytics.MobclickAgent;


public abstract class ZjbBaseActivity extends Activity {

    //    private ProgressDialog mProgressDialog;
    private MyCarDialog mMyCarDialog;
    public static String MYBROADCAST = "MyBroadcast";
    public static String CANCELORDER = "cancelOrder";

    private BroadcastReceiver reciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
        }
    };
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (VERSION.SDK_INT >= 14) {
            //禁止横屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    public void init() {
        //添加当前界面到容器中
        SysApplication.getInstance().addActivity(this);
        initSP();
        initIntent();
        initData();
        findID();
        initViews();
        setListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ZjbBaseActivity.MYBROADCAST);
        registerReceiver(reciver,filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //友盟统计
        MobclickAgent.onPageStart(getRunningActivityName());
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getRunningActivityName());
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(reciver);
        super.onDestroy();
    }

    private String getRunningActivityName() {
        String contextString = this.toString();
        try {
            return contextString.substring(contextString.lastIndexOf(".") + 1, contextString.indexOf("@"));
        } catch (Exception e) {
        }
        return "";
    }

    public void showLoadingDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new MyCarDialog(this);
            mProgressDialog.show();
            mProgressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                        finishTo();
                    }
                    return false;
                }
            });
        }
    }

    public void showLoadingDialog(final AsyncHttpClient asyncHttpClient, final Context context) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.show();
            mProgressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                        asyncHttpClient.cancelRequests(context,true);
                        mProgressDialog.dismiss();
                    }
                    return false;
                }
            });
        }
    }

    public void cancelLoadingDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    protected abstract void initIntent();

    protected abstract void initSP();

    protected abstract void initData();

    protected abstract void findID();

    protected abstract void initViews();

    protected abstract void setListeners();

    /**
     * 判断网络是否连接
     *
     * @return true or false
     */
    protected boolean isNetAvailable() {
        NetChecker nc = NetChecker.getInstance(this.getApplicationContext());
        if (nc.isNetworkOnline()) {
            return true;
        } else {
            return false;
        }
    }

    protected void sendSms(String mobile, String msg) {
        SmsManager smsManager = SmsManager.getDefault();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        smsManager.sendTextMessage(mobile, null, msg, null, null);
    }

    public void startActivityTo(Intent intent) {
        startActivity(intent);
        overridePendingTransition(com.aiton.administrator.shane_library.R.anim.my_scale_action, com.aiton.administrator.shane_library.R.anim.my_alpha_action);//放大淡出效果
    }

    public void startActivityForResultTo(Intent intent, int result) {
        startActivityForResult(intent, result);
        overridePendingTransition(com.aiton.administrator.shane_library.R.anim.my_scale_action, com.aiton.administrator.shane_library.R.anim.my_alpha_action);//放大淡出效果
    }

    public void finishTo() {
        finish();
        overridePendingTransition(com.aiton.administrator.shane_library.R.anim.slide_up_in, com.aiton.administrator.shane_library.R.anim.slide_down_out);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(com.aiton.administrator.shane_library.R.anim.slide_up_in, com.aiton.administrator.shane_library.R.anim.slide_down_out);
    }
}
