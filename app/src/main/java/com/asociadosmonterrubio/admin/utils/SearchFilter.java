package com.asociadosmonterrubio.admin.utils;

import android.util.Log;
import android.widget.Filter;

import com.asociadosmonterrubio.admin.adapters.ListEmployeeAdapter;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by joseluissanchezcruz on 12/24/17.
 */

public class SearchFilter extends Filter {

    private ListEmployeeAdapter listAdapter;
    private ArrayList<Map<String, String>> employees;

    public SearchFilter(ListEmployeeAdapter listAdapter, ArrayList<Map<String, String>> employees){
        this.listAdapter = listAdapter;
        this.employees = employees;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults filterResults = new FilterResults();
        ArrayList<Map<String, String>> results = new ArrayList<>();
        if (constraint != null) {
            String s_constraint = constraint.toString().toLowerCase().replace(" ", "");
            for (Map<String, String> employee : employees) {

                //Getting values
                String IDExterno = employee.get("IDExterno");
                String nombre = employee.get("Nombre").toLowerCase().trim();
                String apellidoPaterno = employee.get("Apellido_Paterno").toLowerCase().trim();
                String apellidoMaterno = employee.get("Apellido_Materno").toLowerCase().trim();
                String fullName = nombre + apellidoPaterno + apellidoMaterno;

                Log.d("fullName", fullName);
                Log.d("constraint", s_constraint);
                if (IDExterno.contains(s_constraint) || fullName.contains(s_constraint))
                    results.add(employee);
            }
        }

        filterResults.values = results;
        filterResults.count = results.size();

        return filterResults;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        if (results.count == 0)
            listAdapter.notifyDataSetInvalidated();
        else
            listAdapter.updateEmployeesList((ArrayList<Map<String, String>>) results.values);
    }
}
