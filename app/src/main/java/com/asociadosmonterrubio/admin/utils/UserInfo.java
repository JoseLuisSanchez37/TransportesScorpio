package com.asociadosmonterrubio.admin.utils;

import com.asociadosmonterrubio.admin.models.Usuario;
import com.google.firebase.database.DataSnapshot;

/**
 * Created by joseluissanchezcruz on 8/15/17.
 */

public class UserInfo {
    private static final UserInfo ourInstance = new UserInfo();
    private Usuario usuario;

    public static UserInfo getInstance() {
        return ourInstance;
    }

    private UserInfo() {
        usuario = new Usuario();
    }

    public Usuario getUsuario(){
        return usuario;
    }

    public void setUsuario(DataSnapshot dataSnapshot){
        
    }
}
