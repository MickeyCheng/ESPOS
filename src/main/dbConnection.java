
package main;

import javax.swing.JOptionPane;
import java.sql.*;

public class dbConnection {
    Connection conn;
    ResultSet rs;
    PreparedStatement pstmt;
    
    public void doConnect(){
    try{
        Class.forName("com.mysql.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbpos","root","root");
    //    conn = DriverManager.getConnection("jdbc:mysql://dbebpos","root","root");
    }catch(SQLException | ClassNotFoundException e){
        JOptionPane.showMessageDialog(null, e.getMessage());
    }
    }
}
