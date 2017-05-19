package com.xptschool.teacher.ui.setting;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.bugly.beta.Beta;
import com.umeng.message.IUmengCallback;
import com.umeng.message.PushAgent;
import com.xptschool.teacher.BuildConfig;
import com.xptschool.teacher.R;
import com.xptschool.teacher.common.ExtraKey;
import com.xptschool.teacher.common.SharedPreferencesUtil;
import com.xptschool.teacher.model.GreenDaoHelper;
import com.xptschool.teacher.push.UpushTokenHelper;
import com.xptschool.teacher.ui.main.BaseActivity;
import com.xptschool.teacher.ui.main.LoginActivity;
import com.xptschool.teacher.ui.main.WebViewActivity;
import com.xptschool.teacher.view.CustomDialog;

import butterknife.BindView;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {

    @BindView(R.id.txtVersion)
    TextView txtVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setTitle(R.string.mine_setting);

        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            txtVersion.setText(info.versionName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.rlChangePwd, R.id.rlExit, R.id.rlTel, R.id.rlUpdate, R.id.rlHelp})
    void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.rlTel:
                try {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + getString(R.string.company_tel)));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception ex) {
                    Toast.makeText(this, R.string.toast_startcall_error, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.rlUpdate:
                Beta.checkUpgrade();
                break;
            case R.id.rlChangePwd:
                startActivity(new Intent(this, ChangePwdActivity.class));
                break;
            case R.id.rlExit:
                CustomDialog dialog = new CustomDialog(SettingActivity.this);
                dialog.setMessage(R.string.msg_exit);
                dialog.setAlertDialogClickListener(new CustomDialog.DialogClickListener() {
                    @Override
                    public void onPositiveClick() {
                        //清除数据
                        SharedPreferencesUtil.clearUserInfo(SettingActivity.this);
                        //清除upush信息
                        UpushTokenHelper.exitAccount();
                        GreenDaoHelper.getInstance().clearData();
                        //拒收通知
                        PushAgent mPushAgent = PushAgent.getInstance(SettingActivity.this);
                        mPushAgent.disable(new IUmengCallback() {
                            @Override
                            public void onSuccess() {
                                Log.i(TAG, "PushAgent disable onSuccess: ");
                            }

                            @Override
                            public void onFailure(String s, String s1) {
                                Log.i(TAG, "PushAgent disable onFailure: " + s + " s1 " + s1);
                            }
                        });

                        Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra(ExtraKey.LOGIN_ORIGIN, "0");
                        startActivity(intent);
                    }
                });
                break;
            case R.id.rlHelp:
                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra(ExtraKey.WEB_URL, BuildConfig.SERVICE_URL + "/html/app-help/index.html");
                startActivity(intent);
                break;
        }
    }

}