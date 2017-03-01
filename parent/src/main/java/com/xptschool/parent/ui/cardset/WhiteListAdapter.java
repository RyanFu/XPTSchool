package com.xptschool.parent.ui.cardset;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xptschool.parent.R;
import com.xptschool.parent.common.BroadcastAction;
import com.xptschool.parent.util.CardWhiteListClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.VISIBLE;

/**
 * Created by dexing on 2017/1/11.
 * No1
 */

public class WhiteListAdapter extends BaseAdapter {

    private String[] whitelists = null;
    private Context mContext;
    private CardWhiteListClickListener mListener;
    //    private List<WhiteCardView> allCardWhite = new ArrayList<>();
    private int maxLength = 10; //最多设置号码个数
    public static HashMap<Integer, String> whiteNumbers;
    private int handlerIndex = 0;
    private boolean leftDel = false;
    private boolean rightDel = true;

    public WhiteListAdapter(Context context) {
        super();
        mContext = context;
        whitelists = new String[maxLength];
        for (int i = 0; i < maxLength; i++) {
            whitelists[i] = "";
        }
    }

    public HashMap<Integer, String> getWhiteNumbers() {
        return whiteNumbers;
    }

    public void loadData(String[] whites, CardWhiteListClickListener listener) {
        if (whites != null) {
            try {
                int length = whites.length;
                if (length > maxLength) {
                    length = maxLength;
                }
                whiteNumbers = new HashMap<Integer, String>();
                for (int i = 0; i < length; i++) {
                    whitelists[i] = whites[i];
                    whiteNumbers.put(i, whites[i]);
                }
            } catch (Exception ex) {

            }
        }
        mListener = listener;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return whitelists.length;
    }

    @Override
    public String getItem(int i) {
        return whitelists[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.content_card_whitelist, viewGroup, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.imgDel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numbers = whiteNumbers.get(position);
                String[] namNum = numbers.split(":");
                if (namNum.length > 0) {
                    if (namNum.length == 1) {
                        whiteNumbers.put(position, ":");
                    } else {
                        whiteNumbers.put(position, ":" + namNum[1]);
                    }
                    handlerIndex = position;
                    leftDel = true;
                    rightDel = false;
                    notifyDataSetChanged();
                }
            }
        });

        viewHolder.imgDel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numbers = whiteNumbers.get(position);
                String[] namNum = numbers.split(":");
                if (namNum.length > 0) {
                    whiteNumbers.put(position, namNum[0] + ":");
                    handlerIndex = position;
                    leftDel = false;
                    rightDel = true;
                    notifyDataSetChanged();
                }
            }
        });

        viewHolder.imgContract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onContractChooseClick();
                    mContext.registerReceiver(new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            if (intent.getAction().equals(BroadcastAction.WHITELIST_CONTACTS)) {
                                try {
                                    String phone = intent.getStringExtra("phone");
                                    String name = intent.getStringExtra("name");
                                    whiteNumbers.put(position, name + ":" + phone);
                                    mContext.unregisterReceiver(this);
                                    notifyDataSetChanged();
                                } catch (Exception ex) {
                                    Toast.makeText(context, R.string.toast_get_phone_null, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }, new IntentFilter(BroadcastAction.WHITELIST_CONTACTS));
                }
            }
        });

        viewHolder.edtPhoneName.setText("");
        viewHolder.edtPhone.setText("");

        try {
            viewHolder.edtPhoneName.setHint("联系人" + (position + 1));
            String nameNums = whiteNumbers.get(position);
            Log.i("WhiteCard", "getView: " + position + " value " + nameNums);

            if (nameNums.contains(":")) {
                String[] values = nameNums.split(":");
                viewHolder.edtPhoneName.setText(values[0]);
                viewHolder.edtPhone.setText(values[1]);
                if (handlerIndex == position) {
                    if (leftDel) {
                        viewHolder.edtPhoneName.requestFocus();
                    } else {
                        viewHolder.edtPhone.requestFocus();
                    }
                }
            }
        } catch (Exception ex) {
            Log.i("WhiteCard", "getView: " + position + " error: " + ex.getMessage());
        }
        if (position + 1 == getCount()) {
            viewHolder.btnOk.setVisibility(VISIBLE);
        } else {
            viewHolder.btnOk.setVisibility(View.GONE);
        }

        viewHolder.btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.sendBroadcast(new Intent(BroadcastAction.WHITELIST_OKCLICK));
            }
        });
//        clickListener = listener;

//        view = new WhiteCardView(mContext);
//        cardWhiteListView = (WhiteCardView) view;

//        cardWhiteListView.bindData(position + 1, getCount(), getItem(position), mListener);

//        if (allCardWhite.size() > position) {
//            allCardWhite.set(position, view);
//        } else {
//            allCardWhite.add(cardWhiteListView);
//        }

//        if (!allCardWhite.contains(cardWhiteListView)) {
//            allCardWhite.add(cardWhiteListView);
//        }
        return view;
    }

//    public List<WhiteCardView> getCardWhiteViews() {
//        return allCardWhite;
//    }

    class ViewHolder {

        @BindView(R.id.txtPhoneName1)
        TextView txtPhoneName1;
        @BindView(R.id.edtPhoneName)
        EditText edtPhoneName;
        @BindView(R.id.edtPhone)
        EditText edtPhone;

        @BindView(R.id.imgDel1)
        ImageView imgDel1;
        @BindView(R.id.imgDel2)
        ImageView imgDel2;
        @BindView(R.id.imgContract)
        ImageView imgContract;
        @BindView(R.id.btnOk)
        Button btnOk;

        public ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }

}
