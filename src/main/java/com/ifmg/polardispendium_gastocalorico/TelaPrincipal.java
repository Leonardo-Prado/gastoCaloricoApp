package com.ifmg.polardispendium_gastocalorico;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;


import org.xml.sax.SAXException;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import database.CarregarXML;
import database.DBGeneric;
import nucleo.AtividadesRealizadas;
import nucleo.GastoEnergetico;
import nucleo.ListViewItensAdapter;
import nucleo.SpAdapter;
import objetos_auxiliares.FormatNum;
import objetos_auxiliares.ManipuladorDataTempo;
import nucleo.Usuario;

public class TelaPrincipal extends AppCompatActivity {

    private Canvas canvasGrafico;
    private Bitmap bitmapGrafico;
    private EditText edData;
    private ImageButton ibtnAdicionar;
    private ImageView imageViewGrafico;
    private DBGeneric dbGeneric;
    private Usuario usuario;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_tela_principal);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            //inicializa variaveis
            setDbGeneric(new DBGeneric(this));//Cria instancia do manipulador de banco de dados
            usuario = new Usuario();//inicializa usuario
            final ManipuladorDataTempo dataTempo = new ManipuladorDataTempo(new Date());//Cria o manipulador de datas e tempo
            //busca um usuario no banco de dados TODO: trocar por verificar se logado quando implementado sistema de login
            List<List<String>> listUsuarios = getDbGeneric().buscar("Usuarios",new String[]{"_id","MassaCorporal","Nome"});
            if(listUsuarios.size()==0) {              //testa se o usuario existe
                LayoutInflater inflater = (LayoutInflater) this.getSystemService(this.LAYOUT_INFLATER_SERVICE); //infla layout criar usuario
                View view = inflater.inflate(R.layout.login, null);//TODO Subestituir toda essa logica por chamar activity_login quando implementada
                final EditText edNome = (EditText)view.findViewById(R.id.edNomeUsuario);
                final EditText edPeso = (EditText)view.findViewById(R.id.edPeso);
                Button btnCriarUsuario = (Button)view.findViewById(R.id.btnCriarUsuario);
                //cria alrta dialog para criar usuario
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Criar usuarios");
                builder.setView(view);
                final AlertDialog alertDialog = builder.create();
                //implementa listener de click do botao criar usuario
                btnCriarUsuario.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!edNome.getText().toString().isEmpty() && !edPeso.getText().toString().isEmpty()) {
                            usuario.setNome(edNome.getText().toString());
                            usuario.setMassaCorporal(Integer.parseInt(edPeso.getText().toString()));
                            ContentValues values = new ContentValues();
                            values.put("Nome", usuario.getNome());
                            values.put("MassaCorporal", usuario.getMassaCorporal());
                            usuario.setId(dbGeneric.inserir(values, "Usuarios"));
                            recreate();
                            alertDialog.dismiss();
                        }else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            builder.setTitle("Campos necessarios vazios");
                            builder.setMessage("Os campos Nome de Usuario e Massa Corporal devem ser preenchido");
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //fecha dialog
                                    dialog.dismiss();
                                }
                            });
                            //mostra dialog
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    }
                });
                alertDialog.show();


            }else {//se usuario logado implementa logica principal do app
                //pega dados do usuario logado
                usuario.setId(Integer.parseInt(listUsuarios.get(0).get(0)));
                usuario.setMassaCorporal(Double.parseDouble(listUsuarios.get(0).get(1)));
                usuario.setNome(listUsuarios.get(0).get(2));

                setEdData((EditText) findViewById(R.id.edData));//define edittext data
                //define onclick listener do edData para gerar uma instancia do calendario e pegar a data
                getEdData().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Calendar calendario = Calendar.getInstance();
                        int dia = calendario.get(Calendar.DAY_OF_MONTH);
                        int mes = calendario.get(Calendar.MONTH);
                        int ano = calendario.get(Calendar.YEAR);
                        DatePickerDialog datePickerDialog;
                        datePickerDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int selectedAno, int selectedMes, int selectedDia) {
                                try {
                                    selectedMes = selectedMes + 1;
                                    getEdData().setText(selectedDia + "-" + selectedMes + "-" + selectedAno);
                                    List<AtividadesRealizadas> atividadesRealizadas = buscarAtividadesRealizadas(ManipuladorDataTempo.dataStringToDataInt(getEdData().getText().toString()), getUsuario());
                                    atualizarListView(atividadesRealizadas, getUsuario());
                                } catch (Exception e) {
                                    Log.e("erro ao pegar data", e.getMessage());
                                }
                            }
                        }, ano, mes, dia);
                        datePickerDialog.setTitle("Escolha o dia");
                        datePickerDialog.show();
                    }
                });
                //define variaveis
                setIbtnAdicionar((ImageButton) findViewById(R.id.ibtnAdicionar));
                setImageViewGrafico((ImageView) findViewById(R.id.ivGrafico));
                criarDataBases();//Se não existir dados referentes a categorias e atividades fisicas cria apartir do xml
                getEdData().setText(dataTempo.getDataString());//Passa a data atual para o edittext edData
                List<AtividadesRealizadas> atividadesRealizadas = buscarAtividadesRealizadas(dataTempo.getDataInt(), usuario); //busca no banco de dados as atividades realizadas na data marcada e cria uma lista de atividades realizadas atraves do metodo
                if (atividadesRealizadas.size() > 0) {//Se existir atividades realizadas para a data passa as mesmas para o listview
                    atualizarListView(atividadesRealizadas, usuario);//chama metodo para criar as listas de atividades realizadas
                }
                getEdData().setInputType(InputType.TYPE_NULL);//desabilita o teclado do edData

                //listeners do botão adicionar atividade
                getIbtnAdicionar().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            dialogInserirAtividadesRealizadas(ManipuladorDataTempo.dataStringToDataInt(edData.getText().toString()));//chama metodo para criar dialog para criar nova atividade
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            //pega as falhas
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //metodo par atualizar a listview com as atividade realizadas
    private void atualizarListView(List<AtividadesRealizadas> atividadesRealizadas, Usuario usuario) {
        try {
            Long data = ManipuladorDataTempo.dataStringToDataInt(getEdData().getText().toString());//pega a data do edData
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
            if (s.size() > 0) {//se existir gastos nessa data atualiza o banco de dados
                ContentValues c = new ContentValues();
                c.put("GastoCalorico", gastoEnergetico.getCalorias());
                getDbGeneric().atualizar("GastoEnergetico", c, "_id = ?", new String[]{s.get(0).get(0)});
            } else {//se não existir gasto calorico na data cria um.
                ContentValues c = new ContentValues();
                c.put("GastoCalorico", gastoEnergetico.getCalorias());
                c.put("Data", gastoEnergetico.getData());
                c.put("_idUsuario", gastoEnergetico.getIdUsuario());
                getDbGeneric().inserir(c, "GastoEnergetico");
            }
            //cria uma nova instancia da listview para as atividades realizadas na data
            ListView listView = (ListView) findViewById(R.id.lvListaAtividadesDoDia);//cria o listview
            ArrayAdapter listViewItensAdapter = new ListViewItensAdapter(this, atividadesRealizadas, usuario);//cria o listview adapter para as atividades
            listView.setAdapter(listViewItensAdapter);//passa o adapter para as listviews
            //desenha o grafico com os valores dos ultimos 15 dias de gasto energetico
            desenharGrafico();
        } catch (Exception e) {
            Log.e("erro ao criar listview", "Erro ao criar ");
        }
    }
    //cria dados necessarios na primeiraexecução TODO:passar tarefa para splash screen
    private void criarDataBases() throws ParserConfigurationException, SAXException, IOException {

        try {
            List<List<String>> cat = getDbGeneric().buscar("Categorias", new String[]{"_id"});//busca as categorias no banco de dados

            if (!(cat.size() > 0)) { //se a busca não retornar nada carrega apartir do xml(somente na primeira execução)
                CarregarXML carregarXML = new CarregarXML();
                List<List<String>> strings = carregarXML.XML(this, "Categorias.xml", "Categorias");
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
                List<List<String>> strings = carregarXML.XML(this, "AtividadesFisicas.xml", "Atividade");
                if (strings.size() > 0) {
                    //para cada atividade na lista adiciona a atividade no banco de dados
                    for (List<String> list : strings
                            ) {
                        ContentValues values = new ContentValues();
                        values.put("_id", list.get(0));
                        values.put("MET", list.get(1));
                        List<List<String>> l = getDbGeneric().buscar("Categorias", new String[]{"_id"}, "Categoria = ?", new String[]{list.get(2).toString()});
                        if (l.size() > 0)
                            values.put("_idCategoria", Integer.parseInt(l.get(0).get(0)));
                        else
                            list.get(0);
                        values.put("Atividades", list.get(3));
                        getDbGeneric().inserir(values, "AtividadesFisicas");
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Usuario getUsuario() {
        //TODO: Implementar forma de pegar usuario;
        return usuario;
    }

    //busca listas de atividades realizadas na data pelo usuario
    private List<AtividadesRealizadas> buscarAtividadesRealizadas(Long dateInt, Usuario usuario) {
        List<AtividadesRealizadas> atividadesRealizadas = new ArrayList<>();
        try {
            String tabela = "AtividadesRealizadas";
            String[] campos = new String[]{"_id", "_idAtividade", "Data", "HoraInicio", "HoraFim", "_idUsuario"};
            List<List<String>> list = getDbGeneric().buscar(tabela, campos, "Data = ? and _idUsuario = ?", new String[]{Long.toString(dateInt), Integer.toString(usuario.getId())}, "HoraInicio ASC");
            if (list.size() > 0) {//Se a busca retornar alguma coisa retorna isso para quem chamou o metodo
                for (List<String> s : list) {
                    AtividadesRealizadas realizadas = new AtividadesRealizadas();
                    realizadas.setId(Integer.parseInt(s.get(0)));
                    realizadas.setIdAtividade(Integer.parseInt(s.get(1)));
                    realizadas.setDia(Long.parseLong(s.get(2)));
                    realizadas.setHoraInicio(Long.parseLong(s.get(3)));
                    realizadas.setHoraFim(Long.parseLong(s.get(4)));
                    realizadas.setIdUsuario(Integer.parseInt(s.get(5)));
                    atividadesRealizadas.add(realizadas);
                }

            } else {//se a busca não retornar nada cria uma atividade com 24h dormindo para quem chamou o metodo(primeira chamada para a data)
                AtividadesRealizadas realizadas = new AtividadesRealizadas();
                realizadas.setDia(dateInt);
                realizadas.setIdUsuario(usuario.getId());
                realizadas.setIdAtividade(7030);//TODO: passar como parametro id de repouso
                realizadas.setHoraInicio(ManipuladorDataTempo.tempoStringToTempoInt("00:00"));
                realizadas.setHoraFim(ManipuladorDataTempo.tempoStringToTempoInt("23:59"));
                ContentValues values = new ContentValues();
                values.put("_idAtividade", realizadas.getIdAtividade());
                values.put("Data", realizadas.getDia());
                values.put("HoraInicio", realizadas.getHoraInicio());
                values.put("HoraFim", realizadas.getHoraFim());
                values.put("_idUsuario", realizadas.getIdUsuario());
                realizadas.setId(getDbGeneric().inserir(values, tabela));
                atividadesRealizadas.add(realizadas);

            }

        } catch (Exception e) {
            Log.e("erro AtivRealizada", e.getMessage());

        }
        return atividadesRealizadas;
    }

    //Desenha o grafico com o gasto calorico do usuario nos ultimos 15 dias
    private void desenharGrafico() {
        try {
            Display display = getWindowManager().getDefaultDisplay(); //pega diversas informações do display, entre elas o tamanho
            Point size = new Point(); //Cria um objeto ponto
            display.getSize(size); //passa os valores de tamnho do display para o objeto size(ponto)
            View graficoConteiner = findViewById(R.id.graficoContainer);//pega o relative layout que contem o canvas para pegar a altura
            //graficoConteiner.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,size.y/2));
            int largura = size.x; // define a largura para ser usado no bitmap, da largura da tela
            int altura = graficoConteiner.getLayoutParams().height - graficoConteiner.getPaddingBottom();//pega a altura real do relative layout
            setBitmapGrafico(Bitmap.createBitmap(largura, altura, Bitmap.Config.ARGB_8888)); //gera bitmap
            getImageViewGrafico().setImageBitmap(getBitmapGrafico());//passa o bitmap para a imageview
            setCanvasGrafico(new Canvas(getBitmapGrafico())); // cria o canvas no bitmap
            getCanvasGrafico().drawColor(ResourcesCompat.getColor(getResources(), R.color.colorGraficoBackground, null)); //define a cor de preenchimento do canvas
            Long dia = ManipuladorDataTempo.tempoStringToTempoInt("24:00");
            ManipuladorDataTempo dataTempo = new ManipuladorDataTempo(new Date());
            List<List<String>> s = getDbGeneric().buscar("GastoEnergetico", new String[]{"Data", "GastoCalorico"}, "Data >= ? and Data <= ? and _idUsuario = ?", new String[]{Long.toString(dataTempo.getDataInt() - dia * 15), Long.toString(dataTempo.getDataInt()), Integer.toString(getUsuario().getId())}, "Data ASC");
            int dias = s.size();
            Point ponto = new Point();
            int numeroX = largura / (dias + 1);
            if (dias > 0) {
                int i = 0;
                while (i < dias) {
                    int x = ponto.x;
                    int y = ponto.y;
                    ponto.x = (i + 1) * numeroX;
                    Float f = Float.parseFloat(s.get(i).get(1));
                    ponto.y = altura - (Math.round(f / 1000) * altura) / 10;
                    Paint paint = new Paint();
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(getResources().getColor(R.color.pontosDoGrafico));
                    paint.setTextSize(18);
                    getCanvasGrafico().drawCircle(ponto.x, ponto.y, 15, paint);
                    paint.setColor(Color.WHITE);
                    getCanvasGrafico().save();
                    getCanvasGrafico().drawText(Double.toString(FormatNum.casasDecimais(Double.parseDouble(s.get(i).get(1)), 2)), ponto.x - 10, ponto.y - 20, paint);
                    getCanvasGrafico().drawText(ManipuladorDataTempo.dataIntToDataString(Long.parseLong(s.get(i).get(0)),"dd-MM"), ponto.x-15, altura - 15, paint);
                    getCanvasGrafico().restore();
                    if (i > 0) {
                        paint.setColor(Color.WHITE);
                        paint.setStrokeWidth(1f);
                        getCanvasGrafico().drawLine(x, y, ponto.x, ponto.y, paint);
                    }
                    if(i==s.size()-1){
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setColor(getResources().getColor(R.color.pontosDoGrafico));
                        getCanvasGrafico().drawCircle(ponto.x, ponto.y, 25, paint);
                    }

                    i++;
                }
            }

        } catch (Exception e) {

        }

    }

    private void dialogInserirAtividadesRealizadas(final Long data) {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.content_dialog_nova_atividade, null);
        final EditText edHoraInicio = (EditText) view.findViewById(R.id.edHoraInicio);
        edHoraInicio.setInputType(InputType.TYPE_NULL);
        final EditText edHorafim = (EditText) view.findViewById(R.id.edHorafim);
        edHorafim.setInputType(InputType.TYPE_NULL);
        final Spinner spCategoria = (Spinner) view.findViewById(R.id.spCategoria);
        final Spinner spAtividade = (Spinner) view.findViewById(R.id.spAtividade);
        Button btnConfirmar = (Button) view.findViewById(R.id.btnConfirmar);
        List<List<String>> list = getDbGeneric().buscar("Categorias", new String[]{"_id", "Categoria", "Descricao"});
        List<String> strings = new ArrayList<>();
        for (List<String> s : list
                ) {
            strings.add(s.get(1));
        }
        SpAdapter spAdapter = new SpAdapter(this, R.layout.support_simple_spinner_dropdown_item, strings);
        spCategoria.setAdapter(spAdapter);
        list = getDbGeneric().buscar("AtividadesFisicas", new String[]{"_id", "Atividades"}, "_idCategoria = ?", new String[]{list.get(0).get(0)});
        strings = new ArrayList<>();
        for (List<String> s : list
                ) {
            strings.add(s.get(1));
        }
        spAdapter = new SpAdapter(this, R.layout.support_simple_spinner_dropdown_item, strings);
        spAtividade.setAdapter(spAdapter);
        spCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                List<List<String>> list = getDbGeneric().buscar("Categorias", new String[]{"_id"}, "Categoria = ?", new String[]{adapterView.getItemAtPosition(i).toString()});
                list = getDbGeneric().buscar("AtividadesFisicas", new String[]{"_id", "Atividades"}, "_idCategoria = ?", new String[]{list.get(0).get(0)});
                List<String> strings = new ArrayList<>();
                for (List<String> s : list
                        ) {
                    strings.add(s.get(1));
                }
                SpAdapter spAdapter = new SpAdapter(view.getContext(), R.layout.support_simple_spinner_dropdown_item, strings);
                spAtividade.setAdapter(spAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(this.getString(R.string.tituloDialog));
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
                            adicionarNovaAtividadeRealizada(atividadesRealizadas);
                            List<AtividadesRealizadas> atividadesRealizadasList = buscarAtividadesRealizadas(data, getUsuario());
                            atualizarListView(atividadesRealizadasList, getUsuario());
                            alerta.dismiss();
                        }else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            builder.setTitle("Valores inadequados");
                            builder.setMessage("O tempo inicial deve ser menor que a tempo final");
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Campos necessarios vazios");
                    builder.setMessage("Os campos hora inicial e final devem ser preenchido");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });


        alerta.show();
    }

    private void adicionarNovaAtividadeRealizada(AtividadesRealizadas atividadesRealizadas) throws ParseException {
        try {
            atividadesRealizadas.setDia(ManipuladorDataTempo.dataStringToDataInt(getEdData().getText().toString()));
            List<AtividadesRealizadas> atividadesRealizadasList = new ArrayList<>();
            List<List<String>> listDeListString = new DBGeneric(this).buscar("AtividadesRealizadas", new String[]{"_id", "_idAtividade", "HoraInicio", "HoraFim"}, "_idUsuario = ? and Data = ?", new String[]{Integer.toString(atividadesRealizadas.getIdUsuario()), Long.toString(atividadesRealizadas.getDia())});
            if (listDeListString.size() > 0) {
                for (List<String> s : listDeListString
                        ) {
                    AtividadesRealizadas realizadas = new AtividadesRealizadas();
                    AtividadesRealizadas realizadas2 = new AtividadesRealizadas();
                    Long inicio = Long.parseLong(s.get(2));
                    Long fim = Long.parseLong(s.get(3));
                    if (inicio <= atividadesRealizadas.getHoraInicio() && fim >= atividadesRealizadas.getHoraFim()) {
                        realizadas.setHoraInicio(inicio);
                        realizadas.setHoraFim(atividadesRealizadas.getHoraInicio());
                        realizadas.setDia(atividadesRealizadas.getDia());
                        realizadas.setId(Integer.parseInt(s.get(0)));
                        realizadas.setIdUsuario(atividadesRealizadas.getIdUsuario());
                        realizadas.setIdAtividade(Integer.parseInt(s.get(1)));
                        realizadas2.setHoraInicio(atividadesRealizadas.getHoraFim());
                        realizadas2.setHoraFim(fim);
                        realizadas2.setDia(atividadesRealizadas.getDia());
                        realizadas2.setId(Integer.parseInt(s.get(0)));
                        realizadas2.setIdUsuario(atividadesRealizadas.getIdUsuario());
                        realizadas2.setIdAtividade(Integer.parseInt(s.get(1)));
                        ContentValues values = new ContentValues();
                        values.put("HoraInicio", realizadas.getHoraInicio());
                        values.put("HoraFim", realizadas.getHoraFim());
                        getDbGeneric().atualizar("AtividadesRealizadas", values, "_id = ?", new String[]{realizadas.getIdString()});
                        values = new ContentValues();
                        values.put("HoraInicio", realizadas2.getHoraInicio());
                        values.put("HoraFim", realizadas2.getHoraFim());
                        values.put("_idAtividade", realizadas2.getIdAtividade());
                        values.put("_idUsuario", realizadas2.getIdUsuario());
                        values.put("Data", realizadas2.getDia());
                        getDbGeneric().inserir(values, "AtividadesRealizadas");
                    } else if (inicio >= atividadesRealizadas.getHoraInicio() && inicio <= atividadesRealizadas.getHoraFim() && fim >= atividadesRealizadas.getHoraFim()) {
                        realizadas.setHoraInicio(atividadesRealizadas.getHoraFim());
                        realizadas.setHoraFim(fim);
                        realizadas.setDia(atividadesRealizadas.getDia());
                        realizadas.setId(Integer.parseInt(s.get(0)));
                        realizadas.setIdUsuario(atividadesRealizadas.getIdUsuario());
                        realizadas.setIdAtividade(Integer.parseInt(s.get(1)));
                        ContentValues values = new ContentValues();
                        values.put("HoraInicio", realizadas.getHoraInicio());
                        values.put("HoraFim", realizadas.getHoraFim());
                        getDbGeneric().atualizar("AtividadesRealizadas", values, "_id = ?", new String[]{realizadas.getIdString()});

                    } else if (inicio <= atividadesRealizadas.getHoraInicio() && fim >= atividadesRealizadas.getHoraInicio() && fim <= atividadesRealizadas.getHoraFim()) {

                        realizadas.setHoraInicio(inicio);
                        realizadas.setHoraFim(atividadesRealizadas.getHoraInicio());
                        realizadas.setDia(atividadesRealizadas.getDia());
                        realizadas.setId(Integer.parseInt(s.get(0)));
                        realizadas.setIdUsuario(atividadesRealizadas.getIdUsuario());
                        realizadas.setIdAtividade(Integer.parseInt(s.get(1)));
                        ContentValues values = new ContentValues();
                        values.put("HoraInicio", realizadas.getHoraInicio());
                        values.put("HoraFim", realizadas.getHoraFim());
                        getDbGeneric().atualizar("AtividadesRealizadas", values, "_id = ?", new String[]{realizadas.getIdString()});
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
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tela_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public Canvas getCanvasGrafico() {
        return canvasGrafico;
    }

    public void setCanvasGrafico(Canvas canvasGrafico) {
        this.canvasGrafico = canvasGrafico;
    }

    public Bitmap getBitmapGrafico() {
        return bitmapGrafico;
    }

    public void setBitmapGrafico(Bitmap bitmapGrafico) {
        this.bitmapGrafico = bitmapGrafico;
    }

    public EditText getEdData() {
        return edData;
    }

    public void setEdData(EditText edData) {
        this.edData = edData;
    }

    public ImageButton getIbtnAdicionar() {
        return ibtnAdicionar;
    }

    public void setIbtnAdicionar(ImageButton ibtnAdicionar) {
        this.ibtnAdicionar = ibtnAdicionar;
    }

    public ImageView getImageViewGrafico() {
        return imageViewGrafico;
    }

    public void setImageViewGrafico(ImageView imageViewGrafico) {
        this.imageViewGrafico = imageViewGrafico;
    }

    public DBGeneric getDbGeneric() {
        return dbGeneric;
    }

    public void setDbGeneric(DBGeneric dbGeneric) {
        this.dbGeneric = dbGeneric;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
