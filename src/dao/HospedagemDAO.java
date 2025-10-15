package dao;

import model.Hospedagem;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HospedagemDAO {
    private final Connection conexao;

    public HospedagemDAO() throws SQLException {
        conexao = Conexao.getConnection();
    }

    public void inserir(Hospedagem h) throws SQLException {
        String sql = "INSERT INTO hospedagens (id_reserva, id_cliente, id_quarto, data_checkin, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, h.getReserva().getId());
            ps.setInt(2, h.getCliente().getId());
            ps.setInt(3, h.getQuarto().getId());
            ps.setString(4, h.getDataCheckin().toString());
            ps.setString(5, h.getStatus());
            ps.executeUpdate();
        }
    }

    public void finalizarHospedagem(int idReserva, LocalDateTime dataCheckout) throws SQLException {
        String sql = "UPDATE hospedagens SET data_checkout = ?, status = 'Finalizada' WHERE id_reserva = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, dataCheckout.toString());
            ps.setInt(2, idReserva);
            ps.executeUpdate();
        }
    }
}
