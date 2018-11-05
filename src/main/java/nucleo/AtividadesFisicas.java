package nucleo;

import database.DBGeneric;

public class AtividadesFisicas extends Categoria{
    private int id;
    private float met;
    private String atividade;
    private int idCategotia;

    public AtividadesFisicas() {
    }

    public AtividadesFisicas(int id) {this.id = id; }

    public AtividadesFisicas(int idCategotia, int id) {
        super(idCategotia);
        this.idCategotia = idCategotia;
        this.id = id;
    }

    public AtividadesFisicas(float met, String atividade, int idCategotia) {
        super(idCategotia);
        this.setMet(met);
        this.setAtividade(atividade);
        this.setIdCategotia(idCategotia);
    }

    public AtividadesFisicas(int id, float met, String atividade, int idCategotia) {
        super(idCategotia);
        this.setId(id);
        this.setMet(met);
        this.setAtividade(atividade);
        this.setIdCategotia(idCategotia);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getMet() {
        return met;
    }

    public void setMet(float met) {
        this.met = met;
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
    }
}
