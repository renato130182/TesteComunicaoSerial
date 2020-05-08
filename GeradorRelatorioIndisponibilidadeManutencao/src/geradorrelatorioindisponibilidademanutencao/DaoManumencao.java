/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geradorrelatorioindisponibilidademanutencao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author renato.soares
 */
public class DaoManumencao {
    private final Connection conec;
    private String sql;

    public DaoManumencao(Connection conec) {
        this.conec = conec;
    }
    
    public List<Manutencao> buscaListaManutencoes(){
        List<Manutencao> manutencao = new ArrayList<>();
         try {
            sql = "SELECT CodObjeto,CodOrdClb,CodTipTrb,DatFimClb,DatIniClb "
                    + "FROM Calibra where  not IsNull(Calibra.CodOrdClb)";
            PreparedStatement st = conec.prepareStatement(sql);                       
            ResultSet res = st.executeQuery();
            while(res.next()){
                if(null!=res.getString("DatIniClb") && null!=res.getString("DatFimClb")){                                   
                    Manutencao man = new Manutencao();
                    man.setCodObjeto(res.getString("CodObjeto"));
                    man.setCodOrdClb(res.getString("CodOrdClb"));
                    System.out.println(res.getString("CodOrdClb"));
                    man.setCodTipTrb(res.getString("CodTipTrb"));
                    man.setDatIniClb(res.getDate("DatIniClb"));
                    man.setDatFimClb(res.getDate("DatFimClb"));
                    man.setHoraInicio(res.getTime("DatIniClb"));
                    man.setHoraFim(res.getTime("DatFimClb"));                        
                    manutencao.add(man);                    
                }
            }                
            return manutencao;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public List<Time> buscaHorarioTurno(Date data, String codMaquina){
        List<Time> horario = new ArrayList<>();
        try {
            sql = "select entrada,saida from condumigproducao.turno t " +
                "inner join condumigproducao.disponibilidademaquina d on t.codigo = d.codTurno " +
                "where codMaquina = ? and `data` = ?;";
            PreparedStatement st = conec.prepareStatement(sql);      
            st.setString(1, codMaquina);
            st.setDate(2, data);
            ResultSet res = st.executeQuery();
            if(res.next()){
                horario.add(new Time(res.getTime("entrada").getTime()));
                horario.add(new Time(res.getTime("saida").getTime()));                
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Falha Ao buscar horario de inicio do turno");
        }
        return horario;
    }

    public long buscaDisponibilidadeMaquinaData(String codObjeto, Date data) {
        try {
            sql = "SELECT HorasDisp FROM condumigproducao.disponibilidademaquina "
                    + "where codMaquina = ? and `data` = ?;";
            PreparedStatement st = conec.prepareStatement(sql);      
            st.setString(1, codObjeto);
            st.setDate(2, data);
            ResultSet res = st.executeQuery();
            if(res.next()){
                return res.getTime("HorasDisp").getTime();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean validaMaquinaProducao(String codObjeto) {
        try {
            sql = "SELECT situacao FROM condumigproducao.maquina where codigo = ?;";
            PreparedStatement st = conec.prepareStatement(sql);      
            st.setString(1, codObjeto);            
            ResultSet res = st.executeQuery();
            return res.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    boolean registrarListaManut(List<Manutencao> listaManut, String man_table) {
        try {
            removerRegistrosAnteriores(man_table);
            if(criarTabelaTemporario(man_table)){                
                for (int i=0;i<listaManut.size();i++){
                    sql = "insert into condumigproducao."+man_table+" (codObjeto, "
                            + "CodTipTrb,CodOrdClb, DatFimClb, DatIniClb, HoraInicio, HoraFim,"
                            + " tempoManutencao)Values(?,?,?,?,?,?,?,?);";
                    PreparedStatement st = conec.prepareStatement(sql);      
                    st.setString(1, listaManut.get(i).getCodObjeto());            
                    st.setString(2, listaManut.get(i).getCodTipTrb());
                    st.setString(3, listaManut.get(i).getCodOrdClb());
                    st.setString(4, listaManut.get(i).getDatFimClb().toString());
                    st.setString(5, listaManut.get(i).getDatIniClb().toString());
                    st.setString(6,listaManut.get(i).getHoraInicio().toString());
                    st.setString(7,listaManut.get(i).getHoraFim().toString());
                    st.setInt(8,(int) listaManut.get(i).getTempoManutencao());
                    System.out.println(sql);
                    st.execute();
                    //System.out.println(listaManut.get(i));
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void removerRegistrosAnteriores(String man_table) {
        try {
            sql = "drop table if exists condumigproducao." + man_table + ";";
            PreparedStatement st = conec.prepareStatement(sql);                         
            st.execute();            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean criarTabelaTemporario(String man_table) {
        try {
            sql = "CREATE TABLE `condumigproducao`.`"+man_table+"` (" +
                    "  `codObjeto` VARCHAR(45) NULL," +
                    "  `CodTipTrb` VARCHAR(45) NULL," +
                    "  `CodOrdClb` VARCHAR(45) NULL," +
                    "  `DatFimClb` VARCHAR(45) NULL," +
                    "  `DatIniClb` VARCHAR(45) NULL," +
                    "  `HoraInicio` VARCHAR(45) NULL," +
                    "  `HoraFim` VARCHAR(45) NULL," +
                    "  `tempoManutencao` INT(10) NULL" +
                    ");";
            Statement stnt = (Statement) conec.createStatement();
            stnt.execute(sql);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Time> buscaTurnoUTL() {
     List<Time> horario = new ArrayList<>();
        try {
            sql = "select entrada,saida from condumigproducao.turno t " +
                "where codigo = '014'";
            PreparedStatement st = conec.prepareStatement(sql);      
            ResultSet res = st.executeQuery();
            if(res.next()){
                horario.add(new Time(res.getTime("entrada").getTime()));
                horario.add(new Time(res.getTime("saida").getTime()));                
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Falha ao buscar turno de utilidades COD:014");
        }
        return horario;
    }            
}
