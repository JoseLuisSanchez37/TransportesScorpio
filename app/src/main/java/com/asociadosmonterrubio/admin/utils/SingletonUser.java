package com.asociadosmonterrubio.admin.utils;

import android.util.Log;

import com.asociadosmonterrubio.admin.models.Usuario;
import com.google.firebase.database.DataSnapshot;

/**
 * Created by joseluissanchezcruz on 8/15/17.
 */

public class SingletonUser {
    private static final SingletonUser ourInstance = new SingletonUser();
    private Usuario usuario;

    public static SingletonUser getInstance() {
        return ourInstance;
    }

    private SingletonUser() {
        usuario = new Usuario();
    }

    public Usuario getUsuario(){
        return usuario;
    }

    public void setUsuario(DataSnapshot dataSnapshot){
		if (dataSnapshot != null) {
			this.usuario = dataSnapshot.getValue(Usuario.class);
            Log.d("usuario", usuario.toString());
		}else {
			this.usuario = null;
		}
    }

    public void setSavedUsuario(Usuario usuario){
        this.usuario = usuario;
        Log.d("usuario", usuario.toString());
    }
}
