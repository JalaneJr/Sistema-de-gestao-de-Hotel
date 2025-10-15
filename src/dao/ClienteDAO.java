package dao;

import model.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    // 🔹 Inserir cliente (com origem do cadastro)
    public void inserir(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO clientes (nome, email, telefone, documento, cadastrado_por) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getEmail());
            stmt.setString(3, cliente.getTelefone());
            stmt.setString(4, cliente.getDocumento());
            stmt.setString(5, cliente.getCadastradoPor() == null ? "CLIENTE" : cliente.getCadastradoPor());

            stmt.executeUpdate();

            // Atualiza o ID gerado automaticamente
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    cliente.setId(rs.getInt(1));
                }
            }
        }
    }

    //  Conta total de clientes cadastrados
    public int contarClientes() {
        String sql = "SELECT COUNT(*) AS total FROM clientes";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //  Buscar cliente por email
    public Cliente buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT * FROM clientes WHERE email = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Cliente c = new Cliente();
                    c.setId(rs.getInt("id"));
                    c.setNome(rs.getString("nome"));
                    c.setEmail(rs.getString("email"));
                    c.setTelefone(rs.getString("telefone"));
                    c.setDocumento(rs.getString("documento"));
                    c.setCadastradoPor(rs.getString("cadastrado_por"));
                    return c;
                }
            }
        }
        return null;
    }

    //  Listar todos os clientes
    public List<Cliente> listarTodos() throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes";
        try (Connection conn = Conexao.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Cliente c = new Cliente();
                c.setId(rs.getInt("id"));
                c.setNome(rs.getString("nome"));
                c.setEmail(rs.getString("email"));
                c.setTelefone(rs.getString("telefone"));
                c.setDocumento(rs.getString("documento"));
                c.setCadastradoPor(rs.getString("cadastrado_por"));
                lista.add(c);
            }
        }
        return lista;
    }

     // lista apenas clientes cadastrados pelo funcionário
    public List<Cliente> listarCadastradosPorFuncionario() throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes WHERE cadastrado_por = 'FUNCIONARIO'";
        try (Connection conn = Conexao.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Cliente c = mapResultSetParaCliente(rs);
                lista.add(c);
            }
        }
        return lista;
    }
      //  Mapeia o ResultSet para objeto Cliente
    private Cliente mapResultSetParaCliente(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setId(rs.getInt("id"));
        c.setNome(rs.getString("nome"));
        c.setEmail(rs.getString("email"));
        c.setTelefone(rs.getString("telefone"));
        c.setDocumento(rs.getString("documento"));
        c.setCadastradoPor(rs.getString("cadastrado_por"));
        return c;
    }
}
