package nucleo;

import java.util.List;

public class GastoEnergetico extends Usuario {
    private int id;
    private double calorias = 0;
    private long data;
    private int idUsuario;
    private List<AtividadesRealizadas> atividadesRealizadas;

    public GastoEnergetico() {
    }

    public GastoEnergetico(List<AtividadesRealizadas> atividadesRealizadas) {
        this.atividadesRealizadas = atividadesRealizadas;
        int i = 0;
        while (i<atividadesRealizadas.size()){
            setCalorias(getCalorias()+atividadesRealizadas.get(i).calorias());
            i++;
        }
    }

    public GastoEnergetico(long data, int idUsuario, List<AtividadesRealizadas> atividadesRealizadas) {
        super(idUsuario);
        this.data = data;
        this.idUsuario = idUsuario;
        this.atividadesRealizadas = atividadesRealizadas;
    }

    public GastoEnergetico(double calorias, long data, int idUsuario, List<AtividadesRealizadas> atividadesRealizadas) {
        super(idUsuario);
        this.calorias = calorias;
        this.data = data;
        this.idUsuario = idUsuario;
        this.atividadesRealizadas = atividadesRealizadas;
    }

    public GastoEnergetico(int id, double calorias, long data, int idUsuario, List<AtividadesRealizadas> atividadesRealizadas) {
        super(idUsuario);
        this.id = id;
        this.calorias = calorias;
        this.data = data;
        this.idUsuario = idUsuario;
        this.atividadesRealizadas = atividadesRealizadas;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getCalorias() {
        return calorias;
    }

    public void setCalorias(double calorias) {
        this.calorias = calorias;
    }

    public long getData() {
        return data;
    }

    public void setData(long data) {
        this.data = data;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public List<AtividadesRealizadas> getAtividadesRealizadas() {
        return atividadesRealizadas;
    }

    public void setAtividadesRealizadas(List<AtividadesRealizadas> atividadesRealizadas) {
        this.atividadesRealizadas = atividadesRealizadas;
    }
}
