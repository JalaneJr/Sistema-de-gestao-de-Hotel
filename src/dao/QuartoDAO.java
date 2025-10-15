package dao;

import model.Quarto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuartoDAO {

    public void inserir(Quarto quarto) {
        String sql = "INSERT INTO quartos (numero, tipo, preco, descricao, ofertas, imagem, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quarto.getNumero());
            stmt.setString(2, quarto.getTipo());
            stmt.setDouble(3, quarto.getPreco());
            stmt.setString(4, quarto.getDescricao());
            stmt.setString(5, quarto.getOfertas());
            stmt.setString(6, quarto.getImagem());
            stmt.setString(7, quarto.getStatus()); // novo campo

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void atualizar(Quarto quarto) {
        String sql = "UPDATE quartos SET numero=?, tipo=?, preco=?, descricao=?, ofertas=?, imagem=?, status=? WHERE id=?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quarto.getNumero());
            stmt.setString(2, quarto.getTipo());
            stmt.setDouble(3, quarto.getPreco());
            stmt.setString(4, quarto.getDescricao());
            stmt.setString(5, quarto.getOfertas());
            stmt.setString(6, quarto.getImagem());
            stmt.setString(7, quarto.getStatus()); // novo campo
            stmt.setInt(8, quarto.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletar(int id) {
        String sql = "DELETE FROM quartos WHERE id=?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Quarto> listarTodos() {
        List<Quarto> lista = new ArrayList<>();
        String sql = "SELECT * FROM quartos";

        try (Connection conn = Conexao.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Quarto quarto = new Quarto(
                        rs.getInt("id"),
                        rs.getInt("numero"),
                        rs.getString("tipo"),
                        rs.getDouble("preco"),
                        rs.getString("descricao"),
                        rs.getString("ofertas"),
                        rs.getString("imagem"),
                        rs.getString("status") // novo campo
                );
                lista.add(quarto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public void atualizarStatus(int quartoId, String status) {
        String sql = "UPDATE quartos SET status = ? WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, quartoId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            // Log e apresentação de mensagem segura
            e.printStackTrace();
        }
    }

}
