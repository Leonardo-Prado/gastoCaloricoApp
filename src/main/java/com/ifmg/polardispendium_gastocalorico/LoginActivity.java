package com.ifmg.polardispendium_gastocalorico;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.Firebase;

import java.util.Date;
import java.util.List;

import database.DBGeneric;
import database.FirebaseAuth;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        res = this.getResources();
        setContentView(R.layout.activity_login);
        edEmail = findViewById(R.id.edEmail);
        edSenha = findViewById(R.id.edSenha);
        btnEntrar = findViewById(R.id.btnEntrar);
        dbGeneric = new DBGeneric(this);

        final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        online =  (activeNetwork != null && activeNetwork.isConnected())?true:false;
        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    email = edEmail.getText().toString();
                    senha = edSenha.getText().toString();
                    if(validarEmail(getEmail())&&validarSenha(getSenha())){
                        final String senha = HashCriador.encriptarToString(edSenha.getText().toString());
                        List<List<String>> usuario = dbGeneric.buscar("Usuarios",new String[]{"_id"},"Email = ?",new String[]{edEmail.getText().toString()});
                        if(usuario.size()==1){
                            usuario = dbGeneric.buscar("Usuarios",new String[]{"_id"},"_id = ? AND Senha = ?",new String[]{usuario.get(0).get(0),senha});
                            if(usuario.size()==1){
                                ContentValues values = new ContentValues();
                                values.put("Logado",1);
                                values.put("Metodo",Usuario.INTERNO);
                                dbGeneric.atualizar("Usuarios",values,"_id = ?",new String[]{usuario.get(0).get(0)});
                                Intent intent = new Intent(LoginActivity.this,TelaPrincipal.class);
                                startActivity(intent);
                                finish();
                            }/*else if(FirebaseAuth.getUser(firebase,getEmail(),getSenha())!=null) {
                                Usuario user = FirebaseAuth.getUser(firebase,getEmail(),getSenha());
                                ContentValues values = new ContentValues();
                                values.put("Logado", 1);
                                values.put("Metodo", Usuario.FIREBASE);
                                dbGeneric.atualizar("Usuarios", values, "_id = ?", new String[]{Integer.toString(user.getId())});
                                Intent intent = new Intent(LoginActivity.this, TelaPrincipal.class);
                                startActivity(intent);
                                finish();
                            }*/else {
                                new DialogConstrutor(view.getContext(),res.getString(R.string.login_activ_dialog_senha_errada_titulo),res.getString(R.string.login_activ_dialog_senha_errada_menssagem),res.getString(R.string.dialog_positive_button_texto_padrao));
                            }
                        }else{
                            final LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
                            View usuarioInexistente = inflater.inflate(R.layout.dialog_usuario_inexistente, null);
                            Button btnRepetir = usuarioInexistente.findViewById(R.id.btnRepitir);
                            Button btnRegistrar = usuarioInexistente.findViewById(R.id.btnRegistrar);
                            final DialogConstrutor dialogUsuarioInexistente = new DialogConstrutor(view.getContext(),usuarioInexistente,res.getString(R.string.login_activ_dialog_usuario_inexistente_titulo),res.getString(R.string.login_activ_dialog_usuario_inexistente_menssagem));
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
                                    View registrarUsuario = inflater.inflate(R.layout.dialog_registrar_usuario,null);
                                    Button btnCancelar = registrarUsuario.findViewById(R.id.btnCancelarRegistro);
                                    Button btnConfirmar = registrarUsuario.findViewById(R.id.btnConfirmarRegistro);
                                    final EditText edNome = registrarUsuario.findViewById(R.id.edNomeRegistrar);
                                    final EditText edSenhaRepetir = registrarUsuario.findViewById(R.id.edRepetirSenhaRegistrar);
                                    final DialogConstrutor dialogRegistrarUsuario = new DialogConstrutor(view.getContext(),registrarUsuario,"Registre-se","");
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
                                                            ContentValues values = new ContentValues();
                                                            values.put("Nome", edNome.getText().toString());
                                                            values.put("Email", edEmail.getText().toString());
                                                            values.put("Senha", senha);
                                                            values.put("Logado", 1);
                                                            values.put("Metodo", Usuario.INTERNO);
                                                            values.put("DataCriacao", new ManipuladorDataTempo(new Date()).getDataInt());
                                                            int i = dbGeneric.inserir(values, "Usuarios");
                                                            if (i >= 0) {
                                                                new DialogConstrutor(view.getContext(), res.getString(R.string.login_activ_dialog_registrar_menssagem_sucesso_titulo), res.getString(R.string.login_activ_dialog_registrar_menssagem_sucesso_menssagem), res.getString(R.string.dialog_positive_button_texto_padrao));
                                                                Intent intent = new Intent(LoginActivity.this, TelaPrincipal.class);
                                                                startActivity(intent);
                                                                finish();
                                                            } else {
                                                                new DialogConstrutor(view.getContext(), res.getString(R.string.login_activ_dialog_registrar_menssagem_falha_titulo), res.getString(R.string.login_activ_dialog_registrar_menssagem_falha_menssagem), res.getString(R.string.dialog_positive_button_texto_padrao));
                                                            }
                                                        } else {
                                                            new DialogConstrutor(view.getContext(), res.getString(R.string.login_activ_dialog_registrar_menssagem_senha_diferente_titulo), res.getString(R.string.login_activ_dialog_registrar_menssagem_senha_diferente_menssagem), res.getString(R.string.dialog_positive_button_texto_padrao));
                                                        }
                                                    }else {
                                                        new DialogConstrutor(view.getContext(), res.getString(R.string.login_activ_dialog_registrar_menssagem_senha_diferente_titulo), res.getString(R.string.login_activ_dialog_registrar_menssagem_senha_diferente_menssagem), res.getString(R.string.dialog_positive_button_texto_padrao));
                                                    }
                                                }else {
                                                    new DialogConstrutor(view.getContext(), res.getString(R.string.login_activ_dialog_registrar_menssagem_nomeUsuario_minimo_titulo), res.getString(R.string.login_activ_dialog_registrar_menssagem_nomeUsuario_minimo_menssagem), res.getString(R.string.dialog_positive_button_texto_padrao));
                                                }
                                            }catch (Exception e){
                                                Log.e("erro ao criar user",e.getMessage());
                                            }

                                        }

                                    });
                                }
                            });
                        }
                    }else {
                        new DialogConstrutor(view.getContext(), res.getString(R.string.login_activ_dialog_senha_ou_email_invalidos_titulo), res.getString(R.string.login_activ_dialog_senha_ou_email_invalidos_menssagem), res.getString(R.string.dialog_positive_button_texto_padrao));
                    }
                }catch (Exception e){
                    Log.e("erro criar firebase",e.getMessage());
                }
            }

        });

    }

    private boolean validarEmail(String email) {
        return email.contains("@") && email.length() >= 6;
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
}

