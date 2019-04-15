package nucleo.entidades_do_nucleo;

public class AtividadesFisicas extends Categoria {
    private int id;
    private String idString;
    private float met;
    private String metString;
    private String atividade;
    private int idCategotia;
    private String idCategoriaString;

    public AtividadesFisicas() {
    }

    public AtividadesFisicas(int id) {this.id = id; }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        setIdString(Integer.toString(getId()));

    }

    public float getMet() {
        return met;
    }

    public void setMet(float met) {
        this.met = met;
        setMetString(Float.toString(getMet()));
    }

    public String getAtividade() {
        return atividade;
    }

    public void setAtividade(String atividade) {
        this.atividade = atividade;
    }

    public int getIdCategotia() {
        return idCategotia;
    }

    public void setIdCategotia(int idCategotia) {
        super.setId(idCategotia);
        this.idCategotia = idCategotia;
        setIdCategoriaString(Integer.toString(getIdCategotia()));
    }

    public String getIdString() {
        return idString;
    }

    public void setIdString(String idString) {
        this.idString = idString;
    }

    public String getMetString() {
        return metString;
    }

    public void setMetString(String metString) {
        this.metString = metString;
    }

    public String getIdCategoriaString() {
        return idCategoriaString;
    }

    public void setIdCategoriaString(String idCategoriaString) {
        this.idCategoriaString = idCategoriaString;
    }

    @Override
    public String toString() {
        return "AtividadesFisicas{" +
                "idString='" + idString + '\'' +
                ", metString='" + metString + '\'' +
                ", atividade='" + atividade + '\'' +
                ", idCategoriaString='" + idCategoriaString + '\'' +
                '}';
    }
}
