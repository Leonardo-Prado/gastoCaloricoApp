package com.ifmg.polardispendium_gastocalorico;


import android.content.ContentValues;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import database.DBGeneric;
import nucleo.entidades_do_nucleo.Usuario;
import objetos_auxiliares.DialogConstrutor;
import objetos_auxiliares.FormatNum;
import objetos_auxiliares.ManipuladorDataTempo;


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
    Resources res;


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
        res = getResources();
        final TextView tvPesoAtual = inflate.findViewById(R.id.tvPesoAtual);
        TextView tvPesoInicial = inflate.findViewById(R.id.tvPesoInicial);
        final TextView tvIMC = inflate.findViewById(R.id.tvIMC);
        final TextView tvAltura = inflate.findViewById(R.id.tvAltura);
        TextView tvMediaGastoCalorias = inflate.findViewById(R.id.tvMediaGastoCalorias);
        TextView tvMaximoGastoCalorias = inflate.findViewById(R.id.tvMaximoGastoCalorias);
        TextView tvMinimoGastoCalorias = inflate.findViewById(R.id.tvMinimoGastoCalorias);
        ImageButton ibtnEditarPeso = inflate.findViewById(R.id.ibtnEditPeso);
        ImageButton ibtnEditarAltura = inflate.findViewById(R.id.ibtnEditAltura);
        dbGeneric = new DBGeneric(getContext());
        List<List<String>> lists = dbGeneric.buscar("Peso",new String[]{"Peso"},"_idUsuario = ? AND Inicial = ?",new String[]{Integer.toString(usuario.getId()),"1"});
        tvPesoInicial.setText(tvPesoInicial.getText().toString() + " " +FormatNum.casasDecimais(Double.parseDouble(lists.get(0).get(0)),1)+" "+res.getString(R.string.fragment_informacoes_usuarios_tvpeso_unidade));
        tvPesoAtual.setText(tvPesoAtual.getText().toString() + " " + Double.toString(FormatNum.casasDecimais(getUsuario().getMassaCorporal(),1))+" "+res.getString(R.string.fragment_informacoes_usuarios_tvpeso_unidade));
        tvAltura.setText(tvAltura.getText().toString()+" "+Double.toString(FormatNum.casasDecimais(usuario.getAltura(),2)) + " "+res.getString(R.string.fragment_informacoes_usuarios_tvaltura_unidade));
        defineImc(tvIMC);
        //gera media de calorias do usuario e passa para o tvgastomedio
        usuario.setGastoMedio(Double.parseDouble(dbGeneric.buscar("Usuarios",new String[]{"GastoMedio"},"_id = ?",new String[]{Integer.toString(usuario.getId())}).get(0).get(0)));
        usuario.setGastoMaximo(Double.parseDouble(dbGeneric.buscar("Usuarios",new String[]{"GastoMaximo"},"_id = ?",new String[]{Integer.toString(usuario.getId())}).get(0).get(0)));
        usuario.setGastoMinimo(Double.parseDouble(dbGeneric.buscar("Usuarios",new String[]{"GastoMinimo"},"_id = ?",new String[]{Integer.toString(usuario.getId())}).get(0).get(0)));
        tvMediaGastoCalorias.setText(tvMediaGastoCalorias.getText().toString() + " "+ Double.toString(FormatNum.casasDecimais(usuario.getGastoMedio(),2)) +  " "+res.getString(R.string.fragment_informacoes_usuarios_tvgastocaloricomedio_unidade));
        tvMaximoGastoCalorias.setText(tvMaximoGastoCalorias.getText().toString() + " "+ Double.toString(FormatNum.casasDecimais(usuario.getGastoMaximo(),2)));
        tvMinimoGastoCalorias.setText(tvMinimoGastoCalorias.getText().toString() + " "+ Double.toString(FormatNum.casasDecimais(usuario.getGastoMinimo(),2)));
        ibtnEditarAltura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = inflater.inflate(R.layout.dialog_editar_valor,null);
                final EditText edAtualizarValor = v.findViewById(R.id.edAtualizarValor);
                Button btnAtualizar = v.findViewById(R.id.btnAtualizar);
                edAtualizarValor.setHint(res.getString(R.string.fragment_informacoes_usuarios_dialog_editar_altura_edatualizarvalor_hint));
                final DialogConstrutor dialogConstrutor = new DialogConstrutor(getContext(),v,res.getString(R.string.fragment_informacoes_usuarios_dialog_editar_altura_titulo),res.getString(R.string.fragment_informacoes_usuarios_dialog_editar_altura_menssagem));
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
                try{
                    View v = inflater.inflate(R.layout.dialog_editar_valor,null);
                    final EditText edAtualizarValor = v.findViewById(R.id.edAtualizarValor);
                    Button btnAtualizar = v.findViewById(R.id.btnAtualizar);
                    edAtualizarValor.setHint(res.getString(R.string.fragment_informacoes_usuarios_dialog_editar_peso_edatualizarvalor_hint));
                    final DialogConstrutor dialogConstrutor = new DialogConstrutor(getContext(),v,res.getString(R.string.fragment_informacoes_usuarios_dialog_editar_peso_titulo),res.getString(R.string.fragment_informacoes_usuarios_dialog_editar_peso_menssagem));
                    btnAtualizar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try{
                                tvPesoAtual.setText(edAtualizarValor.getText().toString());
                                getUsuario().setMassaCorporal(Double.parseDouble(edAtualizarValor.getText().toString()));
                                ContentValues values = new ContentValues();
                                values.put("MassaCorporal",Double.parseDouble(edAtualizarValor.getText().toString()));
                                if(getUsuario().getPesoMinimo()>=getUsuario().getMassaCorporal()){
                                    usuario.setPesoMinimo(getUsuario().getMassaCorporal());
                                    values.put("PesoMinimo",usuario.getPesoMinimo());
                                }else if(getUsuario().getPesoMaximo()<=getUsuario().getMassaCorporal()){
                                    usuario.setPesoMaximo(usuario.getMassaCorporal());
                                    values.put("PesoMaximo",usuario.getPesoMaximo());
                                }
                                dbGeneric.atualizar("Usuarios",values,"_id = ?",new String[]{Integer.toString(getUsuario().getId())});
                                defineImc(tvIMC);
                                List<List<String>> pesos = dbGeneric.buscar("Peso",new String[]{"_id"},"_idUsuario = ? AND Data = ?",new String[]{Integer.toString(getUsuario().getId()),Long.toString(new ManipuladorDataTempo(new Date()).getDataInt())});
                                values = new ContentValues();
                                values.put("Peso",usuario.getMassaCorporal());
                                dbGeneric.atualizar("Peso",values,"_id = ?",new String[]{pesos.get(0).get(0)});
                                dialogConstrutor.fechar();
                            }catch (Exception e){
                                Log.e("erro ao criar novo peso",e.getMessage());
                            }
                        }
                    });
                }catch (Exception e){
                    Log.e("erro ao criar dialog",e.getMessage());
                }

            }
        });

        return inflate;
    }

    private void defineImc(TextView tvIMC) {
        imc = usuario.getMassaCorporal()/Math.pow(usuario.getAltura(),2);
        tvIMC.setText(getResources().getString(R.string.tv_imc)+" "+Double.toString(FormatNum.casasDecimais(imc,2)) +" "+res.getString(R.string.fragment_informacoes_usuarios_tvimc_unidade) );
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
