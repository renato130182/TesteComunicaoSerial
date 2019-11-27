package model;

public class Pesagem {
    private String codigo;
    private String observacao;
    private String codEmbalagem;
    private Long metragemOperador;
    private Long saldoConsumo;
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

    public String getCodEmbalagem() {
        return codEmbalagem;
    }

    public void setCodEmbalagem(String codEmbalagem) {
        this.codEmbalagem = codEmbalagem;
    }

    public Long getSaldoConsumo() {
        return saldoConsumo;
    }

    public void setSaldoConsumo(Long saldoConsumo) {
        this.saldoConsumo = saldoConsumo;
    }
    

}
