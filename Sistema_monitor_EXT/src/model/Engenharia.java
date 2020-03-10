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
public class Engenharia {
    private String codItem;
    private String descricao;
    private String lote;
    private String emb;
    private int pesagem;
    private double quantidade;
    private String unidade;

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }


    public String getCodItem() {
        return codItem;
    }

    public void setCodItem(String codItem) {
        this.codItem = codItem;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public String getEmb() {
        return emb;
    }

    public void setEmb(String emb) {
        this.emb = emb;
    }

    public int getPesagem() {
        return pesagem;
    }

    public void setPesagem(int pesagem) {
        this.pesagem = pesagem;
    }

    
}
