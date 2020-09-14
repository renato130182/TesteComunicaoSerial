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
    private long metragemEvento;
    private long idEvento;
    private String dataHoraInicio;
    private String dataHoraFinal;   
    private long metragemRetorno;
    
    public EventoMaquina() {
        this.cod_maquina = "";
        this.metragemEvento = 0;
        this.metragemRetorno = 0;
        this.idEvento = 0;
        this.dataHoraFinal="";
        this.dataHoraInicio="";
    }

    public String getDataHoraInicio() {
        return dataHoraInicio;
    }

    public void setDataHoraInicio(String dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
    }

    public String getDataHoraFinal() {
        return dataHoraFinal;
    }

    public void setDataHoraFinal(String dataHoraFinal) {
        this.dataHoraFinal = dataHoraFinal;
    }

    public long getMetragemRetorno() {
        return metragemRetorno;
    }

    public void setMetragemRetorno(long metragemRetorno) {
        this.metragemRetorno = metragemRetorno;
    }
    
    
    
    public long getMetragemEvento() {
        return metragemEvento;
    }

    public void setMetragemEvento(long metragemEvento) {
        this.metragemEvento = metragemEvento;
    }
    public String getCod_maquina() {
        return cod_maquina;
    }

    public void setCod_maquina(String cod_maquina) {
        this.cod_maquina = cod_maquina;
    }

    public long getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(long idEvento) {
        this.idEvento = idEvento;
    }
    
    
}
