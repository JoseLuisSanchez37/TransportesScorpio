package com.asociadosmonterrubio.admin.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.asociadosmonterrubio.admin.R;

import java.util.ArrayList;

import com.asociadosmonterrubio.admin.models.Employee;

/**
 * Created by joseluissanchezcruz on 5/10/17.
 */

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeVH> implements CompoundButton.OnCheckedChangeListener{

    private static final int TAG_ID = 0xffffffff;

    private Context context;
    private ArrayList<String> employeesSelected;
    private ArrayList<Employee> employees;

    public EmployeeAdapter(ArrayList<Employee> employees, Context context){
        this.employees = employees;
        this.context = context;
        this.employees = new ArrayList<>();
        this.employeesSelected = new ArrayList<>();
    }

    @Override
    public EmployeeVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_new_employee, parent, false);
        return new EmployeeVH(view);
    }

    @Override
    public void onBindViewHolder(EmployeeVH holder, int position) {
        holder.checkBox_selected.setEnabled(true);
        holder.checkBox_selected.setOnCheckedChangeListener(null);
        holder.checkBox_selected.setOnCheckedChangeListener(this);
        Employee employee = employees.get(position);
        holder.checkBox_selected.setTag(TAG_ID, employee.getKey());
        holder.photo.setImageBitmap(employee.getImage());
        holder.fullName.setText(employee.getNombre());
        holder.curp.setText(employee.getCURP());

        boolean isSelected = employeesSelected.contains(String.valueOf(employee.getKey()));
        holder.checkBox_selected.setOnCheckedChangeListener(null);
        holder.checkBox_selected.setChecked(isSelected);
        holder.checkBox_selected.setOnCheckedChangeListener(this);
    }

    @Override
    public int getItemCount() {
        return employees.size();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        buttonView.setOnCheckedChangeListener(null);
        buttonView.setChecked(isChecked);
        if (isChecked){
            employeesSelected.add(buttonView.getTag(0xffffffff).toString());
        }else {
            employeesSelected.remove(buttonView.getTag(0xffffffff).toString());
        }
        buttonView.setOnCheckedChangeListener(this);
    }

    public void selectAllEmployees(boolean isSelectAll){
        if (isSelectAll){
            for (Employee employee : employees)
                employeesSelected.add(String.valueOf(employee.getKey()));
        }else
            employeesSelected.clear();

       notifyDataSetChanged();
    }

    public void updateEmployees(ArrayList<Employee> employees){
        this.employees.clear();
        this.employees.addAll(employees);
        this.notifyDataSetChanged();
    }

    public ArrayList<String> getEmployeesSelected() {
        return employeesSelected;
    }

    public boolean isEmpty() {
        return employees.isEmpty();
    }

}
