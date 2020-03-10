package model;

public class Paradas {

    private int codigo;
    private String descricao;
    private String abreviacao;
    private String observacao;
    private int idRegistro;
    private int codPesagemSaida;
    private int codPesagemEntrada;
    
    public Paradas() {
        codigo = 0;
        descricao = "";
        abreviacao = "";
    }
    
    public int getCodPesagemEntrada() {
        return codPesagemEntrada;
    }

    public void setCodPesagemEntrada(int codPesagemEntrada) {
        this.codPesagemEntrada = codPesagemEntrada;
    }

    public int getCodPesagemSaida() {
        return codPesagemSaida;
    }

    public void setCodPesagemSaida(int codPesagem) {
        this.codPesagemSaida = codPesagem;
    }

    public int getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(int idRegistro) {
        this.idRegistro = idRegistro;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getAbreviacao() {
        return abreviacao;
    }

    public void setAbreviacao(String abreviacao) {
        this.abreviacao = abreviacao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
