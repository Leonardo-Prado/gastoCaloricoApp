package com.ifmg.polardispendium_gastocalorico;

import java.util.ArrayList;
import java.util.List;

import listeners.ResultadoLoginInterface;

public class Resultado{
    public static int FRACASSO = 0;
    public static int SUCESSO = 1;
    public static int TIMEOUT = 2;
    public static int SENHA_INVALIDA = 3;
    public static int USUARIO_JA_EXISTE = 4;
    public static int USUARIO_INEXISTE = 5;

    private int statusResultado;
    private final List<ResultadoLoginInterface> observadoresResultado = new ArrayList<>();
    private void notificarObservadores(){
        for (ResultadoLoginInterface resultado:observadoresResultado) {
            resultado.notificarResultado(this);
        }
    }
    public void adicionarObservador(ResultadoLoginInterface resultado) { //também chamado de addListener(...)
        observadoresResultado.add(resultado); //"obs" passará a ser notificado sobre mudanças em this
    }
    public void removerObservador(ResultadoLoginInterface resultado) { //também chamado de addListener(...)
        observadoresResultado.remove(resultado);
    }
    public void removerTodosObservadores(){
        for(ResultadoLoginInterface r:observadoresResultado){
            observadoresResultado.remove(r);
        }
    }

    public int getStatusResultado() {
        return statusResultado;
    }

    public void setStatusResultado(int statusResultado) {
        this.statusResultado = statusResultado;
        notificarObservadores();
    }
}
