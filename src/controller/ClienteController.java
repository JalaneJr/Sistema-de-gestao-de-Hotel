package controller;

import dao.ClienteDAO;
import model.Cliente;
import java.util.List;

public class ClienteController {
    private ClienteDAO clienteDAO;

    public ClienteController() {
        this.clienteDAO = new ClienteDAO();
    }

    public boolean cadastrarCliente(Cliente cliente) {
        try {
            clienteDAO.inserir(cliente);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Cliente> listarClientes() {
        try {
            return clienteDAO.listarTodos();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
