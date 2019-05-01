package nucleo.listviews_manipuladores;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class RecyclerViewAdapter extends RecyclerView.Adapter {
    private Context context;
    final private List<AtividadesRealizadas> atividadesRealizadas;
    private Usuario usuario;
    private GastoEnergetico gastoEnergetico = new GastoEnergetico();
    DBGeneric dbGeneric;
    View parent;

    public RecyclerViewAdapter(@NonNull Context context, @NonNull List<AtividadesRealizadas> objects, Usuario usuario) {
        this.context = context;
        this.atividadesRealizadas = objects;
        this.usuario = usuario;
        dbGeneric = new DBGeneric(context);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.lista_atividades_do_dia,parent,false);
        AtividadesViewHolder viewHolder = new AtividadesViewHolder(view);
        this.parent = parent;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        final int position = i;
        AtividadesViewHolder viewHolder = (AtividadesViewHolder)holder;
        String tabela = "AtividadesFisicas";
        String[] campos = new String[]{"Atividades","MET","_idCategoria"};
        String where = "_id = ?";
        String[] argumento = new String[]{Integer.toString(atividadesRealizadas.get(position).getIdAtividade())};
        List<List<String>> atividade = dbGeneric.buscar(tabela,campos,where,argumento);
        viewHolder.tvAtividade.setText(atividade.get(0).get(0));
        long inicio,fim;
        inicio = atividadesRealizadas.get(position).getHoraInicio();
        fim = atividadesRealizadas.get(position).getHoraFim();
        viewHolder.tvHoraInicio.setText(context.getResources().getString(R.string.fragment_inicio_listview_tvhorainicio_text)+"\n"+ManipuladorDataTempo.tempoIntToTempoString(inicio));
        viewHolder.tvHoraFim.setText(context.getResources().getString(R.string.fragment_inicio_listview_tvhoratermino_text)+"\n"+ManipuladorDataTempo.tempoIntToTempoString(fim));
        double tempoTotal = ManipuladorDataTempo.horas(fim) - ManipuladorDataTempo.horas(inicio);
        viewHolder.tvTempoTotal.setText(context.getResources().getString(R.string.fragment_inicio_listview_tvtempototal_text) +"\n"+Double.toString(FormatNum.casasDecimais(tempoTotal,2)));
        double calorias = tempoTotal * usuario.getMassaCorporal() * Double.parseDouble(atividade.get(0).get(1));
        viewHolder.tvGastoCalorico.setText(context.getResources().getString(R.string.fragment_inicio_listview_tvgastocalorico_text)+Double.toString(FormatNum.casasDecimais(calorias,2)));
        gastoEnergetico.setCalorias(gastoEnergetico.getCalorias()+calorias);
        int idCat = Integer.parseInt(atividade.get(0).get(2));
        Resources resources = context.getResources();
        TypedArray typedArray = resources.obtainTypedArray(R.array.imagemListCategorias);
        Drawable drawable;
        if(idCat == 8)
            drawable = typedArray.getDrawable(idCat);
        else if(idCat == 9)
            drawable = typedArray.getDrawable(idCat-2);
        else
            drawable = typedArray.getDrawable(idCat-1);
        viewHolder.imvImagemCategorias.setImageDrawable(drawable);

        viewHolder.ibtnApagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AtividadesRealizadas atividadesRealizada = atividadesRealizadas.get(position);
                atividadesRealizada.setIdAtividade(7030);
                dbGeneric.deletar("AtividadesRealizadas","_id = ?",new String[]{atividadesRealizada.getIdString()});
                AtividadeRealizadaCriador criador = new AtividadeRealizadaCriador(view.getContext(),atividadesRealizada.getDia(),atividadesRealizada);
                List<AtividadesRealizadas> atividadesRealizadasList = TelaPrincipal.buscarAtividadesRealizadas(atividadesRealizada.getDia(), usuario,context);
                new AtualizadorLista(view.getContext(),atividadesRealizada.getDia(),usuario,atividadesRealizadasList,parent);
            }
        });
        viewHolder.ibtnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DialogNovaAtividade(view.getContext(),usuario,atividadesRealizadas.get(position).getDia(),parent,atividadesRealizadas.get(position).getHoraInicio(),atividadesRealizadas.get(position).getHoraFim(),atividadesRealizadas.get(position).getIdAtividade(),atividadesRealizadas.get(position).getId());
            }
        });


    }

    @Override
    public int getItemCount() {
        return atividadesRealizadas.size();
    }
}
class AtividadesViewHolder extends RecyclerView.ViewHolder{
    TextView tvAtividade;
    TextView tvHoraInicio;
    TextView tvHoraFim;
    TextView tvTempoTotal;
    TextView tvGastoCalorico;
    ImageButton ibtnEditar;
    ImageButton ibtnApagar;
    ImageView imvImagemCategorias;

    public AtividadesViewHolder(View view) {
        super(view);
        tvAtividade = view.findViewById(R.id.tvAtividade);
        tvHoraInicio = view.findViewById(R.id.tvHoraInicio);
        tvHoraFim = view.findViewById(R.id.tvHoraFim);
        tvTempoTotal= view.findViewById(R.id.tvTempoTotal);
        tvGastoCalorico = view.findViewById(R.id.tvGastoCalorico);
        ibtnEditar = view.findViewById(R.id.ibtnEditar);
        ibtnApagar = view.findViewById(R.id.ibtnApagar);
        imvImagemCategorias = view.findViewById(R.id.imvImagemCategorias);
    }
}