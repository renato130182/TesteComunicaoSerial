package model;

public class Pesagem {
    private String codigo;
    private String observacao;
    private String codEmbalagem;
    private Long metragemOperador;
    private Long saldoConsumo;
    private String codItem;
    private String DecItem;
    private int qtosFios;
    private String lote;

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public int getQtosFios() {
        return qtosFios;
    }

    public void setQtosFios(int qtosFios) {
        this.qtosFios = qtosFios;
    }
    
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

    public String getCodItem() {
        return codItem;
    }

    public void setCodItem(String codItem) {
        this.codItem = codItem;
    }

    public String getDecItem() {
        return DecItem;
    }

    public void setDecItem(String DecItem) {
        this.DecItem = DecItem;
    }
    

}
