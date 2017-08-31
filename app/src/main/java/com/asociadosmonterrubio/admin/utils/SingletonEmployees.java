package com.asociadosmonterrubio.admin.utils;

import java.util.ArrayList;

import com.asociadosmonterrubio.admin.models.Employee;

/**
 * Created by joseluissanchezcruz on 3/20/17.
 */

public class SingletonEmployees {

    private ArrayList<Employee> employees = new ArrayList<>();
    private static final SingletonEmployees ourInstance = new SingletonEmployees();

    public static SingletonEmployees getInstance() {
        return ourInstance;
    }

    private SingletonEmployees() { }

    public void add(Employee employee){
        employees.add(employee);
    }

    public ArrayList<Employee> getEmployees(){
        return employees;
    }

    public void setEmployess(ArrayList<Employee> employess){
        this.employees.addAll(employess);
    }

}
