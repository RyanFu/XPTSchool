package com.xptschool.parent.ui.wallet.pocket;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.android.widget.spinner.MaterialSpinner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xptschool.parent.R;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.HttpErrorMsg;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.ui.main.BaseActivity;
import com.xptschool.parent.ui.wallet.bankcard.BankListActivity;
import com.xptschool.parent.ui.wallet.bankcard.BeanBankCard;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 提现
 */
public class TakeOutMoneyActivity extends BaseActivity {

    @BindView(R.id.spnBankCard)
    MaterialSpinner spnBankCard;

    @BindView(R.id.txt_rest_money)
    TextView txt_rest_money;

    @BindView(R.id.edt_money)
    EditText edt_money;

    @BindView(R.id.btn_takeout)
    Button btn_takeout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_out_money);
        setTitle(R.string.label_takeout);
        getBankList();

        txt_rest_money.setText(BalanceUtil.getParentBalance() + "");
    }

    @OnClick({R.id.btn_takeout})
    void viewOnClick(View view) {
        switch (view.getId()) {
            case R.id.btn_takeout:
                String money = edt_money.getText().toString().trim();
                if (money.isEmpty()) {
                    Toast.makeText(this, "请输入提款金额", Toast.LENGTH_SHORT).show();
                }
                BeanBankCard bankCard = (BeanBankCard) spnBankCard.getSelectedItem();
                takeoutMoney(money, bankCard.getId());
                break;
        }
    }

    private void getBankList() {

        VolleyHttpService.getInstance().sendPostRequest(HttpAction.GET_BankCards, new VolleyHttpParamsEntity()
                .addParam("token", CommonUtil.encryptToken(HttpAction.GET_BankCards)), new MyVolleyRequestListener() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onResponse(VolleyHttpResult volleyHttpResult) {
                super.onResponse(volleyHttpResult);
                switch (volleyHttpResult.getStatus()) {
                    case HttpAction.SUCCESS:
                        try {
                            Gson gson = new Gson();
                            List<BeanBankCard> bankCards = gson.fromJson(volleyHttpResult.getData().toString(),
                                    new TypeToken<List<BeanBankCard>>() {
                                    }.getType());

                            if (bankCards.size() == 0) {
                                spnBankCard.setItems("未绑定银行卡");
                                btn_takeout.setEnabled(false);
                            } else {
                                spnBankCard.setItems(bankCards);
                            }

                        } catch (Exception ex) {
                            Log.i(TAG, "onResponse: " + ex.getMessage());
                        }
                        break;
                    default:
                        Toast.makeText(TakeOutMoneyActivity.this, volleyHttpResult.getInfo(), Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
            }
        });
    }

    private void takeoutMoney(String money, String bankId) {
        VolleyHttpService.getInstance().sendPostRequest(HttpAction.REFUND_ADD, new VolleyHttpParamsEntity()
                .addParam("money", money)
                .addParam("memo", "零钱提款")
                .addParam("bank_id", bankId)
                .addParam("token", CommonUtil.encryptToken(HttpAction.REFUND_ADD)), new MyVolleyRequestListener() {
            @Override
            public void onStart() {
                super.onStart();
                showProgress(R.string.progress_loading_cn);
            }

            @Override
            public void onResponse(VolleyHttpResult volleyHttpResult) {
                super.onResponse(volleyHttpResult);
                hideProgress();
                Toast.makeText(TakeOutMoneyActivity.this, volleyHttpResult.getInfo(), Toast.LENGTH_SHORT).show();
                if (volleyHttpResult.getStatus() == HttpAction.SUCCESS) {
                    finish();
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                hideProgress();
            }
        });
    }
}
