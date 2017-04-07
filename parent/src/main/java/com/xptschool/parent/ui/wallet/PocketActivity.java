package com.xptschool.parent.ui.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xptschool.parent.R;
import com.xptschool.parent.ui.main.BaseActivity;

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


}