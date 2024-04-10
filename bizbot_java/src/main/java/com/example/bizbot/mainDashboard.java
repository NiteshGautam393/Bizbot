package com.example.bizbot;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.view.JasperViewer;


import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.text.SimpleDateFormat;

public class mainDashboard implements Initializable {
    @FXML
    private Button btn_add;

    @FXML
    private Button btn_clear;

    @FXML
    private Button btn_delete;

    @FXML
    private Button btn_import;

    @FXML
    private Button btn_update;
    @FXML
    private TableColumn<productData,String > column_date;

    @FXML
    private TableColumn<productData,String > column_pID;

    @FXML
    private TableColumn<productData,String > column_pName;

    @FXML
    private TableColumn<productData,String > column_price;

    @FXML
    private TableColumn<productData,String > column_status;

    @FXML
    private TableColumn<productData,String > column_stock;
    @FXML
    private TableColumn<?, ?> column_sizes;
    @FXML
    private TableColumn<?, ?> column_colors;
    @FXML
    private TextField text_sizes;
    @FXML
    private TextField text_color;
    @FXML
    private TextField test_orderNo_textfield;

    @FXML
    private ComboBox<?> test_orderStatus;

    @FXML
    private TextField test_phone_textfield;

    @FXML
    private AnchorPane dashboard_form;

    @FXML
    private Button id_customer;

    @FXML
    private Button id_dashboard;

    @FXML
    private Button id_inventory;

    @FXML
    private Button id_order;

    @FXML
    private Button id_report;
    @FXML
    private Button id_signout;

    @FXML
    private AnchorPane inventory_display;

    @FXML
    private AnchorPane inventory_form;
    @FXML
    private ImageView text_image;

    @FXML
    private TextField text_pID;

    @FXML
    private TextField text_pName;
    @FXML
    private AnchorPane dashboard_display;

    @FXML
    private TextField text_price;

    @FXML
    private TextField text_stock;
    @FXML
    private TableColumn<OrderData,String > test_date;

    @FXML
    private AnchorPane test_order;

    @FXML
    private TableColumn<OrderData,String > test_order_number;

    @FXML
    private TableView<OrderData> test_order_table;

    @FXML
    private TableColumn<OrderData,String > test_phone;

    @FXML
    private TableColumn<OrderData,String > test_status;
    @FXML
    private TableColumn<OrderData, Integer> test_product;

    @FXML
    private TableColumn<OrderData, String> test_quantity;

    @FXML
    private TableColumn<OrderData, String> test_size;

    @FXML
    private TableColumn<OrderData, String> test_color;

    @FXML
    private TableView<productData> inventory_tableview;
    @FXML
    private ComboBox<?> combo_status;
    @FXML
    private AnchorPane order_display;
    @FXML
    private TableColumn<productData, String> order_col_size;
    @FXML
    private AnchorPane customer_display;
    @FXML
    private AnchorPane report_display;
    @FXML
    private Label dash_username;

    @FXML
    private TextField order_amount;

    @FXML
    private Label order_change;

    @FXML
    private TableColumn<productData, String> order_col_pname;

    @FXML
    private TableColumn<productData, String> order_col_price;

    @FXML
    private TableColumn<productData, String> order_col_quantity;

    @FXML
    private GridPane order_grid;

    @FXML
    private Button order_placeOrderbtn;

    @FXML
    private Button order_receiptBtn;

    @FXML
    private Button order_removeBtn;

    @FXML
    private ScrollPane order_scroll;

    @FXML
    private TableView<productData> order_tableview;

    @FXML
    private Label order_total;

    @FXML
    private TableColumn<CustomerData, String> customer_col_cid;

    @FXML
    private TableColumn<CustomerData, String> customer_col_date;

    @FXML
    private TableColumn<CustomerData, String> customer_col_emp;

    @FXML
    private TableColumn<CustomerData, String> customer_col_total;
    @FXML
    private TableView<CustomerData> customer_tableview;
    @FXML
    private Label customer_no;
    @FXML
    private BarChart<?, ?> dashboard_CC;

    @FXML
    private AreaChart<?, ?> dashboard_IC;
    @FXML
    private BarChart<?, ?> product_chart;
    @FXML
    private Label today_income;

    @FXML
    private Label total_sales;
    @FXML
    private PieChart report_piechart;
    private Image image;
    private Alert alert;
    private Connection connect;
    private PreparedStatement psmt;
    private Statement smt;
    private ResultSet rset;
    private ObservableList<productData> card_list_data = FXCollections.observableArrayList();

    private String[] statusList = {"Available", "Unavailable"};
    private String [] orderStatus = {"Initiated", "In-Transit", "Delivered"};
    //method to display the active username in the menu
    public void displayUsername(){
        String user = data.username;
        user = user.substring(0,1).toUpperCase() + user.substring(1);
        dash_username.setText(user);
    }
    //method to sign out for the current user
    public void signOutBtn(){
        try{
            alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Message");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to log out?");
            Optional<ButtonType> option = alert.showAndWait();
            if(option.get().equals(ButtonType.OK)){
                //To hide the main form
                id_signout.getScene().getWindow().hide();
                //To move back to login menu signing out the current user
                Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
                Stage stage = new Stage();
                Scene scene = new Scene(root);
                stage.setTitle("Bizbot Login");
                stage.setScene(scene);
                stage.show();
            }
        }
        catch (Exception e){
            System.out.println("Error"+e);
        }
    }

    //To display the number of customers in the dashboard
    public void dashboardDisplayCustomer(){
        String customer_number_query = "select count(id) from receipt";
        connect = DatabaseConnectivity.connectDb();
        try{
            int customer_number = 0;
            psmt = connect.prepareStatement(customer_number_query);
            rset = psmt.executeQuery();
            if(rset.next()){
                customer_number = rset.getInt("count(id)");
            }
            customer_no.setText(String.valueOf(customer_number));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    //function to calculate the today's income
    public void dashboardTodayIncome(){
        LocalDate currentDate = LocalDate.now();
        //System.out.println(currentDate);
        String today_income_query = "select sum(total) from receipt where date = '"+
               currentDate + "'";
        connect = DatabaseConnectivity.connectDb();
        try{
            double ti = 0.0;
            psmt = connect.prepareStatement(today_income_query);
            rset = psmt.executeQuery();
            if(rset.next()){
                ti = rset.getDouble("sum(total)");
            }
            today_income.setText("Rs." + ti);

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    //Function to display the total sales
    public void dashboardTotalSales(){
        String total_sales_query = "select sum(total) from receipt";
        connect = DatabaseConnectivity.connectDb();
        try{
            float ts = 0;
            psmt = connect.prepareStatement(total_sales_query);
            rset = psmt.executeQuery();
            if(rset.next()){
                ts = rset.getFloat("sum(total)");
            }
            total_sales.setText("Rs."+ ts);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    //To display the income chart
    public void dashboardIncomeChart(){
        dashboard_IC.getData().clear();
        String income_chart_query = "select date, sum(total) from receipt group by date order by timestamp(date)";
        connect = DatabaseConnectivity.connectDb();
        //Xy chart is used to create various types of chart and Series represents a series of data points that will be plotted in the chart
        XYChart.Series chart = new XYChart.Series();
        try{
            psmt = connect.prepareStatement(income_chart_query);
            rset = psmt.executeQuery();
            while(rset.next()){
                chart.getData().add(new XYChart.Data<>(rset.getString(1), rset.getFloat(2)));
            }
            dashboard_IC.getData().add(chart);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    //To display the customer chart
    public void dashboardCustomerChart(){
        dashboard_CC.getData().clear();
        String customer_chart_query = "select date, count(id) from receipt group by date order by timestamp(date)";
        connect = DatabaseConnectivity.connectDb();
        //Xy chart is used to create various types of chart and Series represents a series of data points that will be plotted in the chart
        XYChart.Series chart = new XYChart.Series();
        try{
            psmt = connect.prepareStatement(customer_chart_query);
            rset = psmt.executeQuery();
            while(rset.next()){
                chart.getData().add(new XYChart.Data<>(rset.getString(1), rset.getInt(2)));
            }
            dashboard_CC.getData().add(chart);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

//Inventory Section
    public void inventoryStatus(){
        //This is to add list items to the combo-box of the inventory section
        List<String> Inv_status = new ArrayList<>();
        for(String data: statusList){
            Inv_status.add(data);
        }
        ObservableList listData = FXCollections.observableArrayList(Inv_status);
        combo_status.setItems(listData);

    }
    //to merge all the data
    public ObservableList<productData> inventoryData(){
        ObservableList<productData> data_list = FXCollections.observableArrayList();
        String select_product_query = "select * from product";
        connect = DatabaseConnectivity.connectDb();
        try{
            psmt = connect.prepareStatement(select_product_query);
            rset = psmt.executeQuery();
            productData prod_data;
            while(rset.next()){
                prod_data = new productData(rset.getInt("id"),
                        rset.getString("product_id"),
                        rset.getString("product_name"),
                        rset.getDouble("price"),
                        rset.getString("status"),
                        rset.getString("image"),
                        rset.getInt("stock"),
                        rset.getString("sizes"),
                        rset.getString("colors"));
                data_list.add(prod_data);
            }
        }
        catch (Exception e){
            System.out.println("Error:"+e);
        }
        return data_list;
    }
    private ObservableList<productData> inventoryListData;
    //To show data in our table
    public void inventoryShowData(){
        inventoryListData = inventoryData();
        column_pID.setCellValueFactory(new PropertyValueFactory<>("product_id"));
        column_pName.setCellValueFactory(new PropertyValueFactory<>("product_name"));
        column_stock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        column_status.setCellValueFactory(new PropertyValueFactory<>("status"));
        column_price.setCellValueFactory(new PropertyValueFactory<>("price"));
//        column_date.setCellValueFactory(new PropertyValueFactory<>("date"));
        column_sizes.setCellValueFactory(new PropertyValueFactory<>("sizes"));
        column_colors.setCellValueFactory(new PropertyValueFactory<>("colors"));

        inventory_tableview.setItems(inventoryListData);
    }
    //Action of add button in the inventory
    public void inventoryAddBtn(){
        if(text_pID.getText().isEmpty() || text_pName.getText().isEmpty() || text_price.getText().isEmpty() ||
                text_stock.getText().isEmpty() || text_sizes.getText().isEmpty() || text_color.getText().isEmpty()||
                combo_status.getSelectionModel().getSelectedItem() == null || data.path == null){
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error message");
            alert.setHeaderText(null);
            alert.setContentText("Please fill all the blank fields");
            alert.showAndWait();
        }
        else{
            String check_prodId_query = "select product_id from product where product_id = '"+text_pID.getText()+"'";
            connect = DatabaseConnectivity.connectDb();
            try{
                smt = connect.createStatement();
                rset = smt.executeQuery(check_prodId_query);
                if(rset.next()){
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error message");
                    alert.setHeaderText(null);
                    alert.setContentText(text_pID.getText()+" is already taken");
                    alert.showAndWait();
                }
                else{
                    String insert_data_query = "insert into product"+"(product_id, product_name, stock, status, price, image, sizes, colors)"+
                            "values (?,?,?,?,?,?,?,?)";
                    psmt = connect.prepareStatement(insert_data_query);
                    psmt.setString(1,text_pID.getText());
                    psmt.setString(2,text_pName.getText());
                    psmt.setString(3,text_stock.getText());
                    psmt.setString(4,(String)combo_status.getSelectionModel().getSelectedItem());
                    psmt.setString(5,text_price.getText());
                    String path = data.path;
                    path = path.replace("\\","\\\\");
                    psmt.setString(6,path);
                    psmt.setString(7, text_sizes.getText());
                    psmt.setString(8, text_color.getText());
                    psmt.executeUpdate();
                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information message");
                    alert.setHeaderText(null);
                    alert.setContentText("Successfully added ");
                    alert.showAndWait();
                    inventoryShowData();
                    inventoryClearBtn();

                }
            }
            catch (Exception e){
                System.out.println("Error :"+e);
            }
        }
    }
    //to clear the entered data after pressing the clear button
    public void inventoryClearBtn(){
        text_pID.setText("");
        text_pName.setText("");
        text_price.setText("");
        text_stock.setText("");
        text_color.setText("");
        text_sizes.setText("");
        data.path = "";
        data.id = 0;
        combo_status.getSelectionModel().clearSelection();
        text_image.setImage(null);
    }

    //Action for import button in the inventory
    public void inventoryImportBtn(){
        FileChooser open_file_path = new FileChooser();
        open_file_path.getExtensionFilters().add(new FileChooser.ExtensionFilter("Open Image file", "*png", "*jpg"));
        File file = open_file_path.showOpenDialog(dashboard_form.getScene().getWindow());
        if(file != null){
            data.path = file.getAbsolutePath();
            // here 180 is the desired width, 155 is the desired height, false is a boolean indicating whether to preserve the aspect ratio, true is a boolean indicating whether to enable smooth scaling
            image = new Image(file.toURI().toString(), 165, 145, false, true );
            text_image.setImage(image);
        }
    }
    //Action for selecting the data from the table
    public void inventorySelectData(){
        productData prodData = inventory_tableview.getSelectionModel().getSelectedItem();
        int num = inventory_tableview.getSelectionModel().getSelectedIndex();
        if((num-1)< -1  ) return;
        text_pID.setText(prodData.getProduct_id());
        text_pName.setText(prodData.getProduct_name());
        text_price.setText(String.valueOf(prodData.getPrice()));
        text_stock.setText(String.valueOf(prodData.getStock()));
        text_sizes.setText(String.valueOf(prodData.getSizes()));
        text_color.setText(String.valueOf(prodData.getColors()));
        data.path = prodData.getImage();
        String path = prodData.getImage();
        data.id = prodData.getId();
        image = new Image(path, 165, 145, false, true);
        text_image.setImage(image);
    }
    //Action for update button in the inventory section
    public void inventoryUpdateBtn(){
        if(text_pID.getText().isEmpty() || text_pName.getText().isEmpty() || text_price.getText().isEmpty() ||
                text_stock.getText().isEmpty() || text_sizes.getText().isEmpty()|| text_color.getText().isEmpty()
                || combo_status.getSelectionModel().getSelectedItem() == null || data.path == null || data.id == 0){
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error message");
            alert.setHeaderText(null);
            alert.setContentText("Please fill all the blank fields");
            alert.showAndWait();
        }
        else{
            String path = data.path;
            path = path.replace("\\", "\\\\");
            String update_query = "update product set " + "product_id ='"+text_pID.getText()+
                    "', product_name = '"+ text_pName.getText()+"', stock = '"+text_stock.getText()+
                    "', price = '"+text_price.getText()+"', status = '"+combo_status.getSelectionModel().getSelectedItem()+
                    "', image = '"+path+"', sizes = '"+text_sizes.getText()+"', colors = '"+
                    text_color.getText()+"' where id = "+data.id;
            connect = DatabaseConnectivity.connectDb();
            try{
                alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation message");
                alert.setHeaderText(null);
                alert.setContentText("Are you sure you want to update Product ID:"+ text_pID.getText());
                Optional<ButtonType> option = alert.showAndWait();
                if(option.get().equals(ButtonType.OK)){
                    psmt = connect.prepareStatement(update_query);
                    psmt.executeUpdate();
                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information message");
                    alert.setHeaderText(null);
                    alert.setContentText("Product updated successfully");
                    alert.showAndWait();
                    //To update the table view
                    inventoryShowData();
                    //to clear the fields
                    inventoryClearBtn();
                }
                else{
                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information message");
                    alert.setHeaderText(null);
                    alert.setContentText("Product update cancelled");
                    alert.showAndWait();
                }


            }
            catch (Exception e){
                System.out.println("Error:"+ e);
            }
        }
    }
    //action for delete button in the inventory
    public void inventoryDeleteBtn(){
        if(text_pID.getText().isEmpty() || text_pName.getText().isEmpty() || text_price.getText().isEmpty() ||
                text_stock.getText().isEmpty() || text_sizes.getText().isEmpty() || text_color.getText().isEmpty()
                || combo_status.getSelectionModel().getSelectedItem() == null || data.path == null || data.id == 0){
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error message");
            alert.setHeaderText(null);
            alert.setContentText("Please fill all the blank fields");
            alert.showAndWait();
        }
        else {
            String delete_query = "delete from product where id = "+data.id;
            try {
                alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation message");
                alert.setHeaderText(null);
                alert.setContentText("Are you sure you want to delete Product ID:" + text_pID.getText());
                Optional<ButtonType> option = alert.showAndWait();
                if (option.get().equals(ButtonType.OK)) {
                    psmt = connect.prepareStatement(delete_query);
                    psmt.executeUpdate();
                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information message");
                    alert.setHeaderText(null);
                    alert.setContentText("Product deleted successfully");
                    alert.showAndWait();
                    inventoryShowData();
                    inventoryClearBtn();
                }
                else{
                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information message");
                    alert.setHeaderText(null);
                    alert.setContentText("Product deletion cancelled");
                    alert.showAndWait();

                }
            }
            catch (Exception e){
                System.out.println("Error:"+ e);
            }
        }
    }
    //Order section
    public ObservableList<productData> orderGetData(){
        String get_card_query = "select * from product";
        ObservableList<productData> listData = FXCollections.observableArrayList();
        connect = DatabaseConnectivity.connectDb();
        try{
            psmt = connect.prepareStatement(get_card_query);
            rset = psmt.executeQuery();
            productData prod;
            while(rset.next()){
                prod = new productData(rset.getInt("id"),
                        rset.getString("product_id"),
                        rset.getString("product_name"),
                        rset.getInt("stock"),
                        rset.getDouble("price"),
                        rset.getString("image"));
                listData.add(prod);
            }
        }
        catch (Exception e){
            System.out.println("error:"+e);
        }
        return listData;
    }
    public void orderDisplayCard(){
        card_list_data.clear();
        card_list_data.addAll(orderGetData());
        int row = 0;
        int column = 0;
        order_grid.getChildren().clear();
        order_grid.getColumnConstraints().clear();
        order_grid.getColumnConstraints().clear();
        for (productData cardListDatum : card_list_data) {
            try {
                FXMLLoader load = new FXMLLoader();
                load.setLocation(getClass().getResource("CardProduct.fxml"));
                AnchorPane pane = load.load();
                CardProductController cardC = load.getController();
                cardC.setData(cardListDatum);
                if (column == 4) {
                    column = 0;
                    row += 1;
                }
                order_grid.add(pane, column++, row);
                GridPane.setMargin(pane, new Insets(5));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public ObservableList<productData> orderGetDisplay(){
        customerID();
        ObservableList<productData> order_list_data = FXCollections.observableArrayList();
        String order_query = "select * from customer where customer_id = "+ cId;
        connect = DatabaseConnectivity.connectDb();
        try{
            psmt = connect.prepareStatement(order_query);
            rset = psmt.executeQuery();
            productData prod;
            while (rset.next()){
                prod = new productData(rset.getInt("id"),
                        rset.getString("product_id"),
                        rset.getString("product_name"),
                        rset.getInt("quantity"),
                        rset.getString("size"),
                        rset.getDouble("price"));
                order_list_data.add(prod);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return order_list_data;
    }
    private ObservableList<productData> order_data;
    public void orderDisplayData(){
        order_data = orderGetDisplay();
        order_col_pname.setCellValueFactory(new PropertyValueFactory<>("product_name"));
        order_col_quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        order_col_size.setCellValueFactory(new PropertyValueFactory<>("size"));
        order_col_price.setCellValueFactory(new PropertyValueFactory<>("price"));
        order_tableview.setItems(order_data);
    }
    private  int get_id;
    public void orderSelect(){
        productData prod = order_tableview.getSelectionModel().getSelectedItem();
        int num = order_tableview.getSelectionModel().getSelectedIndex();
        if((num-1)< -1) return;
        //to get id per order
        get_id = prod.getId();
    }
    private int cId;
    // Function to generate unique customer id
    public void customerID(){
        String sql_query = "select max(customer_id) from customer";
        connect = DatabaseConnectivity.connectDb();
        try{
            psmt = connect.prepareStatement(sql_query);
            rset = psmt.executeQuery();
            if(rset.next()){
                cId = rset.getInt("max(customer_id)");
            }
            String checkCId = "select max(customer_id) from receipt";
            psmt = connect.prepareStatement(checkCId);
            rset = psmt.executeQuery();
            int checkId = 0;
            if(rset.next()){
                checkId = rset.getInt("max(customer_id)");
            }
            if(cId == 0){
                cId+= 1;
            } else if (cId == checkId) {
                cId+=1;
            }
            data.cID = cId;
        }
        catch (Exception e){
           e.printStackTrace();
        }
    }
    private double totalP;
    public void orderGetTotal(){
        customerID();
        String total_query = "select SUM(price) from customer where customer_id = "+ cId;
        connect = DatabaseConnectivity.connectDb();
        try{
            psmt = connect.prepareStatement(total_query);
            rset = psmt.executeQuery();
            if(rset.next()){
                totalP = rset.getDouble("SUM(price)");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void orderDisplayTotal(){
        orderGetTotal();
        order_total.setText("Rs."+ totalP);
    }
    private double amount;
    private double change;
    public void orderTotalAmount(){
        orderGetTotal();
        if(order_amount.getText().isEmpty() || totalP == 0){
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error message");
            alert.setHeaderText(null);
            alert.setContentText("Invalid request!");
            alert.showAndWait();
        }
        else{
            //type conversion
            amount = Double.parseDouble(order_amount.getText());
            change = 0;
            if(amount < totalP){
                order_amount.setText("");
            }
            else{
                change = amount - totalP;
                order_change.setText("Rs."+change);
            }
        }
    }
    public void placeOrderBtn(){
        if(totalP == 0){
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error message");
            alert.setHeaderText(null);
            alert.setContentText("Invalid request!");
            alert.showAndWait();
        }
        else{
            orderGetTotal();
            String place_order_query = "insert into receipt (customer_id, total, date, em_username) "
                    + "values(?,?,?,?)";
            connect = DatabaseConnectivity.connectDb();
            try{
                if(amount == 0){
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error message");
                    alert.setHeaderText(null);
                    alert.setContentText("Invalid request!");
                    alert.showAndWait();
                }
                else{
                    alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation message");
                    alert.setHeaderText(null);
                    alert.setContentText("Are you sure?");
                    Optional<ButtonType> option = alert.showAndWait();
                    if(option.get().equals(ButtonType.OK)){

                        customerID();
                        orderGetTotal();
                        psmt = connect.prepareStatement(place_order_query);
                        psmt.setString(1,String.valueOf(cId));
                        psmt.setString(2,String.valueOf(totalP));
                        LocalDateTime currentDate = LocalDateTime.now();
                        psmt.setString(3,String.valueOf(currentDate));
                        psmt.setString(4,data.username);
                        psmt.executeUpdate();
                        alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Information message");
                        alert.setHeaderText(null);
                        alert.setContentText("Successful");
                        alert.showAndWait();
                        orderDisplayData();

                    }else{
                        alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Error message");
                        alert.setHeaderText(null);
                        alert.setContentText("Cancelled");
                        alert.showAndWait();
                    }
                }

            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public void orderRestart(){
        amount = 0;
        totalP = 0;
        change = 0;
        order_amount.setText("");
        order_change.setText("Rs. 0.0");
        order_total.setText("Rs. 0.0");
    }
    public void orderRemoveBtn(){
            if(get_id == 0){
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error message");
                alert.setHeaderText(null);
                alert.setContentText("Invalid request");
                alert.showAndWait();
            }else {
                String delete_order_query = "delete from customer where id = "+ get_id;
                connect = DatabaseConnectivity.connectDb();
                try{
                    alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation message");
                    alert.setHeaderText(null);
                    alert.setContentText("Are you sure you want to remove this order?");
                    Optional<ButtonType> option = alert.showAndWait();
                    if(option.get().equals(ButtonType.OK)){
                        psmt = connect.prepareStatement(delete_order_query);
                        psmt.executeUpdate();
                    }
                    orderDisplayData();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
    }

    //order receipt generation
    public void orderReceiptBtn(){
        LocalDate currentDate = LocalDate.now();
        if(totalP == 0 || order_amount.getText().isEmpty()){
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error message");
            alert.setHeaderText(null);
            alert.setContentText("Please order first");
            alert.showAndWait();
        }
        else{
            HashMap map = new HashMap();
            map.put("getReceipt", (cId-1));
            try{
                JasperDesign jDesign =  JRXmlLoader.load("C:\\Users\\Aayush Maharjan\\IdeaProjects\\bizbot\\src\\main\\resources\\com\\example\\bizbot\\report.jrxml");
                JasperReport jReport = JasperCompileManager.compileReport(jDesign);
                JasperPrint jPrint = JasperFillManager.fillReport(jReport, map, connect);
                JasperViewer.viewReport(jPrint, false);
                JasperExportManager.exportReportToPdfFile(jPrint,"C:\\Users\\Aayush Maharjan\\IdeaProjects\\bizbot\\receipts\\"+currentDate+"\\customer-"+(cId-1)+".pdf");
                orderRestart();

            }
            catch (JRException e){
                e.printStackTrace();
            }
        }
    }
// Customer section
    public ObservableList<CustomerData> customerDataList(){
        ObservableList<CustomerData> customer_list_data = FXCollections.observableArrayList();
        String customer_query = "select * from receipt ";
        connect = DatabaseConnectivity.connectDb();
        try{
            psmt = connect.prepareStatement(customer_query);
            rset = psmt.executeQuery();
            CustomerData cData;
            while (rset.next()){
                cData = new CustomerData(rset.getInt("id"), rset.getInt("customer_id"),
                        rset.getDouble("total"), rset.getDate("date"),
                        rset.getString("em_username"));
                customer_list_data.add(cData);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return customer_list_data;
    }
    private ObservableList<CustomerData> customer_data;
    public void customerDisplayData(){
        customer_data = customerDataList();
        customer_col_cid.setCellValueFactory(new PropertyValueFactory<>("customer_id"));
        customer_col_total.setCellValueFactory(new PropertyValueFactory<>("total"));
        customer_col_date.setCellValueFactory(new PropertyValueFactory<>("date"));
        customer_col_emp.setCellValueFactory(new PropertyValueFactory<>("em_username"));
        customer_tableview.setItems(customer_data);
    }
    public void DailyFolderCreator() {
        String directoryName = "receipts";
        Date today = new Date();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String folderName = dateFormat.format(today);

        // Create a File object representing the directory
        File directory = new File(directoryName);
        if (!directory.exists()) {
            if (directory.mkdir()) {
                System.out.println("Directory created: " + directory.getAbsolutePath());
            } else {
                System.err.println("Failed to create directory!");
                return;
            }
        } else {
            System.out.println("Directory already exists: " + directory.getAbsolutePath());
        }

        // Create a File object representing the folder inside the directory
        File folder = new File(directory, folderName);

        // Check if the folder already exists
        if (!folder.exists()) {
            // If the folder doesn't exist, create it
            if (folder.mkdir()) {
                System.out.println("Folder created: " + folder.getAbsolutePath());
            } else {
                System.err.println("Failed to create folder!");
            }
        } else {
            System.out.println("Folder already exists for today: " + folder.getAbsolutePath());
        }

    }

    //Report section

    //To generate the pie chart
    public void reportPiechart(){
        report_piechart.getData().clear();
        String product_pie_chart_query = "select product_name, sum(quantity) from customer group by product_name order by sum(quantity) desc";
        connect = DatabaseConnectivity.connectDb();
        //Xy chart is used to create various types of chart and Series represents a series of data points that will be plotted in the chart

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        PieChart chart = new PieChart(pieChartData);

        try{
            psmt = connect.prepareStatement(product_pie_chart_query);
            rset = psmt.executeQuery();
            while(rset.next()){
                pieChartData.add(new PieChart.Data(rset.getString(1), rset.getInt(2)));
            }
            chart.setTitle("Product Pie-chart");
            report_piechart.setData(pieChartData);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    //To display the customer chart
    public void reportProductChart(){
        product_chart.getData().clear();
        String product_chart_query = "select product_name, sum(quantity) from customer group by product_name order by sum(quantity) desc";
        connect = DatabaseConnectivity.connectDb();
        //Xy chart is used to create various types of chart and Series represents a series of data points that will be plotted in the chart
        XYChart.Series chart = new XYChart.Series();
        try{
            psmt = connect.prepareStatement(product_chart_query);
            rset = psmt.executeQuery();
            while(rset.next()){
                chart.getData().add(new XYChart.Data<>(rset.getString(1), rset.getInt(2)));
            }
            product_chart.getData().add(chart);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    //test order part
    public void orderStatus(){
        //This is to add list items to the combo-box of the inventory section
        List<String> order_status = new ArrayList<>();
        for(String data: orderStatus){
            order_status.add(data);
        }
        ObservableList orderlistData = FXCollections.observableArrayList(order_status);
        test_orderStatus.setItems(orderlistData);

    }
    public ObservableList<OrderData> testOrderDataList(){
        ObservableList<OrderData> test_order_list_data = FXCollections.observableArrayList();
        String test_order_query = "select * from testorder";
        connect = DatabaseConnectivity.connectDb();
        try{
            psmt = connect.prepareStatement(test_order_query);
            rset = psmt.executeQuery();
            OrderData oData;
            while (rset.next()){
                oData = new OrderData(rset.getInt("Id"), rset.getInt("OrderNumber"),
                        rset.getDate("orderDate"),
                        rset.getString("Phone"), rset.getString("orderStatus"),
                        rset.getInt("productID"), rset.getInt("Quantity"),
                        rset.getString("Size"),
                        rset.getString("Colors")
                        );
                test_order_list_data.add(oData);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return test_order_list_data;
    }
    private ObservableList<OrderData> test_order_data;
    public void testorderDisplayData(){
        test_order_data = testOrderDataList();
        test_order_number.setCellValueFactory(new PropertyValueFactory<>("OrderNumber"));
        test_date.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        test_phone.setCellValueFactory(new PropertyValueFactory<>("Phone"));
        test_product.setCellValueFactory(new PropertyValueFactory<>("productID"));
        test_quantity.setCellValueFactory(new PropertyValueFactory<>("Quantity"));
        test_color.setCellValueFactory(new PropertyValueFactory<>("Colors"));
        test_size.setCellValueFactory(new PropertyValueFactory<>("Size"));
        test_status.setCellValueFactory(new PropertyValueFactory<>("orderStatus"));
        test_order_table.setItems(test_order_data);
    }

    //order clear button to clear the selected fields
    public void orderClearBtn(){
        test_orderNo_textfield.setText("");
        test_phone_textfield.setText("");
        test_orderStatus.getSelectionModel().clearSelection();
    }

    //function to select a particular order
    public void orderSelectItem(){
       OrderData orderData = test_order_table.getSelectionModel().getSelectedItem();
       int num = test_order_table.getSelectionModel().getSelectedIndex();
       if((num-1)< -1) return;
       test_orderNo_textfield.setText(String.valueOf(orderData.getOrderNumber()));
       test_phone_textfield.setText(String.valueOf(orderData.getPhone()));
        data.id = orderData.getId();
    }

    //function to update the content of order
    public void updateOrderBtn(){
        if(test_orderNo_textfield.getText().isEmpty() || test_phone_textfield.getText().isEmpty() || test_orderStatus.getSelectionModel().getSelectedItem() == null){
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all the blank fields");
            alert.showAndWait();
        }
        else{
            String order_update_query = "update testorder set " + "OrderNumber ="+test_orderNo_textfield.getText()
                    +", Phone ='"+ test_phone_textfield.getText()+"', orderStatus='"+
                    test_orderStatus.getSelectionModel().getSelectedItem()+"' where id ="+ data.id;
            connect = DatabaseConnectivity.connectDb();
            try{
                alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation Message");
                alert.setHeaderText(null);
                alert.setContentText("Are you sure you want to update Order-number:"+ test_orderNo_textfield.getText());
                Optional<ButtonType> option = alert.showAndWait();
                if(option.get().equals(ButtonType.OK)){
                    psmt = connect.prepareStatement(order_update_query);
                    psmt.executeUpdate();
                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Order updated successfully");
                    alert.showAndWait();
                    testorderDisplayData();
                    orderClearBtn();
                }
                else{
                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Order updated cancelled");
                    alert.showAndWait();
                }

            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    //function to delete the order
    public void orderDeleteBtn(){
        if(test_orderNo_textfield.getText().isEmpty() || test_phone_textfield.getText().isEmpty() || test_orderStatus.getSelectionModel().getSelectedItem() == null){
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all the blank fields");
            alert.showAndWait();
        }
        else{
            String delete_testorder_query = "delete from testorder where id ="+data.id;
            try{
                alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation Message");
                alert.setHeaderText(null);
                alert.setContentText("Are you sure you want to delete Order-number:"+ test_orderNo_textfield.getText());
                Optional<ButtonType> option = alert.showAndWait();
                if(option.get().equals(ButtonType.OK)){
                    psmt = connect.prepareStatement(delete_testorder_query);
                    psmt.executeUpdate();
                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Order deleted successfully");
                    alert.showAndWait();
                    testorderDisplayData();
                    orderClearBtn();
                }
                else{
                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Order deletion cancelled");
                    alert.showAndWait();
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources){
        DailyFolderCreator();
        displayUsername();
        inventoryStatus();
        inventoryShowData();
        orderDisplayCard();
        inventoryData();
        orderGetDisplay();
        orderDisplayTotal();
        orderDisplayData();
        customerDisplayData();
        dashboardTotalSales();
        dashboardTodayIncome();
        dashboardDisplayCustomer();
        dashboardIncomeChart();
        dashboardCustomerChart();
        reportPiechart();
        reportProductChart();
        testorderDisplayData();
        orderStatus();
    }
    public void switchToTestOrder(ActionEvent event){
        customer_display.setVisible(false);
        order_display.setVisible(false);
        inventory_display.setVisible(false);
        dashboard_display.setVisible(false);
        report_display.setVisible(false);
        test_order.setVisible(true);
        testorderDisplayData();
    }
    public void switchToDashboard(ActionEvent event){
        //this is to switch from inventory to dashboard page
        if(event.getSource() == id_dashboard){
            //when dashboard button is pressed, that anchor pane is set to visible and other sections are made invisible
            dashboard_display.setVisible(true);
            inventory_display.setVisible(false);
            order_display.setVisible(false);
            customer_display.setVisible(false);
            report_display.setVisible(false);
            test_order.setVisible(false);
            dashboardTotalSales();
            dashboardTodayIncome();
            dashboardDisplayCustomer();
            dashboardIncomeChart();
            dashboardCustomerChart();
        }
    }
    public void switchToInventory(ActionEvent event){
        //this is to switch from dashboard to inventory page
        if(event.getSource() == id_inventory){
            //when inventory button is pressed, that anchor pane is set to visible and other sections are made invisible
            inventory_display.setVisible(true);
            dashboard_display.setVisible(false);
            order_display.setVisible(false);
            customer_display.setVisible(false);
            report_display.setVisible(false);
            test_order.setVisible(false);
            inventoryData();
            inventoryShowData();
        }
    }
    public void switchToOrderList(ActionEvent event){
        //this is to switch to order section
        if(event.getSource()== id_order){
            //when order button is pressed, that anchor pane is set to visible and other sections are made invisible
            order_display.setVisible(true);
            inventory_display.setVisible(false);
            dashboard_display.setVisible(false);
            customer_display.setVisible(false);
            report_display.setVisible(false);
            test_order.setVisible(false);
            orderDisplayCard();
            orderGetDisplay();
            orderDisplayTotal();
            orderDisplayData();
        }
    }
    public void switchToCustomer(ActionEvent event){
        customer_display.setVisible(true);
        order_display.setVisible(false);
        inventory_display.setVisible(false);
        dashboard_display.setVisible(false);
        report_display.setVisible(false);
        test_order.setVisible(false);
        customerDisplayData();
    }
    public void switchToReport(ActionEvent event){
        report_display.setVisible(true);
        customer_display.setVisible(false);
        order_display.setVisible(false);
        inventory_display.setVisible(false);
        dashboard_display.setVisible(false);
        test_order.setVisible(false);
        reportPiechart();
        reportProductChart();
    }
}