package dao;

import model.ReportRow;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RelatorioDAO {

    // Gera relatório com base no tipo e período
    public List<ReportRow> gerarRelatorio(String tipo, LocalDate from, LocalDate to) throws SQLException {
        List<ReportRow> lista = new ArrayList<>();
        String sql = "";

        switch (tipo) {
            case "📈 Consolidado Mensal":
            case "📅 Desempenho Mensal":
                sql = "SELECT DATE_FORMAT(dataEntrada, '%Y-%m') AS col1, COUNT(*) AS val1, SUM(valor_total) AS val2 " +
                      "FROM reservas WHERE dataEntrada BETWEEN ? AND ? GROUP BY col1 ORDER BY col1";
                break;

            case "🏨 Quartos Mais Ocupados":
                sql = "SELECT q.numero AS col1, COUNT(*) AS val1, SUM(valor_total) AS val2 " +
                      "FROM reservas r JOIN quartos q ON r.quarto_id = q.id " +
                      "WHERE r.dataEntrada BETWEEN ? AND ? GROUP BY q.numero ORDER BY val1 DESC";
                break;

            case "📊 Taxa de Ocupação":
                sql = "SELECT q.numero AS col1, COUNT(*) AS val1 " +
                      "FROM reservas r JOIN quartos q ON r.quarto_id = q.id " +
                      "WHERE r.dataEntrada BETWEEN ? AND ? GROUP BY q.numero";
                break;

            case "💰 Financeiro":
                // Colunas: id (col1), valor_total (val1), dataEntrada (val2Str)
                sql = "SELECT r.id AS col1, r.valor_total AS val1, DATE_FORMAT(r.dataEntrada, '%Y-%m-%d') AS val2Str " +
                      "FROM reservas r WHERE r.dataEntrada BETWEEN ? AND ? ORDER BY r.dataEntrada";
                break;

            case "👥 Clientes Frequentes":
                sql = "SELECT c.nome AS col1, COUNT(*) AS val1 " +
                      "FROM reservas r JOIN clientes c ON r.cliente_id = c.id " +
                      "WHERE r.dataEntrada BETWEEN ? AND ? GROUP BY c.nome ORDER BY val1 DESC";
                break;

            case "🛏️ Receita por Tipo Quarto":
                sql = "SELECT q.tipo AS col1, SUM(r.valor_total) AS val1 " +
                      "FROM reservas r JOIN quartos q ON r.quarto_id = q.id " +
                      "WHERE r.dataEntrada BETWEEN ? AND ? GROUP BY q.tipo";
                break;

            default:
                return gerarRelatorioDiario();
        }

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(from));
            stmt.setDate(2, Date.valueOf(to));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ReportRow r = new ReportRow();
                    r.setCol1(rs.getString("col1"));
                    if (hasColumn(rs, "val1")) r.setVal1(rs.getDouble("val1"));
                    if (hasColumn(rs, "val2")) r.setVal2(rs.getDouble("val2"));
                    if (hasColumn(rs, "val2Str")) r.setVal2Str(rs.getString("val2Str"));
                    lista.add(r);
                }
            }
        }

        return lista;
    }

    // Relatório diário automático
    public List<ReportRow> gerarRelatorioDiario() throws SQLException {
        List<ReportRow> lista = new ArrayList<>();
        String sql = "SELECT r.id AS col1, r.valor_total AS val1, DATEDIFF(r.dataSaida, r.dataEntrada) AS val2 " +
                     "FROM reservas r WHERE r.dataEntrada = CURDATE()";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ReportRow r = new ReportRow();
                r.setCol1(rs.getString("col1"));
                r.setVal1(rs.getDouble("val1"));
                r.setVal2(rs.getDouble("val2"));
                lista.add(r);
            }
        }

        return lista;
    }

    // Utilitário para checar se a coluna existe no ResultSet
    private boolean hasColumn(ResultSet rs, String columnName) {
        try {
            rs.findColumn(columnName);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
