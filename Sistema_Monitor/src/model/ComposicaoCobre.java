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
public class ComposicaoCobre {
    private int idPesagem;
    private double porcentagem;
    private String laminadora;

    public int getIdPesagem() {
        return idPesagem;
    }

    public void setIdPesagem(int idPesagem) {
        this.idPesagem = idPesagem;
    }

    public double getPorcentagem() {
        return porcentagem;
    }

    public void setPorcentagem(double porcentagem) {
        this.porcentagem = porcentagem;
    }

    public String getLaminadora() {
        return laminadora;
    }

    public void setLaminadora(String laminadora) {
        this.laminadora = laminadora;
    }
    
    
}
