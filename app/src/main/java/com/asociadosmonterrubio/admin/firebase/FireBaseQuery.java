package com.asociadosmonterrubio.admin.firebase;

import com.asociadosmonterrubio.admin.utils.SingletonUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.asociadosmonterrubio.admin.models.Employee;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by joseluissanchezcruz on 4/23/17.
 */

public class FireBaseQuery {

    /**
     * ¡¡¡¡¡¡¡¡¡  ATTENTION  !!!!!!!
     * THIS ASSIGNATION CAN NOT BE CHANGED BECAUSE THEY ARE PART OF FIREBASE'S STRUCTURE.
     * PLEASE REFER TO THE OWN PROJECT TO MAKE CHANGES.
     */
    public static final String EMPLEADOS = "empleados";
    public static final String CAMPOS = "campos";
    public static final String SALIDAS = "salidas";
	public static final String SALIDAS_COPIA = "salidasCopia";
    public static final String TEMPORADAS = "temporadas";
    public static final String IMAGENES = "imagenes";
    public static final String USUARIOS = "usuarios";
    public static final String PASE_DE_LISTA = "pase_de_lista";
    public static final String ASISTENCIAS = "asistencias";
    public static final String REGISTROS_TRABAJADORES = "registros_trabajadores";
	public static final String TEMPORADA_CAMPO = "temporada_campo";
	public static final String TEMPORADAS_SEDES = "temporadas_sedes";
	public static final String ASIGNACION_EMPLEADOS_CAMPO = "asignacion_empleados_campo";
	public static final String MOTIVOS_BAJA = "motivos_baja";
	public static final String BAJAS_PENDIENTES = "bajas_pendientes";
    public static final String INDEX = "index";

    //Firebase references
    public static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private static StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    /**
     * Insertar un nuevo empleado a la base de datos
     * @param employee Estructura del objecto Empleado
     * @return El objeto empleado despues de realizar la inserción en la base de datos con el pushId asignado
     */
    public static Employee pushNewEmployee(Employee employee){
        Map<String, String> mapEmployee = FirebaseStructure.getMapEmployee(employee);
        DatabaseReference reference = databaseReference.child(EMPLEADOS).push();
        reference.setValue(mapEmployee);
        employee.setKey(reference.getKey());
        return employee;
    }

    /**
     * Obtener referencia de Firebase Storage para guardar la imagen del empleado
     * @param pushId pushId del trabajador en donde se guardara su foto
     * @return Referencia Firebase para consultar o modificar su registro
     */
    public static StorageReference getReferenceForSaveUserImage(String pushId){
        return storageReference.child(IMAGENES.concat("/").concat(pushId));
    }

    /**
     * Pase de lista a un trabajador
     * @param ID ID unico de trabajador, ya sea ID asignado por el sistem o por el campo
     * @param Perfil Perfil del trabajador, JORNALERO, CAMPERO, etc.
     */
    public static void PushCheckList(String ID,String Perfil, Calendar calendarSelected){
        String date = calendarSelected.get(Calendar.YEAR)+"-"+(calendarSelected.get(Calendar.MONTH) +1 )+"-"+calendarSelected.get(Calendar.DAY_OF_MONTH);
        String campo = SingletonUser.getInstance().getUsuario().getCampo();
        String sede = SingletonUser.getInstance().getUsuario().getSede();

        String pathPaseDeListaPorCampo = ASISTENCIAS + "/" +  sede + "/" + campo + "/" + date;
        String pathPaseDeListaPorEmpleado = REGISTROS_TRABAJADORES + "/" + sede + "/" + campo + "/" + ID + "/" + ASISTENCIAS + "/" + date;

        databaseReference.child(pathPaseDeListaPorCampo).child(ID).setValue(Perfil);
        databaseReference.child(pathPaseDeListaPorEmpleado).setValue(Perfil);
    }

    public static void pushGirosPrestamos(String ID, String fecha, String cantidad, String tipo){

        String campo = SingletonUser.getInstance().getUsuario().getCampo();
        String sede = SingletonUser.getInstance().getUsuario().getSede();

        String path =  REGISTROS_TRABAJADORES + "/" + sede + "/" + campo + "/" + ID + "/" + tipo.toLowerCase() + "/" + fecha;

        databaseReference.child(path).setValue(cantidad);
    }

    /**
     * Asignar empleados a un camion
     * @param busNumber numero del camoin
     * @param pushId pushId del trabajador a agregar
     * @param userName nombre del trabajador
     * @param departureDate fecha de salida del camion
     */
    public static void pushEmployeeToTrip(String busNumber, String pushId, String userName, String departureDate) {
        HashMap<String, String> params = new HashMap<>();
        params.put("nombre", userName);
        //Calendar calendar = Calendar.getInstance();
        //String date = calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH) +1 )+"-"+calendar.get(Calendar.DAY_OF_MONTH);
        //Agregar id del empleado en referencias de salidas y salidasCopia
        databaseReference.child(SALIDAS)
                .child(departureDate)
                .child(String.valueOf(busNumber))
                .child(pushId).setValue(params);
		databaseReference.child(SALIDAS_COPIA)
				.child(departureDate)
				.child(String.valueOf(busNumber))
				.child(pushId).setValue(params);
        //Agregar al atributo del empleado el numero del camion al que fue asignado.
        databaseReference.child(EMPLEADOS).child(pushId).child(Employee._CAMION).setValue(busNumber);
        //Agregar fecha de salida a empleado
        databaseReference.child(EMPLEADOS).child(pushId).child(Employee._FECHA_SAL).setValue(departureDate);
    }

    public static void pushEmployeeSoloToField(String pathRoot, Employee employee, long ID, boolean isCampoEspecial){
        String campo = SingletonUser.getInstance().getUsuario().getCampo();
        String sede = SingletonUser.getInstance().getUsuario().getSede();

        Map<String, String> mapEmployeeAssigment = FirebaseStructure.getMapEmployee(employee);
        mapEmployeeAssigment.put(Employee._PUSH_ID, employee.getKey());
        mapEmployeeAssigment.put(Employee._SEDE, SingletonUser.getInstance().getUsuario().getSede());
        databaseReference.child(pathRoot).child(String.valueOf(ID)).setValue(mapEmployeeAssigment);
        databaseReference.child(pathRoot).child(String.valueOf(ID)).child("campos").child(SingletonUser.getInstance().getUsuario().getCampo()).setValue(true);

        //Si NO es un campo especial. Entonces insertamos tambien en el nodo de pase de lista.
        if (!isCampoEspecial){
            databaseReference.child(PASE_DE_LISTA).child(sede).child(campo).child(String.valueOf(ID)).setValue(employee.getActividad());
        }

        //Eliminamos el empleado del nodo de Empleados.
        databaseReference.child(EMPLEADOS).child(employee.getKey()).removeValue();

    }

    /**
     *  DatabaseReference para obtener los pase de lista del dia actual del pase.
     * @return referencia de base de datos de los pases de lista
     */
    public static DatabaseReference obtenerAsistencias(String date){
        String campo = SingletonUser.getInstance().getUsuario().getCampo();
        String sede = SingletonUser.getInstance().getUsuario().getSede();
        String path = ASISTENCIAS + "/" +  sede + "/" + campo + "/" + date ;
        return FireBaseQuery.databaseReference.child(path);
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

    public static void pushReasonsToFire(){
		ArrayList<String> motivos = new ArrayList<>();
        motivos.add("Consumo de drogas");
        motivos.add("Faltista");
        motivos.add("Enfermedad");
        motivos.add("Grillero");
        databaseReference.child(MOTIVOS_BAJA).setValue(motivos);
	}

	public static void pushFire(Map<String, String> params){
		DatabaseReference ref = databaseReference.child(BAJAS_PENDIENTES).child(params.get("ID"));
		params.remove("ID");
		ref.setValue(params);
	}

}
