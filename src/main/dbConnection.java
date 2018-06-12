
package main;

import javax.swing.JOptionPane;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import static main.frmLogin.showUserName;

public class dbConnection {
    Connection conn;
    ResultSet rs;
    PreparedStatement pstmt;
    SimpleDateFormat sdfDateGlobal = new SimpleDateFormat("dd-MM-yyyy");
    java.util.Date todayDateGlobal = new java.util.Date();
    public static String LoggedInUser;
    static String SQLQuery;
    static int getMaxAuditIDGlobal;
    SimpleDateFormat sdfAuditGlobal = new SimpleDateFormat("dd-MM-yyyy hh:mm a"); 
    DecimalFormat dfGlobal = new DecimalFormat("#.000");
    public void doConnect(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbesposbeta","root","root");
//            conn = DriverManager.getConnection("jdbc:mysql://166.62.10.53:3306/dbesposbeta","esposuserone","espos1");
        }catch(SQLException | ClassNotFoundException e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
    public void getNextAuditID(){
        try{
            pstmt = conn.prepareStatement("SELECT * from tblaudittrail order by at_id DESC LIMIT 1");
            rs = pstmt.executeQuery();
            if (rs.next()){
                getMaxAuditIDGlobal = rs.getInt(1);
                getMaxAuditIDGlobal++;
            }else{
                getMaxAuditIDGlobal = 1;
            }
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
    public void saveAuditTrail(String getTransaction){
        getNextAuditID();
        try{
            String saveAuditQuery = "INSERT into tblaudittrail (at_id,at_transaction,at_dateandTime,at_user)"
                    + "values(?,?,?,?)";
            pstmt = conn.prepareStatement(saveAuditQuery);
            pstmt.setInt(1, getMaxAuditIDGlobal);
            pstmt.setString(2,getTransaction);
            java.util.Date getDateAudit = new java.util.Date();
            pstmt.setString(3,String.valueOf(sdfAuditGlobal.format(getDateAudit)));
            pstmt.setString(4,showUserName);
            pstmt.execute();
            pstmt.close();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
    
}
