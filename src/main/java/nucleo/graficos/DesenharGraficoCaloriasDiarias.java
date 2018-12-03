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
import android.support.v4.content.res.ResourcesCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.ifmg.polardispendium_gastocalorico.R;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import database.DBGeneric;
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
    private int numPontos;
    private int altura;
    private int alturaVelha;
    private boolean escondido = true;

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
                dbGeneric = new DBGeneric(getContext());
                desenharGrafico();
            }
           getImageViewGrafico().setOnTouchListener(touchListener);
        }
        catch (Exception e){
            Log.e("Erro ao construir",e.getMessage());
        }
    }

    private void desenharGrafico() {
        try {
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
            paintAlpha.setAlpha(10);
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
                int maiorCaloria = 0;
                int menorCaloria = 0;
                while(i < dias){
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
                }
                i = 0;
                int alt = altura-12*parametros[0];
                while (i < dias) {
                    int x = ponto.x;
                    int y = ponto.y;
                    ponto.x = (i + 1) * numeroX;
                    Float f = Float.parseFloat(s.get(i).get(1));
                    if((maiorCaloria-menorCaloria)>0)
                        f = (f-(menorCaloria)) / (maiorCaloria-menorCaloria);
                    else
                        f = 1.0f;
                    ponto.y = (int)(alt -  f * alt)+6*parametros[0];
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
                    paint.setColor(Color.WHITE);
                    getCanvasGrafico().drawText(Integer.toString((int)FormatNum.casasDecimais(Double.parseDouble(s.get(i).get(1)), 1)), ponto.x -(parametros[0]+parametros[0]/2), ponto.y - (parametros[0]+parametros[0]/2), paint);
                    getCanvasGrafico().drawText(ManipuladorDataTempo.dataIntToDataString(Long.parseLong(s.get(i).get(0)),"dd-MM"), ponto.x-(parametros[0]+parametros[0]/2), altura - 5 , paint);
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
            paint.setTextSize((int)(parametrosPorDensidade()[1]*1.3));
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(res.getColor(R.color.colorAccent));
            getCanvasGrafico().drawText(res.getString(R.string.grafico_gasto_calorico_titulo),20,30,paint);

        } catch (Exception e) {
            Log.e("erro ao desenhar",e.getMessage());

        }

    }

    private int[] parametrosPorDensidade() {
        int [] parametros = {4, 4 +5};
        DisplayMetrics metricas = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metricas);
        switch (metricas.densityDpi){
            case(DisplayMetrics.DENSITY_LOW):
                parametros[0] = 4;
                parametros[1] = 11;
                break;
            case(DisplayMetrics.DENSITY_HIGH):
                parametros[0] = 3* 4 - 2;
                parametros[1] = 17;
                break;
            case(DisplayMetrics.DENSITY_MEDIUM):
                parametros[0] = 2* 4 - 2;
                parametros[1] = 15;
                break;
            case(DisplayMetrics.DENSITY_XHIGH):
                parametros[0] = 4* 4 -2;
                parametros[1] = 25;
                break;
            case(DisplayMetrics.DENSITY_XXHIGH):
                parametros[0] = 6* 4;
                parametros[1] = 30;
                break;
            case(DisplayMetrics.DENSITY_XXXHIGH):
                parametros[0] = 7* 4;
                parametros[1] = 40;
                break;
            default:
                parametros[0] = 2* 4;
                parametros[1] = 11;
                break;

        }
        return parametros;
    }

    public void girarGrafico(int deslocamento) throws ParseException {
        try {
            setPrimeiraData(getPrimeiraData()-deslocamento*ManipuladorDataTempo.tempoStringToTempoInt("24:00"));
            desenharGrafico();
        } catch (ParseException e) {
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

    public void setImageViewGrafico(ImageView imageViewGrafico) {
        this.imageViewGrafico = imageViewGrafico;
    }

    public View getGraficoContainer() {
        return graficoContainer;
    }

    public void setGraficoContainer(View graficoContainer) {
        this.graficoContainer = graficoContainer;
    }

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

    public int getNumPontos() {
        return numPontos;
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

    public int getAltura() {
        return altura;
    }

    public void setAltura(int altura) {
        this.altura = altura;
    }

    public int getAlturaVelha() {
        return alturaVelha;
    }

    public void setAlturaVelha(int alturaVelha) {
        this.alturaVelha = alturaVelha;
    }

    public boolean isEscondido() {
        return escondido;
    }

    public void setEscondido(boolean escondido) {
        this.escondido = escondido;
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

        // don't return false here or else none of the other
        // gestures will work
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        Log.i("TAG", "onSingleTapConfirmed: ");
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.i("TAG", "onLongPress: ");
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Log.i("TAG", "onDoubleTap: ");
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.i("TAG", "onScroll: ");
        deslocamentos = -(int)distanceX/divisor;
        return true;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
        Log.d("TAG", "onFling: ");
        setFling(true);
        return true;
    }

    public int getDivisor() {
        return divisor;
    }

    public void setDivisor(int divisor) {
        this.divisor = divisor;
    }

    public int getDeslocamentos() {
        return deslocamentos;
    }

    public void setDeslocamentos(int deslocamentos) {
        this.deslocamentos = deslocamentos;
    }

    public boolean isFling() {
        return isFling;
    }

    private void setFling(boolean fling) {
        isFling = fling;
    }
}