package com.asociadosmonterrubio.admin.models;

/**
 * Created by joseluissanchezcruz on 5/24/17.
 */

public class Salidas {

    private String id;
    private String numeroCamion;
    private String campo;
    private String fecha;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumeroCamion() {
        return numeroCamion;
    }

    public void setNumeroCamion(String numeroCamion) {
        this.numeroCamion = numeroCamion;
    }

    public String getCampo() {
        return campo;
    }

    public void setCampo(String campo) {
        this.campo = campo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
