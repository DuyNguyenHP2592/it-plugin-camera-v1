package it.plugin.camera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by XMEN on 12/6/2016.
 */

public class SavePhoto {
    private Activity activity;
    private Bitmap bm;
    private String ImageName;
    private String TAG="CameraSavePhoto";
    private  int pictureWidthSmall;
    private  int pictureHeightSmall;
    private String folder;
    SavePhoto(Activity a, Bitmap bitmap, String photoName, int imageSmallWidth, int imageSmallHeight, String folderName)
    {
        a=activity;
        bm=bitmap;
        ImageName=photoName;
        pictureWidthSmall=imageSmallWidth;
        pictureHeightSmall=imageSmallHeight;
        folder=folderName;
    }
    public String savePhoto() {
        try {
            String ImageNameSmall = ImageName.replace(".jpg", "_small.jpg");
            int intWidth=bm.getWidth();
            int intHeight=bm.getHeight();
            if(intWidth < intHeight)
            {
                intWidth=pictureHeightSmall;
                intHeight=pictureWidthSmall;
                //ImageName = ImageName.replace(".jpg", "_3x4.jpg");
            }
            else
            {
                intWidth=pictureWidthSmall;
                intHeight=pictureHeightSmall;
                //ImageName = ImageName.replace(".jpg", "_4x3.jpg");
            }
            Bitmap bmPhotoSmall = Bitmap.createScaledBitmap(bm, intWidth, intHeight, true);
            File file = new File(folder);
            file.mkdirs();
            File imageFile = new File(file, ImageName);
            File imageFileSmall = new File(file, ImageNameSmall);
            FileOutputStream out = null;
            FileOutputStream outSmall = null;
            try {
                out = new FileOutputStream(imageFile);
                outSmall = new FileOutputStream(imageFileSmall);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return "";
            }
            //Save bitmap to library
            bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
            bmPhotoSmall.compress(Bitmap.CompressFormat.JPEG, 100, outSmall);
            bm.recycle();
            bmPhotoSmall.recycle();
            System.gc();
            String img=imageFile.getPath();
            Log.d(TAG,"Save photo successful: "+img);
            return  img;
        } catch (RuntimeException ex) {
            Log.e(TAG, ex.getMessage());
            return  "";
        }
    }
}
