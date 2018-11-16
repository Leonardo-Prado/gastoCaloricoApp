package com.ifmg.polardispendium_gastocalorico;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import database.DBGeneric;
import nucleo.entidades_do_nucleo.AtividadesRealizadas;
import nucleo.listviews_manipuladores.AtualizadorLista;
import nucleo.graficos.DesenharGraficoCaloriasDiarias;
import nucleo.outros_manipuladores.DialogNovaAtividade;
import nucleo.entidades_do_nucleo.Usuario;
import objetos_auxiliares.ManipuladorDataTempo;

public class FragmentInicio extends Fragment {

    private Canvas canvasGrafico;
    private Bitmap bitmapGrafico;
    private EditText edData;
    private ImageButton ibtnAdicionar;
    private ImageView imageViewGrafico;
    private DBGeneric dbGeneric;
    private Usuario usuario;
    private FloatingActionButton fabAdicionar;
    private WindowManager windowManager;

    public static FragmentInicio newInstance(Usuario usuario,WindowManager windowManager) {
        FragmentInicio fragment = new FragmentInicio();
        fragment.setUsuario(usuario);
        fragment.setWindowManager(windowManager);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ManipuladorDataTempo dataTempo;
        this.setUsuario(getUsuario());
        final View inflate = inflater.inflate(R.layout.fragment_inicio, container, false);
        try {
            dataTempo = new ManipuladorDataTempo(new Date());
            edData = (EditText)inflate.findViewById(R.id.edData);
            final View graficoConteiner = inflate.findViewById(R.id.graficoContainer);
            edData.setText(dataTempo.getDataString());
            edData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    Calendar calendario = Calendar.getInstance();
                    int dia = calendario.get(Calendar.DAY_OF_MONTH);
                    int mes = calendario.get(Calendar.MONTH);
                    int ano = calendario.get(Calendar.YEAR);
                    DatePickerDialog datePickerDialog;
                    datePickerDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int selectedAno, int selectedMes, int selectedDia) {
                            try {
                                selectedMes = selectedMes + 1;
                                edData.setText(selectedDia + "-" + selectedMes + "-" + selectedAno);
                                List<AtividadesRealizadas> atividadesRealizadas = TelaPrincipal.buscarAtividadesRealizadas(ManipuladorDataTempo.dataStringToDataInt(edData.getText().toString()), getUsuario(),view.getContext());
                                new AtualizadorLista(getContext(),ManipuladorDataTempo.dataStringToDataInt(edData.getText().toString()), getUsuario(),atividadesRealizadas,inflate);
                            } catch (Exception e) {
                                Log.e("erro ao pegar data", e.getMessage());
                            }
                        }
                    }, ano, mes, dia);
                    datePickerDialog.setTitle("Escolha o dia");
                    datePickerDialog.show();
                }
            });
            //define variaveis
            fabAdicionar =  inflate.findViewById(R.id.fabAdicionar);
            imageViewGrafico =(ImageView) inflate.findViewById(R.id.ivGrafico);
            edData.setText(dataTempo.getDataString());//Passa a data atual para o edittext edData
            List<AtividadesRealizadas> atividadesRealizadas = TelaPrincipal.buscarAtividadesRealizadas(dataTempo.getDataInt(), getUsuario(),getContext()); //busca no banco de dados as atividades realizadas na data marcada e cria uma lista de atividades realizadas atraves do metodo
            if (atividadesRealizadas.size() > 0) {//Se existir atividades realizadas para a data passa as mesmas para o listview
                new AtualizadorLista(getContext(),dataTempo.getDataInt(), getUsuario(),atividadesRealizadas,inflate);//chama metodo para criar as listas de atividades realizadas
                new DesenharGraficoCaloriasDiarias(getContext(),imageViewGrafico,graficoConteiner,usuario, getWindowManager());
            }
            edData.setText(new ManipuladorDataTempo(new Date()).getDataString());
            edData.clearFocus();
            edData.setInputType(InputType.TYPE_NULL);//desabilita o teclado do edData


            //listeners do botão adicionar atividade
            fabAdicionar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        new DialogNovaAtividade(getContext(), getUsuario(),ManipuladorDataTempo.dataStringToDataInt(edData.getText().toString()),inflate);//chama metodo para criar dialog para criar nova atividade
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });

            imageViewGrafico.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() { new DesenharGraficoCaloriasDiarias(getContext(),imageViewGrafico,graficoConteiner, getUsuario(), getWindowManager()); }
            });

        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Inflate the layout for this fragment
        return inflate;

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
}
