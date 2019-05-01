package com.ifmg.polardispendium_gastocalorico;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import database.DBGeneric;
import listeners.ResultadoLoginInterface;
import nucleo.entidades_do_nucleo.Usuario;
import objetos_auxiliares.DialogConstrutor;
import objetos_auxiliares.HashCriador;
import objetos_auxiliares.ManipuladorDataTempo;

/**
 * A dialog_dados_do_usuario screen that offers dialog_dados_do_usuario via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    private Button btnEntrar;
    private EditText edEmail;
    private EditText edSenha;
    private DBGeneric dbGeneric;
    private Resources res;
    private boolean online = false;
    private Firebase firebase;
    private Usuario usuario;
    private String email;
    private String senha;
    private String senhaFB;
    private FirebaseAuth auth;
    private int userId = -1;
    private List<List<String>> userList;
    private FirebaseUser firebaseUser;
    private boolean fbLogado;
    private boolean retFB;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Resultado resultado;
    private  ResultadoLoginInterface resultadoLoginInterface;
    private List<List<String>> usuariolist;
    private Context context;
    private String userToken;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        Intent intent = getIntent();
        resultado = new Resultado();
        res = this.getResources();
        setContentView(R.layout.activity_login);
        edEmail = findViewById(R.id.edEmail);
        edSenha = findViewById(R.id.edSenha);
        btnEntrar = findViewById(R.id.btnEntrar);
        dbGeneric = new DBGeneric(this);

        final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        online =  (activeNetwork != null && activeNetwork.isConnected())?true:false;
        if(online) {
            auth = FirebaseAuth.getInstance();
            firebaseUser = auth.getCurrentUser();
            if (intent.hasExtra("userId")) {
                userId = intent.getIntExtra("userId", 0);
                if (userId != -1)
                    userList = dbGeneric.buscar("Usuarios", new String[]{"_id", "Nome", "Email", "Senha", "Idade", "Sexo", "MassaCorporal", "Altura", "DataCriacao", "PesoMinimo", "PesoMaximo", "GastoMinimo", "GastoMaximo", "GastoMedio"}, "_id = ?", new String[]{Integer.toString(userId)});
            }
        }
        if(!online) {
            btnEntrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        email = edEmail.getText().toString();
                        senha = edSenha.getText().toString();
                        if (validarEmail(getEmail()) && validarSenha(getSenha())) {
                            senhaFB = senha;
                            final String senha = HashCriador.encriptarToString(edSenha.getText().toString());
                            List<List<String>> usuario = dbGeneric.buscar("Usuarios", new String[]{"_id"}, "Email = ?", new String[]{edEmail.getText().toString()});
                            if (usuario.size() == 1) {
                                usuario = dbGeneric.buscar("Usuarios", new String[]{"_id"}, "_id = ? AND Senha = ?", new String[]{usuario.get(0).get(0), senha});
                                if (usuario.size() == 1) {
                                    atualizarUsuarioInterno(usuario.get(0).get(0),Usuario.INTERNO);
                                    carregarTelaPrincipal();
                                }else {
                                    new DialogConstrutor(view.getContext(), res.getString(R.string.login_activ_dialog_senha_errada_titulo), res.getString(R.string.login_activ_dialog_senha_errada_menssagem), res.getString(R.string.dialog_positive_button_texto_padrao));
                                }
                            } else {
                                final LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
                                View usuarioInexistente = inflater.inflate(R.layout.dialog_usuario_inexistente, null);
                                Button btnRepetir = usuarioInexistente.findViewById(R.id.btnRepitir);
                                Button btnRegistrar = usuarioInexistente.findViewById(R.id.btnRegistrar);
                                final DialogConstrutor dialogUsuarioInexistente = new DialogConstrutor(view.getContext(), usuarioInexistente, res.getString(R.string.login_activ_dialog_usuario_inexistente_titulo), res.getString(R.string.login_activ_dialog_usuario_inexistente_menssagem));
                                btnRepetir.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialogUsuarioInexistente.fechar();
                                    }
                                });
                                btnRegistrar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialogUsuarioInexistente.fechar();
                                        View registrarUsuario = inflater.inflate(R.layout.dialog_registrar_usuario, null);
                                        Button btnCancelar = registrarUsuario.findViewById(R.id.btnCancelarRegistro);
                                        Button btnConfirmar = registrarUsuario.findViewById(R.id.btnConfirmarRegistro);
                                        final EditText edNome = registrarUsuario.findViewById(R.id.edNomeRegistrar);
                                        final EditText edSenhaRepetir = registrarUsuario.findViewById(R.id.edRepetirSenhaRegistrar);
                                        final DialogConstrutor dialogRegistrarUsuario = new DialogConstrutor(view.getContext(), registrarUsuario, "Registre-se", "");
                                        btnCancelar.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialogRegistrarUsuario.fechar();
                                            }
                                        });
                                        btnConfirmar.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                try {
                                                    if (edNome.getText().length() >= 3) {
                                                        if (validarSenha(edSenhaRepetir.getText().toString())) {
                                                            String repetirSenha = HashCriador.encriptarToString(edSenhaRepetir.getText().toString());
                                                            if (senha.equals(repetirSenha)) {
                                                                boolean criado = criarNovoUsuarioInterno(edEmail.getText().toString(),senha,edNome.getText().toString(),Usuario.INTERNO, null, null);
                                                                if (criado) {
                                                                    carregarTelaPrincipal();
                                                                } else {
                                                                    new DialogConstrutor(view.getContext(), res.getString(R.string.login_activ_dialog_registrar_menssagem_falha_titulo), res.getString(R.string.login_activ_dialog_registrar_menssagem_falha_menssagem), res.getString(R.string.dialog_positive_button_texto_padrao));
                                                                }
                                                            } else {
                                                                new DialogConstrutor(view.getContext(), res.getString(R.string.login_activ_dialog_registrar_menssagem_senha_diferente_titulo), res.getString(R.string.login_activ_dialog_registrar_menssagem_senha_diferente_menssagem), res.getString(R.string.dialog_positive_button_texto_padrao));
                                                            }
                                                        } else {
                                                            new DialogConstrutor(view.getContext(), res.getString(R.string.login_activ_dialog_registrar_menssagem_senha_diferente_titulo), res.getString(R.string.login_activ_dialog_registrar_menssagem_senha_diferente_menssagem), res.getString(R.string.dialog_positive_button_texto_padrao));
                                                        }
                                                    } else {
                                                        new DialogConstrutor(view.getContext(), res.getString(R.string.login_activ_dialog_registrar_menssagem_nomeUsuario_minimo_titulo), res.getString(R.string.login_activ_dialog_registrar_menssagem_nomeUsuario_minimo_menssagem), res.getString(R.string.dialog_positive_button_texto_padrao));
                                                    }
                                                } catch (Exception e) {
                                                    Log.e("erro ao criar user", e.getMessage());
                                                }

                                            }

                                        });
                                    }
                                });
                            }
                        } else {
                            new DialogConstrutor(view.getContext(), res.getString(R.string.login_activ_dialog_senha_ou_email_invalidos_titulo), res.getString(R.string.login_activ_dialog_senha_ou_email_invalidos_menssagem), res.getString(R.string.dialog_positive_button_texto_padrao));
                        }
                    } catch (Exception e) {
                        Log.e("erro criar firebase", e.getMessage());
                    }
                }

            });
        }else{
            if(userList==null) btnEntrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        email = edEmail.getText().toString();
                        senha = edSenha.getText().toString();
                        if (validarEmail(getEmail()) && validarSenha(getSenha())) {
                            senhaFB = senha;
                            final String senha = HashCriador.encriptarToString(edSenha.getText().toString());
                            usuariolist = dbGeneric.buscar("Usuarios", new String[]{"_id"}, "Email = ?", new String[]{edEmail.getText().toString()});
                            logarFb(auth, email, senhaFB);
                            resultadoLoginInterface = new ResultadoLoginInterface() {
                                @Override
                                public void notificarResultado(final Resultado resultado) {
                                    try {
                                        resultado.removerObservador(resultadoLoginInterface);
                                        if (usuariolist.size() == 1 || fbLogado||resultado.getStatusResultado()==Resultado.USUARIO_INEXISTE) {
                                            firebaseUser = auth.getCurrentUser();
                                            if (firebaseUser != null&&resultado.getStatusResultado()!=Resultado.USUARIO_INEXISTE) {
                                                String m = firebaseUser.getEmail();
                                                usuariolist = dbGeneric.buscar("Usuarios", new String[]{"_id"}, "Email = ?", new String[]{m});
                                                if (usuariolist.size() == 1) {
                                                    atualizarUsuarioInterno(usuariolist.get(0).get(0),Usuario.FIREBASE);
                                                    carregarTelaPrincipal();
                                                }else {
                                                    resultado.adicionarObservador(new ResultadoLoginInterface() {
                                                        @Override
                                                        public void notificarResultado(Resultado resultado) {
                                                            if (resultado.getStatusResultado() == 1)
                                                                carregarTelaPrincipal();
                                                        }
                                                    });
                                                    firebaseUser.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                                                public void onComplete(@NonNull Task<GetTokenResult> task) {
                                                                    if (task.isSuccessful()) {
                                                                        userToken = task.getResult().getToken();
                                                                        boolean criado = criarNovoUsuarioInterno(email, senha, firebaseUser.getDisplayName(), Usuario.FIREBASE,firebaseUser.getUid(),userToken);
                                                                        if(criado)
                                                                            resultado.setStatusResultado(Resultado.SUCESSO);
                                                                        else
                                                                            resultado.setStatusResultado(Resultado.FRACASSO);

                                                                    } else {
                                                                        // Handle error -> task.getException();
                                                                    }
                                                                }
                                                            });


                                                }
                                            } else if(usuariolist.size() == 1 ){
                                                criarUsuarioFirebase(email, senhaFB);
                                                atualizarUsuarioInterno(usuariolist.get(0).get(0), Usuario.FIREBASE);
                                                carregarTelaPrincipal();
                                            }else{
                                                final ResultadoLoginInterface criarResultado = new ResultadoLoginInterface() {
                                                    @Override
                                                    public void notificarResultado(Resultado resultado) {
                                                        resultado.removerTodosObservadores();
                                                        logarFb(auth, email, senhaFB);
                                                        resultado.adicionarObservador(resultadoLoginInterface);
                                                    }
                                                };
                                                resultado.adicionarObservador(criarResultado);
                                                criarUsuarioFirebase(email, senhaFB);


                                            }
                                        }

                                    } catch (Exception e) {
                                        Log.e("Erro:", e.getMessage());
                                    }
                                }
                            };
                            resultado.adicionarObservador(resultadoLoginInterface);


                        } else {
                            new DialogConstrutor(view.getContext(), res.getString(R.string.login_activ_dialog_senha_ou_email_invalidos_titulo), res.getString(R.string.login_activ_dialog_senha_ou_email_invalidos_menssagem), res.getString(R.string.dialog_positive_button_texto_padrao));
                        }
                    } catch (Exception e) {
                        Log.e("erro criar firebase", e.getMessage());
                    }

                }
            });
            else{
                btnEntrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            email = edEmail.getText().toString();
                            senha = edSenha.getText().toString();
                            if (validarEmail(getEmail()) && validarSenha(getSenha())){
                                senhaFB = senha;
                                criarUsuarioFirebase(email,senhaFB);


                                atualizarUsuarioInterno(userList.get(0).get(0),Usuario.FIREBASE);
                            }else {
                                new DialogConstrutor(view.getContext(), res.getString(R.string.login_activ_dialog_senha_ou_email_invalidos_titulo), res.getString(R.string.login_activ_dialog_senha_ou_email_invalidos_menssagem), res.getString(R.string.dialog_positive_button_texto_padrao));
                            }
                        }catch (Exception e) {
                            Log.e("erro criar firebase", e.getMessage());
                        }

                    }
                });
            }
        }

    }

    private void atualizarUsuarioInterno(String userId, int metodo) {
        ContentValues values = new ContentValues();
        values.put("Logado", 1);
        values.put("Metodo", metodo);
        dbGeneric.atualizar("Usuarios", values, "_id = ?", new String[]{userId});
    }
    private void criarUsuarioFirebase(String email,String senha){
        auth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("CreateFB:", "createUserWithEmail:success");
                            resultado.setStatusResultado(1);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("CreateFB:", "createUserWithEmail:failure", task.getException());
                           resultado.setStatusResultado(0);
                        }
                    }
                });
    }

    private boolean criarNovoUsuarioInterno(String email, String senha, String nome, int metodoLogin, String uid, String userToken) {
        try {
            ContentValues values = new ContentValues();
            if(nome==null)
                nome = email.split("@")[0];
            values.put("Nome", nome);
            values.put("Email", email);
            values.put("Senha", senha);
            values.put("Logado", 1);
            values.put("Metodo", metodoLogin);
            values.put("DataCriacao", new ManipuladorDataTempo(new Date()).getDataInt());

            if(uid!=null)
                values.put("FirebaseUId", uid);
            if(uid!=null)
                values.put("FirebaseToken", userToken);
            int i = dbGeneric.inserir(values, "Usuarios");
            if(i>0)
                return true;
            else
                return false;

        }catch (Exception e) {
            return false;
        }
    }

    private void carregarTelaPrincipal() {
        new DialogConstrutor(this, res.getString(R.string.login_activ_dialog_registrar_menssagem_sucesso_titulo), res.getString(R.string.login_activ_dialog_registrar_menssagem_sucesso_menssagem), res.getString(R.string.dialog_positive_button_texto_padrao));
        Intent intent = new Intent(LoginActivity.this, TelaPrincipal.class);
        startActivity(intent);
        finish();
    }

    private boolean validarEmail(String email) {
        String regExpn = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";
        CharSequence inputStr = email;
        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches())
            return true;
        else
            return false;
    }
    private boolean validarSenha(String senha) {
        return senha.length() >= 6;
    }

    public Button getBtnEntrar() {
        return btnEntrar;
    }

    public void setBtnEntrar(Button btnEntrar) {
        this.btnEntrar = btnEntrar;
    }

    public EditText getEdEmail() {
        return edEmail;
    }

    public void setEdEmail(EditText edEmail) {
        this.edEmail = edEmail;
    }

    public EditText getEdSenha() {
        return edSenha;
    }

    public void setEdSenha(EditText edSenha) {
        this.edSenha = edSenha;
    }

    public DBGeneric getDbGeneric() {
        return dbGeneric;
    }

    public void setDbGeneric(DBGeneric dbGeneric) {
        this.dbGeneric = dbGeneric;
    }

    public Resources getRes() {
        return res;
    }

    public void setRes(Resources res) {
        this.res = res;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public Firebase getFirebase() {
        return firebase;
    }

    public void setFirebase(Firebase firebase) {
        this.firebase = firebase;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    private void logarFb(FirebaseAuth auth,String email,String password){
        Toast.makeText(context,"Logando...",Toast.LENGTH_SHORT);
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Log.w("AUTH", "Falha ao efetuar o Login: ", task.getException());
                    String menssagemErro = task.getException().getMessage();
                    if(menssagemErro.contains("There is no user record corresponding to this identifier. The user may have been deleted.")) {
                        resultado.setStatusResultado(Resultado.USUARIO_INEXISTE);
                        Toast.makeText(context,"Usuario não encontrado, Criando cadastro",Toast.LENGTH_SHORT);
                    }else
                        resultado.setStatusResultado(Resultado.FRACASSO);
                    if(resultado.getStatusResultado()==Resultado.FRACASSO)
                        new DialogConstrutor(context, res.getString(R.string.login_activ_dialog_senha_errada_titulo), res.getString(R.string.login_activ_dialog_senha_errada_menssagem), res.getString(R.string.dialog_positive_button_texto_padrao));
                    fbLogado = false;

                }else{
                    Log.d("AUTH", "Login Efetuado com sucesso!!!");
                    fbLogado = true;
                    Toast.makeText(context,"Login Efetuado com sucesso!!!",Toast.LENGTH_SHORT);
                    resultado.setStatusResultado(1);

                }
            }
        });
    }

}

