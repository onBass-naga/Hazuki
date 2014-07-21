package ui

import groovy.transform.Canonical
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.control.cell.CheckBoxTableCell
import javafx.scene.control.cell.PropertyValueFactory
import javafx.stage.DirectoryChooser
import javafx.stage.Window
import javafx.util.Callback
import service.ConnectionService
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
    @FXML Button directoryChooserBtn;
    @FXML TextField writeFilePath
    @FXML ChoiceBox<ConnectionModel> connectionChoiceBox;

    ObservableList<Table> tables = FXCollections.observableArrayList();

    MasterService masterService = new MasterService()
    ConnectionService connectionService = new ConnectionService()

    @FXML
    def void showTableNames(ActionEvent event) {

        progressIndicator.visible = true
        progressIndicator.indeterminate = true
        tables.clear()

        def service = [
                createMyTask: {
                    def names
                    def task = [
                            myCall: {
                                def connection = connectionChoiceBox.selectionModel.selectedItem
                                names = masterService.findTableNames(connection)
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
    def void export() {
        def selected = tables.findAll { it.isChecked() }
        def tableNames = selected.collect { it.tableName }
        def directory = writeFilePath.text
        def connection = connectionChoiceBox.selectionModel.selectedItem
        masterService.export(tableNames, directory, connection)
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        connectionChoiceBox.setItems(FXCollections.observableArrayList(
                connectionService.load()?.collect {
                    new ConnectionModel(it)
                }))

        tableView.setItems(tables);
        tableView.setEditable(true);
        tableNameColumn.setCellValueFactory(
                new PropertyValueFactory<Table, String>("tableName"));

        checkedColumn.setCellValueFactory(
                new PropertyValueFactory<Table, Boolean>("checked"));
        checkedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkedColumn));

        directoryChooserBtn.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {
                        final DirectoryChooser directoryChooser =
                                new DirectoryChooser();
                        final File selectedDirectory =
                                directoryChooser.showDialog(new Window());
                        if (selectedDirectory != null) {
                            writeFilePath.text = selectedDirectory.getAbsolutePath();
                        }
                    }
                }
        );
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

    public Boolean isChecked() {
        return checked.get();
    }

    public void setChecked(checked) {
        this.checked.set(checked as Boolean);
    }

    public BooleanProperty checkedProperty() {
        return checked;
    }
}

