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
public class ReservaMaquina {
    private long codigoReserva;
    private int seuqencia;
    private String codigoMaquina;
    private String codigoOperador;
    private String CodItemProd;
    private String loteItemRes;
    private int qtosFios;
    private String codigoembalagem;
    private String CodItemRes;
    private double quantItemRes;
    private String loteProducao;
    private String tipoExtrusao;
    private int pesagem;

    public long getCodigoReserva() {
        return codigoReserva;
    }

    public void setCodigoReserva(long codigoReserva) {
        this.codigoReserva = codigoReserva;
    }

    public String getCodItemProd() {
        return CodItemProd;
    }

    public void setCodItemProd(String CodItemProd) {
        this.CodItemProd = CodItemProd;
    }

    public String getCodItemRes() {
        return CodItemRes;
    }

    public void setCodItemRes(String CodItemRes) {
        this.CodItemRes = CodItemRes;
    }

    
    public long getcodigoReserva() {
        return codigoReserva;
    }

    public void setcodigoReserva(long codigo) {
        this.codigoReserva = codigo;
    }
    
    public int getSeuqencia() {
        return seuqencia;
    }

    public void setSeuqencia(int seuqencia) {
        this.seuqencia = seuqencia;
    }

    public String getCodigoMaquina() {
        return codigoMaquina;
    }

    public void setCodigoMaquina(String codigoMaquina) {
        this.codigoMaquina = codigoMaquina;
    }

    public String getCodigoOperador() {
        return codigoOperador;
    }

    public void setCodigoOperador(String codigoOperador) {
        this.codigoOperador = codigoOperador;
    }



    public void setItemProd(Item ItemProd) {
        ItemProd = ItemProd;
    }

    public String getLoteItemRes() {
        return loteItemRes;
    }

    public void setLoteItemRes(String loteItemRes) {
        this.loteItemRes = loteItemRes;
    }

    public int getQtosFios() {
        return qtosFios;
    }

    public void setQtosFios(int qtosFios) {
        this.qtosFios = qtosFios;
    }

    public String getCodigoembalagem() {
        return codigoembalagem;
    }

    public void setCodigoembalagem(String codigoembalagem) {
        this.codigoembalagem = codigoembalagem;
    }

    public double getQuantItemRes() {
        return quantItemRes;
    }

    public void setQuantItemRes(double quantItemRes) {
        this.quantItemRes = quantItemRes;
    }

    public String getLoteProducao() {
        return loteProducao;
    }

    public void setLoteProducao(String loteProducao) {
        this.loteProducao = loteProducao;
    }

    public String getTipoExtrusao() {
        return tipoExtrusao;
    }

    public void setTipoExtrusao(String tipoExtrusao) {
        this.tipoExtrusao = tipoExtrusao;
    }

    public int getPesagem() {
        return pesagem;
    }

    public void setPesagem(int pesagem) {
        this.pesagem = pesagem;
    }    
}
