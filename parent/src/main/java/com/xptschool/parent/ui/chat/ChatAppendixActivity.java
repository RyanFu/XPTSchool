package com.xptschool.parent.ui.chat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoImpl;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.model.TakePhotoOptions;
import com.jph.takephoto.permission.InvokeListener;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.permission.TakePhotoInvocationHandler;
import com.jph.takephoto.uitl.TFileUtils;
import com.xptschool.parent.XPTApplication;
import com.xptschool.parent.common.LocalImageHelper;
import com.xptschool.parent.ui.main.BaseListActivity;
import com.xptschool.parent.util.ChatUtil;

import java.io.File;

/**
 * Created by dexing on 2017/6/2.
 * No1
 */

public class ChatAppendixActivity extends BaseListActivity implements TakePhoto.TakeResultListener, InvokeListener {

    private TakePhoto takePhoto;
    private InvokeParam invokeParam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);
        TFileUtils.setCacheFile(XPTApplication.getInstance().getCachePath());
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000) {
            //拍照，录像
            if (resultCode == 1001) {
                String path = data.getStringExtra("path");
                takeSuccess(path, ChatUtil.TYPE_FILE, 0);
            } else if (resultCode == 1002) {
                String path = data.getStringExtra("path");
                long duration = data.getLongExtra("duration", 0);
                videoSuccess(path, duration);
            }
        } else {
            getTakePhoto().onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.TPermissionType type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handlePermissionsResult(this, type, invokeParam, this);
    }

    /**
     * 获取TakePhoto实例
     *
     * @return
     */
    public TakePhoto getTakePhoto() {
        if (takePhoto == null) {
            takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this, this));
        }
        return takePhoto;
    }

    @Override
    public final void takeSuccess(TResult result) {
        takeSuccess(result.getImage().getCompressPath(), ChatUtil.TYPE_FILE, 0);
    }

    public void takeSuccess(String path, char type, long duration) {

    }

    public void videoSuccess(String path, long duration) {
        takeSuccess(path, ChatUtil.TYPE_VIDEO, duration);
    }

    @Override
    public void takeFail(TResult result, String msg) {
        Log.i(TAG, "takeFail:" + msg);
    }

    @Override
    public void takeCancel() {
        Log.i(TAG, getResources().getString(com.jph.takephoto.R.string.msg_operation_canceled));
    }

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.getMethod());
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam;
        }
        return type;
    }

    public void pickPhoto() {
        TakePhoto takePhoto = getTakePhoto();
        configCompress(takePhoto);
        configTakePhotoOption(takePhoto);
        takePhoto.onPickFromGallery();
    }

    public void takePhoto() {

        startActivityForResult(new Intent(this, RecordVideoActivity.class), 1000);

//        String cameraPath = LocalImageHelper.getInstance().setCameraImgPath();
//        File file = new File(cameraPath);
//        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
//        Uri imageUri = Uri.fromFile(file);
//        TakePhoto takePhoto = getTakePhoto();
//        configCompress(takePhoto);
//        configTakePhotoOption(takePhoto);
//        takePhoto.onPickFromCapture(imageUri);
    }

    private void configCompress(TakePhoto takePhoto) {
        int maxSize = 204800;
        int width = 800;
        int height = 800;
        boolean showProgressBar = true;
        boolean enableRawFile = false;
        CompressConfig config = new CompressConfig.Builder()
                .setMaxSize(maxSize)
                .setMaxPixel(width >= height ? width : height)
                .enableReserveRaw(enableRawFile)
                .create();
        takePhoto.onEnableCompress(config, showProgressBar);
    }

    private void configTakePhotoOption(TakePhoto takePhoto) {
        TakePhotoOptions.Builder builder = new TakePhotoOptions.Builder();
        builder.setWithOwnGallery(true);
        builder.setCorrectImage(true);
        takePhoto.setTakePhotoOptions(builder.create());
    }


}
