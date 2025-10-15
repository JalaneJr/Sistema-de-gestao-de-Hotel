package staff_paineis;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import dao.Conexao;
import java.sql.*;

public class PainelDashboard extends VBox {

    private int reservasAtivas = 0;
    private int ocupacao = 0;
    private int clientes = 0;
    private int checkinsHoje = 0;

    public PainelDashboard() {
        setAlignment(Pos.CENTER);
        setSpacing(30);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #ecf0f1;");

        carregarDadosDoBanco();

        HBox cards = new HBox(20);
        cards.setAlignment(Pos.CENTER);
        cards.getChildren().addAll(
            criarCardAnimado("📅 Reservas Ativas", reservasAtivas, "#2980b9"),
            criarCardAnimado("🛏 Ocupação (%)", ocupacao, "#27ae60"),
            criarCardAnimado("👤 Clientes", clientes, "#8e44ad"),
            criarCardAnimado("✅ Check-ins Hoje", checkinsHoje, "#e67e22")
        );

        PieChart grafico = new PieChart();
        grafico.getData().add(new PieChart.Data("Quartos Ocupados", ocupacao));
        grafico.getData().add(new PieChart.Data("Quartos Livres", 100 - ocupacao));
        grafico.setTitle("Taxa de Ocupação");

        getChildren().addAll(cards, grafico);
    }

    private void carregarDadosDoBanco() {
        try (Connection conn = Conexao.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs1 = stmt.executeQuery("SELECT COUNT(*) FROM reservas WHERE status='ativa'");
            if (rs1.next()) reservasAtivas = rs1.getInt(1);

            ResultSet rs2 = stmt.executeQuery("SELECT (SELECT COUNT(*) FROM quartos WHERE status='ocupado') * 100 / COUNT(*) FROM quartos");
            if (rs2.next()) ocupacao = rs2.getInt(1);

            ResultSet rs3 = stmt.executeQuery("SELECT COUNT(*) FROM clientes");
            if (rs3.next()) clientes = rs3.getInt(1);

            ResultSet rs4 = stmt.executeQuery("SELECT * FROM reservas WHERE dataEntrada IS NOT NULL");
            if (rs4.next()) checkinsHoje = rs4.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private VBox criarCardAnimado(String titulo, int valorFinal, String cor) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setPrefSize(200, 120);
        card.setStyle("-fx-background-color: " + cor + "; -fx-background-radius: 12;");

        Label lblTitulo = new Label(titulo);
        lblTitulo.setTextFill(Color.WHITE);
        lblTitulo.setFont(new Font(14));

        Label lblValor = new Label("0");
        lblValor.setTextFill(Color.WHITE);
        lblValor.setFont(new Font(22));

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(20), e -> {
            int atual = Integer.parseInt(lblValor.getText());
            if (atual < valorFinal) lblValor.setText(String.valueOf(atual + 1));
        }));
        timeline.setCycleCount(valorFinal);
        timeline.play();

        card.getChildren().addAll(lblTitulo, lblValor);
        return card;
    }
}
