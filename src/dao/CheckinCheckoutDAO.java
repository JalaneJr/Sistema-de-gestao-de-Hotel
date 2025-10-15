package dao;

import model.Reserva;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.CheckinCheckout;

public class CheckinCheckoutDAO {

    private final ReservaDAO reservaDAO = new ReservaDAO();
    private final QuartoDAO quartoDAO = new QuartoDAO();

    // Realiza check-in da reserva
    public void realizarCheckin(Reserva reserva) throws SQLException {
        if (!"ATIVA".equalsIgnoreCase(reserva.getStatus())) {
            throw new IllegalStateException("Só é possível fazer check-in em reservas ATIVAS.");
        }
        reserva.setStatus("EM ANDAMENTO");
        reservaDAO.atualizar(reserva);
        quartoDAO.atualizarStatus(reserva.getQuarto().getId(), "OCUPADO");
    }

    // Realiza check-out da reserva
    public void realizarCheckout(Reserva reserva) throws SQLException {
        if (!"EM ANDAMENTO".equalsIgnoreCase(reserva.getStatus())) {
            throw new IllegalStateException("Só é possível fazer check-out em reservas EM ANDAMENTO.");
        }
        reserva.setStatus("FINALIZADA");
        reservaDAO.atualizar(reserva);
        quartoDAO.atualizarStatus(reserva.getQuarto().getId(), "LIVRE");
    }

    // Cancela a reserva
    public void cancelarReserva(Reserva reserva) throws SQLException {
        if ("FINALIZADA".equalsIgnoreCase(reserva.getStatus()) || "CANCELADA".equalsIgnoreCase(reserva.getStatus())) {
            throw new IllegalStateException("Reserva já está FINALIZADA ou CANCELADA.");
        }
        reservaDAO.cancelarReserva(reserva.getId());
        if (reserva.getQuarto() != null) {
            quartoDAO.atualizarStatus(reserva.getQuarto().getId(), "LIVRE");
        }
    }

    // Lista todas as reservas
    public List<Reserva> listarTodasReservas() {
        return reservaDAO.listarTodas();
    }

    // Lista apenas as reservas finalizadas (histórico de hospedagens)
    public List<Reserva> historicoHospedagens() {
        return reservaDAO.listarTodas().stream()
                .filter(r -> "FINALIZADA".equalsIgnoreCase(r.getStatus()))
                .toList();
    }

    // Lista checkin/checkout
    public List<CheckinCheckout> listarTodos() {
        List<CheckinCheckout> lista = new ArrayList<>();
        List<Reserva> reservas = reservaDAO.listarTodas();

        for (Reserva r : reservas) {
            CheckinCheckout cc = new CheckinCheckout();
            cc.setReserva(r);
            cc.setStatus(r.getStatus() != null ? r.getStatus().toUpperCase() : null);

            if (r.getStatus() == null) {
                cc.setDataCheckin(null);
                cc.setDataCheckout(null);
            } else {
                switch (r.getStatus().toUpperCase()) {
                    case "EM ANDAMENTO":
                        cc.setDataCheckin(LocalDateTime.now()); // pegar data real do banco
                        cc.setDataCheckout(null);
                        break;
                    case "FINALIZADA":
                        cc.setDataCheckin(LocalDateTime.now().minusDays(2)); // exemplo
                        cc.setDataCheckout(LocalDateTime.now());
                        break;
                    case "ATIVA":
                        cc.setDataCheckin(null);
                        cc.setDataCheckout(null);
                        break;
                    default:
                        cc.setDataCheckin(null);
                        cc.setDataCheckout(null);
                        break;
                }
            }

            lista.add(cc);
        }

        return lista;
    }
}
