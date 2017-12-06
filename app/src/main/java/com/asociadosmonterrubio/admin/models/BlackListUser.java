package com.asociadosmonterrubio.admin.models;

/**
 * Created by joseluissanchezcruz on 12/5/17.
 */

public class BlackListUser {

    private String Apellido_Paterno;
    private String Apellido_Materno;
    private String CURP;
    private String Fecha_Nacimiento;
    private String Lugar_Nacimiento;
    private String Nombre;
    private String Motivo;
    private String Observaciones;

    public BlackListUser(){ }

    public String getApellido_Paterno() {
        return Apellido_Paterno;
    }

    public void setApellido_Paterno(String apellido_Paterno) {
        Apellido_Paterno = apellido_Paterno;
    }

    public String getApellido_Materno() {
        return Apellido_Materno;
    }

    public void setApellido_Materno(String apellido_Materno) {
        Apellido_Materno = apellido_Materno;
    }

    public String getCURP() {
        return CURP;
    }

    public void setCURP(String CURP) {
        this.CURP = CURP;
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

    public String getMotivo() {
        return Motivo;
    }

    public void setMotivo(String motivo) {
        Motivo = motivo;
    }

    public String getObservaciones() {
        return Observaciones;
    }

    public void setObservaciones(String observaciones) {
        Observaciones = observaciones;
    }
}
