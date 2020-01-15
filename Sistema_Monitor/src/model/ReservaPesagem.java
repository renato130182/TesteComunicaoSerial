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
public class ReservaPesagem {
   private String codigoPesagem;
   private int sequencia;
   private String itemReserva;
   private String loteReserva;
   private int qtosfios;
   private String codigoEmbalagem;
   private double quantidade;
   private String codigoEmbalagelTroca;
   private String loteReservaTroca;
   private int idPesagem;
   private int idMatPrima;
   private String itemDescricao;
   private String unidade;

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }
    public String getItemDescricao() {
        return itemDescricao;
    }

    public void setItemDescricao(String itemDescricao) {
        this.itemDescricao = itemDescricao;
    }

    public String getCodigoPesagem() {
        return codigoPesagem;
    }

    public void setCodigoPesagem(String codigoPesagem) {
        this.codigoPesagem = codigoPesagem;
    }

    public int getSequencia() {
        return sequencia;
    }

    public void setSequencia(int sequencia) {
        this.sequencia = sequencia;
    }

    public String getItemReserva() {
        return itemReserva;
    }

    public void setItemReserva(String itemReserva) {
        this.itemReserva = itemReserva;
    }

    public String getLoteReserva() {
        return loteReserva;
    }

    public void setLoteReserva(String loteReserva) {
        this.loteReserva = loteReserva;
    }

    public int getQtosfios() {
        return qtosfios;
    }

    public void setQtosfios(int qtosfios) {
        this.qtosfios = qtosfios;
    }

    public String getCodigoEmbalagem() {
        return codigoEmbalagem;
    }

    public void setCodigoEmbalagem(String codigoEmbalagem) {
        this.codigoEmbalagem = codigoEmbalagem;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public String getCodigoEmbalagelTroca() {
        return codigoEmbalagelTroca;
    }

    public void setCodigoEmbalagelTroca(String codigoEmbalagelTroca) {
        this.codigoEmbalagelTroca = codigoEmbalagelTroca;
    }

    public String getLoteReservaTroca() {
        return loteReservaTroca;
    }

    public void setLoteReservaTroca(String loteReservaTroca) {
        this.loteReservaTroca = loteReservaTroca;
    }

    public int getIdPesagem() {
        return idPesagem;
    }

    public void setIdPesagem(int idPesagem) {
        this.idPesagem = idPesagem;
    }

    public int getIdMatPrima() {
        return idMatPrima;
    }

    public void setIdMatPrima(int idMatPrima) {
        this.idMatPrima = idMatPrima;
    }         
}
