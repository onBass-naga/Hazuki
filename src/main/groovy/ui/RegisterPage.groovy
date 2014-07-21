package ui

import groovy.transform.Canonical
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.control.cell.CheckBoxTableCell
import javafx.scene.control.cell.PropertyValueFactory
import javafx.stage.FileChooser
import javafx.stage.Stage
import service.ConnectionService
import service.MasterService

/**
 * Created by naga on 2014/06/29.
 */
class RegisterPage implements Page {
    @Override
    def getFxml() { 'register.fxml' }
}

public class RegisterController extends Controller implements Initializable {

    @FXML Label messageLabel
    @FXML ProgressIndicator progressIndicator
    @FXML TableView<DataFile> tableView
    @FXML TableColumn<DataFile, String> fileNameColumn
    @FXML TableColumn<DataFile, Boolean> checkedColumn
    @FXML Button fileChooserBtn
    @FXML ChoiceBox<ConnectionModel> connectionChoiceBox

    javafx.collections.ObservableList<DataFile> files = FXCollections.observableArrayList()

    MasterService masterService = new MasterService()
    ConnectionService connectionService = new ConnectionService()

    @FXML
    def void addToTableView(ActionEvent event) {

        final FileChooser fc = new FileChooser();
        fc.setTitle("ファイル選択");
        List<File> selected = fc.showOpenMultipleDialog(new Stage())
        files.addAll(selected.collect() { new DataFile(it) })
    }

    @FXML
    def void importToDB(ActionEvent event) {

        progressIndicator.visible = true
        progressIndicator.indeterminate = true

        def service = [
                createMyTask: {
                    def task = [
                            myCall: {
                                def connection = connectionChoiceBox.selectionModel.selectedItem
                                def selected = files.findAll { it.isChecked() }.collect{ it.file }
                                masterService.importToDB(selected, connection)
                                return null
                            },
                            succeeded: {
                                progressIndicator.setVisible(false)
//                                messageLabel.text = result?.message
//                                messageLabel.visible = true
                            }
                    ] as MyTask
                }] as MyService
        service.start();

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        connectionChoiceBox.setItems(FXCollections.observableArrayList(
                connectionService.load()?.collect {
                    new ConnectionModel(it)
                }))

        tableView.setItems(files);
        tableView.setEditable(true);
        fileNameColumn.setCellValueFactory(
                new PropertyValueFactory<DataFile, String>("fileName"));

        checkedColumn.setCellValueFactory(
                new PropertyValueFactory<DataFile, Boolean>("checked"));
        checkedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkedColumn));
    }
}

@Canonical
def class DataFile {

    def final SimpleStringProperty fileName = new SimpleStringProperty("")
    def final SimpleBooleanProperty checked = new SimpleBooleanProperty(false)
    def final SimpleObjectProperty file = new SimpleObjectProperty(false)

    def DataFile() {
        setfileName("")
        setChecked(false)
    }

    def DataFile(File file) {
        setFileName(file.absolutePath)
        setChecked(false)
        setFile(file)
    }

    public String getFileName() {
        return fileName.get();
    }

    public void setFileName(fileName) {
        this.fileName.set(fileName as String);
    }

    public File getFile() {
        return file.get() as File;
    }

    public void setFile(file) {
        this.file.set(file);
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