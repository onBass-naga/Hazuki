package ui

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList;
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.TextField
import javafx.scene.text.Text
import javafx.util.Callback
import service.ConnectCondition
import service.ConnectionService
import service.Result

/**
 * Created by naga on 2014/06/29.
 */
class ConnectionPage implements Page {
    @Override
    def getFxml() { 'connection.fxml' }
}

public class ConnectionController extends Controller implements Initializable {

    static final Integer NOT_YET_STORED = -1

    @FXML ListView<ConnectionModel> nameListView

    @FXML Label messageLabel
    @FXML TextField nameField
    @FXML TextField urlField
    @FXML TextField userField
    @FXML TextField passwordField
    @FXML TextField driverField
    @FXML ProgressIndicator progressIndicator

    ObservableList<ConnectionModel> connections = FXCollections.observableArrayList();
    Integer current;

    ConnectionService connectionService = new ConnectionService()


    @FXML
    def void create(ActionEvent event) {
        clear()
        current = NOT_YET_STORED
    }

    @FXML
    def void testConnection(ActionEvent event) {

        progressIndicator.visible = true
        progressIndicator.indeterminate = true

        def service = [
                createMyTask: {
                    Result result
                    def task = [
                            myCall: {
                                def condition = new ConnectCondition(
                                        url: urlField.text,
                                        user: userField.text,
                                        password: passwordField.text,
                                        driver: driverField.text)
                                result = connectionService.testConnection(condition)
                                return null
                            },
                            succeeded: {
                                progressIndicator.setVisible(false)
                                messageLabel.text = result?.message
                                messageLabel.visible = true
                            }
                    ] as MyTask
                }] as MyService
        service.start();
    }

    @FXML
    def void save(ActionEvent event) {

        def condition = new ConnectCondition(
                name: nameField.text,
                url: urlField.text,
                user: userField.text,
                password: passwordField.text,
                driver: driverField.text)

        if (current == NOT_YET_STORED) {
            connections.add(new ConnectionModel(condition))
        } else {
            def index = current
            connections.remove(current)
            connections.add(index, new ConnectionModel(condition))

            nameListView.scrollTo(index);
            nameListView.getSelectionModel().select(index);
        }

        connectionService.saveCondition(connections)
    }

    @FXML
    def void delete(ActionEvent event) {

        connections.remove(current)
        nameListView.scrollTo(0);
        nameListView.getSelectionModel().select(0);
        connectionService.saveCondition(connections)
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        nameListView.setCellFactory(new Callback<ListView<ConnectionModel>, ListCell<ConnectionModel>>() {
            @Override
            public ListCell<ConnectionModel> call(ListView<ConnectionModel> listView) {
                return new ConnectionCell();
            }
        });

        nameListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ConnectionModel>() {
            @Override
            public void changed(ObservableValue<? extends ConnectionModel> observable, ConnectionModel oldValue, ConnectionModel newValue) {
                current = connections.indexOf(newValue)
                rewrite(newValue)
            }
        });

        connections.addAll(connectionService.load()?.collect {
            new ConnectionModel(it)
        })

        nameListView.setItems(connections)
    }

    def rewrite(newValue) {
        nameField.text = newValue?.name
        urlField.text = newValue?.url
        userField.text = newValue?.user
        passwordField.text = newValue?.password
        driverField.text = newValue?.driver
    }

    def clear() {
        nameField.text = ''
        urlField.text = ''
        userField.text = ''
        passwordField.text = ''
        driverField.text = ''
    }
}

public class ConnectionModel {

    private StringProperty name = new SimpleStringProperty();
    private StringProperty url = new SimpleStringProperty();
    private StringProperty user = new SimpleStringProperty();
    private StringProperty password = new SimpleStringProperty();
    private StringProperty driver = new SimpleStringProperty();

    public ConnectionModel(name, url, user, password, driver) {
        this.name.set(name as String)
        this.url.set(url as String)
        this.user.set(user as String)
        this.password.set(password as String)
        this.driver.set(driver as String)
    }

    public ConnectionModel(ConnectCondition conn) {
        this.name.set(conn.name as String)
        this.url.set(conn.url as String)
        this.user.set(conn.user as String)
        this.password.set(conn.password as String)
        this.driver.set(conn.driver as String)
    }

    public String getName() {
        return name.getValueSafe()
    }

    public String getUrl() {
        return url.getValueSafe()
    }

    public String getUser() {
        return user.getValueSafe()
    }

    public String getPassword() {
        return password.getValueSafe()
    }

    public String getDriver() {
        return driver.getValueSafe()
    }

    public StringProperty nameProperty() {
        return name
    }

    public StringProperty urlProperty() {
        return url
    }

    public StringProperty userProperty() {
        return user
    }

    public StringProperty passwordProperty() {
        return password
    }

    public StringProperty driverProperty() {
        return driver
    }

    @Override
    public String toString() {
        return name.getValue()
    }
}

def class ConnectionCell extends ListCell<ConnectionModel> {


    def Text nameText;

    def boolean bound

    public ConnectionCell() {
        initComponent();
    }

    private void initComponent() {
        nameText = new Text();
    }

    @Override
    protected void updateItem(ConnectionModel model, boolean empty) {
        super.updateItem(model, empty);
        if (!bound) {
            nameText.wrappingWidthProperty().bind(getListView().widthProperty().subtract(25));
            bound = true;
        }
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            nameText.setText(model.nameProperty().get());
            setGraphic(nameText);
        }
    }
}