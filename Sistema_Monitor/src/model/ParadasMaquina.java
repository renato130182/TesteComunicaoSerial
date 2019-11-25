package model;

import java.util.ArrayList;
import java.util.List;

public class ParadasMaquina {

    private List<Paradas> listaParadas;
    private String cod_maquina;

    public ParadasMaquina() {
        this.listaParadas = new ArrayList<>();
    }

    public List<Paradas> getListaParadas() {
        return listaParadas;
    }

    public void setListaParadas(List<Paradas> listaParadas) {
        this.listaParadas = listaParadas;
    }
    
    public String getCod_maquina() {
        return cod_maquina;
    }

    public void setCod_maquina(String cod_maquina) {
        this.cod_maquina = cod_maquina;
    }

}
