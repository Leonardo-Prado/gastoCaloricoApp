package nucleo.listviews_manipuladores;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ifmg.polardispendium_gastocalorico.R;
import com.ifmg.polardispendium_gastocalorico.TelaPrincipal;

import java.util.List;

import database.DBGeneric;
import nucleo.entidades_do_nucleo.AtividadesRealizadas;
import nucleo.entidades_do_nucleo.GastoEnergetico;
import nucleo.entidades_do_nucleo.Usuario;
import nucleo.outros_manipuladores.AtividadeRealizadaCriador;
import nucleo.outros_manipuladores.DialogNovaAtividade;
import objetos_auxiliares.FormatNum;
import objetos_auxiliares.ManipuladorDataTempo;

class ListViewItensAdapter extends ArrayAdapter<AtividadesRealizadas> {
    private Context context;
    final private List<AtividadesRealizadas> atividadesRealizadas;
    private Usuario usuario;
    private GastoEnergetico gastoEnergetico = new GastoEnergetico();

    public ListViewItensAdapter(@NonNull Context context, @NonNull List<AtividadesRealizadas> objects, Usuario usuario) {
        super(context, R.layout.lista_atividades_do_dia, objects);
        this.context = context;
        this.atividadesRealizadas = objects;
        this.usuario = usuario;
        DBGeneric dbGeneric = new DBGeneric(getContext());
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        //infla o layout
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.lista_atividades_do_dia,parent,false);

        //cria as variaveis
        TextView tvAtividade = view.findViewById(R.id.tvAtividade);
        TextView tvHoraInicio = view.findViewById(R.id.tvHoraInicio);
        TextView tvHoraFim = view.findViewById(R.id.tvHoraFim);
        TextView tvTempoTotal= view.findViewById(R.id.tvTempoTotal);
        TextView tvGastoCalorico = view.findViewById(R.id.tvGastoCalorico);
        ImageButton ibtnEditar = view.findViewById(R.id.ibtnEditar);
        ImageButton ibtnApagar = view.findViewById(R.id.ibtnApagar);
        ImageView imvImagemCategorias = view.findViewById(R.id.imvImagemCategorias);

        //busca as atividades no banco de dados pela idAtividade fornecida pela atividade
        //realizada para passar para o campo tvAtividade no list view
        final DBGeneric db = new DBGeneric(context);
        String tabela = "AtividadesFisicas";
        String[] campos = new String[]{"Atividades","MET","_idCategoria"};
        String where = "_id = ?";
        String[] argumento = new String[]{Integer.toString(atividadesRealizadas.get(position).getIdAtividade())};
        List<List<String>> atividade = db.buscar(tabela,campos,where,argumento);
        tvAtividade.setText(atividade.get(0).get(0));
        long inicio,fim;
        inicio = atividadesRealizadas.get(position).getHoraInicio();
        fim = atividadesRealizadas.get(position).getHoraFim();
        tvHoraInicio.setText(context.getResources().getString(R.string.fragment_inicio_listview_tvhorainicio_text)+"\n"+ManipuladorDataTempo.tempoIntToTempoString(inicio));
        tvHoraFim.setText(context.getResources().getString(R.string.fragment_inicio_listview_tvhoratermino_text)+"\n"+ManipuladorDataTempo.tempoIntToTempoString(fim));
        double tempoTotal = ManipuladorDataTempo.horas(fim) - ManipuladorDataTempo.horas(inicio);
        tvTempoTotal.setText(context.getResources().getString(R.string.fragment_inicio_listview_tvtempototal_text) +"\n"+Double.toString(FormatNum.casasDecimais(tempoTotal,2)));
        double calorias = tempoTotal * usuario.getMassaCorporal() * Double.parseDouble(atividade.get(0).get(1));
        tvGastoCalorico.setText(context.getResources().getString(R.string.fragment_inicio_listview_tvgastocalorico_text)+Double.toString(FormatNum.casasDecimais(calorias,2)));
        gastoEnergetico.setCalorias(gastoEnergetico.getCalorias()+calorias);
        int idCat = Integer.parseInt(atividade.get(0).get(2));
        Resources resources = getContext().getResources();
        TypedArray typedArray = resources.obtainTypedArray(R.array.imagemListCategorias);
        Drawable drawable = typedArray.getDrawable(idCat-1);
        imvImagemCategorias.setImageDrawable(drawable);

        ibtnApagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AtividadesRealizadas atividadesRealizada = atividadesRealizadas.get(position);
                atividadesRealizada.setIdAtividade(7030);
                db.deletar("AtividadesRealizadas","_id = ?",new String[]{atividadesRealizada.getIdString()});
                AtividadeRealizadaCriador criador = new AtividadeRealizadaCriador(view.getContext(),atividadesRealizada.getDia(),atividadesRealizada);
                List<AtividadesRealizadas> atividadesRealizadasList = TelaPrincipal.buscarAtividadesRealizadas(atividadesRealizada.getDia(), usuario,context);
                new AtualizadorLista(view.getContext(),atividadesRealizada.getDia(),usuario,atividadesRealizadasList,parent);
            }
        });
        ibtnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DialogNovaAtividade(view.getContext(),usuario,atividadesRealizadas.get(position).getDia(),parent,atividadesRealizadas.get(position).getHoraInicio(),atividadesRealizadas.get(position).getHoraFim(),atividadesRealizadas.get(position).getIdAtividade(),atividadesRealizadas.get(position).getId());
            }
        });


        return view;
    }
}
