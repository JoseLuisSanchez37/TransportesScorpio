package com.asociadosmonterrubio.admin.models;

import android.graphics.Bitmap;

/**
 * Created by joseluissanchezcruz on 3/12/17.
 */

public class Employee {

    /**
     * ¡¡¡¡¡¡¡¡¡  ATTENTION  !!!!!!!
     * THIS ASSIGNATION CAN NOT BE CHANGED BECAUSE THEY ARE PART OF FIREBASE'S STRUCTURE.
     * PLEASE REFER TO THE OWN PROJECT TO MAKE CHANGES.
     */
    //Definition
    public static final String _JORNALERO   = "Jornalero";
    public static final String _MOD_SOLO    = "Solo";
    public static final String _MOD_RENOV   = "Renovacion";

    //Attributes DON`T CHANGE THEM
    public static final String _DEF_ID      = "ID"; //Default ID which was associated when and employee was assigned to a field. Provided by Index's node
    public static final String _ACTIVIDAD   = "Actividad";
    public static final String _APELLIDO_P  = "Apellido_Paterno";
    public static final String _APELLIDO_M  = "Apellido_Materno";
    public static final String _CURP        = "CURP";
    public static final String _CAMION      = "Camion";
    public static final String _CONTRATO    = "Contrato";
    public static final String _ENGANCHE    = "Enganche";
    public static final String _FECHA_NAC   = "Fecha_Nacimiento";
    public static final String _LUGAR_NAC   = "Lugar_Nacimiento";
    public static final String _NOMBRE      = "Nombre";
    public static final String _FECHA_SAL   = "Fecha_Salida";
    public static final String _CAMPOS      = "campos";
    public static final String _PUSH_ID     = "pushId";
    public static final String _SEDE        = "sede";
    public static final String _MODALIDAD   = "Modalidad";
    public static final String _ID_EXTERNO  = "IDExterno"; //External ID associated to employees who belong to a special fields where their owners assigned to them special ID's

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
    private String Fecha_Salida;
    private String Modalidad;
    private long ID;

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

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
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

    public String getModalidad() {
        return Modalidad;
    }

    public void setModalidad(String modalidad) {
        Modalidad = modalidad;
    }

    public String getFecha_Salida() {
        return Fecha_Salida;
    }

    public void setFecha_Salida(String fecha_Salida) {
        Fecha_Salida = fecha_Salida;
    }
}
