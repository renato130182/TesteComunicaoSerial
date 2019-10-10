package model;

public class ParadasMaquina extends Paradas {

    private Paradas paradas;
    private Maquina maquina;
    private int codigoParadaMaquina;

    public Paradas getParadas() {
        return paradas;
    }

    public void setParadas(Paradas paradas) {
        this.paradas = paradas;
    }

    public Maquina getMaquina() {
        return maquina;
    }

    public void setMaquina(Maquina maquina) {
        this.maquina = maquina;
    }


    public int getCodigoParadaMaquina() {
        return codigoParadaMaquina;
    }

    public void setCodigoParadaMaquina(int codigoParadaMaquina) {
        this.codigoParadaMaquina = codigoParadaMaquina;
    }

}
