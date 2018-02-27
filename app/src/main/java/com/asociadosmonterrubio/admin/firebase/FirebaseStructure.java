package com.asociadosmonterrubio.admin.firebase;

import com.asociadosmonterrubio.admin.models.Employee;
import com.asociadosmonterrubio.admin.models.Usuario;
import com.asociadosmonterrubio.admin.utils.SingletonUser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by joseluissanchezcruz on 8/26/17.
 */

public class FirebaseStructure {

    public static Map<String, String> getMapEmployee(Employee employee){
        Map<String, String> map = new HashMap<>();
        map.put(Employee._ACTIVIDAD, employee.getActividad());
        map.put(Employee._APELLIDO_P, employee.getApellido_Paterno());
        map.put(Employee._APELLIDO_M, employee.getApellido_Materno());
        map.put(Employee._CURP, employee.getCURP());
        map.put(Employee._CONTRATO, employee.getContrato());
        map.put(Employee._ENGANCHE, employee.getEnganche());
        map.put(Employee._FECHA_NAC, employee.getFecha_Nacimiento());
        map.put(Employee._LUGAR_NAC, employee.getLugar_Nacimiento());
        map.put(Employee._NOMBRE, employee.getNombre());
        map.put(Employee._FECHA_SAL, employee.getFecha_Salida());
        if (SingletonUser.getInstance().getUsuario().getRol().equals(Usuario.ROL_ENCARGADO_CAMPO)){
            map.put(Employee._MODALIDAD, Employee._MOD_SOLO);
            map.put(Employee._ENGANCHE, "0");
            map.put(Employee._CAMION, "0");
        }
        return map;
    }

}
