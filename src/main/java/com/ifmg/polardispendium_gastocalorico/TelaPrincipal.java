package com.ifmg.polardispendium_gastocalorico;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;


import org.xml.sax.SAXException;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import database.CarregarXML;
import database.DBGeneric;
import nucleo.entidades_do_nucleo.AtividadesRealizadas;
import objetos_auxiliares.DialogConstrutor;
import objetos_auxiliares.FontsOverride;
import objetos_auxiliares.ManipuladorDataTempo;
import nucleo.entidades_do_nucleo.Usuario;

public class TelaPrincipal extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private Canvas canvasGrafico;
    private Bitmap bitmapGrafico;
    private EditText edData;
    private ImageButton ibtnAdicionar;
    private ImageView imageViewGrafico;
    private DBGeneric dbGeneric;
    private Usuario usuario;
    FloatingActionButton fabAdicionar;
    WindowManager windowManager;
    //TODO: criar area clicavel no canvas para selecionar data.
    //TODO: Criar collapse expand no canvas/imageview para esconder/mostrar
    //TODO: Criar nova view para mostrar grafico de peso. //sidemenu ou barra inferior
    //TODO: Criar opção de duplicar atividade
    //TODO: Criar opção de atualizar o peso//Notificação interna semanal
    //TODO: Criar icone de peso
    //TODO: Criar grafico com acumulados semanais de calorias
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            FontsOverride.setDefaultFont(this, "DEFAULT", "font/century-gothic.ttf");
            FontsOverride.setDefaultFont(this, "MONOSPACE", "font/century-gothic.ttf");
            FontsOverride.setDefaultFont(this, "SERIF", "font/century-gothic.ttf");
            FontsOverride.setDefaultFont(this, "SANS_SERIF", "font/century-gothic.ttf");
            setContentView(R.layout.activity_tela_principal);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            BottomNavigationView bnvMenuInferior = (BottomNavigationView) findViewById(R.id.bnvMenuInferior);
            bnvMenuInferior.setOnNavigationItemSelectedListener(this);

            //inicializa variaveis
            windowManager = getWindowManager();
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
                            try {
                                ManipuladorDataTempo manipuladorDataTempo = new ManipuladorDataTempo(new Date());
                                values = new ContentValues();
                                values.put("Data",manipuladorDataTempo.getDataInt());
                                values.put("Peso",usuario.getMassaCorporal());
                                values.put("_idUsuario",usuario.getId());
                                dbGeneric.inserir(values,"Peso");
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            recreate();
                            alertDialog.dismiss();
                        }else{
                            DialogConstrutor dialogConstrutor =  new DialogConstrutor(view.getContext(),"Campos necessarios vazios","Os campos nomes de usuario e massa corporal devem ser preenchidos","ok");
                        }
                    }
                });
                alertDialog.setCancelable(false);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();


            }else {//se usuario logado implementa logica principal do app
                //pega dados do usuario logado
                usuario.setId(Integer.parseInt(listUsuarios.get(0).get(0)));
                usuario.setMassaCorporal(Double.parseDouble(listUsuarios.get(0).get(1)));
                usuario.setNome(listUsuarios.get(0).get(2));
                Fragment fragmentInicio = FragmentInicio.newInstance(getUsuario(),windowManager);
                openFragment(fragmentInicio);
                criarDataBases();//Se não existir dados referentes a categorias e atividades fisicas cria apartir do xml
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

    //busca listas de atividades realizadas na data pelo usuario
    public static List<AtividadesRealizadas> buscarAtividadesRealizadas(Long dateInt, Usuario usuario, Context context) {
        List<AtividadesRealizadas> atividadesRealizadas = new ArrayList<>();
        try {
            DBGeneric db = new DBGeneric(context);
            String tabela = "AtividadesRealizadas";
            String[] campos = new String[]{"_id", "_idAtividade", "Data", "HoraInicio", "HoraFim", "_idUsuario"};
            List<List<String>> list = db.buscar(tabela, campos, "Data = ? and _idUsuario = ?", new String[]{Long.toString(dateInt), Integer.toString(usuario.getId())}, "HoraInicio ASC");
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
                realizadas.setId(db.inserir(values, tabela));
                atividadesRealizadas.add(realizadas);
            }

        } catch (Exception e) {
            Log.e("erro AtivRealizada", e.getMessage());
        }
        return atividadesRealizadas;
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_home: {
                getSupportActionBar().setTitle("Inicio");
                Fragment fragmentInicio = FragmentInicio.newInstance(getUsuario(),windowManager);
                openFragment(fragmentInicio);
                break;
            }
            case R.id.menu_item_peso: {
                getSupportActionBar().setTitle("Meu Progresso");
                Fragment fragmentPeso = FragmentPeso.newInstance(getUsuario(),windowManager);
                openFragment(fragmentPeso);
                break;
            }
        }
        return true;
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frmContainerFragment, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
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

    private Usuario getUsuario() {
        //TODO: Implementar forma de pegar usuario;
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
