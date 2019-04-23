package com.jiayuan.shuibiao.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.cymaybe.foucsurfaceview.FocusSurfaceView;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.jiayuan.shuibiao.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.Manifest.permission.CAMERA;

public class TakeWaterMeterNumberActivity extends BaseActivity implements SurfaceHolder.Callback  {

    private static final String TAG = "moubiao";

    @BindView(R.id.preview_sv)
    FocusSurfaceView previewSFV;
    @BindView(R.id.take_bt)
    Button mTakeBT;

    private Camera mCamera;
    private SurfaceHolder mHolder;
    private boolean focus = false;

    private int cameraPosition = Camera.CameraInfo.CAMERA_FACING_BACK;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_water_meter_number);
        ButterKnife.bind(this);

        ViewGroup.LayoutParams layoutParams = previewSFV.getLayoutParams();
        layoutParams.width = ScreenUtils.getScreenWidth();
        layoutParams.height = ScreenUtils.getScreenWidth() * 4/3;
        previewSFV.setLayoutParams(layoutParams);

        initData();
        initView();
        setListener();
    }

    private void initData() {
        DetectScreenOrientation detectScreenOrientation = new DetectScreenOrientation(this);
        detectScreenOrientation.enable();
    }

    private void initView() {
        mHolder = previewSFV.getHolder();
        mHolder.addCallback(TakeWaterMeterNumberActivity.this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        initCamera();
        setCameraParams();

        previewSFV.setCropMode(FocusSurfaceView.CropMode.CUSTOM);
        previewSFV.setCustomRatio(44,15);
    }

    private void initCamera() {
        if (checkPermission()) {
            try {
                mCamera = android.hardware.Camera.open(0);//1:采集指纹的摄像头. 0:拍照的摄像头.
                Thread.sleep(50);
                mCamera.setPreviewDisplay(mHolder);
            } catch (Exception e) {
                Snackbar.make(mTakeBT, "camera open failed!", Snackbar.LENGTH_SHORT).show();
                finish();
                e.printStackTrace();
            }
        } else {
            requestPermission();
        }
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, 10000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 10000:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        initCamera();
                        setCameraParams();
                    }
                }

                break;
        }
    }

    private void setCameraParams() {
        if (mCamera == null) {
            return;
        }
        try {
            Camera.Parameters parameters = mCamera.getParameters();

            int orientation = judgeScreenOrientation();
            if (Surface.ROTATION_0 == orientation) {
                mCamera.setDisplayOrientation(90);
//                parameters.setRotation(90);
            } else if (Surface.ROTATION_90 == orientation) {
                mCamera.setDisplayOrientation(0);
//                parameters.setRotation(0);
            } else if (Surface.ROTATION_180 == orientation) {
                mCamera.setDisplayOrientation(180);
//                parameters.setRotation(180);
            } else if (Surface.ROTATION_270 == orientation) {
                mCamera.setDisplayOrientation(180);
//                parameters.setRotation(180);
            }
//            parameters.setPictureSize(1280, 960);
//            parameters.setPreviewSize(1280, 960);
            mCamera.setParameters(parameters);
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    /**
     * 判断屏幕方向
     *
     * @return 0：竖屏 1：左横屏 2：反向竖屏 3：右横屏
     */
    private int judgeScreenOrientation() {
        return getWindowManager().getDefaultDisplay().getRotation();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        releaseCamera();
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @OnClick({R.id.take_bt})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.take_bt:
                if (!focus) {
                    takePicture();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 拍照
     */
    private void takePicture() {
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                focus = success;
                if (success) {
                    mCamera.cancelAutoFocus();
                    mCamera.takePicture(new Camera.ShutterCallback() {
                        @Override
                        public void onShutter() {
                        }
                    }, null, null, new Camera.PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
                            //照片旋转
                            Bitmap originalBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                            Log.e(TAG, originalBitmap.getWidth()+"--original--"+ originalBitmap.getHeight());
                            //前置图片顺时针旋转180度加镜像翻转
                            if (cameraPosition == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                                originalBitmap = mirrorRotate(rotate(originalBitmap,180));
                            }

                            //目前本人测试的几款手机都会有90度的旋转，所以需要复原图片，如果有不同之处，请在github留言
                            if (cameraPosition == Camera.CameraInfo.CAMERA_FACING_BACK){
                                originalBitmap = rotate(originalBitmap,90);
                            }else {
                                originalBitmap = rotate(originalBitmap,-90);
                            }
                            //到此图片和预览视角相同
                            Log.e(TAG, originalBitmap.getWidth()+"----"+ originalBitmap.getHeight());

                            Bitmap cropBitmap = previewSFV.getPicture(originalBitmap);

                            try {
                                File file = saveToSDCard(cropBitmap);
                                Intent intent = new Intent();
                                Photo photo = new Photo(file.getName(),file.getAbsolutePath(),
                                        0,0,0,0,"");
                                intent.putExtra("photo",photo);
                                setResult(1,intent);
                                finish();
                                focus = false;
                                mCamera.startPreview();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 用来监测左横屏和右横屏切换时旋转摄像头的角度
     */
    private class DetectScreenOrientation extends OrientationEventListener {
        DetectScreenOrientation(Context context) {
            super(context);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            if (260 < orientation && orientation < 290) {
                setCameraParams();
            } else if (80 < orientation && orientation < 100) {
                setCameraParams();
            }
        }
    }


    /**
     * 将拍下来的照片存放在SD卡中
     * @param cropBitmap
     * @return jpgFile
     * @throws IOException
     */
    public File saveToSDCard(Bitmap cropBitmap) throws IOException {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); // 格式化时间
        String filename = format.format(date) + ".jpg";
        File fileFolder = new File(Environment.getExternalStorageDirectory(), File.separator + "DCIM" + File.separator + "Camera" + File.separator);
        if (!fileFolder.exists() || !fileFolder.isDirectory()) {
            if (!fileFolder.mkdirs()) {
                fileFolder = getExternalFilesDir(null);
                if (null == fileFolder || !fileFolder.exists()) {
                    fileFolder = getFilesDir();
                    if (null == fileFolder || !fileFolder.exists()) {
                        String cacheDirPath = File.separator + "data" + File.separator + "data" + File.separator + getPackageName() + File.separator + "cache" + File.separator;
                        fileFolder = new File(cacheDirPath);
                        if (!fileFolder.exists()) {
                            fileFolder.mkdirs();
                        }
                    }
                }
            }
        }
        File jpgFile = new File(fileFolder, filename);
        FileOutputStream outputStream = new FileOutputStream(jpgFile); // 文件输出流

        cropBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        outputStream.flush();
        outputStream.close(); // 关闭输出流

        return jpgFile;
    }

    /**前置图片顺时针旋转180度**/
    private Bitmap rotate(Bitmap bitmap, int degree){

        return ImageUtils.rotate(bitmap, degree,
                bitmap.getWidth() / 2, bitmap.getHeight() / 2, true);
    }
    /**图片镜像翻转**/
    private Bitmap mirrorRotate(Bitmap bitmap){
        Matrix matrix = new Matrix();
        matrix.postScale(-1, 1);// 镜像水平翻转
        return Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return 0;
    }

    @Override
    public void initView(View view) {

    }

    @Override
    public void setListener() {

    }

    @Override
    public void doBusiness(Context mContext) {

    }
}
