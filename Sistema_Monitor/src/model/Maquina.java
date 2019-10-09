package model;

import controller.InterfaceMaquina;

public class Maquina implements InterfaceMaquina {

    private String codigo;
    private String descricao;
    private float alertaPercentualVelocidade;
    private int alertaMetrosParaArrebentamento;

    public Maquina() {
        codigo="";
        descricao="";
        alertaPercentualVelocidade=0.f;
        alertaMetrosParaArrebentamento=0;
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

    public float getAlertaPercentualVelocidade() {
        return alertaPercentualVelocidade;
    }

    public void setAlertaPercentualVelocidade(float alertaPercentualVelocidade) {
        this.alertaPercentualVelocidade = alertaPercentualVelocidade;
    }

    public int getAlertaMetrosParaArrebentamento() {
        return alertaMetrosParaArrebentamento;
    }

    public void setAlertaMetrosParaArrebentamento(int alertaMetrosParaArrebentamento) {
        this.alertaMetrosParaArrebentamento = alertaMetrosParaArrebentamento;
    }

    @Override
    public void buscaDadosMaquina() {
        
    }   
}
