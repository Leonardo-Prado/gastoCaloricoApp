package nucleo.graficos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
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

public class DesenharGraficoCaloriasDiarias {

    private Context context;
    private Bitmap bitmapGrafico;
    private Canvas canvasGrafico;
    private ImageView imageViewGrafico;
    private View graficoContainer;
    private DBGeneric dbGeneric;
    private WindowManager windowManager;
    private Usuario usuario;

    public DesenharGraficoCaloriasDiarias(Context context, ImageView imageViewGrafico, View graficoContainer, Usuario usuario, WindowManager windowManager) {
        this.context = context;
        this.imageViewGrafico = imageViewGrafico;
        this.graficoContainer = graficoContainer;
        this.usuario = usuario;
        this.windowManager = windowManager;
        dbGeneric = new DBGeneric(getContext());
        desenharGrafico();
    }

    private void desenharGrafico() {
        try {
            View graficoConteiner = getGraficoContainer();//pega o relative layout que contem o canvas para pegar a altura
            int largura = graficoConteiner.getWidth(); // define a largura para ser usado no bitmap, da largura da tela
            int altura = graficoConteiner.getHeight();//pega a altura real do relative layout
            setBitmapGrafico(Bitmap.createBitmap(largura, altura, Bitmap.Config.ARGB_8888)); //gera bitmap
            getImageViewGrafico().setImageBitmap(getBitmapGrafico());//passa o bitmap para a imageview
            setCanvasGrafico(new Canvas(getBitmapGrafico())); // cria o canvas no bitmap
            getCanvasGrafico().drawColor(ResourcesCompat.getColor(getContext().getResources(), R.color.colorGraficoBackground, null)); //define a cor de preenchimento do canvas
            Long dia = ManipuladorDataTempo.tempoStringToTempoInt("24:00");
            ManipuladorDataTempo dataTempo = new ManipuladorDataTempo(new Date());
            List<List<String>> s = getDbGeneric().buscar("GastoEnergetico", new String[]{"Data", "GastoCalorico"}, "Data >= ? and Data <= ? and _idUsuario = ?", new String[]{Long.toString(dataTempo.getDataInt() - dia * 7), Long.toString(dataTempo.getDataInt()), Integer.toString(getUsuario().getId())}, "Data ASC");
            int dias = s.size();
            final Point ponto = new Point();
            int numeroX = largura / (dias + 1);
            if (dias > 0) {
                int i = 0;
                while (i < dias) {
                    int x = ponto.x;
                    int y = ponto.y;
                    final int[] parametros = parametrosPorDensidade(4);
                    ponto.x = (i + 1) * numeroX;
                    Float f = Float.parseFloat(s.get(i).get(1));
                    ponto.y = altura - (Math.round(f / 1000) * altura) / 10;
                    final Paint paint = new Paint();
                    if (i > 0) {
                        paint.setColor(Color.GREEN);
                        paint.setStrokeWidth(1f);
                        paint.setAntiAlias(true);
                        getCanvasGrafico().drawLine(x, y, ponto.x, ponto.y, paint);
                    }
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(getContext().getResources().getColor(R.color.pontosDoGrafico));
                    paint.setTextSize(parametros[1]);
                    getCanvasGrafico().drawCircle(ponto.x, ponto.y, parametros[0], paint);
                    paint.setColor(Color.WHITE);
                    getCanvasGrafico().save();
                    getCanvasGrafico().drawText(Double.toString(FormatNum.casasDecimais(Double.parseDouble(s.get(i).get(1)), 2)), ponto.x - parametros[0], ponto.y - (parametros[0]+20), paint);
                    getCanvasGrafico().drawText(ManipuladorDataTempo.dataIntToDataString(Long.parseLong(s.get(i).get(0)),"dd-MM"), ponto.x-parametros[0], altura - parametros[1], paint);
                    getCanvasGrafico().restore();
                    if (i > 0) {
                        paint.setColor(Color.WHITE);
                        paint.setStrokeWidth(1f);
                        paint.setAntiAlias(true);
                        getCanvasGrafico().drawLine(x, y, ponto.x, ponto.y, paint);
                    }
                    if(i==s.size()-1) {
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setColor(getContext().getResources().getColor(R.color.pontosDoGrafico));
                        getCanvasGrafico().drawCircle(ponto.x, ponto.y, (parametros[0]/2) + parametros[0], paint);
                    }
                    i++;
                }
            }

        } catch (Exception e) {
            Log.e("erro ao desenhar",e.getMessage());

        }

    }

    private int[] parametrosPorDensidade(int base) {
        int [] parametros = {base,base+5};
        DisplayMetrics metricas = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metricas);
        switch (metricas.densityDpi){
            case(DisplayMetrics.DENSITY_LOW):
                parametros[0] = base;
                parametros[1] = 11;
                break;
            case(DisplayMetrics.DENSITY_HIGH):
                parametros[0] = 3*base - 2;
                parametros[1] = 17;
                break;
            case(DisplayMetrics.DENSITY_MEDIUM):
                parametros[0] = 2*base - 2;
                parametros[1] = 15;
                break;
            case(DisplayMetrics.DENSITY_XHIGH):
                parametros[0] = 4*base - 2;
                parametros[1] = 22;
                break;
            case(DisplayMetrics.DENSITY_XXHIGH):
                parametros[0] = 5*base;
                parametros[1] = 27;
                break;
            case(DisplayMetrics.DENSITY_XXXHIGH):
                parametros[0] = 6*base;
                parametros[1] = 32;
                break;
            default:
                parametros[0] = 2*base;
                parametros[1] = 11;
                break;

        }
        return parametros;
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
}
