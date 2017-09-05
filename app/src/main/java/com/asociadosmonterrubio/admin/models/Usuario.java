package com.asociadosmonterrubio.admin.models;

import java.util.ArrayList;

/**
 * Created by joseluissanchezcruz on 8/16/17.
 */

public class Usuario {

    public static final String ROL_ADMIN            = "Admin";
    public static final String ROL_ENCARGADO_CAMPO  = "encargadoDeCampo";
    public static final String ROL_CORREDOR         = "Corredor";
    public static final String ROL_SUPERVISOR       = "supervisor";

    private String email;
    private String nombre;
    private String rol;
    private String sede;
    private String campo;
    private ArrayList<String> campos;

    public Usuario(){
        campos = new ArrayList<>();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getSede() {
        return sede;
    }

    public void setSede(String sede) {
        this.sede = sede;
    }

    public ArrayList<String> getCampos() {
        return campos;
    }

    public void setCampos(ArrayList<String> campos) {
        this.campos = campos;
    }

    public String getCampo() {
        return campo;
    }

    public void setCampo(String campo) {
        this.campo = campo;
    }
}
