package com.asociadosmonterrubio.admin.activities;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.asociadosmonterrubio.admin.R;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class DialogConfirmTrip extends DialogFragment implements View.OnClickListener{

    private EditText edt_trip_departure_date, edt_trip_number_bus;
    private ConfirmTrip callback;
    private Button btn_cancel, btn_confirm_trip;
    private Calendar calendar = Calendar.getInstance();

    public DialogConfirmTrip() { }

    public void setCallback(ConfirmTrip callback){
        this.callback = callback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_confirm_trip, container, false);
        edt_trip_departure_date = (EditText) view.findViewById(R.id.edt_trip_departure_date);
        edt_trip_number_bus = (EditText) view.findViewById(R.id.edt_trip_number_bus);
        edt_trip_departure_date.setText(getCurrentFormattedDate());
        edt_trip_departure_date.setOnClickListener(this);

        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_confirm_trip = (Button) view.findViewById(R.id.btn_confirm_trip);

        btn_confirm_trip.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);

        return view;
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            edt_trip_departure_date.setText(getCurrentFormattedDate());
        }
    };

    private String getCurrentFormattedDate(){
        return calendar.get(Calendar.YEAR) +"-"+ (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edt_trip_departure_date:
                new DatePickerDialog(getActivity(),
                        date,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH))
                        .show();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edt_trip_number_bus.getWindowToken(), 0);
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_confirm_trip:
                if (!edt_trip_number_bus.getText().toString().isEmpty()) {
                    InputMethodManager u = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    u.hideSoftInputFromWindow(edt_trip_number_bus.getWindowToken(), 0);
                    confirmDialog();
                } else
                    Toast.makeText(getActivity(), "El número del camión esta vacio", Toast.LENGTH_SHORT).show();
                break;
        }

    }

    private void confirmDialog(){
        final String busNumber = edt_trip_number_bus.getText().toString();
        final String departureDate = edt_trip_departure_date.getText().toString();

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Confirme que los datos sean correctos")
        .setMessage("Numero de camión: "+ busNumber+ "  con fecha de salida: "+departureDate)
                .setPositiveButton("Si, confirmar salida!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        if (callback != null)
                            callback.OnConfirm(DialogConfirmTrip.this, busNumber, departureDate);
                    }
                })
                .setNegativeButton("No estoy seguro", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.create();
        builder.show();
    }

    public interface ConfirmTrip{
        void OnConfirm(DialogFragment dialogFragment, String numberBus, String departureDate);
    }

}
