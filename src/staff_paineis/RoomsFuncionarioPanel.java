package staff_paineis;

import dao.QuartoDAO;
import dao.ReservaDAO;
import dao.ClienteDAO;
import model.Quarto;
import model.Reserva;
import model.Cliente;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

public class RoomsFuncionarioPanel extends BorderPane {

    private VBox roomsList;
    private DatePicker headerCheckIn;
    private DatePicker headerCheckOut;

    public RoomsFuncionarioPanel() {
        // ====== TOPO ======
        VBox header = new VBox();
        header.setStyle("-fx-background-color: #3e3e3e;");
        header.setPadding(new Insets(20));
        header.setSpacing(10);

        Label hotelName = new Label("🏨 Gerenciamento de Quartos - Funcionário");
        hotelName.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        hotelName.setStyle("-fx-text-fill: white;");

        HBox searchBar = new HBox(10);
        searchBar.setAlignment(Pos.CENTER);

        headerCheckIn = new DatePicker();
        headerCheckOut = new DatePicker();

        Button checkAvailability = new Button("Verificar Disponibilidade");
        checkAvailability.setStyle("-fx-background-color: goldenrod; -fx-text-fill: white;");

        Button refreshBtn = new Button("🔄 Atualizar Quartos");
        refreshBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold;");

        searchBar.getChildren().addAll(
                new Label("Check In:"), headerCheckIn,
                new Label("Check Out:"), headerCheckOut,
                checkAvailability, refreshBtn
        );

        header.getChildren().addAll(hotelName, searchBar);

        // ====== LISTA DE QUARTOS ======
        roomsList = new VBox(20);
        roomsList.setPadding(new Insets(20));

        ScrollPane scrollPane = new ScrollPane(roomsList);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #f9f9f9;");

        // ====== RODAPÉ ======
        HBox footer = new HBox();
        footer.setPadding(new Insets(20));
        footer.setStyle("-fx-background-color: #1c1c1c;");
        footer.setAlignment(Pos.CENTER);

        Label footerText = new Label("© 2025 LUXORY Hotel - Painel do Funcionário");
        footerText.setStyle("-fx-text-fill: white;");
        footer.getChildren().add(footerText);

        // ====== ROOT ======
        this.setTop(header);
        this.setCenter(scrollPane);
        this.setBottom(footer);

        // carregar inicialmente
        loadRooms();

        refreshBtn.setOnAction(e -> loadRooms());
        checkAvailability.setOnAction(e -> checkAvailabilityAction());
    }

    // carrega todos os quartos
    private void loadRooms() {
        roomsList.getChildren().clear();
        QuartoDAO qdao = new QuartoDAO();
        List<Quarto> quartos = qdao.listarTodos();
        boolean imageLeft = true;
        for (Quarto q : quartos) {
            HBox card = createRoomCard(q, imageLeft);
            roomsList.getChildren().add(card);
            imageLeft = !imageLeft;
        }
    }

    // ação do botão verificar disponibilidade do topo
    private void checkAvailabilityAction() {
        LocalDate in = headerCheckIn.getValue();
        LocalDate out = headerCheckOut.getValue();

        if (in == null || out == null || out.isBefore(in)) {
            showAlert("Datas Inválidas", "Por favor selecione um intervalo de datas válido (check-out após check-in).");
            return;
        }

        roomsList.getChildren().clear();
        QuartoDAO qdao = new QuartoDAO();
        ReservaDAO rdao = new ReservaDAO();
        List<Quarto> quartos = qdao.listarTodos();
        boolean imageLeft = true;

        for (Quarto q : quartos) {
            List<Reserva> reservas = rdao.buscarPorQuarto(q.getId());
            boolean disponivel = isRoomAvailableForPeriod(reservas, in, out);
            HBox card = createRoomCard(q, imageLeft);

            String borderColor = borderColorForStatus(q.getStatus());
            if (!disponivel) borderColor = "red";
            card.setStyle("-fx-background-color: white; -fx-border-color: " + borderColor + "; -fx-border-width: 3;");

            roomsList.getChildren().add(card);
            imageLeft = !imageLeft;
        }
    }

    // cria o card do quarto
    private HBox createRoomCard(Quarto q, boolean imageLeft) {
        HBox card = new HBox();
        card.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 2;");
        card.setPrefHeight(350);
        card.setAlignment(Pos.CENTER_LEFT);

        ImageView imageView;
        try {
            File f = new File(q.getImagem());
            if (f.exists()) imageView = new ImageView(new Image(f.toURI().toString()));
            else imageView = new ImageView(new Image(getClass().getResourceAsStream("/imagens/quarto 4.jpg")));
        } catch (Exception ex) {
            imageView = new ImageView(new Image(getClass().getResourceAsStream("/imagens/quarto 4.jpg")));
        }
        imageView.setFitHeight(350);
        imageView.setFitWidth(600);
        imageView.setPreserveRatio(false);

        VBox textBox = new VBox(8);
        textBox.setAlignment(Pos.TOP_LEFT);
        textBox.setPadding(new Insets(20));
        textBox.setPrefWidth(600);

        Label title = new Label(q.getTipo());
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Label price = new Label("Preço: " + q.getPreco() + " MZN / Noite");
        price.setStyle("-fx-text-fill: goldenrod; -fx-font-size: 16px; -fx-font-weight: bold;");

        Label status = new Label("Status: " + q.getStatus());
        Label description = new Label(q.getDescricao());
        Label offers = new Label(q.getOfertas());

        HBox buttons = new HBox(10);
        Button reservarBtn = new Button("Reservar Quarto");
        reservarBtn.setStyle("-fx-background-color: goldenrod; -fx-text-fill: white; -fx-font-weight: bold;");
        buttons.getChildren().addAll(reservarBtn);

        textBox.getChildren().addAll(title, price, status, description, offers, buttons);

        if (imageLeft) card.getChildren().addAll(imageView, textBox);
        else card.getChildren().addAll(textBox, imageView);

        reservarBtn.setOnAction(e -> openReservationModal(q));

        return card;
    }

    // modal de reserva
    private void openReservationModal(Quarto quarto) {
        Stage modal = new Stage(StageStyle.TRANSPARENT);
        modal.initModality(Modality.APPLICATION_MODAL);

        VBox box = new VBox(12);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: rgba(255,255,255,0.95); -fx-background-radius: 10;");
        box.setMaxWidth(600);

        Label title = new Label("Reservar Quarto: " + quarto.getTipo());
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        DatePicker dpIn = new DatePicker(LocalDate.now());
        DatePicker dpOut = new DatePicker(LocalDate.now().plusDays(1));

        HBox dateRow = new HBox(10, new Label("Check-in:"), dpIn, new Label("Check-out:"), dpOut);

        // 🔹 Combobox de clientes cadastrados
        ComboBox<Cliente> cbClientes = new ComboBox<>();
        cbClientes.setPrefWidth(300);
        cbClientes.setPromptText("Selecione o cliente...");

        try {
            ClienteDAO cdao = new ClienteDAO();
            List<Cliente> clientes = cdao.listarTodos();
            cbClientes.getItems().addAll(clientes);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        cbClientes.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Cliente c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? null : c.getNome() + " (ID: " + c.getId() + ")");
            }
        });
        cbClientes.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Cliente c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? null : c.getNome() + " (ID: " + c.getId() + ")");
            }
        });

        VBox clientBox = new VBox(5, new Label("Cliente:"), cbClientes);

        Label totalLabel = new Label("Total: 0.00 MZN");
        totalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        Runnable recalcTotal = () -> {
            try {
                LocalDate in = dpIn.getValue();
                LocalDate out = dpOut.getValue();
                if (in == null || out == null || !out.isAfter(in)) {
                    totalLabel.setText("Total: 0.00 MZN");
                    return;
                }
                long nights = ChronoUnit.DAYS.between(in, out);
                double total = nights * quarto.getPreco();
                totalLabel.setText(String.format("Total: %.2f MZN (%d noites)", total, nights));
            } catch (Exception ex) {
                totalLabel.setText("Total: 0.00 MZN");
            }
        };
        dpIn.valueProperty().addListener((obs, o, n) -> recalcTotal.run());
        dpOut.valueProperty().addListener((obs, o, n) -> recalcTotal.run());
        recalcTotal.run();

        HBox btns = new HBox(10);
        btns.setAlignment(Pos.CENTER_RIGHT);
        Button reservar = new Button("Confirmar Reserva");
        Button cancelar = new Button("Cancelar");
        reservar.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-weight: bold;");
        cancelar.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white;");
        btns.getChildren().addAll(cancelar, reservar);

        box.getChildren().addAll(title, dateRow, clientBox, totalLabel, btns);

        cancelar.setOnAction(e -> modal.close());

        reservar.setOnAction(e -> {
            Cliente cliente = cbClientes.getValue();
            if (cliente == null) {
                showAlert("Erro", "Selecione um cliente antes de continuar.");
                return;
            }

            LocalDate in = dpIn.getValue();
            LocalDate out = dpOut.getValue();
            if (in == null || out == null || !out.isAfter(in)) {
                showAlert("Erro", "Selecione datas válidas de check-in e check-out.");
                return;
            }

            try {
                ReservaDAO rdao = new ReservaDAO();
                QuartoDAO qdao = new QuartoDAO();

                List<Reserva> reservas = rdao.buscarPorQuarto(quarto.getId());
                if (!isRoomAvailableForPeriod(reservas, in, out)) {
                    showAlert("Indisponível", "O quarto já está reservado nesse período.");
                    return;
                }

                long nights = ChronoUnit.DAYS.between(in, out);
                double total = nights * quarto.getPreco();

                Reserva reserva = new Reserva(cliente, quarto, in, out, total);
                rdao.inserir(reserva);
                qdao.atualizarStatus(quarto.getId(), "Reservado");

                showAlert("Sucesso", "Reserva efetuada com sucesso!");
                modal.close();
                loadRooms();
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Erro", "Erro ao efetuar a reserva.");
            }
        });

        StackPane root = new StackPane(box);
        root.setStyle("-fx-background-color: rgba(0,0,0,0.45);");
        Scene scene = new Scene(root);
        modal.setScene(scene);
        modal.showAndWait();
    }

    // utilitários
    private boolean isRoomAvailableForPeriod(List<Reserva> reservas, LocalDate in, LocalDate out) {
        if (in == null || out == null) return false;
        for (Reserva r : reservas) {
            LocalDate rIn = r.getDataEntrada();
            LocalDate rOut = r.getDataSaida();
            if (in.isBefore(rOut) && out.isAfter(rIn)) return false;
        }
        return true;
    }

    private String borderColorForStatus(String status) {
        if (status == null) return "#ddd";
        String s = status.toLowerCase();
        if (s.contains("livre")) return "green";
        if (s.contains("ocup") || s.contains("reserv")) return "red";
        if (s.contains("manutenc") || s.contains("manutenção")) return "yellow";
        return "#ddd";
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
