package dao;

import model.Reserva;
import model.Cliente;
import model.Quarto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAO {

    // Inserir uma nova reserva
    public void inserir(Reserva reserva) throws SQLException {
        String sql = "INSERT INTO reservas (cliente_id, quarto_id, dataEntrada, dataSaida, valor_total, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reserva.getCliente().getId());
            stmt.setInt(2, reserva.getQuarto().getId());
            stmt.setDate(3, Date.valueOf(reserva.getDataEntrada()));
            stmt.setDate(4, Date.valueOf(reserva.getDataSaida()));
            stmt.setDouble(5, reserva.getValorTotal());
            stmt.setString(6, reserva.getStatus());
            stmt.executeUpdate();
        }
    }

    // Atualizar uma reserva existente
    public void atualizar(Reserva reserva) throws SQLException {
        String sql = "UPDATE reservas SET cliente_id=?, quarto_id=?, dataEntrada=?, dataSaida=?, valor_total=?, status=? WHERE id=?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reserva.getCliente().getId());
            stmt.setInt(2, reserva.getQuarto().getId());
            stmt.setDate(3, Date.valueOf(reserva.getDataEntrada()));
            stmt.setDate(4, Date.valueOf(reserva.getDataSaida()));
            stmt.setDouble(5, reserva.getValorTotal());
            stmt.setString(6, reserva.getStatus());
            stmt.setInt(7, reserva.getId());
            stmt.executeUpdate();
        }
    }

    // Eliminar uma reserva
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM reservas WHERE id=?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // Cancelar reserva (atualiza status para "Cancelada")
    public void cancelarReserva(int id) throws SQLException {
        String sql = "UPDATE reservas SET status='Cancelada' WHERE id=?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // Contar reservas ativas
    public int contarReservasAtivas() {
        String sql = "SELECT COUNT(*) AS total FROM reservas WHERE status='Ativa'";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Calcular taxa de ocupação
    public double calcularTaxaOcupacao() {
        String sql = "SELECT (SELECT COUNT(*) FROM reservas WHERE status='Ativa') / (SELECT COUNT(*) FROM quartos) * 100 AS taxa";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getDouble("taxa");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Receita mensal
    public double calcularReceitaMensal() {
        String sql = "SELECT SUM(valor_total) AS receita FROM reservas " +
                     "WHERE MONTH(dataEntrada)=MONTH(CURDATE()) AND YEAR(dataEntrada)=YEAR(CURDATE())";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getDouble("receita");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Listar todas reservas com dados completos de cliente e quarto
    public List<Reserva> getTodasReservas() {
        List<Reserva> lista = new ArrayList<>();
        String sql = """
                SELECT r.*, 
                       c.id AS cliente_id, c.nome AS cliente_nome, c.email, c.telefone, c.documento,
                       q.id AS quarto_id, q.numero, q.tipo, q.preco, q.descricao, q.ofertas, q.imagem
                  FROM reservas r
                  JOIN clientes c ON r.cliente_id = c.id
                  JOIN quartos q ON r.quarto_id = q.id
                  ORDER BY r.dataEntrada DESC
                """;

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Reserva r = new Reserva();
                r.setId(rs.getInt("id"));
                r.setDataEntrada(rs.getDate("dataEntrada").toLocalDate());
                r.setDataSaida(rs.getDate("dataSaida").toLocalDate());
                r.setValorTotal(rs.getDouble("valor_total"));
                r.setStatus(rs.getString("status"));

                Cliente c = new Cliente();
                c.setId(rs.getInt("cliente_id"));
                c.setNome(rs.getString("cliente_nome"));
                c.setEmail(rs.getString("email"));
                c.setTelefone(rs.getString("telefone"));
                c.setDocumento(rs.getString("documento"));
                r.setCliente(c);

                Quarto q = new Quarto();
                q.setId(rs.getInt("quarto_id"));
                q.setNumero(rs.getInt("numero"));
                q.setTipo(rs.getString("tipo"));
                q.setPreco(rs.getDouble("preco"));
                q.setDescricao(rs.getString("descricao"));
                q.setOfertas(rs.getString("ofertas"));
                q.setImagem(rs.getString("imagem"));
                r.setQuarto(q);

                lista.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // Listar todas reservas (alias)
    public List<Reserva> listarTodas() {
        return getTodasReservas();
    }

    // Buscar reservas de um quarto específico
    public List<Reserva> buscarPorQuarto(int quartoId) {
        List<Reserva> lista = new ArrayList<>();
        String sql = "SELECT * FROM reservas WHERE quarto_id=?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quartoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Reserva r = new Reserva();
                r.setId(rs.getInt("id"));
                r.setDataEntrada(rs.getDate("dataEntrada").toLocalDate());
                r.setDataSaida(rs.getDate("dataSaida").toLocalDate());
                r.setValorTotal(rs.getDouble("valor_total"));
                r.setStatus(rs.getString("status"));

                Quarto q = new Quarto();
                q.setId(quartoId);
                r.setQuarto(q);

                Cliente c = new Cliente();
                c.setId(rs.getInt("cliente_id"));
                r.setCliente(c);

                lista.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Buscar reservas de um cliente específico
    public List<Reserva> buscarPorCliente(int clienteId) {
        List<Reserva> lista = new ArrayList<>();
        String sql = "SELECT * FROM reservas WHERE cliente_id=?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, clienteId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Reserva r = new Reserva();
                r.setId(rs.getInt("id"));
                r.setDataEntrada(rs.getDate("dataEntrada").toLocalDate());
                r.setDataSaida(rs.getDate("dataSaida").toLocalDate());
                r.setValorTotal(rs.getDouble("valor_total"));
                r.setStatus(rs.getString("status"));

                Quarto q = new Quarto();
                q.setId(rs.getInt("quarto_id"));
                r.setQuarto(q);

                Cliente c = new Cliente();
                c.setId(clienteId);
                r.setCliente(c);

                lista.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

}
