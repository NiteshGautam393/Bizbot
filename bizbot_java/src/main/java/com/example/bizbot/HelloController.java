package com.example.bizbot;

import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.sql.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class HelloController implements Initializable  {
    @FXML
    private Hyperlink id_forgot;

    @FXML
    private Button id_loginBtn;

    @FXML
    private AnchorPane id_loginform;

    @FXML
    private PasswordField id_password;

    @FXML
    private Button id_sidebtn;
    @FXML
    private Button id_ready;

    @FXML
    private AnchorPane id_sideform;

    @FXML
    private TextField id_username;

    @FXML
    private TextField su_answer;

    @FXML
    private ComboBox<?> su_combo;

    @FXML
    private PasswordField su_password;

    @FXML
    private Button su_signupBtn;

    @FXML
    private AnchorPane su_signupForm;

    @FXML
    private TextField su_username;
    @FXML
    private TextField fp_answer;

    @FXML
    private Button fp_backBtn;

    @FXML
    private AnchorPane fp_form;

    @FXML
    private Button fp_proceedBtn;

    @FXML
    private ComboBox<?> fp_questions;
    @FXML
    private TextField fp_username;

    @FXML
    private Button np_backBtn;

    @FXML
    private Button np_changeBtn;

    @FXML
    private PasswordField np_confirmationpassword;

    @FXML
    private AnchorPane np_form;

    @FXML
    private PasswordField np_newpassword;
    private Connection connect;
    private PreparedStatement psmt;
    private ResultSet rset;
    private Alert alert;

    private String[] question_list = {"What is your birth month?", "What is your favourite color?", "What is your favourite subject"};

    //Question list for registering account
    public void reqQuestionList() {
        List<String> listQ = new ArrayList<>();
        for (String data : question_list) {
            listQ.add(data);
        }
        ObservableList list_data = FXCollections.observableArrayList(listQ);
        su_combo.setItems(list_data);
    }

    //Question list for forgot password method
    public void forgotQuestionList() {
        List<String> listQ = new ArrayList<>();
        for (String data : question_list) {
            listQ.add(data);
        }
        ObservableList list_data = FXCollections.observableArrayList(listQ);
        fp_questions.setItems(list_data);
    }

    //Action of the proceed button
    public void proceedBtn() {
        if (fp_questions.getSelectionModel().getSelectedItem() == null || fp_answer.getText().isEmpty() || fp_username.getText().isEmpty()) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please fill up the blank fields");
            alert.showAndWait();
        } else {
            String select_data_query = "select username, questions, answers from bizemployee where username = ? and questions = ? and answers = ?";
            connect = DatabaseConnectivity.connectDb();
            try {
                psmt = connect.prepareStatement(select_data_query);
                psmt.setString(1, fp_username.getText());
                psmt.setString(2, (String) fp_questions.getSelectionModel().getSelectedItem());
                psmt.setString(3, fp_answer.getText());

                rset = psmt.executeQuery();
                if (rset.next()) {
                    np_form.setVisible(true);
                    fp_form.setVisible(false);
                } else {
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Incorrect Username/Answer ");
                    alert.showAndWait();
                }
            } catch (Exception e) {
                System.out.println("Error:" + e);
            }
        }
    }

    //Action of Change Password Button
    public void changePasswordButton() {
        if (np_newpassword.getText().isEmpty() || np_confirmationpassword.getText().isEmpty()) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please fill all the blanks ");
            alert.showAndWait();
        } else if (!Objects.equals(np_newpassword.getText(), np_confirmationpassword.getText())) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Password did not match");
            alert.showAndWait();
        } else if (np_newpassword.getText().length() < 8) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Password too small");
            alert.showAndWait();

        } else {
            //Query to get the new date of the new password
            String get_new_date = "select date from bizemployee where username = '" + fp_username.getText() + "'";
            connect = DatabaseConnectivity.connectDb();
            try {
                psmt = connect.prepareStatement(get_new_date);
                rset = psmt.executeQuery();
                String date = "";
                if (rset.next()) {
                    date = rset.getString("date");
                }
                //Query to update the password
                String update_password_query = "update bizemployee set password = '" + np_newpassword.getText() + "', " +
                        "questions ='" + fp_questions.getSelectionModel().getSelectedItem() + "', answers = '"
                        + fp_answer.getText() + "', date = '" + date + "' where username = '" + fp_username.getText() + "'";
                psmt = connect.prepareStatement(update_password_query);
                psmt.executeUpdate();
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information message");
                alert.setHeaderText(null);
                alert.setContentText("Password updated successfully");
                alert.showAndWait();
                id_loginform.setVisible(true);
                np_form.setVisible(false);
                //To clear the field after updating
                np_newpassword.setText("");
                np_confirmationpassword.setText("");
                fp_username.setText("");
                fp_questions.getSelectionModel().clearSelection();
                fp_answer.setText("");
            } catch (Exception e) {
                System.out.println("Error:" + e);
            }

        }
    }
    //Action of back button that leads to login form
    public void backToLoginFormBtn(){
        id_loginform.setVisible(true);
        fp_form.setVisible(false);
    }
    public void backToFpFormBtn(){
        fp_form.setVisible(true);
        np_form.setVisible(false);
    }

    //method of user registration
    public void userRegistration() {
        if (su_username.getText().isEmpty() || su_password.getText().isEmpty() || su_combo.getSelectionModel().isEmpty() || su_answer.getText().isEmpty()) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please fill up the blank fields");
            alert.showAndWait();
        } else {
            String reg_query = "insert into bizemployee (username, password, questions, answers, date)" + "values (?,?,?,?,?)";
            connect = DatabaseConnectivity.connectDb();
            try {
                //To check if username already exists
                String check_username_query = "select username from bizemployee where username = '" + su_username.getText() + "'";
                psmt = connect.prepareStatement(check_username_query);
                rset = psmt.executeQuery();
                if (rset.next()) {
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Username already exists");
                    alert.showAndWait();
                }
                //To alert the user that password is too small
                else if (su_password.getText().length() < 8) {
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Password too small");
                    alert.showAndWait();
                } else {
                    psmt = connect.prepareStatement(reg_query);
                    psmt.setString(1, su_username.getText());
                    psmt.setString(2, su_password.getText());
                    psmt.setString(3, (String) su_combo.getSelectionModel().getSelectedItem());
                    psmt.setString(4, su_answer.getText());

                    //to get the date of account creation
                    LocalDateTime currentDate = LocalDateTime.now();
                    psmt.setString(5, String.valueOf(currentDate));

                    psmt.executeUpdate();
                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information message");
                    alert.setHeaderText(null);
                    alert.setContentText("Successfully registered account");
                    alert.showAndWait();

                    su_username.setText("");
                    su_password.setText("");
                    su_combo.getSelectionModel().clearSelection();
                    su_answer.setText("");

                    //For preparing sliding
                    TranslateTransition slider = new TranslateTransition();
                    slider.setNode(id_sideform);
                    slider.setToX(0);
                    slider.setDuration(Duration.seconds(.5));
                    slider.setOnFinished(
                            (ActionEvent e) -> {
                                id_ready.setVisible(false);
                                id_sidebtn.setVisible(true);
                            }
                    );
                    slider.play();
                }
            } catch (Exception e) {
                System.out.println("Error:" + e);
            }

        }
    }

    //login method for the users
    public void loginAction(ActionEvent event) throws IOException {
        if (id_username.getText().isEmpty() || id_username.getText().isEmpty()) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Information message");
            alert.setHeaderText(null);
            alert.setContentText("Username/Password error");
            alert.showAndWait();
        } else {
            String select_data_query = "select username, password from bizemployee where username = ? and password = ? ";
            connect = DatabaseConnectivity.connectDb();
            try {
                psmt = connect.prepareStatement(select_data_query);
                psmt.setString(1, id_username.getText());
                psmt.setString(2, id_password.getText());
                rset = psmt.executeQuery();
                //if username and password exists then login is successful
                if (rset.next()) {
                    //this will send the username to display in the dashboard
                    data.username = id_username.getText();
                    //Action after Login is  successful
                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information message");
                    alert.setHeaderText(null);
                    alert.setContentText("Login successful");
                    alert.showAndWait();
                    //switch login form to the dashboard
                    Parent root = FXMLLoader.load(getClass().getResource("maindesign.fxml"));
                    Stage stage = new Stage();
                    Scene scene = new Scene(root);
                    stage.setTitle("Bizbot Dashboard");
                    stage.setMinWidth(1100);
                    stage.setMinHeight(600);
                    stage.setScene(scene);
                    stage.show();
                    id_loginBtn.getScene().getWindow().hide();
                }
                //if username and password does not exist then login is not successful
                else {
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Information message");
                    alert.setHeaderText(null);
                    alert.setContentText("Username/Password error");
                    alert.showAndWait();
                }
            } catch (Exception e) {
               e.printStackTrace();
            }
        }
    }

    public void switchForgotPassword() {
        fp_form.setVisible(true);
        id_loginform.setVisible(false);
        forgotQuestionList();
    }

    //The function below is just for testing purpose. this should be removed and replaced with db connection and user authorization
//    public void Login(ActionEvent event) throws IOException {
//        if(id_username.getText().equals("admin") && id_password.getText().equals("pass")){
//            Stage stage = new Stage();
//            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("maindesign.fxml"));
//            Scene scene = new Scene(fxmlLoader.load(), 1100, 600);
//            stage.setTitle("Bizbot Dashboard");
//            stage.setScene(scene);
//            stage.show();
//        }
//    }
    public void switchForm(ActionEvent event) {
        // this is for sliding animation. TranslateTransition is an inbuilt library
        TranslateTransition slider = new TranslateTransition();
        // to display the create account form
        if (event.getSource() == id_sidebtn) {
            slider.setNode(id_sideform);
            //ToX is a translation function that translates towards the x-axis
            slider.setToX(300);
            slider.setDuration(Duration.seconds(.5));
            slider.setOnFinished(
                    (ActionEvent e) -> {
                        id_ready.setVisible(true);
                        id_sidebtn.setVisible(false);
                        id_loginform.setVisible(true);
                        fp_form.setVisible(false);
                        np_form.setVisible(false);
                        reqQuestionList();
                    }
            );
            slider.play();
        }
        //to display the login form
        else if (event.getSource() == id_ready) {
            slider.setNode(id_sideform);
            slider.setToX(0);
            slider.setDuration(Duration.seconds(.5));
            slider.setOnFinished(
                    (ActionEvent e) -> {
                        id_ready.setVisible(false);
                        id_sidebtn.setVisible(true);
                        id_loginform.setVisible(true);
                        fp_form.setVisible(false);
                        np_form.setVisible(false);
                    }
            );
            slider.play();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

}