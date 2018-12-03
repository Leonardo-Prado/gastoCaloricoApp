package nucleo.outros_manipuladores;

import android.content.ContentValues;
import android.content.Context;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import database.CarregarXML;
import database.DBGeneric;

public class CriarDataBase {

    private DBGeneric dbGeneric;
    private Context context;

    public CriarDataBase(Context context) {
        this.context = context;
        dbGeneric = new DBGeneric(getContext());
    }

    public boolean criar() throws ParserConfigurationException, SAXException, IOException {
        boolean foiCriado = false;
        try {
            List<List<String>> cat = getDbGeneric().buscar("Categorias", new String[]{"_id"});//busca as categorias no banco de dados
            if (!(cat.size() > 0)) { //se a busca não retornar nada carrega apartir do xml(somente na primeira execução)
                CarregarXML carregarXML = new CarregarXML();
                List<List<String>> strings = carregarXML.XML(getContext(), "Categorias.xml", "Categorias");
                if (strings.size() > 0) {
                    //para cada categotia na lista adiciona a categoria no banco de dados
                    for (List<String> list : strings
                            ) {
                        ContentValues values = new ContentValues();
                        values.put("Categoria", list.get(1));
                        values.put("Descricao", list.get(2));
                        getDbGeneric().inserir(values, "Categorias");
                    }
                }
            }
            cat = getDbGeneric().buscar("AtividadesFisicas", new String[]{"_id"});//busca as atividades no banco de dados
            if (!(cat.size() > 0)) {//se a busca não retornar nada carrega apartir do xml
                CarregarXML carregarXML = new CarregarXML();
                List<List<String>> strings = carregarXML.XML(getContext(), "AtividadesFisicas.xml", "Atividade");
                if (strings.size() > 0) {
                    //para cada atividade na lista adiciona a atividade no banco de dados
                    for (List<String> list : strings
                            ) {
                        ContentValues values = new ContentValues();
                        values.put("_id", list.get(0));
                        values.put("MET", list.get(1));
                        List<List<String>> l = getDbGeneric().buscar("Categorias", new String[]{"_id"}, "Categoria = ?", new String[]{list.get(2)});
                        if (l.size() > 0)
                            values.put("_idCategoria", Integer.parseInt(l.get(0).get(0)));
                        else
                            list.get(0);
                        values.put("Atividades", list.get(3));
                        getDbGeneric().inserir(values, "AtividadesFisicas");
                    }
                }

            }
            foiCriado = true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return foiCriado;
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
}
