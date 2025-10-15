package staff_paineis;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import dao.CheckinCheckoutDAO;
import dao.QuartoDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import model.Cliente;
import model.Reserva;

import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PainelCheckinCheckout extends VBox {

    private TableView<Reserva> tabela;
    private ObservableList<Reserva> listaReservas;
    private FilteredList<Reserva> filtradas;
    private TextField campoBusca;

    private final CheckinCheckoutDAO checkinCheckoutDAO = new CheckinCheckoutDAO();
    private final QuartoDAO quartoDAO = new QuartoDAO();

    private Button btnCheckin;
    private Button btnCheckout;
    private Button btnCancelar;

    public PainelCheckinCheckout() {
        setAlignment(Pos.TOP_CENTER);
        setSpacing(18);
        setPadding(new Insets(22));
        setStyle("-fx-background-color: linear-gradient(to bottom, #f1fbf7, #e6f3ec);");

        Label titulo = new Label("🏨 Painel de Check-in / Check-out");
        titulo.setFont(Font.font("Segoe UI Semibold", 24));
        titulo.setTextFill(Color.web("#125a3c"));
        titulo.setEffect(new DropShadow(3, Color.rgb(0,0,0,0.12)));

        Label subtitulo = new Label("Gerencie reservas — realize check-in, check-out e cancelamentos");
        subtitulo.setFont(Font.font("Segoe UI", 14));
        subtitulo.setTextFill(Color.web("#1b4f72"));

        campoBusca = new TextField();
        campoBusca.setPromptText("🔍 Buscar por cliente, número de quarto ou status...");
        campoBusca.setPrefWidth(360);
        campoBusca.setStyle("""
            -fx-background-radius: 10;
            -fx-border-radius: 10;
            -fx-border-color: #cfd8d8;
            -fx-padding: 10;
        """);

        Button btnPesquisar = criarBotao("🔎 Buscar", "#2d9cdb", "#4fb0ee");
        HBox barraSuperior = new HBox(10, campoBusca, btnPesquisar);
        barraSuperior.setAlignment(Pos.CENTER_LEFT);

        tabela = new TableView<>();
        configurarTabela();
        tabela.setPrefHeight(380);

        listaReservas = FXCollections.observableArrayList();
        filtradas = new FilteredList<>(listaReservas, r -> true);
        carregarReservas();

        SortedList<Reserva> ordenadas = new SortedList<>(filtradas);
        ordenadas.comparatorProperty().bind(tabela.comparatorProperty());
        tabela.setItems(ordenadas);

        btnPesquisar.setOnAction(e -> aplicarFiltro());
        campoBusca.setOnAction(e -> aplicarFiltro());

        btnCheckin = criarBotao("✅ Check-in", "#27ae60", "#2ecc71");
        btnCheckout = criarBotao("🚪 Check-out", "#f39c12", "#f5b041");
        btnCancelar = criarBotao("❌ Cancelar", "#e74c3c", "#ec7063");

        btnCheckin.setOnAction(e -> handleCheckin());
        btnCheckout.setOnAction(e -> handleCheckout());
        btnCancelar.setOnAction(e -> handleCancelar());

        HBox botoes = new HBox(14, btnCheckin, btnCheckout, btnCancelar);
        botoes.setAlignment(Pos.CENTER);
        botoes.setPadding(new Insets(10));
        botoes.setBackground(new Background(new BackgroundFill(Color.web("#ffffffcc"), new CornerRadii(10), Insets.EMPTY)));
        botoes.setEffect(new DropShadow(4, Color.rgb(0,0,0,0.08)));

        getChildren().addAll(titulo, subtitulo, barraSuperior, tabela, botoes);
    }

    private void configurarTabela() {
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabela.setStyle("""
            -fx-background-color: white;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
        """);

        TableColumn<Reserva, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setMaxWidth(80);
        colId.setStyle("-fx-alignment: CENTER;");

        TableColumn<Reserva, String> colCliente = new TableColumn<>("Cliente");
        colCliente.setCellValueFactory(data -> javafx.beans.binding.Bindings.createStringBinding(() -> {
            Cliente c = data.getValue().getCliente();
            return c != null ? c.getNome() : "—";
        }));

        TableColumn<Reserva, String> colQuarto = new TableColumn<>("Nº Quarto");
        colQuarto.setCellValueFactory(data -> javafx.beans.binding.Bindings.createStringBinding(() ->
                data.getValue().getQuarto() != null ? String.valueOf(data.getValue().getQuarto().getNumero()) : "—"));

        TableColumn<Reserva, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(data -> javafx.beans.binding.Bindings.createStringBinding(() ->
                data.getValue().getQuarto() != null ? data.getValue().getQuarto().getTipo() : "—"));

        TableColumn<Reserva, String> colEntrada = new TableColumn<>("Entrada");
        colEntrada.setCellValueFactory(data -> javafx.beans.binding.Bindings.createStringBinding(() ->
                data.getValue().getDataEntrada() != null ? data.getValue().getDataEntrada().toString() : "—"));

        TableColumn<Reserva, String> colSaida = new TableColumn<>("Saída");
        colSaida.setCellValueFactory(data -> javafx.beans.binding.Bindings.createStringBinding(() ->
                data.getValue().getDataSaida() != null ? data.getValue().getDataSaida().toString() : "—"));

        TableColumn<Reserva, String> colValor = new TableColumn<>("Valor");
        colValor.setCellValueFactory(data -> javafx.beans.binding.Bindings.createStringBinding(() ->
                String.format("%.2f", data.getValue().getValorTotal())));

        TableColumn<Reserva, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(data -> javafx.beans.binding.Bindings.createStringBinding(() ->
                data.getValue().getStatus() != null ? data.getValue().getStatus() : "—"));

        tabela.getColumns().addAll(colId, colCliente, colQuarto, colTipo, colEntrada, colSaida, colValor, colStatus);
    }

    private Button criarBotao(String texto, String cor, String hover) {
        Button b = new Button(texto);
        b.setFont(Font.font("Segoe UI", 14));
        b.setPrefHeight(36);
        b.setPrefWidth(140);
        b.setStyle("-fx-background-color:" + cor + "; -fx-text-fill:white; -fx-background-radius:8;");
        b.setOnMouseEntered(e -> b.setStyle("-fx-background-color:" + hover + "; -fx-text-fill:white; -fx-background-radius:8;"));
        b.setOnMouseExited(e -> b.setStyle("-fx-background-color:" + cor + "; -fx-text-fill:white; -fx-background-radius:8;"));
        b.setEffect(new DropShadow(3, Color.rgb(0,0,0,0.12)));
        return b;
    }

    private void aplicarFiltro() {
        String termo = campoBusca.getText();
        if (termo == null) termo = "";
        final String t = termo.trim().toLowerCase();

        filtradas.setPredicate(r -> {
            if (t.isEmpty()) return true;
            boolean nomeMatch = r.getCliente() != null && r.getCliente().getNome().toLowerCase().contains(t);
            boolean quartoMatch = r.getQuarto() != null && String.valueOf(r.getQuarto().getNumero()).contains(t);
            boolean statusMatch = r.getStatus() != null && r.getStatus().toLowerCase().contains(t);
            return nomeMatch || quartoMatch || statusMatch;
        });
    }

    private void carregarReservas() {
        listaReservas.clear();
        try {
            List<Reserva> todas = checkinCheckoutDAO.listarTodasReservas();
            listaReservas.addAll(todas);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro ao carregar reservas: " + e.getMessage());
        }
    }

    // =================== CHECK-IN ===================
    private void handleCheckin() {
        Reserva sel = tabela.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarAlerta("Selecione uma reserva para realizar o check-in.");
            return;
        }

        if (!"ATIVA".equalsIgnoreCase(sel.getStatus())) {
            mostrarAlerta("Não é possível fazer check-in. Apenas reservas ATIVAS podem ser iniciadas.");
            return;
        }

        try {
            checkinCheckoutDAO.realizarCheckin(sel);
            carregarReservas();
            mostrarInfo("Check-in realizado com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro ao realizar check-in: " + e.getMessage());
        }
    }

    // =================== CHECK-OUT ===================
    private void handleCheckout() {
        Reserva sel = tabela.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarAlerta("Selecione uma reserva para realizar o check-out.");
            return;
        }

        if (!"EM ANDAMENTO".equalsIgnoreCase(sel.getStatus())) {
            mostrarAlerta("Aviso: Esta reserva já foi finalizada ou não está em andamento. O recibo ainda pode ser gerado.");
        } else {
            try {
                checkinCheckoutDAO.realizarCheckout(sel);
                carregarReservas();
                mostrarInfo("Check-out concluído com sucesso!");
            } catch (Exception e) {
                e.printStackTrace();
                mostrarAlerta("Erro ao realizar check-out: " + e.getMessage());
            }
        }

        // Sempre gerar recibo independentemente do status
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar Recibo em PDF");
        fileChooser.setInitialFileName("Recibo_" + (sel.getCliente() != null ? sel.getCliente().getNome() : "Cliente") + ".pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        java.io.File file = fileChooser.showSaveDialog(this.getScene().getWindow());

        if (file != null) {
            gerarReciboPDFMelhorado(sel, file);
            mostrarInfo("Recibo salvo em PDF com sucesso!");
        }
    }

    // =================== CANCELAR RESERVA ===================
    private void handleCancelar() {
        Reserva sel = tabela.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarAlerta("Selecione uma reserva para cancelar.");
            return;
        }

        if (!"ATIVA".equalsIgnoreCase(sel.getStatus())) {
            mostrarAlerta("Não é possível cancelar. Apenas reservas ATIVAS podem ser canceladas.");
            return;
        }

        try {
            checkinCheckoutDAO.cancelarReserva(sel);
            carregarReservas();
            mostrarInfo("Reserva cancelada com sucesso.");
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro ao cancelar reserva: " + e.getMessage());
        }
    }

    // =================== MÉTODOS AUXILIARES ===================
    private void mostrarAlerta(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void mostrarInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    // =================== GERAR PDF APRIMORADO ===================
    private void gerarReciboPDFMelhorado(Reserva reserva, java.io.File file) {
        try {
            Document doc = new Document();
            PdfWriter.getInstance(doc, new FileOutputStream(file));
            doc.open();

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            // Cabeçalho
            Paragraph cabecalho = new Paragraph("🏨 LUXORY Hotel\n\n");
            cabecalho.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
            doc.add(cabecalho);

            // Título do recibo
            Paragraph titulo = new Paragraph("Recibo de Hospedagem\n\n");
            titulo.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
            doc.add(titulo);

            // Separador
            doc.add(new Paragraph("-------------------------------\n"));

            // Informações da reserva
            doc.add(new Paragraph("Cliente: " + (reserva.getCliente() != null ? reserva.getCliente().getNome() : "—")));
            doc.add(new Paragraph("Quarto: " + (reserva.getQuarto() != null ? reserva.getQuarto().getNumero() : "—")));
            doc.add(new Paragraph("Tipo: " + (reserva.getQuarto() != null ? reserva.getQuarto().getTipo() : "—")));
            doc.add(new Paragraph("Check-in: " + (reserva.getDataEntrada() != null ? reserva.getDataEntrada().format(dtf) : "—")));
            doc.add(new Paragraph("Check-out: " + (reserva.getDataSaida() != null ? reserva.getDataSaida().format(dtf) : "—")));
            doc.add(new Paragraph(String.format("Valor Total: %,.2f MZN", reserva.getValorTotal())));

            // Separador final
            doc.add(new Paragraph("\n-------------------------------"));

            // Footer
            Paragraph footer = new Paragraph("\nObrigado pela preferência!");
            footer.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
            doc.add(footer);

            doc.close();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro ao gerar PDF: " + e.getMessage());
        }
    }
}