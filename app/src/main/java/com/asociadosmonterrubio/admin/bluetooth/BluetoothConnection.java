package com.asociadosmonterrubio.admin.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by joseluissanchezcruz on 7/9/17.
 */

public class BluetoothConnection {

    public static BluetoothAdapter bluetoothAdapter;

    public static boolean isBluetoothAvailable(Context context){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // Phone does not support Bluetooth so let the user know and exit.
        if (bluetoothAdapter == null) {
            new AlertDialog.Builder(context)
                    .setTitle("Telefono no compatible")
                    .setMessage("Tu telefono no soporta Bluetooth")
                    .setPositiveButton("Salir", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return false;
        }
        return true;
    }
}
