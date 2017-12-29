package com.asociadosmonterrubio.admin.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.asociadosmonterrubio.admin.R;
import com.asociadosmonterrubio.admin.utils.SearchFilter;

import java.util.ArrayList;
import java.util.Map;


/**
 * Created by joseluissanchezcruz on 5/9/17.
 */

public class ListEmployeeAdapter extends BaseAdapter implements Filterable{

    private Context context;
    private SearchFilter searchFilter;
	private ArrayList<Map<String, String>> currentListEmployees, allEmployees;

    public ListEmployeeAdapter(Context context, ArrayList<Map<String, String>> employees){
        this.context = context;
        this.allEmployees = new ArrayList<>(employees);
		this.currentListEmployees = new ArrayList<>(employees);
    }

    public void updateEmployeesList(ArrayList<Map<String, String>> employees){
        this.currentListEmployees.clear();
        this.currentListEmployees.addAll(employees);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return currentListEmployees.size();
    }

    @Override
    public Object getItem(int position) {
        return currentListEmployees.get(position);
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
            viewHolder.txv_departure_type = (TextView) convertView.findViewById(R.id.employee_departure_type);
            convertView.setTag(viewHolder);
        }else
            viewHolder = (ViewHolder) convertView.getTag();

		Map<String, String> employee = currentListEmployees.get(position);
        viewHolder.txv_id.setText(employee.get("ID"));
        viewHolder.txv_id_externo.setText(employee.get("IDExterno"));
		String fullName = employee.get("Nombre") + " " + employee.get("Apellido_Paterno") + " " + employee.get("Apellido_Materno");
        viewHolder.txv_name.setText(fullName);
        viewHolder.txv_departure_type.setText(!TextUtils.isEmpty(employee.get("Modalidad")) ? employee.get("Modalidad") : "Camion");
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (searchFilter == null)
            searchFilter = new SearchFilter(this, allEmployees);
        return searchFilter;
    }

    private class ViewHolder{
        private TextView txv_id, txv_id_externo, txv_name, txv_departure_type;
    }
}



