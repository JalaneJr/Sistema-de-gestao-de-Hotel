package dao;

import model.Tarifa;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TarifaDAO {

    public void inserir(Tarifa t) throws SQLException {
        String sql = "INSERT INTO tarifas(tipoQuarto, preco, promocao) VALUES(?,?,?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, t.getTipoQuarto());
            stmt.setDouble(2, t.getPreco());
            stmt.setBoolean(3, t.isPromocao());
            stmt.executeUpdate();

            // Recupera o ID gerado
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                t.setId(rs.getInt(1));
            }
        }
    }

    public List<Tarifa> listarTodas() throws SQLException {
        List<Tarifa> lista = new ArrayList<>();
        String sql = "SELECT * FROM tarifas";
        try (Connection conn = Conexao.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Tarifa t = new Tarifa();
                t.setId(rs.getInt("id"));
                t.setTipoQuarto(rs.getString("tipoQuarto"));
                t.setPreco(rs.getDouble("preco"));
                t.setPromocao(rs.getBoolean("promocao"));
                lista.add(t);
            }
        }
        return lista;
    }

    public void atualizar(Tarifa t) throws SQLException {
        String sql = "UPDATE tarifas SET tipoQuarto=?, preco=?, promocao=? WHERE id=?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, t.getTipoQuarto());
            stmt.setDouble(2, t.getPreco());
            stmt.setBoolean(3, t.isPromocao());
            stmt.setInt(4, t.getId());
            stmt.executeUpdate();
        }
    }

    public void atualizarPromocao(Tarifa t) throws SQLException {
    String sql = "UPDATE tarifas SET promocao=? WHERE id=?";
    try (Connection conn = Conexao.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setBoolean(1, t.isPromocao());
        stmt.setInt(2, t.getId());
        stmt.executeUpdate();
    }
}

    public void deletar(Tarifa t) throws SQLException {
        String sql = "DELETE FROM tarifas WHERE id=?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, t.getId());
            stmt.executeUpdate();
        }
    }

    // Listar todas as tarifas
    public Tarifa[] listarTodos() {
        List<Tarifa> lista = new ArrayList<>();
        String sql = "SELECT * FROM tarifas";

        try (Connection conn = Conexao.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Tarifa t = new Tarifa();
                t.setId(rs.getInt("id"));
                t.setTipoQuarto(rs.getString("tipoQuarto"));
                t.setPreco(rs.getDouble("preco"));
                t.setPromocao(rs.getBoolean("promocao"));
                lista.add(t);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Converter lista para array
        return lista.toArray(new Tarifa[0]);
    }
}
