package com.asociadosmonterrubio.admin.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.asociadosmonterrubio.admin.models.Usuario;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by joseluissanchezcruz on 8/26/17.
 */

public class UserPreferences {

    public static final String LAST_SESSION_DATE    = "scorpio_last_session_date";
    private static final String SCORPIO_PREF        = "Scorpio_Preferences";
    public static final String LOGIN_SAVE_CREDEN    = "Scorpio_save_credentials";
    public static final String LOGIN_EMAIL          = "Scorpio_save_email";
    public static final String LOGIN_PASS           = "Scorpio_save_password";

    private static final String SESSION_EMAIL       = "email";
    private static final String SESSION_NOMBRE      = "nombre";
    private static final String SESSION_ROL         = "rol";
    private static final String SESSION_SEDE        = "sede";
    private static final String SESSION_CAMPOS      = "campos";

    public static void savePreference(String key, String value){
        SharedPreferences.Editor editor = (AppController.instance.getSharedPreferences(SCORPIO_PREF,Context.MODE_PRIVATE)).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void savePreference(String key, boolean value){
        SharedPreferences.Editor editor = (AppController.instance.getSharedPreferences(SCORPIO_PREF,Context.MODE_PRIVATE)).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static String getPreference(String key){
        SharedPreferences sharedPref = AppController.instance.getSharedPreferences(SCORPIO_PREF, Context.MODE_PRIVATE);
        return sharedPref.getString(key, "");
    }

    public static boolean getPreferenceb(String key){
        SharedPreferences sharedPref = AppController.instance.getSharedPreferences(SCORPIO_PREF, Context.MODE_PRIVATE);
        return sharedPref.getBoolean(key, false);
    }

    public static void saveUserSession(Usuario usuario){
        savePreference(SESSION_EMAIL, usuario.getEmail());
        savePreference(SESSION_NOMBRE, usuario.getNombre());
        savePreference(SESSION_ROL, usuario.getRol());
        savePreference(SESSION_SEDE, usuario.getSede());
        savePreference(SESSION_CAMPOS, usuario.getCampos().toString());
    }

    public static Usuario getUserSession(){
        Usuario usuario = new Usuario();
        usuario.setEmail(getPreference(SESSION_EMAIL));
        usuario.setNombre(getPreference(SESSION_NOMBRE));
        usuario.setRol(getPreference(SESSION_ROL));
        usuario.setSede(getPreference(SESSION_SEDE));
        String strcampos = getPreference(SESSION_CAMPOS);
        ArrayList<String> list_campos;
        if (!strcampos.equals("[]")) {
            strcampos = strcampos.replace("[", "");
            strcampos = strcampos.replace("]", "");
            strcampos = strcampos.replace(" ", "");
            String[] arrayCampos = strcampos.split(",");
            list_campos = new ArrayList<>(Arrays.asList(arrayCampos));
        }else
            list_campos = new ArrayList<>();
        usuario.setCampos(list_campos);
        return usuario;
    }

    public static void clearUserSession(){
        savePreference(SESSION_EMAIL, "");
        savePreference(SESSION_NOMBRE, "");
        savePreference(SESSION_ROL, "");
        savePreference(SESSION_SEDE, "");
        savePreference(SESSION_CAMPOS, "");
    }

}
