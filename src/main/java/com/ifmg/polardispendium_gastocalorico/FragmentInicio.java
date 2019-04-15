package com.ifmg.polardispendium_gastocalorico;

import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import listeners.DialogNovaAtividadeInterface;
import nucleo.entidades_do_nucleo.AtividadesRealizadas;
import nucleo.entidades_do_nucleo.UserPreferences;
import nucleo.listviews_manipuladores.AtualizadorLista;
import nucleo.graficos.DesenharGraficoCaloriasDiarias;
import nucleo.outros_manipuladores.DialogNovaAtividade;
import nucleo.entidades_do_nucleo.Usuario;
import objetos_auxiliares.Estaticos;
import objetos_auxiliares.ManipuladorDataTempo;

public class FragmentInicio extends Fragment {
    static int TEMPOANIME = 400;

    private EditText edData;
    Resources res;
    private ImageView imageViewGrafico;
    private Usuario usuario;
    private WindowManager windowManager;
    private View graficoContainer;
    private View layoutAddNovaAtiv;
    private boolean minItens = false;
    boolean executouScroll = false;
    TimerTask timerTask;
    Timer timer;
    private boolean testou = false;
    private MyGestureListener gestureListener;
    private GestureDetector detector;
    private boolean executouTarefa = true;
    private Runnable voltaGrafico;
    private int tempoGrafico;
    private int rolamentoSens;
    private UserPreferences userPreferences;
    private Long dataRestore = null;
    private boolean encerrouScroll = false;
    private boolean scrollState = true;
    private boolean escondido = false;
    ListView lvAtividadesDoDia;
    RecyclerView rvAtividades;
    Handler h;
    CoordinatorLayout.LayoutParams layoutParamsInicial;
    FloatingActionButton fabAdicionar;
    View inflate;

    public FragmentInicio() {
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
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
        res = this.getResources();
        this.setUsuario(getUsuario());
        if(getUsuario()!=null) {
            userPreferences = new UserPreferences(getContext(), getUsuario());
            setRolamentoSens(userPreferences.getRolamentoSensibilidade());
            setTempoGrafico(userPreferences.getEscondeGraficoTempo());
        }
        try {
            inflate = inflater.inflate(R.layout.fragment_inicio, container, false);
            fabAdicionar = inflate.findViewById(R.id.fabAdicionar);
            dataTempo = new ManipuladorDataTempo(new Date());
            edData = inflate.findViewById(R.id.edData);
            final View graficoConteiner = inflate.findViewById(R.id.graficoContainer);
            setGraficoContainer(graficoConteiner);
            edData.setText(ManipuladorDataTempo.dataIntToDataString(Estaticos.getFragmentInicioData()));
            gestureListener = new MyGestureListener();
            detector = new GestureDetector(getContext(),gestureListener);
            rvAtividades = inflate.findViewById(R.id.rvAtividades);
            final Handler handler = new Handler();
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
                                Estaticos.setFragmentInicioData(ManipuladorDataTempo.dataStringToDataInt(edData.getText().toString()));
                                List<AtividadesRealizadas> atividadesRealizadas = TelaPrincipal.buscarAtividadesRealizadas(ManipuladorDataTempo.dataStringToDataInt(edData.getText().toString()), getUsuario(),view.getContext());
                                new AtualizadorLista(getContext(),ManipuladorDataTempo.dataStringToDataInt(edData.getText().toString()), getUsuario(),atividadesRealizadas,inflate);

                            } catch (Exception e) {
                                Log.e("erro ao pegar data", e.getMessage());
                            }
                        }
                    }, ano, mes, dia);
                    datePickerDialog.setTitle(res.getString(R.string.fragment_inicio_dialog_datepicker_titulo));
                    datePickerDialog.show();
                }
            });
            //define variaveis
            imageViewGrafico = inflate.findViewById(R.id.ivGrafico);
            edData.setText(ManipuladorDataTempo.dataIntToDataString(Estaticos.getFragmentInicioData()));//Passa a data atual para o edittext edData
            List<AtividadesRealizadas> atividadesRealizadas = TelaPrincipal.buscarAtividadesRealizadas(Estaticos.getFragmentInicioData(), getUsuario(),getContext()); //busca no banco de dados as atividades realizadas na data marcada e cria uma lista de atividades realizadas atraves do metodo
            if (atividadesRealizadas.size() > 0) {//Se existir atividades realizadas para a data passa as mesmas para o listview
                new AtualizadorLista(getContext(),Estaticos.getFragmentInicioData(), getUsuario(),atividadesRealizadas,inflate);//chama metodo para criar as listas de atividades realizadas
                new DesenharGraficoCaloriasDiarias(getContext(),imageViewGrafico,graficoConteiner,usuario, getWindowManager());
            }
            edData.setText(ManipuladorDataTempo.dataIntToDataString(Estaticos.getFragmentInicioData()));
            edData.clearFocus();
            edData.setInputType(InputType.TYPE_NULL);//desabilita o teclado do edData


            //listeners do botão adicionar atividade
            fabAdicionar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        DialogNovaAtividade novaAtividade;
                        novaAtividade = new DialogNovaAtividade(getContext(), getUsuario(),ManipuladorDataTempo.dataStringToDataInt(edData.getText().toString()),inflate);//chama metodo para criar dialog para criar nova atividade
                        novaAtividade.adicionarObservador(new DialogNovaAtividadeInterface() {
                            @Override
                            public void notificarConcluir(DialogNovaAtividade dialogNovaAtividade) {
                            }
                        });
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });
            imageViewGrafico.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() { new DesenharGraficoCaloriasDiarias(getContext(),imageViewGrafico,graficoConteiner, getUsuario(), getWindowManager()); }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        // Inflate the layout for this fragment
        return inflate;

    }

   /* private void mostraGrafico() {
        TranslateAnimation animation;
        if(!(getGraficoContainer().getVisibility()==View.VISIBLE)) {
            animation = new TranslateAnimation(0, 0, -getGraficoContainer().getHeight(), 0);
            animation.setDuration(TEMPOANIME);
            animation.setFillAfter(true);
            fabEscondeGrafico.startAnimation(animation);
            lvAtividadesDoDia.startAnimation(animation);
            layoutAddNovaAtiv.startAnimation(animation);
            getGraficoContainer().startAnimation(animation);
            Runnable runnableAnime = new Runnable() {
                @Override
                public void run() {
                    getGraficoContainer().setVisibility(View.VISIBLE);
                    layoutAddNovaAtiv.setVisibility(View.VISIBLE);
                    setEscondido(false);
                    CoordinatorLayout.LayoutParams layoutParams = layoutParamsInicial;
                    layoutParams.gravity = Gravity.CENTER;
                    layoutParams.setAnchorId(R.id.graficoContainer);
                    layoutParams.anchorGravity = Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
                    fabEscondeGrafico.setLayoutParams(layoutParamsInicial);
                    fabEscondeGrafico.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_seta_acima));
                    fabEscondeGrafico.setBackgroundTintList(ColorStateList.valueOf(res.getColor(R.color.colorGraficoBackground)));

                }
            };
            Handler handlerAnimacao = new Handler();
            handlerAnimacao.postDelayed(runnableAnime, TEMPOANIME);
        }
    }

    private void escondeGrafico() {
        TranslateAnimation animation;
        if(getGraficoContainer().getVisibility()!=View.GONE) {
            animation = new TranslateAnimation(0, 0, 0, -getGraficoContainer().getHeight());
            animation.setDuration(TEMPOANIME);
            animation.setFillBefore(true);
            getGraficoContainer().startAnimation(animation);
            fabEscondeGrafico.startAnimation(animation);
            layoutAddNovaAtiv.startAnimation(animation);
            lvAtividadesDoDia.startAnimation(animation);

            Runnable runnableAnime = new Runnable() {
                @Override
                public void run() {
                    getGraficoContainer().setVisibility(View.GONE);
                    layoutAddNovaAtiv.setVisibility(View.GONE);
                    CoordinatorLayout.LayoutParams layoutParams = layoutParamsInicial;
                    layoutParams.setAnchorId(View.NO_ID);
                    layoutParams.gravity = Gravity.TOP|Gravity.CENTER_HORIZONTAL;
                    fabEscondeGrafico.setLayoutParams(layoutParams);
                    fabEscondeGrafico.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_seta_abaixo));
                    fabEscondeGrafico.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    setEscondido(true);
                }
            };
            Handler handlerAnimacao = new Handler();
            handlerAnimacao.postDelayed(runnableAnime, TEMPOANIME - 100);
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

    }

   View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            try {
                if(getGraficoContainer().getVisibility()==View.GONE&&isEncerrouScroll()){
                    executouScroll = gestureListener.isTocandoTela();
                    testou = false;
                        if ((gestureListener.isDoubletoque() || gestureListener.getDistanciaYScrool() < -getRolamentoSens())) {
                            Handler handler = new Handler();
                            handler.post(getVoltaGrafico());
                            gestureListener.setDoubletoque(false);
                            gestureListener.setDistanciaYScrool(0);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    setScrollState(true);
                                }
                            },500);
                            setEncerrouScroll(false);
                            Log.i("encerrou scroll", "false");
                        }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
            return detector.onTouchEvent(event);
        }
    };*/

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

    public boolean isMinItens() {
        return minItens;
    }

    public void setMinItens(boolean minItens) {
        this.minItens = minItens;
    }

    public Runnable getVoltaGrafico() {
        return voltaGrafico;
    }

    public void setVoltaGrafico(Runnable voltaGrafico) {
        this.voltaGrafico = voltaGrafico;
    }

    public int getTempoGrafico() {
        return tempoGrafico;
    }

    public void setTempoGrafico(int tempoGrafico) {
        this.tempoGrafico = tempoGrafico;
    }

    public int getRolamentoSens() {
        return rolamentoSens;
    }

    public void setRolamentoSens(int rolamentoSens) {
        this.rolamentoSens = rolamentoSens;
    }

    public Long getDataRestore() {
        return dataRestore;
    }

    public void setDataRestore(Long dataRestore) {
        this.dataRestore = dataRestore;
    }

    public boolean isEncerrouScroll() {
        return encerrouScroll;
    }

    public void setEncerrouScroll(boolean encerrouScroll) {
        this.encerrouScroll = encerrouScroll;
    }

    public boolean getScrollState() {
        return scrollState;
    }

    public void setScrollState(boolean scrollState) {
        this.scrollState = scrollState;
    }

    public boolean isEscondido() {
        return escondido;
    }

    public void setEscondido(boolean escondido) {
        this.escondido = escondido;
    }
}

class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
    private boolean longPress = false;
    private boolean tocandoTela = true;
    private boolean doubletoque = false;
    private int distanciaYScrool;

    public MyGestureListener() {
        setTocandoTela(false);
    }

    @Override
    public boolean onDown(MotionEvent event) {
        Log.d("TAG","onDown: ");
        setTocandoTela(true);
        setLongPress(false);
        return true;
    }


    @Override
    public void onLongPress(MotionEvent e) {
        Log.i("TAG", "onLongPress: ");
        setLongPress(true);
        setTocandoTela(true);
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Log.i("TAG", "onDoubleTap: ");
        setDoubletoque(true);
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.i("TAG", "onScroll: " + "distancia = "+distanceY);
        if(isLongPress())
            setDistanciaYScrool(0);
        else
            setDistanciaYScrool((int)distanceY);

        return super.onScroll(e1, e2, distanceX, distanceY);
    }

    public boolean isTocandoTela() {
        return tocandoTela;
    }

    public void setTocandoTela(boolean tocandoTela) {
        this.tocandoTela = tocandoTela;
    }

    public boolean isDoubletoque() {
        return doubletoque;
    }

    public void setDoubletoque(boolean doubletoque) {
        this.doubletoque = doubletoque;
    }

    public int getDistanciaYScrool() {
        return distanciaYScrool;
    }

    public void setDistanciaYScrool(int distanciaYScrool) {
        this.distanciaYScrool = distanciaYScrool;
    }

    public boolean isLongPress() {
        return longPress;
    }

    public void setLongPress(boolean longPress) {
        this.longPress = longPress;
    }
}