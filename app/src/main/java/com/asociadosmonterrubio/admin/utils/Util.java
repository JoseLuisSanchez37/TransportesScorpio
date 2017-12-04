package com.asociadosmonterrubio.admin.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


import com.asociadosmonterrubio.admin.models.ChekListCountryside;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by joseluissanchezcruz on 3/20/17.
 */

public class Util {

    public static void hideKeyboard(Activity activity){
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static String convert(Bitmap thumbnail){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static Bitmap convert(String encodedImage){
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    /**
     * Search an IDExternal
     * @param IDExternal or special
     * @return if is found return the object, null otherwise
     */
    public static ChekListCountryside isValidID(String IDExternal){
        if (IDExternal == null) return null;
        int idEmpleado;
        try {
            idEmpleado = Integer.parseInt(IDExternal);
        }catch (NumberFormatException ex){
            return null;
        }

        ChekListCountryside employee = null;
        ArrayList<ChekListCountryside> paseDeLista = ChekListCountrysideSingleton.getInstance().getChekListCountryside();
        for (ChekListCountryside item : paseDeLista){
            if (idEmpleado == item.getIdEmpleado()) {
                employee = item;
                break;
            }
        }
        return employee;
    }

    /**
     * Search by IDExternal and return its map data associated
     * @param ID number of ID
     * @param employees list of employees
     * @return a map found null otherwise
     */
    public static Map<String, String> isValidID(String ID, ArrayList<Map<String, String>> employees){
        if (TextUtils.isEmpty(ID)) return null;
        for (Map<String, String> employee : employees){
            if (employee.get("IDExterno").equals(ID)) {
                return employee;
            }
        }
        return null;
    }

}
