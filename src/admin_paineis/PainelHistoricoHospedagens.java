package admin_paineis;

import dao.CheckinCheckoutDAO;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import model.CheckinCheckout;
import model.Cliente;
import model.Quarto;
import model.Reserva;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class PainelHistoricoHospedagens extends VBox {

    private final TableView<CheckinCheckout> tabela;
    private final ObservableList<CheckinCheckout> listaHistorico;
    private final FilteredList<CheckinCheckout> filtradas;
    private final TextField campoBusca;
    private final Label lblTotal;
    private final CheckinCheckoutDAO checkinCheckoutDAO = new CheckinCheckoutDAO();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public PainelHistoricoHospedagens() {
        // Layout principal
        setAlignment(Pos.TOP_CENTER);
        setSpacing(20);
        setPadding(new Insets(25));
        setStyle("-fx-background-color: linear-gradient(to bottom, #f6fff9, #eaf8f3);");

        // Cabeçalho
        Label titulo = new Label("📜 Histórico de Hospedagens");
        titulo.setFont(Font.font("Segoe UI Semibold", 24));
        titulo.setTextFill(Color.web("#145a32"));
        titulo.setEffect(new DropShadow(3, Color.rgb(0, 0, 0, 0.12)));

        Label subtitulo = new Label("Veja todas as estadias concluídas (check-ins e check-outs).");
        subtitulo.setFont(Font.font("Segoe UI", 14));
        subtitulo.setTextFill(Color.web("#1b4f72"));

        // Barra de busca e ações
        campoBusca = new TextField();
        campoBusca.setPromptText("🔍 Buscar por nome, quarto ou status...");
        campoBusca.setPrefWidth(400);
        campoBusca.setStyle("""
            -fx-background-radius: 10;
            -fx-border-radius: 10;
            -fx-border-color: #cfd8d8;
            -fx-padding: 10;
        """);

        Button btnPesquisar = criarBotao("Buscar", "#2d9cdb", "#4fb0ee");
        Button btnRecarregar = criarBotao("Recarregar", "#27ae60", "#2ecc71");

        HBox barraSuperior = new HBox(10, campoBusca, btnPesquisar, btnRecarregar);
        barraSuperior.setAlignment(Pos.CENTER_LEFT);

        // Tabela
        tabela = new TableView<>();
        configurarTabela();

        // Listas
        listaHistorico = FXCollections.observableArrayList();
        filtradas = new FilteredList<>(listaHistorico, r -> true);
        SortedList<CheckinCheckout> ordenadas = new SortedList<>(filtradas);
        ordenadas.comparatorProperty().bind(tabela.comparatorProperty());
        tabela.setItems(ordenadas);

        // Rodapé
        lblTotal = new Label();
        lblTotal.setFont(Font.font("Segoe UI", 13));
        lblTotal.setTextFill(Color.web("#2c3e50"));

        // Carregar dados
        carregarHistorico();

        // Eventos
        btnPesquisar.setOnAction(e -> aplicarFiltro());
        campoBusca.setOnAction(e -> aplicarFiltro());
        btnRecarregar.setOnAction(e -> carregarHistorico());

        // Montagem final
        getChildren().addAll(titulo, subtitulo, barraSuperior, tabela, lblTotal);
    }

    private void configurarTabela() {
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabela.setPrefHeight(450);
        tabela.setStyle("""
            -fx-background-color: white;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
        """);

        TableColumn<CheckinCheckout, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setMaxWidth(80);
        colId.setStyle("-fx-alignment: CENTER;");

        TableColumn<CheckinCheckout, String> colCliente = new TableColumn<>("Cliente");
        colCliente.setCellValueFactory(data -> Bindings.createStringBinding(() -> {
            Reserva r = data.getValue().getReserva();
            Cliente c = r != null ? r.getCliente() : null;
            return c != null ? c.getNome() : "—";
        }));

        TableColumn<CheckinCheckout, String> colQuarto = new TableColumn<>("Quarto");
        colQuarto.setCellValueFactory(data -> Bindings.createStringBinding(() -> {
            Reserva r = data.getValue().getReserva();
            Quarto q = r != null ? r.getQuarto() : null;
            return q != null ? String.valueOf(q.getNumero()) : "—";
        }));

        TableColumn<CheckinCheckout, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(data -> Bindings.createStringBinding(() -> {
            Reserva r = data.getValue().getReserva();
            Quarto q = r != null ? r.getQuarto() : null;
            return q != null ? q.getTipo() : "—";
        }));

        TableColumn<CheckinCheckout, String> colCheckin = new TableColumn<>("Check-in");
        colCheckin.setCellValueFactory(data ->
                Bindings.createStringBinding(() -> {
                    var d = data.getValue().getDataCheckin();
                    return d != null ? d.format(formatter) : "—";
                }));

        TableColumn<CheckinCheckout, String> colCheckout = new TableColumn<>("Check-out");
        colCheckout.setCellValueFactory(data ->
                Bindings.createStringBinding(() -> {
                    var d = data.getValue().getDataCheckout();
                    return d != null ? d.format(formatter) : "—";
                }));

        TableColumn<CheckinCheckout, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(data ->
                Bindings.createStringBinding(() -> {
                    String status = data.getValue().getStatus();
                    return status != null ? status : "—";
                }));
        colStatus.setStyle("-fx-alignment: CENTER;");

        tabela.getColumns().addAll(colId, colCliente, colQuarto, colTipo, colCheckin, colCheckout, colStatus);
    }

    private Button criarBotao(String texto, String cor, String hover) {
        Button b = new Button(texto);
        b.setFont(Font.font("Segoe UI", 14));
        b.setPrefHeight(36);
        b.setPrefWidth(120);
        b.setStyle("-fx-background-color:" + cor + "; -fx-text-fill:white; -fx-background-radius:8;");
        b.setOnMouseEntered(e -> b.setStyle("-fx-background-color:" + hover + "; -fx-text-fill:white; -fx-background-radius:8;"));
        b.setOnMouseExited(e -> b.setStyle("-fx-background-color:" + cor + "; -fx-text-fill:white; -fx-background-radius:8;"));
        b.setEffect(new DropShadow(3, Color.rgb(0, 0, 0, 0.12)));
        return b;
    }

    private void carregarHistorico() {
        listaHistorico.clear();
        try {
            List<CheckinCheckout> todos = checkinCheckoutDAO.listarTodos();
            if (todos == null || todos.isEmpty()) {
                mostrarAlerta("Nenhum histórico encontrado.");
            } else {
                listaHistorico.addAll(todos);
            }
            atualizarContagem();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarErro("Erro ao carregar histórico", e.getMessage());
        }
    }

    private void aplicarFiltro() {
        String termo = campoBusca.getText();
        if (termo == null) termo = "";
        final String t = termo.trim().toLowerCase();

        filtradas.setPredicate(cc -> {
            if (t.isEmpty()) return true;
            Reserva r = cc.getReserva();
            Cliente c = r != null ? r.getCliente() : null;
            Quarto q = r != null ? r.getQuarto() : null;

            boolean nomeMatch = c != null && c.getNome().toLowerCase().contains(t);
            boolean quartoMatch = q != null && String.valueOf(q.getNumero()).contains(t);
            boolean statusMatch = cc.getStatus() != null && cc.getStatus().toLowerCase().contains(t);

            return nomeMatch || quartoMatch || statusMatch;
        });

        atualizarContagem();
    }

    private void atualizarContagem() {
        lblTotal.setText("Total de hospedagens: " + filtradas.size());
    }

    private void mostrarAlerta(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setTitle("Informação");
        a.setContentText(msg);
        a.showAndWait();
    }

    private void mostrarErro(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(titulo);
        a.setContentText(msg);
        a.showAndWait();
    }

    public Node getRoot() {
        return this;
    }
}
