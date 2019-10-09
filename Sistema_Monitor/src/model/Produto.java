package model;

import controller.InterfaceProduto;

public class Produto implements InterfaceProduto {

    private int codigo;

    private String descricao;

    private float diametroMaximo;

    private float diametroMinimo;

    public Produto(int codigo) {
    }

    public int getCodigo() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int setCodigo(int codigo) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getDescricao() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String setDescricao(String descricao) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public float getDiametroMaximo() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public float setDiametroMaximo(float diametroMaximo) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public float getDiametroMinimo() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public float setDiametroMinimo(float diametroMinimo) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void buscaDadosProduto() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
