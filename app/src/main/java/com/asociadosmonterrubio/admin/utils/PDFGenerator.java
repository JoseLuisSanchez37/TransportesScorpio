package com.asociadosmonterrubio.admin.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.asociadosmonterrubio.admin.activities.AndroidBarcodeView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

/**
 * Created by joseluissanchezcruz on 8/28/17.
 */

public class PDFGenerator {

    private static final int MAX_CREDENTIALS_PEER_PAGE = 7;

    //image
    private static final int INIT_IMAGE_X = 15;
    private static final int INIT_IMAGE_Y = 35;

    //campo
    private static final int INIT_CAMPO_X = 110;
    private static final int INIT_CAMPO_Y = 25;

    //nombre
    private static final int INIT_NOMBRE_X = 120;
    private static final int INIT_NOMBRE_Y = 60;

    //lugar nacimiento
    private static final int INIT_LUGAR_NAC_X = 120;
    private static final int INIT_LUGAR_NAC_Y = 83;

    //fecha_inicio
    private static final int INIT_FECHA_INICIO_X = 120;
    private static final int INIT_FECHA_INICIO_Y = 106;

    //fecha_fin
    private static final int INIT_FECHA_FIN_X = 120;
    private static final int INIT_FECHA_FIN_Y = 130;

    //borde
    private static final int INIT_BORDE_X = 10;
    private static final int INIT_BORDE_Y = 10;
    private static final int FINAL_BORDE_X = 300;
    private static final int FINAL_BORDE_Y = 200;

    //barcode
    private static final int INIT_BARCODE_X = 11;
    private static final int INIT_BARCODE_Y = 140;

    private PdfDocument document;
    private AppCompatActivity activity;
    private String pdfName;
    private ArrayList<Map<String, String>> empleadosEncontrados;
    private Map<String, Bitmap> imagenes;

    public PDFGenerator(PdfDocument document, AppCompatActivity activity, String pdfName, ArrayList<Map<String, String>> empleadosEncontrados, Map<String, Bitmap> imagenes){
        this.document = document;
        this.activity = activity;
        this.pdfName = pdfName;
        this.empleadosEncontrados = empleadosEncontrados;
        this.imagenes = imagenes;
    }

    public void makeCredentials(){
        //Config PDF document
        document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(620, 836, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        //Initialization canvas
        Canvas canvas = page.getCanvas();

        //Initialization of coordinates
        //image
        int init_image_x = INIT_IMAGE_X;
        int init_image_y = INIT_IMAGE_Y;

        //campo
        int init_campo_x = INIT_CAMPO_X;
        int init_campo_y = INIT_CAMPO_Y;

        //nombre
        int init_nombre_x = INIT_NOMBRE_X;
        int init_nombre_y = INIT_NOMBRE_Y;

        int init_lugar_nac_x = INIT_LUGAR_NAC_X;
        int init_lugar_nac_y = INIT_LUGAR_NAC_Y;

        //fecha_inicio
        int init_fecha_inicio_x = INIT_FECHA_INICIO_X;
        int init_fecha_inicio_y = INIT_FECHA_INICIO_Y;

        //fecha_fin
        int init_fecha_fin_x = INIT_FECHA_FIN_X;
        int init_fecha_fin_y = INIT_FECHA_FIN_Y;

        //borde
        int init_borde_x = INIT_BORDE_X;
        int init_borde_y = INIT_BORDE_Y;
        int final_borde_x = FINAL_BORDE_X;
        int final_borde_y = FINAL_BORDE_Y;

        //barcode
        int init_barcode_x = INIT_BARCODE_X;
        int init_barcode_y = INIT_BARCODE_Y;

        //number of skip lines
        int numberCredentials = 0;

        for (int i = 0; i < empleadosEncontrados.size(); i++){

            boolean isSkip;
            if (numberCredentials > 1) {
                isSkip = ((numberCredentials % 2) == 0);
                if (isSkip) {
                    init_image_y += INIT_BORDE_Y + FINAL_BORDE_Y;
                    init_campo_y += INIT_BORDE_Y + FINAL_BORDE_Y;
                    init_nombre_y += INIT_BORDE_Y + FINAL_BORDE_Y;
                    init_lugar_nac_y += INIT_BORDE_Y + FINAL_BORDE_Y;
                    init_fecha_inicio_y += INIT_BORDE_Y + FINAL_BORDE_Y;
                    init_fecha_fin_y += INIT_BORDE_Y + FINAL_BORDE_Y;
                    init_barcode_y += INIT_BORDE_Y + FINAL_BORDE_Y;

                    init_borde_y += INIT_BORDE_Y + FINAL_BORDE_Y;
                    final_borde_y += INIT_BORDE_Y + FINAL_BORDE_Y;

                    //********
                    init_image_x = INIT_IMAGE_X;
                    init_campo_x = INIT_CAMPO_X;
                    init_nombre_x = INIT_NOMBRE_X;
                    init_lugar_nac_x = INIT_LUGAR_NAC_X;
                    init_fecha_inicio_x = INIT_FECHA_INICIO_X;
                    init_fecha_fin_x = INIT_FECHA_FIN_X;
                    init_barcode_x = INIT_BARCODE_X;

                    init_borde_x = INIT_BORDE_X;
                    final_borde_x = FINAL_BORDE_X;

                } else {
                    init_image_x += INIT_BORDE_X + FINAL_BORDE_X;
                    init_campo_x += INIT_BORDE_X + FINAL_BORDE_X;
                    init_nombre_x += INIT_BORDE_X + FINAL_BORDE_X;
                    init_lugar_nac_x += INIT_BORDE_X + FINAL_BORDE_X;
                    init_fecha_inicio_x += INIT_BORDE_X + FINAL_BORDE_X;
                    init_fecha_fin_x += INIT_BORDE_X + FINAL_BORDE_X;
                    init_barcode_x += INIT_BORDE_X + FINAL_BORDE_X;

                    init_borde_x += INIT_BORDE_X + FINAL_BORDE_X;
                    final_borde_x += INIT_BORDE_X + FINAL_BORDE_X;
                }
            }

            Map<String, String> employee = empleadosEncontrados.get(i);

            //Setting image
            Bitmap imageEmployee = imagenes.get(employee.get("pushId"));
            if (imageEmployee != null) {
                Bitmap resized = Bitmap.createScaledBitmap(imageEmployee, 90, 100, true);
                canvas.drawBitmap(resized, init_image_x, init_image_y, null);
            }

            //Getting fecha de salida
            String[] fechaSalida = employee.get("Fecha_Salida").split("-");
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, Integer.parseInt(fechaSalida[0]));
            calendar.set(Calendar.MONTH, (Integer.parseInt(fechaSalida[1]) -1));
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(fechaSalida[2]));

            //Para la fecha de inicio: Fecha de Inicio = Fecha de salida + N dias dependiendo el tipo de salida, Camion, Solo, Renovacion
            //Agregar 2 dias cuando es un empleado que llego por camión
            //Agregar 1 dias cuando es un empleado renovacion y cuando es solo
            if (employee.containsKey("Modalidad") && (employee.get("Modalidad").equalsIgnoreCase("Solo") || employee.get("Modalidad").equalsIgnoreCase("Renovacion")))
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            else
                calendar.add(Calendar.DAY_OF_MONTH, 2); //Por camion

            String fechaInicio = calendar.get(Calendar.YEAR) + "-" +(calendar.get(Calendar.MONTH) +1) +"-"+calendar.get(Calendar.DAY_OF_MONTH);

            //Agregar duracion en dias del contrato. Fecha de Fin : Fecha de Inicio + N dias de duración del contrato. Si no tiene el valor, se agrega el valor de 90 por default
            calendar.add(Calendar.DAY_OF_MONTH, employee.containsKey("Contrato") ? Integer.parseInt(employee.get("Contrato")) : 90);
            String fechaFin = calendar.get(Calendar.YEAR) + "-" +(calendar.get(Calendar.MONTH) +1) +"-"+calendar.get(Calendar.DAY_OF_MONTH);

            //Setting field CAMPO
            Paint paintCampo = new Paint();
            paintCampo.setColor(Color.BLACK);
            paintCampo.setTextSize(15f);
            String campo = SingletonUser.getInstance().getUsuario().getCampo();
            if (campo.equalsIgnoreCase("NAZARIO"))
                campo = "AGRICOLA EL NAZARIO";
            canvas.drawText(campo, init_campo_x, init_campo_y, paintCampo);

            //Setting field NOMBRE
            Paint paintNombre = new Paint();
            paintNombre.setColor(Color.BLACK);
            paintNombre.setTextSize(10f);
            canvas.drawText(employee.get("NombreCompleto"), init_nombre_x, init_nombre_y, paintNombre);

            //Setting field LUGAR DE NACIMIENTO
            Paint paintLugarNacimiento = new Paint();
            paintLugarNacimiento.setColor(Color.BLACK);
            paintLugarNacimiento.setTextSize(10f);
            int lenght = employee.get("Lugar_Nacimiento").length();
            String lugar;
            if (lenght > 22){
                lugar = employee.get("Lugar_Nacimiento");
                lugar = lugar.substring(0, 22);
            }else
                lugar = employee.get("Lugar_Nacimiento");

            canvas.drawText("LUGAR: "+lugar, init_lugar_nac_x, init_lugar_nac_y, paintLugarNacimiento);

            //Setting field FECHA_INICIO
            Paint paintFechaInicio = new Paint();
            paintFechaInicio.setColor(Color.BLACK);
            paintFechaInicio.setTextSize(10f);
            canvas.drawText("FECHA INICIO: "+fechaInicio, init_fecha_inicio_x, init_fecha_inicio_y, paintFechaInicio);

            //Setting field FECHA_FIN
            Paint paintFechaFin = new Paint();
            paintFechaFin.setColor(Color.BLACK);
            paintFechaFin.setTextSize(10f);
            canvas.drawText("FECHA FIN: "+fechaFin, init_fecha_fin_x, init_fecha_fin_y, paintFechaFin);

            //Setting BORDER
            RectF rectF = new RectF(init_borde_x, init_borde_y, final_borde_x, final_borde_y);
            Paint paintBorder = new Paint();
            paintBorder.setStyle(Paint.Style.STROKE);
            paintBorder.setColor(Color.BLACK);
            canvas.drawRect(rectF, paintBorder);

            //Setting barcode
            String ID = employee.containsKey("IDExterno") ? employee.get("IDExterno") : employee.get("ID");
            AndroidBarcodeView barcodeView = new AndroidBarcodeView(activity, ID, init_barcode_x, init_barcode_y);
            barcodeView.draw(canvas);

            if (numberCredentials == 0){
                init_image_x += INIT_BORDE_X + FINAL_BORDE_X;
                init_campo_x += INIT_BORDE_X + FINAL_BORDE_X;
                init_nombre_x += INIT_BORDE_X + FINAL_BORDE_X;
                init_lugar_nac_x += INIT_BORDE_X + FINAL_BORDE_X;
                init_fecha_inicio_x += INIT_BORDE_X + FINAL_BORDE_X;
                init_fecha_fin_x += INIT_BORDE_X + FINAL_BORDE_X;
                init_barcode_x += INIT_BORDE_X + FINAL_BORDE_X;

                init_borde_x += INIT_BORDE_X + FINAL_BORDE_X;
                final_borde_x += INIT_BORDE_X + FINAL_BORDE_X;

            }

            if (numberCredentials == MAX_CREDENTIALS_PEER_PAGE){
                numberCredentials = 0;
                //image
                init_image_x = INIT_IMAGE_X;
                init_image_y = INIT_IMAGE_Y;

                //campo
                init_campo_x = INIT_CAMPO_X;
                init_campo_y = INIT_CAMPO_Y;

                //nombre
                init_nombre_x = INIT_NOMBRE_X;
                init_nombre_y = INIT_NOMBRE_Y;

                //lugar nacimiento
                init_lugar_nac_x = INIT_LUGAR_NAC_X;
                init_lugar_nac_y = INIT_LUGAR_NAC_Y;

                //fecha_inicio
                init_fecha_inicio_x = INIT_FECHA_INICIO_X;
                init_fecha_inicio_y = INIT_FECHA_INICIO_Y;

                //fecha_fin
                init_fecha_fin_x = INIT_FECHA_FIN_X;
                init_fecha_fin_y = INIT_FECHA_FIN_Y;

                //borde
                init_borde_x = INIT_BORDE_X;
                init_borde_y = INIT_BORDE_Y;
                final_borde_x = FINAL_BORDE_X;
                final_borde_y = FINAL_BORDE_Y;

                //barcode
                init_barcode_x = INIT_BARCODE_X;
                init_barcode_y = INIT_BARCODE_Y;

                //Finishing document
                document.finishPage(page);
                page = document.startPage(pageInfo);
                canvas = page.getCanvas();

            }else
                numberCredentials++;
        }

        document.finishPage(page);

        //Saving PDF file in storage
        savePDFile();
    }

    private void savePDFile(){
        String targetPdf = Environment.getExternalStorageDirectory() + "/TransportesScorpio";
        File filePath = new File(targetPdf);
        if (!filePath.exists()) filePath.mkdir();
        String pathPDFile = SingletonUser.getInstance().getUsuario().getCampo()+pdfName + ".pdf";
        File pdfFile = new File(filePath, pathPDFile);
        if (pdfFile.exists()) pdfFile.delete();
        try{
            document.writeTo(new FileOutputStream(pdfFile));
            document.close();
            Toast.makeText(activity, "CREDENCIALES GENERADAS CON EXITO", Toast.LENGTH_LONG).show();
        }catch (IOException e){
            e.printStackTrace();
            Toast.makeText(activity, "OCURRIO UN ERROR: " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

}
