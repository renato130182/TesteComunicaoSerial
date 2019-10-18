package model;

public class Pesagem {
    private String codigo;
    private String observacao;
    private Long metragemOperador;
    public Pesagem() {
    }

    public Long getMetragemOperador() {
        return metragemOperador;
    }

    public void setMetragemOperador(Long metragemOperador) {
        this.metragemOperador = metragemOperador;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
    

}
