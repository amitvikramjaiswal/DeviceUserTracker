package com.teksystems.devicetracker.view.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.teksystems.devicetracker.R;

/**
 * Created by akokala on 28-9-15.
 */
public class NavigationListAdapter extends BaseAdapter {

    private Context mContext;
    private String[] mNavigationItems;
    private int[] mNavigationImages;
    private LayoutInflater mInflater;
    private ImageView mNavigationImage;
    private TextView mNavigationItem;

    public NavigationListAdapter(Context mContext, String[] mNavigationItems, int[] mNavigationImages) {
        this.mContext = mContext;
        this.mNavigationItems = mNavigationItems;
        this.mNavigationImages = mNavigationImages;
    }

    @Override
    public int getCount() {
        return mNavigationItems.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.list_navigation_item, null);
        }

        mNavigationItem = (TextView)convertView.findViewById(R.id.tv_navigation_item);
        mNavigationImage = (ImageView)convertView.findViewById(R.id.iv_navigation_image);

        mNavigationItem.setText(mNavigationItems[position]);
        mNavigationImage.setImageResource(mNavigationImages[position]);

        return convertView;
    }
}
