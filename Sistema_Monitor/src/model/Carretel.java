package model;
import controller.InterfaceCarretel;

public class Carretel implements InterfaceCarretel{

    private String codigo;
    private String descricao;
    private String Flange;

    public Carretel() {
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

    public String getFlange() {
        return Flange;
    }

    public void setFlange(String Flange) {
        this.Flange = Flange;
    }

    @Override
    public void buscaDadosCarretel() {
        
    }


}
