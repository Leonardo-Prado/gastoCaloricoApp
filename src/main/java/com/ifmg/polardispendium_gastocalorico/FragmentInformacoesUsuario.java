package com.ifmg.polardispendium_gastocalorico;


import android.content.ContentValues;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import database.DBGeneric;
import nucleo.entidades_do_nucleo.Usuario;
import objetos_auxiliares.DialogConstrutor;
import objetos_auxiliares.FormatNum;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentInformacoesUsuario#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentInformacoesUsuario extends Fragment {
    private Usuario usuario;
    private WindowManager windowManager;
    private DBGeneric dbGeneric;
    private double mediaGastoCalorico;
    double imc;


    public FragmentInformacoesUsuario() {
        // Required empty public constructor
    }

    public static FragmentInformacoesUsuario newInstance(Usuario usuario, WindowManager windowManager) {
        FragmentInformacoesUsuario fragment = new FragmentInformacoesUsuario();
        fragment.setUsuario(usuario);
        fragment.setWindowManager(windowManager);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_informacoes_usuario, container, false);
        final TextView tvPesoAtual = inflate.findViewById(R.id.tvPesoAtual);
        TextView tvPesoInicial = inflate.findViewById(R.id.tvPesoInicial);
        final TextView tvIMC = inflate.findViewById(R.id.tvIMC);
        final TextView tvAltura = inflate.findViewById(R.id.tvAltura);
        TextView tvMediaGastoCalorias = inflate.findViewById(R.id.tvMediaGastoCalorias);
        ImageButton ibtnEditarPeso = inflate.findViewById(R.id.ibtnEditPeso);
        ImageButton ibtnEditarAltura = inflate.findViewById(R.id.ibtnEditAltura);
        dbGeneric = new DBGeneric(getContext());
        List<List<String>> lists = dbGeneric.buscar("Peso",new String[]{"Peso"},"_idUsuario = ? AND Inicial = ?",new String[]{Integer.toString(usuario.getId()),"1"});
        tvPesoInicial.setText(tvPesoInicial.getText().toString() + " " +lists.get(0).get(0));
        tvPesoAtual.setText(tvPesoAtual.getText().toString() + " " +Double.toString(FormatNum.casasDecimais(getUsuario().getMassaCorporal(),1)));
        tvAltura.setText(tvAltura.getText().toString()+" "+Double.toString(FormatNum.casasDecimais(usuario.getAltura(),2)) + " m");
        defineImc(tvIMC);
        //gera media de calorias do usuario e passa para o tvgastomedio
        List<List<String>> gastosCaloricos= dbGeneric.buscar("GastoEnergetico",new String[]{"GastoCalorico"},"_idUsuario = ?",new String[]{Integer.toString(getUsuario().getId())});
        for (List<String> list:gastosCaloricos
                ) {
            setMediaGastoCalorico(getMediaGastoCalorico()+Double.parseDouble(list.get(0)));
        }
        setMediaGastoCalorico(getMediaGastoCalorico()/gastosCaloricos.size());
        tvMediaGastoCalorias.setText(tvMediaGastoCalorias.getText().toString() + " "+ Double.toString(FormatNum.casasDecimais(getMediaGastoCalorico(),2)) + " por dia");
        ibtnEditarAltura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = inflater.inflate(R.layout.dialog_editar_valor,null);
                final EditText edAtualizarValor = v.findViewById(R.id.edAtualizarValor);
                Button btnAtualizar = v.findViewById(R.id.btnAtualizar);
                edAtualizarValor.setHint("Atualizar altura...");
                final DialogConstrutor dialogConstrutor = new DialogConstrutor(getContext(),v,"Altere a altura","Modificar valor da altura");
                btnAtualizar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getUsuario().setAltura(Double.parseDouble(edAtualizarValor.getText().toString()));
                        tvAltura.setText(Double.toString(getUsuario().getAltura()));
                        ContentValues values = new ContentValues();
                        values.put("Altura",Double.parseDouble(edAtualizarValor.getText().toString()));
                        dbGeneric.atualizar("Usuarios",values,"_id = ?",new String[]{Integer.toString(getUsuario().getId())});
                        defineImc(tvIMC);
                        dialogConstrutor.fechar();

                    }
                });
            }
        });

        ibtnEditarPeso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = inflater.inflate(R.layout.dialog_editar_valor,null);
                final EditText edAtualizarValor = v.findViewById(R.id.edAtualizarValor);
                Button btnAtualizar = v.findViewById(R.id.btnAtualizar);
                edAtualizarValor.setHint("Atualizar massa corporal...");
                final DialogConstrutor dialogConstrutor = new DialogConstrutor(getContext(),v,"Altere o peso","Modificar valor do peso");
                btnAtualizar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tvPesoAtual.setText(edAtualizarValor.getText().toString());
                        getUsuario().setMassaCorporal(Double.parseDouble(edAtualizarValor.getText().toString()));
                        ContentValues values = new ContentValues();
                        values.put("MassaCorporal",Double.parseDouble(edAtualizarValor.getText().toString()));
                        dbGeneric.atualizar("Usuarios",values,"_id = ?",new String[]{Integer.toString(getUsuario().getId())});
                        defineImc(tvIMC);
                        dialogConstrutor.fechar();
                    }
                });
            }
        });

        return inflate;
    }

    private void defineImc(TextView tvIMC) {
        imc = usuario.getMassaCorporal()/Math.pow(usuario.getAltura(),2);
        tvIMC.setText(getResources().getString(R.string.tv_imc)+" "+Double.toString(FormatNum.casasDecimais(imc,2)) + " kg/m\u2072");
        if(imc>25)
            tvIMC.setTextColor(getResources().getColor(R.color.colorAccent));
        else
            tvIMC.setTextColor(Color.GREEN);
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public WindowManager getWindowManager() {
        return windowManager;
    }

    public void setWindowManager(WindowManager windowManager) {
        this.windowManager = windowManager;
    }

    public double getMediaGastoCalorico() {
        return mediaGastoCalorico;
    }

    public void setMediaGastoCalorico(double mediaGastoCalorico) {
        this.mediaGastoCalorico = mediaGastoCalorico;
    }
}
