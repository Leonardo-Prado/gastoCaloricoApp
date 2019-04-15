package nucleo.listviews_manipuladores;

import android.content.ContentValues;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ifmg.polardispendium_gastocalorico.R;

import java.util.List;

import database.DBGeneric;
import nucleo.entidades_do_nucleo.AtividadesRealizadas;
import nucleo.entidades_do_nucleo.GastoEnergetico;
import nucleo.entidades_do_nucleo.Usuario;
import objetos_auxiliares.ManipuladorDataTempo;

public class AtualizadorLista {
    private Context context;
    private Long data;
    private DBGeneric dbGeneric;
    private Usuario usuario;
    private List<AtividadesRealizadas> atividadesRealizadas;
    private View view;

    public AtualizadorLista(Context context, Long data, Usuario usuario, List<AtividadesRealizadas> atividadesRealizadas,View view) {
        this.context = context;
        this.data = data;
        this.usuario = usuario;
        this.atividadesRealizadas = atividadesRealizadas;
        this.view = view;
        dbGeneric = new DBGeneric(getContext());
        atualizarListView(getAtividadesRealizadas(),getUsuario());
    }


    private void atualizarListView(List<AtividadesRealizadas> atividadesRealizadas, Usuario usuario) {
        try {
            Long data = this.data;//pega a data do edData
            GastoEnergetico gastoEnergetico = new GastoEnergetico();//cria uma instancia do objeto gastoenergetico
            gastoEnergetico.setIdUsuario(getUsuario().getId());//passa o id do usuario para gasto calorico
            gastoEnergetico.setData(data);//passa data para gastocalorico
            //para cada atividadeRealizada na lista de atividades realizadas faz o foreach
            for (AtividadesRealizadas a : atividadesRealizadas) {
                double tempoTotal = ManipuladorDataTempo.horas(a.getHoraFim()) - ManipuladorDataTempo.horas(a.getHoraInicio());//calcula o tempo total
                double massa = usuario.getMassaCorporal();//pega o peso do usuario
                List<List<String>> s = getDbGeneric().buscar("AtividadesFisicas", new String[]{"Atividades", "MET"}, "_id = ?", new String[]{(Integer.toString(a.getIdAtividade()))});//pega o MET da atividade fisica realizada
                gastoEnergetico.setCalorias(gastoEnergetico.getCalorias() + tempoTotal * massa * Double.parseDouble(s.get(0).get(1)));//calcula os gastos caloricos da atividade e soma com os gastos caloricos totais na data
            }
            //busca no banco de dados por gastos caloricos do usuario na data estipulada
            List<List<String>> s = getDbGeneric().buscar("GastoEnergetico", new String[]{"_id"}, "Data = ? and _idUsuario = ?", new String[]{data.toString(), Integer.toString(gastoEnergetico.getIdUsuario())});
            ContentValues c = new ContentValues();
            if (s.size() > 0) {//se existir gastos nessa data atualiza o banco de dados
                c.put("GastoCalorico", gastoEnergetico.getCalorias());
                getDbGeneric().atualizar("GastoEnergetico", c, "_id = ?", new String[]{s.get(0).get(0)});
            } else {//se não existir gasto calorico na data cria um.
                c.put("GastoCalorico", gastoEnergetico.getCalorias());
                c.put("Data", gastoEnergetico.getData());
                c.put("_idUsuario", gastoEnergetico.getIdUsuario());
                getDbGeneric().inserir(c, "GastoEnergetico");
            }
            List<List<String>> gastosCaloricos= dbGeneric.buscar("GastoEnergetico",new String[]{"GastoCalorico"},"_idUsuario = ?",new String[]{Integer.toString(getUsuario().getId())},"GastoCalorico DESC");
            double somaGastocaloricos = 0;
            for (List<String> list:gastosCaloricos
                    ) {
                somaGastocaloricos=(somaGastocaloricos+Double.parseDouble(list.get(0)));
            }
            usuario.setGastoMedio(somaGastocaloricos/gastosCaloricos.size());
            usuario.setGastoMaximo(Double.parseDouble(gastosCaloricos.get(0).get(0)));
            usuario.setGastoMinimo(Double.parseDouble(gastosCaloricos.get(gastosCaloricos.size()-1).get(0)));
            c = new ContentValues();
            c.put("GastoMedio",usuario.getGastoMedio());
            c.put("GastoMaximo",usuario.getGastoMaximo());
            c.put("GastoMinimo",usuario.getGastoMinimo());
            dbGeneric.atualizar("Usuarios",c,"_id = ?",new String[]{Integer.toString(usuario.getId())});
            //cria uma nova instancia da listview para as atividades realizadas na data

            RecyclerView rvAtividades = view.findViewById(R.id.rvAtividades);
            RecyclerView.Adapter adapter = new RecyclerViewAdapter(getContext(),atividadesRealizadas,usuario);
            rvAtividades.setAdapter(adapter);
            RecyclerView.LayoutManager layout = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            rvAtividades.setLayoutManager(layout);

          //  ListView listView = view.findViewById(R.id.lvListaAtividadesDoDia);//cria o listview
          //  ArrayAdapter listViewItensAdapter = new ListViewItensAdapter(getContext(), atividadesRealizadas, usuario);//cria o listview adapter para as atividades
           // listView.setAdapter(listViewItensAdapter);//passa o adapter para as listviews
        } catch (Exception e) {
            Log.e("erro ao criar listview", "Erro ao criar ");
        }
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

    public DBGeneric getDbGeneric() {
        return dbGeneric;
    }

    public void setDbGeneric(DBGeneric dbGeneric) {
        this.dbGeneric = dbGeneric;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<AtividadesRealizadas> getAtividadesRealizadas() {
        return atividadesRealizadas;
    }

    public void setAtividadesRealizadas(List<AtividadesRealizadas> atividadesRealizadas) {
        this.atividadesRealizadas = atividadesRealizadas;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }
}
