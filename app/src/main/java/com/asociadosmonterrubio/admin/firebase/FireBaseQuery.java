package com.asociadosmonterrubio.admin.firebase;

import com.asociadosmonterrubio.admin.models.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import com.asociadosmonterrubio.admin.models.Employee;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by joseluissanchezcruz on 4/23/17.
 */

public class FireBaseQuery {

    public static final String EMPLEADOS = "empleados";
    public static final String CAMPOS = "campos";
    public static final String SALIDAS = "salidasCopia";
    public static final String TEMPORADAS = "temporadas";
    public static final String IMAGENES = "imagenes";
    public static final String USUARIOS = "usuarios";
    public static final String PASE_DE_LISTA = "pase_de_lista";

    public static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private static StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    public static Employee pushNewEmployee(Employee employee){
        DatabaseReference reference = databaseReference.child(EMPLEADOS).push();
        reference.setValue(employee);
        employee.setKey(reference.getKey());
        return employee;
    }

    public static StorageReference getReferenceForSaveUserImage(String pushId){
        return storageReference.child(IMAGENES.concat("/").concat(pushId));
    }

    public static void pushEmployeeToTrip(String busNumber, String userId, String userName) {
        HashMap<String, String> params = new HashMap<>();
        params.put("nombre", userName);
        Calendar calendar = Calendar.getInstance();
        String date = calendar.get(Calendar.YEAR)+"-"+calendar.get(Calendar.MONTH)+"-"+calendar.get(Calendar.DAY_OF_MONTH);
        databaseReference.child(SALIDAS)
                .child(date)
                .child(String.valueOf(busNumber))
                .child(userId).setValue(params);
    }

    public static void pushPerfilTrabajadores(){
        ArrayList<String> perfilTrabajadores = new ArrayList<>();
        perfilTrabajadores.add("Jornalero");
        perfilTrabajadores.add("Campero");
        perfilTrabajadores.add("Cabo");
        databaseReference.child("perfilTrabajadores").setValue(perfilTrabajadores);
    }

    public static void pushSedes(){
        ArrayList<String> sedes = new ArrayList<>();
        sedes.add("Torreon");
        sedes.add("Sinaloa");
        databaseReference.child("sedes").setValue(sedes);
    }

    public static void pushTemporadaCampo(){
        String sede = "Torreon"; // Este valor sera dinamico en base a la sede que tiene el usuario
        String nombreTemporada = "Enero - Marzo 2019";
        String path = "/temporadas_sedes/"+sede+"/"+nombreTemporada;
        databaseReference.child(path).setValue(true);
    }

    public static void pushAsignacionEmpleados(){

        /***
         * *************Esta seccion es para insertar los datos de empleado en su respectivo campo.**************
         */

        //Nodo raiz a insertar
        String nodoRaiz = "asignacion_empleados_campo";

        /**
         * La sede a donde se insertara la información. Este valor sera dinamico en base a la sede que tiene el usuario que esta en session dando de alta las asignaciones en web.
         */
        String sede = "Torreon";

        /**
         * PushKey del empleado este ID lo genero firebase cuando se dio de alta al empleado
         */
        String pushKeyEmpleado = "ok0909kb0f0";

        /**
         * Para consultar este valor, se tiene que ir al nodo raiz  "temporada_sede/{sede}" se obtiene el nonbre de la temporada actual.
         */
        String temporadaActual = "Temporada Actual de Prueba";

        /**
         * Nombre del campo a donde se asignara el empleado. Para obtener la lista de los campos actuales se debe ir al nodo raiz "temporada_campo/{temporadaActual}/{nombrCcampoSeleccionado}".
         */
        String nombreCampoSeleccionado = "CampoAtlapexco";

        /**
         * ID que se genero para el empleado, para obtener el proximo ID a usar se debe usar el valor index que esta en la raiz (INDEX)
         */
        String ID = "5";

        /**
         * A este nivel se va a guardar la informacion completa del empleado.
         * Como habiamos comentado anteriormente, toda la información del empleado se va a tener alojada en otro nodo para
         * evitar la sobrecarga de los nodos. Inicialmente se almacena en el nodo de "empleados", pero cuando llega
         * el momento de asignar el empleado a un campo, todos los atributos que tiene en su objeto se migran a  esta nueva rama.
         *
         * La ruta a guardar tendra la siguiente nomenclatura:  "asignacion_empleados_campo/{sede}/{temporadaActual}/{campo}/{ID}"
         *
         */
        String pathEmpleadoAsignado = nodoRaiz + "/" + sede + "/" + temporadaActual + "/" + nombreCampoSeleccionado + "/" + ID;

        Employee employee = new Employee();

        //Como podemos ver el pushKey pasa a ser parte un atributo del empleado y la
        // referencia que ahora tomara su lugar sera el ID que se le asigno en base al index de los trabajadores asignado previamente
        employee.setKey(pushKeyEmpleado);

        //Los demas atributos ya conocidos del empleado provenientes del nodo empleados.
        employee.setNombre("mi nombre...");
        employee.setActividad("Jornalero");

        //Insertar informacion del empleado
        databaseReference.child(pathEmpleadoAsignado).setValue(employee);

        /***
         * *************Esta seccion es para insertar los datos del empleado en el apartado del pase de lista.**************
         * Este nodo guardara la información light del empleado para su pase de lista diario, en ella solo se guardaran los siguientes valores ID y perfil
         * La ruta a guardar tendra la siguiente nomenclatura:  "asignacion_empleados_campo/{sede}/{campo}/{ID}"
         */

        //Nodo raiz a insertar
        String nodoRaizPaseDeLista = "pase_de_lista";

        String pathPaseDeLista = nodoRaizPaseDeLista + "/" + sede + "/" + nombreCampoSeleccionado;
        databaseReference.child(pathPaseDeLista).child(ID).setValue(employee.getActividad()); //actividad es el perfil del empleado "Jornalero, campero, etc.".

    }

    public static void pushAsistencia(){
        for (int i = 0;  i < 2000; i++){
            databaseReference.child("pase_de_lista/Torreon/Campo1/"+i).setValue("Jornalero");
        }
    }

}
