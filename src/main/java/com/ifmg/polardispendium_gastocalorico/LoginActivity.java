package com.ifmg.polardispendium_gastocalorico;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import database.DBGeneric;
import objetos_auxiliares.DialogConstrutor;
import objetos_auxiliares.HashCriador;

/**
 * A dialog_dados_do_usuario screen that offers dialog_dados_do_usuario via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    Button btnEntrar;
    EditText edEmail;
    EditText edSenha;
    DBGeneric dbGeneric;
    Resources res;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        res = this.getResources();
        setContentView(R.layout.activity_login);
        edEmail = findViewById(R.id.edEmail);
        edSenha = findViewById(R.id.edSenha);
        btnEntrar = findViewById(R.id.btnEntrar);
        dbGeneric = new DBGeneric(this);
        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validarEmail(edEmail.getText().toString())&&validarSenha(edSenha.getText().toString())){
                    final String senha = HashCriador.encriptarToString(edSenha.getText().toString());
                    List<List<String>> usuario = dbGeneric.buscar("Usuarios",new String[]{"_id"},"Email = ?",new String[]{edEmail.getText().toString()});
                    if(usuario.size()==1){
                        usuario = dbGeneric.buscar("Usuarios",new String[]{"_id"},"_id = ? AND Senha = ?",new String[]{usuario.get(0).get(0),senha});
                        if(usuario.size()==1){
                            ContentValues values = new ContentValues();
                            values.put("Logado",1);
                            dbGeneric.atualizar("Usuarios",values,"_id = ?",new String[]{usuario.get(0).get(0)});
                            Intent intent = new Intent(LoginActivity.this,TelaPrincipal.class);
                            startActivity(intent);
                            finish();
                        }else {
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
                                        if(edNome.getText().length()>=5){
                                            if(validarSenha(edSenhaRepetir.getText().toString())){
                                                String repetirSenha = HashCriador.encriptarToString(edSenhaRepetir.getText().toString());
                                                if(senha.equals(repetirSenha)){
                                                    ContentValues values = new ContentValues();
                                                    values.put("Nome",edNome.getText().toString());
                                                    values.put("Email",edEmail.getText().toString());
                                                    values.put("Senha",senha);
                                                    values.put("Logado",1);
                                                    int i = dbGeneric.inserir(values,"Usuarios");
                                                    if(i>=0){
                                                        new DialogConstrutor(view.getContext(),res.getString(R.string.login_activ_dialog_registrar_menssagem_sucesso_titulo),res.getString(R.string.login_activ_dialog_registrar_menssagem_sucesso_menssagem),res.getString(R.string.dialog_positive_button_texto_padrao));
                                                        Intent intent = new Intent(LoginActivity.this,TelaPrincipal.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }else{
                                                        new DialogConstrutor(view.getContext(),res.getString(R.string.login_activ_dialog_registrar_menssagem_falha_titulo),res.getString(R.string.login_activ_dialog_registrar_menssagem_falha_menssagem),res.getString(R.string.dialog_positive_button_texto_padrao));
                                                    }
                                                }else{
                                                    new DialogConstrutor(view.getContext(),res.getString(R.string.login_activ_dialog_registrar_menssagem_senha_diferente_titulo),res.getString(R.string.login_activ_dialog_registrar_menssagem_senha_diferente_menssagem),res.getString(R.string.dialog_positive_button_texto_padrao));
                                                }
                                            }
                                        }

                                    }
                                });
                            }
                        });
                    }
                }else
                    new DialogConstrutor(view.getContext(),res.getString(R.string.login_activ_dialog_senha_ou_email_invalidos_titulo),res.getString(R.string.login_activ_dialog_senha_ou_email_invalidos_menssagem),res.getString(R.string.dialog_positive_button_texto_padrao));
            }
        });

    }

    private boolean validarEmail(String email) {
        return email.contains("@") && email.length() >= 6;
    }
    private boolean validarSenha(String senha) {
        return senha.length() >= 6;
    }
}

