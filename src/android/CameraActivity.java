package it.plugin.camera;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

//import android.support.annotation.NonNull;
//import android.support.v13.app.FragmentCompat;
//import android.support.v4.content.ContextCompat;


public class CameraActivity extends Fragment {
    //implements View.OnClickListener, FragmentCompat.OnRequestPermissionsResultCallback {

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final String FRAGMENT_DIALOG = "dialog";
    private static CameraPreviewListener eventListener;
    private String appResourcesPackage;
    private View view;
    public static Activity activityMain;
    private static RelativeLayout frame_camera;
    private static RelativeLayout frame_image;
    private static LinearLayout pane_image;
    private static FrameLayout frame_buttom;
    private static ImageButton btnPicture;//Nút click để chụp ảnh
    private static ImageButton btnPicture2;//Nút click để chụp ảnh
    private static ImageButton btnClose;//Nút ẩn form chụp ảnh
    private static ImageButton btnRefresh;//Nút Refresh
    private static ImageButton btnAccept;//Nút Accept
    private static CheckBox chbShowInfo;//Nút show hoặc ẩn info
    private static TextView tbInfo;//Hiển thị thông tin lỗi trên ảnh
    private static TextView tbDate;//Hiển thị thông tin ngày tháng trên ảnh
    private static ImageView imgTouch;//Hiển thị vùng click focus.
    private static ImageView imgView;//Hieển thị ảnh sau khi chụp
    private static ListView list_image;
    private static String strTakePicture;// Biến hiện trị kiểu chụp ảnh
    private camLauncher mPreview;
    private int numberOfCameras;
    private int cameraCurrentlyLocked;
    // The first rear facing camera
    private int defaultCameraId;
    private static int widthPreview = 1;// Độ rộng của form hiển thị preview cammera - để tính tự động khi load Camera - Camera luôn hiển thị theo chiều ngang
    private static int heightPreview = 1;// Độ cao của form hiện thị camera - để tính tự động khi load Camera
    public int pictureWidth = 0;// 2560 Độ rộng của ảnh -
    public int pictureHeight = 0;// 1920 Chiều cao ảnh -
    private static int pictureWidthSmall = 200; // Độ rộng của ảnh nhỏ
    private static int pictureHeightSmall = 150; // Chiều cao của ảnh nhỏ
    private static int listPreviewWidth = 0;// độ rộng của ảnh xem trước hiển thị nhỏ bên phải màn hình chụp
    private static int listPreviewHeight = 0;// Chiều cao ảnh xem trước hiển thị nhỏ bên phải màn hình chụp
    private static int pixelCamera = 5;// Độ phân giải máy ảnh khi chụp
    private static int infoWidth = 0; //Chiều rộng của text lên ảnh - Thay đổi tùy vào kích thước màn hình
    private static int infoHeight = 0;//Chiều cao của text lên ảnh - Thay đổi tùy vào kích thước màn hình
    private static int infoFontSize = 0;//  Font size của Text vẽ trên màn hình
    private static int infoWidthPicture = 0;// Chiều rộng nền chữ vẽ lên ảnh pictureWidth x pictureHeight
    private static int infoHeightPicture = 0;// Chiều cao nền chữ vẽ lên ảnh pictureWidth x pictureHeight
    private static int infoLeft = 0;//Lưu khoảng cách căn left
    private static int infoTop = 0;//Lưu khoảng cách căn top
    private static int infoLeftPicture = 0; // Tính khoảng cách căn left với ảnh lớn
    private static int infoToptPicture = 0; // Tính khoảng cách căn top với ảnh lớn
    private static int infoFontSizePicture = 0;// Font size của Text vẽ trên ảnh
    private static int iRotate = 0; //Lưu góc xoay của ảnh
    private static String ImageParent = "";
    public static String ImageName = ""; // Lấy thông tin đặt tên ảnh
    public String Info = ""; // Lấy thông tin lỗi vẽ lên ảnh
    public static boolean bShowInfo = true;// = true hiên thị Info vẽ lên ảnh - False: không hiển thị.
    public static String img = "";
    public static String imgSmall = "";
    //public static boolean bTakeSuccess = false;//Xác định đã chụp ảnh xong hay chưa
    private static Paint strokePaint; // Style của viền chữ
    private static Paint textPaint;// Style của chữa trên ảnh
    //private static Bitmap bmPhoto; // Lưu hình ảnh sau khi vẽ
    public static ArrayList<Bitmap> arrIMG;//Lưu danh sách ảnh chụp
    public static ArrayList<String> arrIMGName;//Lưu đường dẫn ảnh
   // private static Bitmap bmImageView;//Lưu ảnh preview
    private static List<ItemImage> listImagePreview;
    private static ItemsListAdapter myItemsImages, myItemsListAdapter2;
    public static JSONArray jsonListImage = new JSONArray(); // Lưu Json ảnh đã chụp thành công chờ save
    public static JSONArray listImageSave=new JSONArray(); //Lưu JSON ảnh đã save thành công
    public int bFinish;// = 0: là đang trong chế độ chụp ảnh = 1: Click vào nút Accept để thoát chụp / = 2: Close không lấy ảnh dù đã chụp
    private Rect activeArray;

    void setEventListener(CameraPreviewListener listener) {
        eventListener = listener;
    }

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    /**
     * Tag for the {@link Log}.
     */
    private static final String TAG = "CameraActivity";

    /**
     * Camera state: Showing camera preview.
     */
    private static final int STATE_PREVIEW = 0;

    /**
     * Camera state: Waiting for the focus to be locked.
     */
    private static final int STATE_WAITING_LOCK = 1;

    /**
     * Camera state: Waiting for the exposure to be precapture state.
     */
    private static final int STATE_WAITING_PRECAPTURE = 2;

    /**
     * Camera state: Waiting for the exposure state to be something other than precapture.
     */
    private static final int STATE_WAITING_NON_PRECAPTURE = 3;

    /**
     * Camera state: Picture was taken.
     */
    private static final int STATE_PICTURE_TAKEN = 4;

    /**
     * Max preview width that is guaranteed by Camera2 API
     */
    private static final int MAX_PREVIEW_WIDTH = 1920;

    /**
     * Max preview height that is guaranteed by Camera2 API
     */
    private static final int MAX_PREVIEW_HEIGHT = 1080;

    /**
     * {@link TextureView.SurfaceTextureListener} handles several lifecycle events on a
     * {@link TextureView}.
     */
    private final TextureView.SurfaceTextureListener mSurfaceTextureListener
            = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
            openCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
            configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {
        }

    };

    /**
     * ID of the current {@link CameraDevice}.
     */
    private String mCameraId;

    /**
     * An {@link AutoFitTextureView} for camera preview.
     */
    private AutoFitTextureView mTextureView;

    /**
     * A {@link CameraCaptureSession } for camera preview.
     */
    private CameraCaptureSession mCaptureSession;

    /**
     * A reference to the opened {@link CameraDevice}.
     */
    private CameraDevice mCameraDevice;

    /**
     * The {@link android.util.Size} of camera preview.
     */
    private Size mPreviewSize;

    /**
     * {@link CameraDevice.StateCallback} is called when {@link CameraDevice} changes its state.
     */
    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(CameraDevice cameraDevice) {
            // This method is called when the camera is opened.  We start camera preview here.
            mCameraOpenCloseLock.release();
            mCameraDevice = cameraDevice;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(CameraDevice cameraDevice, int error) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
            Activity activity = getActivity();
            if (null != activity) {
                activity.finish();
            }
        }

    };

    /**
     * An additional thread for running tasks that shouldn't block the UI.
     */
    private HandlerThread mBackgroundThread;

    /**
     * A {@link Handler} for running tasks in the background.
     */
    private Handler mBackgroundHandler;

    /**
     * An {@link ImageReader} that handles still image capture.
     */
    private ImageReader mImageReader;

    /**
     * This is the output file for our picture.
     */
    private File mFile;

    /**
     * This a callback object for the {@link ImageReader}. "onImageAvailable" will be called when a
     * still image is ready to be saved.
     */
    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener
            = new ImageReader.OnImageAvailableListener() {

        @Override
        public void onImageAvailable(ImageReader reader) {
            mBackgroundHandler.post(new ImageSaver(reader.acquireNextImage(), mFile));
        }

    };

    /**
     * {@link CaptureRequest.Builder} for the camera preview
     */
    private CaptureRequest.Builder mPreviewRequestBuilder;

    /**
     * {@link CaptureRequest} generated by {@link #mPreviewRequestBuilder}
     */
    private CaptureRequest mPreviewRequest;

    /**
     * The current state of camera state for taking pictures.
     *
     * @see #mCaptureCallback
     */
    private int mState = STATE_PREVIEW;

    /**
     * A {@link Semaphore} to prevent the app from exiting before closing the camera.
     */
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);

    /**
     * Whether the current camera device supports Flash or not.
     */
    private boolean mFlashSupported;

    /**
     * Orientation of the camera sensor
     */
    private int mSensorOrientation;

    /**
     * A {@link CameraCaptureSession.CaptureCallback} that handles events related to JPEG capture.
     */
    private CameraCaptureSession.CaptureCallback mCaptureCallback
            = new CameraCaptureSession.CaptureCallback() {

        //boolean areWeFocused = false;

        private void process(CaptureResult result) {
            switch (mState) {
                case STATE_PREVIEW: {
                    // Hiển thị ảnh focus chi hoạt động với 1 số máy, 1 số máy ko hoạt động như Asus
                    // We have nothing to do when the camera preview is working normally.
                    int afState = result.get(CaptureResult.CONTROL_AF_STATE);
                    //Log.d(TAG, "afState: " + afState);
                    if (CaptureResult.CONTROL_AF_TRIGGER_START == afState) {
                        activityMain.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imgTouch.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                    if (CaptureResult.CONTROL_AF_STATE_PASSIVE_FOCUSED == afState) {
                        {
                            activityMain.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imgTouch.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                    break;
                }
                case STATE_WAITING_LOCK: {
                    Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
                    if (afState == null) {
                        activityMain.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imgTouch.setVisibility(View.GONE);
                            }
                        });
                        captureStillPicture();
                    } else if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                            CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState) {
                        // CONTROL_AE_STATE can be null on some devices
                        Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                        if (aeState == null || aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                            mState = STATE_PICTURE_TAKEN;
                            activityMain.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imgTouch.setVisibility(View.GONE);
                                }
                            });
                            captureStillPicture();
                        } else {
                            activityMain.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imgTouch.setVisibility(View.GONE);
                                }
                            });
                            runPrecaptureSequence();
                        }
                    }
                    break;
                }
                case STATE_WAITING_PRECAPTURE: {
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null ||
                            aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                            aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED) {
                        mState = STATE_WAITING_NON_PRECAPTURE;
                    }
                    break;
                }
                case STATE_WAITING_NON_PRECAPTURE: {
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                        mState = STATE_PICTURE_TAKEN;
                        imgTouch.setVisibility(View.GONE);
                        captureStillPicture();
                    }
                    break;
                }
            }
        }

        @Override
        public void onCaptureProgressed(CameraCaptureSession session,
                                        CaptureRequest request,
                                        CaptureResult partialResult) {
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(CameraCaptureSession session,
                                       CaptureRequest request,
                                       TotalCaptureResult result) {
            process(result);
        }

    };

    /**
     * Shows a {@link Toast} on the UI thread.
     *
     * @param text The message to show
     */
    private static void showToast(final String text) {
        //final Activity activity = getActivity();
        if (activityMain != null) {
            activityMain.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activityMain, text, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Given {@code choices} of {@code Size}s supported by a camera, choose the smallest one that
     * is at least as large as the respective texture view size, and that is at most as large as the
     * respective max size, and whose aspect ratio matches with the specified value. If such size
     * doesn't exist, choose the largest one that is at most as large as the respective max size,
     * and whose aspect ratio matches with the specified value.
     *
     * @param choices           The list of sizes that the camera supports for the intended output
     *                          class
     * @param textureViewWidth  The width of the texture view relative to sensor coordinate
     * @param textureViewHeight The height of the texture view relative to sensor coordinate
     * @param maxWidth          The maximum width that can be chosen
     * @param maxHeight         The maximum height that can be chosen
     * @param aspectRatio       The aspect ratio
     * @return The optimal {@code Size}, or an arbitrary one if none were big enough
     */
    private static Size chooseOptimalSize(Size[] choices, int textureViewWidth,
                                          int textureViewHeight, int maxWidth, int maxHeight, Size aspectRatio) {

        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<Size>();
        // Collect the supported resolutions that are smaller than the preview Surface
        List<Size> notBigEnough = new ArrayList<Size>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
//        Log.d(TAG,"aspectRatio: "+w + " x "+h);
//        Log.d(TAG,"textureViewWidth: "+textureViewWidth + " textureViewHeight "+textureViewHeight);
//        Log.d(TAG,"maxWidth: "+maxWidth + " maxHeight "+maxHeight);
//        Log.d(TAG,"maxWidth: "+maxWidth + " maxHeight "+maxHeight);
        for (Size option : choices) {
            //Log.d(TAG,"option: "+option.getWidth() + " x "+option.getHeight());
//            if (option.getWidth() <= maxWidth && option.getHeight() <= maxHeight &&
//                    option.getHeight() == option.getWidth() * h / w) {
//                if (option.getWidth() >= textureViewWidth &&
//                        option.getHeight() >= textureViewHeight) {
//                    bigEnough.add(option);
//                } else {
//                    notBigEnough.add(option);
//                }
//            }
            if (option.getHeight() == option.getWidth() * h / w) {
                if (option.getHeight() >= textureViewHeight) {
                    bigEnough.add(option);
                } else {
                    notBigEnough.add(option);
                }
                break;
            }
        }
        // Pick the smallest of those big enough. If there is no one big enough, pick the
        // largest of those not big enough.
        if (bigEnough.size() > 0) {
            Log.d(TAG, "Set PreviewSize bigEnough: " + Collections.min(bigEnough, new CompareSizesByArea()).getWidth() + " x " + Collections.min(bigEnough, new CompareSizesByArea()).getHeight());
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else if (notBigEnough.size() > 0) {
            Log.d(TAG, "notBigEnough: " + Collections.min(notBigEnough, new CompareSizesByArea()).getWidth() + " x " + Collections.min(notBigEnough, new CompareSizesByArea()).getHeight());
            return Collections.max(notBigEnough, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    public static CameraActivity newInstance() {
        return new CameraActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        appResourcesPackage = getActivity().getPackageName();
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        view = inflater.inflate(getResources().getIdentifier("fragment_camera2_basic", "layout", appResourcesPackage), container, false);
        activityMain = getActivity();
        return inflater.inflate(getResources().getIdentifier("fragment_camera2_basic", "layout", appResourcesPackage), container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        //view.findViewById(getResources().getIdentifier("picture", "id", appResourcesPackage)).setOnClickListener(this);
        mTextureView = (AutoFitTextureView) view.findViewById(getResources().getIdentifier("texture", "id", appResourcesPackage));

        imgView = (ImageView) view.findViewById(getResources().getIdentifier("image_view", "id", appResourcesPackage));
        //frameContainerLayout = (FrameLayout) view.findViewById(getResources().getIdentifier("frame_container", "id", appResourcesPackage));
        btnPicture = (ImageButton) view.findViewById(getResources().getIdentifier("btnPicture", "id", appResourcesPackage));
        btnPicture2 = (ImageButton) view.findViewById(getResources().getIdentifier("btnPicture2", "id", appResourcesPackage));
        btnClose = (ImageButton) view.findViewById(getResources().getIdentifier("btnClose", "id", appResourcesPackage));
        btnRefresh = (ImageButton) view.findViewById(getResources().getIdentifier("btnRefresh", "id", appResourcesPackage));
        btnAccept = (ImageButton) view.findViewById(getResources().getIdentifier("btnAccept", "id", appResourcesPackage));
        tbInfo = (TextView) view.findViewById(getResources().getIdentifier("tbInfo", "id", appResourcesPackage));
        chbShowInfo = (CheckBox) view.findViewById(getResources().getIdentifier("chbShowInfo", "id", appResourcesPackage));
        tbDate = (TextView) view.findViewById(getResources().getIdentifier("tbDate", "id", appResourcesPackage));
        imgTouch = (ImageView) view.findViewById(getResources().getIdentifier("imgTouch", "id", appResourcesPackage));
        frame_buttom = (FrameLayout) view.findViewById(getResources().getIdentifier("frame_buttom", "id", appResourcesPackage));
        frame_camera = (RelativeLayout) view.findViewById(getResources().getIdentifier("frame_camera", "id", appResourcesPackage));
        frame_image = (RelativeLayout) view.findViewById(getResources().getIdentifier("frame_image", "id", appResourcesPackage));
        pane_image = (LinearLayout) view.findViewById(getResources().getIdentifier("pane_image", "id", appResourcesPackage));

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        tbDate.setText(df.format(new Date()));

//        File file = new File("/mnt/sdcard/SURVEY/2016_12_03/");
//
//        if (file.isDirectory()) {
//            listFile = file.listFiles();
//            mFileStrings = new String[listFile.length];
//
//            for (int i = 0; i < listFile.length; i++) {
//                mFileStrings[i] = listFile[i].getAbsolutePath();
//            }
//        }
        AllEvent();
        arrIMG = new ArrayList<Bitmap>();
        list_image = (ListView) view.findViewById(getResources().getIdentifier("list_image", "id", appResourcesPackage));//findViewById(R.id.listview1);
        //area1 = (LinearLayout)view.findViewById(getResources().getIdentifier("pane1", "id", appResourcesPackage));//findViewById(R.id.pane1);
        //area1.setOnDragListener(myOnDragListener);

        //initItems();
        listImagePreview = new ArrayList<ItemImage>();
        myItemsImages = new ItemsListAdapter(getActivity(), listImagePreview);
        list_image.setAdapter(myItemsImages);

        //Auto scroll to end of ListView
        list_image.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        list_image.setOnItemClickListener(listOnItemClickListener);

        list_image.setOnItemLongClickListener(myOnItemLongClickListener);

        pane_image.setOnDragListener(myOnDragListener);
        frame_camera.setOnDragListener(myOnDragListener);
        SetInfoDisplay();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    LinearLayout area1, area2;

    //objects passed in Drag and Drop operation
    class PassObject {
        View view;
        ItemImage item;

        PassObject(View v, ItemImage i) {
            view = v;
            item = i;
        }
    }

    static class ViewHolder {
        ImageView icon;
        TextView text;
    }

    AdapterView.OnItemLongClickListener myOnItemLongClickListener = new AdapterView.OnItemLongClickListener() {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view,
                                       int position, long id) {
            ItemImage selectedItem = (ItemImage) (parent.getItemAtPosition(position));
            PassObject passObj = new PassObject(view, selectedItem);
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
            view.startDrag(data, shadowBuilder, passObj, 0);
            return true;
        }

    };

    View.OnDragListener myOnDragListener = new View.OnDragListener() {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    //prompt.append("ACTION_DRAG_STARTED: " + area  + "\n");
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    //prompt.append("ACTION_DRAG_ENTERED: " + area  + "\n");
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    //prompt.append("ACTION_DRAG_EXITED: " + area  + "\n");
                    break;
                case DragEvent.ACTION_DROP:
                    try {
                        PassObject passObj = (PassObject) event.getLocalState();
                        View view = passObj.view;
                        ItemImage passedItem = passObj.item;
                        ListView oldParent = (ListView) view.getParent();
                        RelativeLayout newParent = (RelativeLayout) v;
                        if (oldParent.equals(list_image)) {
                            if (newParent.equals(frame_camera)) {
                                int i = listImagePreview.indexOf(passedItem);
                                removeItemToList(listImagePreview, passedItem);
                                myItemsImages.notifyDataSetChanged();
                                arrIMG.remove(arrIMG.get(i));
                                jsonListImage.remove(i);
//                                JSONArray arr = jsonListImage;
//                                jsonListImage = new JSONArray();
//                                for (int j = 0; j < arr.length(); j++) {
//                                    if (j != i) {
//                                        JSONObject obj = arr.getJSONObject(j);
//                                        jsonListImage.put(obj);
//                                    }
//                                }
                                imgView.setVisibility(View.GONE);
                                btnPicture.setVisibility(View.VISIBLE);
                                btnPicture2.setVisibility(View.VISIBLE);
                                btnRefresh.setVisibility(View.GONE);
                                if (jsonListImage.length() > 0)
                                    btnAccept.setVisibility(View.VISIBLE);
                                else
                                    btnAccept.setVisibility(View.GONE);

                                showToast("Remove item " + i + " successful!");
                            }
                        }
                    } catch (Exception ex) {
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    //prompt.append("ACTION_DRAG_ENDED: " + area  + "\n");
                default:
                    break;
            }

            return true;
        }

    };

    AdapterView.OnItemClickListener listOnItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            try {
                String strRotate = ((ItemImage) (parent.getItemAtPosition(position))).mName;
                Log.d(TAG, "ID: " + position + " Rotate: " + strRotate);
                Bitmap bm = arrIMG.get(position);
                Bitmap bmImageView;
                Matrix matrixView = new Matrix();
                matrixView.postRotate(-90 - Integer.parseInt(strRotate));
                if (bm.getWidth() > bm.getHeight()) {
                    bmImageView = Bitmap.createScaledBitmap(bm, widthPreview, heightPreview, true);
                    bmImageView = Bitmap.createBitmap(bmImageView, 0, 0, widthPreview, heightPreview, matrixView, true);
                } else {
                    bmImageView = Bitmap.createScaledBitmap(bm, heightPreview, widthPreview, true);
                    bmImageView = Bitmap.createBitmap(bmImageView, 0, 0, heightPreview, widthPreview, matrixView, true);
                }
                imgView.setImageBitmap(bmImageView);
                imgView.setVisibility(View.VISIBLE);
                btnPicture.setVisibility(View.GONE);
                btnPicture2.setVisibility(View.GONE);
                btnRefresh.setVisibility(View.VISIBLE);
                btnAccept.setVisibility(View.VISIBLE);
                tbInfo.setVisibility(View.GONE);
                chbShowInfo.setVisibility(View.GONE);
            } catch (Exception ex) {

            }

        }

    };

    private static boolean removeItemToList(List<ItemImage> l, ItemImage it) {
        boolean result = l.remove(it);
        return result;
    }

    private static boolean addItemToList(List<ItemImage> l, ItemImage it) {
        boolean result = l.add(it);
        return result;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");
        ImageParent = getActivity().getExternalFilesDir(null).toString();
        mFile = new File(ImageParent, "IMG_" + df.format(new Date()) + ".jpg");
    }

    @Override
    public void onResume() {
        super.onResume();
        startBackgroundThread();
        // When the screen is turned off and turned back on, the SurfaceTexture is already
        // available, and "onSurfaceTextureAvailable" will not be called. In that case, we can open
        // a camera and start preview from here (otherwise, we wait until the surface is ready in
        // the SurfaceTextureListener).
        if (mTextureView.isAvailable()) {
            openCamera(mTextureView.getWidth(), mTextureView.getHeight());
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    @Override
    public void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();
        System.gc();
    }

    private void requestCameraPermission() {
//        if (FragmentCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
//            new ConfirmationDialog().show(getChildFragmentManager(), FRAGMENT_DIALOG);
//        } else {
//            FragmentCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
//                    REQUEST_CAMERA_PERMISSION);
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                ErrorDialog.newInstance("request_permission")
                        .show(getChildFragmentManager(), FRAGMENT_DIALOG);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    void AllEvent() {


        //Sự kiện khi xoay màn hình trả về giá trị xoay vào iRotate
        OrientationEventListener mOrientationListener = new OrientationEventListener(getActivity().getApplicationContext()) {
            @Override
            public void onOrientationChanged(int orientation) {
                if ((orientation >= 0 && orientation <= 45) || (orientation >= 316 && orientation <= 360)) {
                    iRotate = 0;// Điên thoại đang thẳng đứng
                    rotateText(iRotate);
                    //Log.d(TAG, Integer.toString(iRotate));
                }
                if (orientation >= 46 && orientation <= 135) {
                    iRotate = 90; // Điện thoại đang xoay ngang
                    rotateText(iRotate);
                    //Log.d(TAG, Integer.toString(iRotate));
                }
                if (orientation >= 136 && orientation <= 225) {
                    iRotate = 180;// Điên thoại đang xoay ngược theo phương đứng
                    rotateText(iRotate);
                    //Log.d(TAG, Integer.toString(iRotate));
                }
                if (orientation >= 226 && orientation <= 315) {
                    iRotate = 270;// Điện thoại đang xoay ngang theo chiều ngược lại
                    rotateText(iRotate);
                    //Log.d(TAG, Integer.toString(iRotate));
                }

            }
        };
        if (mOrientationListener.canDetectOrientation()) {
            mOrientationListener.enable();
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //Event btnPicture click
                btnPicture.setClickable(true);
                btnPicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Log.d(TAG, "Take photo start!");
                            btnAccept.setVisibility(View.GONE);
                            CaculatorInfo();
                            takePicture();
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                            eventListener.RestartCamera();
                        }
                    }
                });
                btnPicture2.setClickable(true);
                btnPicture2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Log.d(TAG, "Take photo start!");
                            btnAccept.setVisibility(View.GONE);
                            CaculatorInfo();
                            takePicture();
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                            eventListener.RestartCamera();
                        }
                    }
                });
                //Event btnClose click
                btnClose.setClickable(true);
                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (jsonListImage.length() > 0) {
                                //Put up the Yes/No message box
                                AlertDialog.Builder builder = new AlertDialog.Builder(activityMain);
                                builder.setTitle("Bạn đã chụp được: " + jsonListImage.length() + " ảnh.")
                                        .setMessage("Chọn YES để lưu lại ảnh \nChọn NO để quay lại và không lưu ảnh?")
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                for (int i = 0; i < jsonListImage.length(); i++) {
                                                    JSONObject jo = null;
                                                    try {
                                                        jo = (JSONObject) jsonListImage.get(i);
                                                        if (!jo.getBoolean("state")) {
                                                            AlertDialog.Builder builder = new AlertDialog.Builder(activityMain,android.R.style.Theme_Material_Light_Dialog_Alert);
                                                            builder.setTitle("Đang lưu ảnh số: " + (i + 1) + "...")
                                                                    .setMessage("Vui lòng chờ trong giây lát rồi thoát!!!")
                                                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                                                    .show();
                                                            return;
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                bFinish = 1;
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                //Yes button clicked, do something
                                                bFinish = 2;
                                            }
                                        })
                                        .show();
                            } else {
                                bFinish = 2;
                            }
                            imgView.setVisibility(View.GONE);
                            btnAccept.setVisibility(View.GONE);
                            btnPicture.setVisibility(View.VISIBLE);
                            btnPicture2.setVisibility(View.VISIBLE);
                            btnRefresh.setVisibility(View.GONE);
                            arrIMG.clear();
                            unlockFocus();
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                            eventListener.RestartCamera();
                        }


                    }
                });
                //Event btnRefresh click
                btnRefresh.setClickable(true);
                btnRefresh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            unlockFocus();
                            imgView.setVisibility(View.GONE);
                            btnPicture.setVisibility(View.VISIBLE);
                            btnPicture2.setVisibility(View.VISIBLE);
                            btnRefresh.setVisibility(View.GONE);
                            if (jsonListImage.length() > 0)
                                btnAccept.setVisibility(View.VISIBLE);
                            else
                                btnAccept.setVisibility(View.GONE);
                            if (bShowInfo) {
                                tbInfo.setVisibility(View.VISIBLE);
                                chbShowInfo.setVisibility(View.VISIBLE);
                            } else {
                                tbInfo.setVisibility(View.GONE);
                                chbShowInfo.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                            eventListener.RestartCamera();
                        }
                    }
                });
                //Event Accept click
                btnAccept.setClickable(true);
                btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            //savePhoto(bmPhoto);
                            //saveAllPhoto();
                            for (int i = 0; i < jsonListImage.length(); i++) {
                                JSONObject jo = (JSONObject) jsonListImage.get(i);
                                if (!jo.getBoolean("state")) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(activityMain,android.R.style.Theme_Material_Light_Dialog_Alert);
                                    builder.setTitle("Đang lưu ảnh số: " + (i + 1) + "...")
                                            .setMessage("Vui lòng chờ trong giây lát rồi thoát!!!")
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .show();
                                    return;
                                }
                            }
                            bFinish = 1;
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                            eventListener.RestartCamera();
                        }
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
                                Log.d(TAG, "W: " + infoWidth + " H: " + infoHeight + " infoLeft: " + infoLeft + " infoTop: " + infoTop
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
                frame_camera.setClickable(true);
                frame_camera.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                            Log.d(TAG, "onTouch: click");
//                            int iLeft = (int) event.getX();
//                            int iTop = (int) event.getY();
//                            unlockFocus();
                        }
                        return true;
                    }
                });
            }
        });
    }

    void EnableTakePicture() {
        btnPicture.setVisibility(View.VISIBLE);
        btnPicture2.setVisibility(View.VISIBLE);
    }

    void DisableTakePicture() {
        btnPicture.setVisibility(View.GONE);
        btnPicture2.setVisibility(View.GONE);
    }

    //Xoay text hien thi loi va text hien thi ngay thang
    void rotateText(int iRotate) {
        //Xoay các nút, text lỗi, ngày tháng hiện thị trên form chụp ảnh
        btnPicture.setRotation(270 - iRotate);
        btnPicture2.setRotation(270 - iRotate);
        btnClose.setRotation(270 - iRotate);
        btnRefresh.setRotation(270 - iRotate);
        btnAccept.setRotation(270 - iRotate);
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
            DisableTakePicture();
        } else if (iRotate == 90) {
            iLeft = tbDate.getMeasuredWidth() / 2;
            iTop = tbDate.getMeasuredHeight();
            DisableTakePicture();
        } else if (iRotate == 180) {
            iLeft = tbDate.getMeasuredHeight() / 2;
            iTop = heightPreview - tbDate.getMeasuredWidth();
            DisableTakePicture();
        } else if (iRotate == 270) {
            iLeft = widthPreview - tbDate.getMeasuredWidth() - tbDate.getMeasuredWidth() / 2;
            iTop = heightPreview - tbDate.getMeasuredHeight() * 2;
            EnableTakePicture();
        }
        para.setMargins(iLeft, iTop, iRight, iBottom);//Left,Top,Right,Bottom
        tbDate.setLayoutParams(para);
        tbDate.measure(0, 0);
    }

    /**
     * Sets up member variables related to camera.
     *
     * @param width  The width of available size for camera preview
     * @param height The height of available size for camera preview
     */
    private void setUpCameraOutputs(int width, int height) {
        Activity activity = getActivity();
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

                // We don't use a front facing camera in this sample.
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }

                activeArray = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map == null) {
                    continue;
                }
                Size largest = new Size(0, 0);
                // For still image captures, we use the largest available size.
                for (Size size : map.getOutputSizes(ImageFormat.JPEG)
                        ) {
                    if ((int) size.getWidth() * 3 == (int) size.getHeight() * 4 && Math.round(((double) size.getWidth() * (double) size.getHeight()) / (double) 1024000) == 5) {
                        largest = size;
                        Log.d(TAG, "PictureSize= " + size.getWidth() + " x " + size.getHeight());
                    }
                }
                //Size largest = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)), new CompareSizesByArea());
                mImageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(), ImageFormat.JPEG, /*maxImages*/2);
                mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mBackgroundHandler);

                // Find out if we need to swap dimension to get the preview size relative to sensor
                // coordinate.
                int displayRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
                //noinspection ConstantConditions
                mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                boolean swappedDimensions = false;
                switch (displayRotation) {
                    case Surface.ROTATION_0:
                    case Surface.ROTATION_180:
                        if (mSensorOrientation == 90 || mSensorOrientation == 270) {
                            swappedDimensions = true;
                        }
                        break;
                    case Surface.ROTATION_90:
                    case Surface.ROTATION_270:
                        if (mSensorOrientation == 0 || mSensorOrientation == 180) {
                            swappedDimensions = true;
                        }
                        break;
                    default:
                        Log.e(TAG, "Display rotation is invalid: " + displayRotation);
                }

                Point displaySize = new Point();
                activity.getWindowManager().getDefaultDisplay().getSize(displaySize);
                int rotatedPreviewWidth = width;
                int rotatedPreviewHeight = height;
                int maxPreviewWidth = displaySize.x;
                int maxPreviewHeight = displaySize.y;

                if (swappedDimensions) {
                    rotatedPreviewWidth = height;
                    rotatedPreviewHeight = width;
                    maxPreviewWidth = displaySize.y;
                    maxPreviewHeight = displaySize.x;
                }

                if (maxPreviewWidth > MAX_PREVIEW_WIDTH) {
                    maxPreviewWidth = MAX_PREVIEW_WIDTH;
                }

                if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) {
                    maxPreviewHeight = MAX_PREVIEW_HEIGHT;
                }

                // Danger, W.R.! Attempting to use too large a preview size could  exceed the camera
                // bus' bandwidth limitation, resulting in gorgeous previews but the storage of
                // garbage capture data.
                mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                        rotatedPreviewWidth, rotatedPreviewHeight, maxPreviewWidth,
                        maxPreviewHeight, largest);

                // We fit the aspect ratio of TextureView to the size of preview we picked.
                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    mTextureView.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
                    Log.d(TAG, "mTextureView LANDSCAPE: " + mTextureView.getMeasuredWidth() + " x " + mTextureView.getMeasuredHeight());
                } else {
                    mTextureView.setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());
                    Log.d(TAG, "mTextureView: " + mPreviewSize.getHeight() + " x " + mPreviewSize.getWidth());
                }
                heightPreview = mTextureView.getMeasuredHeight();
                widthPreview = mTextureView.getMeasuredHeight() * 4 / 3;
                pictureWidth = largest.getWidth();
                pictureHeight = largest.getHeight();
                // Tính tỉ lệ kích thước text vẽ lên ảnh
                tbInfo.measure(0, 0);
                infoFontSize = (int) tbInfo.getTextSize();
                infoFontSizePicture = Math.round((int) infoFontSize * (int) pictureWidth / widthPreview);
                infoWidth = (int) tbInfo.getMeasuredWidth();
                infoHeight = (int) tbInfo.getMeasuredHeight();
                infoWidthPicture = Math.round((int) infoWidth * (int) pictureWidth / widthPreview);
                infoHeightPicture = Math.round((int) infoHeight * (int) pictureHeight / heightPreview);

                listPreviewWidth = displaySize.x - widthPreview;
                listPreviewHeight = listPreviewWidth * 3 / 4;
                FrameLayout.LayoutParams para = new FrameLayout.LayoutParams(listPreviewWidth, heightPreview);
                para.gravity = Gravity.RIGHT;
                frame_image.setLayoutParams(para);

                FrameLayout.LayoutParams paraTouch = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                paraTouch.gravity = Gravity.CENTER;
                paraTouch.setMargins(0, 0, listPreviewWidth / 2, 0);
                imgTouch.setLayoutParams(paraTouch);
                // Check if the flash is supported.
                Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                mFlashSupported = available == null ? false : available;

                mCameraId = cameraId;
                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            // Currently an NPE is thrown when the Camera2API is used but not supported on the
            // device this code runs.
//            ErrorDialog.newInstance(getString(R.string.camera_error))
//                    .show(getChildFragmentManager(), FRAGMENT_DIALOG);
        }
    }

    //    /**
//     * Opens the camera specified by {@link Camera2BasicFragment#mCameraId}.
//     */
    private void openCamera(int width, int height) {
//        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
//                != PackageManager.PERMISSION_GRANTED) {
//            requestCameraPermission();
//            return;
//        }
        setUpCameraOutputs(width, height);
        configureTransform(width, height);
        Activity activity = getActivity();
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            manager.openCamera(mCameraId, mStateCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera opening.", e);
        }
    }

    /**
     * Closes the current {@link CameraDevice}.
     */
    private void closeCamera() {
        try {
            mCameraOpenCloseLock.acquire();
            if (null != mCaptureSession) {
                mCaptureSession.close();
                mCaptureSession = null;
            }
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (null != mImageReader) {
                mImageReader.close();
                mImageReader = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
            mCameraOpenCloseLock.release();
        }
    }

    /**
     * Starts a background thread and its {@link Handler}.
     */
    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    /**
     * Stops the background thread and its {@link Handler}.
     */
    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new {@link CameraCaptureSession} for camera preview.
     */
    private void createCameraPreviewSession() {
        try {
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;

            // We configure the size of default buffer to be the size of camera preview we want.
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());

            // This is the output Surface we need to start preview.
            Surface surface = new Surface(texture);

            // We set up a CaptureRequest.Builder with the output Surface.
            mPreviewRequestBuilder
                    = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(surface);

            // Here, we create a CameraCaptureSession for camera preview.
            mCameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                            // The camera is already closed
                            if (null == mCameraDevice) {
                                return;
                            }

                            // When the session is ready, we start displaying the preview.
                            mCaptureSession = cameraCaptureSession;
                            try {

                                // Auto focus should be continuous for camera preview.
                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
//                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_ANTIBANDING_MODE, CaptureRequest.CONTROL_AE_ANTIBANDING_MODE_AUTO);
//
//                                // control.aeExposureCompensation
//                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, 0);
//
//                                // control.aeLock
//                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_LOCK, false);
//
//                                // control.aePrecaptureTrigger
//                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER, CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_IDLE);
//
//                                // control.afTrigger
//                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_IDLE);
//
//                                // control.awbMode
//                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_AUTO);
//
//                                // control.awbLock
//                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AWB_LOCK, false);
//                                MeteringRectangle[] activeRegions =  new MeteringRectangle[] {
//                                        new MeteringRectangle(/*x*/0, /*y*/0, /*width*/activeArray.width() - 1,
//                    /*height*/activeArray.height() - 1,/*weight*/1)};
//                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_REGIONS, activeRegions);
//                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AWB_REGIONS, activeRegions);
//                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_REGIONS, activeRegions);
//
//                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
//                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO);
//                                mPreviewRequestBuilder.set(CaptureRequest.STATISTICS_FACE_DETECT_MODE, CaptureRequest.STATISTICS_FACE_DETECT_MODE_OFF);
//                                //mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION,12);


                                // Flash is automatically enabled when necessary.
                                //setAutoFlash(mPreviewRequestBuilder);

                                // Finally, we start displaying the camera preview.
                                mPreviewRequest = mPreviewRequestBuilder.build();
                                mCaptureSession.setRepeatingRequest(mPreviewRequest,
                                        mCaptureCallback, mBackgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(
                                CameraCaptureSession cameraCaptureSession) {
                            showToast("Failed");
                        }
                    }, null
            );
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Configures the necessary {@link android.graphics.Matrix} transformation to `mTextureView`.
     * This method should be called after the camera preview size is determined in
     * setUpCameraOutputs and also the size of `mTextureView` is fixed.
     *
     * @param viewWidth  The width of `mTextureView`
     * @param viewHeight The height of `mTextureView`
     */
    private void configureTransform(int viewWidth, int viewHeight) {
        Activity activity = getActivity();
        if (null == mTextureView || null == mPreviewSize || null == activity) {
            return;
        }
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
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

    /**
     * Initiate a still image capture.
     */
    public void takePicture() {
        lockFocus();
    }

    /**
     * Lock the focus as the first step for a still image capture.
     */
    private void lockFocus() {
        try {
            // This is how to tell the camera to lock focus.
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the lock.
            mState = STATE_WAITING_LOCK;
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Run the precapture sequence for capturing a still image. This method should be called when
     * we get a response in {@link #mCaptureCallback} from {@link #lockFocus()}.
     */
    private void runPrecaptureSequence() {
        try {
            // This is how to tell the camera to trigger.
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                    CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the precapture sequence to be set.
            mState = STATE_WAITING_PRECAPTURE;
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Capture a still picture. This method should be called when we get a response in
     * {@link #mCaptureCallback} from both {@link #lockFocus()}.
     */
    private void captureStillPicture() {
        try {
            final Activity activity = getActivity();
            if (null == activity || null == mCameraDevice) {
                return;
            }
            // This is the CaptureRequest.Builder that we use to take a picture.
            final CaptureRequest.Builder captureBuilder =
                    mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(mImageReader.getSurface());
            //Set the JPEG quality here like so
            captureBuilder.set(CaptureRequest.JPEG_QUALITY, (byte) 100);

            // Use the same AE and AF modes as the preview.
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            setAutoFlash(captureBuilder);
            // Orientation
            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getOrientation(rotation));

            CameraCaptureSession.CaptureCallback CaptureCallback = new CameraCaptureSession.CaptureCallback() {

                @Override
                public void onCaptureCompleted(CameraCaptureSession session,
                                               CaptureRequest request,
                                               TotalCaptureResult result) {
                    //showToast("Saved: " + mFile);
                    //Log.d(TAG, mFile.toString());
                    unlockFocus();
                }
            };

            mCaptureSession.stopRepeating();
            mCaptureSession.capture(captureBuilder.build(), CaptureCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the JPEG orientation from the specified screen rotation.
     *
     * @param rotation The screen rotation.
     * @return The JPEG orientation (one of 0, 90, 270, and 360)
     */
    private int getOrientation(int rotation) {
        // Sensor orientation is 90 for most devices, or 270 for some devices (eg. Nexus 5X)
        // We have to take that into account and rotate JPEG properly.
        // For devices with orientation of 90, we simply return our mapping from ORIENTATIONS.
        // For devices with orientation of 270, we need to rotate the JPEG 180 degrees.
        return (ORIENTATIONS.get(rotation) + mSensorOrientation + 270) % 360;
    }

    /**
     * Unlock the focus. This method should be called when still image capture sequence is
     * finished.
     */
    private void unlockFocus() {
        try {
            // Reset the auto-focus trigger
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            //setAutoFlash(mPreviewRequestBuilder);
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
            // After this, the camera will go back to the normal state of preview.
            mState = STATE_PREVIEW;
            mCaptureSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void setAutoFlash(CaptureRequest.Builder requestBuilder) {
        if (mFlashSupported) {
            // Hiện tại không bật FLASH
//            requestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
//                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);

        }
    }

    /**
     * Saves a JPEG {@link Image} into the specified {@link File}.
     */
    private static class ImageSaver implements Runnable {

        /**
         * The JPEG image
         */
        private final Image mImage;
        /**
         * The file we save the image into.
         */
        private final File mFile;

        public ImageSaver(Image image, File file) {
            mImage = image;
            mFile = file;
        }

        private void getNewImageName() {
        }

        @Override
        public void run() {
            activityMain.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.remaining()];
                        buffer.get(bytes);
                        Bitmap picture = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);

                        Matrix matrix = new Matrix();
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

                        //Draw
                        Bitmap bm = Bitmap.createBitmap(pictureWidth, pictureHeight, Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bm);
                        canvas.drawBitmap(picture, 0, 0, null);
                        SimpleDateFormat now = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        String strDate = now.format(new Date());

                        //Paint border
                        strokePaint = new Paint();
                        strokePaint.setColor(Color.BLACK);
                        strokePaint.setTextSize(infoFontSizePicture);
                        strokePaint.setTextAlign(Paint.Align.CENTER);
                        strokePaint.setTypeface(Typeface.DEFAULT_BOLD);
                        strokePaint.setStyle(Paint.Style.STROKE);
                        strokePaint.setStrokeWidth(5);

                        //Paint text
                        textPaint = new Paint();
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
                        Matrix matrixView = new Matrix();
                        matrixView.postRotate(-90 - iRotate);
                        Bitmap bmImagePreview;
                        Bitmap bmPreview;
                        SimpleDateFormat dt = new SimpleDateFormat("yyyy_MM_dd");
                        String folder = "/mnt/sdcard/SURVEY/" + dt.format(new Date());
                        dt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                        String photoName = "";
                        if (bm.getWidth() > bm.getHeight()) {
                            bmPreview = Bitmap.createScaledBitmap(bm, widthPreview, heightPreview, true);
                            bmPreview = Bitmap.createBitmap(bmPreview, 0, 0, widthPreview, heightPreview, matrixView, true);
                            bmImagePreview = Bitmap.createScaledBitmap(bm, listPreviewWidth, listPreviewHeight, true);
                            bmImagePreview = Bitmap.createBitmap(bmImagePreview, 0, 0, listPreviewWidth, listPreviewHeight, matrixView, true);
                            photoName = ImageName + "_" + dt.format(new Date()) + "_4x3.jpg";
                        } else {
                            bmPreview = Bitmap.createScaledBitmap(bm, heightPreview, widthPreview, true);
                            bmPreview = Bitmap.createBitmap(bmPreview, 0, 0, heightPreview, widthPreview, matrixView, true);
                            bmImagePreview = Bitmap.createScaledBitmap(bm, listPreviewHeight, listPreviewWidth, true);
                            bmImagePreview = Bitmap.createBitmap(bmImagePreview, 0, 0, listPreviewHeight, listPreviewWidth, matrixView, true);
                            photoName = ImageName + "_" + dt.format(new Date()) + "_3x4.jpg";
                        }
                        arrIMG.add(bmPreview);
                        //savePhoto(bm);
                        JSONObject jo = new JSONObject();
                        try {
                            //jo.put("img_" + jsonListImage.length(), folder+"/"+photoName);
                            jo.put("img", folder + "/" + photoName);
                            jo.put("state", false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        jsonListImage.put(jo);
                        btnAccept.setVisibility(View.VISIBLE);
                        Log.d(TAG, "Take photo successful!");
                        ItemImage item = new ItemImage(bmImagePreview, Integer.toString(iRotate));
                        addItemToList(listImagePreview, item);
                        myItemsImages.notifyDataSetChanged();
                        SavePhotoAsync saveImage = new SavePhotoAsync(2,activityMain, bm, photoName, (jsonListImage.length() == 0 ? 0 : jsonListImage.length() - 1), pictureWidthSmall, pictureHeightSmall, folder);
                        saveImage.execute();

                    } catch (OutOfMemoryError ex) {
                        showToast("Tràn bộ nhớ! Lưu ảnh - quay lại - sau đó tiếp tục chụp!");
                        if (jsonListImage.length() > 0)
                            btnAccept.setVisibility(View.VISIBLE);
                        else
                            btnAccept.setVisibility(View.GONE);
                        System.gc();
                    }
                }
            });
        }

    }

    private static ItemsListAdapter listAdapter;

    private static void savePhoto(Bitmap bmPhoto) {
        try {
            int intWidth = bmPhoto.getWidth();
            int intHeight = bmPhoto.getHeight();
            SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd");
            df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String photoName = df.format(new Date());
            String ImageNameSmall = ImageName + "_" + photoName + "_small.jpg";
            if (intWidth < intHeight) {
                intWidth = pictureHeightSmall;
                intHeight = pictureWidthSmall;
                ImageName = ImageName + "_" + photoName + "_3x4.jpg";
            } else {
                intWidth = pictureWidthSmall;
                intHeight = pictureHeightSmall;
                ImageName = ImageName + "_" + photoName + "_4x3.jpg";
            }
            Bitmap bmPhotoSmall = Bitmap.createScaledBitmap(bmPhoto, intWidth, intHeight, true);
            File myDir = new File("/mnt/sdcard/SURVEY/" + df.format(new Date()));
            myDir.mkdirs();

            File imageFile = new File(myDir, ImageName);
            File imageFileSmall = new File(myDir, ImageNameSmall);
            FileOutputStream out = null;
            FileOutputStream outSmall = null;
            try {
                out = new FileOutputStream(imageFile);
                outSmall = new FileOutputStream(imageFileSmall);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //Save bitmap to library
            bmPhoto.compress(Bitmap.CompressFormat.JPEG, 100, out);
            bmPhotoSmall.compress(Bitmap.CompressFormat.JPEG, 100, outSmall);
            bmPhoto.recycle();
            bmPhotoSmall.recycle();
            System.gc();
            img = imageFile.getPath();
            imgSmall = imageFileSmall.getPath();
            Log.d(TAG, "Save photo successful!");

            imgView.setVisibility(View.GONE);
            btnAccept.setVisibility(View.GONE);
            btnPicture.setVisibility(View.VISIBLE);
            btnPicture2.setVisibility(View.VISIBLE);

            JSONObject jo = new JSONObject();
            try {
                jo.put("img", img);
                jo.put("imgSmall", imgSmall);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONArray ja = new JSONArray();
            ja.put(jo);
            //bTakeSuccess = true;
            //eventListener.hideCamera();
            eventListener.onPictureTaken(ja);

        } catch (RuntimeException ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    private void saveAllPhoto() {
        try {
            JSONArray ja = new JSONArray();
            for (int i = 0; i < arrIMG.size(); i++) {
                Bitmap bm = arrIMG.get(i);
                Bitmap bmSmall = Bitmap.createScaledBitmap(bm, pictureWidthSmall, pictureHeightSmall, true);
                SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd");
                File myDir = new File("/mnt/sdcard/SURVEY/" + df.format(new Date()));
                myDir.mkdirs();
                df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                String photoName = df.format(new Date());
                String ImageNameSmall = ImageName + "_" + photoName + "_small.jpg";
                String ImageNameLarge = ImageName + "_" + photoName + ".jpg";
                File imageFile = new File(myDir, ImageNameLarge);
                File imageFileSmall = new File(myDir, ImageNameSmall);
                FileOutputStream out = null;
                FileOutputStream outSmall = null;
                try {
                    out = new FileOutputStream(imageFile);
                    outSmall = new FileOutputStream(imageFileSmall);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                //Save bitmap to library
                bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
                bmSmall.compress(Bitmap.CompressFormat.JPEG, 100, outSmall);
                img = imageFile.getPath();
                imgSmall = imageFileSmall.getPath();

                imgView.setVisibility(View.GONE);
                btnAccept.setVisibility(View.GONE);
                btnPicture.setVisibility(View.VISIBLE);
                btnPicture2.setVisibility(View.VISIBLE);

                JSONObject jo = new JSONObject();
                try {
                    jo.put("img_" + i, img);
                    Log.d(TAG, "img_" + i + ": " + img);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ja.put(jo);
            }
            Toast.makeText(getActivity(), "Save photo successful!", Toast.LENGTH_SHORT).show();
            //bTakeSuccess = true;
            arrIMG.clear();
            //eventListener.hideCamera();
            eventListener.onPictureTaken(ja);
        } catch (RuntimeException ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    //Tính tỉ lệ hiển thị info trên ảnh đầu ra
    public void CaculatorInfo() {
        //Tinh Left top trên ảnh đầu ra
        infoLeft = tbInfo.getLeft();
        infoTop = tbInfo.getTop();
        if (iRotate == 270) {
            infoLeftPicture = Math.round(infoLeft * pictureWidth / widthPreview);
            infoToptPicture = Math.round(infoTop * pictureHeight / heightPreview);
        } else if (iRotate == 180) {
            infoLeftPicture = Math.round((infoTop - (infoWidth - infoHeight) / 2) * pictureHeight / heightPreview);
            infoToptPicture = Math.round((widthPreview - infoLeft - infoWidth + (infoWidth - infoHeight) / 2) * pictureWidth / widthPreview);
        } else if (iRotate == 90) {
            infoToptPicture = Math.round((heightPreview - infoTop - infoHeight) * pictureWidth / widthPreview);
            infoLeftPicture = Math.round((widthPreview - infoLeft - infoWidth) * pictureHeight / heightPreview);
        } else if (iRotate == 0) {
            infoLeftPicture = Math.round((heightPreview - infoTop - infoHeight - Math.round((infoWidth - infoHeight) / 2)) * pictureHeight / heightPreview);
            infoToptPicture = Math.round((infoLeft * pictureWidth / widthPreview) + (infoWidthPicture - infoHeightPicture) / 2);
        }

        Log.d(TAG, "CaculatorInfo W: " + infoWidth + " H: " + infoHeight + " infoLeft: " + infoLeft + " infoTop: " + infoTop
                + " infoLeftPicture: " + infoLeftPicture + " infoToptPicture:" + infoToptPicture);
    }

    //Thiết lập hiển thị Info lên trên ảnh
    public void SetInfoDisplay() {
        jsonListImage = new JSONArray();
        arrIMG.clear();
        bFinish = 0;
        imgView.setVisibility(View.GONE);
        btnAccept.setVisibility(View.GONE);
        btnPicture.setVisibility(View.VISIBLE);
        btnPicture2.setVisibility(View.VISIBLE);
        btnRefresh.setVisibility(View.GONE);
        listImagePreview.clear();
        myItemsImages.notifyDataSetChanged();

        if (bShowInfo) {
            chbShowInfo.setVisibility(View.VISIBLE);
            chbShowInfo.setChecked(true);
            tbInfo.setText(Info);
            tbInfo.setVisibility(View.VISIBLE);

            //Tinh Left top trên ảnh đầu ra
//            tbInfo.measure(0, 0);
//            infoLeft = tbInfo.getLeft();
//            infoTop = tbInfo.getTop();
//            if (iRotate == 270) {
//                infoLeftPicture = Math.round(infoLeft * pictureWidth / widthPreview);
//                infoToptPicture = Math.round(infoTop * pictureHeight / heightPreview);
//            }
//            if (iRotate == 180) {
//                infoLeftPicture = Math.round((infoTop - (infoWidth - infoHeight) / 2) * pictureHeight / heightPreview);
//                infoToptPicture = Math.round((widthPreview - infoLeft - infoWidth + (infoWidth - infoHeight) / 2) * pictureWidth / widthPreview);
//            }
//            if (iRotate == 90) {
//                infoToptPicture = Math.round((heightPreview - infoTop - infoHeight) * pictureWidth / widthPreview);
//                infoLeftPicture = Math.round((widthPreview - infoLeft - infoWidth) * pictureHeight / heightPreview);
//            }
//            if (iRotate == 0) {
//                infoLeftPicture = Math.round((heightPreview - infoTop - infoHeight - Math.round((infoWidth - infoHeight) / 2)) * pictureHeight / heightPreview);
//                infoToptPicture = Math.round((infoLeft * pictureWidth / widthPreview) + (infoWidthPicture - infoHeightPicture) / 2);
//            }
//            Log.d(TAG, "SetInfoDisplay W: " + infoWidth + " H: " + infoHeight + " infoLeft: " + infoLeft + " infoTop: " + infoTop
//                    + " infoLeftPicture: " + infoLeftPicture + " infoToptPicture:" + infoToptPicture);
        } else {
            chbShowInfo.setVisibility(View.GONE);
            chbShowInfo.setChecked(false);
            tbInfo.setVisibility(View.GONE);
        }
    }

    /**
     * Compares two {@code Size}s based on their areas.
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
     * Shows an error message dialog.
     */
    public static class ErrorDialog extends DialogFragment {

        private static final String ARG_MESSAGE = "message";

        public static ErrorDialog newInstance(String message) {
            ErrorDialog dialog = new ErrorDialog();
            Bundle args = new Bundle();
            args.putString(ARG_MESSAGE, message);
            dialog.setArguments(args);
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity activity = getActivity();
            return new AlertDialog.Builder(activity)
                    .setMessage(getArguments().getString(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            activity.finish();
                        }
                    })
                    .create();
        }

    }

    /**
     * Shows OK/Cancel confirmation dialog about camera permission.
     */
    public static class ConfirmationDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Fragment parent = getParentFragment();
            return new AlertDialog.Builder(getActivity())
                    .setMessage("request_permission")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            FragmentCompat.requestPermissions(parent,
//                                    new String[]{Manifest.permission.CAMERA},
//                                    REQUEST_CAMERA_PERMISSION);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Activity activity = parent.getActivity();
                                    if (activity != null) {
                                        activity.finish();
                                    }
                                }
                            })
                    .create();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }

    interface CameraPreviewListener {
        void onPictureTaken(JSONArray args);

        void hideCamera();

        void RestartCamera();
    }


}
