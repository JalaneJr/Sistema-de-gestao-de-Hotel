package admin_paineis;

import dao.TarifaDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import model.Tarifa;

import java.util.Optional;

public class TarifasView {

    private VBox root;
    private TableView<Tarifa> tabela;
    private ObservableList<Tarifa> listaTarifas;

    private TextField tfTipoQuarto, tfPreco;
    private CheckBox cbPromocao;

    private TarifaDAO tarifaDAO = new TarifaDAO();

    public TarifasView() {
        root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #fdf6e3;");

        Label titulo = new Label("💲 Tarifas & Promoções");
        titulo.setFont(Font.font("Segoe UI Semibold", 24));
        titulo.setTextFill(Color.web("#d35400"));

        // ───── Campos de cadastro ─────
        tfTipoQuarto = new TextField();
        tfTipoQuarto.setPromptText("Tipo de Quarto");
        tfTipoQuarto.setPrefWidth(150);

        tfPreco = new TextField();
        tfPreco.setPromptText("Preço");
        tfPreco.setPrefWidth(100);

        cbPromocao = new CheckBox("Promoção");

        Button btnAdicionar = new Button("➕ Adicionar");
        btnAdicionar.setStyle(botaoStyle("#27ae60"));
        btnAdicionar.setOnMouseEntered(e -> btnAdicionar.setStyle(botaoHoverStyle("#27ae60")));
        btnAdicionar.setOnMouseExited(e -> btnAdicionar.setStyle(botaoStyle("#27ae60")));
        btnAdicionar.setOnAction(e -> adicionarTarifa());

        HBox campos = new HBox(15, tfTipoQuarto, tfPreco, cbPromocao, btnAdicionar);
        campos.setAlignment(Pos.CENTER_LEFT);

        // ───── Tabela ─────
        tabela = new TableView<>();
        configurarTabela();
        carregarTarifas();

        // ───── Botões editar e excluir ─────
        Button btnEditar = new Button("✏️ Editar");
        btnEditar.setStyle(botaoStyle("#f1c40f"));
        btnEditar.setOnMouseEntered(e -> btnEditar.setStyle(botaoHoverStyle("#f1c40f")));
        btnEditar.setOnMouseExited(e -> btnEditar.setStyle(botaoStyle("#f1c40f")));
        btnEditar.setOnAction(e -> editarTarifa());

        Button btnExcluir = new Button("🗑 Excluir");
        btnExcluir.setStyle(botaoStyle("#e74c3c"));
        btnExcluir.setOnMouseEntered(e -> btnExcluir.setStyle(botaoHoverStyle("#e74c3c")));
        btnExcluir.setOnMouseExited(e -> btnExcluir.setStyle(botaoStyle("#e74c3c")));
        btnExcluir.setOnAction(e -> excluirTarifa());

        HBox botoes = new HBox(15, btnEditar, btnExcluir);
        botoes.setAlignment(Pos.CENTER);

        root.getChildren().addAll(titulo, campos, tabela, botoes);
    }

    public VBox getRoot() { return root; }

    // ───── Estilos ─────
    private String botaoStyle(String cor) {
        return "-fx-background-color:" + cor + "; -fx-text-fill:white; -fx-background-radius:6; -fx-cursor: hand;";
    }

    private String botaoHoverStyle(String cor) {
        return "-fx-background-color:" + cor + "AA; -fx-text-fill:white; -fx-background-radius:6; -fx-cursor: hand;";
    }

    // ───── Configura tabela ─────
    private void configurarTabela() {
        tabela.setPrefHeight(400);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Tarifa, String> colTipo = new TableColumn<>("Tipo de Quarto");
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoQuarto"));

        TableColumn<Tarifa, Double> colPreco = new TableColumn<>("Preço");
        colPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));

        TableColumn<Tarifa, Boolean> colPromocao = new TableColumn<>("Promoção");
        colPromocao.setCellValueFactory(new PropertyValueFactory<>("promocao"));

        tabela.getColumns().addAll(colTipo, colPreco, colPromocao);
    }

    // ───── Carregar dados ─────
    private void carregarTarifas() {
        listaTarifas = FXCollections.observableArrayList();
        try {
            listaTarifas.addAll(tarifaDAO.listarTodas());
        } catch (Exception e) {
            e.printStackTrace();
        }
        tabela.setItems(listaTarifas);
    }

    // ───── Ações ─────
    private void adicionarTarifa() {
        String tipo = tfTipoQuarto.getText().trim();
        String precoStr = tfPreco.getText().trim();
        boolean promocao = cbPromocao.isSelected();

        if (tipo.isEmpty() || precoStr.isEmpty()) {
            mostrarAlerta("Todos os campos devem ser preenchidos!");
            return;
        }

        double preco;
        try {
            preco = Double.parseDouble(precoStr);
        } catch (NumberFormatException e) {
            mostrarAlerta("Preço inválido!");
            return;
        }

        Tarifa t = new Tarifa();
        t.setTipoQuarto(tipo);
        t.setPreco(preco);
        t.setPromocao(promocao);

        try {
            tarifaDAO.inserir(t);
            listaTarifas.add(t);
            mostrarInfo("Tarifa adicionada com sucesso!");
            limparCampos();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro ao adicionar tarifa: " + e.getMessage());
        }
    }

    private void editarTarifa() {
        Tarifa sel = tabela.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrarAlerta("Selecione uma tarifa!"); return; }

        TextInputDialog dialog = new TextInputDialog(sel.getTipoQuarto());
        dialog.setHeaderText("Editar Tipo de Quarto");
        dialog.setContentText("Tipo:");
        Optional<String> res = dialog.showAndWait();
        res.ifPresent(novoTipo -> {
            if (!novoTipo.trim().isEmpty()) sel.setTipoQuarto(novoTipo.trim());
        });

        tabela.refresh();
        // TODO: atualizar no banco via DAO
    }

    private void excluirTarifa() {
        Tarifa sel = tabela.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrarAlerta("Selecione uma tarifa!"); return; }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("Confirmar exclusão");
        confirm.setContentText("Deseja realmente excluir esta tarifa?");
        Optional<ButtonType> resultado = confirm.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            listaTarifas.remove(sel);
            // TODO: remover do banco via DAO
        }
    }

    private void limparCampos() {
        tfTipoQuarto.clear();
        tfPreco.clear();
        cbPromocao.setSelected(false);
    }

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
}
