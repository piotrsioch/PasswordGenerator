package com.example.passwordgenerator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.datatransfer.Clipboard;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.*;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;


public class AppController implements Initializable {

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
    private TextField passwordTextField;

    @FXML
    private Label rememberPasswordLabel;

    @FXML
    private Button saveButton;

    @FXML
    private Label warningLabel;

    @FXML
    private Button generateMoreThanOnePasswordButton;

   private String symbols = "!@#$%^&*";
   private String lowercaseCharacters = "abcdefghijklmnopqrstuvwxyz";
   private String uppercaseCharacters = "ABCDEFGIJKLMNOPQRSTUVWXYZ";
   private String numbers = "1234567890";
   private String ambigousCharacters = "{}[]()/\\`~,;;.<>";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializePasswordLength();
        passwordLength.setStyle("-fx-font: 14px \"Serif\";");
    }

    @FXML
    private void generateButtonOnAction() {
        passwordTextField.setText("");
        rememberPasswordLabel.setText("Remember your password with the first character of each word in this sentence.");
        if(!isAnyCheckBoxSelected()) {
            warningLabel.setText("You have to select one of the checboxes!");
        }
        else if(isArrayListLengthGraterThanPasswordLength()) {
            warningLabel.setText("");
            String password = generatePassword();
            passwordTextField.setText(password);
            if(passwordLength.getSelectionModel().getSelectedItem() < 50) {
                setLabelToRememberPassword();
            }


        }
        else {
            warningLabel.setText("Could not generate password - not enough possible characters! Reduce password " +
                    "length or uncheck no duplicate characters checbox.");
        }
    }

    @FXML
    private void copyButtonOnAction() {
        String password = passwordTextField.getText();
        StringSelection stringSelection = new StringSelection(password);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
        }

    @FXML
    private void saveButtonOnAction() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(new Stage());
        savePassword(file, passwordTextField.getText());

    }

     void savePassword(File file, String password) {
        try {
            PrintWriter printWriter = new PrintWriter(file);
            printWriter.write(password);
            printWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

    @FXML
    private void generateMoreThanOnePasswordButton() {
        try {
            Parent root = (Parent) FXMLLoader.load(AppController.class.getResource("more-than-one-password-generator.fxml"));
            Stage stage = (Stage) generateMoreThanOnePasswordButton.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    private void setLabelToRememberPassword() {
        String password = passwordTextField.getText();
        String stringToRemember = "";
        ArrayList<Character> passwordCharacters = new ArrayList<>();
        for(int i = 0; i < password.length(); i++) {
            passwordCharacters.add(password.charAt(i));
        }

        for(Character character : passwordCharacters) {
            if(Character.isLetter(character)) {
                if(Character.isLowerCase(character)) {
                    stringToRemember += CharactersToWord.characterToWordMap.get(character);
                } else {
                    character = Character.toLowerCase(character);
                    String string = CharactersToWord.characterToWordMap.get(character);
                    stringToRemember += string.toUpperCase();
                }
            } else {
                stringToRemember += character;
            }
            stringToRemember += " ";
        }

        rememberPasswordLabel.setText(stringToRemember);
    }

    boolean isAnyCheckBoxSelected() {
        if(includeLowercaseCharacters.isSelected() || includeUppercaseCharacters.isSelected() ||  includeNumbers.isSelected() || includeSymbols.isSelected()
               ) {
            return true;
        }
        return false;
    }

    boolean isArrayListLengthGraterThanPasswordLength() {
        if(!noDuplicateCharacters.isSelected()) {
            return true;
        }
        ArrayList<Character> arrayList = getCharactersToPassword();
        int lengthOfPassword = passwordLength.getSelectionModel().getSelectedItem();
        if(arrayList.size() > lengthOfPassword) {
            return true;
        }
        return false;
    }

    String generatePassword() {
        Random random = new Random();
        ArrayList<Character> passwordCharacters = getCharactersToPassword();
        String password = "";
        int lengthOfPassword = passwordLength.getSelectionModel().getSelectedItem();

        if(beginWithLetter.isSelected()) {
            ArrayList<Character> lowerAndUpperCharactersOnly = getOnlyUpperAndLowerCasedCharactersToPassword();
            int index = random.nextInt(lowerAndUpperCharactersOnly.size());
            password += lowerAndUpperCharactersOnly.get(index);
            lengthOfPassword -= 1;
            if(noDuplicateCharacters.isSelected()) {
                passwordCharacters.remove(lowerAndUpperCharactersOnly.indexOf(lowerAndUpperCharactersOnly.get(index)));
            }
        }

        for(int i = 0; i < lengthOfPassword; i++) {
            int index = random.nextInt(passwordCharacters.size());
            password += passwordCharacters.get(index);
            if(noDuplicateCharacters.isSelected()) {
                passwordCharacters.remove(passwordCharacters.indexOf(passwordCharacters.get(index)));
            }
        }
        return password;
    }

    private int getRandomNumber(int maxValue) {
        Random random = new Random();
        return random.nextInt(maxValue);
    }

    ArrayList<Character> getCharactersToPassword() {
        ArrayList<Character> charArrayList = new ArrayList<>();

        if(checkIfCheckBoxIsSelected(includeLowercaseCharacters)) {
            getCharactersFromString(charArrayList, lowercaseCharacters);
        }
        if(!checkIfCheckBoxIsSelected(excludeAmbiguousCharacters)) {
            getCharactersFromString(charArrayList, ambigousCharacters);
        }
        if(checkIfCheckBoxIsSelected(includeNumbers)) {
            getCharactersFromString(charArrayList, numbers);
        }
        if(checkIfCheckBoxIsSelected(includeUppercaseCharacters)) {
            getCharactersFromString(charArrayList, uppercaseCharacters);
        }
        if(checkIfCheckBoxIsSelected(includeSymbols)) {
            getCharactersFromString(charArrayList, symbols);
        }
        return charArrayList;
    }

    ArrayList<Character> getOnlyUpperAndLowerCasedCharactersToPassword() {
        ArrayList<Character> charArrayList = new ArrayList<>();
        getCharactersFromString(charArrayList, lowercaseCharacters);
        getCharactersFromString(charArrayList, uppercaseCharacters);
        return charArrayList;
    }

    private boolean checkIfCheckBoxIsSelected(CheckBox checkBox) {
        if (checkBox.isSelected()) {
            return true;
        }
        return false;
    }



    private ArrayList<Character> getCharactersFromString(ArrayList<Character> charArrayList, String string) {
        for(int i = 0; i < string.length(); i++) {
            charArrayList.add(string.charAt(i));
        }
        return charArrayList;
    }


    void initializePasswordLength() {
        ArrayList<Integer> passwordLengthArrayList = new ArrayList<>();

        for (int i = 5; i < 128; i++) {
            passwordLengthArrayList.add(i);
        }

        ObservableList<Integer> ob = FXCollections.observableArrayList(passwordLengthArrayList);

        passwordLength.setItems(ob);
        passwordLength.getSelectionModel().selectFirst();
    }
}