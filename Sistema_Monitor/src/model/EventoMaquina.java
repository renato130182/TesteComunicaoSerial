/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author renato.soares
 */
public class EventoMaquina {
    private String cod_maquina;
    private long metragem;
    private long idEvento;
    public EventoMaquina() {
        this.cod_maquina = "";
        this.metragem = 0;
        this.idEvento = 0;
    }
    
    public String getCod_maquina() {
        return cod_maquina;
    }

    public void setCod_maquina(String cod_maquina) {
        this.cod_maquina = cod_maquina;
    }

    public long getMetragem() {
        return metragem;
    }

    public void setMetragem(long metragem) {
        this.metragem = metragem;
    }

    public long getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(long idEvento) {
        this.idEvento = idEvento;
    }
    
    
}
