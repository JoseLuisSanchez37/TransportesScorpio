package com.asociadosmonterrubiotest.admin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.asociadosmonterrubiotest.admin.R;

import java.util.ArrayList;


/**
 * Created by joseluissanchezcruz on 5/9/17.
 */

public class ListViajesAdapter extends BaseAdapter {

    private Context context;
	private ArrayList<String> fechasSalidas;

    public ListViajesAdapter(Context context, ArrayList<String> fechasSalidas){
        this.context = context;
		this.fechasSalidas = fechasSalidas;
    }

    @Override
    public int getCount() {
        return fechasSalidas.size();
    }

    @Override
    public Object getItem(int position) {
        return fechasSalidas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list_fechas_salidas, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.txv_fecha_salida = (TextView) convertView.findViewById(R.id.txv_fecha_salida);
            convertView.setTag(viewHolder);
        }else
            viewHolder = (ViewHolder) convertView.getTag();

        viewHolder.txv_fecha_salida.setText(fechasSalidas.get(position));
        return convertView;
    }

    private class ViewHolder{
        private TextView txv_fecha_salida;
    }
}



