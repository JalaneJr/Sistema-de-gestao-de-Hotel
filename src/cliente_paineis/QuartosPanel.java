// QuartosPanel.java
package cliente_paineis;

import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

public class QuartosPanel extends VBox {
    public QuartosPanel() {
        this.getChildren().add(new Label("Painel de Quartos - Conteúdo do banco de dados"));
    }
}
