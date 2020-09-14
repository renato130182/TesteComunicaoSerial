package model;

import controller.InterfaceProduto;

public class Produto implements InterfaceProduto {
    
    private float diametroMaximo;
    private float diametroMinimo;
    private float diametroNominal;
    public  Item item;
    public float getDiametroNominal() {
        return diametroNominal;
    }

    public void setDiametroNominal(float diametroNominal) {
        this.diametroNominal = diametroNominal;
    }

    public Produto() {
        this.item = new Item();
    }

    public Produto(Item item, float diametroMinimo, float diametroNominal, float diametroMaximo) {
        this.item = item;
        this.diametroMaximo = diametroMaximo;
        this.diametroMinimo = diametroMinimo;
        this.diametroNominal = diametroNominal;
    }

    public Produto(Item item) {
        this.item = item;
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
