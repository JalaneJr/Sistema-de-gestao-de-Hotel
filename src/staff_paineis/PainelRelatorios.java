package staff_paineis;

import dao.RelatorioDAO;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.util.Duration;
import model.ReportRow;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class PainelRelatorios extends VBox {

    private TableView<ReportRow> table;
    private ObservableList<ReportRow> data;
    private VBox chartContainer;
    private RelatorioDAO dao;

    private DatePicker dpFrom, dpTo;
    private ComboBox<String> cbTipo;
    private Button btnGerar;
    private Label lblStatus;

    public PainelRelatorios() {
        setAlignment(Pos.TOP_CENTER);
        setSpacing(15);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #d5f5e3; -fx-background-radius: 8;");

        dao = new RelatorioDAO();

        // Título
        Label titulo = new Label("📑 Relatórios do Funcionário");
        titulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        Label descricao = new Label("Visualize relatórios simplificados de desempenho.");
        descricao.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50;");

        // Painel de filtros simplificado
        dpFrom = new DatePicker(LocalDate.now().minusDays(7));
        dpTo = new DatePicker(LocalDate.now());

        cbTipo = new ComboBox<>();
        cbTipo.getItems().addAll(
                "🔍 Relatório Diário",
                "📊 Taxa de Ocupação",
                "📅 Desempenho Semanal",
                "🏨 Quartos Mais Ocupados"
        );
        cbTipo.setValue("🔍 Relatório Diário");

        btnGerar = new Button("🔄 Gerar Relatório");
        btnGerar.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        btnGerar.setOnAction(e -> gerarRelatorio());

        HBox filtros = new HBox(10, new Label("De:"), dpFrom, new Label("Até:"), dpTo, new Label("Tipo:"), cbTipo, btnGerar);
        filtros.setAlignment(Pos.CENTER_LEFT);
        filtros.setPadding(new Insets(10));
        filtros.setStyle("-fx-background-color: rgba(255,255,255,0.9); -fx-background-radius: 8;");

        // Tabela
        table = new TableView<>();
        data = FXCollections.observableArrayList();
        table.setItems(data);
        table.setPrefHeight(200);

        // Gráfico
        chartContainer = new VBox(10);
        chartContainer.setAlignment(Pos.CENTER);
        chartContainer.setPadding(new Insets(10));
        chartContainer.setStyle("-fx-background-color: white; -fx-background-radius: 8;");

        // Status
        lblStatus = new Label("Selecione os filtros e clique em Gerar");
        lblStatus.setStyle("-fx-font-size: 12px; -fx-text-fill: #34495e;");

        getChildren().addAll(titulo, descricao, filtros, table, chartContainer, lblStatus);

        // Gera relatório inicial
        gerarRelatorio();
    }

    private void gerarRelatorio() {
        String tipo = cbTipo.getValue();
        LocalDate from = dpFrom.getValue();
        LocalDate to = dpTo.getValue();

        if (from == null || to == null) {
            lblStatus.setText("Selecione o período corretamente.");
            return;
        }
        if (to.isBefore(from)) {
            lblStatus.setText("A data final deve ser após a data inicial.");
            return;
        }

        try {
            List<ReportRow> rows;
            switch (tipo) {
                case "🔍 Relatório Diário":
                    rows = dao.gerarRelatorioDiario();
                    break;
                default:
                    rows = dao.gerarRelatorio(tipo, from, to);
            }

            processarResultados(rows);
            atualizarGrafico(rows, tipo);

        } catch (SQLException ex) {
            ex.printStackTrace();
            lblStatus.setText("Erro ao gerar relatório: " + ex.getMessage());
        }
    }

    private void processarResultados(List<ReportRow> rows) {
        data.clear();
        if (rows == null || rows.isEmpty()) {
            lblStatus.setText("Nenhum dado encontrado para o período selecionado.");
            table.getColumns().clear();
            return;
        }

        data.addAll(rows);
        montarColunasDinamicas();
        lblStatus.setText("Relatório gerado com sucesso! " + rows.size() + " registros encontrados.");
    }

    private void montarColunasDinamicas() {
        table.getColumns().clear();

        TableColumn<ReportRow, String> col1 = new TableColumn<>("Descrição");
        col1.setCellValueFactory(new PropertyValueFactory<>("col1"));
        col1.setPrefWidth(200);
        table.getColumns().add(col1);

        if (data.stream().anyMatch(r -> r.getVal1() != 0)) {
            TableColumn<ReportRow, Double> val1Col = new TableColumn<>("Valor 1");
            val1Col.setCellValueFactory(new PropertyValueFactory<>("val1"));
            val1Col.setPrefWidth(120);
            table.getColumns().add(val1Col);
        }

        if (data.stream().anyMatch(r -> r.getVal2() != 0)) {
            TableColumn<ReportRow, Double> val2Col = new TableColumn<>("Valor 2");
            val2Col.setCellValueFactory(new PropertyValueFactory<>("val2"));
            val2Col.setPrefWidth(120);
            table.getColumns().add(val2Col);
        }

        if (data.stream().anyMatch(r -> r.getVal2Str() != null)) {
            TableColumn<ReportRow, String> val2StrCol = new TableColumn<>("Data");
            val2StrCol.setCellValueFactory(new PropertyValueFactory<>("val2Str"));
            val2StrCol.setPrefWidth(120);
            table.getColumns().add(val2StrCol);
        }
    }

    private void atualizarGrafico(List<ReportRow> rows, String tipo) {
        chartContainer.getChildren().clear();
        if (rows == null || rows.isEmpty()) return;

        switch (tipo) {
            case "📊 Taxa de Ocupação":
            case "🔍 Relatório Diário":
                PieChart pieChart = new PieChart();
                pieChart.setTitle(tipo);
                for (ReportRow r : rows) {
                    double val = r.getVal1();
                    if (val > 0) pieChart.getData().add(new PieChart.Data(r.getCol1(), val));
                }
                animarGrafico(pieChart);
                chartContainer.getChildren().add(pieChart);
                break;

            case "📅 Desempenho Semanal":
            case "🏨 Quartos Mais Ocupados":
                CategoryAxis xAxis = new CategoryAxis();
                NumberAxis yAxis = new NumberAxis();
                BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
                barChart.setTitle(tipo);
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Valores");
                for (ReportRow r : rows) {
                    series.getData().add(new XYChart.Data<>(r.getCol1(), r.getVal1()));
                }
                barChart.getData().add(series);
                animarGrafico(barChart);
                chartContainer.getChildren().add(barChart);
                break;
        }
    }

    private void animarGrafico(Chart chart) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(50), e -> chart.setOpacity(chart.getOpacity() + 0.05)));
        timeline.setCycleCount(20);
        chart.setOpacity(0);
        timeline.play();
    }
}
