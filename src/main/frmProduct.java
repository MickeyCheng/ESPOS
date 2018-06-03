
package main;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import static main.frmLogin.showUserName;
import net.proteanit.sql.DbUtils;

public class frmProduct extends javax.swing.JFrame {
//ResultSet dbConn.rs;
//Connection conn;
//PreparedStatement dbConn.pstmt;
dbConnection dbConn = new dbConnection();
boolean add,edit;
String getProductID;
DecimalFormat df = new DecimalFormat("0.000");
double doCheckPrice,doCheckSupPrice;
int getMaxAuditID;
SimpleDateFormat sdfAudit = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
    public frmProduct() {
        initComponents();
        dbConn.doConnect();
        clearTexts();
        lockTexts();
        fillComboBox();
        fillTable();
        setLocationRelativeTo(this);
        fillComboSupplier();
        setDefaultCloseOperation(frmProduct.DISPOSE_ON_CLOSE);
        jIntFrmFave.setVisible(false);
        fillProductFavorite();
        fillFaveList();
        tblFaveList.setAutoCreateRowSorter(true);
        tableProduct.setAutoCreateRowSorter(true);
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {listenSearch();}
            @Override
            public void removeUpdate(DocumentEvent e) {listenSearch();}
            @Override
            public void changedUpdate(DocumentEvent e) {listenSearch();}
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
    private void fillFaveList(){
        try{
            dbConn.pstmt = dbConn.conn.prepareStatement("select productName,favorite from tblProduct where favorite<>? order by favorite");
            dbConn.pstmt.setString(1, "0");
            dbConn.rs = dbConn.pstmt.executeQuery();
            tblFaveList.setModel(DbUtils.resultSetToTableModel(dbConn.rs));
            dbConn.pstmt.close();
            tblFaveList.getColumnModel().getColumn(0).setHeaderValue("ITEM");
            tblFaveList.getColumnModel().getColumn(1).setHeaderValue("#");
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }    
    }
    private void fillProductFavorite(){
        cmbProduct.removeAllItems();
        cmbFaveNumber.removeAllItems();
        try{
            dbConn.pstmt = dbConn.conn.prepareStatement("Select * from tblproduct order by productName");
            dbConn.rs = dbConn.pstmt.executeQuery();
            while (dbConn.rs.next()){
                cmbProduct.addItem(dbConn.rs.getString("productName"));
            }
            dbConn.pstmt.close();
        }catch(SQLException e){
            e.getMessage();
        }
        for (int i=1; i<13;i++){
            cmbFaveNumber.addItem(String.valueOf(i));
        }
    }
    private void fillComboSupplier(){
        cmbSupplier.removeAllItems();
        cmbSupplier2.removeAllItems();
        try{
            dbConn.pstmt = dbConn.conn.prepareStatement("Select sm_name from tblSupplierMaster order by sm_name");
            dbConn.rs =dbConn.pstmt.executeQuery();
            while (dbConn.rs.next()){
                cmbSupplier.addItem(dbConn.rs.getString("sm_name"));
                cmbSupplier2.addItem(dbConn.rs.getString("sm_name"));
            }
            cmbSupplier.setSelectedIndex(-1);
            cmbSupplier2.setSelectedIndex(-1);
        }catch(SQLException e){
            e.getMessage();
        }
    }
    private void listenSearch(){
        try{
            String searchSQL = "Select productId,productName,unitPrice,supplier,stockOnHand "
                    + "from tblProduct where productName like? OR productId like? OR category like?";
            dbConn.pstmt = dbConn.conn.prepareStatement(searchSQL);
            dbConn.pstmt.setString(1,"%"+txtSearch.getText()+"%");
            dbConn.pstmt.setString(2,"%"+txtSearch.getText()+"%");
            dbConn.pstmt.setString(3,"%"+txtSearch.getText()+"%");
            dbConn.rs = dbConn.pstmt.executeQuery();
            tableProduct.setModel(DbUtils.resultSetToTableModel(dbConn.rs));
            tableProduct.getColumnModel().getColumn(0).setHeaderValue("BARCODE");
            tableProduct.getColumnModel().getColumn(1).setHeaderValue("ITEM NAME");
            tableProduct.getColumnModel().getColumn(2).setHeaderValue("UNIT PRICE");
            tableProduct.getColumnModel().getColumn(3).setHeaderValue("MAIN SUPPLIER");
            tableProduct.getColumnModel().getColumn(4).setHeaderValue("SOH");
        }catch(SQLException e){
            e.getMessage();
        }
    }

    private void fillTable(){
        try{
            String fillTable = "Select productId,productName,unitPrice,supplier,"
                    + "stockOnHand from tblProduct order by category";
            dbConn.pstmt = dbConn.conn.prepareStatement(fillTable);
            dbConn.rs = dbConn.pstmt.executeQuery();
            tableProduct.setModel(DbUtils.resultSetToTableModel(dbConn.rs));
            tableProduct.setAutoCreateRowSorter(true);
            tableProduct.getColumnModel().getColumn(0).setHeaderValue("BARCODE");
            tableProduct.getColumnModel().getColumn(1).setHeaderValue("ITEM NAME");
            tableProduct.getColumnModel().getColumn(2).setHeaderValue("UNIT PRICE");
            tableProduct.getColumnModel().getColumn(3).setHeaderValue("MAIN SUPPLIER");
            tableProduct.getColumnModel().getColumn(4).setHeaderValue("SOH");
        }catch(SQLException e){
            e.getMessage();
        }
    }    
    protected void fillComboBox(){
        cmbCategory.removeAllItems();
        try{
            dbConn.pstmt = dbConn.conn.prepareStatement("Select pc_name from tblProductCategory order by pc_id");
            dbConn.rs = dbConn.pstmt.executeQuery();
            while (dbConn.rs.next()){
                cmbCategory.addItem(dbConn.rs.getString("pc_name"));
            }
            dbConn.pstmt.close();
        }catch(SQLException e){
            e.getMessage();
        }
    }    
    private void unlockTexts(){
        txtPrice.setEnabled(true);
        txtProductName.setEnabled(true);
        txtProductId.setEnabled(true);
        txtStock.setEnabled(true);
        cmbSupplier2.setEnabled(true);
        txtSupplierPrice.setEnabled(true);
    }    
    private void lockTexts(){
        txtPrice.setEnabled(false);
        txtProductName.setEnabled(false);
        txtProductId.setEnabled(false);
        txtStock.setEnabled(false);
        cmbSupplier2.setEnabled(false);
        txtSupplierPrice.setEnabled(false);
    }    
    private void clearTexts(){
        txtPrice.setText("");
        txtProductId.setText("");
        txtStock.setText("");
        txtProductName.setText("");
        txtSupplierPrice.setText("");
    }    
//    private void doConnect(){
//    try{
//        Class.forName("com.mysql.jdbc.Driver");
//        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbpos","root","root");
//    }catch(SQLException | ClassNotFoundException e){
//        JOptionPane.showMessageDialog(this, e.getMessage());
//    }
//    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel4 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jIntFrmFave = new javax.swing.JInternalFrame();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblFaveList = new javax.swing.JTable();
        jLabel12 = new javax.swing.JLabel();
        cmbProduct = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        cmbFaveNumber = new javax.swing.JComboBox<>();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtPrice = new javax.swing.JTextField();
        txtProductId = new javax.swing.JTextField();
        txtProductName = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        cmbSupplier2 = new javax.swing.JComboBox();
        txtStock = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        cmbCategory = new javax.swing.JComboBox();
        cmbSupplier = new javax.swing.JComboBox();
        jButton2 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        txtSupplierPrice = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        txtSearch = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableProduct = new javax.swing.JTable();
        btnAdd = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnDelete1 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        mnuClose = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel4.setBackground(new java.awt.Color(214, 214, 194));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(214, 214, 194));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jIntFrmFave.setBackground(new java.awt.Color(214, 214, 194));
        jIntFrmFave.setVisible(true);
        jIntFrmFave.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tblFaveList.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tblFaveList);

        jIntFrmFave.getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 20, 210, 190));

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(0, 0, 0));
        jLabel12.setText("FAVE #:");
        jIntFrmFave.getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 100, 40));

        cmbProduct.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        cmbProduct.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jIntFrmFave.getContentPane().add(cmbProduct, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 20, 160, 40));

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(0, 0, 0));
        jLabel13.setText("PRODUCT NAME:");
        jIntFrmFave.getContentPane().add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 100, 40));

        cmbFaveNumber.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        cmbFaveNumber.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jIntFrmFave.getContentPane().add(cmbFaveNumber, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 90, 160, 40));

        jButton3.setBackground(new java.awt.Color(255, 255, 255));
        jButton3.setForeground(new java.awt.Color(0, 0, 0));
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/SaveIcon.png"))); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jIntFrmFave.getContentPane().add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 160, 110, 50));

        jButton4.setBackground(new java.awt.Color(255, 255, 255));
        jButton4.setForeground(new java.awt.Color(0, 0, 0));
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/closeIcon.png"))); // NOI18N
        jButton4.setText("CLOSE");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jIntFrmFave.getContentPane().add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 160, 120, 50));

        jPanel2.add(jIntFrmFave, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 540, 250));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("CATEGORY:");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 130, 20));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("PRODUCT ID:");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 130, 20));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("PRODUCT NAME:");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 130, 20));

        txtPrice.setNextFocusableComponent(cmbSupplier);
        jPanel2.add(txtPrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 130, 420, 30));

        txtProductId.setNextFocusableComponent(txtProductName);
        jPanel2.add(txtProductId, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 10, 420, 30));

        txtProductName.setNextFocusableComponent(cmbCategory);
        jPanel2.add(txtProductName, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 50, 420, 30));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("PRICE:");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 130, 20));

        cmbSupplier2.setNextFocusableComponent(txtSupplierPrice);
        jPanel2.add(cmbSupplier2, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 210, 200, 30));
        jPanel2.add(txtStock, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 210, 100, 30));

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("INITIAL STOCK:");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 210, 90, 20));

        jButton1.setBackground(new java.awt.Color(255, 255, 255));
        jButton1.setForeground(new java.awt.Color(0, 0, 0));
        jButton1.setText("PRODUCT CATEGORY");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 270, -1, 40));

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 0, 0));
        jLabel8.setText("MAIN SUPPLIER:");
        jPanel2.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 130, 20));

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 0, 0));
        jLabel9.setText("SUB SUPPLIER:");
        jPanel2.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, 130, 20));

        cmbCategory.setNextFocusableComponent(txtPrice);
        jPanel2.add(cmbCategory, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 90, 420, 30));

        cmbSupplier.setNextFocusableComponent(cmbSupplier2);
        jPanel2.add(cmbSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 170, 200, 30));

        jButton2.setBackground(new java.awt.Color(255, 255, 255));
        jButton2.setForeground(new java.awt.Color(0, 0, 0));
        jButton2.setText("SUPPLIER");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 270, 100, 40));

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 0, 0));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("SUPPLIER PRICE:");
        jPanel2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 170, 110, 20));

        txtSupplierPrice.setNextFocusableComponent(txtStock);
        jPanel2.add(txtSupplierPrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 170, 100, 30));
        jPanel2.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 250, 600, 20));

        jPanel4.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 600, 320));
        jPanel4.add(txtSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 430, 600, 30));

        tableProduct.setModel(new javax.swing.table.DefaultTableModel(
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
        tableProduct.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableProductMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tableProduct);

        jPanel4.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 470, 600, 170));

        btnAdd.setBackground(new java.awt.Color(255, 255, 255));
        btnAdd.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnAdd.setForeground(new java.awt.Color(0, 0, 0));
        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/NewIcon.png"))); // NOI18N
        btnAdd.setText("ADD");
        btnAdd.setFocusCycleRoot(true);
        btnAdd.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAdd.setNextFocusableComponent(txtProductId);
        btnAdd.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });
        jPanel4.add(btnAdd, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, 70));

        btnEdit.setBackground(new java.awt.Color(255, 255, 255));
        btnEdit.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnEdit.setForeground(new java.awt.Color(0, 0, 0));
        btnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/EditIcon.png"))); // NOI18N
        btnEdit.setText("EDIT");
        btnEdit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEdit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });
        jPanel4.add(btnEdit, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 90, 70));

        btnSave.setBackground(new java.awt.Color(255, 255, 255));
        btnSave.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnSave.setForeground(new java.awt.Color(0, 0, 0));
        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/SaveIcon.png"))); // NOI18N
        btnSave.setText("SAVE");
        btnSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jPanel4.add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 10, 90, 70));

        btnCancel.setBackground(new java.awt.Color(255, 255, 255));
        btnCancel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnCancel.setForeground(new java.awt.Color(0, 0, 0));
        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/cancelIcon.png"))); // NOI18N
        btnCancel.setText("CANCEL");
        btnCancel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCancel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        jPanel4.add(btnCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 10, 90, 70));

        btnDelete.setBackground(new java.awt.Color(255, 255, 255));
        btnDelete.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnDelete.setForeground(new java.awt.Color(0, 0, 0));
        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/DeleteIcon.png"))); // NOI18N
        btnDelete.setText("DELETE");
        btnDelete.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDelete.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        jPanel4.add(btnDelete, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 10, 90, 70));

        btnDelete1.setBackground(new java.awt.Color(255, 255, 255));
        btnDelete1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnDelete1.setForeground(new java.awt.Color(0, 0, 0));
        btnDelete1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/faveIcon.png"))); // NOI18N
        btnDelete1.setText("SET FAVE");
        btnDelete1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDelete1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnDelete1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelete1ActionPerformed(evt);
            }
        });
        jPanel4.add(btnDelete1, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 10, 90, 70));

        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("Search:");
        jPanel4.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 400, 90, -1));

        getContentPane().add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 630, 650));

        jMenu1.setText("File");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        jMenuItem1.setText("NEW");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem2.setText("SAVE");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem3.setText("MAIN MENU");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        mnuClose.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
        mnuClose.setText("Close");
        mnuClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCloseActionPerformed(evt);
            }
        });
        jMenu1.add(mnuClose);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        add = true;
        edit = false;
        unlockTexts();
        clearTexts();
        fillComboBox();
        txtProductId.requestFocus();
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        edit = true;
        add =false;
        unlockTexts();
        fillComboBox();
    }//GEN-LAST:event_btnEditActionPerformed
    private void validateTextboxes(){
        String checkUnitPrice,checkSupplierPrice;
        try{
            checkUnitPrice = txtPrice.getText();
            doCheckPrice = Double.valueOf(checkUnitPrice);
            txtPrice.setText(df.format(doCheckPrice));
        }catch(NumberFormatException e){
            JOptionPane.showMessageDialog(this, "CHECK UNIT PRICE","UNIT PRICE",JOptionPane.ERROR_MESSAGE);
        }
        
        try{
            checkSupplierPrice = txtSupplierPrice.getText();
            doCheckSupPrice = Double.valueOf(checkSupplierPrice);
            txtSupplierPrice.setText(df.format(doCheckSupPrice));
        }catch(NumberFormatException e){
            JOptionPane.showMessageDialog(this, "CHECK SUPPLIER PRICE","SUPPLIER",JOptionPane.ERROR_MESSAGE);
        }
    }
    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
       validateTextboxes();
        if (add == true && edit ==false){
            try{
                String insertTblProduct = "INSERT INTO tblProduct (productId,productName,unitPrice,category,"
                        + "stockOnHand,supplier,supplier2,supplierPrice) "
                        + "values (?,?,?,?,?,?,?,?)";
                dbConn.pstmt = dbConn.conn.prepareStatement(insertTblProduct);
                dbConn.pstmt.setString(1,txtProductId.getText());
                dbConn.pstmt.setString(2,txtProductName.getText());
                dbConn.pstmt.setString(3, txtPrice.getText());
                dbConn.pstmt.setString(4,cmbCategory.getSelectedItem().toString());
                dbConn.pstmt.setInt(5,Integer.valueOf(txtStock.getText()));
               
                dbConn.pstmt.setString(6, cmbSupplier.getSelectedItem().toString());
                if (cmbSupplier2.getSelectedIndex() == -1){
                    dbConn.pstmt.setString(7,"");
                }else{
                    dbConn.pstmt.setString(7, cmbSupplier2.getSelectedItem().toString());
                }
                dbConn.pstmt.setString(8, txtSupplierPrice.getText());
                dbConn.pstmt.execute();
                JOptionPane.showMessageDialog(this, "PRODUCT ADDED");
                dbConn.pstmt.close();
                saveAuditTrail("ADDED PRODUCT: " + txtProductName.getText());
                fillTable();
                clearTexts();
                lockTexts();
                add = false;
                edit = false;
            }catch(SQLException e){
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }else if (add == false && edit == true){
            try{
                String updateTblProduct = "UPDATE tblProduct set productId=?,productName=?,unitPrice=?,category=?,stockOnHand=?"
                        + ",supplier=?, supplier2=?, supplierPrice=?"
                        + " where productId=?";
                dbConn.pstmt = dbConn.conn.prepareStatement(updateTblProduct);
                dbConn.pstmt.setString(1,txtProductId.getText());
                dbConn.pstmt.setString(2,txtProductName.getText());
                dbConn.pstmt.setString(3, txtPrice.getText());
                dbConn.pstmt.setString(4,cmbCategory.getSelectedItem().toString());
                dbConn.pstmt.setInt(5,Integer.valueOf(txtStock.getText())); 
                dbConn.pstmt.setString(6, cmbSupplier.getSelectedItem().toString());
                if (cmbSupplier2.getSelectedIndex() == -1){
                    dbConn.pstmt.setString(7, "");
                }else{
                    dbConn.pstmt.setString(7, cmbSupplier2.getSelectedItem().toString());
                }
                dbConn.pstmt.setString(8, txtSupplierPrice.getText());              
                dbConn.pstmt.setString(9, getProductID);
                dbConn.pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "PRODUCT DETAILS EDITED");
                dbConn.pstmt.close();
                saveAuditTrail("EDITED PRODUCT: " + txtProductName.getText());
                fillTable();
                clearTexts();
                lockTexts();
                add = false;
                edit = false;
            }catch(SQLException e){
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
        
    }//GEN-LAST:event_btnSaveActionPerformed

    private void tableProductMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableProductMouseClicked
        int row = tableProduct.getSelectedRow();
        int ba = tableProduct.convertRowIndexToModel(row);
        try{
            String tblClick = (tableProduct.getModel().getValueAt(ba, 0).toString());
            String tableQuery = "Select * from tblProduct where productId=?";
            dbConn.pstmt = dbConn.conn.prepareStatement(tableQuery);
            dbConn.pstmt.setString(1, tblClick);
            dbConn.rs = dbConn.pstmt.executeQuery();
            if (dbConn.rs.next()){
                txtProductName.setText(dbConn.rs.getString("productName"));
                txtProductId.setText(dbConn.rs.getString("productId"));
                txtPrice.setText(dbConn.rs.getString("unitPrice"));
                txtStock.setText(String.valueOf(dbConn.rs.getInt("stockOnHand")));
                cmbCategory.setSelectedItem(dbConn.rs.getString("category"));
                cmbSupplier.setSelectedItem(dbConn.rs.getString("supplier"));                
                cmbSupplier2.setSelectedItem(dbConn.rs.getString("supplier2"));
                getProductID= dbConn.rs.getString("productId");
                txtSupplierPrice.setText(dbConn.rs.getString("supplierPrice"));
            }
        }catch(SQLException e){
            e.getMessage();
        }
    }//GEN-LAST:event_tableProductMouseClicked

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        btnAddActionPerformed(evt);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        btnSaveActionPerformed(evt);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        frmMain obj = new frmMain();
        obj.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        frmProductControl obj = new frmProductControl();
        obj.setVisible(true);
        
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        clearTexts();
        lockTexts();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        frmSupplierMaster obj = new frmSupplierMaster();
        obj.setVisible(true);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        int itemSelected = JOptionPane.showConfirmDialog(this, "DELETE RECORD?","DELETE",JOptionPane.YES_NO_OPTION);
        if (itemSelected==JOptionPane.YES_OPTION){
            try{
                dbConn.pstmt = dbConn.conn.prepareStatement("DELETE FROM tblProduct where productId=?");
                dbConn.pstmt.setString(1,txtProductId.getText());
                dbConn.pstmt.executeUpdate();
                dbConn.pstmt.close();
                JOptionPane.showMessageDialog(this, "RECORD DELETED");
                saveAuditTrail("DELETED PRODUCT: " + txtProductName.getText());
                clearTexts();
                fillTable();
            }catch(SQLException e){
                e.getMessage();
            }
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnDelete1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelete1ActionPerformed
        
        jIntFrmFave.setVisible(true);
    }//GEN-LAST:event_btnDelete1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        try{
            dbConn.pstmt = dbConn.conn.prepareStatement("UPDATE tblProduct set favorite=? where favorite=?");
            dbConn.pstmt.setString(1,"0");
            dbConn.pstmt.setString(2,cmbFaveNumber.getSelectedItem().toString());
            dbConn.pstmt.executeUpdate();
            dbConn.pstmt.close();
        }catch(SQLException e){
            e.getMessage();
        }
            
        try{
            dbConn.pstmt = dbConn.conn.prepareStatement("UPDATE tblProduct set favorite=? where productName=?");
            dbConn.pstmt.setString(1,cmbFaveNumber.getSelectedItem().toString());
            dbConn.pstmt.setString(2,cmbProduct.getSelectedItem().toString());
            dbConn.pstmt.executeUpdate();
            dbConn.pstmt.close();
            JOptionPane.showMessageDialog(this, "ITEM ADDED TO FAVORITE LIST");
            saveAuditTrail("ADDED TO FAVORITES: " + cmbProduct.getSelectedItem().toString() + 
                    "FOR #: " + cmbFaveNumber.getSelectedItem().toString());
        }catch(SQLException e){
            e.getMessage();
        }
        fillFaveList();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        jIntFrmFave.setVisible(false);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void mnuCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCloseActionPerformed
        this.dispose();
    }//GEN-LAST:event_mnuCloseActionPerformed

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
            java.util.logging.Logger.getLogger(frmProduct.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frmProduct.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frmProduct.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frmProduct.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frmProduct().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnDelete1;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox cmbCategory;
    private javax.swing.JComboBox<String> cmbFaveNumber;
    private javax.swing.JComboBox<String> cmbProduct;
    private javax.swing.JComboBox cmbSupplier;
    private javax.swing.JComboBox cmbSupplier2;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JInternalFrame jIntFrmFave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JMenuItem mnuClose;
    private javax.swing.JTable tableProduct;
    private javax.swing.JTable tblFaveList;
    private javax.swing.JTextField txtPrice;
    private javax.swing.JTextField txtProductId;
    private javax.swing.JTextField txtProductName;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtStock;
    private javax.swing.JTextField txtSupplierPrice;
    // End of variables declaration//GEN-END:variables
}
