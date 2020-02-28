/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dao.ConexaoDatabase;
import dao.MicrometroDAO;
import java.sql.Connection;
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
            if((!info.getHora().trim().equals(""))){
                this.setMicrometroHora(Time.valueOf(info.getHora()));
            }else{
                System.out.println("dados inválidos");
                return null;
            }
            if(!info.getMetragem().trim().equals("")){
                this.setMetragem(Integer.parseInt(info.getMetragem()));
            }else{
                System.out.println("dados inválidos");
                return null;
            }
            if(!info.getDesvio().trim().equals(""))this.setDesvio(Float.parseFloat(info.getDesvio()));
            if(!info.getMaximo().trim().equals(""))this.setDiametroMaximo(Float.parseFloat(info.getMaximo()));
            if(!info.getMedia().trim().equals(""))this.setDiametroMedio(Float.parseFloat(info.getMedia()));
            if(!info.getMinimo().trim().equals(""))this.setDiametroMinimo(Float.parseFloat(info.getMinimo()));
                       
            
            return this;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return null;
    } 
    
    public boolean registraRelatorioMicrometro(Micrometro dados,String codMaquina,String lote, int metros,int velocidade){
        try {
            ConexaoDatabase db = new ConexaoDatabase();
            if(db.isInfoDB()){
                Connection conec = db.getConnection();     
                if(conec==null)return false;
                MicrometroDAO dao = new MicrometroDAO(conec);
                if(dao.registraDadosMicrometro(dados, codMaquina, lote, metros,velocidade)){
                    db.desconectar();
                    return true;
                }else{
                    db.desconectar();
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return false;
    }
    
}
