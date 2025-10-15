package controller;

import dao.ReservaDAO;
import model.Reserva;
import java.util.List;

public class ReservaController {
    private ReservaDAO reservaDAO;

    public ReservaController() {
        this.reservaDAO = new ReservaDAO();
    }

    public boolean cadastrarReserva(Reserva reserva) {
        try {
            // Regras de negócio: cálculo automático
            long dias = java.time.temporal.ChronoUnit.DAYS.between(reserva.getDataEntrada(), reserva.getDataSaida());
            reserva.setValorTotal(dias * reserva.getQuarto().getPreco());

            reservaDAO.inserir(reserva);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Reserva> listarReservas() {
        try {
            return reservaDAO.listarTodas();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
