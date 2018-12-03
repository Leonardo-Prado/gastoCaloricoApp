package nucleo.entidades_do_nucleo;

public class Usuario {
    public static int MASCULINO = 0;
    public static int FEMININO = 1;
    private int id;
    private String nome;
    private int idade;
    private double massaCorporal;
    private double altura;
    private int sexo;
    private String email;

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
        if(altura>=0.4 && altura<=3)
            this.altura = altura;
        else
            this.altura = altura/100;
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
        this.massaCorporal = massaCorporal;
    }

    public double getAltura() { return altura; }

    public void setAltura(double altura) {
        if(altura>=0.4 && altura<=3)
            this.altura = altura;
        else
            this.altura = altura/100;
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
        this.massaCorporal = Double.parseDouble(massaCorporal);
    }

    public void setAltura(String altura) {
        double a = Double.parseDouble(altura);
        if(a>=0.4 && a<=3)
            this.altura = a;
        else
            this.altura = a/100;
    }
}
