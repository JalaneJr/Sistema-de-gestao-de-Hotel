package controller;

import dao.UsuarioDAO;
import model.Usuario;

public class UsuarioController {
    private UsuarioDAO usuarioDAO;

    public UsuarioController() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public Usuario login(String username, String senha) {
        try {
            return usuarioDAO.autenticar(username, senha);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
