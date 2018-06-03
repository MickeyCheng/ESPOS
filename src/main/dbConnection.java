
package main;

import javax.swing.JOptionPane;
import java.sql.*;
import java.text.SimpleDateFormat;

public class dbConnection {
    Connection conn;
    ResultSet rs;
    PreparedStatement pstmt;
    SimpleDateFormat sdfDateGlobal = new SimpleDateFormat("dd-MM-yyyy");
    java.util.Date todayDateGlobal = new java.util.Date();
    public static String LoggedInUser;
    public String SQLQuery;
    public void doConnect(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbpos","root","root");
        }catch(SQLException | ClassNotFoundException e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
}
