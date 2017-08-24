package com.asociadosmonterrubio.admin.models;

import android.graphics.Bitmap;

/**
 * Created by joseluissanchezcruz on 3/12/17.
 */

public class Employee {

    public static final String JORNALERO = "Jornalero";

    private String key;
    private String Actividad;
    private String Apellido_Materno;
    private String Apellido_Paterno;
    private String CURP;
    private String Contrato;
    private String Fecha_Nacimiento;
    private String Lugar_Nacimiento;
    private String Nombre;
    private Bitmap image;
    private String enganche;
    private String fechaSalida;

    public Employee(){ }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getActividad() {
        return Actividad;
    }

    public void setActividad(String actividad) {
        Actividad = actividad;
    }

    public String getApellido_Materno() {
        return Apellido_Materno;
    }

    public void setApellido_Materno(String apellido_Materno) {
        Apellido_Materno = apellido_Materno;
    }

    public String getApellido_Paterno() {
        return Apellido_Paterno;
    }

    public void setApellido_Paterno(String apellido_Paterno) {
        Apellido_Paterno = apellido_Paterno;
    }

    public String getCURP() {
        return CURP;
    }

    public void setCURP(String CURP) {
        this.CURP = CURP;
    }

    public String getContrato() {
        return Contrato;
    }

    public void setContrato(String contrato) {
        Contrato = contrato;
    }

    public String getFecha_Nacimiento() {
        return Fecha_Nacimiento;
    }

    public void setFecha_Nacimiento(String fecha_Nacimiento) {
        Fecha_Nacimiento = fecha_Nacimiento;
    }

    public String getLugar_Nacimiento() {
        return Lugar_Nacimiento;
    }

    public void setLugar_Nacimiento(String lugar_Nacimiento) {
        Lugar_Nacimiento = lugar_Nacimiento;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getEnganche() {
        return enganche;
    }

    public void setEnganche(String enganche) {
        this.enganche = enganche;
    }

    public String getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(String fechaSalida) {
        this.fechaSalida = fechaSalida;
    }
}
