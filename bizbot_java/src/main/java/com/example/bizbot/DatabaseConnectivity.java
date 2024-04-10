package com.example.bizbot;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnectivity {
    public static Connection connectDb(){
        try{
            //Creating a connection
            String url = "jdbc:mysql://localhost:3306/Bizbot";
            String username = "root";
            String password = "Bizbot@9067";
            Connection con = DriverManager.getConnection(url,username,password);
            return con;
        }
        catch (Exception e){
            System.out.println("Error:"+e);
        }
        return null;
    }
}
