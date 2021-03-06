package main;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import static main.frmLogin.showUserName;
import net.proteanit.sql.DbUtils;

public class frmProductControl extends javax.swing.JFrame {
//ResultSet dbConn.rs;
//Connection conn;
//PreparedStatement dbConn.pstmt;
boolean add, edit;
String getAccountName,tblClick;
int getMaxID;
int getMaxAuditID;
SimpleDateFormat sdfAudit = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
dbConnection dbConn = new dbConnection();
    public frmProductControl() {
        initComponents();
        dbConn.doConnect();
        fillTable();
        setDefaultCloseOperation(frmProductControl.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
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
    private void fillTable(){
        try{
            String fillUserTable = "Select * from tblproductcategory";
            dbConn.pstmt = dbConn.conn.prepareStatement(fillUserTable);
            dbConn.rs = dbConn.pstmt.executeQuery();
            tblProductControl.setModel(DbUtils.resultSetToTableModel(dbConn.rs));
            tblProductControl.getColumnModel().getColumn(0).setHeaderValue("ID");
            tblProductControl.getColumnModel().getColumn(1).setHeaderValue("CATEGORY");
        }catch(SQLException e){
            e.getMessage();
        }
    }
//    private void doConnect(){
//    try{
//        Class.forName("com.mysql.jdbc.Driver");
//        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbpos","root","root");
//    }catch (SQLException | ClassNotFoundException e){
//        e.getMessage();
//    }
//}

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
        jLabel1 = new javax.swing.JLabel();
        txtCategory = new javax.swing.JTextField();
        btnCancel = new javax.swing.JButton();
        btnAdd = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblProductControl = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(214, 214, 194));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(214, 214, 194));
        jPanel2.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(153, 153, 153)));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("CATEGORY:");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 100, 158, 48));
        jPanel2.add(txtCategory, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 100, 450, 48));

        btnCancel.setBackground(new java.awt.Color(255, 255, 255));
        btnCancel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnCancel.setForeground(new java.awt.Color(0, 0, 0));
        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/closeIcon.png"))); // NOI18N
        btnCancel.setText("CANCEL");
        btnCancel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCancel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        jPanel2.add(btnCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 10, 80, 70));

        btnAdd.setBackground(new java.awt.Color(255, 255, 255));
        btnAdd.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnAdd.setForeground(new java.awt.Color(0, 0, 0));
        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/NewIcon.png"))); // NOI18N
        btnAdd.setText("ADD");
        btnAdd.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAdd.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });
        jPanel2.add(btnAdd, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 10, 70, 70));

        btnEdit.setBackground(new java.awt.Color(255, 255, 255));
        btnEdit.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
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
        jPanel2.add(btnEdit, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 10, 70, 70));

        btnSave.setBackground(new java.awt.Color(255, 255, 255));
        btnSave.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
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
        jPanel2.add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 10, 70, 70));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 720, 170));

        tblProductControl.setBackground(new java.awt.Color(214, 214, 194));
        tblProductControl.setModel(new javax.swing.table.DefaultTableModel(
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
        tblProductControl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblProductControlMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblProductControl);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 190, 720, 180));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 760, 380));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        add = true;
        edit = false;  
        txtCategory.requestFocus();
        txtCategory.setText("");
        txtCategory.setEnabled(true);
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        edit= true;
        add= false;
        txtCategory.requestFocus();
        txtCategory.setEnabled(true);
    }//GEN-LAST:event_btnEditActionPerformed
    private void getNextID(){
        try{
            dbConn.pstmt = dbConn.conn.prepareStatement("SELECT pc_id from tblproductcategory order by pc_id DESC LIMIT 1");
            dbConn.rs = dbConn.pstmt.executeQuery();
            if (dbConn.rs.next()){
                getMaxID = dbConn.rs.getInt("pc_id");
                getMaxID++;
            }else{
                getMaxID =1;
            }
        }catch(SQLException e){
            e.getMessage();
        }    
    }
    
    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        getNextID();
        if (add == true && edit == false){
            try{
                dbConn.pstmt = dbConn.conn.prepareStatement("INSERT INTO tblproductcategory (pc_id, pc_name) values (?,?)");
                dbConn.pstmt.setInt(1, getMaxID);
                dbConn.pstmt.setString(2, txtCategory.getText());
                dbConn.pstmt.execute();
                JOptionPane.showMessageDialog(this, "PRODUCT CATEGORY SAVED");
                fillTable();
                saveAuditTrail("SAVED PRODUCT CATEGORY: " + txtCategory.getText());
                txtCategory.setText("");
                txtCategory.setEnabled(false);
                frmProduct obj = new frmProduct();
                obj.fillComboBox();
            }catch(SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }else if(add ==false && edit == true){
            
            try{
                dbConn.pstmt = dbConn.conn.prepareStatement("UPDATE tblproductcategory set pc_name=? where pc_id=?");
                dbConn.pstmt.setString(1, txtCategory.getText());
                dbConn.pstmt.setInt(2, Integer.valueOf(tblClick));
                dbConn.pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "PRODUCT CATEGORY UPDATED");
                fillTable();
                saveAuditTrail("EDITED PRODUCT CATEGORY: " + txtCategory.getText());
                txtCategory.setText("");
                txtCategory.setEnabled(false);
            }catch(SQLException e){
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void tblProductControlMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblProductControlMouseClicked
        int row = tblProductControl.getSelectedRow();
        int ba = tblProductControl.convertRowIndexToModel(row);
        try{
            tblClick = (tblProductControl.getModel().getValueAt(ba, 0).toString());
            String tableQuery = "Select * from tblproductcategory where pc_id=?";
            dbConn.pstmt = dbConn.conn.prepareStatement(tableQuery);
            dbConn.pstmt.setString(1,tblClick);
            dbConn.rs =dbConn.pstmt.executeQuery();
            if (dbConn.rs.next()){
                txtCategory.setText(dbConn.rs.getString("pc_name"));
            }
            dbConn.pstmt.close();
        }catch(SQLException e){
            e.getMessage();
        }
    }//GEN-LAST:event_tblProductControlMouseClicked

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        txtCategory.setEnabled(false);
    }//GEN-LAST:event_btnCancelActionPerformed

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
            java.util.logging.Logger.getLogger(frmProductControl.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frmProductControl.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frmProductControl.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frmProductControl.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frmProductControl().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblProductControl;
    private javax.swing.JTextField txtCategory;
    // End of variables declaration//GEN-END:variables
}
