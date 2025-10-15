package dao;

import model.Funcionario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FuncionarioDAO {

    // Inserir um novo funcionário
    public void inserir(Funcionario funcionario) throws SQLException {
        String sql = "INSERT INTO funcionarios (nome, documento, telefone, email, cargo, salario, ativo) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, funcionario.getNome());
            stmt.setString(2, funcionario.getDocumento());
            stmt.setString(3, funcionario.getTelefone());
            stmt.setString(4, funcionario.getEmail());
            stmt.setString(5, funcionario.getCargo());
            stmt.setDouble(6, funcionario.getSalario());
            stmt.setBoolean(7, funcionario.isAtivo()); // true ou false

            stmt.executeUpdate();

            // Atualiza o ID gerado no objeto
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    funcionario.setId(rs.getInt(1));
                }
            }
        }
    }

    // Atualizar dados do funcionário (não altera email, senha ou credenciais)
    public void atualizar(Funcionario funcionario) throws SQLException {
        String sql = "UPDATE funcionarios SET nome=?, telefone=?, cargo=?, salario=?, ativo=? WHERE id=?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, funcionario.getNome());
            stmt.setString(2, funcionario.getDocumento());
            stmt.setString(3, funcionario.getTelefone());
            stmt.setString(4, funcionario.getCargo());
            stmt.setDouble(5, funcionario.getSalario());
            stmt.setBoolean(6, funcionario.isAtivo());
            stmt.setInt(7, funcionario.getId());

            stmt.executeUpdate();
        }
    }

    // Deletar funcionário
    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM funcionarios WHERE id=?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // Listar todos os funcionários
    public List<Funcionario> listarTodos() throws SQLException {
        List<Funcionario> lista = new ArrayList<>();
        String sql = "SELECT * FROM funcionarios";

        try (Connection conn = Conexao.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Funcionario f = new Funcionario();
                f.setId(rs.getInt("id"));
                f.setNome(rs.getString("nome"));
                f.setDocumento(rs.getString("documento"));
                f.setTelefone(rs.getString("telefone"));
                f.setEmail(rs.getString("email"));
                f.setCargo(rs.getString("cargo"));
                f.setSalario((int) rs.getDouble("salario"));
                f.setAtivo(rs.getBoolean("ativo"));

                lista.add(f);
            }
        }

        return lista;
    }

    // Buscar funcionário por ID
    public Funcionario buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM funcionarios WHERE id=?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Funcionario f = new Funcionario();
                    f.setId(rs.getInt("id"));
                    f.setNome(rs.getString("nome"));
                    f.setDocumento(rs.getString("documento"));
                    f.setTelefone(rs.getString("telefone"));
                    f.setEmail(rs.getString("email"));
                    f.setCargo(rs.getString("cargo"));
                    f.setSalario((int) rs.getDouble("salario"));
                    f.setAtivo(rs.getBoolean("ativo"));
                    return f;
                }
            }
        }
        return null;
    }


    public void desativar(Funcionario funcionario) throws SQLException {
        String sql = "UPDATE funcionarios SET ativo=0 WHERE id=?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, funcionario.getId());
            stmt.executeUpdate();
        }
    }

    public int proximoCodigoSenha(String nome, String cargo) throws SQLException {
        String emailDomain = cargo.equals("Recepcionista") ? "staff@gmail.com" : "admin@gmail.com";
        String baseNome = nome.toLowerCase().replaceAll("\\s+", "");
        String sql = "SELECT COUNT(*) AS total FROM funcionarios WHERE email LIKE ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, baseNome + "%@" + emailDomain);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }
    
    public int contarTodos() throws SQLException {
    String sql = "SELECT COUNT(*) FROM funcionarios";
    try (Connection conn = Conexao.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        if (rs.next()) return rs.getInt(1);
    }
    return 0;
}

public int contarAtivos() throws SQLException {
    String sql = "SELECT COUNT(*) FROM funcionarios WHERE ativo = 1";
    try (Connection conn = Conexao.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        if (rs.next()) return rs.getInt(1);
    }
    return 0;
}

}