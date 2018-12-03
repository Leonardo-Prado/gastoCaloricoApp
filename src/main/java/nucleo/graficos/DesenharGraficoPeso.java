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
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.ifmg.polardispendium_gastocalorico.R;

import java.util.Date;
import java.util.List;

import database.DBGeneric;
import nucleo.entidades_do_nucleo.Usuario;
import objetos_auxiliares.FormatNum;
import objetos_auxiliares.ManipuladorDataTempo;

public class DesenharGraficoPeso {
    private Context context;
    private Bitmap bitmapGrafico;
    private Canvas canvasGrafico;
    private ImageView imageViewGrafico;
    private View graficoContainer;
    private DBGeneric dbGeneric;
    private WindowManager windowManager;
    private Usuario usuario;

    public DesenharGraficoPeso(Context context, ImageView imageViewGrafico, View graficoContainer, Usuario usuario, WindowManager windowManager) {
        this.context = context;
        this.imageViewGrafico = imageViewGrafico;
        this.graficoContainer = graficoContainer;
        this.usuario = usuario;
        this.windowManager = windowManager;
        if(context!=null){
            dbGeneric = new DBGeneric(getContext());
            desenharGrafico();
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
            getCanvasGrafico().drawColor(res.getColor(R.color.colorGraficoBackground)); //define a cor de preenchimento do canvas
            Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.back_grafico);
            Paint paintAlpha = new Paint();
            paintAlpha.setAntiAlias(true);
            paintAlpha.setAlpha(10);
            getCanvasGrafico().drawBitmap(bitmap,null,new Rect(0,0,largura,altura),paintAlpha);

            Long dia = ManipuladorDataTempo.tempoStringToTempoInt("24:00");
            Long semana = dia*7;
            Long mes = dia * 30;
            ManipuladorDataTempo dataTempo = new ManipuladorDataTempo(new Date());
            List<List<String>> s = getDbGeneric().buscar("Peso", new String[]{"Data", "Peso"}, "Data >= ? and Data <= ? and _idUsuario = ?", new String[]{Long.toString(dataTempo.getDataInt() - 2*mes), Long.toString(dataTempo.getDataInt()), Integer.toString(getUsuario().getId())}, "Data ASC");
            int dias = s.size();
            final Point ponto = new Point();
            int numeroX = largura / (dias + 1);
            if (dias > 0) {
                int i = 0;
                int maiorPeso = 0;
                int menorPeso = 0;
                while(i < dias){
                    try {
                        if (i > 0) {
                            if (Double.parseDouble(s.get(i).get(1)) > maiorPeso)
                                maiorPeso =(int) Double.parseDouble(s.get(i).get(1));
                            if (Double.parseDouble(s.get(i).get(1)) < menorPeso)
                                menorPeso = (int) Double.parseDouble(s.get(i).get(1));
                        } else {
                            maiorPeso = (int) Double.parseDouble(s.get(i).get(1));
                            menorPeso = (int) Double.parseDouble(s.get(i).get(1));
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    i++;
                }
                i = 0;
                final int[] parametros = parametrosPorDensidade();
                int alt = altura-12*parametros[0];
                while (i < dias) {
                    int x = ponto.x;
                    int y = ponto.y;
                    ponto.x = (i + 1) * numeroX;
                    float f = Float.parseFloat(s.get(i).get(1));
                    f = (f-(menorPeso)) / (maiorPeso-menorPeso);
                    ponto.y = (int)(alt -  f * alt)+ 7*parametros[0];
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
                    getCanvasGrafico().save();
                    getCanvasGrafico().drawText(Double.toString(FormatNum.casasDecimais(Double.parseDouble(s.get(i).get(1)), 1)), ponto.x - parametros[0], ponto.y - (parametros[0]+10), paint);
                    getCanvasGrafico().drawText(ManipuladorDataTempo.dataIntToDataString(Long.parseLong(s.get(i).get(0)),"dd-MM"), ponto.x-parametros[0], altura - 5, paint);
                    getCanvasGrafico().restore();
                    if (i > 0) {
                        paint.setColor(Color.WHITE);
                        paint.setStrokeWidth(1f);
                        paint.setAntiAlias(true);
                        getCanvasGrafico().drawLine(x, y, ponto.x, ponto.y, paint);
                    }
                    if(i==s.size()-1) {
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
            getCanvasGrafico().drawText(res.getString(R.string.grafico_peso_titulo),20,30,paint);

        } catch (Exception e) {
            Log.e("erro ao desenhar",e.getMessage());

        }

    }

    private int[] parametrosPorDensidade() {
        int [] parametros = {4, 4 +5};
        DisplayMetrics metricas = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metricas);
        switch (metricas.densityDpi){
            case(DisplayMetrics.DENSITY_LOW):
                parametros[0] = 4;
                parametros[1] = 11;
                break;
            case(DisplayMetrics.DENSITY_HIGH):
                parametros[0] = 3* 4 - 2;
                parametros[1] = 20;
                break;
            case(DisplayMetrics.DENSITY_MEDIUM):
                parametros[0] = 2* 4 - 2;
                parametros[1] = 15;
                break;
            case(DisplayMetrics.DENSITY_XHIGH):
                parametros[0] = 4* 4 - 2;
                parametros[1] = 25;
                break;
            case(DisplayMetrics.DENSITY_XXHIGH):
                parametros[0] = 6* 4;
                parametros[1] = 30;
                break;
            case(DisplayMetrics.DENSITY_XXXHIGH):
                parametros[0] = 7* 4;
                parametros[1] = 32;
                break;
            default:
                parametros[0] = 2* 4;
                parametros[1] = 11;
                break;

        }
        return parametros;
    }

    private Bitmap getBitmapGrafico() {
        return bitmapGrafico;
    }

    private void setBitmapGrafico(Bitmap bitmapGrafico) {
        this.bitmapGrafico = bitmapGrafico;
    }

    private Canvas getCanvasGrafico() {
        return canvasGrafico;
    }

    private void setCanvasGrafico(Canvas canvasGrafico) {
        this.canvasGrafico = canvasGrafico;
    }

    private ImageView getImageViewGrafico() {
        return imageViewGrafico;
    }

    public void setImageViewGrafico(ImageView imageViewGrafico) {
        this.imageViewGrafico = imageViewGrafico;
    }

    private View getGraficoContainer() {
        return graficoContainer;
    }

    public void setGraficoContainer(View graficoContainer) {
        this.graficoContainer = graficoContainer;
    }

    private DBGeneric getDbGeneric() {
        return dbGeneric;
    }

    public void setDbGeneric(DBGeneric dbGeneric) {
        this.dbGeneric = dbGeneric;
    }

    private Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
