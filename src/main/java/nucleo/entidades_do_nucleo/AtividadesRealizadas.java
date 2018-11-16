package nucleo.entidades_do_nucleo;

import objetos_auxiliares.ManipuladorDataTempo;

public class AtividadesRealizadas extends AtividadesFisicas {
    private int id;
    private int idAtividade;
    private long dia;
    private long horaInicio;
    private long horaFim;
    private int idUsuario;

    public AtividadesRealizadas() {
    }

    public AtividadesRealizadas(int id, int idAtividade) {
        super(idAtividade);
        this.idAtividade = idAtividade;
        this.id = id;
    }

    public AtividadesRealizadas(int idAtividade, long dia, long horaInicio, long horaFim) {
        super(idAtividade);
        this.idAtividade = idAtividade;
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdAtividade() {
        return idAtividade;
    }

    public void setIdAtividade(int idAtividade) {

        this.idAtividade = idAtividade;
        super.setId(idAtividade);
    }

    public long getDia() {
        return dia;
    }

    public void setDia(long dia) {
        this.dia = dia;
    }

    public long getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(long horaInicio) {
        this.horaInicio = horaInicio;
    }

    public long getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(long horaFim) {
        this.horaFim = horaFim;
    }


    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }


    public double calorias(){
        long tempo = horaFim - horaInicio;
        double horas  = ManipuladorDataTempo.horas(tempo);
        double massa = 60;//TODO: implementar uma forma de pegar o valor de massa do usuario
        double met = super.getMet(); //TODO: implementar forma de pegar o MET da atividade cujo id está em idAtividade
        return horas*massa*met;
    }
    public String getIdString(){
        return Integer.toString(getId());
    }

}
