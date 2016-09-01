package com.weilian.phonelive.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import com.umeng.analytics.MobclickAgent;
import com.weilian.phonelive.AppContext;
import com.weilian.phonelive.R;
import com.weilian.phonelive.api.remote.ApiUtils;
import com.weilian.phonelive.api.remote.PhoneLiveApi;
import com.weilian.phonelive.base.BaseActivity;
import com.weilian.phonelive.bean.UserBean;
import com.weilian.phonelive.utils.FileUtil;
import com.weilian.phonelive.utils.ImageUtils;
import com.weilian.phonelive.utils.StringUtils;
import com.weilian.phonelive.utils.Utils;
import com.weilian.phonelive.widget.LoadUrlImageView;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 头像上传
 */
public class SelectAvatarActivity extends BaseActivity {
    @InjectView(R.id.av_edit_head)
    LoadUrlImageView mUHead;
    private final static int CROP = 200;

    private final static String FILE_SAVEPATH = Environment
            .getExternalStorageDirectory().getAbsolutePath()
            + "/PhoneLive/Portrait/";
    private Uri origUri;
    private Uri cropUri;
    private File protraitFile;
    private Bitmap protraitBitmap;
    private String protraitPath;
    private String theLarge;
    private String imgUrl;

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        mUHead.setImageLoadUrl(getIntent().getStringExtra("uhead"));
    }

    @OnClick({R.id.iv_select_avatar_back, R.id.btn_avator_from_album, R.id.btn_avator_photograph})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_select_avatar_back:
                finish();
                break;
            case R.id.btn_avator_from_album://相册
                startImagePick();
                break;
            case R.id.btn_avator_photograph:
                startTakePhoto();
                break;
        }

    }

    /**
     * 选择图片裁剪
     */
    private void startImagePick() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "选择图片"),
                    ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP);
        } else {
            intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "选择图片"),
                    ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP);
        }
    }

    // 裁剪头像的绝对路径
    private Uri getUploadTempFile(Uri uri) {
        String storageState = Environment.getExternalStorageState();

        String extFileDir = null;

        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            File savedir = new File(FILE_SAVEPATH);
            if (!savedir.exists()) {
                if (!savedir.mkdirs()) {
                    extFileDir = getExternalFilesDir(null).getAbsolutePath();
                }

            }
        } else {
            AppContext.showToastAppMsg(this, "无法保存上传的头像，请检查SD卡是否挂载");
            return null;
        }
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
                .format(new Date());
        String thePath = ImageUtils.getAbsolutePathFromNoStandardUri(uri);

        // 如果是标准Uri
        if (StringUtils.isEmpty(thePath)) {
            thePath = ImageUtils.getAbsoluteImagePath(this, uri);
        }
        String ext = FileUtil.getFileFormat(thePath);
        ext = StringUtils.isEmpty(ext) ? "jpg" : ext;
        // 照片命名
        String cropFileName = "osc_crop_" + timeStamp + "." + ext;
        // 裁剪头像的绝对路径

        if (extFileDir == null) {

            protraitPath = FILE_SAVEPATH + cropFileName;

        } else {

            protraitPath = extFileDir + "/" + cropFileName;
        }


        protraitFile = new File(protraitPath);


        cropUri = Uri.fromFile(protraitFile);
        return this.cropUri;
    }

    private void startTakePhoto() {
        Intent intent;
        // 判断是否挂载了SD卡
        String savePath = "";
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            savePath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/oschina/Camera/";
            File savedir = new File(savePath);
            if (!savedir.exists()) {
                if (!savedir.mkdirs()) {
                    savePath = getExternalFilesDir(null).getAbsolutePath() + "/";
                }
            }
        }

        // 没有挂载SD卡，无法保存文件
        if (StringUtils.isEmpty(savePath)) {
            AppContext.showToastShort("无法保存照片，请检查SD卡是否挂载");
            return;
        }

        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
                .format(new Date());
        String fileName = "osc_" + timeStamp + ".jpg";// 照片命名
        File out = new File(savePath, fileName);
        Uri uri = Uri.fromFile(out);
        origUri = uri;

        theLarge = savePath + fileName;// 该照片的绝对路径

        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent,
                ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
    }

    /**
     * 拍照后裁剪
     *
     * @param data 原始图片
     */
    private void startActionCrop(Uri data) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(data, "image/*");
        intent.putExtra("output", this.getUploadTempFile(data));
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);// 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", CROP);// 输出图片大小
        intent.putExtra("outputY", CROP);
        intent.putExtra("scale", true);// 去黑边
        intent.putExtra("scaleUpIfNeeded", true);// 去黑边
        startActivityForResult(intent,
                ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnIntent) {
        if (resultCode != Activity.RESULT_OK)
            return;

        switch (requestCode) {
            case ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA:
                startActionCrop(origUri);// 拍照后裁剪
                break;
            case ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP:
                startActionCrop(imageReturnIntent.getData());// 选图后裁剪
                break;
            case ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD:
                uploadNewPhoto();
                break;
        }
    }


    /**
     * 上传新照片
     */
    private void uploadNewPhoto() {
        showWaitDialog("正在上传头像...");

        // 获取头像缩略图
        if (!StringUtils.isEmpty(protraitPath) && protraitFile.exists()) {
            protraitBitmap = ImageUtils
                    .loadImgThumbnail(protraitPath, 200, 200);
        } else {
            hideWaitDialog();
            AppContext.showToastAppMsg(this, "图像不存在，上传失败");
        }
        if (protraitBitmap != null) {

            try {
                PhoneLiveApi.updatePortrait(AppContext.getInstance()
                                .getLoginUid(), AppContext.getInstance().getToken(), protraitFile,
                        new StringCallback() {

                            @Override
                            public void onError(Call call, Exception e) {
                                AppContext.showToastAppMsg(SelectAvatarActivity.this, "上传头像失败");
                                hideWaitDialog();
                            }

                            @Override
                            public void onResponse(String response) {
                                String res = ApiUtils.checkIsSuccess(response, SelectAvatarActivity.this);
                                if (null != res) {
                                    AppContext.showToastAppMsg(SelectAvatarActivity.this, "头像修改成功");
                                    UserBean u = AppContext.getInstance().getLoginUser();
                                    u.setAvatar(res);
                                    mUHead.setImageLoadUrl(res);
                                    AppContext.getInstance().updateUserInfo(u);
                                    Utils.IS_IMG_CHANGED = true;
                                }
                                hideWaitDialog();
                            }
                        });

            } catch (Exception e) {
                hideWaitDialog();
                AppContext.showToast("图像不存在，上传失败");
            }
        }

    }


    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("头像上传"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this);          //统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("头像上传"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_head;
    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }
}
