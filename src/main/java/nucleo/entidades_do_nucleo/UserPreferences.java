package nucleo.entidades_do_nucleo;

import android.content.ContentValues;
import android.content.Context;

import java.util.List;

import database.DBGeneric;

public class UserPreferences extends Usuario {
    private int id;
    private int usuarioId;
    private String usuarioIdString;
    private int rolamentoSensibilidade;
    private int escondeGraficoTempo;
    private int graficoPontosSize;
    private int graficoTextoSize;
    private Context context;
    private DBGeneric dbGeneric;


    public UserPreferences(Context context,Usuario usuario) {
        super(usuario.getId());
        setUsuarioId(usuario.getId());
        this.context = context;
        dbGeneric = new DBGeneric(getContext());
        List<List<String>> s = dbGeneric.buscar("Preferencias",new String[]{"_id","RolamentoSens","EscondeGraficoTempo","GraficoPontosSize","GraficoTextoSize"},"_idUsuario = ?",new String[]{Integer.toString(usuario.getId())});
        if(s.size()==0){
            setRolamentoSensibilidade(50);
            setEscondeGraficoTempo(5000);
            setGraficoPontosSize(4);
            setGraficoTextoSize(11);
            ContentValues values = new ContentValues();
            values.put("_idUsuario",usuario.getId());
            values.put("RolamentoSens",getRolamentoSensibilidade());
            values.put("EscondeGraficoTempo",getEscondeGraficoTempo());
            values.put("GraficoPontosSize",getGraficoPontosSize());
            values.put("GraficoTextoSize",getGraficoTextoSize());
            setId(dbGeneric.inserir(values,"Preferencias"));
        }else{
            setRolamentoSensibilidade(Integer.parseInt(s.get(0).get(1)));
            setEscondeGraficoTempo(Integer.parseInt(s.get(0).get(2)));
            setGraficoPontosSize(Integer.parseInt(s.get(0).get(3)));
            setGraficoTextoSize(Integer.parseInt(s.get(0).get(4)));
        }
    }

    public boolean atualizarDBPreferencias(int id){
        boolean inserido = false;
        ContentValues values = new ContentValues();
        values.put("RolamentoSens",getRolamentoSensibilidade());
        values.put("EscondeGraficoTempo",getEscondeGraficoTempo());
        values.put("GraficoPontosSize",getGraficoPontosSize());
        values.put("GraficoTextoSize",getGraficoTextoSize());
        inserido = dbGeneric.atualizar(values,"Preferencias","_idUsuario = ?",new String[]{getUsuarioIdString()});
        return inserido;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        setUsuarioIdString(Integer.toString(usuarioId));
        this.usuarioId = usuarioId;
    }

    public int getRolamentoSensibilidade() {
        return rolamentoSensibilidade;
    }

    public void setRolamentoSensibilidade(int rolamentoSensibilidade) {
        this.rolamentoSensibilidade = rolamentoSensibilidade;
    }

    public int getEscondeGraficoTempo() {
        return escondeGraficoTempo;
    }

    public void setEscondeGraficoTempo(int escondeGraficoTempo) {
        this.escondeGraficoTempo = escondeGraficoTempo;
    }

    public int getGraficoPontosSize() {
        return graficoPontosSize;
    }

    public void setGraficoPontosSize(int graficoPontosSize) {
        this.graficoPontosSize = graficoPontosSize;
    }

    public int getGraficoTextoSize() {
        return graficoTextoSize;
    }

    public void setGraficoTextoSize(int graficoTextoSize) {
        this.graficoTextoSize = graficoTextoSize;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getUsuarioIdString() {
        return usuarioIdString;
    }

    public void setUsuarioIdString(String usuarioIdString) {
        this.usuarioIdString = usuarioIdString;
    }

    public void redefinir() {
        setRolamentoSensibilidade(60);
        setGraficoPontosSize(4);
        setGraficoTextoSize(11);
        setEscondeGraficoTempo(5000);
        ContentValues values = new ContentValues();
        values.put("RolamentoSens",getRolamentoSensibilidade());
        values.put("EscondeGraficoTempo",getEscondeGraficoTempo());
        values.put("GraficoPontosSize",getGraficoPontosSize());
        values.put("GraficoTextoSize",getGraficoTextoSize());
        dbGeneric.atualizar(values,"Preferencias","_idUsuario = ?",new String[]{getUsuarioIdString()});
    }
}
