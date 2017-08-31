package com.asociadosmonterrubio.admin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.asociadosmonterrubio.admin.R;

import java.util.ArrayList;
import java.util.Map;


/**
 * Created by joseluissanchezcruz on 5/9/17.
 */

public class ListEmployeeAdapter extends BaseAdapter {

    private Context context;
	private ArrayList<Map<String, String>> employees;

    public ListEmployeeAdapter(Context context, ArrayList<Map<String, String>> employees){
        this.context = context;
		this.employees = employees;
    }

    @Override
    public int getCount() {
        return employees.size();
    }

    @Override
    public Object getItem(int position) {
        return employees.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list_employees, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.txv_id = (TextView) convertView.findViewById(R.id.employee_id);
            viewHolder.txv_id_externo = (TextView) convertView.findViewById(R.id.employee_id_external);
            viewHolder.txv_name = (TextView) convertView.findViewById(R.id.employee_name);
            convertView.setTag(viewHolder);
        }else
            viewHolder = (ViewHolder) convertView.getTag();

		Map<String, String> employee = employees.get(position);
        viewHolder.txv_id.setText(employee.get("ID"));
        viewHolder.txv_id_externo.setText(employee.get("IDExterno"));
		String fullName = employee.get("Nombre") + " " + employee.get("Apellido_Paterno") + " " + employee.get("Apellido_Materno");
        viewHolder.txv_name.setText(fullName);
        return convertView;
    }

    private class ViewHolder{
        private TextView txv_id, txv_id_externo, txv_name;
    }
}



