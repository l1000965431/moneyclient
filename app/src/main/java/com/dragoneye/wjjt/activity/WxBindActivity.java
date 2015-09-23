package com.dragoneye.wjjt.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dragoneye.wjjt.R;
import com.dragoneye.wjjt.activity.base.BaseActivity;
import com.dragoneye.wjjt.application.MyApplication;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;

/**
 * Created by happysky on 15-9-17.
 */
public class WxBindActivity extends BaseActivity {

    public static void CallActivity(Activity activity){
        Intent intent = new Intent(activity, WxBindActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setIsNeedLoadingFeature(true);
        setContentView(R.layout.activity_wx_bind);

        View gotoBind = findViewById(R.id.wx_bind_tv_goto_bind);
        gotoBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                WXTextObject textObject = new WXTextObject();
//                textObject.text = "一条测试";
//
//                WXMediaMessage mediaMessage = new WXMediaMessage();
//                mediaMessage.mediaObject = textObject;
//                mediaMessage.description = "一条测试";
//
//                SendMessageToWX.Req req = new SendMessageToWX.Req();
//                req.transaction = String.valueOf(System.currentTimeMillis());
//                req.message = mediaMessage;
//                req.scene = SendMessageToWX.Req.;
//
//                        ((MyApplication) getApplication()).getWXAPI().sendReq(req);
                Intent intent = new Intent();
                ComponentName cmp = new ComponentName("com.tencent.mm","com.tencent.mm.ui.LauncherUI");
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setComponent(cmp);
                startActivity(intent);
                finish();
            }
        });
    }
}
