/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

/**
 *
 * @author renato.soares
 */
public class ProtocoloNazkomUDC1_default {
     private String produto;
     private String maquina;
     private String minimoPermitido;
     private String maximoPernitido;
     private String hora;
     private String metragem;
     private String media;
     private String desvio;
     private String minimo;
     private String maximo;
     private String ctl;
     private String leituraSerial;

    public void setLeituraSerial(String leituraSerial) {
        this.leituraSerial = leituraSerial;
        this.TratarProtocolo();
    }

     //metodo para tradas a linha recebida na serial
     private void TratarProtocolo(){
         
     }
     
    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public String getMaquina() {
        return maquina;
    }

    public void setMaquina(String maquina) {
        this.maquina = maquina;
    }

    public String getMinimoPermitido() {
        return minimoPermitido;
    }

    public void setMinimoPermitido(String minimoPermitido) {
        this.minimoPermitido = minimoPermitido;
    }

    public String getMaximoPernitido() {
        return maximoPernitido;
    }

    public void setMaximoPernitido(String maximoPernitido) {
        this.maximoPernitido = maximoPernitido;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getMetragem() {
        return metragem;
    }

    public void setMetragem(String metragem) {
        this.metragem = metragem;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getDesvio() {
        return desvio;
    }

    public void setDesvio(String desvio) {
        this.desvio = desvio;
    }

    public String getMinimo() {
        return minimo;
    }

    public void setMinimo(String minimo) {
        this.minimo = minimo;
    }

    public String getMaximo() {
        return maximo;
    }

    public void setMaximo(String maximo) {
        this.maximo = maximo;
    }

    public String getCtl() {
        return ctl;
    }

    public void setCtl(String ctl) {
        this.ctl = ctl;
    }
    
    
     
    
    
}
