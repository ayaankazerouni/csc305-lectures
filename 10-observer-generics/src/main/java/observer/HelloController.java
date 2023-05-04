package observer;

import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.Binding;

import java.util.HashSet;
import java.util.Set;

public class HelloController {
    @FXML
    private TableView<Binding> envTable;

    @FXML
    private TableColumn<Binding, String> nameCol;

    @FXML
    private TableColumn<Binding, Double> valCol;

    private Set<Binding> environment;

    public void initialize() {
       // What if we wanted to "inject" an environment that already
       // exists?
        this.environment = new HashSet<>();

        // Add some bindings to the map
        this.environment.add(new Binding("a", 3.1));
        this.environment.add(new Binding("b", 9.3));
        this.environment.add(new Binding("c", 7e-8));

        // Manually populate the TableView with the map contents
        this.nameCol.setCellValueFactory(e -> new ReadOnlyStringWrapper(e.getValue().getName()));
        this.valCol.setCellValueFactory(e -> new SimpleDoubleProperty(e.getValue().getValue()).asObject());

        this.envTable.getItems().addAll(this.environment);
    }
}