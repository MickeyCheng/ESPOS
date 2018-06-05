
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
    static String SQLQuery;
    public void doConnect(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
//            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbpos","root","root");
            conn = DriverManager.getConnection("jdbc:mysql://166.62.10.53:3306/dbesposbeta","esposuserone","espos1");
        }catch(SQLException | ClassNotFoundException e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
}
