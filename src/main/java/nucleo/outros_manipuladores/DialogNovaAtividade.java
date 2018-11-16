package nucleo.outros_manipuladores;

import android.app.TimePickerDialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.ifmg.polardispendium_gastocalorico.R;
import com.ifmg.polardispendium_gastocalorico.TelaPrincipal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import database.DBGeneric;
import nucleo.entidades_do_nucleo.AtividadesRealizadas;
import nucleo.entidades_do_nucleo.Usuario;
import nucleo.listviews_manipuladores.AtualizadorLista;
import nucleo.outros_manipuladores.AtividadeRealizadaCriador;
import nucleo.spinners_manipuladores.SpAdapter;
import objetos_auxiliares.DialogConstrutor;
import objetos_auxiliares.ManipuladorDataTempo;

public class DialogNovaAtividade {

    private Context context;
    private DBGeneric dbGeneric;
    private Usuario usuario;
    private Long data;
    private View layoutPai;

    public DialogNovaAtividade(Context context, Usuario usuario, Long data,View view) {
        this.context = context;
        this.usuario = usuario;
        this.data = data;
        this.layoutPai = view;
        dbGeneric = new DBGeneric(getContext());
        dialogInserirAtividadesRealizadas(this.data);
    }


    private void dialogInserirAtividadesRealizadas(final Long data) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.content_dialog_nova_atividade, null);
        final EditText edHoraInicio = (EditText) view.findViewById(R.id.edHoraInicio);
        edHoraInicio.setInputType(InputType.TYPE_NULL);
        final EditText edHorafim = (EditText) view.findViewById(R.id.edHorafim);
        edHorafim.setInputType(InputType.TYPE_NULL);
        final AppCompatAutoCompleteTextView acAtividade = (AppCompatAutoCompleteTextView) view.findViewById(R.id.acAtividade);
        final Spinner spCategoria = (Spinner) view.findViewById(R.id.spCategoria);
        final Spinner spAtividade = (Spinner) view.findViewById(R.id.spAtividade);


        Button btnConfirmar = (Button) view.findViewById(R.id.btnConfirmar);
        List<List<String>> list = getDbGeneric().buscar("Categorias", new String[]{"_id", "Categoria", "Descricao"},"Categoria ASC");
        List<String> strings = new ArrayList<>();
        for (List<String> s : list
                ) {
            strings.add(s.get(1));
        }
        SpAdapter spAdapter = new SpAdapter(getContext(), R.layout.support_simple_spinner_dropdown_item, strings);
        spCategoria.setAdapter(spAdapter);
        list = getDbGeneric().buscar("AtividadesFisicas", new String[]{"_id", "Atividades"}, "_idCategoria = ?", new String[]{list.get(0).get(0)},"Atividades ASC");
        strings = new ArrayList<>();
        for (List<String> s : list
                ) {
            strings.add(s.get(1));
        }
        spAdapter = new SpAdapter(getContext(), R.layout.support_simple_spinner_dropdown_item, strings);
        spAtividade.setAdapter(spAdapter);
        list = getDbGeneric().buscar("AtividadesFisicas", new String[]{ "Atividades"}, "Atividades ASC");
        strings = new ArrayList<>();
        for (List<String> s : list) {strings.add(s.get(0));}
        ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(), R.layout.autocomplete_aux_layout,R.id.item, strings);
        acAtividade.setThreshold(1);
        acAtividade.setAdapter(adapter);

        acAtividade.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    List<List<String>> list = getDbGeneric().buscar("AtividadesFisicas", new String[]{"_idCategoria,_id,Atividades"}, "Atividades = ?", new String[]{adapterView.getItemAtPosition(i).toString()});
                    if (list.size() > 0) {
                        String categoria = getDbGeneric().buscar("Categorias", new String[]{"Categoria"}, "_id = ?", new String[]{list.get(0).get(0)}).get(0).get(0);
                        int j = 0;
                        while (j < spCategoria.getAdapter().getCount()) {
                            String s = spCategoria.getAdapter().getItem(j).toString();
                            if (s.equalsIgnoreCase(categoria)) {
                                spCategoria.setSelection(j);
                                break;
                            }
                            j++;
                        }
                        List<List<String>> list1 = getDbGeneric().buscar("Categorias", new String[]{"_id"}, "Categoria = ?", new String[]{categoria});
                        list1 = getDbGeneric().buscar("AtividadesFisicas", new String[]{"_id", "Atividades"}, "_idCategoria = ?", new String[]{list1.get(0).get(0)}, "Atividades ASC");
                        List<String> strings = new ArrayList<>();
                        for (List<String> s : list1) {
                            strings.add(s.get(1));
                        }
                        SpAdapter spAdapter = new SpAdapter(view.getContext(), R.layout.support_simple_spinner_dropdown_item, strings);
                        spAtividade.setAdapter(spAdapter);
                        j = 0;
                        while (j < spAtividade.getAdapter().getCount()) {
                            if (spAtividade.getAdapter().getItem(j).toString().equalsIgnoreCase(list.get(0).get(2))) {
                                spAtividade.setSelection(j);
                                break;
                            }
                            j++;
                        }
                    } else {
                        new DialogConstrutor(view.getContext(), "Item selecionado invalido", "Item selecionado invalido", "ok");
                    }

                }catch (Exception e){
                    Log.e("erro listener ac",e.getMessage());
                }
            }
        });

        spCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                List<List<String>> list = getDbGeneric().buscar("Categorias", new String[]{"_id"}, "Categoria = ?", new String[]{adapterView.getItemAtPosition(i).toString()});
                list = getDbGeneric().buscar("AtividadesFisicas", new String[]{"_id", "Atividades"}, "_idCategoria = ?", new String[]{list.get(0).get(0)},"Atividades ASC");
                List<String> strings = new ArrayList<>();
                for (List<String> s : list) {strings.add(s.get(1));}
                SpAdapter spAdapter = new SpAdapter(view.getContext(), R.layout.support_simple_spinner_dropdown_item, strings);
                spAtividade.setAdapter(spAdapter);
                if(!acAtividade.getText().toString().isEmpty()){
                    int j = 0;
                    String string = acAtividade.getText().toString();
                    while (j < spAtividade.getAdapter().getCount()) {
                        if (spAtividade.getAdapter().getItem(j).toString().equalsIgnoreCase(string)) {
                            spAtividade.setSelection(j);
                            break;
                        }
                        j++;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {            }
        });
        edHoraInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendario = Calendar.getInstance();
                final int hora = calendario.get(Calendar.HOUR_OF_DAY);
                int minuto = calendario.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog;
                timePickerDialog = new TimePickerDialog(view.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String horaString;
                        String minutoString;
                        if (selectedHour <= 9)
                            horaString = "0" + Integer.toString(selectedHour);
                        else
                            horaString = Integer.toString(selectedHour);
                        if (selectedMinute <= 9)
                            minutoString = "0" + Integer.toString(selectedMinute);
                        else
                            minutoString = Integer.toString(selectedMinute);
                        edHoraInicio.setText(horaString + ":" + minutoString);
                    }
                }, hora, minuto, true);
                timePickerDialog.setTitle("Inicio");
                timePickerDialog.show();
            }
        });
        edHorafim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendario = Calendar.getInstance();
                int hora = calendario.get(Calendar.HOUR_OF_DAY);
                int minuto = calendario.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog;
                timePickerDialog = new TimePickerDialog(view.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String horaString;
                        String minutoString;
                        if (selectedHour <= 9)
                            horaString = "0" + Integer.toString(selectedHour);
                        else
                            horaString = Integer.toString(selectedHour);
                        if (selectedMinute <= 9)
                            minutoString = "0" + Integer.toString(selectedMinute);
                        else
                            minutoString = Integer.toString(selectedMinute);
                        edHorafim.setText(horaString + ":" + minutoString);
                    }
                }, hora, minuto, true);
                timePickerDialog.setTitle("Inicio");
                timePickerDialog.show();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.tituloDialog));
        builder.setView(view);
        final AlertDialog alerta = builder.create();
        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edHoraInicio.getText().toString().isEmpty() && !edHorafim.getText().toString().isEmpty()) {
                    try {
                        if(ManipuladorDataTempo.tempoStringToTempoInt(edHoraInicio.getText().toString())< ManipuladorDataTempo.tempoStringToTempoInt(edHorafim.getText().toString())) {

                            AtividadesRealizadas atividadesRealizadas = new AtividadesRealizadas();
                            atividadesRealizadas.setHoraInicio(ManipuladorDataTempo.tempoStringToTempoInt(edHoraInicio.getText().toString()));
                            atividadesRealizadas.setHoraFim(ManipuladorDataTempo.tempoStringToTempoInt(edHorafim.getText().toString()));
                            atividadesRealizadas.setIdAtividade(Integer.parseInt(getDbGeneric().buscar("AtividadesFisicas", new String[]{"_id"}, "Atividades = ?", new String[]{spAtividade.getSelectedItem().toString()}).get(0).get(0)));
                            atividadesRealizadas.setIdCategotia(Integer.parseInt(getDbGeneric().buscar("Categorias", new String[]{"_id"}, "Categoria = ?", new String[]{spCategoria.getSelectedItem().toString()}).get(0).get(0)));
                            atividadesRealizadas.setIdUsuario(getUsuario().getId());
                            AtividadeRealizadaCriador atividadeRealizadaCriador = new AtividadeRealizadaCriador(getContext(),getData(),atividadesRealizadas);
                            List<AtividadesRealizadas> atividadesRealizadasList = TelaPrincipal.buscarAtividadesRealizadas(data, getUsuario(),context);
                            new AtualizadorLista(getContext(),getData(),getUsuario(),atividadesRealizadasList,layoutPai);
                            alerta.dismiss();
                        }else {
                            DialogConstrutor dialogConstrutor = new DialogConstrutor(view.getContext(),"Valores inadequados","O tempo inicial deve ser menor que a tempo final","OK");
                        }
                    } catch (Exception e) { e.printStackTrace(); }

                } else {
                    DialogConstrutor dialogConstrutor = new DialogConstrutor(view.getContext(),"Campos necessarios vazios","Os campos hora inicial e final devem ser preenchido","OK");
                }
            }
        });
        alerta.show();
        Window window = alerta.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT );
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
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

    public Long getData() {
        return data;
    }

    public void setData(Long data) {
        this.data = data;
    }
}
