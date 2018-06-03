package main;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.Connection;
import java.util.Date;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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

public class frmReports extends javax.swing.JFrame {
//ResultSet dbConn.rs;
//Connection dbConn.conn;
//PreparedStatement dbConn.pstmt;
int getMaxExpenseID;
String getTransactionId,getPOStatus;
DecimalFormat dfo = new DecimalFormat("0.000");
DecimalFormat df = new DecimalFormat("0.000");
SimpleDateFormat tdf = new SimpleDateFormat("dd/M/YYYY");
SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy");
String getCardSalesRange,getCashSalesRange,getFocRange;
int getMaxAuditID;
SimpleDateFormat sdfAudit = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
dbConnection dbConn = new dbConnection();
    public frmReports() {
        initComponents();
        dbConn.doConnect();
        fillTablepanReceipt();
        fillComboSupplier();    
        fillComboLPO();
        fillTableProduct();
        fillProductPrint();
        sortTableExpense();
        sortTablesIncome();
        sortTableLPOPay();
        fillAuditTrail();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(frmReports.DISPOSE_ON_CLOSE);
        //listenedbConn.rs
        txtProductName.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {listenToTextItem();}
            @Override
            public void removeUpdate(DocumentEvent e) {listenToTextItem();}
            @Override
            public void changedUpdate(DocumentEvent e) {listenToTextItem();}
        });
        cmbSupplier.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {listenComboSupplier();}
        });
    }
    private void getNextAuditID(){
        try{
            dbConn.pstmt = dbConn.conn.prepareStatement("SELECT * from tblAuditTrail order by at_id DESC LIMIT 1");
            dbConn.rs = dbConn.pstmt.executeQuery();
            if (dbConn.rs.next()){
                getMaxAuditID = dbConn.rs.getInt(1);
                getMaxAuditID++;
            }else{
                getMaxAuditID = 1;
            }
        }catch(SQLException e){
            e.getMessage();
        }
    }
    private void saveAuditTrail(String getTransaction){
        getNextAuditID();
        try{
            String saveAuditQuery = "INSERT into tblAuditTrail (at_id,at_transaction,at_dateandTime,at_user)"
                    + "values(?,?,?,?)";
            dbConn.pstmt = dbConn.conn.prepareStatement(saveAuditQuery);
            dbConn.pstmt.setInt(1, getMaxAuditID);
            dbConn.pstmt.setString(2,getTransaction);
            Date getDateAudit = new Date();
            dbConn.pstmt.setString(3,String.valueOf(sdfAudit.format(getDateAudit)));
            dbConn.pstmt.setString(4,frmLogin.showUserName);
            dbConn.pstmt.execute();
            dbConn.pstmt.close();
        }catch(SQLException e){
            e.getMessage();
        }
    }
    private void fillComboLPO(){
        cmbLPOStatus.removeAllItems();
        cmbLPOStatus.addItem("ORDERED");
        cmbLPOStatus.addItem("RECEIVED");
        cmbLPOStatus.addItem("PAID");
        
    }
    private void fillAuditTrail(){
        try{
            dbConn.pstmt = dbConn.conn.prepareStatement("Select * from tblAuditTrail order by at_dateandTime DESC");
            dbConn.rs = dbConn.pstmt.executeQuery();
            tblAuditTrail.setModel(DbUtils.resultSetToTableModel(dbConn.rs));
            dbConn.pstmt.close();
            tblAuditTrail.getColumnModel().getColumn(0).setHeaderValue("ID");
            tblAuditTrail.getColumnModel().getColumn(1).setHeaderValue("TRANSACTION");
            tblAuditTrail.getColumnModel().getColumn(2).setHeaderValue("DATE AND TIME");
            tblAuditTrail.getColumnModel().getColumn(3).setHeaderValue("USER");
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
    private void sortTablesIncome(){
        tblSalesIncome.getColumnModel().getColumn(0).setHeaderValue("ID");
        tblSalesIncome.getColumnModel().getColumn(1).setHeaderValue("ITEM");
        tblSalesIncome.getColumnModel().getColumn(2).setHeaderValue("PRICE");
        tblSalesIncome.getColumnModel().getColumn(3).setHeaderValue("DATE");
        tblSalesIncome.getColumnModel().getColumn(4).setHeaderValue("METHOD");
        
        tblExpenseIncome.getColumnModel().getColumn(0).setHeaderValue("ID");
        tblExpenseIncome.getColumnModel().getColumn(1).setHeaderValue("AMOUNT");
        tblExpenseIncome.getColumnModel().getColumn(2).setHeaderValue("COMMENT");
        tblExpenseIncome.getColumnModel().getColumn(3).setHeaderValue("DATE");
    }
    private void fillTablepanReceipt(){
    try{
        dbConn.pstmt = dbConn.conn.prepareStatement("Select transactionId, productName,quantity,paymentMethod,date,time "
                + "from tblReceipt order by transactionId DESC");
        dbConn.rs = dbConn.pstmt.executeQuery();
        tableReceipt.setModel(DbUtils.resultSetToTableModel(dbConn.rs));
        tableReceipt.getColumnModel().getColumn(0).setHeaderValue("ID");
        tableReceipt.getColumnModel().getColumn(1).setHeaderValue("PRODUCT");
        tableReceipt.getColumnModel().getColumn(2).setHeaderValue("QTY");
        tableReceipt.getColumnModel().getColumn(3).setHeaderValue("METHOD");
        tableReceipt.getColumnModel().getColumn(4).setHeaderValue("DATE");
        tableReceipt.getColumnModel().getColumn(5).setHeaderValue("TIME");
    }catch(SQLException e){
        e.getMessage();
    }
}
//    private void doConnect(){
//    try{
//        Class.forName("com.mysql.jdbc.Driver");
//        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbpos","root","root");
//    }catch(SQLException | ClassNotFoundException e){
//        JOptionPane.showMessageDialog(this, e.getMessage());
//    }
//    }
    
    //panProduct
    private void fillProductPrint(){
        cmbProductPrint.removeAllItems();
        cmbProductPrint.addItem("PRINT BY SUPPLIER");
        cmbProductPrint.addItem("PRINT BY CATEGORY");
    }
    private void fillTableProduct(){
     try{
            dbConn.pstmt = dbConn.conn.prepareStatement("SELECT productId,productName,"
                    + "stockOnHand,supplier,supplierPrice from tblProduct order by productName");
            dbConn.rs = dbConn.pstmt.executeQuery();
            tblProduct.setModel(DbUtils.resultSetToTableModel(dbConn.rs));
            tblProduct.getColumnModel().getColumn(0).setHeaderValue("SKU");
            tblProduct.getColumnModel().getColumn(1).setHeaderValue("ITEM");
            tblProduct.getColumnModel().getColumn(2).setHeaderValue("SOH");
            tblProduct.getColumnModel().getColumn(3).setHeaderValue("SUPPLIER");
            tblProduct.getColumnModel().getColumn(4).setHeaderValue("SUP PRICE");
            dbConn.pstmt.close();
        }catch(SQLException e){
            e.getMessage();
        }
    }
    private void listenComboSupplier(){
        try{
            dbConn.pstmt = dbConn.conn.prepareStatement("SELECT productId,productName,"
                    + "stockOnHand,supplier,supplierPrice from tblProduct where supplier = ?");
            dbConn.pstmt.setString(1,cmbSupplier.getSelectedItem().toString());
            dbConn.rs = dbConn.pstmt.executeQuery();
            tblProduct.setModel(DbUtils.resultSetToTableModel(dbConn.rs));
            dbConn.pstmt.close();
            tblProduct.getColumnModel().getColumn(0).setHeaderValue("SKU");
            tblProduct.getColumnModel().getColumn(1).setHeaderValue("ITEM");
            tblProduct.getColumnModel().getColumn(2).setHeaderValue("SOH");
            tblProduct.getColumnModel().getColumn(3).setHeaderValue("SUPPLIER");
            tblProduct.getColumnModel().getColumn(4).setHeaderValue("SUP PRICE");
        }catch(SQLException e){
            e.getMessage();
        }    
    }
    private void listenToTextItem(){
        try{
            dbConn.pstmt = dbConn.conn.prepareStatement("SELECT productId,productName,"
                    + "stockOnHand,supplier,supplierPrice from tblProduct where productName like ?");
            dbConn.pstmt.setString(1, "%"+ txtProductName.getText() + "%");
            dbConn.rs = dbConn.pstmt.executeQuery();
            if (dbConn.rs.next()){
                cmbSupplier.setSelectedItem(dbConn.rs.getString("supplier"));
            }
            dbConn.pstmt.close();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }  
    }
    private void fillComboSupplier(){
        cmbSupplier.removeAllItems();
        try{
            dbConn.pstmt = dbConn.conn.prepareStatement("select sm_name from tblSupplierMaster order by sm_name");
            dbConn.rs = dbConn.pstmt.executeQuery();
            while (dbConn.rs.next()){
                cmbSupplier.addItem(dbConn.rs.getString("sm_name"));
            }
            dbConn.pstmt.close();
            cmbSupplier.setSelectedIndex(-1);
        }catch(SQLException e){
            e.getMessage();
        }    
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        panProduct = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtProductName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        cmbSupplier = new javax.swing.JComboBox<>();
        jSeparator1 = new javax.swing.JSeparator();
        jButton1 = new javax.swing.JButton();
        cmbProductPrint = new javax.swing.JComboBox<>();
        jButton4 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblProduct = new javax.swing.JTable();
        panReceipts = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableReceipt = new javax.swing.JTable();
        jPanel13 = new javax.swing.JPanel();
        lblChange = new javax.swing.JLabel();
        lblAmountPaid = new javax.swing.JLabel();
        lblTotalAmount = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        panSales = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        datePicker = new com.toedter.calendar.JDateChooser();
        jButton3 = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblDailyCard = new javax.swing.JLabel();
        lblDailyCash = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lblDailyFoc = new javax.swing.JLabel();
        jPanel17 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        dateTo = new com.toedter.calendar.JDateChooser();
        dateFrom = new com.toedter.calendar.JDateChooser();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        panExpense = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jPanel18 = new javax.swing.JPanel();
        jPanel19 = new javax.swing.JPanel();
        dateFromExpense = new com.toedter.calendar.JDateChooser();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        dateToExpense = new com.toedter.calendar.JDateChooser();
        btnPrintExpense = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        btnShowAllExpense = new javax.swing.JButton();
        btnViewExpense = new javax.swing.JButton();
        jLabel27 = new javax.swing.JLabel();
        lblTotalExpenseShow = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblExpense = new javax.swing.JTable();
        panIncome = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jPanel20 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblSalesIncome = new javax.swing.JTable();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jPanel21 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblExpenseIncome = new javax.swing.JTable();
        jSeparator4 = new javax.swing.JSeparator();
        dateFromIncome = new com.toedter.calendar.JDateChooser();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        dateToIncome = new com.toedter.calendar.JDateChooser();
        btnViewIncome = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JSeparator();
        jLabel17 = new javax.swing.JLabel();
        lblTotalSalesIncome = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        lblTotalFOCIncome = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        lblTotalExpenseIncome = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        lblNetIncome = new javax.swing.JLabel();
        jSeparator6 = new javax.swing.JSeparator();
        btnPrintIncome = new javax.swing.JButton();
        jPanel22 = new javax.swing.JPanel();
        jPanel23 = new javax.swing.JPanel();
        jPanel24 = new javax.swing.JPanel();
        jPanel25 = new javax.swing.JPanel();
        dateFromLPO = new com.toedter.calendar.JDateChooser();
        jLabel18 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jLabel26 = new javax.swing.JLabel();
        dateToLPO = new com.toedter.calendar.JDateChooser();
        jscrollpane = new javax.swing.JScrollPane();
        tblLPOPayment = new javax.swing.JTable();
        jPanel26 = new javax.swing.JPanel();
        jPanel27 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        txtAmountPaid = new javax.swing.JTextField();
        lblTotalAmountLPOPayment = new javax.swing.JLabel();
        lblSupplier = new javax.swing.JLabel();
        btnProcessLPOPayment = new javax.swing.JButton();
        jLabel25 = new javax.swing.JLabel();
        lblLPONumber = new javax.swing.JLabel();
        btnPrintALLLPO = new javax.swing.JButton();
        jSeparator7 = new javax.swing.JSeparator();
        cmbLPOStatus = new javax.swing.JComboBox<>();
        btnPrintLPOStatus = new javax.swing.JButton();
        jLabel28 = new javax.swing.JLabel();
        jPanel28 = new javax.swing.JPanel();
        jPanel31 = new javax.swing.JPanel();
        jPanel29 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tblAuditTrail = new javax.swing.JTable();
        jPanel30 = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        dateFromAudit = new com.toedter.calendar.JDateChooser();
        jLabel30 = new javax.swing.JLabel();
        dateToAudit = new com.toedter.calendar.JDateChooser();
        btnSearchAudit = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        jSeparator8 = new javax.swing.JSeparator();
        btnAuditTrail = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(214, 214, 194));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTabbedPane1.setBackground(new java.awt.Color(214, 214, 194));
        jTabbedPane1.setForeground(new java.awt.Color(0, 0, 0));

        panProduct.setBackground(new java.awt.Color(214, 214, 194));
        panProduct.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel11.setBackground(new java.awt.Color(214, 214, 194));
        jPanel11.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel11.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(214, 214, 194));
        jPanel2.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setBackground(new java.awt.Color(214, 214, 194));
        jPanel3.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("PRODUCT NAME:");
        jPanel3.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 110, 30));

        txtProductName.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtProductName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtProductNameActionPerformed(evt);
            }
        });
        jPanel3.add(txtProductName, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 10, 180, 30));

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("SUPPLIER:");
        jPanel3.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 110, 30));

        cmbSupplier.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cmbSupplier.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel3.add(cmbSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 50, 180, 30));

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel3.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 0, 20, 110));

        jButton1.setBackground(new java.awt.Color(255, 255, 255));
        jButton1.setForeground(new java.awt.Color(0, 0, 0));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/printericon.png"))); // NOI18N
        jButton1.setText("PRODUCT REPORT");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 50, 190, 50));

        cmbProductPrint.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel3.add(cmbProductPrint, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 10, 200, 30));

        jButton4.setBackground(new java.awt.Color(255, 255, 255));
        jButton4.setForeground(new java.awt.Color(0, 0, 0));
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/viewIcon.png"))); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 10, 60, 70));

        jPanel2.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 650, 110));

        jPanel11.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 670, 130));

        jPanel4.setBackground(new java.awt.Color(214, 214, 194));
        jPanel4.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tblProduct.setBackground(new java.awt.Color(214, 214, 194));
        tblProduct.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblProduct);

        jPanel4.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 630, 310));

        jPanel11.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 670, 340));

        panProduct.add(jPanel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 11, 694, 500));

        jTabbedPane1.addTab("PRODUCTS", panProduct);

        panReceipts.setBackground(new java.awt.Color(214, 214, 194));

        jPanel6.setBackground(new java.awt.Color(214, 214, 194));
        jPanel6.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel10.setBackground(new java.awt.Color(214, 214, 194));
        jPanel10.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel10.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel12.setBackground(new java.awt.Color(214, 214, 194));
        jPanel12.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel12.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

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
        jScrollPane2.setViewportView(tableReceipt);

        jPanel12.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 630, 260));

        jPanel13.setBackground(new java.awt.Color(214, 214, 194));
        jPanel13.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel13.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblChange.setFont(new java.awt.Font("Tahoma", 2, 14)); // NOI18N
        lblChange.setForeground(new java.awt.Color(0, 0, 0));
        lblChange.setText("0.000");
        jPanel13.add(lblChange, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 80, 100, 20));

        lblAmountPaid.setFont(new java.awt.Font("Tahoma", 2, 14)); // NOI18N
        lblAmountPaid.setForeground(new java.awt.Color(0, 0, 0));
        lblAmountPaid.setText("0.000");
        jPanel13.add(lblAmountPaid, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 40, 100, 20));

        lblTotalAmount.setFont(new java.awt.Font("Tahoma", 2, 14)); // NOI18N
        lblTotalAmount.setForeground(new java.awt.Color(0, 0, 0));
        lblTotalAmount.setText("0.000");
        jPanel13.add(lblTotalAmount, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 0, 100, 20));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("TOTAL AMOUNT:");
        jPanel13.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 150, 20));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("AMOUNT PAID:");
        jPanel13.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 150, 20));

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("CHANGE:");
        jPanel13.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 150, 20));

        jButton2.setBackground(new java.awt.Color(255, 255, 255));
        jButton2.setForeground(new java.awt.Color(0, 0, 0));
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/printericon.png"))); // NOI18N
        jButton2.setText("REPRINT RECEIPT");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel13.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 260, 40));

        jPanel12.add(jPanel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 290, 280, 180));

        jPanel10.add(jPanel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 650, 490));

        jPanel6.add(jPanel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 670, 510));

        javax.swing.GroupLayout panReceiptsLayout = new javax.swing.GroupLayout(panReceipts);
        panReceipts.setLayout(panReceiptsLayout);
        panReceiptsLayout.setHorizontalGroup(
            panReceiptsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panReceiptsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 695, Short.MAX_VALUE)
                .addContainerGap())
        );
        panReceiptsLayout.setVerticalGroup(
            panReceiptsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panReceiptsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 529, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("RECEIPTS", panReceipts);

        panSales.setBackground(new java.awt.Color(214, 214, 194));
        panSales.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));

        jPanel7.setBackground(new java.awt.Color(214, 214, 194));
        jPanel7.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel16.setBackground(new java.awt.Color(214, 214, 194));
        jPanel16.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel16.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel14.setBackground(new java.awt.Color(214, 214, 194));
        jPanel14.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel14.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        datePicker.setDateFormatString("MMM dd, yyyy");
        jPanel14.add(datePicker, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, 300, 40));

        jButton3.setBackground(new java.awt.Color(255, 255, 255));
        jButton3.setForeground(new java.awt.Color(0, 0, 0));
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/generateIcon.png"))); // NOI18N
        jButton3.setText("GENERATE SALES");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel14.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 300, 50));
        jPanel14.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 130, 350, 10));

        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("TOTAL CASH SALES:");
        jPanel14.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 180, -1));

        jLabel7.setForeground(new java.awt.Color(0, 0, 0));
        jLabel7.setText("TOTAL CARD SALES:");
        jPanel14.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 180, -1));

        lblDailyCard.setForeground(new java.awt.Color(0, 0, 0));
        lblDailyCard.setText("0.000");
        jPanel14.add(lblDailyCard, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 150, 130, -1));

        lblDailyCash.setForeground(new java.awt.Color(0, 0, 0));
        lblDailyCash.setText("0.000");
        jPanel14.add(lblDailyCash, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 170, 130, -1));

        jLabel8.setForeground(new java.awt.Color(0, 0, 0));
        jLabel8.setText("TOTAL FOC:");
        jPanel14.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 190, 180, -1));

        lblDailyFoc.setForeground(new java.awt.Color(0, 0, 0));
        lblDailyFoc.setText("0.000");
        jPanel14.add(lblDailyFoc, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 190, 130, -1));

        jPanel16.add(jPanel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, 220));

        jPanel7.add(jPanel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 370, 240));

        jPanel17.setBackground(new java.awt.Color(214, 214, 194));
        jPanel17.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel17.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel15.setBackground(new java.awt.Color(214, 214, 194));
        jPanel15.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel15.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        dateTo.setDateFormatString("MMM dd, yyyy");
        jPanel15.add(dateTo, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 110, 200, 30));

        dateFrom.setDateFormatString("MMM dd, yyyy");
        jPanel15.add(dateFrom, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 30, 200, 30));

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 0, 0));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("TO");
        jPanel15.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 80, 110, 20));

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 0, 0));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("FROM");
        jPanel15.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 0, 110, 20));

        jButton5.setBackground(new java.awt.Color(255, 255, 255));
        jButton5.setForeground(new java.awt.Color(0, 0, 0));
        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/viewIcon.png"))); // NOI18N
        jButton5.setText("VIEW SALES");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jPanel15.add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 160, 250, 50));

        jPanel17.add(jPanel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 280, 220));

        jPanel7.add(jPanel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 10, 300, 240));

        javax.swing.GroupLayout panSalesLayout = new javax.swing.GroupLayout(panSales);
        panSales.setLayout(panSalesLayout);
        panSalesLayout.setHorizontalGroup(
            panSalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panSalesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, 702, Short.MAX_VALUE)
                .addContainerGap())
        );
        panSalesLayout.setVerticalGroup(
            panSalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panSalesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("SALES", panSales);

        panExpense.setBackground(new java.awt.Color(214, 214, 194));

        jPanel8.setBackground(new java.awt.Color(214, 214, 194));
        jPanel8.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel18.setBackground(new java.awt.Color(214, 214, 194));
        jPanel18.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel19.setBackground(new java.awt.Color(214, 214, 194));
        jPanel19.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel19.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        dateFromExpense.setDateFormatString("MMM dd, yyyy");
        jPanel19.add(dateFromExpense, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 10, 200, 30));

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 0, 0));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel11.setText("FROM");
        jPanel19.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 10, 80, 20));

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(0, 0, 0));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel12.setText("TO");
        jPanel19.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 50, 80, 20));

        dateToExpense.setDateFormatString("MMM dd, yyyy");
        jPanel19.add(dateToExpense, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 50, 200, 30));

        btnPrintExpense.setBackground(new java.awt.Color(255, 255, 255));
        btnPrintExpense.setForeground(new java.awt.Color(0, 0, 0));
        btnPrintExpense.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/printericon.png"))); // NOI18N
        btnPrintExpense.setText("PRINT EXPENSE");
        btnPrintExpense.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintExpenseActionPerformed(evt);
            }
        });
        jPanel19.add(btnPrintExpense, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 130, 230, 40));

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel19.add(jSeparator3, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 0, 30, 190));

        btnShowAllExpense.setBackground(new java.awt.Color(255, 255, 255));
        btnShowAllExpense.setForeground(new java.awt.Color(0, 0, 0));
        btnShowAllExpense.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/showIcon.png"))); // NOI18N
        btnShowAllExpense.setText("SHOW ALL");
        btnShowAllExpense.setHideActionText(true);
        btnShowAllExpense.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnShowAllExpense.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnShowAllExpense.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowAllExpenseActionPerformed(evt);
            }
        });
        jPanel19.add(btnShowAllExpense, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 90, 100, 80));

        btnViewExpense.setBackground(new java.awt.Color(255, 255, 255));
        btnViewExpense.setForeground(new java.awt.Color(0, 0, 0));
        btnViewExpense.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/viewIcon.png"))); // NOI18N
        btnViewExpense.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewExpenseActionPerformed(evt);
            }
        });
        jPanel19.add(btnViewExpense, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 90, 70, 50));

        jLabel27.setForeground(new java.awt.Color(0, 0, 0));
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setText("TOTAL EXPENSE:");
        jPanel19.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 40, 200, -1));

        lblTotalExpenseShow.setForeground(new java.awt.Color(0, 0, 0));
        lblTotalExpenseShow.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotalExpenseShow.setText("0.000");
        jPanel19.add(lblTotalExpenseShow, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 60, 200, -1));

        jPanel18.add(jPanel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 650, 190));

        tblExpense.setBackground(new java.awt.Color(214, 214, 194));
        tblExpense.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(tblExpense);

        jPanel18.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 220, 650, 260));

        jPanel8.add(jPanel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 670, 510));

        javax.swing.GroupLayout panExpenseLayout = new javax.swing.GroupLayout(panExpense);
        panExpense.setLayout(panExpenseLayout);
        panExpenseLayout.setHorizontalGroup(
            panExpenseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panExpenseLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, 695, Short.MAX_VALUE)
                .addContainerGap())
        );
        panExpenseLayout.setVerticalGroup(
            panExpenseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panExpenseLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, 554, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("EXPENSE", panExpense);

        panIncome.setBackground(new java.awt.Color(214, 214, 194));

        jPanel9.setBackground(new java.awt.Color(214, 214, 194));
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel20.setBackground(new java.awt.Color(214, 214, 194));
        jPanel20.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel20.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tblSalesIncome.setBackground(new java.awt.Color(214, 214, 194));
        tblSalesIncome.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        tblSalesIncome.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5"
            }
        ));
        jScrollPane4.setViewportView(tblSalesIncome);

        jPanel20.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 660, 120));

        jPanel9.add(jPanel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 680, 140));

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(0, 0, 0));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("SALES");
        jPanel9.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 680, -1));

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(0, 0, 0));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("EXPENSE");
        jPanel9.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 200, 680, -1));

        jPanel21.setBackground(new java.awt.Color(214, 214, 194));
        jPanel21.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel21.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tblExpenseIncome.setBackground(new java.awt.Color(214, 214, 194));
        tblExpenseIncome.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        tblExpenseIncome.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane5.setViewportView(tblExpenseIncome);

        jPanel21.add(jScrollPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 660, 120));

        jPanel9.add(jPanel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 240, 680, 140));
        jPanel9.add(jSeparator4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 400, 680, 10));

        dateFromIncome.setDateFormatString("MMM dd, yyyy");
        jPanel9.add(dateFromIncome, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 430, 200, 30));

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(0, 0, 0));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("FROM");
        jPanel9.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 410, 110, 10));

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(0, 0, 0));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("TO");
        jPanel9.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 460, 110, 20));

        dateToIncome.setDateFormatString("MMM dd, yyyy");
        jPanel9.add(dateToIncome, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 480, 200, 30));

        btnViewIncome.setBackground(new java.awt.Color(255, 255, 255));
        btnViewIncome.setForeground(new java.awt.Color(0, 0, 0));
        btnViewIncome.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/viewIcon.png"))); // NOI18N
        btnViewIncome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewIncomeActionPerformed(evt);
            }
        });
        jPanel9.add(btnViewIncome, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 450, 60, 50));

        jSeparator5.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel9.add(jSeparator5, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 400, 10, 130));

        jLabel17.setForeground(new java.awt.Color(0, 0, 0));
        jLabel17.setText("TOTAL SALES:");
        jPanel9.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 410, 100, -1));

        lblTotalSalesIncome.setForeground(new java.awt.Color(0, 0, 0));
        lblTotalSalesIncome.setText("0.000");
        jPanel9.add(lblTotalSalesIncome, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 410, 80, -1));

        jLabel19.setForeground(new java.awt.Color(0, 0, 0));
        jLabel19.setText("TOTAL FOC:");
        jPanel9.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 430, 100, -1));

        lblTotalFOCIncome.setForeground(new java.awt.Color(0, 0, 0));
        lblTotalFOCIncome.setText("0.000");
        jPanel9.add(lblTotalFOCIncome, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 430, 80, -1));

        jLabel21.setForeground(new java.awt.Color(0, 0, 0));
        jLabel21.setText("TOTAL EXPENSE:");
        jPanel9.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 450, 100, -1));

        lblTotalExpenseIncome.setForeground(new java.awt.Color(0, 0, 0));
        lblTotalExpenseIncome.setText("0.000");
        jPanel9.add(lblTotalExpenseIncome, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 450, 80, -1));

        jLabel23.setForeground(new java.awt.Color(0, 0, 0));
        jLabel23.setText("NET INCOME:");
        jPanel9.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 480, 100, -1));

        lblNetIncome.setForeground(new java.awt.Color(0, 0, 0));
        lblNetIncome.setText("0.000");
        jPanel9.add(lblNetIncome, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 480, 80, -1));
        jPanel9.add(jSeparator6, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 470, 170, 10));

        btnPrintIncome.setBackground(new java.awt.Color(255, 255, 255));
        btnPrintIncome.setForeground(new java.awt.Color(0, 0, 0));
        btnPrintIncome.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/printericon.png"))); // NOI18N
        btnPrintIncome.setText("PRINT");
        btnPrintIncome.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPrintIncome.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPrintIncome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintIncomeActionPerformed(evt);
            }
        });
        jPanel9.add(btnPrintIncome, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 420, 70, 70));

        javax.swing.GroupLayout panIncomeLayout = new javax.swing.GroupLayout(panIncome);
        panIncome.setLayout(panIncomeLayout);
        panIncomeLayout.setHorizontalGroup(
            panIncomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panIncomeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, 695, Short.MAX_VALUE)
                .addContainerGap())
        );
        panIncomeLayout.setVerticalGroup(
            panIncomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panIncomeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("INCOME", panIncome);

        jPanel22.setBackground(new java.awt.Color(214, 214, 194));

        jPanel23.setBackground(new java.awt.Color(214, 214, 194));
        jPanel23.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel23.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel24.setBackground(new java.awt.Color(214, 214, 194));
        jPanel24.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel24.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel25.setBackground(new java.awt.Color(214, 214, 194));
        jPanel25.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel25.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        dateFromLPO.setDateFormatString("MMM dd, yyyy");
        jPanel25.add(dateFromLPO, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 170, 40));

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setForeground(new java.awt.Color(0, 0, 0));
        jLabel18.setText("FROM:");
        jPanel25.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 40, 30));

        jButton6.setBackground(new java.awt.Color(255, 255, 255));
        jButton6.setForeground(new java.awt.Color(0, 0, 0));
        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/SearchIcon.png"))); // NOI18N
        jButton6.setText("SEARCH");
        jButton6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jPanel25.add(jButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 10, 90, 70));

        jButton7.setBackground(new java.awt.Color(255, 255, 255));
        jButton7.setForeground(new java.awt.Color(0, 0, 0));
        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/refreshIcon.png"))); // NOI18N
        jButton7.setText("REFRESH");
        jButton7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton7.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        jPanel25.add(jButton7, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 10, 100, 70));

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setForeground(new java.awt.Color(0, 0, 0));
        jLabel26.setText("TO:");
        jPanel25.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 0, 30, 30));

        dateToLPO.setDateFormatString("MMM dd, yyyy");
        jPanel25.add(dateToLPO, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 40, 170, 40));

        jPanel24.add(jPanel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 660, 90));

        tblLPOPayment.setBackground(new java.awt.Color(214, 214, 194));
        tblLPOPayment.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5"
            }
        ));
        tblLPOPayment.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblLPOPaymentMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tblLPOPaymentMouseEntered(evt);
            }
        });
        jscrollpane.setViewportView(tblLPOPayment);

        jPanel24.add(jscrollpane, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, 660, 180));

        jPanel23.add(jPanel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 680, 310));

        jPanel26.setBackground(new java.awt.Color(214, 214, 194));
        jPanel26.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel27.setBackground(new java.awt.Color(214, 214, 194));
        jPanel27.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel27.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setForeground(new java.awt.Color(0, 0, 0));
        jLabel20.setText("SUPPLIER:");
        jPanel27.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 140, -1));

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setForeground(new java.awt.Color(0, 0, 0));
        jLabel22.setText("AMOUNT PAID:");
        jPanel27.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 100, 140, -1));

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setForeground(new java.awt.Color(0, 0, 0));
        jLabel24.setText("LPO TOTAL AMOUNT:");
        jPanel27.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, 140, -1));
        jPanel27.add(txtAmountPaid, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 100, 110, -1));

        lblTotalAmountLPOPayment.setBackground(new java.awt.Color(255, 255, 255));
        lblTotalAmountLPOPayment.setForeground(new java.awt.Color(0, 0, 0));
        lblTotalAmountLPOPayment.setText("N/A");
        jPanel27.add(lblTotalAmountLPOPayment, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 70, 110, -1));

        lblSupplier.setBackground(new java.awt.Color(255, 255, 255));
        lblSupplier.setForeground(new java.awt.Color(0, 0, 0));
        lblSupplier.setText("N/A");
        jPanel27.add(lblSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 40, 110, -1));

        btnProcessLPOPayment.setBackground(new java.awt.Color(255, 255, 255));
        btnProcessLPOPayment.setForeground(new java.awt.Color(0, 0, 0));
        btnProcessLPOPayment.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/processIcon.png"))); // NOI18N
        btnProcessLPOPayment.setText("PROCESS LPO PAYMENT");
        btnProcessLPOPayment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProcessLPOPaymentActionPerformed(evt);
            }
        });
        jPanel27.add(btnProcessLPOPayment, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 140, 280, 60));

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setForeground(new java.awt.Color(0, 0, 0));
        jLabel25.setText("LPO #:");
        jPanel27.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 140, -1));

        lblLPONumber.setBackground(new java.awt.Color(255, 255, 255));
        lblLPONumber.setForeground(new java.awt.Color(0, 0, 0));
        lblLPONumber.setText("N/A");
        jPanel27.add(lblLPONumber, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 10, 110, -1));

        btnPrintALLLPO.setBackground(new java.awt.Color(255, 255, 255));
        btnPrintALLLPO.setForeground(new java.awt.Color(0, 0, 0));
        btnPrintALLLPO.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/printericon.png"))); // NOI18N
        btnPrintALLLPO.setText("PRINT ALL");
        btnPrintALLLPO.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPrintALLLPO.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPrintALLLPO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintALLLPOActionPerformed(evt);
            }
        });
        jPanel27.add(btnPrintALLLPO, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 80, 100, 80));

        jSeparator7.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel27.add(jSeparator7, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 0, 20, 210));

        cmbLPOStatus.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cmbLPOStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel27.add(cmbLPOStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 30, 320, 30));

        btnPrintLPOStatus.setBackground(new java.awt.Color(255, 255, 255));
        btnPrintLPOStatus.setForeground(new java.awt.Color(0, 0, 0));
        btnPrintLPOStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/printericon.png"))); // NOI18N
        btnPrintLPOStatus.setText("PRINT PER STATUS");
        btnPrintLPOStatus.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPrintLPOStatus.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPrintLPOStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintLPOStatusActionPerformed(evt);
            }
        });
        jPanel27.add(btnPrintLPOStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 80, 140, 80));

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setForeground(new java.awt.Color(0, 0, 0));
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setText("LPO STATUS");
        jPanel27.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 10, 320, -1));

        jPanel26.add(jPanel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 660, 210));

        jPanel23.add(jPanel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 320, 680, 230));

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("LPO", jPanel22);

        jPanel28.setBackground(new java.awt.Color(214, 214, 194));
        jPanel28.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel28.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel31.setBackground(new java.awt.Color(214, 214, 194));
        jPanel31.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel29.setBackground(new java.awt.Color(214, 214, 194));
        jPanel29.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel29.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tblAuditTrail.setBackground(new java.awt.Color(214, 214, 194));
        tblAuditTrail.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        tblAuditTrail.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane6.setViewportView(tblAuditTrail);

        jPanel29.add(jScrollPane6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 147, 650, 360));

        jPanel30.setBackground(new java.awt.Color(214, 214, 194));
        jPanel30.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel30.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel29.setBackground(new java.awt.Color(255, 255, 255));
        jLabel29.setForeground(new java.awt.Color(0, 0, 0));
        jLabel29.setText("FROM:");
        jPanel30.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 40, 40));

        dateFromAudit.setDateFormatString("MMM dd, yyyy");
        jPanel30.add(dateFromAudit, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 10, 140, 40));

        jLabel30.setBackground(new java.awt.Color(255, 255, 255));
        jLabel30.setForeground(new java.awt.Color(0, 0, 0));
        jLabel30.setText("TO:");
        jPanel30.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 10, 30, 40));

        dateToAudit.setDateFormatString("MMM dd, yyyy");
        jPanel30.add(dateToAudit, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 10, 140, 40));

        btnSearchAudit.setBackground(new java.awt.Color(255, 255, 255));
        btnSearchAudit.setForeground(new java.awt.Color(0, 0, 0));
        btnSearchAudit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/SearchIcon.png"))); // NOI18N
        btnSearchAudit.setText("SEARCH");
        btnSearchAudit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchAuditActionPerformed(evt);
            }
        });
        jPanel30.add(btnSearchAudit, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 60, 140, 50));

        btnRefresh.setBackground(new java.awt.Color(255, 255, 255));
        btnRefresh.setForeground(new java.awt.Color(0, 0, 0));
        btnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/refreshIcon.png"))); // NOI18N
        btnRefresh.setText("REFRESH");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });
        jPanel30.add(btnRefresh, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 60, 140, 50));

        jSeparator8.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel30.add(jSeparator8, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 0, 20, 130));

        btnAuditTrail.setBackground(new java.awt.Color(255, 255, 255));
        btnAuditTrail.setForeground(new java.awt.Color(0, 0, 0));
        btnAuditTrail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/printericon.png"))); // NOI18N
        btnAuditTrail.setText("PRINT AUDIT TRAIL");
        btnAuditTrail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAuditTrailActionPerformed(evt);
            }
        });
        jPanel30.add(btnAuditTrail, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 20, 220, 80));

        jPanel29.add(jPanel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 650, 130));

        jPanel31.add(jPanel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 670, 520));

        jPanel28.add(jPanel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 690, 540));

        jTabbedPane1.addTab("AUDIT TRAIL", jPanel28);

        jPanel1.add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 720, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 747, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 598, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtProductNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProductNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProductNameActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (cmbProductPrint.getSelectedItem().equals("PRINT BY SUPPLIER")){
            Map param = new HashMap();
            saveAuditTrail("PRINTED PRODUCT REPORT BY SUPPLIER");
        try{
            dbConn.conn.close();
            Class.forName("com.mysql.jdbc.Driver");
            dbConn.conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbpos","root","root");
            JasperDesign jd = JRXmlLoader.load(new File("src\\Reports\\repProductBySupplier.jrxml"));
            JasperReport jr = JasperCompileManager.compileReport(jd);
            JasperPrint jp = JasperFillManager.fillReport(jr, param,dbConn.conn);
            JasperViewer.viewReport(jp,false);

        }catch(ClassNotFoundException | SQLException | JRException e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
        }else if (cmbProductPrint.getSelectedItem().equals("PRINT BY CATEGORY")){
            Map param = new HashMap();
            saveAuditTrail("PRINTED PRODUCT REPORT BY CATEGORY");
        try{
            dbConn.conn.close();
            Class.forName("com.mysql.jdbc.Driver");
            dbConn.conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbpos","root","root");
            JasperDesign jd = JRXmlLoader.load(new File("src\\Reports\\repProductByCategory.jrxml"));
            JasperReport jr = JasperCompileManager.compileReport(jd);
            JasperPrint jp = JasperFillManager.fillReport(jr, param,dbConn.conn);
            JasperViewer.viewReport(jp,false);

        }catch(ClassNotFoundException | SQLException | JRException e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void tableReceiptMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableReceiptMouseClicked
        int row = tableReceipt.getSelectedRow();
        int ba = tableReceipt.convertRowIndexToModel(row);
        getTransactionId = (tableReceipt.getModel().getValueAt(ba, 0)).toString();
        String tableQuery = "SELECT * from tblReceipt where transactionId=?";

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
            e.getMessage();
        }
    }//GEN-LAST:event_tableReceiptMouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
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

    }//GEN-LAST:event_jButton2ActionPerformed
    private void displayDailySales(){
        try{
            dbConn.pstmt = dbConn.conn.prepareStatement("Select sum(bdPrice) from tblReceipt where date=? and paymentMethod=?");
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
            dbConn.pstmt = dbConn.conn.prepareStatement("Select sum(bdPrice) from tblReceipt where date=? and paymentMethod=?");
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
            dbConn.pstmt = dbConn.conn.prepareStatement("Select sum(bdPrice) from tblReceipt where date=? and paymentMethod=?");
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
    private void displaySalesRange(){
        try{
            dbConn.pstmt = dbConn.conn.prepareStatement("SELECT sum(bdPrice) from tblReceipt where "
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
            e.getMessage();
        }
        
            try{
            dbConn.pstmt = dbConn.conn.prepareStatement("SELECT sum(bdPrice) from tblReceipt where "
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
            e.getMessage();
        }
            
            try{
            dbConn.pstmt = dbConn.conn.prepareStatement("SELECT sum(bdPrice) from tblReceipt where "
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
            e.getMessage();
        }
    }
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        displayDailySales();
        Map param = new HashMap();
        param.put("date", String.valueOf(tdf.format(datePicker.getDate())));
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
        //        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
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
    }//GEN-LAST:event_jButton5ActionPerformed

    private void btnShowAllExpenseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowAllExpenseActionPerformed
        try{
            dbConn.pstmt = dbConn.conn.prepareStatement("SELECT * from tblExpense order by ex_date DESC");
            dbConn.rs = dbConn.pstmt.executeQuery();
            tblExpense.setModel(DbUtils.resultSetToTableModel(dbConn.rs));
            dbConn.pstmt.close();
            sortTableExpense();
        }catch(SQLException e){
            e.getMessage();
        }
    }//GEN-LAST:event_btnShowAllExpenseActionPerformed
    private void sortTableExpense(){
        tblExpense.getColumnModel().getColumn(0).setHeaderValue("ID");
        tblExpense.getColumnModel().getColumn(1).setHeaderValue("AMOUNT");
        tblExpense.getColumnModel().getColumn(2).setHeaderValue("COMMENTS");
        tblExpense.getColumnModel().getColumn(3).setHeaderValue("DATE");
    }
    private void btnViewExpenseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewExpenseActionPerformed
        Date getFromExpense = dateFromExpense.getDate();
        Date getToExpense = dateToExpense.getDate();
        try{
            dbConn.pstmt = dbConn.conn.prepareStatement("Select * from tblExpense where ex_date between ? and ? order by ex_date DESC");
            dbConn.pstmt.setString(1, String.valueOf(sdfDate.format(getFromExpense)));
            dbConn.pstmt.setString(2, String.valueOf(sdfDate.format(getToExpense)));
            dbConn.rs = dbConn.pstmt.executeQuery();
            tblExpense.setModel(DbUtils.resultSetToTableModel(dbConn.rs));
            dbConn.pstmt.close();
            sortTableExpense();
        }catch(SQLException e){
            e.getMessage();
        }
        try{
            dbConn.pstmt = dbConn.conn.prepareStatement("SELECT sum(ex_amount) from tblExpense where "
                    + "ex_date between ? and ?");
            dbConn.pstmt.setString(1, String.valueOf(sdfDate.format(getFromExpense)));
            dbConn.pstmt.setString(2, String.valueOf(sdfDate.format(getToExpense)));
            dbConn.rs = dbConn.pstmt.executeQuery();
            if(dbConn.rs.next()){
                lblTotalExpenseShow.setText(df.format(Double.valueOf(dbConn.rs.getString(1))));
            }
            dbConn.pstmt.close();
        }catch(SQLException ex){
            ex.getMessage();
        }
    }//GEN-LAST:event_btnViewExpenseActionPerformed

    private void btnViewIncomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewIncomeActionPerformed
        Date getFromIncome = dateFromIncome.getDate();
        Date getToIncome = dateToIncome.getDate();
        saveAuditTrail("VIEWED INCOME FROM " + String.valueOf(tdf.format(dateFromIncome.getDate())) 
                + " TO" + String.valueOf(tdf.format(dateToIncome.getDate())));
        try{    
            String getSalesQuery = "Select sum(bdPrice) from tblReceipt where "
                    + "paymentMethod<>? and date between ? and ?";
            dbConn.pstmt = dbConn.conn.prepareStatement(getSalesQuery);
            dbConn.pstmt.setString(1,"FOC");
            dbConn.pstmt.setString(2, String.valueOf(tdf.format(getFromIncome)));
            dbConn.pstmt.setString(3, String.valueOf(tdf.format(getToIncome)));
            dbConn.rs = dbConn.pstmt.executeQuery();
            if (dbConn.rs.next()){
                lblTotalSalesIncome.setText(String.valueOf(df.format(dbConn.rs.getDouble(1))));
            }
            dbConn.pstmt.close();
                try{
                    dbConn.pstmt = dbConn.conn.prepareStatement("select transactionId,productName,bdPrice,date,paymentMethod "
                            + "from tblReceipt where date between ? and ? order by date DESC");
                    dbConn.pstmt.setString(1, String.valueOf(tdf.format(getFromIncome)));
                    dbConn.pstmt.setString(2, String.valueOf(tdf.format(getToIncome)));
                    dbConn.rs = dbConn.pstmt.executeQuery();
                    tblSalesIncome.setModel(DbUtils.resultSetToTableModel(dbConn.rs));
                    dbConn.pstmt.close();
                    sortTablesIncome();
                }catch(SQLException ex){
                    ex.getMessage();
                }
        }catch(SQLException e){
            e.getMessage();
        }
        
        //getFOC
        try{
            dbConn.pstmt = dbConn.conn.prepareStatement("Select sum(bdPrice) from tblReceipt where "
                    + "paymentMethod=? and date between ? and ?");
            dbConn.pstmt.setString(1,"FOC");
            dbConn.pstmt.setString(2, String.valueOf(tdf.format(getFromIncome)));
            dbConn.pstmt.setString(3, String.valueOf(tdf.format(getToIncome)));
            dbConn.rs = dbConn.pstmt.executeQuery();
            if (dbConn.rs.next()){
                lblTotalFOCIncome.setText(String.valueOf(df.format(dbConn.rs.getDouble(1))));
            }
            dbConn.pstmt.close();
        }catch(SQLException ex){
            ex.getMessage();
        }
        
        
        //getExpense
        try{
            dbConn.pstmt = dbConn.conn.prepareStatement("Select sum(ex_amount) from tblExpense where ex_date between ? and ?");
            dbConn.pstmt.setString(1, String.valueOf(tdf.format(getFromIncome)));
            dbConn.pstmt.setString(2, String.valueOf(tdf.format(getToIncome)));
            dbConn.rs = dbConn.pstmt.executeQuery();
            if (dbConn.rs.next()){
                lblTotalExpenseIncome.setText(String.valueOf(df.format(dbConn.rs.getDouble(1))));
            }
            dbConn.pstmt.close();
                try{
                    dbConn.pstmt = dbConn.conn.prepareStatement("select * from tblExpense where ex_date between ? and ? "
                            + "order by ex_date DESC");
                    dbConn.pstmt.setString(1, String.valueOf(tdf.format(getFromIncome)));
                    dbConn.pstmt.setString(2, String.valueOf(tdf.format(getToIncome)));
                    dbConn.rs = dbConn.pstmt.executeQuery();
                    tblExpenseIncome.setModel(DbUtils.resultSetToTableModel(dbConn.rs));
                    dbConn.pstmt.close();
                    sortTablesIncome();
                }catch(SQLException ex){
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                }
        }catch(SQLException e){
           JOptionPane.showMessageDialog(this, e.getMessage());
        }
        double showNetIncome = Double.valueOf(lblTotalSalesIncome.getText())- Double.valueOf(lblTotalExpenseIncome.getText());
        lblNetIncome.setText(String.valueOf(df.format(showNetIncome)));
    }//GEN-LAST:event_btnViewIncomeActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        
        
    }//GEN-LAST:event_jButton4ActionPerformed
    private void sortTableLPOPay(){
        tblLPOPayment.getColumnModel().getColumn(0).setHeaderValue("LPO");
        tblLPOPayment.getColumnModel().getColumn(1).setHeaderValue("SUPPLIER");
        tblLPOPayment.getColumnModel().getColumn(2).setHeaderValue("RECEIVED");
        tblLPOPayment.getColumnModel().getColumn(3).setHeaderValue("AMOUNT");
        tblLPOPayment.getColumnModel().getColumn(4).setHeaderValue("DATE");
    }
    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        Date getDateFromLPO = dateFromLPO.getDate();
        Date getDateToLPO = dateToLPO.getDate();
        try{
            dbConn.pstmt = dbConn.conn.prepareStatement("SELECT po_Id,po_supplier,po_qtyReceived,po_payableAmount, po_lpoDate from "
                + "tblPurchaseOrder where po_lpoDate between ? and ?");
            dbConn.pstmt.setString(1,String.valueOf(sdfDate.format(getDateFromLPO)));
            dbConn.pstmt.setString(2,String.valueOf(sdfDate.format(getDateToLPO)));
            dbConn.rs = dbConn.pstmt.executeQuery();
            tblLPOPayment.setModel(DbUtils.resultSetToTableModel(dbConn.rs));
            dbConn.pstmt.close();
            sortTableLPOPay();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_jButton6ActionPerformed
    private void fillLPOPayment(){
        try{
            dbConn.pstmt = dbConn.conn.prepareStatement("SELECT po_Id,po_supplier,po_qtyReceived,po_payableAmount,po_lpoDate from tblPurchaseOrder"
                    + " order by po_lpoDate");
            dbConn.rs = dbConn.pstmt.executeQuery();
            tblLPOPayment.setModel(DbUtils.resultSetToTableModel(dbConn.rs));
            dbConn.pstmt.close();
            tblLPOPayment.getColumnModel().getColumn(0).setHeaderValue("LPO");
            tblLPOPayment.getColumnModel().getColumn(1).setHeaderValue("SUPPLIER");
            tblLPOPayment.getColumnModel().getColumn(2).setHeaderValue("RECEIVED");
            tblLPOPayment.getColumnModel().getColumn(3).setHeaderValue("AMOUNT");
            tblLPOPayment.getColumnModel().getColumn(4).setHeaderValue("DATE");
        }catch(SQLException e){
            e.getMessage();
        }
    }
    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        fillLPOPayment();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void tblLPOPaymentMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblLPOPaymentMouseClicked
        int row = tblLPOPayment.getSelectedRow();
        int ba = tblLPOPayment.convertRowIndexToModel(row);
        String tblClick = tblLPOPayment.getModel().getValueAt(ba, 0).toString();
        String fetchData = "SELECT * from tblPurchaseOrder where po_Id=?";
        try{
            dbConn.pstmt  = dbConn.conn.prepareStatement(fetchData);
            dbConn.pstmt.setInt(1, Integer.valueOf(tblClick));
            dbConn.rs = dbConn.pstmt.executeQuery();
            if (dbConn.rs.next()){
                lblLPONumber.setText(String.valueOf(dbConn.rs.getInt("po_Id")));
                lblSupplier.setText(dbConn.rs.getString("po_supplier"));
                getPOStatus = dbConn.rs.getString("po_status");
                txtAmountPaid.requestFocus();
            }
            dbConn.pstmt.close();
            dbConn.rs.close();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
        
        try{
            String fetchDataSum = "SELECT sum(po_payableAmount) from tblPurchaseOrder where po_Id=?";
            dbConn.pstmt = dbConn.conn.prepareStatement(fetchDataSum);
            dbConn.pstmt.setInt(1, Integer.valueOf(tblClick));
            dbConn.rs = dbConn.pstmt.executeQuery();
            if (dbConn.rs.next()){
                lblTotalAmountLPOPayment.setText(dbConn.rs.getString(1));
                double getTotalAmountPayment = Double.valueOf(lblTotalAmountLPOPayment.getText());
                lblTotalAmountLPOPayment.setText(dfo.format(getTotalAmountPayment));
            }
            dbConn.pstmt.close();
        }catch(SQLException ex){
            ex.getMessage();
        }
    }//GEN-LAST:event_tblLPOPaymentMouseClicked

    private void tblLPOPaymentMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblLPOPaymentMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_tblLPOPaymentMouseEntered
    private void getNextExpense(){
            try{
                dbConn.pstmt  = dbConn.conn.prepareStatement("SELECT ex_id from tblExpense order by ex_id DESC LIMIT 1");
                dbConn.rs = dbConn.pstmt.executeQuery();
                if(dbConn.rs.next()){
                    getMaxExpenseID = dbConn.rs.getInt(1);
                    getMaxExpenseID++;
                }else{
                    getMaxExpenseID=1;
                }
            }catch(SQLException e){
                e.getMessage();
            }
    }
    private void btnProcessLPOPaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProcessLPOPaymentActionPerformed
        if (getPOStatus.equals("PAID")){
            JOptionPane.showMessageDialog(this, "LPO IS PAID ALREADY","LPO PAID",JOptionPane.WARNING_MESSAGE);
        }else{
            try{
                dbConn.pstmt = dbConn.conn.prepareStatement("update tblPurchaseOrder set po_status=?, po_paidAmount=? where po_Id=?");
                dbConn.pstmt.setString(1,"PAID");
                dbConn.pstmt.setDouble(2,Double.valueOf(txtAmountPaid.getText()));
                dbConn.pstmt.setInt(3,Integer.valueOf(lblLPONumber.getText()));
                dbConn.pstmt.executeUpdate();
                dbConn.pstmt.close();
                JOptionPane.showMessageDialog(this, "LPO PAYMENT PROCESSED");
                saveAuditTrail("PROCESSED LPO PAYMENT FOR #: " + lblLPONumber.getText());
            }catch(SQLException e){
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
            
            //add to expense
            try{
                getNextExpense();
                dbConn.pstmt = dbConn.conn.prepareStatement("INSERT INTO tblExpense(ex_id,ex_amount,ex_comment,ex_date)"
                    + "values(?,?,?,?)");
                dbConn.pstmt.setInt(1, getMaxExpenseID);
                dbConn.pstmt.setDouble(2,Double.valueOf(txtAmountPaid.getText()));
                dbConn.pstmt.setString(3,"FOR LPO #: " + lblLPONumber.getText());
                Date dateToday = new Date();
                dbConn.pstmt.setString(4,String.valueOf(sdfDate.format(dateToday)));
                dbConn.pstmt.execute();
                dbConn.pstmt.close();
                lblLPONumber.setText("");
                lblTotalAmountLPOPayment.setText("");
                lblSupplier.setText("");
            }catch(SQLException e){
                //JOptionPane.showMessageDialog(this, "ADD TO EXPENSE ERROR","EXPENSE MASTER",JOptionPane.ERROR_MESSAGE);
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }

    }//GEN-LAST:event_btnProcessLPOPaymentActionPerformed

    private void btnPrintExpenseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintExpenseActionPerformed
        Map param = new HashMap();
        param.put("dateFrom", String.valueOf(tdf.format(dateFromExpense.getDate())));
        param.put("dateTo", String.valueOf(tdf.format(dateToExpense.getDate())));
        param.put("showExpense", lblTotalExpenseShow.getText());
        saveAuditTrail("VIEWED EXPENSE FROM " + String.valueOf(tdf.format(dateFromExpense.getDate())) 
                + " TO" + String.valueOf(tdf.format(dateToExpense.getDate())));
        try{
            dbConn.conn.close();
            Class.forName("com.mysql.jdbc.Driver");
            dbConn.conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbpos","root","root");
            JasperDesign jd = JRXmlLoader.load(new File("src\\Reports\\repExpense.jrxml"));
            JasperReport jr = JasperCompileManager.compileReport(jd);
            JasperPrint jp = JasperFillManager.fillReport(jr, param,dbConn.conn);
            JasperViewer.viewReport(jp,false);
        }catch(ClassNotFoundException | SQLException | JRException e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_btnPrintExpenseActionPerformed

    private void btnPrintIncomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintIncomeActionPerformed
        Map param = new HashMap();
        param.put("dateFrom", String.valueOf(tdf.format(dateFromIncome.getDate())));
        param.put("dateTo", String.valueOf(tdf.format(dateToIncome.getDate())));
        param.put("showSales", lblTotalSalesIncome.getText());
        param.put("showFOC", lblTotalFOCIncome.getText());
        param.put("showExpense", lblTotalExpenseIncome.getText());
        param.put("showNetIncome", lblNetIncome.getText());
        saveAuditTrail("VIEWED INCOME FROM " + String.valueOf(tdf.format(dateFromIncome.getDate())) 
                + " TO" + String.valueOf(tdf.format(dateToIncome.getDate())));
        try{
            dbConn.conn.close();
            Class.forName("com.mysql.jdbc.Driver");
            dbConn.conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbpos","root","root");
            JasperDesign jd = JRXmlLoader.load(new File("src\\Reports\\repIncome.jrxml"));
            JasperReport jr = JasperCompileManager.compileReport(jd);
            JasperPrint jp = JasperFillManager.fillReport(jr, param,dbConn.conn);
            JasperViewer.viewReport(jp,false);
        }catch(ClassNotFoundException | SQLException | JRException e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_btnPrintIncomeActionPerformed

    private void btnPrintALLLPOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintALLLPOActionPerformed
        Map param = new HashMap();
        param.put("dateFrom", String.valueOf(tdf.format(dateFromLPO.getDate())));
        param.put("dateTo", String.valueOf(tdf.format(dateToLPO.getDate())));
        saveAuditTrail("VIEWED LPO REPORT FROM " + String.valueOf(tdf.format(dateFromLPO.getDate())) 
                + " TO" + String.valueOf(tdf.format(dateToLPO.getDate())));
        try{
            dbConn.conn.close();
            Class.forName("com.mysql.jdbc.Driver");
            dbConn.conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbpos","root","root");
            JasperDesign jd = JRXmlLoader.load(new File("src\\Reports\\repLPOReport.jrxml"));
            JasperReport jr = JasperCompileManager.compileReport(jd);
            JasperPrint jp = JasperFillManager.fillReport(jr, param,dbConn.conn);
            JasperViewer.viewReport(jp,false);
        }catch(ClassNotFoundException | SQLException | JRException e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_btnPrintALLLPOActionPerformed

    private void btnPrintLPOStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintLPOStatusActionPerformed
        Map param = new HashMap();
        param.put("dateFrom", String.valueOf(tdf.format(dateFromLPO.getDate())));
        param.put("dateTo", String.valueOf(tdf.format(dateToLPO.getDate())));
        param.put("getStatus",cmbLPOStatus.getSelectedItem().toString());
        saveAuditTrail("VIEWED LPO REPORT FROM " + String.valueOf(tdf.format(dateFromLPO.getDate())) 
                + " TO" + String.valueOf(tdf.format(dateToLPO.getDate())));
        try{
            dbConn.conn.close();
            Class.forName("com.mysql.jdbc.Driver");
            dbConn.conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbpos","root","root");
            JasperDesign jd = JRXmlLoader.load(new File("src\\Reports\\repLPOStatus.jrxml"));
            JasperReport jr = JasperCompileManager.compileReport(jd);
            JasperPrint jp = JasperFillManager.fillReport(jr, param,dbConn.conn);
            JasperViewer.viewReport(jp,false);
        }catch(ClassNotFoundException | SQLException | JRException e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_btnPrintLPOStatusActionPerformed

    private void btnSearchAuditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchAuditActionPerformed
        Date getDateFromAudit = dateFromAudit.getDate();
        Date getDateToAudit = dateToAudit.getDate();
        try{
            dbConn.pstmt = dbConn.conn.prepareStatement("Select * from tblAuditTrail where at_dateAndTime between"
                    + " ? and ? order by at_dateandTime DESC");
            dbConn.pstmt.setString(1,String.valueOf(sdfAudit.format(getDateFromAudit)));
            dbConn.pstmt.setString(2,String.valueOf(sdfAudit.format(getDateToAudit)));
            dbConn.rs = dbConn.pstmt.executeQuery();
            tblAuditTrail.setModel(DbUtils.resultSetToTableModel(dbConn.rs));
            dbConn.pstmt.close();
            tblAuditTrail.getColumnModel().getColumn(0).setHeaderValue("ID");
            tblAuditTrail.getColumnModel().getColumn(1).setHeaderValue("TRANSACTION");
            tblAuditTrail.getColumnModel().getColumn(2).setHeaderValue("DATE AND TIME");
            tblAuditTrail.getColumnModel().getColumn(3).setHeaderValue("USER");
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_btnSearchAuditActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        fillAuditTrail();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnAuditTrailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAuditTrailActionPerformed
        Map param = new HashMap();
        param.put("dateFrom", String.valueOf(tdf.format(dateFromAudit.getDate())));
        param.put("dateTo", String.valueOf(tdf.format(dateToAudit.getDate())));
        saveAuditTrail("VIEWED AUDIT TRAIL FROM " + String.valueOf(tdf.format(dateFromAudit.getDate())) 
                + " TO" + String.valueOf(tdf.format(dateToAudit.getDate())));
        try{
            dbConn.conn.close();
            Class.forName("com.mysql.jdbc.Driver");
            dbConn.conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbpos","root","root");
            JasperDesign jd = JRXmlLoader.load(new File("src\\Reports\\repAuditTrail.jrxml"));
            JasperReport jr = JasperCompileManager.compileReport(jd);
            JasperPrint jp = JasperFillManager.fillReport(jr, param,dbConn.conn);
            JasperViewer.viewReport(jp,false);
        }catch(ClassNotFoundException | SQLException | JRException e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_btnAuditTrailActionPerformed

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
            java.util.logging.Logger.getLogger(frmReports.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frmReports.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frmReports.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frmReports.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frmReports().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAuditTrail;
    private javax.swing.JButton btnPrintALLLPO;
    private javax.swing.JButton btnPrintExpense;
    private javax.swing.JButton btnPrintIncome;
    private javax.swing.JButton btnPrintLPOStatus;
    private javax.swing.JButton btnProcessLPOPayment;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSearchAudit;
    private javax.swing.JButton btnShowAllExpense;
    private javax.swing.JButton btnViewExpense;
    private javax.swing.JButton btnViewIncome;
    private javax.swing.JComboBox<String> cmbLPOStatus;
    private javax.swing.JComboBox<String> cmbProductPrint;
    private javax.swing.JComboBox<String> cmbSupplier;
    private com.toedter.calendar.JDateChooser dateFrom;
    private com.toedter.calendar.JDateChooser dateFromAudit;
    private com.toedter.calendar.JDateChooser dateFromExpense;
    private com.toedter.calendar.JDateChooser dateFromIncome;
    private com.toedter.calendar.JDateChooser dateFromLPO;
    private com.toedter.calendar.JDateChooser datePicker;
    private com.toedter.calendar.JDateChooser dateTo;
    private com.toedter.calendar.JDateChooser dateToAudit;
    private com.toedter.calendar.JDateChooser dateToExpense;
    private com.toedter.calendar.JDateChooser dateToIncome;
    private com.toedter.calendar.JDateChooser dateToLPO;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JPanel jPanel31;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JScrollPane jscrollpane;
    private javax.swing.JLabel lblAmountPaid;
    private javax.swing.JLabel lblChange;
    private javax.swing.JLabel lblDailyCard;
    private javax.swing.JLabel lblDailyCash;
    private javax.swing.JLabel lblDailyFoc;
    private javax.swing.JLabel lblLPONumber;
    private javax.swing.JLabel lblNetIncome;
    private javax.swing.JLabel lblSupplier;
    private javax.swing.JLabel lblTotalAmount;
    private javax.swing.JLabel lblTotalAmountLPOPayment;
    private javax.swing.JLabel lblTotalExpenseIncome;
    private javax.swing.JLabel lblTotalExpenseShow;
    private javax.swing.JLabel lblTotalFOCIncome;
    private javax.swing.JLabel lblTotalSalesIncome;
    private javax.swing.JPanel panExpense;
    private javax.swing.JPanel panIncome;
    private javax.swing.JPanel panProduct;
    private javax.swing.JPanel panReceipts;
    private javax.swing.JPanel panSales;
    private javax.swing.JTable tableReceipt;
    private javax.swing.JTable tblAuditTrail;
    private javax.swing.JTable tblExpense;
    private javax.swing.JTable tblExpenseIncome;
    private javax.swing.JTable tblLPOPayment;
    private javax.swing.JTable tblProduct;
    private javax.swing.JTable tblSalesIncome;
    private javax.swing.JTextField txtAmountPaid;
    private javax.swing.JTextField txtProductName;
    // End of variables declaration//GEN-END:variables
}
