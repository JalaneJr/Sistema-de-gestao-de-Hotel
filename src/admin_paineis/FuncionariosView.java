package admin_paineis;

import dao.FuncionarioDAO;
import dao.UsuarioDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import model.Funcionario;
import model.Usuario;

import java.util.Optional;
import java.util.Random;

public class FuncionariosView {

    private VBox root;
    private TableView<Funcionario> tabela;
    private ObservableList<Funcionario> listaFuncionarios;

    private TextField tfNome, tfTelefone, tfSalario;
    private ComboBox<String> cbCargo, cbDocumentoTipo;
    private TextField tfDocumento;

    private FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    public FuncionariosView() {
        root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #f0f8ff;");

        // ───── Título ─────
        Label titulo = new Label("👨‍💼 Gestão de Funcionários");
        titulo.setFont(Font.font("Segoe UI Semibold", 24));
        titulo.setTextFill(Color.web("#145a32"));

        // ───── Campos de cadastro ─────
        tfNome = new TextField();
        tfNome.setPromptText("Nome completo");
        tfNome.setPrefWidth(200);

        tfTelefone = new TextField();
        tfTelefone.setPromptText("Telefone (82-87 + 7 números)");
        tfTelefone.setPrefWidth(150);

        tfSalario = new TextField();
        tfSalario.setPromptText("Salário");
        tfSalario.setPrefWidth(100);

        cbCargo = new ComboBox<>();
        cbCargo.getItems().addAll("Recepcionista", "Gestor", "Faxineiro");
        cbCargo.setPrefWidth(150);
        cbCargo.setPromptText("Cargo");

        cbDocumentoTipo = new ComboBox<>();
        cbDocumentoTipo.getItems().addAll("BI", "Passaporte");
        cbDocumentoTipo.setPrefWidth(120);
        cbDocumentoTipo.setPromptText("Documento");

        tfDocumento = new TextField();
        tfDocumento.setPromptText("Documento");

        HBox documentoBox = new HBox(10, cbDocumentoTipo, tfDocumento);
        documentoBox.setAlignment(Pos.CENTER_LEFT);

        HBox camposCadastro = new HBox(15, tfNome, tfTelefone, tfSalario, cbCargo, documentoBox);
        camposCadastro.setAlignment(Pos.CENTER_LEFT);

        // ───── Botões ─────
        Button btnCadastrar = new Button("➕ Cadastrar");
        btnCadastrar.setStyle(botaoStyle("#3498db"));
        btnCadastrar.setOnMouseEntered(e -> btnCadastrar.setStyle(botaoHoverStyle("#3498db")));
        btnCadastrar.setOnMouseExited(e -> btnCadastrar.setStyle(botaoStyle("#3498db")));
        btnCadastrar.setOnAction(e -> cadastrarFuncionario());

        Button btnEditar = new Button("✏️ Editar");
        btnEditar.setStyle(botaoStyle("#f1c40f"));
        btnEditar.setOnMouseEntered(e -> btnEditar.setStyle(botaoHoverStyle("#f1c40f")));
        btnEditar.setOnMouseExited(e -> btnEditar.setStyle(botaoStyle("#f1c40f")));
        btnEditar.setOnAction(e -> editarFuncionario());

        Button btnExcluir = new Button("🗑 Excluir");
        btnExcluir.setStyle(botaoStyle("#e74c3c"));
        btnExcluir.setOnMouseEntered(e -> btnExcluir.setStyle(botaoHoverStyle("#e74c3c")));
        btnExcluir.setOnMouseExited(e -> btnExcluir.setStyle(botaoStyle("#e74c3c")));
        btnExcluir.setOnAction(e -> excluirFuncionario());

        HBox botoes = new HBox(15, btnCadastrar, btnEditar, btnExcluir);
        botoes.setAlignment(Pos.CENTER);

        // ───── Tabela ─────
        tabela = new TableView<>();
        configurarTabela();
        carregarFuncionarios();

        root.getChildren().addAll(titulo, camposCadastro, botoes, tabela);
    }

    public VBox getRoot() { return root; }

    // ───── Estilos ─────
    private String botaoStyle(String cor) {
        return "-fx-background-color:" + cor + "; -fx-text-fill:white; -fx-background-radius:6; -fx-cursor: hand;";
    }

    private String botaoHoverStyle(String cor) {
        return "-fx-background-color:" + cor + "AA; -fx-text-fill:white; -fx-background-radius:6; -fx-cursor: hand;";
    }

    // ───── Configura Tabela ─────
    private void configurarTabela() {
        tabela.setPrefHeight(400);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Funcionario, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

        TableColumn<Funcionario, String> colCargo = new TableColumn<>("Cargo");
        colCargo.setCellValueFactory(new PropertyValueFactory<>("cargo"));

        TableColumn<Funcionario, String> colTelefone = new TableColumn<>("Telefone");
        colTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));

        TableColumn<Funcionario, String> colDocumento = new TableColumn<>("Documento");
        colDocumento.setCellValueFactory(new PropertyValueFactory<>("documento"));

        TableColumn<Funcionario, Double> colSalario = new TableColumn<>("Salário");
        colSalario.setCellValueFactory(new PropertyValueFactory<>("salario"));

        tabela.getColumns().addAll(colNome, colCargo, colTelefone, colDocumento, colSalario);
    }

    // ───── Carregar dados ─────
    private void carregarFuncionarios() {
        listaFuncionarios = FXCollections.observableArrayList();
        try {
            listaFuncionarios.addAll(funcionarioDAO.listarTodos());
        } catch (Exception e) {
            e.printStackTrace();
        }
        tabela.setItems(listaFuncionarios);
    }

    // ───── Cadastro ─────
    private void cadastrarFuncionario() {
        String nome = tfNome.getText().trim();
        String telefone = tfTelefone.getText().trim();
        String salarioStr = tfSalario.getText().trim();
        String cargo = cbCargo.getValue();
        String tipoDoc = cbDocumentoTipo.getValue();
        String documento = tfDocumento.getText().trim();

        if (nome.isEmpty() || telefone.isEmpty() || salarioStr.isEmpty() ||
                cargo == null || tipoDoc == null || documento.isEmpty()) {
            mostrarAlerta("Todos os campos devem ser preenchidos!");
            return;
        }

        if (!telefone.matches("8[2-7]\\d{7}")) {
            mostrarAlerta("Telefone inválido! Deve iniciar com 82-87 e ter 9 dígitos.");
            return;
        }

        if (!validarDocumento(tipoDoc, documento)) {
            mostrarAlerta("Documento inválido!");
            return;
        }

        double salario;
        try {
            salario = Double.parseDouble(salarioStr);
        } catch (NumberFormatException ex) {
            mostrarAlerta("Salário inválido!");
            return;
        }

        Funcionario f = new Funcionario();
        f.setNome(nome);
        f.setTelefone(telefone);
        f.setCargo(cargo);
        f.setSalario((int) salario);
        f.setDocumento(documento);

        // Gera email e senha
        String email = gerarEmail(cargo, nome);
        String senha = gerarSenha(nome);
        f.setEmail(email);

        try {
            funcionarioDAO.inserir(f);

            // Salvar credenciais na tabela Usuario
            Usuario u = new Usuario();
            u.setNome(nome);
            u.setEmail(email);
            u.setSenha(senha);
            u.setPerfil(cargo.equals("Recepcionista") ? "staff" : "admin");
            usuarioDAO.registrar(u);

            listaFuncionarios.add(f);

            mostrarInfo("Funcionário cadastrado!\nUsuário: " + email + "\nSenha: " + senha);
            limparCampos();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro ao cadastrar funcionário: " + e.getMessage());
        }
    }

    // ───── Edição ─────
    private void editarFuncionario() {
        Funcionario sel = tabela.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrarAlerta("Selecione um funcionário!"); return; }

        TextInputDialog dialog = new TextInputDialog(sel.getNome());
        dialog.setHeaderText("Editar Nome");
        dialog.setContentText("Nome:");
        Optional<String> res = dialog.showAndWait();
        res.ifPresent(novoNome -> {
            if (!novoNome.trim().isEmpty()) sel.setNome(novoNome.trim());
        });

        try {
            // TODO: implementar atualizar no DAO
            tabela.refresh();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ───── Exclusão ─────
    private void excluirFuncionario() {
        Funcionario sel = tabela.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrarAlerta("Selecione um funcionário!"); return; }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("Confirmar exclusão");
        confirm.setContentText("Deseja realmente excluir este funcionário?");
        Optional<ButtonType> resultado = confirm.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            listaFuncionarios.remove(sel);
            // TODO: implementar delete no FuncionarioDAO
        }
    }

    // ───── Geração de email e senha ─────
    private String gerarEmail(String cargo, String nome) {
        String sufixo = cargo.equals("Recepcionista") ? ".staff@gmail.com" : ".admin@gmail.com";
        return nome.toLowerCase().replaceAll("\\s+", "") + sufixo;
    }

    private String gerarSenha(String nome) {
        Random r = new Random();
        int n = r.nextInt(20); // 00-19
        return nome.toLowerCase().replaceAll("\\s+", "") + String.format("%02d", n);
    }

    // ───── Documento ─────
    private boolean validarDocumento(String tipo, String doc) {
        if (tipo.equals("BI")) return doc.matches("\\d{12}[A-Z]");
        else return doc.matches("[A-Z]{2}\\d{7}");
    }

    // ───── Alertas ─────
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

    private void limparCampos() {
        tfNome.clear();
        tfTelefone.clear();
        tfSalario.clear();
        cbCargo.getSelectionModel().clearSelection();
        cbDocumentoTipo.getSelectionModel().clearSelection();
        tfDocumento.clear();
    }

}
