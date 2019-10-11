package model;

public class ProgramacaoMaquina {

    private long metragemTotalProgramada;
    private String loteproducao;
    private int quantidadeProgramada;
    private int quantidadeProduzida;
    private long metragemProgramada;
    private String dataProgramada;
    private Produto produto;


    public ProgramacaoMaquina() {
    }

    public String getDataProgramada() {
        return dataProgramada;
    }

    public void setDataProgramada(String dataProgramada) {
        this.dataProgramada = dataProgramada;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public long getMetragemTotalProgramada() {
        return metragemTotalProgramada;
    }

    public void setMetragemTotalProgramada(long metragemTotalProgramada) {
        this.metragemTotalProgramada = metragemTotalProgramada;
    }

    public String getLoteproducao() {
        return loteproducao;
    }

    public void setLoteproducao(String loteproducao) {
        this.loteproducao = loteproducao;
    }

    public int getQuantidadeProgramada() {
        return quantidadeProgramada;
    }

    public void setQuantidadeProgramada(int quantidadeProgramada) {
        this.quantidadeProgramada = quantidadeProgramada;
    }

    public int getQuantidadeProduzida() {
        return quantidadeProduzida;
    }

    public void setQuantidadeProduzida(int quantidadeProduzida) {
        this.quantidadeProduzida = quantidadeProduzida;
    }

    public long getMetragemProgramada() {
        return metragemProgramada;
    }

    public void setMetragemProgramada(long metragemProgramada) {
        this.metragemProgramada = metragemProgramada;
    }
}
