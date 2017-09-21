package com.asociadosmonterrubio.admin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.asociadosmonterrubio.admin.R;
import com.asociadosmonterrubio.admin.models.Employee;

import java.util.ArrayList;

/**
 * Created by joseluissanchezcruz on 5/21/17.
 */

public class CheckListSupervisorAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private ArrayList<Employee> employees;

	public CheckListSupervisorAdapter(Context context, ArrayList<Employee> employees) {
		this.employees = employees;
		this.inflater = LayoutInflater.from(context);
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
		if (convertView == null){
			convertView = inflater.inflate(R.layout.item_check_list_supervisor, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.txv_nombre = (TextView) convertView.findViewById(R.id.txv_nombre);
			viewHolder.txv_fecha_nac = (TextView) convertView.findViewById(R.id.txv_fecha_nac);
			viewHolder.txv_origen = (TextView) convertView.findViewById(R.id.txv_origen);
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		Employee employee = employees.get(position);
		String nombre = employee.getNombre().concat(" "+employee.getApellido_Paterno().concat(" "+employee.getApellido_Materno()));
		viewHolder.txv_nombre.setText("Nombre: ".concat(nombre));
		viewHolder.txv_origen.setText("Originario: ".concat(employee.getLugar_Nacimiento()));
		viewHolder.txv_fecha_nac.setText("Fecha Nac: ".concat(employee.getFecha_Nacimiento()));

		return convertView;
	}

	private static class ViewHolder {
		TextView txv_nombre, txv_origen, txv_fecha_nac;
	}

}
