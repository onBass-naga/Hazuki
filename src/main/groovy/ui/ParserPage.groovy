package ui

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.TextArea

/**
 * Created by naga on 2014/06/29.
 */
class ParserPage implements Page {
    @Override
    def getFxml() { 'parser.fxml' }
}

public class ParserController extends Controller implements Initializable {

    @FXML
    TextArea textArea

    @FXML
    def void parse(ActionEvent event) {

        System.out.print(textArea.text)
    }
}