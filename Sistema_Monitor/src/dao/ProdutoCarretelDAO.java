/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.ProdutoCarretel;

/**
 *
 * @author renato.soares
 */
public class ProdutoCarretelDAO {
    private String sql;
    
    public ProdutoCarretel buscaDadosProdutoCarretel(String produto, String carretel,String maquina){
        ProdutoCarretel prodCar = new ProdutoCarretel();
        ConexaoDatabase db = new ConexaoDatabase();
        try {
            sql = "SELECT metragempadrao as padrao, metragemmaxima as maxima FROM "
                    + "condumigproducao.produtometragem where codigoitem = ? and "
                    + "codigomaquina = ? and codigoflange = right(?,4);";
            Connection conec = db.getConnection();
            PreparedStatement st = conec.prepareStatement(sql);
            st.setString(1, produto);
            st.setString(2, maquina);
            st.setString(3, carretel);
            ResultSet res = st.executeQuery();
            if(res.next()){
                prodCar.setMetragemMaxima(res.getInt("maxima"));
                prodCar.setMetragemPadrao(res.getInt("padrao"));
                db.desconectar();
                return prodCar;
            }
        } catch (SQLException e) {
            System.err.println("Falha ao buscar item em producao" + e.getMessage());
        }
        db.desconectar();
        return null;
    }
}
