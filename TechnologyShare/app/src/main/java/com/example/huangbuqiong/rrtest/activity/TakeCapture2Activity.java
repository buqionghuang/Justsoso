package com.example.huangbuqiong.rrtest.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;

import com.example.huangbuqiong.rrtest.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by huangbuqiong on 2018/2/7.
 */

public class TakeCapture2Activity extends AppCompatActivity implements TextureView.SurfaceTextureListener {
    private static final String TAG = "TakeCapture2Activity";
    /**
     * 相机状态：
     * 0：预览
     * 1：等待上锁（拍照片前将预览锁上保证图像不再变化）
     * 2：等待预拍照（对焦、曝光等操作）
     * 3：等待非预拍照（闪光灯等操作）
     * 4：已经获取图片
     */
    private static final int STATE_PREVIEW = 0;
    private static final int STATE_WAITING_LOCK = 1;
    private static final int STATE_WAITING_PRECAPTURE = 2;
    private static final int STATE_WAITING_NON_PRECAPTURE = 3;
    private static final int STATE_PICTURE_TAKEN = 4;

    /**
     * Camera2 API提供的最大预览宽度和高度
     */
    private static final int MAX_PREVIEW_WIDTH = 1920;
    private static final int MAX_PREVIEW_HEIGHT = 1080;
    /**
     * 正在使用的相机id
     */
    private String mCameraId;

    /**
     * 预览使用的自定义TextureView控件
     */
    private TextureView mTextureView;

    /**
     * 预览用的获取会话
     */
    private CameraCaptureSession mCaptureSession;

    /**
     * 正在使用的相机
     */
    private CameraDevice mCameraDevice;

    /**
     * 预览数据的尺寸
     */
    private Size mPreviewSize;


    private int REQUEST_CHANGE_CAMARA_PERMISSION = 5;
    private HandlerThread mThreadHandler;
    private Handler mHandler;
    private TextureView mPreView;
    private CaptureRequest.Builder mPreViewBuilder;
    private CameraCaptureSession mSession;
    private Button bt_takePic;

    private static final SparseIntArray ORIENTATION = new SparseIntArray();
    static {
        ORIENTATION.append(Surface.ROTATION_0, 90);
        ORIENTATION.append(Surface.ROTATION_90, 0);
        ORIENTATION.append(Surface.ROTATION_180, 270);
        ORIENTATION.append(Surface.ROTATION_270, 180);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera2);
        mThreadHandler = new HandlerThread("CAMERA2");
        mThreadHandler.start();
        mHandler = new Handler(mThreadHandler.getLooper());
        mPreView = (TextureView) findViewById(R.id.tv_preview);
        mPreView.setSurfaceTextureListener(this);
        bt_takePic = (Button) findViewById(R.id.bt_takePic);
        bt_takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mSession.capture(mPreViewBuilder.build(), mSessionCapTrueCallBack, mHandler);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CHANGE_CAMARA_PERMISSION) {
            if (grantResults != null && permissions != null) {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        try {
                            openCamera();
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                        Log.d("HUANG", "有相机权限 ");
                    }
                }
            }
        }
    }

    private CameraManager cameraManager;
    private String[] CameraIdList;
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            CameraIdList = cameraManager.getCameraIdList();
            //获取可用相机设备列表
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(CameraIdList[0]);
            //在这里可以通过CameraCharacteristics设置相机的功能,当然必须检查是否支持
            characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
            openCamera();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CHANGE_CAMARA_PERMISSION);
            return;
        }
    }

    private void openCamera() throws CameraAccessException {
        checkPermission();
        cameraManager.openCamera(CameraIdList[0], mCameraDeviceStateCallback, mHandler);
    }


    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private CameraDevice.StateCallback mCameraDeviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            try {
                startPreview(camera);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {

        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {

        }
    };

    private void startPreview(CameraDevice camera) throws CameraAccessException {
        SurfaceTexture texture = mPreView.getSurfaceTexture();
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        texture.setDefaultBufferSize(mPreView.getWidth(), mPreView.getHeight());
        Surface surface = new Surface(texture);
        mPreViewBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
        mPreViewBuilder.addTarget(surface);
        mPreViewBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATION.get(rotation));
        mPreViewBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

        camera.createCaptureSession(Arrays.asList(surface), mSessionStateCallBack, mHandler);
    }

    private CameraCaptureSession.StateCallback mSessionStateCallBack = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            mSession = session;
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {

        }
    };

    private CameraCaptureSession.CaptureCallback mSessionCapTrueCallBack =
            new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
                    super.onCaptureProgressed(session, request, partialResult);
                }

                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                }
            };

    /**
     * 屏幕方向发生改变时调用转换数据方法
     *
     * @param viewWidth  mTextureView 的宽度
     * @param viewHeight mTextureView 的高度
     */
    private void configureTransform(int viewWidth, int viewHeight) {
        if (null == mTextureView || null == mPreviewSize) {
            return;
        }
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        mTextureView.setTransform(matrix);
    }


//    /**
//     * 解开锁定的焦点
//     */
//    private void unlockFocus() {
//        try {
//            // 构建失能AF的请求
//
//            mPreViewBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
//                    CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
//            // 构建自动闪光请求(之前拍照前会构建为需要或者不需要闪光灯, 这里重新设回自动)
//            mPreViewBuilder.set(CaptureRequest.CONTROL_AE_MODE,
//                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
//            // 提交以上构建的请求
//            mCaptureSession.capture(mPreViewBuilder.build(), mSessionCapTrueCallBack,
//                    mHandler);
//            // 拍完照后, 设置成预览状态, 并重复预览请求
//            mState = STATE_PREVIEW;
//            mCaptureSession.setRepeatingRequest(mPreviewRequest, mSessionCapTrueCallBack,
//                    mHandler);
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 保存jpeg到指定的文件夹下, 开启子线程执行保存操作
     */
    private static class ImageSaver implements Runnable {

        /**
         * jpeg格式的文件
         */
        private final Image mImage;
        /**
         * 保存的文件
         */
        private final File mFile;

        public ImageSaver(Image image, File file) {
            mImage = image;
            mFile = file;
        }

        @Override
        public void run() {
            ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            FileOutputStream output = null;
            try {
                output = new FileOutputStream(mFile);
                output.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mImage.close();
                if (null != output) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }



    /**
     * 比较两个Size的大小基于它们的area
     */
    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }

    /**
     * 显示错误信息的对话框
     */
    public static class  ErrorDialog extends DialogFragment {
        private static final String MSG = "message";
        private static ErrorDialog newInstance(String msg) {
            ErrorDialog dialog = new ErrorDialog();
            Bundle b = new Bundle();
            b.putString(MSG, msg);
            dialog.setArguments(b);
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity activity = getActivity();
            return new AlertDialog.Builder(activity)
                    .setMessage(getArguments().getString(MSG))
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            activity.finish();
                        }
                    }).create();
        }
    }
}
