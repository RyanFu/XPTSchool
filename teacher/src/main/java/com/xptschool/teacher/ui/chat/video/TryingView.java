package com.xptschool.teacher.ui.chat.video;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cjt2325.cameralibrary.JCameraView;
import com.cjt2325.cameralibrary.listener.ErrorListener;
import com.xptschool.teacher.R;
import com.xptschool.teacher.model.ContactParent;
import com.xptschool.teacher.model.GreenDaoHelper;
import com.xptschool.teacher.server.SyncHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by dexing on 2017/6/14.
 * No1
 */

public class TryingView extends LinearLayout {

    private String TAG = TryingView.class.getSimpleName();
    @BindView(R.id.view_call_trying_imageView_avatar)
    public ImageView imageView_avatar;
    @BindView(R.id.view_call_trying_textView_name)
    public TextView tvRemote;
    @BindView(R.id.view_call_trying_textView_info)
    public TextView mTvInfo;

    @BindView(R.id.llHangUp)
    LinearLayout llHangUp;
    @BindView(R.id.txt_cancel)
    TextView txt_cancel;
    @BindView(R.id.llAccept)
    LinearLayout llAccept;

    @BindView(R.id.mMainLayout)
    FrameLayout mMainLayout;
    @BindView(R.id.jcameraview)
    JCameraView jcameraview;

    private tryingClickListener mTryingClickListener;

    public TryingView(Context context, ErrorListener cameraError) {
        this(context, null, cameraError);
    }

    public TryingView(Context context, @Nullable AttributeSet attrs, ErrorListener cameraError) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.view_call_trying, this, true);
        ButterKnife.bind(this);
        jcameraview.setErrorListener(cameraError);
    }

    public void onReOpenCamera() {
        if (jcameraview != null) {
            Log.i(TAG, "onReOpenCamera openCamera: ");
            jcameraview.openCamera();
        } else {
            Log.i(TAG, "onReOpenCamera jcameraview is null");
        }
    }

    public void setTryingClickListener(tryingClickListener mTryingClickListener) {
        this.mTryingClickListener = mTryingClickListener;
    }

    public void isInCallingView(boolean isInCall) {
        if (isInCall) {
            mMainLayout.removeView(jcameraview);
        } else {
            llAccept.setVisibility(GONE);
            txt_cancel.setText(R.string.btn_cancel);
        }
    }

    public void setCallingParent(ContactParent contactParent) {
        Log.i(TAG, "setCallingParent: ");
        if (contactParent != null) {
            Log.i(TAG, "setCallingParent: " + contactParent.toString());
            tvRemote.setText(contactParent.getName());
            if (contactParent.getSex().equals("1")) {
                imageView_avatar.setImageResource(R.drawable.parent_father);
            } else {
                imageView_avatar.setImageResource(R.drawable.parent_mother);
            }
        } else {
            tvRemote.setText("未知联系人");
        }
    }

    public void setParentId(final String parentUId, final IncomingParentCallBack callBack) {
        Log.i(TAG, "setParentId: " + parentUId);
        ContactParent parent = GreenDaoHelper.getInstance().getStudentParentByPUId(parentUId);
        if (parent == null) {
            SyncHelper.getInstance().syncContacts(new SyncHelper.SyncCallBack() {
                @Override
                public void onSyncSuccess() {
                    Log.i(TAG, "onSyncSuccess: " + parentUId);
                    ContactParent syncParent = GreenDaoHelper.getInstance().getStudentParentByPUId(parentUId);
                    setCallingParent(syncParent);
                    if (callBack != null) {
                        callBack.onGetParent(syncParent);
                    }
                }

                @Override
                public void onSyncError() {
                    Log.i(TAG, "onSyncError: ");
                    setCallingParent(null);
                    if (callBack != null) {
                        callBack.onGetParent(null);
                    }
                }
            });
        } else {
            setCallingParent(parent);
            if (callBack != null) {
                callBack.onGetParent(parent);
            }
        }
    }

    @OnClick({R.id.view_call_trying_imageButton_hang, R.id.view_call_trying_imageButton_accept})
    void viewOnClick(View view) {
        switch (view.getId()) {
            case R.id.view_call_trying_imageButton_hang:
                if (mTryingClickListener != null) {
                    mTryingClickListener.onHangUpClick();
                }
                break;
            case R.id.view_call_trying_imageButton_accept:
                if (mTryingClickListener != null) {
                    mTryingClickListener.onAcceptClick();
                }
                break;
        }
    }

    public interface tryingClickListener {
        void onHangUpClick();

        void onAcceptClick();
    }

    public interface IncomingParentCallBack {
        void onGetParent(ContactParent parent);
    }

}
