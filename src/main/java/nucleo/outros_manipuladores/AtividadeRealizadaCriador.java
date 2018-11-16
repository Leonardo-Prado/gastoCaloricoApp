package nucleo.outros_manipuladores;

import android.content.ContentValues;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import database.DBGeneric;
import nucleo.entidades_do_nucleo.AtividadesRealizadas;

public class AtividadeRealizadaCriador {

    private DBGeneric dbGeneric;
    private Context context;
    private Long data;
    private AtividadesRealizadas atividadesRealizadas;

    public AtividadeRealizadaCriador(Context context, Long data,AtividadesRealizadas atividadesRealizadas) {
        this.context = context;
        this.data = data;
        this.atividadesRealizadas = atividadesRealizadas;
        this.dbGeneric = new DBGeneric(getContext());
        adicionarNovaAtividadeRealizada(getAtividadesRealizadas());
    }


    private void adicionarNovaAtividadeRealizada(AtividadesRealizadas atividadesRealizadas){
        try {
            atividadesRealizadas.setDia(getData());
            List<AtividadesRealizadas> atividadesRealizadasList = new ArrayList<>();
            List<List<String>> listDeListString = new DBGeneric(getContext()).buscar("AtividadesRealizadas", new String[]{"_id", "_idAtividade", "HoraInicio", "HoraFim"}, "_idUsuario = ? and Data = ?", new String[]{Integer.toString(atividadesRealizadas.getIdUsuario()), Long.toString(atividadesRealizadas.getDia())});
            if (listDeListString.size() > 0) {
                for (List<String> s : listDeListString) {
                    AtividadesRealizadas realizadas = new AtividadesRealizadas();
                    AtividadesRealizadas realizadas2 = new AtividadesRealizadas();
                    Long inicio = Long.parseLong(s.get(2));
                    Long fim = Long.parseLong(s.get(3));
                    if (inicio <= atividadesRealizadas.getHoraInicio() && fim >= atividadesRealizadas.getHoraFim()) {
                        ContentValues values = new ContentValues();
                        values.put("HoraInicio", inicio);
                        values.put("HoraFim", atividadesRealizadas.getHoraInicio());
                        getDbGeneric().atualizar("AtividadesRealizadas", values, "_id = ?", new String[]{s.get(0)});
                        values = new ContentValues();
                        values.put("HoraInicio", atividadesRealizadas.getHoraFim());
                        values.put("HoraFim", fim);
                        values.put("_idAtividade", Integer.parseInt(s.get(1)));
                        values.put("_idUsuario", atividadesRealizadas.getIdUsuario());
                        values.put("Data", atividadesRealizadas.getDia());
                        getDbGeneric().inserir(values, "AtividadesRealizadas");
                    } else if (inicio >= atividadesRealizadas.getHoraInicio() && inicio <= atividadesRealizadas.getHoraFim() && fim >= atividadesRealizadas.getHoraFim()) {
                        ContentValues values = new ContentValues();
                        values.put("HoraInicio", atividadesRealizadas.getHoraFim());
                        values.put("HoraFim", fim);
                        getDbGeneric().atualizar("AtividadesRealizadas", values, "_id = ?", new String[]{s.get(0)});
                    } else if (inicio <= atividadesRealizadas.getHoraInicio() && fim >= atividadesRealizadas.getHoraInicio() && fim <= atividadesRealizadas.getHoraFim()) {
                        ContentValues values = new ContentValues();
                        values.put("HoraInicio", inicio);
                        values.put("HoraFim", atividadesRealizadas.getHoraInicio());
                        getDbGeneric().atualizar("AtividadesRealizadas", values, "_id = ?", new String[]{s.get(0)});
                    } else if (inicio >= atividadesRealizadas.getHoraInicio() && fim < atividadesRealizadas.getHoraFim()) {
                        getDbGeneric().deletar("AtividadesRealizadas", "_id = ?", new String[]{s.get(0)});
                    }
                }
                ContentValues values = new ContentValues();
                values.put("HoraInicio", atividadesRealizadas.getHoraInicio());
                values.put("HoraFim", atividadesRealizadas.getHoraFim());
                values.put("_idAtividade", atividadesRealizadas.getIdAtividade());
                values.put("_idUsuario", atividadesRealizadas.getIdUsuario());
                values.put("Data", atividadesRealizadas.getDia());
                getDbGeneric().inserir(values, "AtividadesRealizadas");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public Long getData() {
        return data;
    }

    public void setData(Long data) {
        this.data = data;
    }

    public AtividadesRealizadas getAtividadesRealizadas() {
        return atividadesRealizadas;
    }

    public void setAtividadesRealizadas(AtividadesRealizadas atividadesRealizadas) {
        this.atividadesRealizadas = atividadesRealizadas;
    }
}
