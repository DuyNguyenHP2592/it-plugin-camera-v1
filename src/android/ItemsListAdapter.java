package it.plugin.camera;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by XMEN on 12/4/2016.
 */

public class ItemsListAdapter extends BaseAdapter {

    private List<ItemImage> list;
    Activity activity;

    ItemsListAdapter(Activity a, List<ItemImage> l) {
        activity = a;
        list = l;
    }

    @Override
    public int getCount() {
        if (list == null)
            return 0;
        else
            return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(activity.getResources().getIdentifier("row", "layout", activity.getPackageName()), null);

            CameraActivity.ViewHolder viewHolder = new CameraActivity.ViewHolder();
            viewHolder.icon = (ImageView) rowView.findViewById(activity.getResources().getIdentifier("rowImageView", "id", activity.getPackageName()));
            viewHolder.text = (TextView) rowView.findViewById(activity.getResources().getIdentifier("rowTextView", "id", activity.getPackageName()));
            rowView.setTag(viewHolder);
        }

        CameraActivity.ViewHolder holder = (CameraActivity.ViewHolder) rowView.getTag();
        holder.icon.setImageBitmap(list.get(position).mPhoto);
        holder.text.setText(list.get(position).mName);

        return rowView;
    }

    public List<ItemImage> getList() {
        return list;
    }
}