package com.jiajunhui.multiscreen_udp_terminal;

import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jiajunhui.multiscreen_udp_terminal.bean.BasePackage;
import com.jiajunhui.multiscreen_udp_terminal.utils.GsonTools;
import com.xapp.jjh.xui.activity.TopBarActivity;
import com.xapp.jjh.xui.inter.MenuType;

import java.net.SocketAddress;

public class MainActivity extends TopBarActivity {

    private EditText mEtContent;
    private Button mBtnSend;
    private TextView mTvDialog;
    private boolean hasConnected;

    @Override
    public View getContentView(LayoutInflater layoutInflater, ViewGroup container) {
        return inflate(R.layout.activity_main);
    }

    @Override
    public void findViewById() {
        mEtContent = findView(R.id.et_contet);
        mBtnSend = findView(R.id.btn_send);
        mTvDialog = findView(R.id.tv_dialog);
    }

    @Override
    public void initData() {
        setTopBarTitle("MultiScreenTerminal");
        setMenuType(MenuType.TEXT,R.string.menu_main);
        setSwipeBackEnable(false);
        setNavigationVisible(false);
        startSearDevices();
    }

    @Override
    public void setListener() {
        super.setListener();
        mBtnSend.setOnClickListener(this);
        mTvDialog.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.btn_send:
                if(hasConnected){
                    String content = mEtContent.getText().toString();
                    if(TextUtils.isEmpty(content)){
                        showSnackBar("请输入内容",null,null);
                        return;
                    }
                    BasePackage basePackage = new BasePackage();
                    basePackage.setEventCode(EventContants.EVENT_CODE_INPUT);
                    basePackage.setDeviceInfo(Build.MODEL);
                    basePackage.setEventMessage(content);
                    sendMessage(basePackage);
                }else{
                    showSnackBar("未连接设备",null,null);
                }
                break;

            case R.id.tv_dialog:
                BasePackage basePackage = new BasePackage();
                basePackage.setEventCode(EventContants.EVENT_CODE_DIALOG);
                basePackage.setDeviceInfo(Build.MODEL);
                basePackage.setEventMessage("请求弹窗");
                sendMessage(basePackage);
                break;
        }
    }

    private void sendMessage(final BasePackage basePackage) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                ReceiveUDP.sendUDP(GsonTools.createGsonString(basePackage));
            }
        }.start();
    }

    private void startSearDevices() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                ReceiveUDP.listenUDP(new ReceiveUDP.OnUDPListener() {
                    @Override
                    public void onReceive(BasePackage basePackage, SocketAddress socketAddress) {
                        if(basePackage.getEventCode() == EventContants.EVENT_CODE_DEVICE_AUTH){
                            hasConnected = true;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((TextView)getMenuView()).setTextColor(Color.BLUE);
                                    setMenuText("已连接");
                                }
                            });

                        }
                    }
                });
            }
        }.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ReceiveUDP.stopListener();
    }
}
