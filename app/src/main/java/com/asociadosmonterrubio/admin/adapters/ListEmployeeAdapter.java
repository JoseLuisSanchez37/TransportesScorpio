package com.asociadosmonterrubio.admin.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.asociadosmonterrubio.admin.R;
import com.asociadosmonterrubio.admin.utils.SearchFilter;

import java.util.ArrayList;
import java.util.Map;


/**
 * Created by joseluissanchezcruz on 5/9/17.
 */

public class ListEmployeeAdapter extends BaseAdapter implements Filterable{

    public static final int NONE = 0;
    public static final int PRINTING_TOUCH_TO_ADD = 1;
    public static final int PRINTING_TOUCH_TO_REMOVE = 2;

    private int iconType;
    private Context context;
    private SearchFilter searchFilter;
	private ArrayList<Map<String, String>> currentListEmployees, allEmployees;

    public ListEmployeeAdapter(Context context, ArrayList<Map<String, String>> employees, int iconType){
        this.context = context;
        this.iconType = iconType;
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
            viewHolder = new ViewHolder();

            if (iconType == PRINTING_TOUCH_TO_REMOVE || iconType == PRINTING_TOUCH_TO_ADD) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_list_employees_with_printing, parent, false);
            }else
                convertView = LayoutInflater.from(context).inflate(R.layout.item_list_employees, parent, false);

            viewHolder.txv_id = (TextView) convertView.findViewById(R.id.employee_id);
            viewHolder.txv_id_externo = (TextView) convertView.findViewById(R.id.employee_id_external);
            viewHolder.txv_name = (TextView) convertView.findViewById(R.id.employee_name);
            if (iconType == NONE)
                viewHolder.txv_departure_type = (TextView) convertView.findViewById(R.id.employee_departure_type);

            if (iconType == PRINTING_TOUCH_TO_REMOVE || iconType == PRINTING_TOUCH_TO_ADD) {
                viewHolder.btn_for_printing = (ImageView) convertView.findViewById(R.id.btn_for_printing);
                int color = 0;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    color = iconType == PRINTING_TOUCH_TO_REMOVE ? context.getColor(R.color.panda) : context.getColor(R.color.yellow);
                }else
                    color = iconType == PRINTING_TOUCH_TO_REMOVE ? context.getResources().getColor(R.color.panda) : context.getResources().getColor(R.color.yellow);
                convertView.setBackgroundColor(color);
            }

            convertView.setTag(viewHolder);
        }else
            viewHolder = (ViewHolder) convertView.getTag();

		Map<String, String> employee = currentListEmployees.get(position);
        viewHolder.txv_id.setText(employee.get("ID"));
        viewHolder.txv_id_externo.setText(employee.get("IDExterno"));
		String fullName = employee.get("Nombre") + " " + employee.get("Apellido_Paterno") + " " + employee.get("Apellido_Materno");
        viewHolder.txv_name.setText(fullName);

        if (iconType == NONE)
            viewHolder.txv_departure_type.setText(!TextUtils.isEmpty(employee.get("Modalidad")) ? employee.get("Modalidad") : "Camion");

        if (iconType == PRINTING_TOUCH_TO_REMOVE)
            viewHolder.btn_for_printing.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_delete_forever));

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (searchFilter == null)
            searchFilter = new SearchFilter(this, allEmployees);
        return searchFilter;
    }

    private class ViewHolder{
        private ImageView btn_for_printing;
        private TextView txv_id, txv_id_externo, txv_name, txv_departure_type;
    }
}



