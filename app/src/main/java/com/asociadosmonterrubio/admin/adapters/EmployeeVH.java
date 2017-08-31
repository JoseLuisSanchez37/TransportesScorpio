package com.asociadosmonterrubio.admin.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.asociadosmonterrubio.admin.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by joseluissanchezcruz on 5/10/17.
 */

public class EmployeeVH extends RecyclerView.ViewHolder {

    public ImageView photo;
    public TextView fullName, curp, busNumer;
    public CheckBox checkBox_selected;

    public EmployeeVH(View itemView) {
        super(itemView);
        photo = (CircleImageView) itemView.findViewById(R.id.profile_image);
        fullName = (TextView) itemView.findViewById(R.id.txv_full_name);
        curp = (TextView) itemView.findViewById(R.id.txv_curp);
        busNumer = (TextView) itemView.findViewById(R.id.txv_bus_number);
        checkBox_selected = (CheckBox) itemView.findViewById(R.id.checkbox_selected);
    }
}
