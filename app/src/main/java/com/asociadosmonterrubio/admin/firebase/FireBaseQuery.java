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
    public static final String EMPLEADOS                    = "empleados";      //First place where new employees are stored
    public static final String CAMPOS                       = "campos";     //Fields available for each sede
    public static final String SALIDAS                      = "salidas";        // Bus Departures
	private static final String SALIDAS_COPIA               = "salidasCopia";       //Copy of Bus Departures. This is used for some specific tasks
    public static final String TEMPORADAS                   = "temporadas";     //Seasons, normally they took one year
    private static final String IMAGENES                    = "imagenes";       //place where images are stored
    public static final String USUARIOS                     = "usuarios";       //users of the app, roles
    public static final String PASE_DE_LISTA                = "pase_de_lista";      // Check list
    public static final String ASISTENCIAS                  = "asistencias";        //Check list of each employee
    private static final String REGISTROS_TRABAJADORES      = "registros_trabajadores";     //Activity of each employee during his contract
	public static final String TEMPORADA_CAMPO              = "temporada_campo";        //Seasons associated to a field
	public static final String TEMPORADAS_SEDES             = "temporadas_sedes";       //Seasons associated to a sede
	public static final String ASIGNACION_EMPLEADOS_CAMPO   = "asignacion_empleados_campo";     //Association employee - field
	public static final String MOTIVOS_BAJA                 = "motivos_baja";       //Quits
	private static final String BAJAS_PENDIENTES            = "bajas_pendientes";       //Pending quits
    public static final String LISTA_NEGRA                  = "lista_negra";        //Black list, employees that can not return again into the business
    public static final String INDEX                        = "index";      //Index used to increment each new row inserted in the database when is assigned

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
     * Actualiza la informacion de un empleado tales como el nombre, apellidos, fotografia, etc.
     * No se actualizan los ids externos o tipo de perfil
     * @param path ruta del id del empleado a actualizar
     * @param info datos del empleado a actualizar
     */
    public static void updateInfoEmployee(String path, Map<String, String> info){
        DatabaseReference reference =  databaseReference.child(path);
        for (Map.Entry<String,String> data : info.entrySet())
            reference.child(data.getKey()).setValue(data.getValue());
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

    /**
     * Save data in node sales and loans
     * @param ID IDExterno
     * @param fecha date when the sale has made
     * @param cantidad quantity of the loan
     * @param tipo kind of loan (sales and loans)
     */
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

    /**
     * Save user's information in node "asignacion_empleados_campo"
     * @param pathRoot string path
     * @param employee Employee data
     * @param ID IDExterno
     * @param isCampoEspecial true if it is a field special
     */
    public static void pushEmployeeSoloToField(String pathRoot, Employee employee, long ID, boolean isCampoEspecial){
        String campo = SingletonUser.getInstance().getUsuario().getCampo();
        String sede = SingletonUser.getInstance().getUsuario().getSede();

        Map<String, String> mapEmployeeAssigment = FirebaseStructure.getMapEmployee(employee);
        mapEmployeeAssigment.put(Employee._PUSH_ID, employee.getKey());
        mapEmployeeAssigment.put(Employee._SEDE, SingletonUser.getInstance().getUsuario().getSede());
        databaseReference.child(pathRoot).child(String.valueOf(ID)).setValue(mapEmployeeAssigment);
        databaseReference.child(pathRoot).child(String.valueOf(ID)).child("campos").child(SingletonUser.getInstance().getUsuario().getCampo()).setValue(true);

        //Si NO es un campo especial. Entonces insertamos tambien en el nodo de pase de lista.
        if (!isCampoEspecial)
            databaseReference.child(PASE_DE_LISTA).child(sede).child(campo).child(String.valueOf(ID)).setValue(employee.getActividad());

        //Eliminamos el empleado del nodo de Empleados.
        databaseReference.child(EMPLEADOS).child(employee.getKey()).removeValue();
    }

    /**
     * Set employee to fire in database
     * @param employeeId Id employee
     * @param data information about others
     */
    public static void sendEmployeePendingToBeFired(String employeeId, Map<String,String> data){
        String campo = SingletonUser.getInstance().getUsuario().getCampo();
        String sede = SingletonUser.getInstance().getUsuario().getSede();
        databaseReference.child(BAJAS_PENDIENTES).child(sede).child(campo).child(employeeId).setValue(data);
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

    //----These methods below are deprecated. They still are there for knowledge purposes.

    @Deprecated
    /**
     * Attention don't use this method. It still is here for knowledge purposes.
     * How to add new profile. Just add items as many as you need and push them.
     */
    public static void pushPerfilTrabajadores(){
        ArrayList<String> perfilTrabajadores = new ArrayList<>();
        perfilTrabajadores.add("Jornalero");
        perfilTrabajadores.add("Campero");
        perfilTrabajadores.add("Cabo");
        databaseReference.child("perfilTrabajadores").setValue(perfilTrabajadores);
    }

    @Deprecated
    /**
     * Attention don't use this method. It still is here for knowledge purposes.
     * How to push a new "sede" item. Just add items as many as you need and push them.
     */
    public static void pushSedes(){
        ArrayList<String> sedes = new ArrayList<>();
        sedes.add("Torreon");
        sedes.add("Sinaloa");
        databaseReference.child("sedes").setValue(sedes);
    }

    @Deprecated
    /**
     * Attention don't use this method. It still is here for knowledge purposes.
     * How to push a relation field - season
     */
    public static void pushTemporadaCampo(){
        String sede = "Torreon"; // Este valor sera dinamico en base a la sede que tiene el usuario
        String nombreTemporada = "Enero - Marzo 2019";
        String path = "/temporadas_sedes/"+sede+"/"+nombreTemporada;
        databaseReference.child(path).setValue(true);
    }

    @Deprecated
    /**
     * Attention don't use this method. It still is here for knowledge purposes.
     * How to create "motivos_baja" node. Just add items as many as you need and push them.
     */
    public static void pushReasonsToFire(){
        ArrayList<String> motivos = new ArrayList<>();
        motivos.add("Consumo de drogas");
        motivos.add("Faltista");
        motivos.add("Enfermedad");
        motivos.add("Grillero");
        databaseReference.child(MOTIVOS_BAJA).setValue(motivos);
    }

    @Deprecated
    /**
     * Attention don't use this method. It still is here for knowledge purposes.
     * How to remove a employee from his charge
     */
    public static void pushFire(Map<String, String> params){
        DatabaseReference ref = databaseReference.child(BAJAS_PENDIENTES).child(params.get(Employee._DEF_ID));
        params.remove(Employee._DEF_ID);
        ref.setValue(params);
    }

}
