package it.plugin.camera;

//import android.annotation.TargetApi;
//import android.app.Activity;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

//import android.app.FragmentManager;
//import android.app.FragmentTransaction;
//import android.content.res.Configuration;
//import android.hardware.SensorManager;
//import android.media.ExifInterface;
//import android.os.Build;
//import android.util.Base64;
//import android.widget.Button;
//import org.w3c.dom.Text;
//
//import java.io.ByteArrayOutputStream;
//import java.text.DateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//
//import android.view.Surface;
//import android.view.SurfaceHolder;
//import android.widget.Toast;

//import static android.app.Activity.RESULT_OK;

public class CameraActivity_PicSaveView extends Fragment {
    private static final int FLASH_OFF = 0;
    private static final int FLASH_ON = 1;
    private static final int FLASH_AUTO = 2;
    private static final int FOCUS_AUTO = 0;
    private static final int FOCUS_CONTINUOUS = 1;
    private static final String TAG = "CameraActivity";
    String defaultCamera;
    boolean tapToTakePicture;
    boolean dragEnabled;
    private CameraPreviewListener eventListener;
    private FrameLayout frameContainerLayout;
    private ImageButton buttomPicture;//Nút click để chụp ảnh
    private ImageButton buttomClose;//Nút ẩn form chụp ảnh
    private ImageButton buttomRefresh;//Nút Refresh
    private ImageButton buttomAccept;//Nút Accept
    private CheckBox chbShowInfo;//Nút show hoặc ẩn info
    private TextView tbInfo;//Hiển thị thông tin lỗi trên ảnh
    private TextView tbDate;//Hiển thị thông tin ngày tháng trên ảnh
    private ImageView imgTouch;//Hiển thị vùng click focus.
    private ImageView imgView;//Hieển thị ảnh sau khi chụp
    private String strTakePicture;// Biến hiện trị kiểu chụp ảnh
    private Preview mPreview;
    private boolean canTakePicture = true;
    private View view;
    private Camera.Parameters cameraParameters;
    private Camera mCamera;
    private int numberOfCameras;
    private int cameraCurrentlyLocked;
    // The first rear facing camera
    private int defaultCameraId;
    //    private int width;
//    private int height;
    private int widthPreview;// Độ rộng của form hiển thị preview cammera - để tính tự động khi load Camera - Camera luôn hiển thị theo chiều ngang
    private int heightPreview;// Độ cao của form hiện thị camera - để tính tự động khi load Camera
    private int pictureWidth = 0;// 2560 Độ rộng của ảnh -
    private int pictureHeight = 0;// 1920 Chiều cao ảnh -
    private int pictureWidthSmall = 200; // Độ rộng của ảnh nhỏ
    private int pictureHeightSmall = 150; // Chiều cao của ảnh nhỏ
    private int pixelCamera = 5;// Độ phân giải máy ảnh khi chụp
    private int infoWidth = 0; //Chiều rộng của text lên ảnh - Thay đổi tùy vào kích thước màn hình
    private int infoHeight = 0;//Chiều cao của text lên ảnh - Thay đổi tùy vào kích thước màn hình
    private int infoFontSize = 0;//  Font size của Text vẽ trên màn hình
    private int infoWidthPicture = 0;// Chiều rộng nền chữ vẽ lên ảnh pictureWidth x pictureHeight
    private int infoHeightPicture = 0;// Chiều cao nền chữ vẽ lên ảnh pictureWidth x pictureHeight
    private int infoLeft = 0;//Lưu khoảng cách căn left
    private int infoTop = 0;//Lưu khoảng cách căn top
    private int infoRight = 0;//Lưu khoảng cách căn left
    private int infoBottom = 0;//Lưu khoảng cách căn top
    private int infoLeftPicture = 0; // Tính khoảng cách căn left với ảnh lớn
    private int infoToptPicture = 0; // Tính khoảng cách căn top với ảnh lớn
    private int infoFontSizePicture = 0;// Font size của Text vẽ trên ảnh
    private int iRotate = 0; //Lưu góc xoay của ảnh
    private int screenHeight = 0;
    private int screenWidth = 0;
    private String appResourcesPackage;
    private int currentFlashMode = FLASH_OFF;
    private int currentFocusMode = FOCUS_AUTO;
    private int sizeBorderText = 3;
    private static final int FOCUS_AREA_SIZE = 300;

    public String ImageName = ""; // Lấy thông tin đặt tên ảnh
    public String Info = ""; // Lấy thông tin lỗi vẽ lên ảnh
    public boolean bShowInfo = true;// = true hiên thị Info vẽ lên ảnh - False: không hiển thị.
    public String img = "";
    public String imgSmall = "";

    void setEventListener(CameraPreviewListener listener) {
        eventListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate run");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        appResourcesPackage = getActivity().getPackageName();
        Log.d(TAG, "onCreateView run");
        // Inflate the layout for this fragment
        view = inflater.inflate(getResources().getIdentifier("camera_activity", "layout", appResourcesPackage), container, false);
        if (mPreview != null)
            return view;
        //Set Full screen
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Mở camera trước khi lấy thông số
        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        //Hiển thị máy ảnh
        mPreview = new Preview(getActivity());
        FrameLayout video_view = (FrameLayout) view.findViewById(getResources().getIdentifier("video_view", "id", appResourcesPackage));
        video_view.addView(mPreview);
        imgView = (ImageView) view.findViewById(getResources().getIdentifier("image_view", "id", appResourcesPackage));
        frameContainerLayout = (FrameLayout) view.findViewById(getResources().getIdentifier("frame_container", "id", appResourcesPackage));
        buttomPicture = (ImageButton) view.findViewById(getResources().getIdentifier("picture", "id", appResourcesPackage));
        buttomClose = (ImageButton) view.findViewById(getResources().getIdentifier("close", "id", appResourcesPackage));
        buttomRefresh = (ImageButton) view.findViewById(getResources().getIdentifier("refresh", "id", appResourcesPackage));
        buttomAccept = (ImageButton) view.findViewById(getResources().getIdentifier("Accept", "id", appResourcesPackage));
        tbInfo = (TextView) view.findViewById(getResources().getIdentifier("tbInfo", "id", appResourcesPackage));
        chbShowInfo = (CheckBox) view.findViewById(getResources().getIdentifier("chbShowInfo", "id", appResourcesPackage));
        tbDate = (TextView) view.findViewById(getResources().getIdentifier("tbDate", "id", appResourcesPackage));
        imgTouch = (ImageView) view.findViewById(getResources().getIdentifier("imgTouch", "id", appResourcesPackage));
        FrameLayout frame_buttom = (FrameLayout) view.findViewById(getResources().getIdentifier("frame_buttom", "id", appResourcesPackage));
        FrameLayout frame_camera = (FrameLayout) view.findViewById(getResources().getIdentifier("frame_camera", "id", appResourcesPackage));

        //Lấy thông số màn hình và set thông số hiển thị camera
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        screenWidth = display.getWidth();
        screenHeight = display.getHeight();
        Log.d(TAG, "Screen: " + screenWidth + " " + screenHeight);

        //Lấy thông tin kích thước ảnh camera
        List<Camera.Size> mSizes = mCamera.getParameters().getSupportedPictureSizes();
        for (Camera.Size size : mSizes
                ) {
            if ((int) size.width * 3 == (int) size.height * 4 && Math.round(((double) size.width * (double) size.height) / (double) 1024000) == pixelCamera) {
                pictureWidth = size.width;
                pictureHeight = size.height;
                Camera.Parameters para = mCamera.getParameters();
                para.setPictureSize(pictureWidth, pictureHeight);
                mCamera.setParameters(para);
                Log.d(TAG, "PictureSize= " + size.width + " x " + size.height);
            }
        }
        widthPreview = Math.round(screenHeight * 4 / 3);
        heightPreview = screenHeight;
        Log.d(TAG, "ScreenPreview: " + widthPreview + " " + heightPreview);

        // Tính tỉ lệ kích thước text vẽ lên ảnh
        tbInfo.measure(0, 0);
        infoFontSize = (int) tbInfo.getTextSize();
        infoFontSizePicture = Math.round((int) infoFontSize * (int) pictureWidth / widthPreview);
        infoWidth = (int) tbInfo.getMeasuredWidth(); //Math.round(infoWidthPicture * widthPreview / pictureWidth);
        infoHeight = (int) tbInfo.getMeasuredHeight();//Math.round(infoHeightPicture * heightPreview / pictureHeight);
        infoWidthPicture = Math.round((int) infoWidth * (int) pictureWidth / widthPreview);
        infoHeightPicture = Math.round((int) infoHeight * (int) pictureHeight / heightPreview);
        Log.d(TAG, "infoFontSize: " + infoFontSize + " infoFontSizePicture: " + infoFontSizePicture +
                " infoWidth:" + infoWidth + " infoHeight: " + infoHeight +
                " infoWidthPicture:" + infoWidthPicture + " infoHeightPicture: " + infoHeightPicture);


        frame_buttom.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        FrameLayout.LayoutParams paraButtom = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, heightPreview);
        paraButtom.gravity = Gravity.RIGHT | Gravity.END;
        frame_buttom.setLayoutParams(paraButtom);

        frame_camera.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        FrameLayout.LayoutParams paraCamera = new FrameLayout.LayoutParams(widthPreview, heightPreview);
        paraCamera.setMargins(0, 0, screenWidth - widthPreview, 0);
        frame_camera.setLayoutParams(paraCamera);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        tbDate.setText(df.format(new Date()));

        AllEvent();

        return view;
    }

    void AllEvent() {
        //Sự kiện khi xoay màn hình trả về giá trị xoay vào iRotate
        OrientationEventListener mOrientationListener = new OrientationEventListener(getActivity().getApplicationContext()) {
            @Override
            public void onOrientationChanged(int orientation) {
                if ((orientation >= 0 && orientation <= 45) || (orientation >= 316 && orientation <= 360)) {
                    iRotate = 0;// Điên thoại đang thẳng đứng
                    rotateText(iRotate);
                }
                if (orientation >= 46 && orientation <= 135) {
                    iRotate = 90; // Điện thoại đang xoay ngang
                    rotateText(iRotate);
                }
                if (orientation >= 136 && orientation <= 225) {
                    iRotate = 180;// Điên thoại đang xoay ngược theo phương đứng
                    rotateText(iRotate);
                }
                if (orientation >= 226 && orientation <= 315) {
                    iRotate = 270;// Điện thoại đang xoay ngang theo chiều ngược lại
                    rotateText(iRotate);
                }

            }
        };

        if (mOrientationListener.canDetectOrientation()) {
            mOrientationListener.enable();
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //Event buttomPicture click
                buttomPicture.setClickable(true);
                buttomPicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //canTakePicture = true;
                        takePhoto(pictureWidth, pictureHeight);
                    }
                });
                //Event buttomClose click
                buttomClose.setClickable(true);
                buttomClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        eventListener.hideCamera();
                        canTakePicture = true;
                        imgView.setVisibility(View.GONE);
                        buttomAccept.setVisibility(View.GONE);
                        buttomPicture.setVisibility(View.VISIBLE);
                        Camera.Parameters parameters = mCamera.getParameters();
                        parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                        mCamera.setParameters(parameters);
                    }
                });
                //Event buttomRefresh click
                buttomRefresh.setClickable(true);
                buttomRefresh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        canTakePicture = true;
                        imgView.setVisibility(View.GONE);
                        buttomAccept.setVisibility(View.GONE);
                        buttomPicture.setVisibility(View.VISIBLE);
                        Camera.Parameters parameters = mCamera.getParameters();
                        parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                        mCamera.setParameters(parameters);
                        mCamera.startPreview();
                    }
                });
                //Event Accept click
                buttomAccept.setClickable(true);
                buttomAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        canTakePicture = true;
                        imgView.setVisibility(View.GONE);
                        buttomAccept.setVisibility(View.GONE);
                        buttomPicture.setVisibility(View.VISIBLE);
                        Camera.Parameters parameters = mCamera.getParameters();
                        parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                        mCamera.setParameters(parameters);
                        mCamera.startPreview();

                        JSONObject jo = new JSONObject();
                        try {
                            jo.put("img", img);
                            jo.put("imgSmall", imgSmall);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        JSONArray ja = new JSONArray();
                        ja.put(jo);
                        eventListener.hideCamera();
                        eventListener.onPictureTaken(ja);

                    }
                });
                //Event tbInfo click
                tbInfo.setClickable(true);
                tbInfo.setOnTouchListener(new View.OnTouchListener() {
                    private int mLastTouchX = 0;
                    private int mLastTouchY = 0;

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        int x;
                        int y;
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                if (mLastTouchX == 0 || mLastTouchY == 0) {
                                    mLastTouchX = (int) event.getRawX();
                                    mLastTouchY = (int) event.getRawY();
                                    infoLeft = tbInfo.getLeft();
                                    infoTop = tbInfo.getTop();
                                } else {
                                    mLastTouchX = (int) event.getRawX();
                                    mLastTouchY = (int) event.getRawY();
                                }
                                break;
                            case MotionEvent.ACTION_MOVE:
                                x = (int) event.getRawX();
                                y = (int) event.getRawY();
                                final float dx = x - mLastTouchX;
                                final float dy = y - mLastTouchY;
                                infoLeft += dx;
                                infoTop += dy;
                                Log.d(TAG, "iRotate: " + iRotate);
                                //Tinh Left top trên ảnh đầu ra
                                if (iRotate == 270) {
                                    infoLeftPicture = Math.round(infoLeft * pictureWidth / widthPreview);
                                    infoToptPicture = Math.round(infoTop * pictureHeight / heightPreview);
                                }
                                if (iRotate == 180) {
                                    infoLeftPicture = Math.round((infoTop - (infoWidth - infoHeight) / 2) * pictureHeight / heightPreview);
                                    infoToptPicture = Math.round((widthPreview - infoLeft - infoWidth + (infoWidth - infoHeight) / 2) * pictureWidth / widthPreview);
                                }
                                if (iRotate == 90) {
                                    infoToptPicture = Math.round((heightPreview - infoTop - infoHeight) * pictureWidth / widthPreview);
                                    infoLeftPicture = Math.round((widthPreview - infoLeft - infoWidth) * pictureHeight / heightPreview);
                                }
                                if (iRotate == 0) {
                                    infoLeftPicture = Math.round((heightPreview - infoTop - infoHeight - Math.round((infoWidth - infoHeight) / 2)) * pictureHeight / heightPreview);
                                    infoToptPicture = Math.round((infoLeft * pictureWidth / widthPreview) + (infoWidthPicture - infoHeightPicture) / 2);
                                }
                                FrameLayout.LayoutParams para = new FrameLayout.LayoutParams(infoWidth, infoHeight);
                                para.setMargins(infoLeft, infoTop, 0, 0);
                                tbInfo.setLayoutParams(para);
                                mLastTouchX = x;
                                mLastTouchY = y;
                                Log.d(TAG, "W: " + infoWidth + " H: " + infoHeight + " infoLeft: " + infoLeft + " infoTop: " + infoTop + "infoRight: " + infoRight + " infoBottom: " + infoBottom
                                        + " infoLeftPicture: " + infoLeftPicture + " infoToptPicture:" + infoToptPicture);
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });
                //Event chbShowInfo click
                chbShowInfo.setClickable(true);
                chbShowInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (chbShowInfo.isChecked()) {
                            bShowInfo = true;
                            tbInfo.setVisibility(View.VISIBLE);
                        } else {
                            bShowInfo = false;
                            tbInfo.setVisibility(View.GONE);
                        }
                    }
                });
                frameContainerLayout.setClickable(true);
                frameContainerLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            int iLeft = (int) event.getX();
                            int iTop = (int) event.getY();
                            //Move touch image
                            //imgTouch.measure(0, 0);
//                            RelativeLayout.LayoutParams para = new RelativeLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
//                            para.setMargins(iLeft - (int) imgTouch.getMeasuredWidth() / 2, iTop - (int) imgTouch.getMeasuredHeight(), 0, 0);
//                            imgTouch.setLayoutParams(para);
                            imgTouch.setVisibility(View.VISIBLE);
                            if (canTakePicture = true)
                                focusOnTouch(event);
                        }
                        return true;
                    }
                });
            }
        });
    }

    //Xoay text hien thi loi va text hien thi ngay thang
    void rotateText(int iRotate) {
        //Xoay các nút, text lỗi, ngày tháng hiện thị trên form chụp ảnh
        buttomPicture.setRotation(270 - iRotate);
        buttomClose.setRotation(270 - iRotate);
        buttomRefresh.setRotation(270 - iRotate);
        buttomAccept.setRotation(270 - iRotate);
        tbInfo.setRotation(270 - iRotate);
        chbShowInfo.setRotation(270 - iRotate);
        tbDate.setRotation(270 - iRotate);
        // Thay đổi vị trí hiển thị datetime
        tbDate.measure(0, 0);
        FrameLayout.LayoutParams para = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        int iLeft = 0;
        int iTop = 0;
        int iRight = 0;
        int iBottom = 0;
        if (iRotate == 0) {
            iTop = tbDate.getMeasuredWidth() / 2;
            iLeft = widthPreview - tbDate.getMeasuredHeight() - tbDate.getMeasuredWidth();// Vì để Size của text là tự do nên cần trừ thêm
        } else if (iRotate == 90) {
            iLeft = tbDate.getMeasuredWidth() / 2;
            iTop = tbDate.getMeasuredHeight();
        } else if (iRotate == 180) {
            iLeft = tbDate.getMeasuredHeight() / 2;
            iTop = heightPreview - tbDate.getMeasuredWidth();
        } else if (iRotate == 270) {
            iLeft = widthPreview - tbDate.getMeasuredWidth() - tbDate.getMeasuredWidth() / 2;
            iTop = heightPreview - tbDate.getMeasuredHeight() * 2;
        }
        para.setMargins(iLeft, iTop, iRight, iBottom);//Left,Top,Right,Bottom
        tbDate.setLayoutParams(para);
        tbDate.measure(0, 0);
        //Log.d(TAG,"tbDate.setLayoutParams: "+tbDate.getMeasuredWidth()+" "+tbDate.getMeasuredHeight());
        //Log.d(TAG,"tbDate.setLayoutParams: "+iLeft+" "+iTop+" "+iRight+" "+iBottom);
    }

    //Focus camera when touch preview form.
    private void focusOnTouch(MotionEvent event) {
        // Tạm thời không dùng - để focus chính tâm hinh anh
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            if (parameters.getMaxNumMeteringAreas() > 0) {
                //Rect rect = calculateFocusArea(event.getX(), event.getY());
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                //List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
                //meteringAreas.add(new Camera.Area(rect, 1000));
                //parameters.setFocusAreas(meteringAreas);
                mCamera.setParameters(parameters);
                mCamera.autoFocus(mAutoFocusTakePictureCallback);
            } else {
                mCamera.autoFocus(mAutoFocusTakePictureCallback);
            }
        }
    }

    private Camera.AutoFocusCallback mAutoFocusTakePictureCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (success) {
                imgTouch.setVisibility(View.GONE);
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                mCamera.setParameters(parameters);
            } else {
                Log.d("Focus", "fail!");
                canTakePicture = true;
            }
        }
    };

//    private Rect calculateFocusArea(float x, float y) {
//        int ileft = Math.round(x); //clamp(Float.valueOf((x / mPreview.getWidth()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);
//        int itop = Math.round(y); //clamp(Float.valueOf((y / mPreview.getHeight()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);
//        int iright = ileft + FOCUS_AREA_SIZE;
//        int ibottom = itop + FOCUS_AREA_SIZE;
//        if (iright >= widthPreview) {
//            iright = widthPreview;
//            ileft = iright - FOCUS_AREA_SIZE;
//        }
//        if (ibottom >= heightPreview) {
//            ibottom = heightPreview;
//            itop = ibottom - FOCUS_AREA_SIZE;
//        }
//        //if()
//        Log.d(TAG, "FocusArea: left:" + Integer.toString(ileft) + " top:" + Integer.toString(itop) + " right:" + Integer.toString(iright) + " bottom:" + Integer.toString(ibottom));
//
//        return new Rect(ileft, itop, iright, ibottom);
//
//    }
//
//    private int clamp(int touchCoordinateInCameraReper, int focusAreaSize) {
//        int result;
//        if (Math.abs(touchCoordinateInCameraReper) + focusAreaSize / 2 > 1000) {
//            if (touchCoordinateInCameraReper > 0) {
//                result = 1000 - focusAreaSize / 2;
//            } else {
//                result = -1000 + focusAreaSize / 2;
//            }
//        } else {
//            result = touchCoordinateInCameraReper - focusAreaSize / 2;
//        }
//        return result;
//    }
//
//    private void setDefaultCameraId() {
//
//        // Find the total number of cameras available
//        numberOfCameras = Camera.getNumberOfCameras();
//
//        int camId = defaultCamera.equals("front") ? Camera.CameraInfo.CAMERA_FACING_FRONT : Camera.CameraInfo.CAMERA_FACING_BACK;
//
//        // Find the ID of the default camera
//        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
//        for (int i = 0; i < numberOfCameras; i++) {
//            Camera.getCameraInfo(i, cameraInfo);
//            if (cameraInfo.facing == camId) {
//                defaultCameraId = camId;
//                break;
//            }
//        }
//    }

    @Override
    public void onResume() {
        super.onResume();
        try {

            Camera.Parameters paraCamera = mCamera.getParameters();
            paraCamera.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            mCamera.setParameters(paraCamera);
            if (cameraParameters != null) {
                mCamera.setParameters(cameraParameters);
            }
            // set continuous autofocus
            //setFocusMode(currentFocusMode);
            //setFlashMode(currentFlashMode);

            cameraCurrentlyLocked = defaultCameraId;

            if (mPreview.mPreviewSize == null) {
                mPreview.viewHeight = heightPreview;
                mPreview.viewWidth = widthPreview;
                mPreview.setCamera(mCamera, cameraCurrentlyLocked);
            } else {
                mPreview.switchCamera(mCamera, cameraCurrentlyLocked);
                mCamera.startPreview();
            }

        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        // Because the Camera object is a shared resource, it's very
        // important to release it when the activity is paused.
        if (mCamera != null) {
            mPreview.setCamera(null, -1);
            mCamera.release();
            mCamera = null;
        }
        System.gc();
    }

    Camera getCamera() {
        return mCamera;
    }

    void switchCamera() {
        // check for availability of multiple cameras
        if (numberOfCameras == 1) {
            //There is only one camera available
            return;
        }
        Log.d(TAG, "numberOfCameras: " + numberOfCameras);

        // OK, we have multiple cameras.
        // Release this camera -> cameraCurrentlyLocked
        if (mCamera != null) {
            mCamera.stopPreview();
            mPreview.setCamera(null, -1);
            mCamera.release();
            mCamera = null;
        }

        // Acquire the next camera and request Preview to reconfigure
        // parameters.
        mCamera = Camera.open((cameraCurrentlyLocked + 1) % numberOfCameras);

        if (cameraParameters != null) {
            mCamera.setParameters(cameraParameters);
        }

        cameraCurrentlyLocked = (cameraCurrentlyLocked + 1) % numberOfCameras;
        mPreview.switchCamera(mCamera, cameraCurrentlyLocked);

        Log.d(TAG, "cameraCurrentlyLocked new: " + cameraCurrentlyLocked);

        // Start the preview
        mCamera.startPreview();
    }

    void setFlashMode(int flashMode) {

        Camera.Parameters parameters = mCamera.getParameters();
        List<String> supportedFlashModes = parameters.getSupportedFlashModes();

        if (supportedFlashModes != null) {
            //parameters.setFlashMode(Parameters.FLASH_MODE_ON);
            if (flashMode == FLASH_OFF && supportedFlashModes.contains(Parameters.FLASH_MODE_OFF)) {
                parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
            } else if (flashMode == FLASH_ON && supportedFlashModes.contains(Parameters.FLASH_MODE_ON)) {
                parameters.setFlashMode(Parameters.FLASH_MODE_ON);
            } else if (flashMode == FLASH_AUTO && supportedFlashModes.contains(Parameters.FLASH_MODE_AUTO)) {
                parameters.setFlashMode(Parameters.FLASH_MODE_AUTO);
            } else if (flashMode == FLASH_AUTO && supportedFlashModes.contains(Parameters.FLASH_MODE_ON)) {
                parameters.setFlashMode(Parameters.FLASH_MODE_ON);
            }
            mCamera.setParameters(parameters);
        }

        Log.d(TAG, "flashmode: " + flashMode);

        currentFlashMode = flashMode;
    }

//    private void setFocusMode(int focusMode) {
//        Camera.Parameters parameters = mCamera.getParameters();
//        List<String> supportedFocusModes = parameters.getSupportedFocusModes();
//
//        if (supportedFocusModes != null) {
//            parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
//            mCamera.setParameters(parameters);
//        } else {
//            Log.d(TAG, "focusMode: none suport");
//        }
//        currentFocusMode = focusMode;
//    }

    void setCameraParameters(Camera.Parameters params) {
        cameraParameters = params;

        if (mCamera != null && cameraParameters != null) {
            mCamera.setParameters(cameraParameters);
        }
    }

    //Thiết lập hiển thị Info lên trên ảnh
    void SetInfoDisplay() {
        if (bShowInfo) {
            chbShowInfo.setVisibility(View.VISIBLE);
            chbShowInfo.setChecked(true);
            tbInfo.setText(Info);
            tbInfo.setVisibility(View.VISIBLE);
        } else {
            chbShowInfo.setVisibility(View.GONE);
            chbShowInfo.setChecked(false);
            tbInfo.setVisibility(View.GONE);
        }
    }

    void takePhoto(final int maxWidth, final int maxHeight) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            tbDate.setText(df.format(new Date()));

            if (mPreview == null) {
                canTakePicture = false;
                Log.d(TAG, "mPreview null = " + canTakePicture);
            }
            if (!canTakePicture)
                return;
            canTakePicture = false;
            Camera.Parameters para = mCamera.getParameters();
            para.setFocusMode(Parameters.FLASH_MODE_AUTO);
            mCamera.setParameters(para);
            imgTouch.setVisibility(View.VISIBLE);
            FrameLayout.LayoutParams paraLayout = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                public void onAutoFocus(boolean success, Camera camera) {
                    try {
                        if (success && canTakePicture == false) {
                            imgTouch.setVisibility(View.GONE);
                            mCamera.takePicture(null, null, mPicture);
                        }
                    } catch (RuntimeException ex) {
                        Log.e(TAG, "Auto-focus crash");
                        canTakePicture = true;
                    }
                }
            });
        } catch (RuntimeException ex) {
            Log.e(TAG, "void takePhoto error:" + ex.getMessage());
        }
    }

    private PictureCallback mPicture = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            try {
                //eventListener.hideCamera();
                Bitmap picture = BitmapFactory.decodeByteArray(data, 0, data.length);
                Matrix matrix = new Matrix();
                //Rotate bitmap follow iRotate
                matrix.postRotate(90 + iRotate);
                int pictureWidth = picture.getWidth();
                int pictureHeight = picture.getHeight();
                picture = Bitmap.createBitmap(picture, 0, 0, pictureWidth, pictureHeight, matrix, true);
                pictureWidth = picture.getWidth();
                pictureHeight = picture.getHeight();
                //Xoay lại ảnh nhỏ
                if (pictureWidth < pictureHeight) {
                    int i = pictureWidthSmall;
                    pictureWidthSmall = pictureHeightSmall;
                    pictureHeightSmall = i;
                }

                SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd");
                File myDir = new File("/mnt/sdcard/SURVEY/" + df.format(new Date()));
                myDir.mkdirs();
                df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                String ImageNameSmall = ImageName + "_" + df.format(new Date()) + "_small.jpg";
                ImageName = ImageName + "_" + df.format(new Date()) + ".jpg";
                File imageFile = new File(myDir, ImageName);
                FileOutputStream out = new FileOutputStream(imageFile);
                File imageFileSmall = new File(myDir, ImageNameSmall);
                FileOutputStream outSmall = new FileOutputStream(imageFileSmall);

                //Draw
                Bitmap bmOverlay = Bitmap.createBitmap(pictureWidth, pictureHeight, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bmOverlay);
                canvas.drawBitmap(picture, 0, 0, null);

                SimpleDateFormat now = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String strDate = now.format(new Date());

                //Paint border
                Paint strokePaint = new Paint();
                strokePaint.setColor(Color.BLACK);
                strokePaint.setTextSize(infoFontSizePicture);
                strokePaint.setTextAlign(Paint.Align.CENTER);
                strokePaint.setTypeface(Typeface.DEFAULT_BOLD);
                strokePaint.setStyle(Paint.Style.STROKE);
                strokePaint.setStrokeWidth(sizeBorderText);

                //Paint text
                Paint textPaint = new Paint();
                textPaint.setARGB(255, 235, 165, 44);
                textPaint.setTextSize(infoFontSizePicture);
                textPaint.setTextAlign(Paint.Align.CENTER);
                textPaint.setTypeface(Typeface.DEFAULT_BOLD);
                textPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
                canvas.drawText(strDate, pictureWidth - 500, pictureHeight - 200, strokePaint);
                canvas.drawText(strDate, pictureWidth - 500, pictureHeight - 200, textPaint);

                if (bShowInfo) {
                    Paint rectanglePaint = new Paint();
                    rectanglePaint.setColor(Color.BLUE);
                    rectanglePaint.setStyle(Paint.Style.FILL_AND_STROKE);
                    canvas.drawRect(infoLeftPicture, infoToptPicture, infoLeftPicture + infoWidthPicture, infoToptPicture + infoHeightPicture, rectanglePaint);

                    String[] arrText = tbInfo.getText().toString().split("\\n");
                    int iTop = infoToptPicture;
                    for (String str : arrText
                            ) {
                        canvas.drawText(str.trim(), infoLeftPicture + infoWidthPicture / 2, iTop + infoFontSizePicture, strokePaint);
                        canvas.drawText(str.trim(), infoLeftPicture + infoWidthPicture / 2, iTop + infoFontSizePicture, textPaint);
                        iTop = iTop + infoFontSizePicture + infoFontSizePicture / 2;
                    }
                }
                Bitmap bmOverlaySmall = Bitmap.createScaledBitmap(bmOverlay, pictureWidthSmall, pictureHeightSmall, true);
                //Save bitmap to library
                bmOverlay.compress(Bitmap.CompressFormat.JPEG, 100, out);
                bmOverlaySmall.compress(Bitmap.CompressFormat.JPEG, 100, outSmall);
                img = imageFile.getPath();
                imgSmall = imageFileSmall.getPath();

                Toast.makeText(getActivity(), "Take photo successful!", Toast.LENGTH_SHORT).show();

                Bitmap bmImageView = Bitmap.createScaledBitmap(bmOverlay, pictureWidth, pictureHeight, true);
                Matrix matrixView = new Matrix();
                matrixView.postRotate(-90 - iRotate);
                bmImageView = Bitmap.createBitmap(bmImageView, 0, 0, pictureWidth, pictureHeight, matrixView, true);
                imgView.setImageBitmap(bmImageView);
                imgView.setVisibility(View.VISIBLE);
                buttomAccept.setVisibility(View.VISIBLE);
                buttomPicture.setVisibility(View.GONE);

            } catch (OutOfMemoryError oom) {
                Log.e(TAG, "OutOfMemoryError: " + oom.getMessage());
                canTakePicture = true;
            } catch (FileNotFoundException e) {
                Log.e(TAG, "FileNotFoundException: " + e.getMessage());
                canTakePicture = true;
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    interface CameraPreviewListener {
        void onPictureTaken(JSONArray args);

        void hideCamera();
    }
}
