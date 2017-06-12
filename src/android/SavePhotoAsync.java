package it.plugin.camera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by XMEN on 12/6/2016.
 */

public class SavePhotoAsync extends AsyncTask<Void, Integer, Void> {
    private Activity activity;
    private Bitmap bm;
    private String ImageName;
    private int index;
    private String TAG = "CameraSavePhotoAsync";
    private int pictureWidthSmall;
    private int pictureHeightSmall;
    private String img;
    private String folder;
    private int iCam;
    //constructor này được truyền vào là MainActivity
    public SavePhotoAsync(int icam,Activity a, Bitmap bitmap, String photoName, int i, int imageSmallWidth, int imageSmallHeight, String folderName) {
        iCam=icam;
        activity = a;
        bm = bitmap;
        ImageName = photoName;
        index = i;
        pictureWidthSmall = imageSmallWidth;
        pictureHeightSmall = imageSmallHeight;
        folder = folderName;
    }

    //hàm này sẽ được thực hiện đầu tiên
    @Override
    protected void onPreExecute() {
        //Toast.makeText(activity, "Saving...", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Saving...");
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        SavePhoto utp = new SavePhoto(activity, bm, ImageName, pictureWidthSmall, pictureHeightSmall, folder);
        img = utp.savePhoto();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (img != "") {
                    // Save successfull.
                } else {
                    Toast.makeText(activity, "Save photo fail!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return null;
    }

    /**
     * sau khi tiến trình thực hiện xong thì hàm này sảy ra
     */
    @Override
    protected void onPostExecute(Void result) {
        //Toast.makeText(activity, "Save successful!", Toast.LENGTH_SHORT).show();
        try {
            if (CameraActivity.jsonListImage.length() > 0 && iCam==2) {
                JSONObject jo = (JSONObject) CameraActivity.jsonListImage.get(index);
                jo.remove("state");
                jo.put("state", true);
                CameraActivity.jsonListImage.put(index,jo);
                Log.d(TAG, "index: "+index+ " - "+jo.get("img"));
            }
            if (Camera1Activity.jsonListImage.length() > 0  && iCam==1)
            {
                JSONObject jo = (JSONObject) Camera1Activity.jsonListImage.get(index);
                jo.remove("state");
                jo.put("state", true);
                Camera1Activity.jsonListImage.put(index,jo);
                Log.d(TAG, "index1: "+index+ " - "+jo.get("img"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        super.onPostExecute(result);
    }
}