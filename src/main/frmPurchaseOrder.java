package main;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.Connection;
import java.util.Date;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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

public class frmPurchaseOrder extends javax.swing.JFrame {
//ResultSet dbConn.rs;
//Connection conn;
//PreparedStatement dbConn.pstmt;
dbConnection dbConn = new dbConnection();
int showOrderQty =0, getMaxLPOid,getMaxExpenseID;
String getSupPrice,getPOStatus;
SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
DecimalFormat dfo = new DecimalFormat("0.000");
int getMaxAuditID;
SimpleDateFormat sdfAudit = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
    public frmPurchaseOrder() {
        initComponents();
        dbConn.doConnect();
        fillTable();
        setLocationRelativeTo(null);
        tableMinimum.setAutoCreateRowSorter(true);
        tableReceive.setAutoCreateRowSorter(true);
        tableOrder.setAutoCreateRowSorter(true);
        sortTableOrder();
        fillComboSupplier();
        sortTableReceived();
        fillLPOPayment();
        dateOrder.getJCalendar().setMinSelectableDate(new Date());
        setDefaultCloseOperation(frmProductControl.DISPOSE_ON_CLOSE);
        txtItem.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {listenToTextItem();}
            @Override
            public void removeUpdate(DocumentEvent e) {listenToTextItem();}
            @Override
            public void changedUpdate(DocumentEvent e) {listenToTextItem();}
        });
        cmbSupplier.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {btnShowItemsBySupplierActionPerformed(null);}
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
            dbConn.pstmt.setString(4,showUserName);
            dbConn.pstmt.execute();
            dbConn.pstmt.close();
        }catch(SQLException e){
            e.getMessage();
        }
    }
    
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
    private void listenToTextItem(){
        try{
            dbConn.pstmt = dbConn.conn.prepareStatement("SELECT productId,productName,"
                    + "stockOnHand,supplier from tblProduct where productName like ?");
            dbConn.pstmt.setString(1, "%"+ txtItem.getText() + "%");
            dbConn.rs = dbConn.pstmt.executeQuery();
            if (dbConn.rs.next()){
                cmbSupplier.setSelectedItem(dbConn.rs.getString("supplier"));
            }
            dbConn.pstmt.close();
        }catch(SQLException e){
            e.getMessage();
        }  
    }
    private void fillTable(){
        tableMinimum.getColumnModel().getColumn(0).setHeaderValue("SKU");
        tableMinimum.getColumnModel().getColumn(1).setHeaderValue("NAME");
        tableMinimum.getColumnModel().getColumn(2).setHeaderValue("STOCK");
        tableMinimum.getColumnModel().getColumn(3).setHeaderValue("SUPPLIER");
        tableMinimum.getColumnModel().getColumn(4).setHeaderValue("PRICE");
//        try{
//            dbConn.pstmt = dbConn.conn.prepareStatement("SELECT productId,productName,supplier,supplier2,supplierPrice "
//                    + "from tblProduct order by category");
//            dbConn.rs = dbConn.pstmt.executeQuery();
//            tableMinimum.setModel(DbUtils.resultSetToTableModel(dbConn.rs));
//        }catch(SQLException e){
//            e.getMessage();
//        }
    }    
//    private void doConnect(){
//    try{
//        Class.forName("com.mysql.jdbc.Driver");
//        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbpos","root","root");
//    }catch(SQLException | ClassNotFoundException e){
//        JOptionPane.showMessageDialog(this, e.getMessage());
//    }
//    }
    private void sortTable(){    

    tableMinimum.getColumnModel().getColumn(0).setHeaderValue("BARCODE");
    tableMinimum.getColumnModel().getColumn(1).setHeaderValue("NAME");
    tableMinimum.getColumnModel().getColumn(2).setHeaderValue("U/PRICE");
    tableMinimum.getColumnModel().getColumn(3).setHeaderValue("CATEGORY");
    tableMinimum.getColumnModel().getColumn(4).setHeaderValue("QTY");
    tableMinimum.getColumnModel().getColumn(5).setHeaderValue("MIN ORDER");
    tableMinimum.getColumnModel().getColumn(6).setHeaderValue("MAX ORDER");

    tableMinimum.removeColumn(tableMinimum.getColumnModel().getColumn(6));
    tableMinimum.removeColumn(tableMinimum.getColumnModel().getColumn(3));
    tableMinimum.removeColumn(tableMinimum.getColumnModel().getColumn(2));
    tableMinimum.removeColumn(tableMinimum.getColumnModel().getColumn(0));
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnCategory = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableMinimum = new javax.swing.JTable();
        btnCritical = new javax.swing.JButton();
        jPanel11 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableOrder = new javax.swing.JTable();
        jButton16 = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        lblTotalAmount = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        lblTranNumber = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jPanel17 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        cmbSupplier = new javax.swing.JComboBox<>();
        txtItem = new javax.swing.JTextField();
        btnShowItemsBySupplier = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        dateOrder = new com.toedter.calendar.JDateChooser();
        jButton12 = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblCritical = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        datePicker = new com.toedter.calendar.JDateChooser();
        jLabel13 = new javax.swing.JLabel();
        btnShowOrder = new javax.swing.JButton();
        jPanel16 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tableReceive = new javax.swing.JTable();
        jPanel13 = new javax.swing.JPanel();
        jPanel18 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblLPO = new javax.swing.JTable();
        btnReceiveAll = new javax.swing.JButton();
        jButton30 = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        datePayLPO = new com.toedter.calendar.JDateChooser();
        jLabel8 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jscrollpane = new javax.swing.JScrollPane();
        tblLPOPayment = new javax.swing.JTable();
        jPanel19 = new javax.swing.JPanel();
        jPanel20 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txtAmountPaid = new javax.swing.JTextField();
        lblTotalAmountLPOPayment = new javax.swing.JLabel();
        lblSupplier = new javax.swing.JLabel();
        btnProcessLPOPayment = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        lblLPONumber = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTabbedPane1.setBackground(java.awt.Color.white);
        jTabbedPane1.setForeground(new java.awt.Color(0, 0, 0));

        jPanel1.setBackground(new java.awt.Color(214, 214, 194));
        jPanel1.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tableMinimum.setModel(new javax.swing.table.DefaultTableModel(
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
        tableMinimum.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableMinimumMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tableMinimumMouseEntered(evt);
            }
        });
        jScrollPane1.setViewportView(tableMinimum);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 260, 540, 160));

        btnCritical.setBackground(new java.awt.Color(255, 255, 255));
        btnCritical.setForeground(new java.awt.Color(0, 0, 0));
        btnCritical.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/cancelIcon.png"))); // NOI18N
        btnCritical.setText("CANCEL LPO");
        btnCritical.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCritical.setVerifyInputWhenFocusTarget(false);
        btnCritical.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCritical.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCriticalActionPerformed(evt);
            }
        });
        jPanel1.add(btnCritical, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 120, 170));

        jPanel11.setBackground(new java.awt.Color(214, 214, 194));
        jPanel11.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel11.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel12.setBackground(new java.awt.Color(214, 214, 194));
        jPanel12.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel12.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tableOrder.setBackground(new java.awt.Color(214, 214, 194));
        tableOrder.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        tableOrder.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(tableOrder);

        jPanel12.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 450, 180));

        jButton16.setBackground(new java.awt.Color(255, 255, 255));
        jButton16.setForeground(new java.awt.Color(0, 0, 0));
        jButton16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/Process.png"))); // NOI18N
        jButton16.setText("PROCESS LPO");
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });
        jPanel12.add(jButton16, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 30, 290, 50));

        jButton17.setBackground(new java.awt.Color(255, 255, 255));
        jButton17.setForeground(new java.awt.Color(0, 0, 0));
        jButton17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/closeIcon.png"))); // NOI18N
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });
        jPanel12.add(jButton17, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 80, 50));

        lblTotalAmount.setForeground(new java.awt.Color(0, 0, 0));
        lblTotalAmount.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotalAmount.setText("0.000");
        jPanel12.add(lblTotalAmount, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 290, 100, 30));

        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("TOTAL AMOUNT:");
        jPanel12.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 290, 100, 30));
        jPanel12.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 290, 270, 10));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("LPO NUMBER:");
        jPanel12.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 100, 20));

        lblTranNumber.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblTranNumber.setForeground(new java.awt.Color(255, 255, 255));
        lblTranNumber.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel12.add(lblTranNumber, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 0, 80, 20));

        jPanel11.add(jPanel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 470, 340));

        jPanel1.add(jPanel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 10, 490, 360));

        jPanel8.setBackground(new java.awt.Color(214, 214, 194));
        jPanel8.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel9.setBackground(new java.awt.Color(214, 214, 194));
        jPanel9.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel17.setBackground(new java.awt.Color(214, 214, 194));
        jPanel17.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel17.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("ITEM:");
        jPanel17.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 70, 30));

        jLabel12.setForeground(new java.awt.Color(0, 0, 0));
        jLabel12.setText("SUPPLIER:");
        jPanel17.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 70, 30));

        cmbSupplier.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel17.add(cmbSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 10, 270, 30));
        jPanel17.add(txtItem, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 50, 270, 30));

        btnShowItemsBySupplier.setBackground(new java.awt.Color(255, 255, 255));
        btnShowItemsBySupplier.setForeground(new java.awt.Color(0, 0, 0));
        btnShowItemsBySupplier.setText("SHOW ITEMS");
        btnShowItemsBySupplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowItemsBySupplierActionPerformed(evt);
            }
        });
        jPanel17.add(btnShowItemsBySupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 90, 270, 30));

        jPanel9.add(jPanel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 370, 130));

        jPanel8.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 390, 150));

        jPanel1.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 10, 410, 170));

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel1.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 10, 10, 580));

        dateOrder.setDateFormatString("MMM dd, yyyy");
        jPanel1.add(dateOrder, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 200, 220, 40));

        jButton12.setBackground(new java.awt.Color(255, 255, 255));
        jButton12.setForeground(new java.awt.Color(0, 0, 0));
        jButton12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/processIcon.png"))); // NOI18N
        jButton12.setText("PROCESS ORDER");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton12, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 190, 230, 60));

        tblCritical.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane5.setViewportView(tblCritical);

        jPanel1.add(jScrollPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 437, 540, 150));

        jTabbedPane1.addTab("ORDERING", jPanel1);

        jPanel2.setBackground(new java.awt.Color(214, 214, 194));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel4.setBackground(new java.awt.Color(214, 214, 194));
        jPanel4.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel5.setBackground(new java.awt.Color(214, 214, 194));
        jPanel5.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel6.setBackground(new java.awt.Color(214, 214, 194));
        jPanel6.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        datePicker.setDateFormatString("MMM dd, yyyy");
        jPanel6.add(datePicker, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 200, 30));

        jLabel13.setForeground(new java.awt.Color(0, 0, 0));
        jLabel13.setText("LPO DATE:");
        jPanel6.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 130, 30));

        btnShowOrder.setBackground(new java.awt.Color(255, 255, 255));
        btnShowOrder.setForeground(new java.awt.Color(0, 0, 0));
        btnShowOrder.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/SearchIcon.png"))); // NOI18N
        btnShowOrder.setText("SEARCH");
        btnShowOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowOrderActionPerformed(evt);
            }
        });
        jPanel6.add(btnShowOrder, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 200, 50));

        jPanel5.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 220, 150));

        jPanel4.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 240, 170));

        jPanel16.setBackground(java.awt.Color.white);
        jPanel16.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tableReceive.setBackground(java.awt.Color.white);
        tableReceive.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5", "Title 6"
            }
        ));
        tableReceive.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableReceiveMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tableReceive);

        jPanel16.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 550, 140));

        jPanel4.add(jPanel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 200, 570, 170));

        jPanel13.setBackground(new java.awt.Color(214, 214, 194));
        jPanel13.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel13.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel18.setBackground(new java.awt.Color(214, 214, 194));
        jPanel18.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel18.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tblLPO.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "LPO", "SUPPLIER", "AMOUNT"
            }
        ));
        tblLPO.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblLPOMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tblLPO);

        jPanel18.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 280, 130));

        jPanel13.add(jPanel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 300, 150));

        jPanel4.add(jPanel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 10, 320, 170));

        btnReceiveAll.setBackground(new java.awt.Color(255, 255, 255));
        btnReceiveAll.setForeground(new java.awt.Color(0, 0, 0));
        btnReceiveAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/Process.png"))); // NOI18N
        btnReceiveAll.setText("PROCESS");
        btnReceiveAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReceiveAllActionPerformed(evt);
            }
        });
        jPanel4.add(btnReceiveAll, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 390, 240, 60));

        jButton30.setBackground(new java.awt.Color(255, 255, 255));
        jButton30.setForeground(new java.awt.Color(0, 0, 0));
        jButton30.setText("COMPLETE FILL UP");
        jButton30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton30ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton30, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 390, 210, 60));

        jPanel2.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 590, 550));

        jPanel7.setBackground(new java.awt.Color(214, 214, 194));
        jPanel7.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("LPO PAYMENTS");
        jPanel7.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 390, 30));

        jPanel14.setBackground(new java.awt.Color(214, 214, 194));
        jPanel14.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel15.setBackground(new java.awt.Color(214, 214, 194));
        jPanel15.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel15.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        datePayLPO.setDateFormatString("MMM dd, yyyy");
        jPanel15.add(datePayLPO, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 10, 150, 50));

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setForeground(new java.awt.Color(0, 0, 0));
        jLabel8.setText("LPO DATE:");
        jPanel15.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 80, 40));

        jButton1.setBackground(new java.awt.Color(255, 255, 255));
        jButton1.setForeground(new java.awt.Color(0, 0, 0));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/SearchIcon.png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel15.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 10, 60, 50));

        jButton2.setBackground(new java.awt.Color(255, 255, 255));
        jButton2.setForeground(new java.awt.Color(0, 0, 0));
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/refreshIcon.png"))); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel15.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 10, 50, 50));

        jPanel14.add(jPanel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 370, 70));

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

        jPanel14.add(jscrollpane, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 370, 170));

        jPanel7.add(jPanel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 390, 270));

        jPanel19.setBackground(java.awt.Color.white);
        jPanel19.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel20.setBackground(java.awt.Color.white);
        jPanel20.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel10.setForeground(new java.awt.Color(0, 0, 0));
        jLabel10.setText("SUPPLIER:");
        jPanel20.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 140, -1));

        jLabel11.setForeground(new java.awt.Color(0, 0, 0));
        jLabel11.setText("AMOUNT PAID:");
        jPanel20.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 100, 140, -1));

        jLabel14.setForeground(new java.awt.Color(0, 0, 0));
        jLabel14.setText("LPO TOTAL AMOUNT:");
        jPanel20.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, 140, -1));
        jPanel20.add(txtAmountPaid, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 100, 160, -1));

        lblTotalAmountLPOPayment.setForeground(new java.awt.Color(0, 0, 0));
        lblTotalAmountLPOPayment.setText("N/A");
        jPanel20.add(lblTotalAmountLPOPayment, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 70, 160, -1));

        lblSupplier.setForeground(new java.awt.Color(0, 0, 0));
        lblSupplier.setText("N/A");
        jPanel20.add(lblSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 40, 160, -1));

        btnProcessLPOPayment.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/Process.png"))); // NOI18N
        btnProcessLPOPayment.setText("PROCESS LPO PAYMENT");
        btnProcessLPOPayment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProcessLPOPaymentActionPerformed(evt);
            }
        });
        jPanel20.add(btnProcessLPOPayment, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 140, 330, 60));

        jLabel15.setForeground(new java.awt.Color(0, 0, 0));
        jLabel15.setText("LPO #:");
        jPanel20.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 140, -1));

        lblLPONumber.setForeground(new java.awt.Color(0, 0, 0));
        lblLPONumber.setText("N/A");
        jPanel20.add(lblLPONumber, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 10, 160, -1));

        jPanel19.add(jPanel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 370, 210));

        jPanel7.add(jPanel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 320, 390, 230));

        jPanel2.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 0, 410, 560));

        jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel2.add(jSeparator4, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 0, 20, 600));

        jTabbedPane1.addTab("RECEIVING", jPanel2);

        getContentPane().add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 1090, 630));

        jMenuBar1.setBackground(new java.awt.Color(214, 214, 194));

        jMenu1.setText("File");

        jMenuItem1.setText("MAIN MENU");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void viewAllCritical(){
        DefaultTableModel model = (DefaultTableModel)tableMinimum.getModel(); 
        try{
            String viewCriticalSQL = "Select * from tblProduct order by category";
            dbConn.pstmt = dbConn.conn.prepareStatement(viewCriticalSQL);
            dbConn.rs = dbConn.pstmt.executeQuery();
            while(model.getRowCount()>0){
                model.setRowCount(0);
            }
            while(dbConn.rs.next()){
                String prodName = dbConn.rs.getString("productName");
                int getStock = dbConn.rs.getInt("stockOnHand");
                int getMinimum = dbConn.rs.getInt("minimumOrder");
                 if (getStock<getMinimum){
                    Object[] adRow = {"",prodName,"","",getStock,getMinimum,""};
                    model.addRow(adRow);
            }
            }
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
    private void btnCriticalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCriticalActionPerformed
         int itemSelected = JOptionPane.showConfirmDialog(this, "CANCEL LPO?","LPO",JOptionPane.YES_NO_OPTION);
         if (itemSelected == JOptionPane.YES_OPTION){
               cmbSupplier.setEnabled(true);
               lblTotalAmount.setText("0.000");
               DefaultTableModel model = (DefaultTableModel)tableOrder.getModel();
               model.setRowCount(0);
               DefaultTableModel model2 = (DefaultTableModel)tableMinimum.getModel();
               model2.setRowCount(0);
               cmbSupplier.setSelectedIndex(-1);
               dateOrder.setDate(null);
         }
    }//GEN-LAST:event_btnCriticalActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        frmMain obj = new frmMain();
        obj.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jMenuItem1ActionPerformed
    private void viewAllByCategory(){
                String getCategory="";   
          
            try{
                dbConn.pstmt = dbConn.conn.prepareStatement("Select * from tblProduct where category=?");
                dbConn.pstmt.setString(1, getCategory);
                dbConn.rs = dbConn.pstmt.executeQuery();
                tableMinimum.setModel(DbUtils.resultSetToTableModel(dbConn.rs));
                sortTable();
            }catch(SQLException e){
                e.getMessage();
            }
    }
    private void tableMinimumMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableMinimumMouseClicked
//        int row = tableMinimum.getSelectedRow();
//        int ba = tableMinimum.convertRowIndexToModel(row);
//        String tblClick = (tableMinimum.getModel().getValueAt(ba, 1).toString());
//        String selectedItemSQL = "Select * from tblProduct where productName=?";
//        try{
//            dbConn.pstmt = dbConn.conn.prepareStatement(selectedItemSQL);
//            dbConn.pstmt.setString(1, tblClick);
//            dbConn.rs = dbConn.pstmt.executeQuery();
//            if (dbConn.rs.next()){
//                lblName.setText(dbConn.rs.getString("productName"));
//                lblSoh.setText(String.valueOf(dbConn.rs.getInt("stockOnHand")));
//                lblCategory.setText(String.valueOf(dbConn.rs.getString("category")));
//                lblProductId.setText(dbConn.rs.getString("productId"));
//                getSupPrice = dbConn.rs.getString("supplierPrice");
//            }
//            txtOrderQty.setText("");
//        }catch(SQLException e){
//            e.getMessage();
//        }
    }//GEN-LAST:event_tableMinimumMouseClicked

    private void tableMinimumMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableMinimumMouseEntered
       
    }//GEN-LAST:event_tableMinimumMouseEntered

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        DefaultTableModel model = (DefaultTableModel)tableOrder.getModel();
        model.removeRow(tableOrder.getSelectedRow());
        getTotalAmountLPO();
    }//GEN-LAST:event_jButton17ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        getNextLPO();
        Date dateCheck = dateOrder.getDate();
        cmbSupplier.setEnabled(false);
        if (dateCheck == null){
            JOptionPane.showMessageDialog(this, "PICK AN LPO DATE FIRST","DATE ORDER",JOptionPane.ERROR_MESSAGE);
        }else{
            DefaultTableModel model = (DefaultTableModel)tableOrder.getModel();
            DefaultTableModel model2 = (DefaultTableModel)tableMinimum.getModel();
            for (int i=0;i<tableMinimum.getRowCount();i++){
                Object getQty = model2.getValueAt(i, 5);
                String checkQty = String.valueOf(getQty);
                    if (checkQty.equals("") || checkQty.equals("null")){
                        model2.setValueAt(0, i, 5);
                    }else{
                        model2.getValueAt(i, 0).toString();
                    }
                    double getSum  = Double.valueOf(model2.getValueAt(i, 4).toString()) * Double.valueOf(model2.getValueAt(i, 5).toString());   
                    Object [] adRow = {model2.getValueAt(i, 0).toString(),model2.getValueAt(i, 1).toString(),
                        model2.getValueAt(i, 5).toString(),model2.getValueAt(i, 4).toString(),
                        dfo.format(getSum)};
                        model.addRow(adRow);
                    for (int jx=0;jx<tableOrder.getRowCount();jx++){
                        Object checkQtyOrder = model.getValueAt(jx, 2);
                        if (checkQtyOrder.equals("0")){
                            model.removeRow(jx);
                        }    
                    }   
            }
        }
        
        getTotalAmountLPO();
            
//            int duplicateRow = -1;
//            if (model.getRowCount() ==0){
//                    double getSum  = Double.valueOf(txtOrderQty.getText()) * Double.valueOf(getSupPrice);
//                Object [] adRow = {lblProductId.getText(),lblName.getText(),txtOrderQty.getText(),getSupPrice,dfo.format(getSum)};
//                model.addRow(adRow);
//            }else{
//                for(int j=0;j<model.getRowCount();j++){
//                    Object obj = model.getValueAt(j, 0);
//                    if (obj.equals(lblName.getText())){
//                        duplicateRow = j;
//                        break;
//                    }
//                }
//                if (duplicateRow == -1){
//                    double getSum  = Double.valueOf(txtOrderQty.getText()) * Double.valueOf(getSupPrice);
//                    Object [] adRow = {lblProductId.getText(),lblName.getText(), 
//                    txtOrderQty.getText(),getSupPrice,dfo.format(getSum)};
//                    model.addRow(adRow);
//                }else{
//                    int getQty = Integer.valueOf(txtOrderQty.getText()) + Integer.valueOf(model.getValueAt(duplicateRow,1).toString());
//                    model.setValueAt(lblProductId.getText(), duplicateRow, 0);
//                    model.setValueAt(lblName.getText(), duplicateRow, 1);
//                    model.setValueAt(getQty,duplicateRow, 2);
//                    model.setValueAt(getSupPrice,duplicateRow,3);
//                    double getSum  = getQty * Integer.valueOf(getSupPrice);
//                    model.setValueAt(dfo.format(getSum), duplicateRow, 4);
//                }
//            }
//            showOrderQty=0;
//            txtOrderQty.setText("");
////
////            try{
////                dbConn.pstmt = dbConn.conn.prepareStatement("Select * from tblOrder order by transactionNumber DESC LIMIT 1");
////                dbConn.rs = dbConn.pstmt.executeQuery();
////                if (dbConn.rs.next()){
////                    int tranNumber = dbConn.rs.getInt("transactionNumber");
////                    tranNumber++;
////                    lblTranNumber.setText(String.valueOf(tranNumber));
////                }else{
////                    lblTranNumber.setText("1");
////                }
////                dbConn.pstmt.close();
////            }catch(SQLException e){
////                JOptionPane.showMessageDialog(this, e.getMessage());
////            }
//        }
        
    }//GEN-LAST:event_jButton12ActionPerformed
    private void getTotalAmountLPO(){
        double getSumAmount,showTotalAmountLPO=0;
        for(int i =0; i<tableOrder.getRowCount();i++){
            getSumAmount = Double.valueOf(tableOrder.getValueAt(i,4).toString());
            showTotalAmountLPO+= getSumAmount;
        }
        lblTotalAmount.setText(String.valueOf(dfo.format(showTotalAmountLPO)));
    }    private void getNextLPO(){
        try{
            dbConn.pstmt = dbConn.conn.prepareStatement("Select po_Id from tblPurchaseOrder order by po_Id DESC LIMIT 1");
            dbConn.rs = dbConn.pstmt.executeQuery();
            if (dbConn.rs.next()){
                getMaxLPOid = dbConn.rs.getInt(1);
                getMaxLPOid++;
            }else{
                getMaxLPOid =1;
            }
        }catch(SQLException e){
            e.getMessage();
        }
        lblTranNumber.setText(String.valueOf(getMaxLPOid));
    }
    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        try{
            for(int i=0; i<tableOrder.getRowCount();i++){
                dbConn.pstmt = dbConn.conn.prepareStatement("INSERT INTO tblPurchaseOrder (po_Id,po_prodSku,po_prodName,po_qtyOrder,po_supPrice,"
                        + "po_totalPrice,po_userNameLPO,po_supplier,po_lpoDate,po_totalAmount,po_status) values(?,?,?,?,?,?,?,?,?,?,?)");
                dbConn.pstmt.setInt(1,getMaxLPOid);
                dbConn.pstmt.setString(2,tableOrder.getValueAt(i, 0).toString());
                dbConn.pstmt.setString(3,tableOrder.getValueAt(i, 1).toString());
                dbConn.pstmt.setInt(4,Integer.valueOf(tableOrder.getValueAt(i, 2).toString()));
                dbConn.pstmt.setDouble(5,Double.valueOf(tableOrder.getValueAt(i, 3).toString()));
                dbConn.pstmt.setDouble(6,Double.valueOf(tableOrder.getValueAt(i, 4).toString()));
                dbConn.pstmt.setString(7,frmLogin.showUserName);
                dbConn.pstmt.setString(8,cmbSupplier.getSelectedItem().toString());
                dbConn.pstmt.setString(9,String.valueOf(df.format(dateOrder.getDate())));
                dbConn.pstmt.setString(10,lblTotalAmount.getText());
                dbConn.pstmt.setString(11,"ORDERED");
                dbConn.pstmt.execute();
                dbConn.pstmt.close();
            }
                JOptionPane.showMessageDialog(this, "ORDER SAVED FOR: " + String.valueOf(df.format(dateOrder.getDate())));
                cmbSupplier.setEnabled(true);   
                saveAuditTrail("LPO PROCESSED FOR: " + String.valueOf(getMaxLPOid));
                lblTotalAmount.setText("0.000");
                DefaultTableModel model = (DefaultTableModel)tableOrder.getModel();
                model.setRowCount(0);
                DefaultTableModel model2 = (DefaultTableModel)tableMinimum.getModel();
                model2.setRowCount(0);
                dateOrder.setDate(null);
//            DefaultTableModel model = (DefaultTableModel)tableOrder.getModel();
//            model.setRowCount(0);
            printOrder();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
//add category of products and save in the report
//        try{
//            for(int i=0; i<tableOrder.getRowCount();i++){
//                dbConn.pstmt = dbConn.conn.prepareStatement("INSERT INTO tblOrder (productName,qtyOrder,orderDate,category,transactionNumber) values(?,?,?,?,?)");
//                dbConn.pstmt.setString(1,tableOrder.getValueAt(i, 0).toString());
//                dbConn.pstmt.setInt(2,Integer.valueOf(tableOrder.getValueAt(i, 2).toString()));
//                dbConn.pstmt.setString(3, String.valueOf(df.format(dateOrder.getDate())));
//                dbConn.pstmt.setString(4,tableOrder.getValueAt(i, 3).toString());
//                dbConn.pstmt.setInt(5,Integer.valueOf(lblTranNumber.getText()));
//                dbConn.pstmt.execute();
//                dbConn.pstmt.close();
//            }
//            JOptionPane.showMessageDialog(this, "ORDER SAVED FOR: " + String.valueOf(df.format(dateOrder.getDate())));
//            DefaultTableModel model = (DefaultTableModel)tableOrder.getModel();
//            model.setRowCount(0);
//            printOrder();
//        }catch(SQLException e){
//            JOptionPane.showMessageDialog(this, e.getMessage());
//        }
    }//GEN-LAST:event_jButton16ActionPerformed

    private void btnShowOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowOrderActionPerformed
        Date getDate = datePicker.getDate();
        try{
            dbConn.pstmt = dbConn.conn.prepareStatement("SELECT po_Id,po_supplier,po_TotalAmount from tblPurchaseOrder where po_lpoDate=?");
            dbConn.pstmt.setString(1,String.valueOf(df.format(getDate)));
            dbConn.rs = dbConn.pstmt.executeQuery();
            tblLPO.setModel(DbUtils.resultSetToTableModel(dbConn.rs));
            dbConn.pstmt.close();
            tblLPO.getColumnModel().getColumn(0).setHeaderValue("LPO");
            tblLPO.getColumnModel().getColumn(1).setHeaderValue("SUPPLIER");
            tblLPO.getColumnModel().getColumn(2).setHeaderValue("AMOUNT");
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
        
//        try{
//            String getOrderSQL = "Select * from tblOrder where orderDate=? or transactionNumber=? order by category";
//            dbConn.pstmt = dbConn.conn.prepareStatement(getOrderSQL);
//            if (getDate == null){
//                dbConn.pstmt.setString(1,"");
//                dbConn.pstmt.setString(2,txtTransactionNumber.getText());
//                dbConn.rs = dbConn.pstmt.executeQuery();
//                tableReceive.setModel(DbUtils.resultSetToTableModel(dbConn.rs));
//            }else{
//                dbConn.pstmt.setString(1,String.valueOf(df.format(getDate)));
//                dbConn.pstmt.setString(2,txtTransactionNumber.getText());
//                dbConn.rs = dbConn.pstmt.executeQuery();
//                tableReceive.setModel(DbUtils.resultSetToTableModel(dbConn.rs));
//            }
//            dbConn.pstmt.close();
//            sortTableReceive();
//        }catch(SQLException e){
//            JOptionPane.showMessageDialog(this, e.getMessage());
//        }

    }//GEN-LAST:event_btnShowOrderActionPerformed
private void saveInSales(){
//    try{
//        String saveSalesSQL = "INSERT INTO tblSales (productName,unitPrice,lastSoh,) values (?,?,?)";
//        dbConn.pstmt  = dbConn.conn.prepareStatement(saveSalesSQL);
//        for (int i=0; i<tableReceive.getRowCount(); i++){
//            dbConn.pstmt.setString(1,tableReceive.getValueAt(i, 0).toString());
//            dbConn.pstmt.setString(2,)
//
//        }    
//    }catch(SQLException e){
//        JOptionPane.showMessageDialog(e.getMessage());
//    }
}
    private void processReceiveAll(){
        DefaultTableModel model = (DefaultTableModel)tableReceive.getModel();
        Date dateToday = new Date();
            try{
                String receiveLPO = "UPDATE tblPurchaseOrder set po_qtyReceived=?,po_receiveDate=?,po_status=?,po_payableAmount=? "
                        + "where po_Id=? and po_prodName=?";
                for(int i=0;i<tableReceive.getRowCount();i++){
                    dbConn.pstmt = dbConn.conn.prepareStatement(receiveLPO);
                    dbConn.pstmt.setInt(1,Integer.valueOf(model.getValueAt(i, 5).toString()));
                    dbConn.pstmt.setString(2,String.valueOf(df.format(dateToday)));
                    dbConn.pstmt.setString(3,"RECEIVED");
                    double getPayable = Double.valueOf(dfo.format(Double.valueOf(model.getValueAt(i, 3).toString()) 
                                        * Double.valueOf(model.getValueAt(i, 5).toString()))); 
                    dbConn.pstmt.setDouble(4,getPayable);
                    dbConn.pstmt.setInt(5, Integer.valueOf(model.getValueAt(i, 0).toString()));
                    dbConn.pstmt.setString(6, tableReceive.getValueAt(i, 1).toString());
                    dbConn.pstmt.executeUpdate();
                    dbConn.pstmt.close();
                }
                     JOptionPane.showMessageDialog(this, "ITEMS RECEIVED");
                     saveAuditTrail("LPO ITEMS RECEIVED FOR: " + model.getValueAt(0, 0).toString()); 
            }catch(SQLException e){
                e.getMessage();
            }
         try{
            String addToStockSQL = "UPDATE tblProduct set stockOnHand= stockOnHand+? where productName=?";
            for(int i=0;i<tableReceive.getRowCount();i++){
                dbConn.pstmt = dbConn.conn.prepareStatement(addToStockSQL);
                dbConn.pstmt.setInt(1,Integer.valueOf(tableReceive.getValueAt(i, 5).toString()));
                dbConn.pstmt.setString(2, tableReceive.getValueAt(i, 1).toString());
                dbConn.pstmt.executeUpdate();
                dbConn.pstmt.close();
            }
        }catch (SQLException e){
            e.getMessage();
        }
        model.setRowCount(0);
        
//    DefaultTableModel model = (DefaultTableModel)tableReceive.getModel();
//    Date dateToday = new Date();
//        try{
//            String receiveAllSQL = "UPDATE tblOrder set orderReceived=?,receivedDate=? where transactionNumber=? and productName=?";
//            for(int i=0;i<tableReceive.getRowCount();i++){
//                dbConn.pstmt = dbConn.conn.prepareStatement(receiveAllSQL);
//                dbConn.pstmt.setInt(1,Integer.valueOf(tableReceive.getValueAt(i, 3).toString()));
//                dbConn.pstmt.setString(2,String.valueOf(df.format(dateToday)));
//                dbConn.pstmt.setString(3, txtTransactionNumber.getText());
//                dbConn.pstmt.setString(4, tableReceive.getValueAt(i, 0).toString());
//                dbConn.pstmt.executeUpdate();
//                dbConn.pstmt.close();
//            }
//                 JOptionPane.showMessageDialog(this, "ORDER PROCESSED");
//        }catch(SQLException e){
//            e.getMessage();
//        }
//        // add to SOH
//        try{
//            String addToStockSQL = "UPDATE tblProduct set stockOnHand= stockOnHand+? where productName=?";
//            for(int i=0;i<tableReceive.getRowCount();i++){
//                dbConn.pstmt = dbConn.conn.prepareStatement(addToStockSQL);
//                dbConn.pstmt.setInt(1,Integer.valueOf(tableReceive.getValueAt(i, 3).toString()));
//                dbConn.pstmt.setString(2, tableReceive.getValueAt(i, 0).toString());
//                dbConn.pstmt.executeUpdate();
//                dbConn.pstmt.close();
//            }
//        }catch (SQLException e){
//            e.getMessage();
//        }
//        model.setRowCount(0);
}
    private void tableReceiveMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableReceiveMouseClicked
//        int row = tableReceive.getSelectedRow();
//        int ba = tableReceive.convertRowIndexToModel(row);
//        String tblClick = tableReceive.getModel().getValueAt(ba, 0).toString();
//        String getSelectedItem = "Select * from tblOrder where productName=?";
//        try{
//            dbConn.pstmt = dbConn.conn.prepareStatement(getSelectedItem);
//            dbConn.pstmt.setString(1,tblClick);
//            dbConn.rs = dbConn.pstmt.executeQuery();
//            if (dbConn.rs.next()){
//            lblReceiveOrder.setText(tblClick);
//            lblReceiveCategory.setText(tableReceive.getValueAt(ba, 2).toString());
//            lblReceiveQtyOrder.setText(tableReceive.getValueAt(ba, 1).toString());
//            }
//            dbConn.pstmt.close();
//            btnClearOrderActionPerformed(null);
//        }catch(SQLException e){
//            e.getMessage();
//        }
    }//GEN-LAST:event_tableReceiveMouseClicked

    private void btnShowItemsBySupplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowItemsBySupplierActionPerformed
        try{
            dbConn.pstmt = dbConn.conn.prepareStatement("SELECT productId,productName,"
                    + "stockOnHand,supplier,supplierPrice from tblProduct where supplier = ?");
            dbConn.pstmt.setString(1,cmbSupplier.getSelectedItem().toString());
            dbConn.rs = dbConn.pstmt.executeQuery();
            tableMinimum.setModel(DbUtils.resultSetToTableModel(dbConn.rs));
             dbConn.pstmt.close();
             DefaultTableModel model = (DefaultTableModel)tableMinimum.getModel();
             model.addColumn("QTY ORDER");
            fillTable();
        }catch(SQLException e){
            e.getMessage();
        }    
    }//GEN-LAST:event_btnShowItemsBySupplierActionPerformed

    private void tblLPOMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblLPOMouseClicked
        int row = tblLPO.getSelectedRow();
        int ba = tblLPO.convertRowIndexToModel(row);
        String tblClick = tblLPO.getModel().getValueAt(ba, 0).toString();
        try{
            dbConn.pstmt = dbConn.conn.prepareStatement("Select po_Id,po_prodName,po_qtyOrder,"
                    + "format(po_supPrice,3),format(po_totalPrice,3),po_qtyReceived from tblPurchaseOrder where po_Id=?");
            dbConn.pstmt.setString(1, tblClick);
            dbConn.rs = dbConn.pstmt.executeQuery();
                tableReceive.setModel(DbUtils.resultSetToTableModel(dbConn.rs));
            dbConn.pstmt.close();
            sortTableReceived();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_tblLPOMouseClicked

    private void jButton30ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton30ActionPerformed
        for(int i=0; i<tableReceive.getRowCount();i++){
            tableReceive.setValueAt(tableReceive.getValueAt(i, 2), i, 5);
        }

    }//GEN-LAST:event_jButton30ActionPerformed

    private void btnReceiveAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReceiveAllActionPerformed
        processReceiveAll();
    }//GEN-LAST:event_btnReceiveAllActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Date getDate = datePayLPO.getDate();
        try{
            dbConn.pstmt = dbConn.conn.prepareStatement("SELECT po_Id,po_supplier,po_qtyReceived,po_payableAmount, po_lpoDate from "
                    + "tblPurchaseOrder where po_lpoDate=?");
            dbConn.pstmt.setString(1,String.valueOf(df.format(getDate)));
            dbConn.rs = dbConn.pstmt.executeQuery();
            tblLPOPayment.setModel(DbUtils.resultSetToTableModel(dbConn.rs));
            dbConn.pstmt.close();
            tblLPOPayment.getColumnModel().getColumn(0).setHeaderValue("LPO");
            tblLPOPayment.getColumnModel().getColumn(1).setHeaderValue("SUPPLIER");
            tblLPOPayment.getColumnModel().getColumn(2).setHeaderValue("RECEIVED");
            tblLPOPayment.getColumnModel().getColumn(3).setHeaderValue("AMOUNT");
            tblLPOPayment.getColumnModel().getColumn(4).setHeaderValue("DATE");
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_jButton1ActionPerformed

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
                datePayLPO.setDate(df.parse(dbConn.rs.getString("po_lpoDate")));
                txtAmountPaid.requestFocus();
            }
            dbConn.pstmt.close();
            dbConn.rs.close();
        }catch(SQLException | ParseException e){
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

    private void btnProcessLPOPaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProcessLPOPaymentActionPerformed
        if (getPOStatus.equals("PAID")){
            JOptionPane.showMessageDialog(this, "LPO IS PAID ALREADY","LPO PAID",JOptionPane.WARNING_MESSAGE);
        }else{
            try{
                dbConn.pstmt = dbConn.conn.prepareStatement("update tblPurchaseOrder set po_status=?, po_paidAmount=? where po_lpoDate=? and po_Id=?");
                dbConn.pstmt.setString(1,"PAID");
                dbConn.pstmt.setDouble(2,Double.valueOf(txtAmountPaid.getText()));
                dbConn.pstmt.setString(3,String.valueOf(df.format(datePayLPO.getDate())));
                dbConn.pstmt.setInt(4,Integer.valueOf(lblLPONumber.getText()));
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
                dbConn.pstmt.setString(4,String.valueOf(df.format(dateToday)));
                dbConn.pstmt.execute();
                dbConn.pstmt.close();          
                saveAuditTrail("LPO PAID FOR #: " + lblLPONumber.getText());
                lblLPONumber.setText("");
                lblTotalAmountLPOPayment.setText("");
                lblSupplier.setText("");
            }catch(SQLException e){
                //JOptionPane.showMessageDialog(this, "ADD TO EXPENSE ERROR","EXPENSE MASTER",JOptionPane.ERROR_MESSAGE);
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
        
    }//GEN-LAST:event_btnProcessLPOPaymentActionPerformed
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
    private void tblLPOPaymentMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblLPOPaymentMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_tblLPOPaymentMouseEntered

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        fillLPOPayment();
    }//GEN-LAST:event_jButton2ActionPerformed
    private void sortTableReceived(){
        tableReceive.getColumnModel().getColumn(0).setHeaderValue("LPO");
        tableReceive.getColumnModel().getColumn(1).setHeaderValue("ITEM");
        tableReceive.getColumnModel().getColumn(2).setHeaderValue("QTY ORDER");
        tableReceive.getColumnModel().getColumn(3).setHeaderValue("PRICE");
        tableReceive.getColumnModel().getColumn(4).setHeaderValue("SUM");
        tableReceive.getColumnModel().getColumn(5).setHeaderValue("QTY RECEIVED");
    }
        
    private void viewCriticalItems(){
    String getCategory="";   
         
        DefaultTableModel model = (DefaultTableModel)tableMinimum.getModel(); 
        try{
            String viewCriticalSQL = "Select * from tblProduct where category=?";
            dbConn.pstmt = dbConn.conn.prepareStatement(viewCriticalSQL);
            dbConn.pstmt.setString(1,getCategory);
            dbConn.rs = dbConn.pstmt.executeQuery();
            while(model.getRowCount()>0){
                model.setRowCount(0);
            }
            while(dbConn.rs.next()){
                String prodName = dbConn.rs.getString("productName");
                int getStock = dbConn.rs.getInt("stockOnHand");
                int getMinimum = dbConn.rs.getInt("minimumOrder");
                 if (getStock<getMinimum){
                    Object[] adRow = {"",prodName,"","",getStock,getMinimum,""};
                    model.addRow(adRow);
            }
            }
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }    
}    private void sortTableReceive(){
        tableReceive.removeColumn(tableReceive.getColumnModel().getColumn(2));
        tableReceive.removeColumn(tableReceive.getColumnModel().getColumn(4));
        tableReceive.getColumnModel().getColumn(0).setHeaderValue("ITEM");
        tableReceive.getColumnModel().getColumn(1).setHeaderValue("QTY ORDERED");
        tableReceive.getColumnModel().getColumn(2).setHeaderValue("CATEGORY");
        tableReceive.getColumnModel().getColumn(3).setHeaderValue("QTY RECEIVED");
        
        try{
            dbConn.pstmt = dbConn.conn.prepareStatement("SELECT * FROM tblProduct order by category");
            dbConn.rs = dbConn.pstmt.executeQuery();
            DefaultTableModel model = (DefaultTableModel)tableReceive.getModel();
            while (dbConn.rs.next()){
                String getProd = dbConn.rs.getString("productName");
                int duplicateRow =-1;
                for (int j=0; j<model.getRowCount();j++){
                    Object obj2 = model.getValueAt(j, 0);
                    if (obj2.equals(getProd)){
                        duplicateRow = j;
                        break;
                    }
                }
                if (duplicateRow == -1){
                    Object[] obj = {getProd,"0",dbConn.rs.getString("category")};
                 model.addRow(obj);
                }
            }
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
    private void printOrder(){
        Map param = new HashMap();
        param.put("lpoNumber", lblTranNumber.getText());
        try{
            dbConn.conn.close();
            Class.forName("com.mysql.jdbc.Driver");
            dbConn.conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbpos","root","root");
            JasperDesign jd = JRXmlLoader.load(new File("src\\Reports\\repLPO.jrxml"));
            JasperReport jr = JasperCompileManager.compileReport(jd);
            JasperPrint jp = JasperFillManager.fillReport(jr, param,dbConn.conn);
            JasperViewer.viewReport(jp,false);

        }catch(ClassNotFoundException | SQLException | JRException e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }

        
//        Map param = new HashMap();
//        param.put("date", String.valueOf(df.format(dateOrder.getDate())));
//        try{
//            dbConn.conn.close();
//            Class.forName("com.mysql.jdbc.Driver");
//            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbpos","root","root");
//            JasperDesign jd = JRXmlLoader.load(new File("src\\Reports\\repOrder.jrxml"));
//            JasperReport jr = JasperCompileManager.compileReport(jd);
//            JasperPrint jp = JasperFillManager.fillReport(jr, param,conn);
//            JasperViewer.viewReport(jp,false);
//
//        }catch(ClassNotFoundException | SQLException | JRException e){
//            JOptionPane.showMessageDialog(this, e.getMessage());
//        }
    
    }
    private void sortTableOrder(){
        DefaultTableModel model = (DefaultTableModel)tableOrder.getModel();
        model.setRowCount(0);
        model.addColumn("SKU");
        model.addColumn("ITEM NAME");
        model.addColumn("QTY");
        model.addColumn("PRICE");
        model.addColumn("SUM");
    }
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
            java.util.logging.Logger.getLogger(frmPurchaseOrder.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frmPurchaseOrder.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frmPurchaseOrder.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frmPurchaseOrder.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frmPurchaseOrder().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup btnCategory;
    private javax.swing.JButton btnCritical;
    private javax.swing.JButton btnProcessLPOPayment;
    private javax.swing.JButton btnReceiveAll;
    private javax.swing.JButton btnShowItemsBySupplier;
    private javax.swing.JButton btnShowOrder;
    private javax.swing.JComboBox<String> cmbSupplier;
    private com.toedter.calendar.JDateChooser dateOrder;
    private com.toedter.calendar.JDateChooser datePayLPO;
    private com.toedter.calendar.JDateChooser datePicker;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton30;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
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
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JScrollPane jscrollpane;
    private javax.swing.JLabel lblLPONumber;
    private javax.swing.JLabel lblSupplier;
    private javax.swing.JLabel lblTotalAmount;
    private javax.swing.JLabel lblTotalAmountLPOPayment;
    private javax.swing.JLabel lblTranNumber;
    private javax.swing.JTable tableMinimum;
    private javax.swing.JTable tableOrder;
    private javax.swing.JTable tableReceive;
    private javax.swing.JTable tblCritical;
    private javax.swing.JTable tblLPO;
    private javax.swing.JTable tblLPOPayment;
    private javax.swing.JTextField txtAmountPaid;
    private javax.swing.JTextField txtItem;
    // End of variables declaration//GEN-END:variables
}
