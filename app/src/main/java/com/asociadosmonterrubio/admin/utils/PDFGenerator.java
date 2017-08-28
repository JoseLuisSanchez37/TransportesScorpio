package com.asociadosmonterrubio.admin.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.widget.Toast;

import com.asociadosmonterrubio.admin.activities.ActivityGenerateCredentials;
import com.asociadosmonterrubio.admin.activities.AndroidBarcodeView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Map;

/**
 * Created by joseluissanchezcruz on 8/28/17.
 */

public class PDFGenerator {

    //image
    private static final int INIT_IMAGE_X = 15;
    private static final int INIT_IMAGE_Y = 40;

    //campo
    private static final int INIT_CAMPO_X = 110;
    private static final int INIT_CAMPO_Y = 25;

    //nombre
    private static final int INIT_NOMBRE_X = 120;
    private static final int INIT_NOMBRE_Y = 60;

    //fecha_inicio
    private static final int INIT_FECHA_INICIO_X = 120;
    private static final int INIT_FECHA_INICIO_Y = 95;

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
    private ActivityGenerateCredentials activity;
    private String fechaSeleccionada;

    public PDFGenerator(PdfDocument document, ActivityGenerateCredentials activity, String fechaSeleccionada){
        this.document = document;
        this.activity = activity;
        this.fechaSeleccionada = fechaSeleccionada;
    }

    public void makeCredentials(){
        //Config PDF document
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

        //number of jump lines
        int numberCredentials = 0;

        for (int i = 0; i < activity.empleadosEncontrados.size(); i++){

            Boolean isSalto;
            if (numberCredentials > 1) {
                isSalto = ((numberCredentials % 2) == 0);
                if (isSalto) {
                    init_image_y += INIT_BORDE_Y + FINAL_BORDE_Y;
                    init_campo_y += INIT_BORDE_Y + FINAL_BORDE_Y;
                    init_nombre_y += INIT_BORDE_Y + FINAL_BORDE_Y;
                    init_fecha_inicio_y += INIT_BORDE_Y + FINAL_BORDE_Y;
                    init_fecha_fin_y += INIT_BORDE_Y + FINAL_BORDE_Y;
                    init_barcode_y += INIT_BORDE_Y + FINAL_BORDE_Y;

                    init_borde_y += INIT_BORDE_Y + FINAL_BORDE_Y;
                    final_borde_y += INIT_BORDE_Y + FINAL_BORDE_Y;

                    //********
                    init_image_x = INIT_IMAGE_X;
                    init_campo_x = INIT_CAMPO_X;
                    init_nombre_x = INIT_NOMBRE_X;
                    init_fecha_inicio_x = INIT_FECHA_INICIO_X;
                    init_fecha_fin_x = INIT_FECHA_FIN_X;
                    init_barcode_x = INIT_BARCODE_X;

                    init_borde_x = INIT_BORDE_X;
                    final_borde_x = FINAL_BORDE_X;

                } else {
                    init_image_x += INIT_BORDE_X + FINAL_BORDE_X;
                    init_campo_x += INIT_BORDE_X + FINAL_BORDE_X;
                    init_nombre_x += INIT_BORDE_X + FINAL_BORDE_X;
                    init_fecha_inicio_x += INIT_BORDE_X + FINAL_BORDE_X;
                    init_fecha_fin_x += INIT_BORDE_X + FINAL_BORDE_X;
                    init_barcode_x += INIT_BORDE_X + FINAL_BORDE_X;

                    init_borde_x += INIT_BORDE_X + FINAL_BORDE_X;
                    final_borde_x += INIT_BORDE_X + FINAL_BORDE_X;

                }
            }

            //Image
            Map<String, String> employee = activity.empleadosEncontrados.get(i);
            Bitmap imageEmployee = activity.imagenes.get(employee.get("pushId"));
            if (imageEmployee != null) {
                Bitmap resized = Bitmap.createScaledBitmap(imageEmployee, 100, 90, true);

                //Setting image
                canvas.drawBitmap(resized, init_image_x, init_image_y, null);
            }

            String[] fechaSalida = employee.get("Fecha_Salida").split("-");
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, Integer.parseInt(fechaSalida[0]));
            calendar.set(Calendar.MONTH, (Integer.parseInt(fechaSalida[1]) -1));
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(fechaSalida[2]));

            //Agregar 2 dias cuando es un empleado que llego por camion, para un empleado solo es solo 1 dia
            calendar.add(Calendar.DAY_OF_MONTH, 2);
            String fechaInicio = calendar.get(Calendar.YEAR) + "-" +(calendar.get(Calendar.MONTH) +1) +"-"+calendar.get(Calendar.DAY_OF_MONTH);

            //Agregar 90 dias que es la duracion del contrato
            calendar.add(Calendar.DAY_OF_MONTH, 90);
            String fechaFin = calendar.get(Calendar.YEAR) + "-" +(calendar.get(Calendar.MONTH) +1) +"-"+calendar.get(Calendar.DAY_OF_MONTH);

            //Setting field CAMPO
            Paint paintCampo = new Paint();
            paintCampo.setColor(Color.BLACK);
            paintCampo.setTextSize(15f);
            canvas.drawText(SingletonUser.getInstance().getUsuario().getCampo(), init_campo_x, init_campo_y, paintCampo);

            //Setting field NOMBRE
            Paint paintNombre = new Paint();
            paintNombre.setColor(Color.BLACK);
            paintNombre.setTextSize(10f);
            canvas.drawText(employee.get("NombreCompleto"), init_nombre_x, init_nombre_y, paintNombre);

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
                init_fecha_inicio_x += INIT_BORDE_X + FINAL_BORDE_X;
                init_fecha_fin_x += INIT_BORDE_X + FINAL_BORDE_X;
                init_barcode_x += INIT_BORDE_X + FINAL_BORDE_X;

                init_borde_x += INIT_BORDE_X + FINAL_BORDE_X;
                final_borde_x += INIT_BORDE_X + FINAL_BORDE_X;

            }

            if (numberCredentials == 7){
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

            }else {
                numberCredentials++;
            }
        }

        document.finishPage(page);

        //Saving PDF file in storage
        savePDFile();
    }

    private void savePDFile(){
        String targetPdf = Environment.getExternalStorageDirectory() + "/TransportesScorpio";
        File filePath = new File(targetPdf);
        if (!filePath.exists()) filePath.mkdir();
        String pathPDFile = SingletonUser.getInstance().getUsuario().getCampo()+fechaSeleccionada + ".pdf";
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
