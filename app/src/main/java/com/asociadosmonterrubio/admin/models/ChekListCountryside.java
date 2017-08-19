package com.asociadosmonterrubio.admin.models;

/**
 * Created by Thomas on 17/08/2017.
 */

public class ChekListCountryside {

    public static final String PASE_DE_LISTA = "";

    private int IdEmpleado;
    private String Perfil;

    public  ChekListCountryside(){}

    public int getIdEmpleado() {
        return IdEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        IdEmpleado = idEmpleado;
    }

    public String getPerfil() {
        return Perfil;
    }

    public void setPerfil(String perfil) {
        Perfil = perfil;
    }
}
