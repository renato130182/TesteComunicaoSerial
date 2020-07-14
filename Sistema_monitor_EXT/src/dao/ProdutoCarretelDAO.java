/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import controller.LogErro;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.Carretel;
import model.ProdutoCarretel;

/**
 *
 * @author renato.soares
 */
public class ProdutoCarretelDAO {
    private String sql;
    LogErro erro = new LogErro();
    
    public ProdutoCarretel buscaDadosProdutoCarretel(String produto, String carretel,String maquina){
        ProdutoCarretel prodCar = new ProdutoCarretel();
        ConexaoDatabase db = new ConexaoDatabase();
        try {
            sql = "SELECT metragempadrao as padrao, metragemmaxima as maxima,"
                    + "(select descricao from condumigproducao.embalagem where codigo = ?) as descEmb, "
                    + "codigoflange as flange FROM condumigproducao.produtometragem where codigoitem = ? "
                    + "and codigomaquina = ? and codigoflange = right(?,4);";
            Connection conec = db.getConnection();
            PreparedStatement st = conec.prepareStatement(sql);
            st.setString(1, carretel);
            st.setString(2, produto);
            st.setString(3, maquina);
            st.setString(4, carretel);
            ResultSet res = st.executeQuery();
            Carretel car = new Carretel();
            if(res.next()){                
                prodCar.setMetragemMaxima(res.getInt("maxima"));
                prodCar.setMetragemPadrao(res.getInt("padrao"));
                car.setCodigo(carretel);
                car.setDescricao(res.getString("DescEmb"));
                car.setFlange(res.getString("flange"));
                prodCar.setCarretel(car);                
            }else{
                prodCar.setMetragemMaxima(22000);
                prodCar.setMetragemPadrao(20000);
                car.setCodigo(carretel);
                car.setDescricao("Sem descrição");
                car.setFlange("Sem Flange");
                prodCar.setCarretel(car);                                
            }
            db.desconectar();
            return prodCar;
        } catch (SQLException e) {
            System.out.println("Falha ao buscar produto metragem..." + sql);
            e.printStackTrace();
            erro.gravaErro(e);
        }
        db.desconectar();
        return null;
    }
    
    public boolean verificaCarretelProdutoMetragem(String codEmbalagem, String codMaquina,String codItem){
        try {
            sql = "SELECT codigo FROM condumigproducao.produtometragem where codigoitem = ? "
                    + "and codigomaquina = ? and codigoflange = ?;";
            ConexaoDatabase db = new ConexaoDatabase();
            Connection conec = db.getConnection();
            PreparedStatement st = conec.prepareStatement(sql);
            st.setString(1, codItem);
            st.setString(2, codMaquina);
            st.setString(3, codEmbalagem.substring(3,7));            
            ResultSet res = st.executeQuery();
            return res.next();                                
        } catch (SQLException e) {
            e.printStackTrace();
            erro.gravaErro(e);
        }
        return false;
    }
    
}
