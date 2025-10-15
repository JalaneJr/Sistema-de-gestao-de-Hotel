package model;

import dao.ClienteDAO;
import model.Cliente;

import java.sql.SQLException;
import java.util.List;

public class Usuario extends Pessoa {
    private String username;
    private String senha;
    private String perfil; // ADMIN, FUNCIONARIO, CLIENTE
    private String perguntaSeguranca;
    private String respostaSeguranca;

    public Usuario() {}

    public Usuario(int id, String nome, String documento, String telefone, String email,
                   String username, String senha, String perfil,
                   String perguntaSeguranca, String respostaSeguranca) {
        super(id, nome, documento, telefone, email);
        this.username = username;
        this.senha = senha;
        this.perfil = perfil;
        this.perguntaSeguranca = perguntaSeguranca;
        this.respostaSeguranca = respostaSeguranca;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getPerfil() { return perfil; }
    public void setPerfil(String perfil) { this.perfil = perfil; }

    // 🔹 Getters e Setters da pergunta e resposta de segurança
    public String getPerguntaSeguranca() { return perguntaSeguranca; }
    public void setPerguntaSeguranca(String perguntaSeguranca) { this.perguntaSeguranca = perguntaSeguranca; }

    public String getRespostaSeguranca() { return respostaSeguranca; }
    public void setRespostaSeguranca(String respostaSeguranca) { this.respostaSeguranca = respostaSeguranca; }

    // 🔹 Novo método para verificar se o usuário já é cliente
    public Cliente toCliente() {
        ClienteDAO dao = new ClienteDAO();
        try {
            List<Cliente> clientes = dao.listarTodos();
            for (Cliente c : clientes) {
                if (c.getEmail().equalsIgnoreCase(this.getEmail())) {
                    return c; // Retorna cliente correspondente
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Não é cliente ainda
    }
}
