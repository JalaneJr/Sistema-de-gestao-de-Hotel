package controller;

import dao.QuartoDAO;
import model.Quarto;
import java.util.List;

public class QuartoController {
    private QuartoDAO quartoDAO;

    public QuartoController() {
        this.quartoDAO = new QuartoDAO();
    }

    public boolean cadastrarQuarto(Quarto quarto) {
        try {
            quartoDAO.inserir(quarto);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Quarto> listarQuartos() {
        try {
            return quartoDAO.listarTodos();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
