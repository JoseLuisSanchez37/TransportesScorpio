package com.asociadosmonterrubio.admin.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.asociadosmonterrubio.admin.R;


/**
 * Created by joseluissanchezcruz on 5/9/17.
 */

public class HomeAdapter extends BaseAdapter {

    private Context context;

    public HomeAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return context.getResources().getIntArray(R.array.home_drawables).length;
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
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_home, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.img_icon = (ImageView) convertView.findViewById(R.id.img_icon);
            viewHolder.txv_icon = (TextView) convertView.findViewById(R.id.txv_icon);
            convertView.setTag(viewHolder);
        }else
            viewHolder = (ViewHolder) convertView.getTag();

        TypedArray drawables = context.getResources().obtainTypedArray(R.array.home_drawables);
        String[] names = context.getResources().getStringArray(R.array.home_names);
        viewHolder.img_icon.setBackgroundResource(drawables.getResourceId(position, 0));
        viewHolder.txv_icon.setText(names[position]);
        drawables.recycle();
        return convertView;
    }

    private class ViewHolder{
        private ImageView img_icon;
        private TextView txv_icon;
    }
}



