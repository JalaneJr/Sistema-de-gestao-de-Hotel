package cliente_paineis;

import dao.ClienteDAO;
import model.Cliente;
import model.Usuario;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.sql.SQLException;
import java.util.function.Consumer;

public class CadastroPanel extends VBox {

    private Consumer<Cliente> callback;

    public CadastroPanel(Usuario usuarioLogado, Consumer<Cliente> callback) {
        this.callback = callback;
        initUI(usuarioLogado);
    }

    private void initUI(Usuario usuarioLogado) {
        this.setAlignment(Pos.CENTER);
        this.setSpacing(15);
        this.setPadding(new Insets(20));
        this.setMaxWidth(350);
        this.setStyle("-fx-background-color: #2C3E50; "
                + "-fx-border-color: #BDC3C7; "
                + "-fx-border-radius: 10; "
                + "-fx-background-radius: 10;");

        Label titulo = new Label("Cadastro de Cliente");
        titulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");

        // Campos
        TextField nomeField = new TextField();
        nomeField.setPromptText("Nome completo");
        nomeField.setMaxWidth(250);

        TextField emailField = new TextField();
        emailField.setPromptText("E-mail");
        emailField.setMaxWidth(250);

        TextField telefoneField = new TextField();
        telefoneField.setPromptText("Telefone (ex: 84xxxxxxx)");
        telefoneField.setMaxWidth(250);

        ComboBox<String> tipoDocumentoBox = new ComboBox<>();
        tipoDocumentoBox.getItems().addAll("BI", "Passaporte");
        tipoDocumentoBox.setPromptText("Tipo de documento");
        tipoDocumentoBox.setMaxWidth(250);

        TextField documentoField = new TextField();
        documentoField.setPromptText("Documento");
        documentoField.setMaxWidth(250);

        Label mensagem = new Label();
        mensagem.setTextFill(Color.RED);

        Button btnSalvar = new Button("Cadastrar");
        btnSalvar.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white; -fx-font-weight: bold;");
        Button btnLimpar = new Button("Limpar");
        btnLimpar.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-weight: bold;");

        // Preencher nome e email automaticamente
        if (usuarioLogado != null) {
            nomeField.setText(usuarioLogado.getNome());
            emailField.setText(usuarioLogado.getEmail());
            nomeField.setDisable(true);
            emailField.setDisable(true);
        }

        // Ação do botão Salvar
        btnSalvar.setOnAction(e -> {
            mensagem.setText("");

            String nome = nomeField.getText().trim();
            String email = emailField.getText().trim();
            String telefone = telefoneField.getText().trim();
            String tipoDoc = tipoDocumentoBox.getValue();
            String documento = documentoField.getText().trim();

            if (telefone.isEmpty() || tipoDoc == null || documento.isEmpty()) {
                mensagem.setText("⚠️ Todos os campos obrigatórios devem ser preenchidos!");
                return;
            }

            if (!telefone.matches("8[2-7]\\d{7}")) {
                mensagem.setText("❌ Telefone inválido! Deve começar com 82–87 e ter 9 dígitos.");
                return;
            }

            if (tipoDoc.equals("BI") && !documento.matches("\\d{12}[A-Z]")) {
                mensagem.setText("❌ BI inválido! Deve conter 12 números e 1 letra maiúscula.");
                return;
            }

            if (tipoDoc.equals("Passaporte") && !documento.matches("[A-Z]{2}\\d{7}")) {
                mensagem.setText("❌ Passaporte inválido! Deve ter 2 letras maiúsculas e 7 números.");
                return;
            }

            Cliente cliente = new Cliente();
            cliente.setNome(nome);
            cliente.setEmail(email);
            cliente.setTelefone(telefone);
            cliente.setDocumento(tipoDoc + ": " + documento);

            try {
                ClienteDAO dao = new ClienteDAO();
                dao.inserir(cliente);

                mensagem.setTextFill(Color.GREEN);
                mensagem.setText("✅ Cliente cadastrado com sucesso!");

                // Limpar apenas campos preenchidos pelo usuário
                telefoneField.clear();
                tipoDocumentoBox.setValue(null);
                documentoField.clear();

                // Notificar ClienteView
                if (callback != null) {
                    callback.accept(cliente);
                }

            } catch (SQLException ex) {
                mensagem.setTextFill(Color.RED);
                mensagem.setText("Erro ao salvar no banco: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // Limpar campos
        btnLimpar.setOnAction(e -> {
            telefoneField.clear();
            tipoDocumentoBox.setValue(null);
            documentoField.clear();
            mensagem.setText("");
        });

        VBox camposBox = new VBox(10, nomeField, emailField, telefoneField, tipoDocumentoBox, documentoField);
        camposBox.setAlignment(Pos.CENTER);
        HBox botoesBox = new HBox(10, btnSalvar, btnLimpar);
        botoesBox.setAlignment(Pos.CENTER);

        this.getChildren().addAll(titulo, camposBox, botoesBox, mensagem);
    }
}
