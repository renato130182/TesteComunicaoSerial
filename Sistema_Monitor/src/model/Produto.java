package model;

import controller.InterfaceProduto;

public class Produto implements InterfaceProduto {

    private String codigo;
    private String descricao;
    private float diametroMaximo;
    private float diametroMinimo;

    public Produto() {
    }

    public Produto(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }
    
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public float getDiametroMaximo() {
        return diametroMaximo;
    }

    public void setDiametroMaximo(float diametroMaximo) {
        this.diametroMaximo = diametroMaximo;
    }

    public float getDiametroMinimo() {
        return diametroMinimo;
    }

    public void setDiametroMinimo(float diametroMinimo) {
        this.diametroMinimo = diametroMinimo;
    }

    @Override
    public void buscaDadosProduto() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
}
