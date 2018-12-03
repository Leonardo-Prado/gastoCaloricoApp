package com.ifmg.polardispendium_gastocalorico;

import android.app.DatePickerDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import nucleo.entidades_do_nucleo.AtividadesRealizadas;
import nucleo.listviews_manipuladores.AtualizadorLista;
import nucleo.graficos.DesenharGraficoCaloriasDiarias;
import nucleo.outros_manipuladores.DialogNovaAtividade;
import nucleo.entidades_do_nucleo.Usuario;
import objetos_auxiliares.ManipuladorDataTempo;

public class FragmentInicio extends Fragment {

    private EditText edData;
    private ImageView imageViewGrafico;
    private Usuario usuario;
    private WindowManager windowManager;
    private View graficoContainer;
    private boolean escondido = false;
    private View layoutAddNovaAtiv;
    private int listViewItemPosition;
    private boolean minItens = false;
    public FragmentInicio() {
    }

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
            edData = inflate.findViewById(R.id.edData);
            final View graficoConteiner = inflate.findViewById(R.id.graficoContainer);
            setGraficoContainer(graficoConteiner);
            edData.setText(dataTempo.getDataString());
            ListView lvAtividadesDoDia = inflate.findViewById(R.id.lvListaAtividadesDoDia);
            lvAtividadesDoDia.setSmoothScrollbarEnabled(true);
            lvAtividadesDoDia.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(final AbsListView absListView, int i) {
                    try{
                        if(getActivity().getResources().getConfiguration().orientation==Configuration.ORIENTATION_PORTRAIT&&isMinItens()){
                            final TranslateAnimation[] animation = new TranslateAnimation[1];
                            if(i==SCROLL_STATE_TOUCH_SCROLL){
                                animation[0] = new TranslateAnimation(0,0,0,-getGraficoContainer().getHeight());
                                animation[0].setDuration(250);
                                animation[0].setFillAfter(true);
                                getGraficoContainer().startAnimation(animation[0]);
                                getGraficoContainer().setVisibility(View.GONE);
                                layoutAddNovaAtiv.setVisibility(View.GONE);
                            }
                            else  {
                                final Handler handler = new Handler();
                                final Runnable r = new Runnable()
                                {
                                    public void run()
                                    {
                                        if(!(getGraficoContainer().getVisibility()==View.VISIBLE)){
                                            animation[0] = new TranslateAnimation(0,0,-getGraficoContainer().getHeight(),0);
                                            animation[0].setDuration(250);
                                            animation[0].setFillAfter(true);
                                            getGraficoContainer().startAnimation(animation[0]);
                                            absListView.smoothScrollToPosition(listViewItemPosition+1);
                                            Toast.makeText(getContext(),"posicao"+listViewItemPosition,Toast.LENGTH_SHORT);
                                            getGraficoContainer().setVisibility(View.VISIBLE);
                                            layoutAddNovaAtiv.setVisibility(View.VISIBLE);
                                        }
                                    }
                                };
                                handler.postDelayed(r,10000);

                            }
                        }
                    }catch (Exception e){
                        Log.e("erro ao movimentar",e.getMessage());
                    }
                }

                @Override
                public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                    setListViewItemPosition(i+i1);
                    if(i2>2)
                        setMinItens(true);
                    else
                        setMinItens(false);
                }
            });
            layoutAddNovaAtiv = inflate.findViewById(R.id.layoutAddNovaAtiv);
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
            FloatingActionButton fabAdicionar = inflate.findViewById(R.id.fabAdicionar);
            imageViewGrafico = inflate.findViewById(R.id.ivGrafico);
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

    private Usuario getUsuario() {
        return usuario;
    }

    private void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    private WindowManager getWindowManager() {
        return windowManager;
    }

    private void setWindowManager(WindowManager windowManager) {
        this.windowManager = windowManager;
    }

    public View getGraficoContainer() {
        return graficoContainer;
    }

    private void setGraficoContainer(View graficoContainer) {
        this.graficoContainer = graficoContainer;
    }

    public boolean isEscondido() {
        return escondido;
    }

    public void setEscondido(boolean escondido) {
        this.escondido = escondido;
    }

    public int getListViewItemPosition() {
        return listViewItemPosition;
    }

    public void setListViewItemPosition(int listViewItemPosition) {
        this.listViewItemPosition = listViewItemPosition;
    }

    public boolean isMinItens() {
        return minItens;
    }

    public void setMinItens(boolean minItens) {
        this.minItens = minItens;
    }
}