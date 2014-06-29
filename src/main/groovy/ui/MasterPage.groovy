package ui

import com.sun.javafx.css.converters.BooleanConverter
import com.sun.org.apache.xpath.internal.operations.Bool
import groovy.transform.Canonical
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.CheckBoxTableCell
import javafx.scene.control.cell.PropertyValueFactory
import javafx.util.Callback
import javafx.util.converter.BooleanStringConverter
import javafx.util.converter.IntegerStringConverter
import service.MasterService

/**
 * Created by naga on 2014/06/29.
 */
class MasterPage implements Page {
    @Override
    def getFxml() { 'master.fxml' }
}

public class MasterController extends Controller implements Initializable {

    @FXML Label messageLabel
    @FXML ProgressIndicator progressIndicator
    @FXML TableView<Table> tableView;
    @FXML TableColumn<Table, String> tableNameColumn;
    @FXML TableColumn<Table, Boolean> checkedColumn;

    ObservableList<Table> tables = FXCollections.observableArrayList();

    MasterService masterService = new MasterService()

    @FXML
    def void showTableNames(ActionEvent event) {

        progressIndicator.visible = true
        progressIndicator.indeterminate = true

        def service = [
                createMyTask: {
                    def names
                    def task = [
                            myCall: {
                                names = masterService.findTableNames()
                                return null
                            },
                            succeeded: {
                                progressIndicator.setVisible(false)
                                tables.addAll(names.collect { new Table(it) })
//                                messageLabel.text = result?.message
//                                messageLabel.visible = true
                            }
                    ] as MyTask
                }] as MyService
        service.start();
    }

    @FXML
    def void createSql() {
        tables.each { System.out.println(it.toString()) }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tableView.setItems(tables);
        tableView.setEditable(true);
        tableNameColumn.setCellValueFactory(
                new PropertyValueFactory<Table, String>("tableName"));

        checkedColumn.setCellValueFactory(
                new PropertyValueFactory<Table, Boolean>("checked"));
        checkedColumn.setCellFactory(
                new CheckBoxCellFactory());
        checkedColumn.setOnEditStart(new EventHandler<TableColumn.CellEditEvent<Table, Boolean>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Table, Boolean> event) {
                System.out.println("checked")
                Table table = (Table)event.getTableView().getItems().get(event.getTablePosition().getRow());
                System.out.println(event.getNewValue())
                table.setChecked(event.getNewValue());
            }
        });
    }
}

@Canonical
def class Table {

    def final SimpleStringProperty tableName = new SimpleStringProperty("")
    def final SimpleBooleanProperty checked = new SimpleBooleanProperty(false)

    def Table() {
        setTableName("")
        setChecked(false)
    }

    def Table(tableName) {
        setTableName(tableName)
        setChecked(false)
    }

    public String getTableName() {
        return tableName.get();
    }

    public void setTableName(tableName) {
        this.tableName.set(tableName as String);
    }

    public String isChecked() {
        return tableName.get();
    }

    public void setChecked(checked) {
        this.checked.set(checked as Boolean);
    }
}

class CheckBoxCellFactory implements Callback<TableColumn, TableCell> {
    @Override
    public TableCell call(TableColumn tableColumn) {
        return new CheckBoxTableCell()
    }
}