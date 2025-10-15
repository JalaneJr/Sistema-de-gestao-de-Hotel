package staff_paineis;

import dao.ClienteDAO;
import model.Cliente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import java.sql.SQLException;
import java.util.List;

public class GerenciarClientesPanel extends VBox {

    private TableView<Cliente> tabela;
    private ObservableList<Cliente> dados;
    private TextField campoPesquisa;

    public GerenciarClientesPanel() {
        Label titulo = new Label("Gestão de Clientes");
        titulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");

        campoPesquisa = new TextField();
        campoPesquisa.setPromptText("Pesquisar por nome ou documento...");

        Button btnPesquisar = new Button("Pesquisar");
        btnPesquisar.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-weight: bold;");

        Button btnAtualizar = new Button("Atualizar Lista");
        btnAtualizar.setStyle("-fx-background-color: #1ABC9C; -fx-text-fill: white; -fx-font-weight: bold;");

        HBox pesquisaBox = new HBox(10, campoPesquisa, btnPesquisar, btnAtualizar);
        pesquisaBox.setAlignment(Pos.CENTER);

        // 🧾 Tabela
        tabela = new TableView<>();
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Cliente, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Cliente, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

        TableColumn<Cliente, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Cliente, String> colTelefone = new TableColumn<>("Telefone");
        colTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));

        TableColumn<Cliente, String> colDoc = new TableColumn<>("Documento");
        colDoc.setCellValueFactory(new PropertyValueFactory<>("documento"));

        tabela.getColumns().addAll(colId, colNome, colEmail, colTelefone, colDoc);

        // 📋 Botões de ações
        Button btnEditar = new Button("Editar");
        btnEditar.setStyle("-fx-background-color: #F1C40F; -fx-text-fill: white; -fx-font-weight: bold;");

        Button btnExcluir = new Button("Eliminar");
        btnExcluir.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-weight: bold;");

        HBox botoes = new HBox(10, btnEditar, btnExcluir);
        botoes.setAlignment(Pos.CENTER);

        Label mensagem = new Label();
        mensagem.setTextFill(Color.RED);

        carregarClientes();

        // 🔍 Pesquisar
        btnPesquisar.setOnAction(e -> pesquisarClientes());
        btnAtualizar.setOnAction(e -> carregarClientes());

        // ✏️ Editar
        btnEditar.setOnAction(e -> editarCliente(mensagem));

        // ❌ Excluir
        btnExcluir.setOnAction(e -> excluirCliente(mensagem));

        this.setSpacing(15);
        this.setPadding(new Insets(20));
        this.setAlignment(Pos.TOP_CENTER);
        this.setStyle("-fx-background-color: #F8F9F9;");
        this.getChildren().addAll(titulo, pesquisaBox, tabela, botoes, mensagem);
    }

    private void carregarClientes() {
        try {
            ClienteDAO dao = new ClienteDAO();
            List<Cliente> lista = dao.listarTodos();
            dados = FXCollections.observableArrayList(lista);
            tabela.setItems(dados);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void pesquisarClientes() {
        String termo = campoPesquisa.getText().trim().toLowerCase();
        if (termo.isEmpty()) {
            carregarClientes();
            return;
        }

        ObservableList<Cliente> filtrados = FXCollections.observableArrayList();
        for (Cliente c : dados) {
            if (c.getNome().toLowerCase().contains(termo) || c.getDocumento().toLowerCase().contains(termo)) {
                filtrados.add(c);
            }
        }
        tabela.setItems(filtrados);
    }

    private void editarCliente(Label mensagem) {
        Cliente selecionado = tabela.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mensagem.setText("⚠️ Selecione um cliente para editar!");
            return;
        }

        Dialog<Cliente> dialog = new Dialog<>();
        dialog.setTitle("Editar Cliente");
        dialog.setHeaderText("Atualize os dados do cliente selecionado:");

        // Campos
        TextField nomeField = new TextField(selecionado.getNome());
        TextField emailField = new TextField(selecionado.getEmail());
        TextField telefoneField = new TextField(selecionado.getTelefone());
        TextField documentoField = new TextField(selecionado.getDocumento());

        VBox box = new VBox(10,
                new Label("Nome:"), nomeField,
                new Label("E-mail:"), emailField,
                new Label("Telefone:"), telefoneField,
                new Label("Documento:"), documentoField
        );
        box.setPadding(new Insets(10));

        dialog.getDialogPane().setContent(box);
        ButtonType salvarBtn = new ButtonType("Salvar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(salvarBtn, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == salvarBtn) {
                // 🔒 Validação do telefone (Moçambique)
                String tel = telefoneField.getText().trim();
                if (!tel.matches("8[2-7]\\d{7}")) {
                    Alert a = new Alert(Alert.AlertType.ERROR, "Telefone inválido! Deve começar com 82–87 e ter 9 dígitos.");
                    a.showAndWait();
                    return null;
                }

                selecionado.setNome(nomeField.getText().trim());
                selecionado.setEmail(emailField.getText().trim());
                selecionado.setTelefone(tel);
                selecionado.setDocumento(documentoField.getText().trim());
                return selecionado;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(cliente -> {
            try (var conn = dao.Conexao.getConnection();
                 var stmt = conn.prepareStatement("UPDATE clientes SET nome=?, email=?, telefone=?, documento=? WHERE id=?")) {

                stmt.setString(1, cliente.getNome());
                stmt.setString(2, cliente.getEmail());
                stmt.setString(3, cliente.getTelefone());
                stmt.setString(4, cliente.getDocumento());
                stmt.setInt(5, cliente.getId());
                stmt.executeUpdate();

                mensagem.setTextFill(Color.GREEN);
                mensagem.setText("✅ Cliente atualizado com sucesso!");
                carregarClientes();
            } catch (SQLException ex) {
                mensagem.setTextFill(Color.RED);
                mensagem.setText("Erro ao atualizar cliente: " + ex.getMessage());
            }
        });
    }

    private void excluirCliente(Label mensagem) {
        Cliente selecionado = tabela.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mensagem.setText("⚠️ Selecione um cliente para eliminar!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmação");
        confirm.setHeaderText("Eliminar Cliente");
        confirm.setContentText("Tem certeza que deseja eliminar o cliente " + selecionado.getNome() + "?");

        confirm.showAndWait().ifPresent(res -> {
            if (res == ButtonType.OK) {
                try (var conn = dao.Conexao.getConnection();
                     var stmt = conn.prepareStatement("DELETE FROM clientes WHERE id=?")) {
                    stmt.setInt(1, selecionado.getId());
                    stmt.executeUpdate();

                    mensagem.setTextFill(Color.GREEN);
                    mensagem.setText("✅ Cliente eliminado com sucesso!");
                    carregarClientes();
                } catch (SQLException ex) {
                    mensagem.setTextFill(Color.RED);
                    mensagem.setText("Erro ao eliminar: " + ex.getMessage());
                }
            }
        });
    }
}
