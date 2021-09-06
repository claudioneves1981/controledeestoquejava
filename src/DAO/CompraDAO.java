package DAO;

import connection.ConnectionFactory;
import models.Compra;
import models.Produto;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CompraDAO {
    
    ConnectionFactory con  = new ConnectionFactory();
    ProdutoDAO pdao = new ProdutoDAO();
    UsuarioDAO udao = new UsuarioDAO();

    public boolean comprar(Compra c) {

        boolean sucess;
        con.getConnection();
        //Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmt = null;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {

            stmt = con.con.prepareStatement("INSERT INTO compras (idUsuario, valor, dataCompra, dataEntrega) " +
                    "VALUES (?, ?, ?, ?);");
            stmt.setInt(1, c.getUsuario().getId());
            stmt.setDouble(2, c.getValor());

            String dcompra = dateFormat.format(c.getData());
            stmt.setDate(3, java.sql.Date.valueOf(dcompra));

            String dentrega = dateFormat.format(c.getDataEntrega());
            stmt.setDate(4, java.sql.Date.valueOf(dentrega));

            stmt.executeUpdate();

            for (Produto p: c.getProdutos()) {

                stmt = null;
                con.getConnection();
                //Connection conn = ConnectionFactory.getConnection();

                stmt = con.con.prepareStatement("INSERT INTO produtos_compra (idCompra, idProduto, qtdProduto, precoUnProduto) " +
                        "VALUES (?, ?, ?, ?);");
                stmt.setInt(1, getIdCompra());
                stmt.setInt(2, p.getId());
                stmt.setDouble(3, p.getQtd());
                stmt.setDouble(4, p.getPreco());

                //stmt.executeUpdate();
                ConnectionFactory.closeConnection(con.con, stmt);
            }
            sucess = true;
        } catch (SQLException e) {
            e.printStackTrace();
            sucess = false;
        } finally {
            ConnectionFactory.closeConnection(con.con, stmt);
        }
        return sucess;

    }

    public int getIdCompra() throws SQLException {
        con.getConnection();
       // Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmt;
        ResultSet rs;

        stmt = con.con.prepareStatement("SELECT `AUTO_INCREMENT` FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'controlx' AND TABLE_NAME = 'compras'");
        rs = stmt.executeQuery();
        if (rs.next()) {
            int id = (rs.getInt("AUTO_INCREMENT"));
            return (id - 1);
        }
        else
            return 9999;

    }

    public boolean up(Compra c) throws ClassNotFoundException {
        con.getConnection();
        //Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmt = null;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dcompra = dateFormat.format(c.getDataFinal());
        boolean sucess;

        try {
            stmt = con.con.prepareStatement("UPDATE compras SET status = ?, dataFinal = ? WHERE id = ?;");
            stmt.setInt(1, c.getStatus());
            stmt.setDate(2, java.sql.Date.valueOf(dcompra));
            stmt.setInt(3, c.getId());

            stmt.executeUpdate();
            sucess = true;
        } catch (SQLException e) {
            e.printStackTrace();
            sucess = false;
        } finally {
            ConnectionFactory.closeConnection(con.con, stmt);
        }

        return sucess;
    }

    public Compra read(Compra c) throws ClassNotFoundException {
        con.getConnection();
        //Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Compra compra = new Compra();

       // DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        try {
            stmt = con.con.prepareStatement("SELECT * FROM compras WHERE id = ?");
            stmt.setInt(1, c.getId());
            rs = stmt.executeQuery();

            if (rs.next()) {
                compra.setId(rs.getInt("id"));
                compra.setUsuario(udao.read(rs.getInt("idUsuario")));
                compra.setStatus(rs.getInt("status"));
                compra.setValor(rs.getDouble("valor"));
                compra.setStatus(rs.getInt("status"));
                compra.setData(rs.getDate("dataCompra"));
                compra.setDataEntrega(rs.getDate("dataEntrega"));
                compra.setDataFinal(rs.getDate("dataFinal"));
            }

            //Produtos da Compra

                stmt = con.con.prepareStatement("SELECT * FROM produtos_compra WHERE idCompra = ?");
                stmt.setInt(1, c.getId());
                rs = stmt.executeQuery();

                List <Produto> lista = new ArrayList<>();
                while (rs.next()) {
                    Produto p = pdao.readAll(rs.getInt("idProduto"));
                    p.setQtd(rs.getDouble("qtdProduto"));
                    p.setPreco(rs.getDouble("precoUnProduto"));

                    lista.add(p);
                }

                compra.setProdutos(lista);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionFactory.closeConnection(con.con, stmt, rs);
        }
        return compra;
    }

    public Compra read(int id) throws ClassNotFoundException {
        con.getConnection();
        //Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Compra compra = new Compra();

        //DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        try {
            stmt = con.con.prepareStatement("SELECT * FROM compras WHERE id = ?");
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                compra.setId(rs.getInt("id"));
                compra.setUsuario(udao.read(rs.getInt("idUsuario")));
                compra.setValor(rs.getDouble("valor"));
                compra.setStatus(rs.getInt("status"));

                compra.setData(rs.getDate("dataCompra"));

                compra.setDataEntrega(rs.getDate("dataEntrega"));

                compra.setDataFinal(rs.getDate("dataFinal"));

            }

            //Produtos da Compra

            stmt = con.con.prepareStatement("SELECT * FROM produtos_compra WHERE idCompra = ?");
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            List <Produto> lista = new ArrayList<>();
            while (rs.next()) {
                Produto p = pdao.readAll(rs.getInt("idProduto"));
                p.setQtd(rs.getDouble("qtdProduto"));
                p.setPreco(rs.getDouble("precoUnProduto"));

                lista.add(p);
            }

            compra.setProdutos(lista);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionFactory.closeConnection(con.con, stmt, rs);
        }
        return compra;
    }

    public List<Compra> listAll() throws ClassNotFoundException {
        con.getConnection();
        //Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Compra> lista = new ArrayList<>();
       // DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");


        try {
            stmt = con.con.prepareStatement("SELECT * FROM compras");
            rs = stmt.executeQuery();

            while (rs.next()) {
                Compra compra = new Compra();
                compra.setId(rs.getInt("id"));
                compra.setUsuario(udao.read(rs.getInt("idUsuario")));
                compra.setValor(rs.getDouble("valor"));
                compra.setStatus(rs.getInt("status"));

                compra.setData(rs.getDate("dataCompra"));

                compra.setDataEntrega(rs.getDate("dataEntrega"));

                compra.setDataFinal(rs.getDate("dataFinal"));

                lista.add(compra);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionFactory.closeConnection(con.con, stmt, rs);

        }

        return lista;
    }


}

