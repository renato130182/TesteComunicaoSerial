/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geradorrelatorioindisponibilidademanutencao;

import java.sql.Date;
import java.sql.Time;

/**
 *
 * @author renato.soares
 */
public class Manutencao {
    private String codObjeto;
    private String CodOrdClb;
    private String CodTipTrb;
    private Date DatFimClb;
    private Date DatIniClb;
    private Time HoraInicio;
    private Time HoraFim;
    private long tempoManutencao;

    public String getCodObjeto() {
        return codObjeto;
    }

    public void setCodObjeto(String codObjeto) {
        this.codObjeto = codObjeto;
    }

    public String getCodOrdClb() {
        return CodOrdClb;
    }

    public void setCodOrdClb(String CodOrdClb) {
        this.CodOrdClb = CodOrdClb;
    }

    public String getCodTipTrb() {
        return CodTipTrb;
    }

    public void setCodTipTrb(String CodTipTrb) {
        this.CodTipTrb = CodTipTrb;
    }

    public Date getDatFimClb() {
        return DatFimClb;
    }

    public void setDatFimClb(Date DatFimClb) {
        this.DatFimClb = DatFimClb;
    }

    public Date getDatIniClb() {
        return DatIniClb;
    }

    public void setDatIniClb(Date DatIniClb) {
        this.DatIniClb = DatIniClb;
    }

    public Time getHoraInicio() {
        return HoraInicio;
    }

    public void setHoraInicio(Time HoraInicio) {
        this.HoraInicio = HoraInicio;
    }

    public Time getHoraFim() {
        return HoraFim;
    }

    public void setHoraFim(Time HoraFim) {
        this.HoraFim = HoraFim;
    }

    public long getTempoManutencao() {
        return tempoManutencao;
    }

    public void setTempoManutencao(long tempoManutencao) {
        this.tempoManutencao = tempoManutencao;
    }
        
}
