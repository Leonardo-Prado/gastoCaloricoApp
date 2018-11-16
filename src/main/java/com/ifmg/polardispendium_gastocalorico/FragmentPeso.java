package com.ifmg.polardispendium_gastocalorico;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import database.DBGeneric;
import nucleo.entidades_do_nucleo.Usuario;
import objetos_auxiliares.DialogConstrutor;
import objetos_auxiliares.FormatNum;
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
    TextView tvGastoMedio;
    EditText edMeuPeso;
    FloatingActionButton fabAtualizar;
    ImageView ivGrafico;
    View graficoContainer;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final ManipuladorDataTempo dataTempo;
        setDbGeneric(new DBGeneric(getContext()));
        // Inflate the layout for this fragment
        final View inflate = inflater.inflate(R.layout.fragment_peso, container, false);
        //pega variaveis
        tvGastoMedio = (TextView) inflate.findViewById(R.id.tvGastoMedio);
        edMeuPeso = (EditText) inflate.findViewById(R.id.edMeuPeso);
        ivGrafico = (ImageView) inflate.findViewById(R.id.ivGrafico);
        graficoContainer = (View) inflate.findViewById(R.id.graficoContainer);
        fabAtualizar = (FloatingActionButton) inflate.findViewById(R.id.fabAtualizarPeso);

        //Listener Onclick FAB
        fabAtualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    ManipuladorDataTempo manipuladorDataTempo = new ManipuladorDataTempo(new Date());
                    ContentValues values = new ContentValues();
                    values.put("Data",manipuladorDataTempo.getDataInt());
                    values.put("Peso",Integer.parseInt(edMeuPeso.getText().toString()));
                    values.put("_idUsuario",getUsuario().getId());
                    List<List<String>> pesos = dbGeneric.buscar("Peso",new String[]{"_id"},"_idUsuario = ? AND Data = ?",new String[]{Integer.toString(getUsuario().getId()),Long.toString(manipuladorDataTempo.getDataInt())});
                    if(pesos.size()==0)
                        dbGeneric.inserir(values,"Peso");
                    else{
                        values = new ContentValues();
                        values.put("Peso",Integer.parseInt(edMeuPeso.getText().toString()));
                        dbGeneric.atualizar("Peso",values,"_id = ?",new String[]{pesos.get(0).get(0)});
                    }
                    getUsuario().setMassaCorporal(Integer.parseInt(edMeuPeso.getText().toString()));
                    values = new ContentValues();
                    values.put("MassaCorporal",Integer.parseInt(edMeuPeso.getText().toString()));
                    dbGeneric.atualizar("Usuarios",values,"_id = ?",new String[]{Integer.toString(getUsuario().getId())});
                    DialogConstrutor dialogConstrutor = new DialogConstrutor(getContext(),"Peso atualizado","Você atualizou sua massa corporal com sucesso","Ok");
                } catch (Exception e) {
                    e.printStackTrace();
                    DialogConstrutor dialogConstrutor = new DialogConstrutor(getContext(),"Erro ao atualizar o peso","Não foi possivel atualizar a massa corporal","Ok");
                }

            }
        });
        //passa peso atual para o edMeuPeso
        edMeuPeso.setText(Double.toString(getUsuario().getMassaCorporal()));
        //gera media de calorias do usuario e passa para o tvgastomedio
        List<List<String>> gastosCaloricos= getDbGeneric().buscar("GastoEnergetico",new String[]{"GastoCalorico"},"_idUsuario = ?",new String[]{Integer.toString(getUsuario().getId())});
        for (List<String> list:gastosCaloricos
             ) {
            setMediaGastoCalorico(getMediaGastoCalorico()+Double.parseDouble(list.get(0)));
        }
        setMediaGastoCalorico(getMediaGastoCalorico()/gastosCaloricos.size());
        tvGastoMedio.setText(tvGastoMedio.getText().toString() + " "+ Double.toString(FormatNum.casasDecimais(getMediaGastoCalorico(),2)) + " por dia");




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

    public double getMediaGastoCalorico() {
        return mediaGastoCalorico;
    }

    public void setMediaGastoCalorico(double mediaGastoCalorico) {
        this.mediaGastoCalorico = mediaGastoCalorico;
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
