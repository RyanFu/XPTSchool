package com.xptschool.teacher.ui.album;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.Toast;

import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.TImage;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.model.TakePhotoOptions;
import com.jph.takephoto.uitl.TFileUtils;
import com.xptschool.teacher.R;
import com.xptschool.teacher.common.LocalImageHelper;
import com.xptschool.teacher.view.AlbumSourceView;
import com.xptschool.teacher.view.imgloader.AlbumViewPager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/29.
 */
public class AlbumActivity extends TakePhotoActivity {

    public ScrollView mScrollView;
    private PopupWindow picPopup;
    public AlbumGridAdapter myPicGridAdapter;

    @Override
    public boolean navigateUpTo(Intent upIntent) {
        return super.navigateUpTo(upIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalImageHelper.getInstance().getLocalCheckedImgs().clear();
    }

    public void showAlbumSource(View view) {
        //选择相片来源
        if (picPopup == null) {
            TFileUtils.setCacheFile(null);
            AlbumSourceView albumSourceView = new AlbumSourceView(AlbumActivity.this);
            albumSourceView.setOnAlbumSourceClickListener(new AlbumSourceView.OnAlbumSourceClickListener() {
                @Override
                public void onAlbumClick() {
                    if (LocalImageHelper.getInstance().getLocalCheckedImgs().size() >= LocalImageHelper.getInstance().getMaxChoiceSize()) {
                        Toast.makeText(AlbumActivity.this, getString(R.string.image_upline, LocalImageHelper.getInstance().getMaxChoiceSize()), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    TakePhoto takePhoto = getTakePhoto();
                    configCompress(takePhoto);
                    configTakePhotoOption(takePhoto);
                    int limit = LocalImageHelper.getInstance().getCurrentEnableMaxChoiceSize();
//                    takePhoto.onPickMultiple(limit);
                    takePhoto.onPickMultipleWithCrop(limit, getCropOptions());
                    picPopup.dismiss();
                }

                @Override
                public void onCameraClick() {
                    if (LocalImageHelper.getInstance().getLocalCheckedImgs().size() >= LocalImageHelper.getInstance().getMaxChoiceSize()) {
                        Toast.makeText(AlbumActivity.this, getString(R.string.image_upline, LocalImageHelper.getInstance().getMaxChoiceSize()), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        //  拍照后保存图片的绝对路径
                        String cameraPath = LocalImageHelper.getInstance().setCameraImgPath();
                        File file = new File(cameraPath);
                        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
                        Uri imageUri = Uri.fromFile(file);
                        Log.i(TAG, "onCameraClick: " + file.getAbsolutePath());
                        TakePhoto takePhoto = getTakePhoto();
                        configCompress(takePhoto);
                        configTakePhotoOption(takePhoto);

                        takePhoto.onPickFromCaptureWithCrop(imageUri, getCropOptions());

                        //getTakePhoto().onPickFromCaptureWithCrop(imageUri, getCropOptions());
//                        takePhoto.onPickFromCapture(imageUri);
//                    takePhoto.onPickFromCapture(Uri.fromFile(file));
//                    AlbumActivityPermissionsDispatcher.openCameraWithCheck(AlbumActivity.this);
                    } catch (Exception ex) {
                        Toast.makeText(AlbumActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "onCameraClick: " + ex.getMessage());
                    }
                    picPopup.dismiss();
                }

                @Override
                public void onBack() {
                    picPopup.dismiss();
                }
            });
            picPopup = new PopupWindow(albumSourceView,
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
            picPopup.setTouchable(true);
            picPopup.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            picPopup.setBackgroundDrawable(new ColorDrawable());
            picPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    backgroundAlpha(1.0f);
                }
            });
        }
        backgroundAlpha(0.5f);
        picPopup.showAtLocation(view, Gravity.BOTTOM, 0, 0);
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

    public void configTakePhotoOption(TakePhoto takePhoto) {
        TakePhotoOptions.Builder builder = new TakePhotoOptions.Builder();
        builder.setWithOwnGallery(true);
        builder.setCorrectImage(true);
        takePhoto.setTakePhotoOptions(builder.create());
    }

    private CropOptions getCropOptions() {
        int height = 800;
        int width = 800;
        boolean withWonCrop = true;

        CropOptions.Builder builder = new CropOptions.Builder();

        builder.setOutputX(width).setOutputY(height);
        builder.setWithOwnCrop(withWonCrop);
        return builder.create();
    }

    @Override
    public void takeCancel() {
        super.takeCancel();
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
        Log.i(TAG, "takeFail: " + msg);
    }

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        showImg(result.getImages());
    }

    private void showImg(ArrayList<TImage> images) {
        for (int i = 0; i < images.size(); i++) {
            String patch = images.get(i).getCompressPath();
//            if (path.isEmpty()) {
//                path = images.get(i).getCompressPath();
//            }
            patch = "file://" + patch;
            Log.i(TAG, "showImg: " + patch + "  file size " + new File(patch).length());
            if (!LocalImageHelper.getInstance().getLocalCheckedImgs().contains(patch)) {
                LocalImageHelper.getInstance().getLocalCheckedImgs().add(patch);
            }
        }
        myPicGridAdapter.reloadPicture(LocalImageHelper.getInstance().getLocalCheckedImgs());
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);//滚动到底部
            }
        });
    }

    //显示大图pager
    public void showViewPager(AlbumViewPager albumviewpager, int index) {
        if (albumviewpager == null)
            return;
        Log.i(TAG, "showViewPager: ");
        albumviewpager.setVisibility(View.VISIBLE);
//        albumviewpager.setAdapter(albumviewpager.new LocalViewPagerAdapter(LocalImageHelper.getInstance().getLocalCheckedImgs()));
        albumviewpager.setAdapter(albumviewpager.new LocalViewPagerAdapter(myPicGridAdapter.getImgPaths()));
        albumviewpager.setCurrentItem(index);
        AnimationSet set = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation((float) 0.9, 1, (float) 0.9, 1, albumviewpager.getWidth() / 2, albumviewpager.getHeight() / 2);
        scaleAnimation.setDuration(200);
        set.addAnimation(scaleAnimation);
        AlphaAnimation alphaAnimation = new AlphaAnimation((float) 0.1, 1);
        alphaAnimation.setDuration(200);
        set.addAnimation(alphaAnimation);
        albumviewpager.startAnimation(set);
    }

    //显示大图pager
    public void showNetImgViewPager(AlbumViewPager albumviewpager, List<String> imgUris, int index) {
        if (albumviewpager == null)
            return;
        Log.i(TAG, "showNetImgViewPager: ");
        albumviewpager.setVisibility(View.VISIBLE);
        albumviewpager.setAdapter(albumviewpager.new NetViewPagerAdapter(imgUris));
        albumviewpager.setCurrentItem(index);
        AnimationSet set = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation((float) 0.9, 1, (float) 0.9, 1, albumviewpager.getWidth() / 2, albumviewpager.getHeight() / 2);
        scaleAnimation.setDuration(200);
        set.addAnimation(scaleAnimation);
        AlphaAnimation alphaAnimation = new AlphaAnimation((float) 0.1, 1);
        alphaAnimation.setDuration(200);
        set.addAnimation(alphaAnimation);
        albumviewpager.startAnimation(set);
    }

    //关闭大图显示
    public void hideViewPager(AlbumViewPager albumviewpager) {
        if (albumviewpager == null)
            return;
        if (albumviewpager.getVisibility() == View.VISIBLE) {
            albumviewpager.setVisibility(View.GONE);
            AnimationSet set = new AnimationSet(true);
            ScaleAnimation scaleAnimation = new ScaleAnimation(1, (float) 0.9, 1, (float) 0.9, albumviewpager.getWidth() / 2, albumviewpager.getHeight() / 2);
            scaleAnimation.setDuration(200);
            set.addAnimation(scaleAnimation);
            AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
            alphaAnimation.setDuration(200);
            set.addAnimation(alphaAnimation);
            albumviewpager.startAnimation(set);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        LocalImageHelper.getInstance().clear();
    }

}
