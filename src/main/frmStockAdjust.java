package main;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import static main.frmLogin.showUserName;
import net.proteanit.sql.DbUtils;

public class frmStockAdjust extends javax.swing.JFrame {
//ResultSet dbConn.rs;
//Connection dbConn.conn;
//PreparedStatement dbConn.pstmt;
int i=0,j=0;
String getProdId;
int getMaxAuditID;
SimpleDateFormat sdfAudit = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
dbConnection dbConn = new dbConnection();
    public frmStockAdjust() {
        initComponents();
        dbConn.doConnect();
        fillTable();
        setLocationRelativeTo(null);
        cmbAdjust.removeAllItems();
        cmbAdjust1.removeAllItems();
        tableProduct.setAutoCreateRowSorter(true);
        setDefaultCloseOperation(frmProductControl.DISPOSE_ON_CLOSE);
        while(i<101 && j<101){
        cmbAdjust.addItem(i++);
        cmbAdjust1.addItem("-"+j++);
        }
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
            dbConn.pstmt = dbConn.conn.prepareStatement("SELECT * from tblaudittrail order by at_id DESC LIMIT 1");
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
            e.getMessage();
        }
    }
    private void listenSearch(){
        try{
            String searchSQL = "Select * from tblproduct where productId like? OR productName like?";
            dbConn.pstmt = dbConn.conn.prepareStatement(searchSQL);
            dbConn.pstmt.setString(1, "%"+txtSearch.getText()+"%");
            dbConn.pstmt.setString(2, "%"+txtSearch.getText()+"%");
            dbConn.rs = dbConn.pstmt.executeQuery();
            tableProduct.setModel(DbUtils.resultSetToTableModel(dbConn.rs));
            sortTable();
       }catch(SQLException e){
            e.getMessage();
       }

       }
    private void fillTable(){
        try{
            dbConn.pstmt = dbConn.conn.prepareStatement("Select * from tblproduct order by category");
            dbConn.rs = dbConn.pstmt.executeQuery();
            tableProduct.setModel(DbUtils.resultSetToTableModel(dbConn.rs));
            sortTable();
        }catch(SQLException e){
            e.getMessage();
        }
    }
    private void sortTable(){
        tableProduct.getColumnModel().getColumn(0).setHeaderValue("BARCODE");
        tableProduct.getColumnModel().getColumn(1).setHeaderValue("NAME");
        tableProduct.getColumnModel().getColumn(2).setHeaderValue("U/PRICE");
        tableProduct.getColumnModel().getColumn(3).setHeaderValue("CATEGORY");
        tableProduct.getColumnModel().getColumn(4).setHeaderValue("SOH");
        tableProduct.getColumnModel().getColumn(5).setHeaderValue("MIN LEVEL");
        tableProduct.getColumnModel().getColumn(6).setHeaderValue("MAX LEVEL");
    }
private void doConnect(){
try{
    Class.forName("com.mysql.jdbc.Driver");
    dbConn.conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbpos","root","root");
}catch(SQLException | ClassNotFoundException e){
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
        jScrollPane1 = new javax.swing.JScrollPane();
        tableProduct = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        lblStock = new javax.swing.JLabel();
        lblProdName = new javax.swing.JLabel();
        lblPrice = new javax.swing.JLabel();
        lblCategory = new javax.swing.JLabel();
        lblProdId = new javax.swing.JLabel();
        cmbAdjust = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        cmbAdjust1 = new javax.swing.JComboBox();
        jButton2 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(214, 214, 194));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tableProduct.setBackground(new java.awt.Color(214, 214, 194));
        tableProduct.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
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

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, 720, 180));

        jPanel2.setBackground(new java.awt.Color(214, 214, 194));
        jPanel2.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setBackground(new java.awt.Color(214, 214, 194));
        jPanel3.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 0, 0));
        jLabel7.setText("CATEGORY");
        jPanel3.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 190, 110, 40));

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 0, 0));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("ADJUST +");
        jPanel3.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 60, 110, 40));

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 0, 0));
        jLabel11.setText("UNIT PRICE:");
        jPanel3.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 110, 40));

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(0, 0, 0));
        jLabel12.setText("NAME:");
        jPanel3.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 110, 40));

        lblStock.setBackground(new java.awt.Color(255, 255, 255));
        lblStock.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblStock.setForeground(new java.awt.Color(0, 0, 0));
        lblStock.setText("0");
        jPanel3.add(lblStock, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 10, 200, 40));

        lblProdName.setBackground(new java.awt.Color(255, 255, 255));
        lblProdName.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblProdName.setForeground(new java.awt.Color(0, 0, 0));
        jPanel3.add(lblProdName, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 70, 220, 40));

        lblPrice.setBackground(new java.awt.Color(255, 255, 255));
        lblPrice.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblPrice.setForeground(new java.awt.Color(0, 0, 0));
        jPanel3.add(lblPrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 130, 220, 40));

        lblCategory.setBackground(new java.awt.Color(255, 255, 255));
        lblCategory.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblCategory.setForeground(new java.awt.Color(0, 0, 0));
        jPanel3.add(lblCategory, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 190, 220, 40));

        lblProdId.setBackground(new java.awt.Color(255, 255, 255));
        lblProdId.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblProdId.setForeground(new java.awt.Color(0, 0, 0));
        jPanel3.add(lblProdId, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 10, 220, 40));

        jPanel3.add(cmbAdjust, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 110, 110, 40));

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 0, 0));
        jLabel9.setText("STOCK:");
        jPanel3.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 10, 90, 40));

        jButton1.setBackground(new java.awt.Color(255, 255, 255));
        jButton1.setForeground(new java.awt.Color(0, 0, 0));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/plusIcon.png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 160, 80, 50));

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 0, 0));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("ADJUST -");
        jPanel3.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 60, 90, 40));

        jPanel3.add(cmbAdjust1, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 110, 90, 40));

        jButton2.setBackground(new java.awt.Color(255, 255, 255));
        jButton2.setForeground(new java.awt.Color(0, 0, 0));
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/minusIcon.png"))); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 160, 80, 50));

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("BARCODE:");
        jPanel3.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 110, 40));

        jPanel2.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 700, 240));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 280, 720, 260));
        jPanel1.add(txtSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 240, 30));

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("SEARCH ITEM:");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 140, 30));

        jButton3.setBackground(new java.awt.Color(255, 255, 255));
        jButton3.setForeground(new java.awt.Color(0, 0, 0));
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/resetIcon.png"))); // NOI18N
        jButton3.setText("RESET");
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 10, 100, 70));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 750, 540));

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

    private void tableProductMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableProductMouseClicked
        int row = tableProduct.getSelectedRow();
        int ba = tableProduct.convertRowIndexToModel(row);
        String tblClick = (tableProduct.getModel().getValueAt(ba, 0).toString());
        String selectedItem = "Select * from tblproduct where productId =?";
        try{
            dbConn.pstmt = dbConn.conn.prepareStatement(selectedItem);
            dbConn.pstmt.setString(1, tblClick);
            dbConn.rs = dbConn.pstmt.executeQuery();
            if (dbConn.rs.next()){
                lblProdId.setText(dbConn.rs.getString("productId"));
                lblProdName.setText(dbConn.rs.getString("productName"));
                lblPrice.setText(dbConn.rs.getString("unitPrice"));
                lblCategory.setText(dbConn.rs.getString("category"));
                lblStock.setText(String.valueOf(dbConn.rs.getInt("stockOnHand")));
                getProdId = tblClick;
            }
            dbConn.pstmt.close();
        }catch(SQLException e){
            e.getMessage();
        }
    }//GEN-LAST:event_tableProductMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try{
            if (lblStock.getText().equals("0")){
                String adjustSQL = "UPDATE tblproduct set stockOnhand=? where productId=?";
                dbConn.pstmt = dbConn.conn.prepareStatement(adjustSQL);
                dbConn.pstmt.setString(1,cmbAdjust.getSelectedItem().toString());
                dbConn.pstmt.setString(2, getProdId);
            }else{
                String adjustSQL = "UPDATE tblproduct set stockOnhand=stockOnHand+? where productId=?";
                dbConn.pstmt = dbConn.conn.prepareStatement(adjustSQL);
                dbConn.pstmt.setString(1,cmbAdjust.getSelectedItem().toString());
                dbConn.pstmt.setString(2, getProdId);
            }
            dbConn.pstmt.executeUpdate();
            dbConn.pstmt.close();
            JOptionPane.showMessageDialog(this, "QTY ADJUSTED");
            saveAuditTrail("ADJUSTED + STOCK FOR: " + getProdId +
                    " BY: " + cmbAdjust.getSelectedItem().toString());
            listenSearch();
        }catch(SQLException e){
            e.getMessage();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if (lblStock.getText().equals("0")){
            JOptionPane.showMessageDialog(this, "STOCK IS AT 0 QTY","STOCK DEPLETED",JOptionPane.ERROR_MESSAGE);
        }else{
            String setStock = cmbAdjust1.getSelectedItem().toString().substring(1);
            try{
                if (Integer.valueOf(lblStock.getText()) < Integer.valueOf(setStock)){
                    JOptionPane.showMessageDialog(this, "WILL RESULT TO NEGATIVE QTY","NEGATIVE",JOptionPane.ERROR_MESSAGE);
                }else{
                String adjustSQL = "UPDATE tblproduct set stockOnhand=stockOnHand-? where productId=?";
                    dbConn.pstmt = dbConn.conn.prepareStatement(adjustSQL);
                    dbConn.pstmt.setString(1,setStock);
                    dbConn.pstmt.setString(2, getProdId);
                    dbConn.pstmt.executeUpdate();
                    dbConn.pstmt.close();
                    JOptionPane.showMessageDialog(this, "QTY ADJUSTED");
            saveAuditTrail("ADJUSTED - STOCK FOR: " + getProdId +
                    " BY: " + cmbAdjust1.getSelectedItem().toString());
                    listenSearch();
                }
            }catch(SQLException e){
                e.getMessage();
            }
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
fillTable();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        frmMain obj = new frmMain();
        obj.setVisible(true);
        this.dispose();
        try{
            dbConn.conn.close();
            dbConn.rs.close();
            dbConn.pstmt.close();
        }catch(SQLException e){
            e.getMessage();
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

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
            java.util.logging.Logger.getLogger(frmStockAdjust.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frmStockAdjust.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frmStockAdjust.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frmStockAdjust.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frmStockAdjust().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cmbAdjust;
    private javax.swing.JComboBox cmbAdjust1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCategory;
    private javax.swing.JLabel lblPrice;
    private javax.swing.JLabel lblProdId;
    private javax.swing.JLabel lblProdName;
    private javax.swing.JLabel lblStock;
    private javax.swing.JTable tableProduct;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
