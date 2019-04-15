package nucleo.graficos;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.ifmg.polardispendium_gastocalorico.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import database.DBGeneric;
import nucleo.entidades_do_nucleo.UserPreferences;
import nucleo.entidades_do_nucleo.Usuario;
import objetos_auxiliares.FormatNum;
import objetos_auxiliares.ManipuladorDataTempo;

public class DesenharGraficoCaloriasDiarias {
    private long hoje;
    private Context context;
    private Bitmap bitmapGrafico;
    private Canvas canvasGrafico;
    private ImageView imageViewGrafico;
    private View graficoContainer;
    private DBGeneric dbGeneric;
    private WindowManager windowManager;
    private Usuario usuario;
    private long primeiraData;
    private GestureDetector detector;
    private MyGestureListener gestureListener;
    private int tamanhoPontos;
    private int tamanhoTexto;
    private int tempoGrafico;
    private int rolamentoSens;
    private int numPontos;
    private UserPreferences userPreferences;
    List<RectF> retangulos;

    public DesenharGraficoCaloriasDiarias(Context context, ImageView imageViewGrafico, View graficoContainer, Usuario usuario, WindowManager windowManager) {
        try {
            this.context = context;
            this.imageViewGrafico = imageViewGrafico;
            this.graficoContainer = graficoContainer;
            this.usuario = usuario;
            this.setWindowManager(windowManager);
            ManipuladorDataTempo dataTempo = new ManipuladorDataTempo(new Date());
            setPrimeiraData(dataTempo.getDataInt());
            gestureListener = new MyGestureListener();
            detector = new GestureDetector(getContext(),gestureListener);
            setHoje(dataTempo.getDataInt());
            if (context != null) {
                userPreferences = new UserPreferences(getContext(),getUsuario());
                setTamanhoPontos(userPreferences.getGraficoPontosSize());
                setTamanhoTexto(userPreferences.getGraficoTextoSize());
                setRolamentoSens(userPreferences.getRolamentoSensibilidade());
                setTempoGrafico(userPreferences.getEscondeGraficoTempo());
                dbGeneric = new DBGeneric(getContext());
                desenharGrafico();
            }
            retangulos = new ArrayList<>();
            getImageViewGrafico().setOnTouchListener(touchListener);
        }
        catch (Exception e){
            Log.e("Erro ao construir",e.getMessage());
        }
    }

    private void desenharGrafico() {
        try {
            retangulos = new ArrayList<>();
            View graficoConteiner = getGraficoContainer();//pega o relative layout que contem o canvas para pegar a altura
            Resources res = getContext().getResources();
            int largura = graficoConteiner.getWidth(); // define a largura para ser usado no bitmap, da largura da tela
            int altura = graficoConteiner.getHeight();//pega a altura real do relative layout
            setBitmapGrafico(Bitmap.createBitmap(largura, altura, Bitmap.Config.ARGB_8888)); //gera bitmap
            getImageViewGrafico().setImageBitmap(getBitmapGrafico());//passa o bitmap para a imageview
            setCanvasGrafico(new Canvas(getBitmapGrafico())); // cria o canvas no bitmap
            getCanvasGrafico().drawColor(res.getColor( R.color.colorGraficoBackground)); //define a cor de preenchimento do canvas
            Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.back_grafico);
            Paint paintAlpha = new Paint();
            paintAlpha.setAntiAlias(true);
            paintAlpha.setAlpha(15);
            getCanvasGrafico().drawBitmap(bitmap,null,new Rect(0,0,largura,altura),paintAlpha);
            Long dia = ManipuladorDataTempo.tempoStringToTempoInt("24:00");
            List<List<String>> s = getDbGeneric().buscar("GastoEnergetico", new String[]{"Data", "GastoCalorico"}, "Data >= ? and Data <= ? and _idUsuario = ?", new String[]{Long.toString(primeiraData - dia * 5), Long.toString(primeiraData), Integer.toString(getUsuario().getId())}, "Data ASC");
            int dias = s.size();
            setNumPontos(dias);
            final Point ponto = new Point();
            int numeroX = largura / (dias + 1);
            gestureListener.setDivisor(numeroX);
            if (dias > 0) {
                final int[] parametros = parametrosPorDensidade();
                int i = 0;
                List<List<String>> menor = getDbGeneric().buscar("1","GastoEnergetico", new String[]{ "GastoCalorico"}, "_idUsuario = ?", new String[]{Integer.toString(getUsuario().getId())}, "GastoCalorico ASC");
                List<List<String>> maior = getDbGeneric().buscar("1","GastoEnergetico", new String[]{"GastoCalorico"}, "_idUsuario = ?", new String[]{Integer.toString(getUsuario().getId())}, "GastoCalorico DESC");
                int maiorCaloria = (int)Double.parseDouble(maior.get(0).get(0));
                int menorCaloria = (int)Double.parseDouble(menor.get(0).get(0));
                /*while(i < dias){
                    try {
                        if (i > 0) {
                            if (Double.parseDouble(s.get(i).get(1)) > maiorCaloria)
                                maiorCaloria =(int) Double.parseDouble(s.get(i).get(1));
                            if (Double.parseDouble(s.get(i).get(1)) < menorCaloria)
                                menorCaloria = (int) Double.parseDouble(s.get(i).get(1));
                        } else {
                            maiorCaloria = (int) Double.parseDouble(s.get(i).get(1));
                            menorCaloria = (int) Double.parseDouble(s.get(i).get(1));
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    i++;
                }*/
                i = 0;
                int alt = altura-14*parametros[0];//desloca o grafico para cima quanto maior o multiplicador
                while (i < dias) {
                    int x = ponto.x;
                    int y = ponto.y;
                    ponto.x = (i + 1) * numeroX;
                    Float f = Float.parseFloat(s.get(i).get(1));
                    if((maiorCaloria-menorCaloria)>0)
                        f = (f-(menorCaloria)) / (maiorCaloria-menorCaloria);
                    else
                        f = 1.0f;
                    ponto.y = (int)(alt -  f * alt)+7*parametros[0];
                    final Paint paint = new Paint();
                    if (i > 0) {
                        paint.setColor(Color.GREEN);
                        paint.setStrokeWidth(1f);
                        paint.setAntiAlias(true);
                        getCanvasGrafico().drawLine(x, y, ponto.x, ponto.y, paint);
                    }
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(res.getColor(R.color.pontosDoGrafico));
                    paint.setTextSize(parametros[1]);
                    getCanvasGrafico().drawCircle(ponto.x, ponto.y, parametros[0], paint);
                    paint.setAlpha(0);
                    RectF rectF = new RectF(ponto.x-2*parametros[0],ponto.y-2*parametros[0],ponto.x+2*parametros[0],ponto.y+2*parametros[0]);
                    retangulos.add(rectF);
                    paint.setAlpha(255);
                    paint.setColor(Color.WHITE);
                    getCanvasGrafico().drawText(Integer.toString((int)FormatNum.casasDecimais(Double.parseDouble(s.get(i).get(1)), 1)), ponto.x -(parametros[0]+parametros[0]/2), ponto.y - (parametros[0]+parametros[0]/2), paint);
                    getCanvasGrafico().drawText(ManipuladorDataTempo.dataIntToDataString(Long.parseLong(s.get(i).get(0)),"dd-MM"), ponto.x-(parametros[0]+parametros[0]/2), altura - 24 , paint);
                    if (i > 0) {
                        paint.setColor(Color.WHITE);
                        paint.setStrokeWidth(1f);
                        paint.setAntiAlias(true);
                        getCanvasGrafico().drawLine(x, y, ponto.x, ponto.y, paint);
                    }
                    if(i==s.size()-1&&Long.parseLong(s.get(i).get(0))== getHoje()) {
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setColor(res.getColor(R.color.pontosDoGrafico));
                        getCanvasGrafico().drawCircle(ponto.x, ponto.y, (parametros[0]/2) + parametros[0], paint);
                    }
                    i++;
                }
            }
            Paint paint = new Paint();
            paint.setTextSize((int)(parametrosPorDensidade()[1]*1.1));
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(res.getColor(R.color.colorAccent));
            getCanvasGrafico().drawText(res.getString(R.string.grafico_gasto_calorico_titulo),10,20,paint);

        } catch (Exception e) {
            Log.e("erro ao desenhar",e.getMessage());

        }

    }

    private int[] parametrosPorDensidade() {
        int [] parametros = {getTamanhoPontos(), getTamanhoTexto()};
        DisplayMetrics metricas = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metricas);
        switch (metricas.densityDpi){
            case(DisplayMetrics.DENSITY_LOW):
                parametros[0] = getTamanhoPontos();
                parametros[1] = getTamanhoTexto();
                break;
            case(DisplayMetrics.DENSITY_HIGH):
                parametros[0] = 3* getTamanhoPontos() - 2;
                parametros[1] = getTamanhoTexto()+6;
                break;
            case(DisplayMetrics.DENSITY_MEDIUM):
                parametros[0] = 2* getTamanhoPontos() - 2;
                parametros[1] = getTamanhoTexto()+3;
                break;
            case(DisplayMetrics.DENSITY_XHIGH):
                parametros[0] = 4* getTamanhoPontos() -2;
                parametros[1] = getTamanhoTexto()+12;
                break;
            case(DisplayMetrics.DENSITY_XXHIGH):
                parametros[0] = 6* getTamanhoPontos();
                parametros[1] = getTamanhoTexto()+18;
                break;
            case(DisplayMetrics.DENSITY_XXXHIGH):
                parametros[0] = 7* getTamanhoPontos();
                parametros[1] = getTamanhoTexto()+24;
                break;
            default:
                parametros[0] = 2* getTamanhoPontos();
                parametros[1] = getTamanhoTexto()+3;
                break;

        }
        return parametros;
    }

    public void girarGrafico(int deslocamento) {
        try {
            setPrimeiraData(getPrimeiraData()-deslocamento*ManipuladorDataTempo.tempoStringToTempoInt("24:00"));
            desenharGrafico();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap getBitmapGrafico() {
        return bitmapGrafico;
    }

    public void setBitmapGrafico(Bitmap bitmapGrafico) {
        this.bitmapGrafico = bitmapGrafico;
    }

    public Canvas getCanvasGrafico() {
        return canvasGrafico;
    }

    public void setCanvasGrafico(Canvas canvasGrafico) {
        this.canvasGrafico = canvasGrafico;
    }

    public ImageView getImageViewGrafico() {
        return imageViewGrafico;
    }

    public View getGraficoContainer() {
        return graficoContainer;
    }

    public void setGraficoContainer(View graficoContainer) {
        this.graficoContainer = graficoContainer;
    }
    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            try {
                girarGrafico(gestureListener.getDeslocamentos());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return detector.onTouchEvent(event);
        }
    };

    public DBGeneric getDbGeneric() {
        return dbGeneric;
    }

    public void setDbGeneric(DBGeneric dbGeneric) {
        this.dbGeneric = dbGeneric;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public long getPrimeiraData() {
        return primeiraData;
    }

    public void setPrimeiraData(long primeiraData) {
        this.primeiraData = primeiraData;
    }

    public void setNumPontos(int numPontos) {
        this.numPontos = numPontos;
    }

    public long getHoje() {
        return hoje;
    }

    public void setHoje(long hoje) {
        this.hoje = hoje;
    }

    public WindowManager getWindowManager() {
        return windowManager;
    }

    public void setWindowManager(WindowManager windowManager) {
        this.windowManager = windowManager;
    }

    public int getTamanhoPontos() {
        return tamanhoPontos;
    }

    public void setTamanhoPontos(int tamanhoPontos) {
        this.tamanhoPontos = tamanhoPontos;
    }

    public int getTamanhoTexto() {
        return tamanhoTexto;
    }

    public void setTamanhoTexto(int tamanhoTexto) {
        this.tamanhoTexto = tamanhoTexto;
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

    public int getNumPontos() {
        return numPontos;
    }
}

class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
    private int divisor;
    private int deslocamentos;
    private boolean isFling = false;

    public MyGestureListener() {
        divisor = 1;
        deslocamentos = 0;
    }

    @Override
    public boolean onDown(MotionEvent event) {
        Log.d("TAG","onDown: ");
        return true;
    }
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.i("TAG", "onScroll: ");
        deslocamentos = -(int)distanceX/divisor;
        return true;
    }

    public void setDivisor(int divisor) {
        this.divisor = divisor;
    }

    public int getDeslocamentos() {
        return deslocamentos;
    }

    private void setFling(boolean fling) {
        isFling = fling;
    }
}