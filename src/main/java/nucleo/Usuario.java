package nucleo;

public class Usuario {
    public static int MASCULINO = 0;
    public static int FEMININO = 1;
    private int id;
    private String nome;
    private int idade;
    private double massaCorporal;
    private int sexo;

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

    public int getSexo() {
        return sexo;
    }

    public void setSexo(int sexo) {
        this.sexo = sexo;
    }
}
