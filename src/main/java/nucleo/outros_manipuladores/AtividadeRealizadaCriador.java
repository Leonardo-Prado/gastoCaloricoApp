package nucleo.outros_manipuladores;

import android.content.ContentValues;
import android.content.Context;

import java.util.List;

import database.DBGeneric;
import nucleo.entidades_do_nucleo.AtividadesRealizadas;

public class AtividadeRealizadaCriador {

    private DBGeneric dbGeneric;
    private Context context;
    private Long data;
    private AtividadesRealizadas atividadesRealizadas;
    private boolean tempoIgual = false;

    public AtividadeRealizadaCriador(Context context, Long data,AtividadesRealizadas atividadesRealizadas) {
        this.context = context;
        this.data = data;
        this.atividadesRealizadas = atividadesRealizadas;
        this.dbGeneric = new DBGeneric(getContext());
        adicionarNovaAtividadeRealizada(getAtividadesRealizadas());
        checarTempoIgual();
    }


    private void adicionarNovaAtividadeRealizada(AtividadesRealizadas atividadesRealizadas){
        try {
            atividadesRealizadas.setDia(getData());
            long inicioAtiv = atividadesRealizadas.getHoraInicio();
            long fimAtiv = atividadesRealizadas.getHoraFim();
            int idAtiv = atividadesRealizadas.getIdAtividade();
            List<List<String>> listDeListString = new DBGeneric(getContext()).buscar("AtividadesRealizadas", new String[]{"_id", "_idAtividade", "HoraInicio", "HoraFim"}, "_idUsuario = ? and Data = ?", new String[]{Integer.toString(atividadesRealizadas.getIdUsuario()), Long.toString(atividadesRealizadas.getDia())},"HoraInicio ASC");
            if (listDeListString.size() > 0) {
                for (List<String> s : listDeListString) {
                    int id = Integer.parseInt(s.get(1));
                    Long inicio = Long.parseLong(s.get(2));
                    Long fim = Long.parseLong(s.get(3));
                    if(id == idAtiv &&inicio==fimAtiv){
                        atividadesRealizadas.setHoraFim(fim);
                        dbGeneric.deletar("AtividadesRealizadas","_id = ?",new String[]{s.get(0)});

                    }else if(id==idAtiv&&inicioAtiv==fim){
                        atividadesRealizadas.setHoraInicio(inicio);
                        dbGeneric.deletar("AtividadesRealizadas","_id = ?",new String[]{s.get(0)});

                    }else if (inicio <= inicioAtiv && fim >= fimAtiv) {
                        ContentValues values = new ContentValues();
                        if (inicio!=inicioAtiv) {
                            values.put("HoraInicio", inicio);
                            values.put("HoraFim", atividadesRealizadas.getHoraInicio());
                            getDbGeneric().atualizar("AtividadesRealizadas", values, "_id = ?", new String[]{s.get(0)});
                        }
                        if (fim!=fimAtiv){
                            values = new ContentValues();
                            values.put("HoraInicio", atividadesRealizadas.getHoraFim());
                            values.put("HoraFim", fim);
                            values.put("_idAtividade", Integer.parseInt(s.get(1)));
                            values.put("_idUsuario", atividadesRealizadas.getIdUsuario());
                            values.put("Data", atividadesRealizadas.getDia());
                            getDbGeneric().inserir(values, "AtividadesRealizadas");
                        }
                        if(fim==fimAtiv&&inicio==inicioAtiv){
                            tempoIgual = true;
                            values.put("_idAtividade", atividadesRealizadas.getIdAtividade());
                            getDbGeneric().atualizar("AtividadesRealizadas", values, "_id = ?", new String[]{s.get(0)});
                        }
                    } else if (inicio >= inicioAtiv && inicio <= fimAtiv && fim >= fimAtiv){
                        ContentValues values = new ContentValues();
                        values.put("HoraInicio", atividadesRealizadas.getHoraFim());
                        values.put("HoraFim", fim);
                        getDbGeneric().atualizar("AtividadesRealizadas", values, "_id = ?", new String[]{s.get(0)});
                    } else if (inicio <= inicioAtiv && fim >= inicioAtiv && fim <= fimAtiv) {
                        ContentValues values = new ContentValues();
                        values.put("HoraInicio", inicio);
                        values.put("HoraFim", atividadesRealizadas.getHoraInicio());
                        getDbGeneric().atualizar("AtividadesRealizadas", values, "_id = ?", new String[]{s.get(0)});
                    } else if (inicio >= inicioAtiv && fim < fimAtiv) {
                        getDbGeneric().deletar("AtividadesRealizadas", "_id = ?", new String[]{s.get(0)});
                    }
                }
                if(!tempoIgual){
                    ContentValues values = new ContentValues();
                    values.put("HoraInicio", atividadesRealizadas.getHoraInicio());
                    values.put("HoraFim", atividadesRealizadas.getHoraFim());
                    values.put("_idAtividade", atividadesRealizadas.getIdAtividade());
                    values.put("_idUsuario", atividadesRealizadas.getIdUsuario());
                    values.put("Data", atividadesRealizadas.getDia());
                    getDbGeneric().inserir(values, "AtividadesRealizadas");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void checarTempoIgual(){
        List<List<String>> listDeListString = new DBGeneric(getContext()).buscar("AtividadesRealizadas", new String[]{"_id", "_idAtividade", "HoraInicio", "HoraFim"}, "_idUsuario = ? and Data = ?", new String[]{Integer.toString(atividadesRealizadas.getIdUsuario()), Long.toString(atividadesRealizadas.getDia())},"HoraInicio ASC");
        if (listDeListString.size() > 0) {
            for (List<String> s : listDeListString) {
                int id = Integer.parseInt(s.get(1));
                long inicio = Long.parseLong(s.get(2));
                long fim = Long.parseLong(s.get(3));
                if(inicio==fim){
                    getDbGeneric().deletar("AtividadesRealizadas", "_id = ?", new String[]{s.get(0)});
                }
            }
        }
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

    private Long getData() {
        return data;
    }

    public void setData(Long data) {
        this.data = data;
    }

    private AtividadesRealizadas getAtividadesRealizadas() {
        return atividadesRealizadas;
    }

    public void setAtividadesRealizadas(AtividadesRealizadas atividadesRealizadas) {
        this.atividadesRealizadas = atividadesRealizadas;
    }
}
