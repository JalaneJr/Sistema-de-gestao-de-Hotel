package view;

import cliente_paineis.CadastroPanel;
import dao.UsuarioDAO;
import model.Cliente;
import model.Usuario;
import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LoginView extends Application {

    private Usuario user;

    private StackPane root;
    private VBox painelLogin;
    private VBox painelRegistro;

    @Override
    public void start(Stage primaryStage) {

        root = new StackPane();
        trocarFundo("3.jpg");

        Label lblHotel = new Label("HOTEL LUXURY");
        lblHotel.setFont(Font.font("Arial", 36));
        lblHotel.setTextFill(Color.WHITE);
        StackPane.setAlignment(lblHotel, Pos.TOP_CENTER);
        StackPane.setMargin(lblHotel, new Insets(30, 0, 0, 0));

        painelLogin = criarPainelLogin(primaryStage);
        painelRegistro = criarPainelRegistro();
        painelRegistro.setVisible(false);

        root.getChildren().addAll(painelLogin, painelRegistro, lblHotel);

        Scene scene = new Scene(root, 1000, 600);
        primaryStage.setTitle("Sistema de Gestão Hoteleira - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void trocarFundo(String imagem) {
        BackgroundImage backgroundImage = new BackgroundImage(
                new Image("file:src/imagens/" + imagem, 1000, 600, false, true),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                BackgroundSize.DEFAULT
        );
        root.setBackground(new Background(backgroundImage));
    }

    private VBox criarPainelLogin(Stage primaryStage) {
        VBox painel = new VBox(15);
        painel.setAlignment(Pos.CENTER);
        painel.setPadding(new Insets(20));
        painel.setMaxWidth(300);
        painel.setMaxHeight(250);
        painel.setBackground(new Background(new BackgroundFill(
                Color.rgb(0, 0, 0, 0.8),
                new CornerRadii(10),
                Insets.EMPTY
        )));

        Label lblTitulo = new Label("Login");
        lblTitulo.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");

        TextField txtEmail = new TextField();
        txtEmail.setPromptText("Email"); 
        
        PasswordField txtSenha = new PasswordField();
        txtSenha.setPromptText("Senha");

        Label lblErro = new Label();
        lblErro.setTextFill(Color.RED);
        lblErro.setStyle("-fx-font-size: 15px");

        Button btnLogin = new Button("Entrar");
        btnLogin.setDefaultButton(true);
        btnLogin.setStyle("-fx-font-size: 14px;-fx-background-color: #4CAF50; -fx-text-fill: white;");

        txtEmail.focusedProperty().addListener((obs, oldV, newV) -> {
            if (newV) txtEmail.setStyle("");
        });
        txtSenha.focusedProperty().addListener((obs, oldV, newV) -> {
            if (newV) txtSenha.setStyle("");
        });

        btnLogin.setOnAction(e -> {
            lblErro.setText("");
            String email = txtEmail.getText().trim();
            String senha = txtSenha.getText().trim();

            boolean erro = false;
            if (email.isEmpty()) {
                txtEmail.setStyle("-fx-border-color: red;");
                lblErro.setText("Informe o email.");
                erro = true;
            } else if (senha.isEmpty()) {
                txtSenha.setStyle("-fx-border-color: red;");
                lblErro.setText("Informe a senha.");
                erro = true;
            }
            if (erro) return;

            UsuarioDAO dao = new UsuarioDAO();
            Usuario user = dao.autenticar(email, senha);

            if (user != null) {
                txtEmail.clear();
                txtSenha.clear();
                mostrarPainelSucesso(primaryStage, user);
            } else {
                txtEmail.setStyle("-fx-border-color: red;");
                txtSenha.setStyle("-fx-border-color: red;");
                lblErro.setTextFill(Color.RED);
                lblErro.setText("Email ou senha incorretos!");
                lblErro.setStyle("-fx-font-size: 15px");
            }
        });

        Label lblConta = new Label("Criar conta (apenas clientes)");
        lblConta.setStyle("-fx-font-size: 15px ;-fx-text-fill: lightblue; -fx-underline: true; -fx-cursor: hand;");
        lblConta.setOnMouseClicked(e -> mostrarRegistro());

        Label lblRecuperar = new Label("Esqueceu a senha?");
        lblRecuperar.setStyle("-fx-font-size: 15px; -fx-text-fill: yellow; -fx-underline: true; -fx-cursor: hand;");

        // 🔹 Recuperação de senha com pergunta de segurança
        lblRecuperar.setOnMouseClicked(e -> {
            TextInputDialog dialogEmail = new TextInputDialog();
            dialogEmail.setHeaderText("Recuperação de Senha");
            dialogEmail.setContentText("Digite seu email:");

            dialogEmail.showAndWait().ifPresent(email -> {
                UsuarioDAO dao = new UsuarioDAO();
                String pergunta = dao.recuperarPerguntaSeguranca(email);

                if (pergunta != null) {
                    // Pergunta de segurança com alerta personalizado
                    TextInputDialog dialogResposta = new TextInputDialog();
                    dialogResposta.setHeaderText("Pergunta de Segurança");
                    dialogResposta.setContentText(pergunta);

                    dialogResposta.showAndWait().ifPresent(resposta -> {
                        boolean correta = dao.verificarRespostaSeguranca(email, resposta);
                        if (correta) {
                            String senha = dao.recuperarSenha(email);
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Senha Recuperada");
                            alert.setHeaderText("Sua senha é:");
                            alert.setContentText(senha);
                            alert.showAndWait();
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Erro");
                            alert.setHeaderText("Resposta incorreta!");
                            alert.setContentText("A resposta da pergunta de segurança não confere.");
                            alert.showAndWait();
                        }
                    });

                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erro");
                    alert.setHeaderText("Email não encontrado!");
                    alert.setContentText("O email digitado não está cadastrado.");
                    alert.showAndWait();
                }
            });
        });

        painel.getChildren().addAll(lblTitulo, txtEmail, txtSenha, btnLogin, lblErro, lblConta, lblRecuperar);
        return painel;
    }

    private VBox criarPainelRegistro() {
        VBox painel = new VBox(15);
        painel.setAlignment(Pos.CENTER);
        painel.setPadding(new Insets(20));
        painel.setMaxWidth(300);
        painel.setMaxHeight(350);
        painel.setBackground(new Background(new BackgroundFill(
                Color.rgb(0, 0, 0, 0.4),
                new CornerRadii(10),
                Insets.EMPTY
        )));

        Label lblTitulo = new Label("Criar Conta - Cliente");
        lblTitulo.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");

        TextField txtNome = new TextField();
        txtNome.setPromptText("Nome completo");

        TextField txtEmail = new TextField();
        txtEmail.setPromptText("Email (deve terminar com @gmail.com)");

        PasswordField txtSenha = new PasswordField();
        txtSenha.setPromptText("Senha (mín. 6 caracteres)");

        // 🔹 ComboBox com perguntas de segurança
        ComboBox<String> cbPerguntaSeguranca = new ComboBox<>();
        cbPerguntaSeguranca.getItems().addAll(
                "Qual o nome da sua mãe?",
                "Qual seu primeiro animal de estimação?",
                "Qual cidade você nasceu?",
                "Qual seu filme favorito?"
        );
        cbPerguntaSeguranca.setPromptText("Selecione uma pergunta de segurança");

        TextField txtRespostaSeguranca = new TextField();
        txtRespostaSeguranca.setPromptText("Resposta da pergunta de segurança");

        Label lblErro = new Label();
        lblErro.setTextFill(Color.RED);

        Button btnRegistrar = new Button("Registrar");
        btnRegistrar.setStyle("-fx-font-size: 15px; -fx-background-color: #2196F3; -fx-text-fill: white;");

        txtNome.focusedProperty().addListener((obs, oldV, newV) -> { if (newV) txtNome.setStyle(""); });
        txtEmail.focusedProperty().addListener((obs, oldV, newV) -> { if (newV) txtEmail.setStyle(""); });
        txtSenha.focusedProperty().addListener((obs, oldV, newV) -> { if (newV) txtSenha.setStyle(""); });
        txtRespostaSeguranca.focusedProperty().addListener((obs, oldV, newV) -> { if (newV) txtRespostaSeguranca.setStyle(""); });

        btnRegistrar.setOnAction(e -> {
            lblErro.setText("");
            String nome = txtNome.getText().trim();
            String email = txtEmail.getText().trim();
            String senha = txtSenha.getText().trim();
            String pergunta = cbPerguntaSeguranca.getValue();
            String resposta = txtRespostaSeguranca.getText().trim();

            boolean erro = false;
            if (nome.isEmpty()) { txtNome.setStyle("-fx-border-color: red;"); lblErro.setText("Informe o nome completo."); erro = true; }
            else if (email.isEmpty()) { txtEmail.setStyle("-fx-border-color: red;"); lblErro.setText("Informe o email."); erro = true; }
            else if (!email.endsWith("@gmail.com")) { txtEmail.setStyle("-fx-border-color: red;"); lblErro.setText("Email deve terminar com @gmail.com."); erro = true; }
            else if (senha.length() < 6) { txtSenha.setStyle("-fx-border-color: red;"); lblErro.setText("A senha deve ter pelo menos 6 caracteres."); erro = true; }
            else if (pergunta == null || pergunta.isEmpty()) { cbPerguntaSeguranca.setStyle("-fx-border-color: red;"); lblErro.setText("Selecione uma pergunta de segurança."); erro = true; }
            else if (resposta.isEmpty()) { txtRespostaSeguranca.setStyle("-fx-border-color: red;"); lblErro.setText("Informe a resposta da pergunta de segurança."); erro = true; }
            if (erro) return;

            Usuario u = new Usuario(0, nome, "", "", email, "", senha, "CLIENTE", pergunta, resposta);
            UsuarioDAO dao = new UsuarioDAO();

            if (dao.registrar(u)) {
                lblErro.setTextFill(Color.LIGHTGREEN);
                lblErro.setText("Usuário registrado com sucesso!");
                txtNome.clear();
                txtEmail.clear();
                txtSenha.clear();
                cbPerguntaSeguranca.setValue(null);
                txtRespostaSeguranca.clear();

                PauseTransition pausa = new PauseTransition(Duration.seconds(4));
                pausa.setOnFinished(ev -> mostrarLogin());
                pausa.play();

            } else {
                lblErro.setTextFill(Color.RED);
                lblErro.setText("Erro ao registrar. Tente novamente.");
            }
        });

        Label lblLogin = new Label("Já tem conta? Fazer login");
        lblLogin.setStyle("-fx-font-size: 15px; -fx-text-fill: lightblue; -fx-underline: true; -fx-cursor: hand;");
        lblLogin.setOnMouseClicked(e -> mostrarLogin());

        painel.getChildren().addAll(
                lblTitulo,
                txtNome,
                txtEmail,
                txtSenha,
                cbPerguntaSeguranca,
                txtRespostaSeguranca,
                btnRegistrar,
                lblErro,
                lblLogin
        );
        return painel;
    }

    private void mostrarPainelSucesso(Stage stage, Usuario user) {
        VBox painelSucesso = new VBox(12);
        painelSucesso.setAlignment(Pos.CENTER);
        painelSucesso.setPadding(new Insets(35));
        painelSucesso.setMaxWidth(350);
        painelSucesso.setMaxHeight(300);
        painelSucesso.setOpacity(10);
        painelSucesso.setScaleX(0.8);
        painelSucesso.setScaleY(0.8);

        BackgroundFill bgInicial = new BackgroundFill(Color.rgb(0, 170, 60, 0.9), new CornerRadii(20), Insets.EMPTY);
        painelSucesso.setBackground(new Background(bgInicial));

        Circle circle = new Circle(45, Color.TRANSPARENT);
        circle.setStroke(Color.WHITE);
        circle.setStrokeWidth(4);

        Label lblCheck = new Label("✔");
        lblCheck.setTextFill(Color.WHITE);
        lblCheck.setFont(Font.font("Arial", 45));

        StackPane icone = new StackPane(circle, lblCheck);
        icone.setScaleX(0);
        icone.setScaleY(0);

        Label lblMsg = new Label("Login efetuado com sucesso!");
        lblMsg.setTextFill(Color.WHITE);
        lblMsg.setFont(Font.font("Arial", 20));

        Label lblNome = new Label("Bem-vindo(a), " + user.getNome() + "!");
        lblNome.setTextFill(Color.WHITE);
        lblNome.setFont(Font.font("Arial Rounded MT Bold", 16));
        lblNome.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 3, 0.3, 0, 1);");

        Label lblSub = new Label("Redirecionando para o seu painel...");
        lblSub.setTextFill(Color.WHITE);
        lblSub.setOpacity(0.8);

        painelSucesso.getChildren().addAll(icone, lblMsg, lblNome, lblSub);
        root.getChildren().add(painelSucesso);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.7), painelSucesso);
        fadeIn.setFromValue(0); fadeIn.setToValue(1);

        ScaleTransition scaleIn = new ScaleTransition(Duration.seconds(0.7), painelSucesso);
        scaleIn.setFromX(0.8); scaleIn.setFromY(0.8);
        scaleIn.setToX(1); scaleIn.setToY(1);
        scaleIn.setInterpolator(Interpolator.EASE_BOTH);

        ScaleTransition iconPop = new ScaleTransition(Duration.seconds(0.5), icone);
        iconPop.setFromX(0); iconPop.setFromY(0); iconPop.setToX(1); iconPop.setToY(1);
        iconPop.setInterpolator(Interpolator.EASE_OUT);

        SequentialTransition seq = new SequentialTransition(new ParallelTransition(fadeIn, scaleIn), iconPop);
        seq.play();

        Timeline corFundo = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(painelSucesso.backgroundProperty(),
                        new Background(new BackgroundFill(Color.rgb(0, 170, 60, 0.9), new CornerRadii(20), Insets.EMPTY)))),
                new KeyFrame(Duration.seconds(2.5), new KeyValue(painelSucesso.backgroundProperty(),
                        new Background(new BackgroundFill(Color.rgb(0, 140, 255, 0.9), new CornerRadii(20), Insets.EMPTY))))
        );
        corFundo.setAutoReverse(true);
        corFundo.setCycleCount(Timeline.INDEFINITE);
        corFundo.play();

        Glow glow = new Glow(0.3);
        lblNome.setEffect(glow);

        Timeline brilho = new Timeline(
                new KeyFrame(Duration.seconds(0), new KeyValue(glow.levelProperty(), 0.3)),
                new KeyFrame(Duration.seconds(0.8), new KeyValue(glow.levelProperty(), 0.8)),
                new KeyFrame(Duration.seconds(1.6), new KeyValue(glow.levelProperty(), 0.3))
        );
        brilho.setCycleCount(Timeline.INDEFINITE);
        brilho.play();

        PauseTransition espera = new PauseTransition(Duration.seconds(3));
        espera.setOnFinished(ev -> {
            corFundo.stop();
            brilho.stop();
            root.getChildren().remove(painelSucesso);
            abrirTelaCorrespondente(stage, user);
        });
        espera.play();
    }

    private void abrirTelaCorrespondente(Stage stage, Usuario user) {
        try {
            if (user.getEmail().equalsIgnoreCase(".admin@gmail.com")) {
                new DashboardAdminView().start(new Stage());
            } else if (user.getEmail().endsWith(".staff@gmail.com")) {
                new DashboardFuncionarioView().start(new Stage());
            } else if (user.getEmail().endsWith("@gmail.com")) {
                boolean abrirCadastro = (user.toCliente() == null);
                new ClienteView(user, abrirCadastro).start(new Stage());
            }
            stage.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void mostrarRegistro() {
        trocarFundo("6.jpg");
        painelRegistro.setTranslateX(1000);
        painelRegistro.setVisible(true);

        TranslateTransition slideIn = new TranslateTransition(Duration.seconds(0.7), painelRegistro);
        slideIn.setToX(0);
        FadeTransition fade = new FadeTransition(Duration.seconds(0.7), painelRegistro);
        fade.setFromValue(0); fade.setToValue(1);
        slideIn.play(); fade.play();

        painelLogin.setVisible(false);
    }

    private void mostrarLogin() {
        trocarFundo("3.jpg");
        painelLogin.setTranslateX(-1000);
        painelLogin.setVisible(true);

        TranslateTransition slideIn = new TranslateTransition(Duration.seconds(1), painelLogin);
        slideIn.setToX(0);
        FadeTransition fade = new FadeTransition(Duration.seconds(1), painelLogin);
        fade.setFromValue(0); fade.setToValue(1);
        slideIn.play(); fade.play();

        painelRegistro.setVisible(false);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
