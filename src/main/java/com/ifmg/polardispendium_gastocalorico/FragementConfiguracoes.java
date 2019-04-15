package com.ifmg.polardispendium_gastocalorico;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import componentes_customizados.SeekBarComTexto;
import nucleo.entidades_do_nucleo.UserPreferences;
import nucleo.entidades_do_nucleo.Usuario;



public class FragementConfiguracoes extends Fragment {

    private UserPreferences preferences;
    private Usuario usuario;
    private WindowManager windowManager;
    ImageView ivTamanhoFonte;
    ImageView ivTamanhoPontos;
    public FragementConfiguracoes() {
        // Required empty public constructor
    }

    public static FragementConfiguracoes newInstance(Usuario usuario, WindowManager windowManager) {
        FragementConfiguracoes fragment = new FragementConfiguracoes();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.setUsuario(usuario);
        fragment.setWindowManager(windowManager);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragement_configuracoes, container, false);
        preferences = new UserPreferences(getContext(),getUsuario());
        /*final SeekBarComTexto sbSenseRolamento = (SeekBarComTexto) view.findViewById(R.id.sbSenseRolamento);
        final SeekBarComTexto sbTempoOculta = (SeekBarComTexto) view.findViewById(R.id.sbTempOcultGrafico);*/
        final SeekBar sbTamanhoFonte = view.findViewById(R.id.sbTamanhoFonte);
        final SeekBar sbTamanhoPontos = view.findViewById(R.id.sbTamanhoPontos);
        /*sbSenseRolamento.setOffset(20);
        sbTempoOculta.setOffset(1);*/
        ivTamanhoFonte = view.findViewById(R.id.ivTamanhoFonte);
        ivTamanhoPontos = view.findViewById(R.id.ivTamanhoPontos);
        Button btnRedefinir = view.findViewById(R.id.btnRedefinir);
       /* sbSenseRolamento.setProgress(preferences.getRolamentoSensibilidade()-20);
        sbTempoOculta.setProgress(preferences.getEscondeGraficoTempo()/1000);*/
        sbTamanhoFonte.setProgress(preferences.getGraficoTextoSize()-7);
        sbTamanhoPontos.setProgress(preferences.getGraficoPontosSize()-2);
        ivTamanhoPontos.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                desenharPonto();
                desenharTexto();
            }
        });

        /*sbSenseRolamento.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                preferences.setRolamentoSensibilidade(i+20);
                preferences.atualizarDBPreferencias(getUsuario().getId());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sbTempoOculta.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                preferences.setEscondeGraficoTempo(i*1000);
                preferences.atualizarDBPreferencias(getUsuario().getId());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });*/
        sbTamanhoFonte.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                preferences.setGraficoTextoSize(i+7);
                preferences.atualizarDBPreferencias(getUsuario().getId());
                desenharTexto();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sbTamanhoPontos.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                preferences.setGraficoPontosSize(i+2);
                preferences.atualizarDBPreferencias(getUsuario().getId());
                desenharPonto();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {            }
        });
        btnRedefinir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferences.redefinir();
               /* sbSenseRolamento.setProgress(preferences.getRolamentoSensibilidade()-20);
                sbTempoOculta.setProgress(preferences.getEscondeGraficoTempo()/1000);*/
                sbTamanhoFonte.setProgress(preferences.getGraficoTextoSize()-7);
                sbTamanhoPontos.setProgress(preferences.getGraficoPontosSize()-2);
            }
        });
        return view;
    }

    public void desenharPonto(){
        try {
            int largura = ivTamanhoPontos.getWidth(); // define a largura para ser usado no bitmap, da largura da tela
            int altura = ivTamanhoPontos.getHeight();//pega a altura real do relative layout
            Canvas canvas = criarCanvas(ivTamanhoPontos);
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(this.getResources().getColor(R.color.pontosDoGrafico));
            int tamanho = parametrosPorDensidade()[0];
            canvas.drawCircle(largura/2, altura/2, tamanho, paint);
        }catch (Exception e){
            Log.e("desenhar ponto erro",e.getMessage());
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            this.finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public void desenharTexto(){
        try {
            int largura = ivTamanhoFonte.getWidth(); // define a largura para ser usado no bitmap, da largura da tela
            int altura = ivTamanhoFonte.getHeight();//pega a altura real do relative layout
            Canvas canvas = criarCanvas(ivTamanhoFonte);
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(this.getResources().getColor(R.color.pontosDoGrafico));
            int tamanho = parametrosPorDensidade()[1];
            paint.setTextSize(tamanho);
            canvas.drawText(Integer.toString(tamanho), largura/2-8, altura/2+5, paint);
        }catch (Exception e){
            Log.e("desenhar ponto erro",e.getMessage());
        }
    }
    private Canvas criarCanvas(ImageView imageView) {
        Canvas canvas = new Canvas();
        try {
            Resources res = getContext().getResources();
            int largura = imageView.getWidth(); // define a largura para ser usado no bitmap, da largura da tela
            int altura = imageView.getHeight();//pega a altura real do relative layout
            Bitmap bitmap = Bitmap.createBitmap(largura, altura, Bitmap.Config.ARGB_8888); //gera bitmap
            imageView.setImageBitmap(bitmap);//passa o bitmap para a imageview
            canvas = new Canvas(bitmap); // cria o canvas no bitmap
            canvas.drawColor(res.getColor(R.color.colorGraficoBackground)); //define a cor de preenchimento do canvas
        }catch (Exception e){
            Log.e("desenhar ponto erro",e.getMessage());
        }
        return canvas;
    }

    private int[] parametrosPorDensidade() {
        int [] parametros = {preferences.getGraficoPontosSize(), preferences.getGraficoTextoSize()};
        DisplayMetrics metricas = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metricas);
        switch (metricas.densityDpi){
            case(DisplayMetrics.DENSITY_LOW):
                parametros[0] = preferences.getGraficoPontosSize();
                parametros[1] = preferences.getGraficoTextoSize();
                break;
            case(DisplayMetrics.DENSITY_HIGH):
                parametros[0] = 3* preferences.getGraficoPontosSize() - 2;
                parametros[1] = preferences.getGraficoTextoSize()+6;
                break;
            case(DisplayMetrics.DENSITY_MEDIUM):
                parametros[0] = 2* preferences.getGraficoPontosSize() - 2;
                parametros[1] = preferences.getGraficoTextoSize()+3;
                break;
            case(DisplayMetrics.DENSITY_XHIGH):
                parametros[0] = 4* preferences.getGraficoPontosSize() -2;
                parametros[1] = preferences.getGraficoTextoSize()+12;
                break;
            case(DisplayMetrics.DENSITY_XXHIGH):
                parametros[0] = 6* preferences.getGraficoPontosSize();
                parametros[1] = preferences.getGraficoTextoSize()+18;
                break;
            case(DisplayMetrics.DENSITY_XXXHIGH):
                parametros[0] = 7* preferences.getGraficoPontosSize();
                parametros[1] = preferences.getGraficoTextoSize()+24;
                break;
            default:
                parametros[0] = 2* preferences.getGraficoPontosSize();
                parametros[1] = preferences.getGraficoTextoSize()+3;
                break;

        }
        return parametros;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public UserPreferences getPreferences() {
        return preferences;
    }

    public void setPreferences(UserPreferences preferences) {
        this.preferences = preferences;
    }

    public WindowManager getWindowManager() {
        return windowManager;
    }

    public void setWindowManager(WindowManager windowManager) {
        this.windowManager = windowManager;
    }

   /* public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }*/
}
