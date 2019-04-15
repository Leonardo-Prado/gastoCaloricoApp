package com.ifmg.polardispendium_gastocalorico;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import database.DBGeneric;
import nucleo.entidades_do_nucleo.Usuario;
import nucleo.graficos.DesenharGraficoPeso;
import objetos_auxiliares.DialogConstrutor;
import objetos_auxiliares.ManipuladorDataTempo;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link //FragmentPeso.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentPeso#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentPeso extends Fragment {

    private Usuario usuario;
    private WindowManager windowManager;
    private TextView tvGastoMedio;
    private EditText edMeuPeso;
    private FloatingActionButton fabAtualizar;
    private ImageView ivGrafico;
    private View graficoContainer;
    private DBGeneric dbGeneric;
    private double mediaGastoCalorico = 0;

    //private OnFragmentInteractionListener mListener;

    public FragmentPeso() {
        // Required empty public constructor
    }

    public static FragmentPeso newInstance(Usuario usuario,WindowManager windowManager) {
        FragmentPeso fragment = new FragmentPeso();
        fragment.setUsuario(usuario);
        fragment.setWindowManager(windowManager);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setDbGeneric(new DBGeneric(getContext()));
        // Inflate the layout for this fragment
        final View inflate = inflater.inflate(R.layout.fragment_peso, container, false);
        //pega variaveis
        //setTvGastoMedio((TextView) inflate.findViewById(R.id.tvGastoMedio));
        setEdMeuPeso((EditText) inflate.findViewById(R.id.edMeuPeso));
        setIvGrafico((ImageView) inflate.findViewById(R.id.ivGraficoPeso));
        setGraficoContainer(inflate.findViewById(R.id.graficoContainerPeso));
        setFabAtualizar((FloatingActionButton) inflate.findViewById(R.id.fabAtualizarPeso));

        //Listener Onclick FAB
        getFabAtualizar().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    ManipuladorDataTempo manipuladorDataTempo = new ManipuladorDataTempo(new Date());
                    ContentValues values = new ContentValues();
                    values.put("Data",manipuladorDataTempo.getDataInt());
                    values.put("Peso",Double.parseDouble(getEdMeuPeso().getText().toString()));
                    values.put("_idUsuario",getUsuario().getId());
                    List<List<String>> pesos = dbGeneric.buscar("Peso",new String[]{"_id"},"_idUsuario = ? AND Data = ?",new String[]{Integer.toString(getUsuario().getId()),Long.toString(manipuladorDataTempo.getDataInt())});
                    if(pesos.size()==0)
                        dbGeneric.inserir(values,"Peso");
                    else{
                        values = new ContentValues();
                        values.put("Peso",Double.parseDouble(getEdMeuPeso().getText().toString()));
                        dbGeneric.atualizar("Peso",values,"_id = ?",new String[]{pesos.get(0).get(0)});

                    }
                    getUsuario().setMassaCorporal(Double.parseDouble(getEdMeuPeso().getText().toString()));
                    values = new ContentValues();
                    values.put("MassaCorporal",Double.parseDouble(getEdMeuPeso().getText().toString()));
                    if(getUsuario().getPesoMinimo()>=getUsuario().getMassaCorporal()){
                        usuario.setPesoMinimo(getUsuario().getMassaCorporal());
                        values.put("PesoMinimo",usuario.getPesoMinimo());
                    }else if(getUsuario().getPesoMaximo()<=getUsuario().getMassaCorporal()){
                        usuario.setPesoMaximo(usuario.getMassaCorporal());
                        values.put("PesoMaximo",usuario.getPesoMaximo());
                    }
                    dbGeneric.atualizar("Usuarios",values,"_id = ?",new String[]{Integer.toString(getUsuario().getId())});
                    new DialogConstrutor(getContext(),"Peso atualizado","Você atualizou sua massa corporal com sucesso","Ok");
                    new DesenharGraficoPeso(getContext(),getIvGrafico(),getGraficoContainer(),getUsuario(),getWindowManager());
                } catch (Exception e) {
                    e.printStackTrace();
                    new DialogConstrutor(getContext(),"Erro ao atualizar o peso","Não foi possivel atualizar a massa corporal","Ok");
                }
                TelaPrincipal.hideKeyboard(getActivity());
            }
        });

        ivGrafico.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() { new DesenharGraficoPeso(getContext(),getIvGrafico(),getGraficoContainer(), getUsuario(), getWindowManager()); }
        });


        //passa peso atual para o edMeuPeso
        getEdMeuPeso().setText(Double.toString(getUsuario().getMassaCorporal()));
        new DesenharGraficoPeso(getContext(),getIvGrafico(),getGraficoContainer(),getUsuario(),getWindowManager());
        return inflate;
    }

    // TODO: Rename method, update argument and hook method into UI event
  /*  public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }*/

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public WindowManager getWindowManager() {
        return windowManager;
    }

    public void setWindowManager(WindowManager windowManager) {
        this.windowManager = windowManager;
    }

    public DBGeneric getDbGeneric() {
        return dbGeneric;
    }

    public void setDbGeneric(DBGeneric dbGeneric) {
        this.dbGeneric = dbGeneric;
    }

    public EditText getEdMeuPeso() {
        return edMeuPeso;
    }

    public void setEdMeuPeso(EditText edMeuPeso) {
        this.edMeuPeso = edMeuPeso;
    }

    public FloatingActionButton getFabAtualizar() {
        return fabAtualizar;
    }

    public void setFabAtualizar(FloatingActionButton fabAtualizar) {
        this.fabAtualizar = fabAtualizar;
    }

    public ImageView getIvGrafico() {
        return ivGrafico;
    }

    public void setIvGrafico(ImageView ivGrafico) {
        this.ivGrafico = ivGrafico;
    }

    public View getGraficoContainer() {
        return graficoContainer;
    }

    public void setGraficoContainer(View graficoContainer) {
        this.graficoContainer = graficoContainer;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
  /*  public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }*/
}
