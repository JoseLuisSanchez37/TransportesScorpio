package com.asociadosmonterrubio.admin.activities;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.asociadosmonterrubio.admin.R;
import com.asociadosmonterrubio.admin.firebase.FireBaseQuery;
import com.asociadosmonterrubio.admin.models.Employee;
import com.asociadosmonterrubio.admin.utils.SingletonEmployees;
import com.asociadosmonterrubio.admin.adapters.EmployeeAdapter;

import java.util.ArrayList;

import br.com.forusers.heinsinputdialogs.HeinsInputDialog;
import br.com.forusers.heinsinputdialogs.interfaces.OnInputLongListener;
import butterknife.Bind;
import butterknife.ButterKnife;


public class ActivityDisplayEmployees extends AppCompatActivity {

    @Bind(R.id.checkbox_select_all) CheckBox checkbox_select_all;
    @Bind(R.id.empty) TextView empty;
    @Bind(R.id.recycler_employees) RecyclerView recycler_employees;

    private boolean goBackDiscardChanges = false;
    private EmployeeAdapter employeeAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_display_employees);
        ButterKnife.bind(this);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(getString(R.string.title_new_worker));
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_display_employees, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_employee:
                Intent intent = new Intent(this, ActivityNewEmployee.class);
                startActivity(intent);
                return true;

            case R.id.send_trip:
                if (employeeAdapter.getEmployeesSelected().isEmpty()) {
                    Toast.makeText(this, getString(R.string.empty_workers_selected), Toast.LENGTH_SHORT).show();
                }else
                    setNumberTrip();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onResume() {
        super.onResume();
        employeeAdapter.updateEmployees(SingletonEmployees.getInstance().getEmployees());
        refreshList();
    }

    @Override
    public void onBackPressed() {
        if (employeeAdapter.isEmpty() || goBackDiscardChanges)
            super.onBackPressed();
        else
            manageOnBackPressed();
    }

    private void init(){
        employeeAdapter = new EmployeeAdapter(SingletonEmployees.getInstance().getEmployees(), this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_employees.setLayoutManager(layoutManager);
        recycler_employees.setAdapter(employeeAdapter);
        checkbox_select_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                employeeAdapter.selectAllEmployees(isChecked);
            }
        });
        refreshList();
    }

    private void setNumberTrip(){
        HeinsInputDialog dialog = new HeinsInputDialog(this);
        dialog.setPositiveButton(new OnInputLongListener() {
            @Override
            public boolean onInputLong(AlertDialog dialog, Long busNumber) {
                setTrip(String.valueOf(busNumber));
                return false;
            }
        });
        dialog.setTitle(getString(R.string.input_bus_number));
        dialog.show();
    }

    public void refreshList(){
        if (employeeAdapter.isEmpty()) {
            recycler_employees.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        } else {
            recycler_employees.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
        }
    }

    private void setTrip(String busNumber){
        ArrayList<String> employeesSelected = employeeAdapter.getEmployeesSelected();
        ArrayList<Employee> employees = SingletonEmployees.getInstance().getEmployees();
        for (String key : employeesSelected){
            for (Employee employee : employees) {
                if (employee.getKey().equals(key)) {
                    FireBaseQuery.pushEmployeeToTrip(busNumber, key, employee.getNombre());
                    employees.remove(employee);
                    break;
                }
            }
        }
        employeeAdapter.updateEmployees(SingletonEmployees.getInstance().getEmployees());
        refreshList();
    }

    private void manageOnBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.title_exit_dialog));
        builder.setMessage(getString(R.string.message_exit_dialog));
        builder.setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                goBackDiscardChanges = true;
                onBackPressed();
            }
        });
        builder.setNegativeButton(R.string.continue_workers_register, null);
        AlertDialog dialog = builder.create();
        dialog.show();

    }

}