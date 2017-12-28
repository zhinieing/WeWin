package com.project.android.wewin.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.project.android.wewin.MyApplication;
import com.project.android.wewin.R;
import com.project.android.wewin.utils.Constants;
import com.project.android.wewin.utils.Util;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.BmobUser.BmobThirdUserAuth;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

public class WXEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxentry);
        MyApplication.api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        MyApplication.api.handleIntent(intent, this);
    }

    //微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq baseReq) {
        switch (baseReq.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX://
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
                break;
            default:
                break;
        }
    }

    //第三方应用请求微信登陆的响应结果会通过该方法回调给第三方， 也就是说微信登录授权成功后，会回调到该方法
    @Override
    public void onResp(BaseResp baseResp) {
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                final String code = ((SendAuth.Resp) baseResp).code;
                // 通过拿到的code，去请求token
                new Thread() {
                    @Override
                    public void run() {
                        String result = Util.getResponse("https://api.weixin.qq.com/sns/oauth2/access_token?appid="+ Constants.WEIXIN_APP_ID+"&secret="+Constants.WEIXIN_APP_SECRET+"&code="+code+"&grant_type=authorization_code");
                        //Log.i("smile", "微信平台返回的token:" + result);
                        loginByWX(result);
                    }

                }.start();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                Toast.makeText(WXEntryActivity.this, "用户拒绝授权", Toast.LENGTH_LONG).show();
                finish();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                Toast.makeText(WXEntryActivity.this, "用户取消授权", Toast.LENGTH_LONG).show();
                finish();
                break;
            default:
                break;
        }
    }


    private void loginByWX(String result){
        JSONObject obj;
        try {
            obj = new JSONObject(result);
            String token = obj.getString("access_token");
            String expires = String.valueOf(obj.getLong("expires_in"));
            String openid = obj.getString("openid");


            BmobThirdUserAuth authInfo = new BmobThirdUserAuth(BmobThirdUserAuth.SNS_TYPE_WEIXIN, token,expires,openid);
            BmobUser.loginWithAuthData(authInfo, new LogInListener<JSONObject>() {

                @Override
                public void done(JSONObject jsonObject, BmobException e) {
                    if (e == null) {
                               
                    } else {
                        Toast.makeText(WXEntryActivity.this,
                                "第三方登陆失败：" + e.toString(), Toast.LENGTH_LONG).show();
                        startActivity(new Intent(WXEntryActivity.this,LoginActivity.class));
                    }
                }

            });
        } catch (JSONException e) {
        }
    }
}
