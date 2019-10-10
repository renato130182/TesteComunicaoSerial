package model;

public class ProdutoCarretel {

    private Produto produto;
    private Carretel carretel;
    private int metragemPadrao;
    private int metragemMaxima;

    public ProdutoCarretel() {
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Carretel getCarretel() {
        return carretel;
    }

    public void setCarretel(Carretel carretel) {
        this.carretel = carretel;
    }

   

    public int getMetragemPadrao() {
        return metragemPadrao;
    }

    public void setMetragemPadrao(int metragemPadrao) {
        this.metragemPadrao = metragemPadrao;
    }

    public int getMetragemMaxima() {
        return metragemMaxima;
    }

    public void setMetragemMaxima(int metragemMaxima) {
        this.metragemMaxima = metragemMaxima;
    }

    
    
}
