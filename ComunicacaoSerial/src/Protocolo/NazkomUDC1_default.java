/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Protocolo;

/**
 *
 * @author renato.soares
 */
public class NazkomUDC1_default {

    private String produto="";
    private String maquina="";
    private String minimoPermitido="";
    private String maximoPernitido="";
    private String data="";
    private String hora="";
    private String metragem="";
    private String media="";
    private String desvio="";
    private String minimo="";
    private String maximo="";
    private String ctl="";
    private String leituraSerial;

    public void TratarDadosSerial() {
        //definir linha recebida
        String linha = leituraSerial.toLowerCase();
        if (linha.contains("Controle estatistico".toLowerCase())
                || linha.contains("Data".toLowerCase())
                || linha.contains("Horario".toLowerCase())) {
            linha1cabecalho();
        } else if (linha.contains("Produto".toLowerCase())
                || linha.contains("Maquina".toLowerCase())
                || linha.contains("REG".toLowerCase())) {
            linha2Cabecalho();
        } else if (linha.contains("TMP".toLowerCase())
                || linha.contains("CMP".toLowerCase())
                || linha.contains("DSV".toLowerCase())) {
            linha3cabecalho();
        } else {
            linhaDados();
        }
    }

    private void linha1cabecalho() {
        boolean dt=false,hr=false;
        String[] dados = leituraSerial.split(" ");
        if (dados.length >= 6) {
            for (int i = 1; i < dados.length; i++) {
                if(dt==false && dados[i].equals(":")){
                    if(dados[i+1].contains("-")){
                        data=dados[i+1].trim();
                        dt=true;                        
                    }
                }
                if(hr==false && dados[i].equals(":")){
                    if(dados[i+1].contains(":")){
                        hora=dados[i+1].trim();
                        hr=true;
                        break;
                    }
                }
            }
        }
    }

    private void linha2Cabecalho() {
        boolean prd=false,maq=false;
        String[] dados = leituraSerial.split("-");
        if(dados.length==3){
            for (int i=0;i<dados.length;i++){
                String aux[] = dados[i].split(":");
                if(aux.length>1){
                    if(prd==false){
                        produto=aux[1];
                        prd=true;
                    }else if(maq==false){
                        maquina=aux[1];
                        break;
                    }
                }else{
                    break;
                }
            }
        }
    }
    private void linha3cabecalho() {
        boolean min=false,max=false;   
        String[] dados = leituraSerial.split(" ");
        if(dados.length>40){
            for (int i=dados.length-1;i>=0;i--){
                if(!dados[i].trim().equals("")){
                    if(max==false){
                        maximoPernitido=dados[i];
                        max=true;
                    }else if(min==false){
                        minimoPermitido=dados[i];
                        break;
                    }
                }
            }
        }
    }

    private void linhaDados() {
        boolean hr=false,mt=false,med=false,dsv=false,min=false,max=false,ct=false;
        String linha = leituraSerial.trim();
        if(linha.length()==79){
            String dados[]=linha.split(" ");
            for (int i=0;i<dados.length;i++){
                if(!dados[i].trim().equals("") && !dados[i].trim().equals(">")&& !dados[i].trim().equals("<")){
                    if(hr==false){
                        hora=dados[i].trim();
                        hr=true;
                    }else if(mt==false){
                        metragem=dados[i].trim();
                        mt=true;
                    }else if (med==false){
                        media=dados[i].trim();
                        med=true;
                    }else if (dsv==false){
                        desvio=dados[i].trim();
                        dsv=true;
                    }else if (min==false){
                        minimo=dados[i].trim();
                        min=true;
                    }else if (max==false){
                        maximo=dados[i].trim();
                        max=true;
                    }else if (ct==false){
                        ctl=dados[i].trim();
                        break;
                    }
                }
            }
        }        
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
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

    public String getLeituraSerial() {
        return leituraSerial;
    }

    public void setLeituraSerial(String leituraSerial) {
        this.leituraSerial = leituraSerial;
        this.TratarDadosSerial();
    }
}
