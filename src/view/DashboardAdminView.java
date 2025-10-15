package view;

import admin_paineis.*;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class DashboardAdminView extends Application {

    private BorderPane root;
    private VBox menuLateral;
    private StackPane conteudo;

    @Override
    public void start(Stage primaryStage) throws SQLException {
        root = new BorderPane();

        // Painel central
        conteudo = new StackPane();
        conteudo.setAlignment(Pos.CENTER);
        conteudo.setStyle("-fx-background-color: #ecf0f1;");

        // Dashboard inicial
        DashboardView dashboard = new DashboardView(); // você pode usar DashboardView se preferir
        conteudo.getChildren().add(dashboard.getRoot());

        // Menu lateral
        menuLateral = criarMenu(primaryStage);

        root.setLeft(menuLateral);
        root.setCenter(conteudo);

        Scene scene = new Scene(root, 1200, 700);
        primaryStage.setTitle("Dashboard - Administrador");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox criarMenu(Stage primaryStage) {
        VBox menu = new VBox(15);
        menu.setPadding(new Insets(20));
        menu.setStyle("-fx-background-color: #2c3e50;");
        menu.setPrefWidth(240);

        // Logo + título
        HBox topo = new HBox(10);
        topo.setAlignment(Pos.CENTER_LEFT);

       ImageView logoView = new ImageView(
            new Image(getClass().getResourceAsStream("/imagens/logo.png"))
        );
        logoView.setFitWidth(100);
        logoView.setFitHeight(100);
        logoView.setPreserveRatio(true);
         VBox logoBox = new VBox(logoView);
        logoBox.setAlignment(Pos.CENTER);
        logoBox.setSpacing(10);
        
        topo.getChildren().addAll(logoView);

        Button btnDashboard = criarBotao("📊 Dashboard");
        Button btnGestaoQuartos = criarBotao("🛏 Gestão de Quartos");
        Button btnGestaoFuncionarios = criarBotao("👨‍💼 Gestão de Funcionários");
        Button btnReservas = criarBotao("📅 Gestão de Reservas");
        Button btnHistorico = criarBotao("📅 Historico de Reservas");
        Button btnTarifas = criarBotao("💲 Tarifas & Promoções");
        Button btnRelatorios = criarBotao("📑 Relatórios Estratégicos");
        Button btnSair = criarBotao("🚪 Sair");

        // Eventos
        btnDashboard.setOnAction(e -> {
            DashboardView view = null;
            try {
                view = new DashboardView();
            } catch (SQLException ex) {
                System.getLogger(DashboardAdminView.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
            conteudo.getChildren().setAll(view.getRoot());
        });
        btnGestaoQuartos.setOnAction(e -> {
            QuartosView view = new QuartosView();
            conteudo.getChildren().setAll(view.getRoot());
        });
        btnGestaoFuncionarios.setOnAction(e -> {
            FuncionariosView view = new FuncionariosView();
            conteudo.getChildren().setAll(view.getRoot());
        });
        btnReservas.setOnAction(e -> {
            ReservasView view = new ReservasView();
            conteudo.getChildren().setAll(view.getRoot());
        });
           btnHistorico.setOnAction(e -> {
             PainelHistoricoHospedagens view = new PainelHistoricoHospedagens();
             conteudo.getChildren().setAll(view.getRoot());
        });
        btnTarifas.setOnAction(e -> {
            TarifasView view = new TarifasView();
            conteudo.getChildren().setAll(view.getRoot());
        });
        btnRelatorios.setOnAction(e -> {
            RelatoriosView view = new RelatoriosView();
            conteudo.getChildren().setAll(view.getRoot());
        });

        btnSair.setOnAction(e -> {
            try {
                Stage loginStage = new Stage();
                new LoginView().start(loginStage);
                primaryStage.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        menu.getChildren().addAll(topo, btnDashboard, btnGestaoQuartos, btnGestaoFuncionarios,
                btnReservas, btnHistorico, btnTarifas, btnRelatorios, btnSair);

        return menu;
    }

    private Button criarBotao(String texto) {
        Button btn = new Button(texto);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14; -fx-background-radius: 5; -fx-alignment: CENTER_LEFT;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #1abc9c; -fx-text-fill: white; -fx-font-size: 14; -fx-background-radius: 5; -fx-alignment: CENTER_LEFT;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14; -fx-background-radius: 5; -fx-alignment: CENTER_LEFT;"));
        return btn;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
