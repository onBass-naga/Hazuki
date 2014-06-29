package ui

import javafx.concurrent.Service
import javafx.concurrent.Task
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Label
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.TextField
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

    @FXML Label messageLabel
    @FXML TextField urlField
    @FXML TextField userField
    @FXML TextField passwordField
    @FXML TextField driverField
    @FXML ProgressIndicator progressIndicator

    ConnectionService connectionService = new ConnectionService()

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
}
