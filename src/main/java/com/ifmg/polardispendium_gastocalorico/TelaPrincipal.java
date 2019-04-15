package com.ifmg.polardispendium_gastocalorico;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import database.DBGeneric;
import nucleo.entidades_do_nucleo.AtividadesRealizadas;
import nucleo.entidades_do_nucleo.UserPreferences;
import objetos_auxiliares.DialogConstrutor;
import objetos_auxiliares.FontsOverride;
import objetos_auxiliares.ManipuladorDataTempo;
import nucleo.entidades_do_nucleo.Usuario;

public class TelaPrincipal extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private EditText edData;
    private DBGeneric dbGeneric;
    private Usuario usuario;
    private boolean back = false;
    FloatingActionButton fabAdicionar;
    private WindowManager windowManager;
    FragmentSobre fragmentSobre;
    Resources res;

    //TODO: criar area clicavel no canvas para selecionar data.
    //TODO: Criar opção de duplicar atividade
    //TODO: Criar grafico com acumulados semanais de calorias
    //TODO: Criar modo de acompanhamento de atividades fisicas
    //TODO: Criar modo de programação de atividades.
    //TODO: Criar modo de metas
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_tela_principal);
            //define fonts do projeto
            FontsOverride.setDefaultFont(this, "DEFAULT", "font/century-gothic.ttf");
            FontsOverride.setDefaultFont(this, "MONOSPACE", "font/century-gothic.ttf");
            FontsOverride.setDefaultFont(this, "SERIF", "font/century-gothic.ttf");
            FontsOverride.setDefaultFont(this, "SANS_SERIF", "font/century-gothic.ttf");
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            //cria a barra de navegação inferior
            BottomNavigationView bnvMenuInferior = findViewById(R.id.bnvMenuInferior);
            bnvMenuInferior.setOnNavigationItemSelectedListener(this);

            //inicializa variaveis
            windowManager = getWindowManager();
            res = this.getResources();
            setDbGeneric(new DBGeneric(this));//Cria instancia do manipulador de banco de dados
            usuario = new Usuario();//inicializa usuario
            final ManipuladorDataTempo dataTempo = new ManipuladorDataTempo(new Date());//Cria o manipulador de datas e tempo
            //busca um usuario no banco de dados
            final List<List<String>> listUsuarios = getDbGeneric().buscar("Usuarios",new String[]{"_id","MassaCorporal","Nome","Altura"},"Logado = ?",new String[]{"1"});
            if(listUsuarios.size()==0) {              //testa se o usuario está logado
                Intent intent = new Intent(TelaPrincipal.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }else {//se usuario logado implementa logica principal do app
                //pega dados do usuario logado
                if(listUsuarios.get(0).size()<=3){
                    final LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
                    final View dadosDoUsuario = inflater.inflate(R.layout.dialog_dados_do_usuario, null);
                    final EditText edPeso = dadosDoUsuario.findViewById(R.id.edPeso);
                    final EditText edAltura = dadosDoUsuario.findViewById(R.id.edAltura);
                    Button btnDadosDoUsuario = dadosDoUsuario.findViewById(R.id.btnDadoDoUsuario);
                    final DialogConstrutor dialogDadosUsuario = new DialogConstrutor(this,dadosDoUsuario,res.getString(R.string.tela_principal_dialog_dados_do_usuario_titulo),res.getString(R.string.tela_principal_dialog_dados_do_usuario_menssagem));
                    dialogDadosUsuario.getDialog().setCanceledOnTouchOutside(false);
                    dialogDadosUsuario.getDialog().setCancelable(false);
                    btnDadosDoUsuario.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(!edAltura.getText().toString().isEmpty()&&!edPeso.getText().toString().isEmpty()){
                                usuario.setMassaCorporal(edPeso.getText().toString());
                                usuario.setAltura(edAltura.getText().toString());
                                usuario.setNome(listUsuarios.get(0).get(1));
                                usuario.setId(Integer.parseInt(listUsuarios.get(0).get(0)));
                                usuario.setPesoMaximo(usuario.getMassaCorporal());
                                usuario.setPesoMinimo(usuario.getMassaCorporal());
                                ContentValues values = new ContentValues();
                                values.put("MassaCorporal",usuario.getMassaCorporal());
                                values.put("Altura",usuario.getAltura());
                                values.put("PesoMinimo",usuario.getPesoMinimo());
                                values.put("PesoMaximo",usuario.getPesoMaximo());
                                dbGeneric.atualizar("Usuarios",values,"_id = ?",new String[]{listUsuarios.get(0).get(0)});
                                long data = dataTempo.getDataInt();
                                values = new ContentValues();
                                values.put("Data",data);
                                values.put("Peso",usuario.getMassaCorporal());
                                values.put("_idUsuario",usuario.getId());
                                values.put("Inicial",1);
                                dbGeneric.inserir(values,"Peso");
                                dialogDadosUsuario.fechar();
                                recreate();
                            }else{
                                new DialogConstrutor(view.getContext(),res.getString(R.string.tela_principal_dialog_campos_necessarios_titulo),res.getString(R.string.tela_principal_dialog_campos_necessarios_menssagem),res.getString(R.string.dialog_positive_button_texto_padrao));
                            }
                        }
                    });


                }else {
                    usuario.setId(Integer.parseInt(listUsuarios.get(0).get(0)));
                    usuario.setMassaCorporal(Double.parseDouble(listUsuarios.get(0).get(1)));
                    usuario.setNome(listUsuarios.get(0).get(2));
                    usuario.setAltura(Double.parseDouble(listUsuarios.get(0).get(3)));
                    UserPreferences userPreferences = new UserPreferences(this,usuario);
                }
                Fragment fragmentInicio = FragmentInicio.newInstance(usuario,windowManager);
                openFragment(fragmentInicio);
            }
            //pega as falhas
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
                realizadas.setIdAtividade(7030);//id Repouso dormindo
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
        switch (item.getItemId()) {

            //noinspection SimplifiableIfStatement
            case R.id.imSobre: {
                LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.dialog_sobre,null);
                Button btnLink = view.findViewById(R.id.btnLink);
                Button btnSobre = view.findViewById(R.id.btnSobreOk);
                final DialogConstrutor dialogConstrutor = new DialogConstrutor(this,view);
                btnLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getSupportActionBar().setTitle("Sobre o aplicativo");
                        fragmentSobre = FragmentSobre.newInstance();
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frmContainerFragment, fragmentSobre,"FragmentSobre");
                        transaction.commit();
                        dialogConstrutor.fechar();
                    }
                });

                btnSobre.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogConstrutor.fechar();
                    }
                });
                Window window = dialogConstrutor.getDialog().getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT );
                break;
            }
            case R.id.imSair:{
                finish();
                break;
            }
            case R.id.imConfiguracoes:{
                getSupportActionBar().setTitle(res.getString(R.string.app_name_reduzido)+" " + res.getString(R.string.menu_item_configuracoes));
                Fragment fragmentConfiguracoes = FragementConfiguracoes.newInstance(getUsuario(),windowManager);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frmContainerFragment, fragmentConfiguracoes,"FragmentConfiguracoes");
                transaction.commit();
                break;
            }
            case R.id.imLogOut:{
                final DialogConstrutor dialogLogout = new DialogConstrutor(this);
                dialogLogout.tituloCustomizado(res.getString(R.string.tela_principal_dialog_logout_titulo));
                dialogLogout.setPositiveButton(res.getString(R.string.tela_principal_dialog_logout_positivebutton), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ContentValues values = new ContentValues();
                        values.put("Logado",0);
                        dbGeneric.atualizar("Usuarios",values,"_id = ?",new String[]{String.valueOf(usuario.getId())});
                        recreate();
                    }
                });
                dialogLogout.setNegativeButton(res.getString(R.string.tela_principal_dialog_logout_negativebutton), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogLogout.fechar();
                    }
                });
                dialogLogout.setMenssagem(res.getString(R.string.tela_principal_dialog_logout_menssagem));
                dialogLogout.setDialog(dialogLogout.create());
                dialogLogout.getDialog().show();
                dialogLogout.menssagemCustomizada();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_home: {
                getSupportActionBar().setTitle(res.getString(R.string.app_name_reduzido) +" "+ res.getString(R.string.tela_principal_menu_navegacao_inferior_item_home));
                Fragment fragmentInicio = FragmentInicio.newInstance(getUsuario(),windowManager);
                openFragment(fragmentInicio);
                break;
            }
            case R.id.menu_item_peso: {
                getSupportActionBar().setTitle(res.getString(R.string.app_name_reduzido) +" "+ res.getString(R.string.tela_principal_menu_navegacao_inferior_item_peso));
                Fragment fragmentPeso = FragmentPeso.newInstance(getUsuario(),windowManager);
                openFragment(fragmentPeso);
                break;
            }
            case R.id.menu_item_eu: {
                getSupportActionBar().setTitle(res.getString(R.string.app_name_reduzido) +" "+ res.getString(R.string.tela_principal_menu_navegacao_inferior_item_informacoes));
                Fragment fragmentEu = FragmentInformacoesUsuario.newInstance(getUsuario(),windowManager);
                openFragment(fragmentEu);
                break;
            }
        }
        return true;
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frmContainerFragment, fragment);
        if(back)
            transaction.addToBackStack(null);
        back = true;
        transaction.commit();
    }


    public EditText getEdData() {
        return edData;
    }

    public void setEdData(EditText edData) {
        this.edData = edData;
    }

    private DBGeneric getDbGeneric() {
        return dbGeneric;
    }

    private void setDbGeneric(DBGeneric dbGeneric) {
        this.dbGeneric = dbGeneric;
    }

    private Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(fragmentSobre!=null){
            if(fragmentSobre.isVisible()){
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.remove(fragmentSobre).commit();
            }
        }

    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
class syncFirebase extends AsyncTask<List<List<String>>,Integer,String>{

    @Override
    protected String doInBackground(List<List<String>> ...lists) {
        return null;
    }
}