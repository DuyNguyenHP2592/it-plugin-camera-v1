package it.plugin.camera;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class camLauncher extends CordovaPlugin implements View.OnKeyListener { //CameraActivity.CameraPreviewListener {

    private final String TAG = "camLauncher";
    private final String setOnPictureTakenHandlerAction = "setOnPictureTakenHandler";
    private final String setColorEffectAction = "setColorEffect";
    private final String startCameraAction = "startCamera";
    private final String stopCameraAction = "stopCamera";
    private final String switchCameraAction = "switchCamera";
    private final String setFlashModeAction = "setFlashMode";
    private final String takePictureAction = "takePhoto";
    private final String showCameraAction = "showCamera";
    private final String showCameraAction1 = "showCamera1";
    private final String hideCameraAction = "hideCamera";

    private final String permission = Manifest.permission.CAMERA;

    private final int permissionsReqId = 0;
    private CallbackContext execCallback;
    private JSONArray execArgs;

    private CameraActivity fragment;//Hiển thị Camera theo API 2
    private Camera1Activity fragment1; // Hiển thị theo Camera API 1

    private CallbackContext takePictureCallbackContext;
    private FrameLayout containerView;

    public camLauncher() {
        super();
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        webView.getView().setOnKeyListener(this);
        if (setOnPictureTakenHandlerAction.equals(action)) {
            return setOnPictureTakenHandler(args, callbackContext);
        } else if (startCameraAction.equals(action)) {
            if (cordova.hasPermission(permission)) {
                return startCamera(args, callbackContext);
            } else {
                execCallback = callbackContext;
                execArgs = args;
                cordova.requestPermission(this, permissionsReqId, permission);
            }
        } else if (takePictureAction.equals(action)) {
            return takePhoto(args, callbackContext);
        } else if (setColorEffectAction.equals(action)) {
            return setColorEffect(args, callbackContext);
        } else if (stopCameraAction.equals(action)) {
            return stopCamera(args, callbackContext);
        } else if (hideCameraAction.equals(action)) {
            return hideCamera(args, callbackContext);
        } else if (showCameraAction.equals(action)) {
            return showCamera(args, callbackContext);
        } else if (showCameraAction1.equals(action)) {
            return showCamera1(args, callbackContext);
        } else if (switchCameraAction.equals(action)) {
            return switchCamera(args, callbackContext);
        } else if (setFlashModeAction.equals(action)) {
            return setFlashMode(args, callbackContext);
        }

        return false;
    }

    //Khởi động Camera API 2
    private boolean startCamera(final JSONArray args, CallbackContext callbackContext) {
        if (fragment != null) {
            return false;
        }
        fragment = new CameraActivity();
        //fragment.setEventListener(this);

        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                try {
                    Activity activity = cordova.getActivity();
                    if (containerView == null) {
                        containerView = new FrameLayout(activity.getApplicationContext());
                        // Look up a view id we inject to ensure there are no conflicts
                        int cameraViewId = activity.getResources().getIdentifier(activity.getClass().getPackage().getName() + ":id/camera_container", null, null);
                        containerView.setId(cameraViewId);
                    }
                    if (containerView.getParent() != webView.getView().getParent()) {
                        if (containerView.getParent() != null) {
                            ((ViewGroup) containerView.getParent()).removeView(containerView);
                        }
                        FrameLayout.LayoutParams containerLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                        ((ViewGroup) webView.getView().getParent()).addView(containerView, containerLayoutParams);
                    }
                    containerView.setAlpha(1);
                    containerView.bringToFront();

                    //add the fragment to the container
                    FragmentManager fragmentManager = cordova.getActivity().getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.add(containerView.getId(), fragment);
                    //fragmentTransaction.hide(fragment);
                    fragmentTransaction.commit();

                    Toast.makeText(cordova.getActivity(), "Camera already!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return true;
    }

    //Khởi động camera API 1
    private boolean startCamera1(final JSONArray args, CallbackContext callbackContext) {
        if (fragment1 != null) {
            return false;
        }
        fragment1 = new it.plugin.camera.Camera1Activity();
        //fragment1.setEventListener((Camera1Activity.CameraPreviewListener) this);
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                try {
                    DisplayMetrics metrics = cordova.getActivity().getResources().getDisplayMetrics();
                    String defaultCamera = "back";
                    Boolean tapToTakePicture = false;
                    Boolean dragEnabled = false;
                    Boolean toBack = false;
                    fragment1.defaultCamera = defaultCamera;
                    fragment1.tapToTakePicture = tapToTakePicture;
                    fragment1.dragEnabled = dragEnabled;
                    Activity activity = cordova.getActivity();
                    if (containerView == null) {
                        containerView = new FrameLayout(activity.getApplicationContext());
                        // Look up a view id we inject to ensure there are no conflicts
                        int cameraViewId = activity.getResources().getIdentifier(activity.getClass().getPackage().getName() + ":id/camera_container", null, null);
                        containerView.setId(cameraViewId);
                    }
                    if (containerView.getParent() != webView.getView().getParent()) {
                        if (containerView.getParent() != null) {
                            ((ViewGroup) containerView.getParent()).removeView(containerView);
                        }
                        FrameLayout.LayoutParams containerLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                        ((ViewGroup) webView.getView().getParent()).addView(containerView, containerLayoutParams);
                    }
                    //display camera bellow the webview
                    if (toBack) {
                        webView.getView().setBackgroundColor(0x00000000);
                        ((ViewGroup) webView.getView()).bringToFront();
                    } else {
                        //set camera back to front
                        containerView.setAlpha(1);
                        containerView.bringToFront();
                    }
                    FragmentManager fragmentManager = cordova.getActivity().getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.add(containerView.getId(), fragment1);
                    //fragmentTransaction.hide(fragment1);
                    fragmentTransaction.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return true;
    }

    private boolean takePhoto(final JSONArray args, CallbackContext callbackContext) {
        if (fragment == null) {
            return false;
        }
        try {
            double maxWidth = args.optDouble(0, 0);
            double maxHeight = args.optDouble(1, 0);
            //fragment.takePhoto((int) Math.floor(maxWidth), (int) Math.floor(maxHeight));

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void onPictureTaken(JSONArray json) {
        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, json);
        pluginResult.setKeepCallback(true);
        takePictureCallbackContext.sendPluginResult(pluginResult);
    }

    private boolean setColorEffect(final JSONArray args, CallbackContext callbackContext) {
//        if (fragment == null) {
//            return false;
//        }
//
//        Camera camera = fragment.getCamera();
//        if (camera == null) {
//            return true;
//        }
//
//        Camera.Parameters params = camera.getParameters();
//
//        try {
//            String effect = args.getString(0);
//
//            if (effect.equals("aqua")) {
//                params.setColorEffect(Camera.Parameters.EFFECT_AQUA);
//            } else if (effect.equals("blackboard")) {
//                params.setColorEffect(Camera.Parameters.EFFECT_BLACKBOARD);
//            } else if (effect.equals("mono")) {
//                params.setColorEffect(Camera.Parameters.EFFECT_MONO);
//            } else if (effect.equals("negative")) {
//                params.setColorEffect(Camera.Parameters.EFFECT_NEGATIVE);
//            } else if (effect.equals("none")) {
//                params.setColorEffect(Camera.Parameters.EFFECT_NONE);
//            } else if (effect.equals("posterize")) {
//                params.setColorEffect(Camera.Parameters.EFFECT_POSTERIZE);
//            } else if (effect.equals("sepia")) {
//                params.setColorEffect(Camera.Parameters.EFFECT_SEPIA);
//            } else if (effect.equals("solarize")) {
//                params.setColorEffect(Camera.Parameters.EFFECT_SOLARIZE);
//            } else if (effect.equals("whiteboard")) {
//                params.setColorEffect(Camera.Parameters.EFFECT_WHITEBOARD);
//            }
//
//            fragment.setCameraParameters(params);
        return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
    }

    private boolean stopCamera(final JSONArray args, CallbackContext callbackContext) {
        if (fragment == null) {
            return false;
        }

        FragmentManager fragmentManager = cordova.getActivity().getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
        fragment = null;
        return true;
    }
    private boolean stopCamera1(final JSONArray args, CallbackContext callbackContext) {
        if (fragment1 == null) {
            return false;
        }

        FragmentManager fragmentManager = cordova.getActivity().getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment1);
        fragmentTransaction.commit();
        fragment1 = null;
        return true;
    }

    public void RestartCamera() {
        if (fragment == null) {
            return;
        }
        FragmentManager fragmentManager = cordova.getActivity().getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
        fragment = null;
        JSONObject jo = new JSONObject();
        JSONArray ja = new JSONArray();
        ja.put(jo);
        startCamera(ja, takePictureCallbackContext);
    }
    //Hiển thị Camera API 1
    private boolean showCamera1(final JSONArray args, CallbackContext callbackContext) {
        if (fragment1 == null) {
            startCamera1(args, callbackContext);
        }
        try {
            fragment1.ImageName = args.getString(0);
            fragment1.Info = args.getString(1);
            fragment1.bFinish = 0;
            fragment1.jsonListImage = new JSONArray();
            if (fragment1.Info == null || fragment1.Info.isEmpty() || fragment1.Info.equals(""))
                fragment1.bShowInfo = false;
            else
                fragment1.bShowInfo = true;
            while (fragment1.bFinish == 0) {
                //Đợi khi nào click vào nut Accept thì thoát
            }
            if (fragment1.bFinish == 1) {
                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, fragment1.jsonListImage);
                pluginResult.setKeepCallback(true);
                callbackContext.sendPluginResult(pluginResult);
            } else {
                PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
                pluginResult.setKeepCallback(true);
                callbackContext.sendPluginResult(pluginResult);
            }
            stopCamera1(args, callbackContext);
            //Clear Memory
            Runtime.getRuntime().gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean showCamera(final JSONArray args, CallbackContext callbackContext) {
        if (fragment == null) {
            startCamera(args, callbackContext);
        }
        try {
            fragment.ImageName = args.getString(0);
            fragment.Info = args.getString(1);
            fragment.bFinish = 0;
            fragment.jsonListImage = new JSONArray();
            if (fragment.Info == null || fragment.Info.isEmpty() || fragment.Info.equals(""))
                fragment.bShowInfo = false;
            else
                fragment.bShowInfo = true;

            while (fragment.bFinish == 0) {
                //Đợi khi nào click vào nut Accept thì thoát
            }
            if (fragment.bFinish == 1) {
                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, fragment.jsonListImage);
                pluginResult.setKeepCallback(true);
                callbackContext.sendPluginResult(pluginResult);
            } else {
                PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
                pluginResult.setKeepCallback(true);
                callbackContext.sendPluginResult(pluginResult);
            }
            stopCamera(args, callbackContext);
            //Clear Memory
            Runtime.getRuntime().gc();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            stopCamera(args, callbackContext);
            return false;
        }
    }

    private boolean hideCamera(final JSONArray args, CallbackContext callbackContext) {
        if (fragment == null) {
            return false;
        }

        FragmentManager fragmentManager = cordova.getActivity().getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.hide(fragment);
        fragmentTransaction.commit();


        return true;
    }

    public void hideCamera() {
        if (fragment == null) {
            return;
        }
//        FragmentManager fragmentManager = cordova.getActivity().getFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.hide(fragment);
//        fragmentTransaction.commit();

        FragmentManager fragmentManager = cordova.getActivity().getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
        fragment = null;
    }

    private boolean switchCamera(final JSONArray args, CallbackContext callbackContext) {
        if (fragment == null) {
            return false;
        }
        //fragment.switchCamera();
        return true;
    }

    private boolean setFlashMode(final JSONArray args, CallbackContext callbackContext) {
        if (fragment == null) {
            return false;
        }
        try {
            //fragment.setFlashMode(args.getInt(0));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean setOnPictureTakenHandler(JSONArray args, CallbackContext callbackContext) {
        Log.d(TAG, "setOnPictureTakenHandler");
        takePictureCallbackContext = callbackContext;
        return true;
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
        for (int r : grantResults) {
            if (r == PackageManager.PERMISSION_DENIED) {
                execCallback.sendPluginResult(new PluginResult(PluginResult.Status.ILLEGAL_ACCESS_EXCEPTION));
                return;
            }
        }
        if (requestCode == permissionsReqId) {
            startCamera(execArgs, execCallback);
        }
    }

    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {

        // Check if the event is equal to KEY_DOWN
        if( keyEvent.getAction() == KeyEvent.ACTION_DOWN )
        {
            // Check what button has been pressed
            if( keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ){
                if(fragment1 != null)
					fragment1.takePhoto(fragment1.pictureWidth,fragment1.pictureHeight);
                else
                    fragment.takePicture();
            }
        }
        return true;
    }
}
