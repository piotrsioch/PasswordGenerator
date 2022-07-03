package com.example.passwordgenerator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;


public class MoreThanOnePasswordAppController extends AppController implements Initializable {
    @FXML
    private Button goBackButton;

    @FXML
    private ComboBox<Integer> quantityCheckBox;

    @FXML
    private CheckBox beginWithLetter;

    @FXML
    private Button copyButton;

    @FXML
    private CheckBox excludeAmbiguousCharacters;

    @FXML
    private Button generatePasswordButton;

    @FXML
    private CheckBox includeLowercaseCharacters;

    @FXML
    private CheckBox includeNumbers;

    @FXML
    private CheckBox includeSymbols;

    @FXML
    private CheckBox includeUppercaseCharacters;

    @FXML
    private CheckBox noDuplicateCharacters;

    @FXML
    private ComboBox<Integer> passwordLength;

    @FXML
    private TextArea passwordTextArea;

    @FXML
    private Label rememberPasswordLabel;

    @FXML
    private Button saveButton;

    @FXML
    private Label warningLabel;

    @FXML
    private Button generateMoreThanOnePasswordButton;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializePasswordLength();
        passwordLength.setStyle("-fx-font: 14px \"Serif\";");
        initializePasswordQuantity();
        quantityCheckBox.setStyle("-fx-font: 14px \"Serif\";");
    }

    @FXML
    private void generateButtonOnAction() {
        passwordTextArea.setText("");
        if(!isAnyCheckBoxSelected()) {
            warningLabel.setText("You have to select one of the checboxes!");
        }
        else if(isArrayListLengthGraterThanPasswordLength()) {
            warningLabel.setText("");
            String password = generatePassword();
            passwordTextArea.setText(password);
        }
        else {
            warningLabel.setText("Could not generate password - not enough possible characters! Reduce password " +
                    "length or uncheck no duplicate characters checbox.");
        }
    }
    @Override
    String generatePassword() {
        int passwordQuantity = quantityCheckBox.getSelectionModel().getSelectedItem();
        ArrayList<Character> passwordCharacters;
        String password = "";

        for(int i= passwordQuantity; i > 0; i--) {
            passwordCharacters = getCharactersToPassword();
            int lengthOfPassword = passwordLength.getSelectionModel().getSelectedItem();

            Random random = new Random();
            if(beginWithLetter.isSelected()) {
                ArrayList<Character> lowerAndUpperCharactersOnly = getOnlyUpperAndLowerCasedCharactersToPassword();
                int index = random.nextInt(lowerAndUpperCharactersOnly.size());
                password += lowerAndUpperCharactersOnly.get(index);
                lengthOfPassword -= 1;
                if(noDuplicateCharacters.isSelected()) {
                    passwordCharacters.remove(lowerAndUpperCharactersOnly.indexOf(lowerAndUpperCharactersOnly.get(index)));
                }
            }

            for(int j = 0; j < lengthOfPassword; j++) {
                int index = random.nextInt(passwordCharacters.size());
                password += passwordCharacters.get(index);
                if(noDuplicateCharacters.isSelected()) {
                    passwordCharacters.remove(passwordCharacters.indexOf(passwordCharacters.get(index)));
                }
            }
            password += "\n";
        }
       return password;
    }


    @FXML
    private void copyButtonOnAction() {
        String password = passwordTextArea.getText();
        StringSelection stringSelection = new StringSelection(password);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    @FXML
    private void saveButtonOnAction() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(new Stage());
        savePassword(file, passwordTextArea.getText());

    }

    @FXML
    private void goBackButtonOnAction() {
        try {
            Parent root = (Parent) FXMLLoader.load(AppController.class.getResource("password-generator.fxml"));
            Stage stage = (Stage) goBackButton.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    private void initializePasswordQuantity() {
        ArrayList<Integer> passwordQuantityArrayList = new ArrayList<>();

        for (int i = 1; i < 100; i++) {
            passwordQuantityArrayList.add(i);
        }

        ObservableList<Integer> ob = FXCollections.observableArrayList(passwordQuantityArrayList);

        quantityCheckBox.setItems(ob);
        quantityCheckBox.getSelectionModel().selectFirst();
    }
}
