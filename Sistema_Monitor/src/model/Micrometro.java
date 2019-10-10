package model;

import java.sql.Time;
import java.util.Date;

public class Micrometro {

    private int metragem;
    private Time micrometroHora;
    private Date micrometroData;
    private Produto produto;
    private float diametroMedio;
    private float desvio;
    private float diametroMaximo;
    private float diametroMinimo;

    public Micrometro() {
    }

    public int getMetragem() {
        return metragem;
    }

    public void setMetragem(int metragem) {
        this.metragem = metragem;
    }

    public Time getMicrometroHora() {
        return micrometroHora;
    }

    public void setMicrometroHora(Time micrometroHora) {
        this.micrometroHora = micrometroHora;
    }

    public Date getMicrometroData() {
        return micrometroData;
    }

    public void setMicrometroData(Date micrometroData) {
        this.micrometroData = micrometroData;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }


    public float getDiametroMedio() {
        return diametroMedio;
    }

    public void setDiametroMedio(float diametroMedio) {
        this.diametroMedio = diametroMedio;
    }

    public float getDesvio() {
        return desvio;
    }

    public void setDesvio(float desvio) {
        this.desvio = desvio;
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

}
