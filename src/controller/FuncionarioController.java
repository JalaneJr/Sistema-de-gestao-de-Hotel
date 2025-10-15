package controller;

import dao.FuncionarioDAO;
import model.Funcionario;
import java.util.List;

public class FuncionarioController {
    private FuncionarioDAO funcionarioDAO;

    public FuncionarioController() {
        this.funcionarioDAO = new FuncionarioDAO();
    }

    public boolean cadastrarFuncionario(Funcionario funcionario) {
        try {
            funcionarioDAO.inserir(funcionario);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Funcionario> listarFuncionarios() {
        try {
            return funcionarioDAO.listarTodos();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
