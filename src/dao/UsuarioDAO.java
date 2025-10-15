package dao;

import model.Usuario;
import java.sql.*;

public class UsuarioDAO {

    // === Registrar novo usuário ===
    public boolean registrar(Usuario u) {
        String sql = "INSERT INTO usuarios(nome, email, senha, perfil, pergunta_seguranca, resposta_seguranca) VALUES(?,?,?,?,?,?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, u.getNome());
            stmt.setString(2, u.getEmail());
            stmt.setString(3, u.getSenha());
            stmt.setString(4, u.getPerfil());
            stmt.setString(5, u.getPerguntaSeguranca());
            stmt.setString(6, u.getRespostaSeguranca());

            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            System.out.println("Erro ao registrar usuário: " + e.getMessage());
            return false;
        }
    }

    // === Autenticar usuário pelo email + senha ===
    public Usuario autenticar(String email, String senha) {
        String sql = "SELECT * FROM usuarios WHERE email=? AND senha=?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, senha);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Usuario(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        "", "",
                        rs.getString("email"),
                        "",                    
                        rs.getString("senha"),
                        rs.getString("perfil"),
                        rs.getString("pergunta_seguranca"),
                        rs.getString("resposta_seguranca")
                );
            }

        } catch (Exception e) {
            System.out.println("Erro ao autenticar: " + e.getMessage());
        }
        return null;
    }

    // === Recuperar senha por email ===
    public String recuperarSenha(String email) {
        String sql = "SELECT senha FROM usuarios WHERE email=?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("senha");
            }

        } catch (Exception e) {
            System.out.println("Erro ao recuperar senha: " + e.getMessage());
        }
        return null;
    }

    // 🔹 Recuperar pergunta de segurança
    public String recuperarPerguntaSeguranca(String email) {
        String sql = "SELECT pergunta_seguranca FROM usuarios WHERE email=?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("pergunta_seguranca");
            }

        } catch (Exception e) {
            System.out.println("Erro ao recuperar pergunta de segurança: " + e.getMessage());
        }
        return null;
    }

    // 🔹 Verificar se a resposta de segurança confere
    public boolean verificarRespostaSeguranca(String email, String resposta) {
        String sql = "SELECT resposta_seguranca FROM usuarios WHERE email=?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String respostaCadastrada = rs.getString("resposta_seguranca");
                return respostaCadastrada.equalsIgnoreCase(resposta.trim());
            }

        } catch (Exception e) {
            System.out.println("Erro ao verificar resposta de segurança: " + e.getMessage());
        }
        return false;
    }
}
