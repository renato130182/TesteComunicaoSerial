/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.sql.Time;
import model.Micrometro;

/**
 *
 * @author renato.soares
 */
public class ControllerMicrometro extends Micrometro{
    Protocolo.NazkomUDC1_default info = new Protocolo.NazkomUDC1_default();    
    LogErro erro = new LogErro();
    public Micrometro setarDadosMicrometro(String Dados){
        try {                    
            info.setLeituraSerial(Dados);
            if(info.getData()==null)return null;
            if(!info.getDesvio().trim().equals(""))this.setDesvio(Float.parseFloat(info.getDesvio()));
            if(!info.getMaximo().trim().equals(""))this.setDiametroMaximo(Float.parseFloat(info.getMaximo()));
            if(!info.getMedia().trim().equals(""))this.setDiametroMedio(Float.parseFloat(info.getMedia()));
            if(!info.getMinimo().trim().equals(""))this.setDiametroMinimo(Float.parseFloat(info.getMinimo()));
            if(!info.getMetragem().trim().equals(""))this.setMetragem(Integer.parseInt(info.getMetragem()));            
            if((!info.getHora().trim().equals("")))this.setMicrometroHora(Time.valueOf(info.getHora()));
            return this;
        } catch (Exception e) {
            erro.gravaErro(e);
        }
        return null;
    } 
}
