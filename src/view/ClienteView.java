package view;

import cliente_paineis.CadastroPanel;
import cliente_paineis.ClienteQuartosPanel;
import cliente_paineis.ClienteReservaPanel;
import cliente_paineis.DashboardClienteView;
import cliente_paineis.HistoricoPanel;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import model.Cliente;
import model.Usuario;

public class ClienteView extends Application {

    private BorderPane root;
    private VBox dashboardContent;
    private Stage primaryStage;
    private Cliente clienteLogado;
    private Usuario usuarioLogado;
    private boolean abrirCadastro;

    // Construtor padrão
    public ClienteView() {}

    // Construtor com usuário e flag para abrir cadastro
    public ClienteView(Usuario usuarioLogado, boolean abrirCadastro) {
        this.usuarioLogado = usuarioLogado;
        this.clienteLogado = usuarioLogado.toCliente(); // retorna null se não for cliente
        this.abrirCadastro = abrirCadastro;
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        if (usuarioLogado == null) {
            System.err.println("⚠️ Usuário não informado! A tela de cliente não pode ser aberta.");
            return;
        }

        root = new BorderPane();

        // --- Header ---
        HBox header = createHeader();
        root.setTop(header);

        // --- Dashboard principal ---
        dashboardContent = new DashboardClienteView(clienteLogado);

        // Se cliente não existir ou flag for verdadeira, abrir cadastro
        if (abrirCadastro || clienteLogado == null) {
            // Passa o Usuario logado para CadastroPanel para preencher automaticamente nome/email
            root.setCenter(new CadastroPanel(usuarioLogado, this::voltarAoDashboard));
        } else {
            root.setCenter(dashboardContent);
        }

        Scene scene = new Scene(root, 1200, 720);
        primaryStage.setTitle("Hotel - Área do Cliente: " + (clienteLogado != null ? clienteLogado.getNome() : "Visitante"));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setSpacing(10);
        header.setPadding(new Insets(12));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: linear-gradient(to right, #2c3e50, #3498db);");

        Label lblUsuario = new Label(clienteLogado != null ? "👤 " + clienteLogado.getNome() : "Cliente");
        lblUsuario.setTextFill(Color.WHITE);
        lblUsuario.setFont(Font.font("System", FontWeight.BOLD, 14));

        Button btnDashboard = new Button("Dashboard");
        Button btnCadastro = new Button("Cadastro");
        Button btnQuartos = new Button("Quartos");
        Button btnReservas = new Button("Reservas");
        Button btnHistorico = new Button("Histórico");
        Button btnSair = new Button("Sair");

        String buttonStyle = "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;";
        String buttonHoverStyle = "-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-background-radius: 4;";

        for (Button b : new Button[]{btnDashboard, btnCadastro, btnQuartos, btnReservas, btnHistorico, btnSair}) {
            b.setStyle(buttonStyle);
            b.setOnMouseEntered(e -> b.setStyle(buttonHoverStyle));
            b.setOnMouseExited(e -> b.setStyle(buttonStyle));
        }

        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);
        header.getChildren().addAll(btnDashboard, btnCadastro, btnQuartos, btnReservas, btnHistorico, espaco, lblUsuario, btnSair);

        setupMenuActions(btnDashboard, btnCadastro, btnQuartos, btnReservas, btnHistorico, btnSair);

        return header;
    }

    private void setupMenuActions(Button btnDashboard, Button btnCadastro, Button btnQuartos,
                                  Button btnReservas, Button btnHistorico, Button btnSair) {
        btnDashboard.setOnAction(e -> root.setCenter(new DashboardClienteView(clienteLogado)));
        btnCadastro.setOnAction(e -> root.setCenter(new CadastroPanel(usuarioLogado, this::voltarAoDashboard)));
        btnQuartos.setOnAction(e -> root.setCenter(new ClienteQuartosPanel(clienteLogado)));
        btnReservas.setOnAction(e -> root.setCenter(new ClienteReservaPanel(clienteLogado)));
        btnHistorico.setOnAction(e -> root.setCenter(new HistoricoPanel(clienteLogado)));

        btnSair.setOnAction(e -> {
            try {
                new LoginView().start(new Stage());
                primaryStage.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    // Método chamado após o cadastro para atualizar clienteLogado e voltar ao dashboard
    public void voltarAoDashboard(Cliente clienteAtualizado) {
        this.clienteLogado = clienteAtualizado;
        root.setCenter(new DashboardClienteView(clienteLogado));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
