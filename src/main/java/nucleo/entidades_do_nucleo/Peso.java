package nucleo.entidades_do_nucleo;

public class Peso extends Usuario {
    private int id;
    private double massaCorporal;
    private int data;
    private int idUsuario;

    public Peso() {
    }

    public Peso(int id, int id1, int idUsuario) {
        super(id);
        this.id = id1;
        this.idUsuario = idUsuario;
    }

    public Peso(int id, double massaCorporal, int idUsuario) {
        super(id);
        this.massaCorporal = massaCorporal;
        this.idUsuario = idUsuario;
    }

    public Peso(int id, int id1, double massaCorporal, int data, int idUsuario) {
        super(id);
        this.id = id1;
        this.massaCorporal = massaCorporal;
        this.data = data;
        this.idUsuario = idUsuario;
    }


    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public double getMassaCorporal() {
        return massaCorporal;
    }

    @Override
    public void setMassaCorporal(double massaCorporal) {
        this.massaCorporal = massaCorporal;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
}
