package admin_paineis;

import dao.ReservaDAO;
import dao.ClienteDAO;
import dao.FuncionarioDAO;
import java.sql.SQLException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class DashboardView {

    private StackPane root;
    private ReservaDAO reservaDAO = new ReservaDAO();
    private ClienteDAO clienteDAO = new ClienteDAO();
    private FuncionarioDAO funcionarioDAO = new FuncionarioDAO(); // DAO dos funcionários

    public DashboardView() throws SQLException {
        root = new StackPane();
        mostrarEstatisticas();
    }

    public StackPane getRoot() {
        return root;
    }

    private void mostrarEstatisticas() throws SQLException {
        root.getChildren().clear();

        // Cards com dados da base
        HBox cards = new HBox(20);
        cards.setAlignment(Pos.CENTER);

        VBox card1 = criarCard("📅 Reservas Ativas", String.valueOf(reservaDAO.contarReservasAtivas()), "#3498db");
        VBox card2 = criarCard("🛏 Ocupação", String.format("%.0f%%", reservaDAO.calcularTaxaOcupacao()), "#2ecc71");
        VBox card3 = criarCard("💰 Receita Mensal", String.format("%.2f MZN", reservaDAO.calcularReceitaMensal()), "#f39c12");
        VBox card4 = criarCard("👤 Clientes", String.valueOf(clienteDAO.contarClientes()), "#9b59b6");

        // ───── NOVOS CARDS DE FUNCIONÁRIOS ─────
        VBox card5 = criarCard("👨‍💼 Funcionários Cadastrados", String.valueOf(funcionarioDAO.contarTodos()), "#1abc9c");
        VBox card6 = criarCard("✅ Funcionários Ativos", String.valueOf(funcionarioDAO.contarAtivos()), "#27ae60");

        cards.getChildren().addAll(card1, card2, card3, card4, card5, card6);

        // Gráfico de Pizza
        PieChart grafico = new PieChart();
        double taxa = reservaDAO.calcularTaxaOcupacao();
        grafico.getData().add(new PieChart.Data("Quartos Ocupados", taxa));
        grafico.getData().add(new PieChart.Data("Quartos Livres", 100 - taxa));
        grafico.setTitle("Taxa de Ocupação");

        VBox painel = new VBox(30, cards, grafico);
        painel.setAlignment(Pos.CENTER);
        painel.setPadding(new Insets(20));

        root.getChildren().add(painel);
    }

    private VBox criarCard(String titulo, String valor, String cor) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setPrefSize(200, 120);
        card.setStyle("-fx-background-color: " + cor + "; -fx-background-radius: 12;");

        Label lblTitulo = new Label(titulo);
        lblTitulo.setTextFill(Color.WHITE);
        lblTitulo.setFont(new Font(14));

        Label lblValor = new Label(valor);
        lblValor.setTextFill(Color.WHITE);
        lblValor.setFont(new Font(22));

        card.getChildren().addAll(lblTitulo, lblValor);
        return card;
    }
}
