package view;

import staff_paineis.PainelReservas;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import staff_paineis.GerenciarClientesPanel;
import staff_paineis.PainelCheckinCheckout;
import staff_paineis.CadastroPanel;
import staff_paineis.FuncionarioQuartosPanel;
import staff_paineis.PainelDashboard;
import staff_paineis.PainelRelatorios;
import staff_paineis.PainelServicos;
import staff_paineis.PainelTarifas;

public class DashboardFuncionarioView extends Application {

    private BorderPane root;
    private VBox menuLateral;
    private StackPane conteudo;

    @Override
    public void start(Stage primaryStage) {
        root = new BorderPane();
        conteudo = new StackPane();
        conteudo.setAlignment(Pos.CENTER);
        conteudo.setStyle("-fx-background-color: #ecf0f1;");

        menuLateral = criarMenu();
        root.setLeft(menuLateral);

        // Painel inicial
        conteudo.getChildren().add(new PainelDashboard());

        root.setCenter(conteudo);

        Scene scene = new Scene(root, 1200, 700);
        primaryStage.setTitle("Dashboard - Funcionário");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox criarMenu() {
        VBox menu = new VBox(15);
        menu.setPadding(new Insets(20));
        menu.setStyle("-fx-background-color: #34495e;");
        menu.setPrefWidth(240);
        menu.setAlignment(Pos.TOP_CENTER);

        // 🔹 Adiciona o logo do hotel
        ImageView logoView = new ImageView(
            new Image(getClass().getResourceAsStream("/imagens/logo.png"))
        );
        logoView.setFitWidth(100);
        logoView.setFitHeight(100);
        logoView.setPreserveRatio(true);

       

        VBox logoBox = new VBox(logoView);
        logoBox.setAlignment(Pos.CENTER);
        logoBox.setSpacing(10);

        menu.getChildren().add(logoBox);

        String[] opcoes = {
            "📊 Dashboard",
            "👤 Cadastro",
            "🛏 Ver Quartos",
            "👤 Ver Clientes",
            "📅 Gerir Reservas",
            "✅ Check-in / Check-out",
            "💲 Consultar Tarifas",
            "📑 Relatórios Simplificados",
            "🚪 Sair"
        };

        for (String opcao : opcoes) {
            Label btn = criarBotao(opcao);
            btn.setOnMouseClicked(e -> handleMenuClick(opcao));
            menu.getChildren().add(btn);
        }

        return menu;
    }

    private Label criarBotao(String texto) {
        Label btn = new Label(texto);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPadding(new Insets(10));
        btn.setTextFill(Color.WHITE);
        btn.setFont(new Font(16));
        btn.setStyle("-fx-background-color: transparent; -fx-background-radius: 5; -fx-alignment: CENTER_LEFT;");

        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #1abc9c; -fx-background-radius: 5; -fx-alignment: CENTER_LEFT;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-background-radius: 5; -fx-alignment: CENTER_LEFT;"));

        return btn;
    }

    private void handleMenuClick(String opcao) {
        conteudo.getChildren().clear();
        Stage stage = (Stage) root.getScene().getWindow();

        switch (opcao) {
            case "📊 Dashboard" -> conteudo.getChildren().add(new PainelDashboard());
            case "👤 Cadastro" -> conteudo.getChildren().add(new CadastroPanel());
            case "🛏 Ver Quartos" -> conteudo.getChildren().add(new FuncionarioQuartosPanel());
            case "👤 Ver Clientes" -> conteudo.getChildren().add(new GerenciarClientesPanel());
            case "📅 Gerir Reservas" -> conteudo.getChildren().add(new PainelReservas());
            case "✅ Check-in / Check-out" -> conteudo.getChildren().add(new PainelCheckinCheckout());
            case "💲 Consultar Tarifas" -> conteudo.getChildren().add(new PainelTarifas());
            case "📑 Relatórios Simplificados" -> conteudo.getChildren().add(new PainelRelatorios());
            case "🚪 Sair" -> {
                stage.close();
                abrirTelaLogin();
            }
            default -> {
                Label lbl = new Label("Opção em construção: " + opcao);
                lbl.setFont(new Font(18));
                conteudo.getChildren().add(lbl);
            }
        }
    }

    private void abrirTelaLogin() {
        try {
            new LoginView().start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
