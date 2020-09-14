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
public class ContadorMetros {
    
    private Metrador metrador;
    private double metragemProduzida;
    private double metragemAtual;
    private long lastTime;    
    private long lastPuls;
    private long time;

    public ContadorMetros() {
        this.lastTime = System.currentTimeMillis();
        metrador = new Metrador();
        metragemProduzida = 0;
        metragemAtual = 0;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
    
    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public long getLastPuls() {
        return lastPuls;
    }

    public void setLastPuls(long lastPuls) {
        this.lastPuls = lastPuls;
    }
            
    public Metrador getMetrador() {
        return metrador;
    }

    public void setMetrador(Metrador metrador) {
        this.metrador = metrador;
    }

    public double getMetragemAtual() {
        return metragemAtual;
    }

    public void setMetragemAtual(double metragemAtual) {
        this.metragemAtual = metragemAtual;
    }

    public double getMetragemProduzida() {
        return metragemProduzida;
    }

    public void setMetragemProduzida(double metragemProduzida) {
        this.metragemProduzida = metragemProduzida;
    }
    
    
    
}
