package staff_paineis;

import dao.ClienteDAO;
import model.Cliente;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import java.sql.SQLException;

public class CadastroPanel extends VBox {

    public CadastroPanel() {
        // 🔷 Título
        Label titulo = new Label("Cadastro de Cliente");
        titulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");

        // 🔹 Campos de texto
        TextField nomeField = new TextField();
        nomeField.setPromptText("Nome completo");
        nomeField.setMaxWidth(260);

        TextField emailField = new TextField();
        emailField.setPromptText("E-mail");
        emailField.setMaxWidth(260);

        TextField telefoneField = new TextField();
        telefoneField.setPromptText("Telefone (ex: 84xxxxxxx)");
        telefoneField.setMaxWidth(260);

        // ComboBox para tipo de documento
        ComboBox<String> tipoDocumentoBox = new ComboBox<>();
        tipoDocumentoBox.getItems().addAll("BI", "Passaporte");
        tipoDocumentoBox.setPromptText("Tipo de documento");
        tipoDocumentoBox.setMaxWidth(260);

        TextField documentoField = new TextField();
        documentoField.setPromptText("Digite o documento...");
        documentoField.setMaxWidth(260);

        // 🔘 Botões
        Button btnSalvar = new Button("Cadastrar");
        Button btnLimpar = new Button("Limpar");
        btnSalvar.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white; -fx-font-weight: bold;");
        btnLimpar.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-weight: bold;");

        // 🔔 Mensagem de feedback
        Label mensagem = new Label();
        mensagem.setTextFill(Color.RED);

        // 🧠 Ação do botão salvar
        btnSalvar.setOnAction(e -> {
            mensagem.setText("");

            String nome = nomeField.getText().trim();
            String email = emailField.getText().trim();
            String telefone = telefoneField.getText().trim();
            String tipoDoc = tipoDocumentoBox.getValue();
            String documento = documentoField.getText().trim();

            // ⚠️ Validações
            if (nome.isEmpty() || email.isEmpty() || telefone.isEmpty() || documento.isEmpty() || tipoDoc == null) {
                mensagem.setText("⚠️ Todos os campos são obrigatórios!");
                return;
            }

            if (!email.matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
                mensagem.setText("❌ E-mail inválido!");
                return;
            }

            if (!telefone.matches("8[2-7]\\d{7}")) {
                mensagem.setText("❌ Telefone inválido! Deve começar com 82–87 e ter 9 dígitos.");
                return;
            }

            if (tipoDoc.equals("BI")) {
                if (!documento.matches("\\d{12}[A-Z]")) {
                    mensagem.setText("❌ BI inválido! Deve conter 12 números e 1 letra maiúscula no final.");
                    return;
                }
            } else if (tipoDoc.equals("Passaporte")) {
                if (!documento.matches("[A-Z]{2}\\d{7}")) {
                    mensagem.setText("❌ Passaporte inválido! Deve ter 2 letras maiúsculas seguidas de 7 números.");
                    return;
                }
            }

            // ✅ Criar cliente
            Cliente cliente = new Cliente();
            cliente.setNome(nome);
            cliente.setEmail(email);
            cliente.setTelefone(telefone);
            cliente.setDocumento(tipoDoc + ": " + documento);

            // ⚙️ Sempre cadastrado por funcionário
            cliente.setCadastradoPor("FUNCIONARIO");

            try {
                ClienteDAO dao = new ClienteDAO();
                dao.inserir(cliente);

                mensagem.setTextFill(Color.GREEN);
                mensagem.setText("✅ Cliente cadastrado com sucesso!");

                nomeField.clear();
                emailField.clear();
                telefoneField.clear();
                documentoField.clear();
                tipoDocumentoBox.setValue(null);

            } catch (SQLException ex) {
                mensagem.setTextFill(Color.RED);
                mensagem.setText("Erro ao salvar no banco: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // 🔄 Limpar campos
        btnLimpar.setOnAction(e -> {
            nomeField.clear();
            emailField.clear();
            telefoneField.clear();
            documentoField.clear();
            tipoDocumentoBox.setValue(null);
            mensagem.setText("");
        });

        // Layout botões
        HBox botoesBox = new HBox(10, btnSalvar, btnLimpar);
        botoesBox.setAlignment(Pos.CENTER);

        // Layout principal
        VBox camposBox = new VBox(10,
                nomeField,
                emailField,
                telefoneField,
                tipoDocumentoBox,
                documentoField
        );
        camposBox.setAlignment(Pos.CENTER);

        this.setPadding(new Insets(25));
        this.setSpacing(18);
        this.setAlignment(Pos.CENTER);
        this.setStyle("-fx-background-color: #F7F9F9; "
                + "-fx-border-color: #BDC3C7; "
                + "-fx-border-radius: 10; "
                + "-fx-background-radius: 10;");

        this.getChildren().addAll(titulo, camposBox, botoesBox, mensagem);
    }
}
