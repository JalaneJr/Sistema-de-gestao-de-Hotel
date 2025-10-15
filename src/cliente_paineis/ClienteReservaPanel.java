package cliente_paineis;

import dao.QuartoDAO;
import dao.ReservaDAO;
import model.Cliente;
import model.Quarto;
import model.Reserva;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.File;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ClienteReservaPanel extends VBox {

    private Cliente clienteLogado;
    private VBox roomsList;
    private DatePicker headerCheckIn;
    private DatePicker headerCheckOut;

    public ClienteReservaPanel(Cliente clienteLogado) {
        this.clienteLogado = clienteLogado;
        this.setSpacing(15);
        this.setPadding(new Insets(20));
        this.setAlignment(Pos.CENTER);
        this.setStyle("-fx-background-color: #f4f6f7;");

        // ====== TOPO ======
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        Label titulo = new Label("Reservar Quarto");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 28));

        HBox datePickers = new HBox(10);
        datePickers.setAlignment(Pos.CENTER);

        headerCheckIn = new DatePicker();
        headerCheckOut = new DatePicker();

        Button checkBtn = new Button("Verificar Disponibilidade");
        checkBtn.setStyle("-fx-background-color: goldenrod; -fx-text-fill: white; -fx-font-weight: bold;");

        datePickers.getChildren().addAll(new Label("Check-in:"), headerCheckIn,
                                         new Label("Check-out:"), headerCheckOut,
                                         checkBtn);

        header.getChildren().addAll(titulo, datePickers);

        // ====== LISTA DE QUARTOS ======
        roomsList = new VBox(20);
        roomsList.setPadding(new Insets(20));
        ScrollPane scrollPane = new ScrollPane(roomsList);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #f9f9f9; -fx-background-insets: 0;");

        this.getChildren().addAll(header, scrollPane);

        // Carregar inicialmente todos os quartos
        loadRooms(null, null);

        checkBtn.setOnAction(e -> loadRooms(headerCheckIn.getValue(), headerCheckOut.getValue()));
    }

    private void loadRooms(LocalDate in, LocalDate out) {
        roomsList.getChildren().clear();
        QuartoDAO qdao = new QuartoDAO();
        ReservaDAO rdao = new ReservaDAO();
        List<Quarto> quartos = qdao.listarTodos();
        boolean imageLeft = true;

        for (Quarto q : quartos) {
            List<Reserva> reservas = rdao.buscarPorQuarto(q.getId());
            boolean disponivel = true;
            if (in != null && out != null) disponivel = isRoomAvailableForPeriod(reservas, in, out);

            HBox card = createRoomCard(q, imageLeft, disponivel, in, out);
            roomsList.getChildren().add(card);
            imageLeft = !imageLeft;
        }
    }

    private HBox createRoomCard(Quarto q, boolean imageLeft, boolean disponivel, LocalDate suggestedIn, LocalDate suggestedOut) {
        HBox card = new HBox();
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPrefHeight(250);
        card.setStyle("-fx-background-color: white; -fx-border-width: 3; -fx-border-radius: 10; -fx-background-radius: 10;");
        String borderColor = disponivel ? "#28a745" : "#e74c3c";
        card.setStyle(card.getStyle() + "-fx-border-color: " + borderColor + ";");

        // ====== imagem ======
        ImageView imageView;
        try {
            File f = new File(q.getImagem());
            if (!f.exists()) f = new File("imagens/fallback.jpg");
            imageView = new ImageView(new Image(f.toURI().toString()));
        } catch (Exception ex) {
            imageView = new ImageView();
        }
        imageView.setFitWidth(400);
        imageView.setFitHeight(250);
        imageView.setPreserveRatio(false);

        // ====== detalhes ======
        VBox textBox = new VBox(8);
        textBox.setPadding(new Insets(10));

        Label type = new Label(q.getTipo());
        type.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        Label price = new Label("Preço / Noite: " + q.getPreco() + " MZN");
        price.setStyle("-fx-text-fill: goldenrod; -fx-font-weight: bold;");

        Label status = new Label("Status: " + q.getStatus());
        status.setTextFill(disponivel ? Color.GREEN : Color.RED);

        Label desc = new Label(q.getDescricao());
        desc.setWrapText(true);

        // Tooltip com ofertas
        Tooltip tooltip = new Tooltip(q.getOfertas() != null ? q.getOfertas() : "Sem ofertas");
        Tooltip.install(desc, tooltip);

        // Serviços rápidos como ícones
        HBox servicesIcons = new HBox(10);
        if (q.getOfertas() != null && q.getOfertas().contains("Breakfast")) {
            ImageView breakfastIcon = new ImageView(new Image("imagens/breakfast.png"));
            breakfastIcon.setFitWidth(24);
            breakfastIcon.setFitHeight(24);
            servicesIcons.getChildren().add(breakfastIcon);
        }
        if (q.getOfertas() != null && q.getOfertas().contains("WiFi")) {
            ImageView wifiIcon = new ImageView(new Image("imagens/wifi.png"));
            wifiIcon.setFitWidth(24);
            wifiIcon.setFitHeight(24);
            servicesIcons.getChildren().add(wifiIcon);
        }
        if (q.getOfertas() != null && q.getOfertas().contains("Parking")) {
            ImageView parkingIcon = new ImageView(new Image("imagens/parking.png"));
            parkingIcon.setFitWidth(24);
            parkingIcon.setFitHeight(24);
            servicesIcons.getChildren().add(parkingIcon);
        }

        Button reservarBtn = new Button(disponivel ? "Reservar" : "Indisponível");
        reservarBtn.setStyle("-fx-background-color: goldenrod; -fx-text-fill: white; -fx-font-weight: bold;");
        reservarBtn.setDisable(!disponivel);

        reservarBtn.setOnAction(e -> openReservationModal(q, suggestedIn, suggestedOut));

        textBox.getChildren().addAll(type, price, status, desc, servicesIcons, reservarBtn);

        if (imageLeft) card.getChildren().addAll(imageView, textBox);
        else card.getChildren().addAll(textBox, imageView);

        return card;
    }

    private void openReservationModal(Quarto quarto, LocalDate suggestedIn, LocalDate suggestedOut) {
        ReservaDAO rdao = new ReservaDAO();
        QuartoDAO qdao = new QuartoDAO();

        Stage modal = new Stage(StageStyle.TRANSPARENT);
        modal.initModality(Modality.APPLICATION_MODAL);

        VBox box = new VBox(12);
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: rgba(255,255,255,0.95); -fx-border-radius: 10; -fx-background-radius: 10;");
        box.setMaxWidth(500);

        Label title = new Label("Reservar: " + quarto.getTipo());
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        DatePicker dpIn = new DatePicker(suggestedIn != null ? suggestedIn : LocalDate.now());
        DatePicker dpOut = new DatePicker(suggestedOut != null ? suggestedOut : dpIn.getValue().plusDays(1));
        HBox dateRow = new HBox(10, new Label("Check-in:"), dpIn, new Label("Check-out:"), dpOut);

        CheckBox svcBreakfast = new CheckBox("Breakfast (+10 MZN)");
        CheckBox svcWifi = new CheckBox("WiFi (+5 MZN)");
        CheckBox svcParking = new CheckBox("Parking (+8 MZN)");
        VBox servicesBox = new VBox(6, new Label("Serviços adicionais:"), svcBreakfast, svcWifi, svcParking);

        Label totalLabel = new Label("Total: 0 MZN");
        totalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        Runnable recalcTotal = () -> {
            LocalDate in = dpIn.getValue();
            LocalDate out = dpOut.getValue();
            if (in == null || out == null || !out.isAfter(in)) {
                totalLabel.setText("Total: 0 MZN");
                return;
            }
            long nights = ChronoUnit.DAYS.between(in, out);
            double base = nights * quarto.getPreco();
            double svc = 0;
            if (svcBreakfast.isSelected()) svc += 10;
            if (svcWifi.isSelected()) svc += 5;
            if (svcParking.isSelected()) svc += 8;
            totalLabel.setText("Total: " + (base + svc) + " MZN");
        };

        dpIn.valueProperty().addListener((obs,o,n)-> recalcTotal.run());
        dpOut.valueProperty().addListener((obs,o,n)-> recalcTotal.run());
        svcBreakfast.selectedProperty().addListener((obs,o,n)-> recalcTotal.run());
        svcWifi.selectedProperty().addListener((obs,o,n)-> recalcTotal.run());
        svcParking.selectedProperty().addListener((obs,o,n)-> recalcTotal.run());
        recalcTotal.run();

        Button reserveBtn = new Button("Confirmar Reserva");
        reserveBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold;");
        Button cancelBtn = new Button("Cancelar");
        cancelBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");

        HBox btnRow = new HBox(10, reserveBtn, cancelBtn);
        btnRow.setAlignment(Pos.CENTER);

        box.getChildren().addAll(title, dateRow, servicesBox, totalLabel, btnRow);

        StackPane root = new StackPane(box);
        root.setStyle("-fx-background-color: rgba(0,0,0,0.45);");
        modal.setScene(new Scene(root));

        cancelBtn.setOnAction(e -> modal.close());

        reserveBtn.setOnAction(e -> {
    LocalDate in = dpIn.getValue();
    LocalDate out = dpOut.getValue();

    if (in == null || out == null || !out.isAfter(in)) {
        showAlert("Erro", "Datas inválidas.");
        return;
    }

    List<Reserva> currentRes = rdao.buscarPorQuarto(quarto.getId());
    if (!isRoomAvailableForPeriod(currentRes, in, out)) {
        showAlert("Indisponível", "Quarto não disponível nesse período.");
        return;
    }

    double total = 0;
    long nights = ChronoUnit.DAYS.between(in, out);
    total += nights * quarto.getPreco();
    if (svcBreakfast.isSelected()) total += 500;
  
    

    // Criando reserva compatível com DAO atualizado
    Reserva reserva = new Reserva();
    reserva.setCliente(clienteLogado);   
    reserva.setQuarto(quarto);           
    reserva.setDataEntrada(in);          
    reserva.setDataSaida(out);           
    reserva.setServicos(
            (svcBreakfast.isSelected() ? "cafe da manha " : "")       
    );
    reserva.setValorTotal(total);
    reserva.setStatus("Ativa");           // status inicial sempre "Ativa"

    try {
        rdao.inserir(reserva);
        qdao.atualizarStatus(quarto.getId(), "Reservado");
        showAlert("Sucesso", "Reserva efetuada com sucesso!");
        modal.close();
        loadRooms(headerCheckIn.getValue(), headerCheckOut.getValue());
    } catch (Exception ex) {
        ex.printStackTrace();
        showAlert("Erro", "Erro ao efetuar reserva.");
    }
});
        modal.showAndWait();
    }

    private boolean isRoomAvailableForPeriod(List<Reserva> reservas, LocalDate in, LocalDate out) {
        for (Reserva r : reservas) {
            LocalDate rIn = r.getCheckin();
            LocalDate rOut = r.getCheckout();
            if (in.isBefore(rOut) && out.isAfter(rIn)) return false;
        }
        return true;
    }

    private void showAlert(String title, String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }
}
