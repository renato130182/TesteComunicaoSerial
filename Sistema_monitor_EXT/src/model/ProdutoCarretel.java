package model;

public class ProdutoCarretel {
    
    private Carretel carretel;

    public Carretel getCarretel() {
        return carretel;
    }

    public void setCarretel(Carretel carretel) {
        this.carretel = carretel;
    }
    private int metragemPadrao;
    private int metragemMaxima;
    
    public ProdutoCarretel() {
        metragemMaxima = 0;
        metragemPadrao = 0;
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
