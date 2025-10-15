package staff_paineis;

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

public class PainelTarifas extends VBox {

    private TableView<Tarifa> tabela;
    private TarifaDAO tarifaDAO = new TarifaDAO();
    private ObservableList<Tarifa> listaTarifas;

    public PainelTarifas() {
        setAlignment(Pos.TOP_CENTER);
        setSpacing(15);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #e8daef;");

        Label titulo = new Label("💲 Consultar Tarifas");
        titulo.setFont(new Font(26));

        Label descricao = new Label("Veja as tarifas atuais e marque promoções disponíveis.");
        descricao.setFont(new Font(16));

        tabela = new TableView<>();
        tabela.setPrefHeight(400);
        configurarTabela();
        carregarTarifas();

        getChildren().addAll(titulo, descricao, tabela);
    }

    private void configurarTabela() {
        TableColumn<Tarifa, String> colTipo = new TableColumn<>("Tipo de Quarto");
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoQuarto"));

        TableColumn<Tarifa, Double> colPreco = new TableColumn<>("Preço (MZN)");
        colPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));

        TableColumn<Tarifa, Boolean> colPromocao = new TableColumn<>("Promoção");
        colPromocao.setCellValueFactory(new PropertyValueFactory<>("promocao"));

        // Coluna com checkbox para alterar promoção
        colPromocao.setCellFactory(tc -> new TableCell<>() {
            private final CheckBox checkBox = new CheckBox();

            {
                checkBox.setOnAction(e -> {
                    Tarifa tarifa = getTableView().getItems().get(getIndex());
                    tarifa.setPromocao(checkBox.isSelected());
                    try {
                        tarifaDAO.atualizarPromocao(tarifa);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    checkBox.setSelected(item != null && item);
                    setGraphic(checkBox);
                }
            }
        });

        tabela.getColumns().addAll(colTipo, colPreco, colPromocao);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void carregarTarifas() {
        listaTarifas = FXCollections.observableArrayList();
        try {
            listaTarifas.addAll(tarifaDAO.listarTodos());
        } catch (Exception e) {
            e.printStackTrace();
        }
        tabela.setItems(listaTarifas);
    }
}
