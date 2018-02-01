package com.asociadosmonterrubio.admin.activities;


import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.asociadosmonterrubio.admin.R;
import com.asociadosmonterrubio.admin.models.BlackListUser;

import butterknife.BindView;
import butterknife.ButterKnife;


public class BlackListMessageFragment extends DialogFragment {


    private BlackListUser blackListUser;

    public BlackListMessageFragment() {}

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_black_list_message, container, false);
        ButterKnife.bind(this, view);
        String fullName = blackListUser.getNombre().concat(" ").concat(blackListUser.getApellido_Paterno().concat(" ").concat(blackListUser.getApellido_Materno()));
        black_list_full_name.setText(fullName);
        black_list_curp.setText(blackListUser.getCURP());
        black_list_fecha_nac.setText(blackListUser.getFecha_Nacimiento());
        black_list_lugar_nac.setText(blackListUser.getLugar_Nacimiento());
        black_list_motivo.setText(blackListUser.getMotivo());
        black_list_observaciones.setText(blackListUser.getObservaciones());
        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BlackListMessageFragment.this.dismiss();
            }
        });
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return view;
    }

    public void setBlackListUser(BlackListUser blackListUser) {
        this.blackListUser = blackListUser;
    }

    @BindView(R.id.black_list_observaciones) TextView black_list_observaciones;
    @BindView(R.id.black_list_full_name) TextView black_list_full_name;
    @BindView(R.id.black_list_fecha_nac) TextView black_list_fecha_nac;
    @BindView(R.id.black_list_lugar_nac) TextView black_list_lugar_nac;
    @BindView(R.id.black_list_motivo) TextView black_list_motivo;
    @BindView(R.id.black_list_curp) TextView black_list_curp;
    @BindView(R.id.btn_accept) Button btn_accept;

}
