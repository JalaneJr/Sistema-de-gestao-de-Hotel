package admin_paineis;

import dao.RelatorioDAO;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.ReportRow;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class RelatoriosView {

    private BorderPane root;
    private RelatorioDAO dao = new RelatorioDAO();
    private TableView<ReportRow> table;
    private ObservableList<ReportRow> data;

    private DatePicker dpFrom, dpTo;
    private ComboBox<String> cbTipo;
    private Button btnGerar, btnSalvar, btnLimpar;
    private Label lblStatus;

    private VBox chartContainer;

    public RelatoriosView() {
        root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f5f7fa;");
        criarUI();
    }

    public Node getRoot() {
        return root;
    }

    private void criarUI() {
        Label titulo = new Label("📑 Sistema de Relatórios");
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        VBox controlePanel = criarPainelControle();
        VBox tabelaPanel = criarTabelaResultados();

        chartContainer = new VBox(15);
        chartContainer.setPadding(new Insets(15));
        chartContainer.setStyle("-fx-background-color: white; -fx-background-radius: 8;");

        lblStatus = new Label("Selecione os filtros e clique em Gerar");
        lblStatus.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");

        VBox mainContent = new VBox(15, titulo, controlePanel, tabelaPanel, chartContainer, lblStatus);
        root.setCenter(mainContent);
    }

    private VBox criarPainelControle() {
        dpFrom = new DatePicker(LocalDate.now().minusMonths(1));
        dpFrom.setPrefWidth(150);
        dpTo = new DatePicker(LocalDate.now());
        dpTo.setPrefWidth(150);

        cbTipo = new ComboBox<>();
        cbTipo.getItems().addAll(
                "📈 Consolidado Mensal",
                "🏨 Quartos Mais Ocupados",
                "📊 Taxa de Ocupação",
                "💰 Financeiro",
                "👥 Clientes Frequentes",
                "🛏️ Receita por Tipo Quarto",
                "📅 Desempenho Mensal",
                "🔍 Relatório Diário Automático"
        );
        cbTipo.setValue("🔍 Relatório Diário Automático");
        cbTipo.setPrefWidth(250);

        btnGerar = criarBotaoEstilizado("🔄 Gerar Relatório", "#2ecc71", "#27ae60");
        btnSalvar = criarBotaoEstilizado("💾 Salvar CSV", "#3498db", "#2980b9");
        btnLimpar = criarBotaoEstilizado("🗑️ Limpar", "#e74c3c", "#c0392b");

        btnGerar.setOnAction(e -> {
            try {
                gerarRelatorio();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
        btnSalvar.setOnAction(e -> salvarCSV());
        btnLimpar.setOnAction(e -> limparResultados());

        btnSalvar.setDisable(true);

        HBox filtrosRow = new HBox(10);
        filtrosRow.setAlignment(Pos.CENTER_LEFT);
        filtrosRow.setPadding(new Insets(15));
        filtrosRow.setStyle("-fx-background-color: rgba(255,255,255,0.95); -fx-background-radius: 8;");

        VBox datasBox = new VBox(5,
                new Label("Período:"), new HBox(5, new Label("De:"), dpFrom, new Label("Até:"), dpTo)
        );
        VBox tipoBox = new VBox(5, new Label("Tipo de Relatório:"), cbTipo);
        VBox botoesBox = new VBox(5, new Label("Ações:"), new HBox(10, btnGerar, btnSalvar, btnLimpar));

        filtrosRow.getChildren().addAll(datasBox, tipoBox, botoesBox);
        return new VBox(filtrosRow);
    }

    private Button criarBotaoEstilizado(String texto, String corNormal, String corHover) {
        Button btn = new Button(texto);
        btn.setStyle("-fx-background-color: " + corNormal + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 8 15; -fx-font-weight: bold;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: " + corHover + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 8 15; -fx-font-weight: bold;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: " + corNormal + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 8 15; -fx-font-weight: bold;"));
        return btn;
    }

    private void gerarRelatorio() throws SQLException {
        String tipo = cbTipo.getValue();

        if (dpFrom.getValue() == null || dpTo.getValue() == null) {
            mostrarAlerta("Erro de Validação", "Selecione as datas de início e fim do período.");
            return;
        }
        if (dpTo.getValue().isBefore(dpFrom.getValue())) {
            mostrarAlerta("Erro de Validação", "A data final deve ser após a data inicial.");
            return;
        }

        lblStatus.setText("Gerando relatório...");
        btnGerar.setDisable(true);

        List<ReportRow> rows;
        if (tipo.equals("🔍 Relatório Diário Automático")) {
            rows = dao.gerarRelatorioDiario();
        } else {
            rows = dao.gerarRelatorio(tipo, dpFrom.getValue(), dpTo.getValue());
        }

        processarResultados(rows);
        atualizarGrafico(rows, tipo);
        btnGerar.setDisable(false);
    }

    private void processarResultados(List<ReportRow> rows) {
        data.clear();

        if (rows == null || rows.isEmpty()) {
            lblStatus.setText("Nenhum dado encontrado para o período selecionado.");
            btnSalvar.setDisable(true);
            table.getColumns().clear();
            return;
        }

        data.addAll(rows);
        montarColunasDinamicas(cbTipo.getValue());
        lblStatus.setText("Relatório gerado com sucesso! " + rows.size() + " registros encontrados.");
        btnSalvar.setDisable(false);
    }

    private VBox criarTabelaResultados() {
        table = new TableView<>();
        data = FXCollections.observableArrayList();
        table.setItems(data);
        table.setPrefHeight(250);

        TableColumn<ReportRow, String> colMensagem = new TableColumn<>("Relatório");
        colMensagem.setPrefWidth(600);
        colMensagem.setCellValueFactory(new PropertyValueFactory<>("col1"));
        table.getColumns().add(colMensagem);

        VBox tabelaContainer = new VBox(10);
        tabelaContainer.setPadding(new Insets(15));
        tabelaContainer.setStyle("-fx-background-color: white; -fx-background-radius: 8;");

        Label tituloTabela = new Label("Resultados do Relatório");
        tituloTabela.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        tabelaContainer.getChildren().addAll(tituloTabela, table);
        return tabelaContainer;
    }

    private void montarColunasDinamicas(String tipo) {
        table.getColumns().clear();

        criarColuna("Descrição", "col1", 200);

        boolean temVal1 = data.stream().anyMatch(r -> r.getVal1() != 0);
        boolean temVal2 = data.stream().anyMatch(r -> r.getVal2() != 0);

        if (temVal1) criarColuna("Valor 1", "val1", 150);
        if (temVal2) criarColuna("Valor 2", "val2", 150);
    }

    private <T> void criarColuna(String titulo, String propriedade, double largura) {
        TableColumn<ReportRow, T> coluna = new TableColumn<>(titulo);
        coluna.setPrefWidth(largura);

        if ("col1".equals(propriedade)) coluna.setCellValueFactory(new PropertyValueFactory<>("col1"));
        else if ("val1".equals(propriedade)) coluna.setCellValueFactory(new PropertyValueFactory<>("val1"));
        else if ("val2".equals(propriedade)) coluna.setCellValueFactory(new PropertyValueFactory<>("val2"));

        table.getColumns().add(coluna);
    }

    private void salvarCSV() {
        if (data.isEmpty()) {
            mostrarAlerta("Sem Dados", "Não há dados para salvar. Gere um relatório primeiro.");
            return;
        }

        try {
            File dir = new File("relatorios");
            if (!dir.exists()) dir.mkdirs();

            String timestamp = java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

            String nomeArquivo = "relatorio_" + cbTipo.getValue().replace(" ", "_") + "_" + timestamp + ".csv";
            File arquivo = new File(dir, nomeArquivo);

            try (PrintWriter pw = new PrintWriter(new FileWriter(arquivo))) {
                // header
                StringBuilder header = new StringBuilder();
                for (TableColumn<ReportRow, ?> coluna : table.getColumns()) {
                    if (header.length() > 0) header.append(",");
                    header.append("\"").append(coluna.getText()).append("\"");
                }
                pw.println(header.toString());

                // dados
                for (ReportRow row : data) {
                    StringBuilder linha = new StringBuilder();
                    linha.append("\"").append(row.getCol1()).append("\"");
                    if (table.getColumns().stream().anyMatch(c -> c.getText().equals("Valor 1"))) {
                        linha.append(",").append(row.getVal1());
                    }
                    if (table.getColumns().stream().anyMatch(c -> c.getText().equals("Valor 2"))) {
                        linha.append(",").append(row.getVal2());
                    }
                    pw.println(linha.toString());
                }
            }

            mostrarAlerta("Sucesso", "Relatório salvo com sucesso!\nLocal: " + arquivo.getAbsolutePath());
            lblStatus.setText("Relatório salvo: " + arquivo.getName());

        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarAlerta("Erro ao Salvar", "Erro ao salvar arquivo CSV: " + ex.getMessage());
        }
    }

    private void limparResultados() {
        data.clear();
        table.getColumns().clear();
        TableColumn<ReportRow, String> colMensagem = new TableColumn<>("Relatório");
        colMensagem.setPrefWidth(600);
        colMensagem.setCellValueFactory(new PropertyValueFactory<>("col1"));
        table.getColumns().add(colMensagem);

        btnSalvar.setDisable(true);
        lblStatus.setText("Selecione os filtros e clique em Gerar");

        dpFrom.setValue(LocalDate.now().minusMonths(1));
        dpTo.setValue(LocalDate.now());
        cbTipo.setValue("🔍 Relatório Diário Automático");

        chartContainer.getChildren().clear();
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void atualizarGrafico(List<ReportRow> rows, String tipo) {
        chartContainer.getChildren().clear();
        if (rows == null || rows.isEmpty()) return;

        switch (tipo) {
            case "📈 Consolidado Mensal":
            case "📅 Desempenho Mensal":
                CategoryAxis xAxis = new CategoryAxis();
                NumberAxis yAxis = new NumberAxis();
                LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
                lineChart.setTitle(tipo);
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Receita");
                for (ReportRow r : rows) series.getData().add(new XYChart.Data<>(r.getCol1(), r.getVal2()));
                lineChart.getData().add(series);
                animarGrafico(lineChart);
                chartContainer.getChildren().add(lineChart);
                break;

            case "🏨 Quartos Mais Ocupados":
            case "💰 Financeiro":
            case "🛏️ Receita por Tipo Quarto":
            case "👥 Clientes Frequentes":
                CategoryAxis xBar = new CategoryAxis();
                NumberAxis yBar = new NumberAxis();
                BarChart<String, Number> barChart = new BarChart<>(xBar, yBar);
                barChart.setTitle(tipo);
                XYChart.Series<String, Number> barSeries = new XYChart.Series<>();
                barSeries.setName("Quantidade / Valor");
                for (ReportRow r : rows) barSeries.getData().add(new XYChart.Data<>(r.getCol1(), r.getVal1()));
                barChart.getData().add(barSeries);
                animarGrafico(barChart);
                chartContainer.getChildren().add(barChart);
                break;

            case "📊 Taxa de Ocupação":
            case "🔍 Relatório Diário Automático":
                PieChart pieChart = new PieChart();
                pieChart.setTitle(tipo);
                for (ReportRow r : rows) pieChart.getData().add(new PieChart.Data(r.getCol1(), r.getVal1()));
                animarGrafico(pieChart);
                chartContainer.getChildren().add(pieChart);
                break;
        }
    }

    private void animarGrafico(Chart chart) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(50), e -> chart.setOpacity(chart.getOpacity() + 0.05)));
        timeline.setCycleCount(20);
        chart.setOpacity(0);
        timeline.play();
    }

    public void mostrarTela() {
        Stage stage = new Stage();
        stage.setTitle("Sistema de Relatórios - Hotel Management");
        stage.setScene(new Scene(root, 1200, 800));
        stage.show();
    }
}
