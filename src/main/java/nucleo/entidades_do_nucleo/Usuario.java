package nucleo.entidades_do_nucleo;

import android.util.Log;

import objetos_auxiliares.FormatNum;

public class Usuario {
    public static int MASCULINO = 0;
    public static int FEMININO = 1;
    public static int INTERNO = 0;
    public static int FIREBASE = 1;
    public static int GOOGLE = 2;
    public static int FACEBOOK = 3;
    private int id;
    private String nome;
    private String email;
    private int idade;
    private double massaCorporal;
    private double altura;
    private double pesoMinimo;
    private double pesoMaximo;
    private double gastoMinimo;
    private double gastoMaximo;
    private double gastoMedio;
    private int sexo;
    private long dataCriacao;
    private int metodo;
    private int tipoUsuario;
    private String firebaseToken;
    private String googleToken;
    private String facebookToken;
    private String firebaseUId;
    private String googleUId;
    private String facebookUId;

    public Usuario() {
    }

    public Usuario(int id) {
        this.id = id;
    }

    public Usuario(int id, String nome, int idade, double massaCorporal, int sexo) {
        this.id = id;
        this.nome = nome;
        this.idade = idade;
        this.massaCorporal = massaCorporal;
        this.sexo = sexo;
    }

    public Usuario(int id, String nome, int idade, double massaCorporal, double altura, int sexo) {
        this.id = id;
        this.nome = nome;
        this.idade = idade;
        this.massaCorporal = massaCorporal;
        setAltura(altura);
        this.sexo = sexo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public double getMassaCorporal() {
        return massaCorporal;
    }

    public void setMassaCorporal(double massaCorporal) {
        this.massaCorporal =FormatNum.casasDecimais( massaCorporal,2);
    }

    public double getAltura() { return altura; }

    public void setAltura(double altura) {
        if(altura>=0.4 && altura<=3)
            this.altura = FormatNum.casasDecimais(altura,2);
        else
            this.altura = FormatNum.casasDecimais(altura/100,2);
    }

    public int getSexo() { return sexo;}

    public void setSexo(int sexo) {this.sexo = sexo; }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMassaCorporal(String massaCorporal) {
        try{
            this.massaCorporal = FormatNum.casasDecimais(Double.parseDouble(massaCorporal),2);
        }catch (Exception e){
            Log.e("erro conversao",e.getMessage());
        }

    }

    public void setAltura(String altura) {
        double a = Double.parseDouble(altura);
        if(a>=0.01 && a<=3)
            this.altura = FormatNum.casasDecimais(a,2);
        else
            this.altura = FormatNum.casasDecimais(a/100,2);
    }

    public int getMetodo() {
        return metodo;
    }

    public void setMetodo(int metodo) {
        this.metodo = metodo;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

    public String getGoogleToken() {
        return googleToken;
    }

    public void setGoogleToken(String googleToken) {
        this.googleToken = googleToken;
    }

    public String getFacebookToken() {
        return facebookToken;
    }

    public void setFacebookToken(String facebookToken) {
        this.facebookToken = facebookToken;
    }

    public String getFirebaseUId() {
        return firebaseUId;
    }

    public void setFirebaseUId(String firebaseUId) {
        this.firebaseUId = firebaseUId;
    }

    public String getGoogleUId() {
        return googleUId;
    }

    public void setGoogleUId(String googleUId) {
        this.googleUId = googleUId;
    }

    public String getFacebookUId() {
        return facebookUId;
    }

    public void setFacebookUId(String facebookUId) {
        this.facebookUId = facebookUId;
    }

    public long getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(long dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public double getPesoMinimo() {
        return pesoMinimo;
    }

    public void setPesoMinimo(double pesoMinimo) {
        this.pesoMinimo = pesoMinimo;
    }

    public double getPesoMaximo() {
        return pesoMaximo;
    }

    public void setPesoMaximo(double pesoMaximo) {
        this.pesoMaximo = pesoMaximo;
    }

    public double getGastoMinimo() {
        return gastoMinimo;
    }

    public void setGastoMinimo(double gastoMinimo) {
        this.gastoMinimo = gastoMinimo;
    }

    public double getGastoMaximo() {
        return gastoMaximo;
    }

    public void setGastoMaximo(double gastoMaximo) {
        this.gastoMaximo = gastoMaximo;
    }

    public double getGastoMedio() {
        return gastoMedio;
    }

    public void setGastoMedio(double gastoMedio) {
        this.gastoMedio = gastoMedio;
    }

    public int getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(int tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }
}
