package nucleo;

import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ifmg.polardispendium_gastocalorico.R;

import java.util.List;

import database.DBGeneric;
import objetos_auxiliares.FormatNum;
import objetos_auxiliares.ManipuladorDataTempo;

public class ListViewItensAdapter extends ArrayAdapter<AtividadesRealizadas> {
    private Context context;
    private List<AtividadesRealizadas> atividadesRealizadas;
    private Usuario usuario;
    private GastoEnergetico gastoEnergetico = new GastoEnergetico();

    public ListViewItensAdapter(@NonNull Context context, @NonNull List<AtividadesRealizadas> objects, Usuario usuario) {
        super(context, R.layout.lista_atividades_do_dia, objects);
        this.context = context;
        this.atividadesRealizadas = objects;
        this.usuario = usuario;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //infla o layout
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.lista_atividades_do_dia,parent,false);

        //cria as variaveis
        TextView tvAtividade = (TextView) view.findViewById(R.id.tvAtividade);
        TextView tvHoraInicio = (TextView) view.findViewById(R.id.tvHoraInicio);
        TextView tvHoraFim = (TextView) view.findViewById(R.id.tvHoraFim);
        TextView tvTempoTotal= (TextView) view.findViewById(R.id.tvTempoTotal);
        TextView tvGastoCalorico = (TextView) view.findViewById(R.id.tvGastoCalorico);
        ImageButton ibtnEditar = (ImageButton) view.findViewById(R.id.ibtnEditar);
        ImageButton ibtnApagar = (ImageButton) view.findViewById(R.id.ibtnApagar);

        //busca as atividades no banco de dados pela idAtividade fornecida pela atividade
        //realizada para passar para o campo tvAtividade no list view
        DBGeneric db = new DBGeneric(context);
        String tabela = "AtividadesFisicas";
        String[] campos = new String[]{"Atividades","MET"};
        String where = "_id = ?";
        String[] argumento = new String[]{Integer.toString(atividadesRealizadas.get(position).getIdAtividade())};
        List<List<String>> atividade = db.buscar(tabela,campos,where,argumento);
        tvAtividade.setText(atividade.get(0).get(0));
        long inicio,fim;
        inicio = atividadesRealizadas.get(position).getHoraInicio();
        fim = atividadesRealizadas.get(position).getHoraFim();
        tvHoraInicio.setText("Inicio:\n"+ManipuladorDataTempo.tempoIntToTempoString(inicio));
        tvHoraFim.setText("Termino: \n"+ManipuladorDataTempo.tempoIntToTempoString(fim));
        double tempoTotal = ManipuladorDataTempo.horas(fim) - ManipuladorDataTempo.horas(inicio);
        tvTempoTotal.setText("Duração: \n"+Double.toString(FormatNum.casasDecimais(tempoTotal,2)));
        double calorias = tempoTotal * usuario.getMassaCorporal() * Double.parseDouble(atividade.get(0).get(1));
        tvGastoCalorico.setText("Calorias gastas: "+Double.toString(FormatNum.casasDecimais(calorias,2)));
        gastoEnergetico.setCalorias(gastoEnergetico.getCalorias()+calorias);

        return view;
    }
}
