package it.plugin.camera;

import android.graphics.Bitmap;

/**
 * Created by XMEN on 12/4/2016.
 */

public class ItemImage {
    Bitmap mPhoto;
    String mName;
    ItemImage(Bitmap bimap, String t){
        mPhoto = bimap;
        mName = t;
    }
}
