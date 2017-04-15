package com.xptschool.parent.ui.wallet.pocket;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.xptschool.parent.R;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.ui.main.BaseActivity;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 零钱
 */
public class PocketActivity extends BaseActivity {

    @BindView(R.id.txt_pocket_money)
    TextView txt_pocket_money;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pocket);
        setTitle(R.string.label_pocket_money);
        setTxtRight(R.string.label_recharge_detail);
        setTextRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PocketActivity.this, PocketDetailActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPocketBalance();
    }

    @OnClick({R.id.btn_recharge, R.id.btn_withdraw})
    void viewOnClick(View view) {
        switch (view.getId()) {
            case R.id.btn_recharge:
                startActivity(new Intent(this, RechargeActivity.class));
                break;
            case R.id.btn_withdraw:

                break;
        }
    }

    private void getPocketBalance() {
        BalanceUtil.getBalance(new BalanceUtil.BalanceCallBack() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess() {
                if (txt_pocket_money != null) {
                    txt_pocket_money.setText("¥ " + BalanceUtil.getParentBalance());
                }
            }

            @Override
            public void onFailed(String error) {
                if (txt_pocket_money != null) {
                    txt_pocket_money.setText("获取失败");
                }
            }
        });

    }


}
