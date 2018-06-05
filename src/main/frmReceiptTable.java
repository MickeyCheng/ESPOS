package main;

import java.io.File;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import static main.frmLogin.showUserName;
import net.proteanit.sql.DbUtils;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

public class frmReceiptTable extends javax.swing.JFrame {
//ResultSet dbConn.rs;
//Connection conn;
//PreparedStatement dbConn.pstmt;
String getTransactionId;
DecimalFormat df = new DecimalFormat("0.000");
SimpleDateFormat tdf = new SimpleDateFormat("dd/M/YYYY");
String getCardSalesRange,getCashSalesRange,getFocRange;
int getMaxAuditID;
dbConnection dbConn = new dbConnection();
SimpleDateFormat sdfAudit = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
    public frmReceiptTable() {
        initComponents();
        dbConn.doConnect();
        fillTable();
        fillTableStock();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(frmProductControl.DISPOSE_ON_CLOSE);
    }
    private void getNextAuditID(){
        try{
            dbConn.pstmt = dbConn.conn.prepareStatement("SELECT * from tblaudittrail order by at_id DESC LIMIT 1");
            dbConn.rs = dbConn.pstmt.executeQuery();
            if (dbConn.rs.next()){
                getMaxAuditID = dbConn.rs.getInt(1);
                getMaxAuditID++;
            }else{
                getMaxAuditID = 1;
            }
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this,e.getMessage());
        }
    }
    private void saveAuditTrail(String getTransaction){
        getNextAuditID();
        try{
            String saveAuditQuery = "INSERT into tblaudittrail (at_id,at_transaction,at_dateandTime,at_user)"
                    + "values(?,?,?,?)";
            dbConn.pstmt = dbConn.conn.prepareStatement(saveAuditQuery);
            dbConn.pstmt.setInt(1, getMaxAuditID);
            dbConn.pstmt.setString(2,getTransaction);
            Date getDateAudit = new Date();
            dbConn.pstmt.setString(3,String.valueOf(sdfAudit.format(getDateAudit)));
            dbConn.pstmt.setString(4,showUserName);
            dbConn.pstmt.execute();
            dbConn.pstmt.close();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this,e.getMessage());
        }
    }
    private void fillTableStock(){
        DefaultTableModel model = (DefaultTableModel)tableStock.getModel();
        model.setRowCount(0);
    try{
        int i=0;
        String fillStockSQL = "Select productName,stockOnHand from tblproduct order by productName";
        dbConn.pstmt = dbConn.conn.prepareStatement(fillStockSQL);
        dbConn.rs = dbConn.pstmt.executeQuery();
        while (dbConn.rs.next()){
            Object[] adRow={dbConn.rs.getString(1), dbConn.rs.getString(2)};
            model.addRow(adRow);
        }
        tableStock.getColumnModel().getColumn(0).setHeaderValue("ITEM");
        tableStock.getColumnModel().getColumn(1).setHeaderValue("STOCK QTY");        
    }catch(SQLException e){
        JOptionPane.showMessageDialog(this,e.getMessage());
    }
    }
//private void doConnect(){
//    try{
//        Class.forName("com.mysql.jdbc.Driver");
//        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbpos","root","root");
//    }catch(SQLException | ClassNotFoundException e){
//        JOptionPane.showMessageDialog(this, e.getMessage());
//    }
//}
private void fillTable(){
    try{
        dbConn.pstmt = dbConn.conn.prepareStatement("Select transactionId, productName,quantity,paymentMethod,date,time "
                + "from tblreceipt order by transactionId DESC");
        dbConn.rs = dbConn.pstmt.executeQuery();
        tableReceipt.setModel(DbUtils.resultSetToTableModel(dbConn.rs));
        tableReceipt.getColumnModel().getColumn(0).setHeaderValue("ID");
        tableReceipt.getColumnModel().getColumn(1).setHeaderValue("PRODUCT");
        tableReceipt.getColumnModel().getColumn(2).setHeaderValue("QTY");
        tableReceipt.getColumnModel().getColumn(3).setHeaderValue("METHOD");
        tableReceipt.getColumnModel().getColumn(4).setHeaderValue("DATE");
        tableReceipt.getColumnModel().getColumn(5).setHeaderValue("TIME");
    }catch(SQLException e){
        JOptionPane.showMessageDialog(this, e.getMessage());
    }
}
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableReceipt = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        lblChange = new javax.swing.JLabel();
        lblAmountPaid = new javax.swing.JLabel();
        lblTotalAmount = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableStock = new javax.swing.JTable();
        datePicker = new com.toedter.calendar.JDateChooser();
        jButton3 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblDailyCard = new javax.swing.JLabel();
        lblDailyCash = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lblDailyFoc = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        dateTo = new com.toedter.calendar.JDateChooser();
        dateFrom = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(214, 214, 194));
        jPanel1.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(214, 214, 194));
        jPanel2.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tableReceipt.setBackground(new java.awt.Color(214, 214, 194));
        tableReceipt.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        tableReceipt.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tableReceipt.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableReceiptMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tableReceipt);

        jPanel2.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 470, 180));

        jPanel5.setBackground(new java.awt.Color(214, 214, 194));
        jPanel5.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblChange.setFont(new java.awt.Font("Tahoma", 2, 14)); // NOI18N
        lblChange.setForeground(new java.awt.Color(0, 0, 0));
        lblChange.setText("0.000");
        jPanel5.add(lblChange, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 80, 240, 20));

        lblAmountPaid.setFont(new java.awt.Font("Tahoma", 2, 14)); // NOI18N
        lblAmountPaid.setForeground(new java.awt.Color(0, 0, 0));
        lblAmountPaid.setText("0.000");
        jPanel5.add(lblAmountPaid, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 40, 240, 20));

        lblTotalAmount.setFont(new java.awt.Font("Tahoma", 2, 14)); // NOI18N
        lblTotalAmount.setForeground(new java.awt.Color(0, 0, 0));
        lblTotalAmount.setText("0.000");
        jPanel5.add(lblTotalAmount, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 0, 240, 20));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("TOTAL AMOUNT:");
        jPanel5.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 150, 20));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("AMOUNT PAID:");
        jPanel5.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 150, 20));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("CHANGE:");
        jPanel5.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 150, 20));

        jButton1.setBackground(new java.awt.Color(255, 255, 255));
        jButton1.setForeground(new java.awt.Color(0, 0, 0));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/printericon.png"))); // NOI18N
        jButton1.setText("REPRINT RECEIPT");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel5.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 400, 40));

        jPanel2.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 10, 420, 180));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 940, 200));

        jPanel3.setBackground(new java.awt.Color(214, 214, 194));
        jPanel3.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tableStock.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Title 1", "Title 2"
            }
        ));
        jScrollPane2.setViewportView(tableStock);

        jPanel3.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 400, 140));

        datePicker.setDateFormatString("MMM dd, yyyy");
        jPanel3.add(datePicker, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 180, 40));

        jButton3.setBackground(new java.awt.Color(255, 255, 255));
        jButton3.setForeground(new java.awt.Color(0, 0, 0));
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/generateIcon.png"))); // NOI18N
        jButton3.setText("GENERATE SALES");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 160, 210, 40));
        jPanel3.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, 400, 10));

        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("TOTAL CASH SALES:");
        jPanel3.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 240, 180, -1));

        jLabel7.setForeground(new java.awt.Color(0, 0, 0));
        jLabel7.setText("TOTAL CARD SALES:");
        jPanel3.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 220, 180, -1));

        lblDailyCard.setForeground(new java.awt.Color(0, 0, 0));
        lblDailyCard.setText("0.000");
        jPanel3.add(lblDailyCard, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 220, 180, -1));

        lblDailyCash.setForeground(new java.awt.Color(0, 0, 0));
        lblDailyCash.setText("0.000");
        jPanel3.add(lblDailyCash, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 240, 180, -1));

        jLabel8.setForeground(new java.awt.Color(0, 0, 0));
        jLabel8.setText("TOTAL FOC:");
        jPanel3.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 260, 180, -1));

        lblDailyFoc.setForeground(new java.awt.Color(0, 0, 0));
        lblDailyFoc.setText("0.000");
        jPanel3.add(lblDailyFoc, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 260, 180, -1));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 220, 480, 280));

        jPanel4.setBackground(new java.awt.Color(214, 214, 194));
        jPanel4.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton2.setBackground(new java.awt.Color(255, 255, 255));
        jButton2.setForeground(new java.awt.Color(0, 0, 0));
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/homeIcon.png"))); // NOI18N
        jButton2.setText("MAIN MENU");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 190, 380, 60));

        dateTo.setDateFormatString("MMM dd, yyyy");
        jPanel4.add(dateTo, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 50, 180, 30));

        dateFrom.setDateFormatString("MMM dd, yyyy");
        jPanel4.add(dateFrom, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 180, 30));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("TO");
        jPanel4.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 20, 180, 20));

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("FROM");
        jPanel4.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 180, 20));

        jButton4.setBackground(new java.awt.Color(255, 255, 255));
        jButton4.setForeground(new java.awt.Color(0, 0, 0));
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/viewIcon.png"))); // NOI18N
        jButton4.setText("VIEW SALES");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 100, 380, 50));

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 220, 440, 280));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 970, 510));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tableReceiptMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableReceiptMouseClicked
        int row = tableReceipt.getSelectedRow();
        int ba = tableReceipt.convertRowIndexToModel(row);
        getTransactionId = (tableReceipt.getModel().getValueAt(ba, 0)).toString();
        String tableQuery = "SELECT * from tblreceipt where transactionId=?";
        
        try{
            dbConn.pstmt = dbConn.conn.prepareStatement(tableQuery);
            dbConn.pstmt.setString(1, getTransactionId);
            dbConn.rs = dbConn.pstmt.executeQuery();
            if (dbConn.rs.next()){
                lblAmountPaid.setText(String.valueOf(df.format(dbConn.rs.getFloat("amountPaid"))));
                lblChange.setText(String.valueOf(df.format(dbConn.rs.getFloat("amountChange"))));
                lblTotalAmount.setText(String.valueOf(df.format(dbConn.rs.getFloat("totalAmount"))));
            }
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this,e.getMessage());
        }
    }//GEN-LAST:event_tableReceiptMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Map param = new HashMap();
        param.put("tranId", getTransactionId);
        saveAuditTrail("REPRINTED RECEIPT #: " + String.valueOf(getTransactionId));
        try{
            dbConn.conn.close();
            Class.forName("com.mysql.jdbc.Driver");
            dbConn.conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbpos","root","root");
            JasperDesign jd = JRXmlLoader.load(new File("src\\Reports\\repReceipt.jrxml"));
            JasperReport jr = JasperCompileManager.compileReport(jd);
            JasperPrint jp = JasperFillManager.fillReport(jr, param,dbConn.conn);
            JasperViewer.viewReport(jp,false);

        }catch(ClassNotFoundException | SQLException | JRException e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    
    
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        frmMain obj = new frmMain();
        obj.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed
    private void displayDailySales(){
        try{
            dbConn.pstmt = dbConn.conn.prepareStatement("Select sum(bdPrice) from tblreceipt where date=? and paymentMethod=?");
            dbConn.pstmt.setString(1, String.valueOf(tdf.format(datePicker.getDate())));
            dbConn.pstmt.setString(2,"CASH");
            dbConn.rs = dbConn.pstmt.executeQuery();
            if (dbConn.rs.next()){
                lblDailyCash.setText(String.valueOf(df.format(dbConn.rs.getDouble(1))));
            }else{
                lblDailyCash.setText("0.000");
            }
            dbConn.pstmt.close();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
        try{
            dbConn.pstmt = dbConn.conn.prepareStatement("Select sum(bdPrice) from tblreceipt where date=? and paymentMethod=?");
            dbConn.pstmt.setString(1, String.valueOf(tdf.format(datePicker.getDate())));
            dbConn.pstmt.setString(2,"CARD");
            dbConn.rs = dbConn.pstmt.executeQuery();
            if (dbConn.rs.next()){
                lblDailyCard.setText(String.valueOf(df.format(dbConn.rs.getDouble(1))));
            }else{
                lblDailyCard.setText("0.000");
            }
            dbConn.pstmt.close();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
        try{
            dbConn.pstmt = dbConn.conn.prepareStatement("Select sum(bdPrice) from tblreceipt where date=? and paymentMethod=?");
            dbConn.pstmt.setString(1, String.valueOf(tdf.format(datePicker.getDate())));
            dbConn.pstmt.setString(2,"FOC");
            dbConn.rs = dbConn.pstmt.executeQuery();
            if (dbConn.rs.next()){
                lblDailyFoc.setText(String.valueOf(df.format(dbConn.rs.getDouble(1))));
            }else{
                lblDailyFoc.setText("0.000");
            }
            dbConn.pstmt.close();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
    
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
            displayDailySales();
            Map param = new HashMap();
            param.put("date", String.valueOf(dbConn.sdfDateGlobal.format(datePicker.getDate())));
            param.put("cardSales", lblDailyCard.getText());
            param.put("cashSales", lblDailyCash.getText());
            param.put("focData", lblDailyFoc.getText());
            saveAuditTrail("VIEWED SALES FOR: " + String.valueOf(tdf.format(datePicker.getDate())));
            try{
                dbConn.conn.close();
                Class.forName("com.mysql.jdbc.Driver");
                dbConn.conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbpos","root","root");
                JasperDesign jd = JRXmlLoader.load(new File("src\\Reports\\repSalesSummary.jrxml"));
                JasperReport jr = JasperCompileManager.compileReport(jd);
                JasperPrint jp = JasperFillManager.fillReport(jr, param,dbConn.conn);
                JasperViewer.viewReport(jp,false);
            }catch(ClassNotFoundException | SQLException | JRException e){
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
    }//GEN-LAST:event_jButton3ActionPerformed
    private void displaySalesRange(){
        try{
            dbConn.pstmt = dbConn.conn.prepareStatement("SELECT sum(bdPrice) from tblreceipt where "
                    + "date between ? and ? and paymentMethod=?");
            dbConn.pstmt.setString(1, String.valueOf(tdf.format(dateFrom.getDate())));
            dbConn.pstmt.setString(2, String.valueOf(tdf.format(dateTo.getDate())));
            dbConn.pstmt.setString(3,"CASH");
            dbConn.rs = dbConn.pstmt.executeQuery();
            if(dbConn.rs.next()){
                getCashSalesRange = String.valueOf(df.format(dbConn.rs.getDouble(1)));
            }else{
                getCashSalesRange = "0.000";
            }
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this,e.getMessage());
        }
        
            try{
            dbConn.pstmt = dbConn.conn.prepareStatement("SELECT sum(bdPrice) from tblreceipt where "
                    + "date between ? and ? and paymentMethod=?");
            dbConn.pstmt.setString(1, String.valueOf(tdf.format(dateFrom.getDate())));
            dbConn.pstmt.setString(2, String.valueOf(tdf.format(dateTo.getDate())));
            dbConn.pstmt.setString(3,"CARD");
            dbConn.rs = dbConn.pstmt.executeQuery();
            if(dbConn.rs.next()){
                getCardSalesRange = String.valueOf(df.format(dbConn.rs.getDouble(1)));
            }else{
                getCardSalesRange = "0.000";
            }
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this,e.getMessage());
        }
            
            try{
            dbConn.pstmt = dbConn.conn.prepareStatement("SELECT sum(bdPrice) from tblreceipt where "
                    + "date between ? and ? and paymentMethod=?");
            dbConn.pstmt.setString(1, String.valueOf(tdf.format(dateFrom.getDate())));
            dbConn.pstmt.setString(2, String.valueOf(tdf.format(dateTo.getDate())));
            dbConn.pstmt.setString(3,"FOC");
            dbConn.rs = dbConn.pstmt.executeQuery();
            if(dbConn.rs.next()){
                getFocRange= String.valueOf(df.format(dbConn.rs.getDouble(1)));
            }else{
                getFocRange = "0.000";
            }
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this,e.getMessage());
        }
    }
    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
            displaySalesRange();
            Map param = new HashMap();
            param.put("dateFrom", String.valueOf(tdf.format(dateFrom.getDate())));
            param.put("dateTo", String.valueOf(tdf.format(dateTo.getDate())));
            param.put("displayCard",getCardSalesRange);
            param.put("displayCash",getCashSalesRange);
            param.put("focData",getFocRange);
            saveAuditTrail("VIEWED SALES FROM " + String.valueOf(tdf.format(dateFrom.getDate()))
            +" TO" + String.valueOf(tdf.format(dateTo.getDate())));
            try{
                dbConn.conn.close();
                Class.forName("com.mysql.jdbc.Driver");
                dbConn.conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbpos","root","root");
                JasperDesign jd = JRXmlLoader.load(new File("src\\Reports\\repSalesDates.jrxml"));
                JasperReport jr = JasperCompileManager.compileReport(jd);
                JasperPrint jp = JasperFillManager.fillReport(jr, param,dbConn.conn);
                JasperViewer.viewReport(jp,false);
            }catch(ClassNotFoundException | SQLException | JRException e){
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
    }//GEN-LAST:event_jButton4ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(frmReceiptTable.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frmReceiptTable.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frmReceiptTable.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frmReceiptTable.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frmReceiptTable().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser dateFrom;
    private com.toedter.calendar.JDateChooser datePicker;
    private com.toedter.calendar.JDateChooser dateTo;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblAmountPaid;
    private javax.swing.JLabel lblChange;
    private javax.swing.JLabel lblDailyCard;
    private javax.swing.JLabel lblDailyCash;
    private javax.swing.JLabel lblDailyFoc;
    private javax.swing.JLabel lblTotalAmount;
    private javax.swing.JTable tableReceipt;
    private javax.swing.JTable tableStock;
    // End of variables declaration//GEN-END:variables
}
