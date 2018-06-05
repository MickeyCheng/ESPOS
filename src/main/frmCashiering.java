
package main;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import static java.lang.Thread.sleep;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import static main.frmLogin.showUserName;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

public class frmCashiering extends javax.swing.JFrame {
Connection conn;
ResultSet rs;
PreparedStatement pstmt;
int iChecker =0, row, ba, getTransactionID;
DecimalFormat df = new DecimalFormat("#.000");
double showAmountPaid;
int getMaxAuditID;
SimpleDateFormat sdfAudit = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
int qty;
String getPaymentMethod;
dbConnection dbConn = new dbConnection();
    public frmCashiering() {
        initComponents();
        dbConn.doConnect();
        sortTable();
        runningTime();
//        btnCash.setText("<html><center>CASH <br> PAYMENT</html>");
//        btnNew.setText("<html><center>NEW <br>TRANSACTION</html>");
        txtSearch.requestFocus();
        setExtendedState(frmCashiering.MAXIMIZED_BOTH);
        setDefaultCloseOperation(frmProductControl.DISPOSE_ON_CLOSE);
        intFrameCard.setVisible(false);
        txtCardNumber.setText("");
        txtCardReference.setText("");
        intFrameFoc.setVisible(false);
        getRootPane().setDefaultButton(btnEnter);
        lblDailyCash.setVisible(false);
        lblDailyCard.setVisible(false);
        lblDailyFoc.setVisible(false);
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {listenSearch();}
            @Override
            public void removeUpdate(DocumentEvent e) {listenSearch();}
            @Override
            public void changedUpdate(DocumentEvent e) {listenSearch();}
        });          
        fillFavorites();
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
            JOptionPane.showMessageDialog(this, e.getMessage());
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
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
    private void fillFavorites(){
        try{
        dbConn.pstmt = dbConn.conn.prepareStatement("Select * from tblproduct where favorite=?");
        dbConn.pstmt.setString(1, "1");
        dbConn.rs = dbConn.pstmt.executeQuery();
        if (dbConn.rs.next()){
            btnFav1.setText(dbConn.rs.getString("productName"));
        }
        dbConn.pstmt.close();
    }catch(SQLException e){
        JOptionPane.showMessageDialog(this, e.getMessage());
    }
    try{
        dbConn.pstmt = dbConn.conn.prepareStatement("Select * from tblproduct where favorite=?");
        dbConn.pstmt.setString(1, "2");
        dbConn.rs = dbConn.pstmt.executeQuery();
        if (dbConn.rs.next()){
            btnFav2.setText(dbConn.rs.getString("productName"));
        }
        dbConn.pstmt.close();
    }catch(SQLException e){
        JOptionPane.showMessageDialog(this, e.getMessage());
    }try{
        dbConn.pstmt = dbConn.conn.prepareStatement("Select * from tblproduct where favorite=?");
        dbConn.pstmt.setString(1, "3");
        dbConn.rs = dbConn.pstmt.executeQuery();
        if (dbConn.rs.next()){
            btnFav3.setText(dbConn.rs.getString("productName"));
        }
        dbConn.pstmt.close();
    }catch(SQLException e){
        JOptionPane.showMessageDialog(this, e.getMessage());
    }try{
        dbConn.pstmt = dbConn.conn.prepareStatement("Select * from tblproduct where favorite=?");
        dbConn.pstmt.setString(1, "4");
        dbConn.rs = dbConn.pstmt.executeQuery();
        if (dbConn.rs.next()){
            btnFav4.setText(dbConn.rs.getString("productName"));
        }
        dbConn.pstmt.close();
    }catch(SQLException e){
        JOptionPane.showMessageDialog(this, e.getMessage());
    }try{
        dbConn.pstmt = dbConn.conn.prepareStatement("Select * from tblproduct where favorite=?");
        dbConn.pstmt.setString(1, "5");
        dbConn.rs = dbConn.pstmt.executeQuery();
        if (dbConn.rs.next()){
            btnFav5.setText(dbConn.rs.getString("productName"));
        }
        dbConn.pstmt.close();
    }catch(SQLException e){
        JOptionPane.showMessageDialog(this, e.getMessage());
    }try{
        dbConn.pstmt = dbConn.conn.prepareStatement("Select * from tblproduct where favorite=?");
        dbConn.pstmt.setString(1, "6");
        dbConn.rs = dbConn.pstmt.executeQuery();
        if (dbConn.rs.next()){
            btnFav6.setText(dbConn.rs.getString("productName"));
        }
        dbConn.pstmt.close();
    }catch(SQLException e){
        JOptionPane.showMessageDialog(this, e.getMessage());
    }try{
        dbConn.pstmt = dbConn.conn.prepareStatement("Select * from tblproduct where favorite=?");
        dbConn.pstmt.setString(1, "7");
        dbConn.rs = dbConn.pstmt.executeQuery();
        if (dbConn.rs.next()){
            btnFav7.setText(dbConn.rs.getString("productName"));
        }
        dbConn.pstmt.close();
    }catch(SQLException e){
        JOptionPane.showMessageDialog(this, e.getMessage());
    }try{
        dbConn.pstmt = dbConn.conn.prepareStatement("Select * from tblproduct where favorite=?");
        dbConn.pstmt.setString(1, "8");
        dbConn.rs = dbConn.pstmt.executeQuery();
        if (dbConn.rs.next()){
            btnFav8.setText(dbConn.rs.getString("productName"));
        }
        dbConn.pstmt.close();
    }catch(SQLException e){
        JOptionPane.showMessageDialog(this, e.getMessage());
    }try{
        dbConn.pstmt = dbConn.conn.prepareStatement("Select * from tblproduct where favorite=?");
        dbConn.pstmt.setString(1, "9");
        dbConn.rs = dbConn.pstmt.executeQuery();
        if (dbConn.rs.next()){
            btnFav9.setText(dbConn.rs.getString("productName"));
        }
        dbConn.pstmt.close();
    }catch(SQLException e){
        JOptionPane.showMessageDialog(this, e.getMessage());
    }try{
        dbConn.pstmt = dbConn.conn.prepareStatement("Select * from tblproduct where favorite=?");
        dbConn.pstmt.setString(1, "10");
        dbConn.rs = dbConn.pstmt.executeQuery();
        if (dbConn.rs.next()){
            btnFav10.setText(dbConn.rs.getString("productName"));
        }
        dbConn.pstmt.close();
    }catch(SQLException e){
        JOptionPane.showMessageDialog(this, e.getMessage());
    }try{
        dbConn.pstmt = dbConn.conn.prepareStatement("Select * from tblproduct where favorite=?");
        dbConn.pstmt.setString(1, "11");
        dbConn.rs = dbConn.pstmt.executeQuery();
        if (dbConn.rs.next()){
            btnFav11.setText(dbConn.rs.getString("productName"));
        }
        dbConn.pstmt.close();
    }catch(SQLException e){
        JOptionPane.showMessageDialog(this, e.getMessage());
    }try{
        dbConn.pstmt = dbConn.conn.prepareStatement("Select * from tblproduct where favorite=?");
        dbConn.pstmt.setString(1, "12");
        dbConn.rs = dbConn.pstmt.executeQuery();
        if (dbConn.rs.next()){
            btnFav12.setText(dbConn.rs.getString("productName"));
        }
        dbConn.pstmt.close();
    }catch(SQLException e){
        JOptionPane.showMessageDialog(this, e.getMessage());
    }
//    try{
//        pstmt = conn.prepareStatement("Select * from tblproduct where favorite=?");
//        pstmt.setString(1, "1");
//        rs = pstmt.executeQuery();
//        if (rs.next()){
//            btnFav1.setText(rs.getString("productName"));
//        }
//        pstmt.close();
//    }catch(SQLException e){
//        JOptionPane.showMessageDialog(this, e.getMessage());
//    }
//    try{
//        pstmt = conn.prepareStatement("Select * from tblproduct where favorite=?");
//        pstmt.setString(1, "2");
//        rs = pstmt.executeQuery();
//        if (rs.next()){
//            btnFav2.setText(rs.getString("productName"));
//        }
//        pstmt.close();
//    }catch(SQLException e){
//        JOptionPane.showMessageDialog(this, e.getMessage());
//    }try{
//        pstmt = conn.prepareStatement("Select * from tblproduct where favorite=?");
//        pstmt.setString(1, "3");
//        rs = pstmt.executeQuery();
//        if (rs.next()){
//            btnFav3.setText(rs.getString("productName"));
//        }
//        pstmt.close();
//    }catch(SQLException e){
//        JOptionPane.showMessageDialog(this, e.getMessage());
//    }try{
//        pstmt = conn.prepareStatement("Select * from tblproduct where favorite=?");
//        pstmt.setString(1, "4");
//        rs = pstmt.executeQuery();
//        if (rs.next()){
//            btnFav4.setText(rs.getString("productName"));
//        }
//        pstmt.close();
//    }catch(SQLException e){
//        JOptionPane.showMessageDialog(this, e.getMessage());
//    }try{
//        pstmt = conn.prepareStatement("Select * from tblproduct where favorite=?");
//        pstmt.setString(1, "5");
//        rs = pstmt.executeQuery();
//        if (rs.next()){
//            btnFav5.setText(rs.getString("productName"));
//        }
//        pstmt.close();
//    }catch(SQLException e){
//        JOptionPane.showMessageDialog(this, e.getMessage());
//    }try{
//        pstmt = conn.prepareStatement("Select * from tblproduct where favorite=?");
//        pstmt.setString(1, "6");
//        rs = pstmt.executeQuery();
//        if (rs.next()){
//            btnFav6.setText(rs.getString("productName"));
//        }
//        pstmt.close();
//    }catch(SQLException e){
//        JOptionPane.showMessageDialog(this, e.getMessage());
//    }try{
//        pstmt = conn.prepareStatement("Select * from tblproduct where favorite=?");
//        pstmt.setString(1, "7");
//        rs = pstmt.executeQuery();
//        if (rs.next()){
//            btnFav7.setText(rs.getString("productName"));
//        }
//        pstmt.close();
//    }catch(SQLException e){
//        JOptionPane.showMessageDialog(this, e.getMessage());
//    }try{
//        pstmt = conn.prepareStatement("Select * from tblproduct where favorite=?");
//        pstmt.setString(1, "8");
//        rs = pstmt.executeQuery();
//        if (rs.next()){
//            btnFav8.setText(rs.getString("productName"));
//        }
//        pstmt.close();
//    }catch(SQLException e){
//        JOptionPane.showMessageDialog(this, e.getMessage());
//    }try{
//        pstmt = conn.prepareStatement("Select * from tblproduct where favorite=?");
//        pstmt.setString(1, "9");
//        rs = pstmt.executeQuery();
//        if (rs.next()){
//            btnFav9.setText(rs.getString("productName"));
//        }
//        pstmt.close();
//    }catch(SQLException e){
//        JOptionPane.showMessageDialog(this, e.getMessage());
//    }try{
//        pstmt = conn.prepareStatement("Select * from tblproduct where favorite=?");
//        pstmt.setString(1, "10");
//        rs = pstmt.executeQuery();
//        if (rs.next()){
//            btnFav10.setText(rs.getString("productName"));
//        }
//        pstmt.close();
//    }catch(SQLException e){
//        JOptionPane.showMessageDialog(this, e.getMessage());
//    }try{
//        pstmt = conn.prepareStatement("Select * from tblproduct where favorite=?");
//        pstmt.setString(1, "11");
//        rs = pstmt.executeQuery();
//        if (rs.next()){
//            btnFav11.setText(rs.getString("productName"));
//        }
//        pstmt.close();
//    }catch(SQLException e){
//        JOptionPane.showMessageDialog(this, e.getMessage());
//    }try{
//        pstmt = conn.prepareStatement("Select * from tblproduct where favorite=?");
//        pstmt.setString(1, "12");
//        rs = pstmt.executeQuery();
//        if (rs.next()){
//            btnFav12.setText(rs.getString("productName"));
//        }
//        pstmt.close();
//    }catch(SQLException e){
//        JOptionPane.showMessageDialog(this, e.getMessage());
//    }
    }
    private void sortTable(){
        DefaultTableModel model = (DefaultTableModel)tableInvoice.getModel();   
        model.addColumn("PRODUCT ID");
        model.addColumn("PRODUCT NAME");
        model.addColumn("UNIT PRICE");
        model.addColumn("QUANTITY");
        model.addColumn("BD");
    }
    private void listenSearch(){
     try{
         String searchSQL = "Select * from tblproduct where productid like ? OR productname like?";
         dbConn.pstmt = dbConn.conn.prepareStatement(searchSQL);
         dbConn.pstmt.setString(1, "%"+txtSearch.getText()+"%");
         dbConn.pstmt.setString(2, "%"+txtSearch.getText()+"%");
         dbConn.rs = dbConn.pstmt.executeQuery();
         if (dbConn.rs.next()){
             lblProductId.setText(dbConn.rs.getString("productId"));
             lblProductName.setText(dbConn.rs.getString("productName"));
             lblUnitPrice.setText(dbConn.rs.getString("unitPrice"));
             txtQuantity.setText("1");
         }
     }catch(SQLException e){
         e.getMessage();
     }
    }
    private void runningTime(){
        Thread clock = new Thread(){
        public void run(){       
        for(;;){        
        Calendar cal = new GregorianCalendar();
//        int month = cal.get(Calendar.MONTH);
//        int year = cal.get(Calendar.YEAR);
//        int day = cal.get(Calendar.DAY_OF_MONTH);
//            lblDate.setText(day+"-"+(month+1)+"-"+year);
        lblDate.setText(dbConn.sdfDateGlobal.format(dbConn.todayDateGlobal));
        int second = cal.get(Calendar.SECOND);
        int minute = cal.get(Calendar.MINUTE);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
            lblTime.setText(hour+":"+(minute)+":"+second);
        try {
        sleep(1000);
        } catch (InterruptedException ex) {
        Logger.getLogger(frmCashiering.class.getName()).log(Level.SEVERE, null, ex);
        }    
        }}};
        clock.start();
    } 
    private void doConnect(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbpos","root","root");
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

        jPanel5 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        intFrameFoc = new javax.swing.JInternalFrame();
        jPanel7 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        txtComment = new javax.swing.JTextField();
        btnProcessCard1 = new javax.swing.JButton();
        btnCancelCard1 = new javax.swing.JButton();
        intFrameCard = new javax.swing.JInternalFrame();
        jPanel6 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtCardReference = new javax.swing.JTextField();
        txtCardNumber = new javax.swing.JTextField();
        btnProcessCard = new javax.swing.JButton();
        btnCancelCard = new javax.swing.JButton();
        lblChange = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        bd3 = new javax.swing.JButton();
        bd500 = new javax.swing.JButton();
        bd1 = new javax.swing.JButton();
        bd2 = new javax.swing.JButton();
        bd20 = new javax.swing.JButton();
        bd5 = new javax.swing.JButton();
        bd10 = new javax.swing.JButton();
        btnCash = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtAmountPaid = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lblTotalAmount = new javax.swing.JLabel();
        bd100 = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        bd50 = new javax.swing.JButton();
        btnCard = new javax.swing.JButton();
        btnFoc = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        btnPrintLastReceipt = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        btnReportZ = new javax.swing.JButton();
        jButton112 = new javax.swing.JButton();
        jButton114 = new javax.swing.JButton();
        btnNew = new javax.swing.JButton();
        lblDailyFoc = new javax.swing.JLabel();
        lblDailyCash = new javax.swing.JLabel();
        lblDailyCard = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        lblUnitPrice = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblProductId = new javax.swing.JLabel();
        lblProductName = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtQuantity = new javax.swing.JTextField();
        btnEnter = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableInvoice = new javax.swing.JTable();
        btnRemoveItem = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        btnFav4 = new javax.swing.JButton();
        btnFav1 = new javax.swing.JButton();
        btnFav2 = new javax.swing.JButton();
        btnFav3 = new javax.swing.JButton();
        btnFav8 = new javax.swing.JButton();
        btnFav7 = new javax.swing.JButton();
        btnFav6 = new javax.swing.JButton();
        btnFav5 = new javax.swing.JButton();
        btnFav9 = new javax.swing.JButton();
        btnFav10 = new javax.swing.JButton();
        btnFav11 = new javax.swing.JButton();
        btnFav12 = new javax.swing.JButton();
        lblTime = new javax.swing.JLabel();
        lblDate = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(51, 51, 51));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel5.setBackground(new java.awt.Color(214, 214, 194));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel4.setBackground(new java.awt.Color(214, 214, 194));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        intFrameFoc.setBackground(new java.awt.Color(214, 214, 194));
        intFrameFoc.setVisible(true);
        intFrameFoc.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel7.setBackground(new java.awt.Color(214, 214, 194));
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setForeground(new java.awt.Color(0, 0, 0));
        jLabel15.setText("FOC COMMENT:");
        jPanel7.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 120, 20));
        jPanel7.add(txtComment, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 10, 290, -1));

        btnProcessCard1.setText("PROCESS FOC");
        btnProcessCard1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProcessCard1ActionPerformed(evt);
            }
        });
        jPanel7.add(btnProcessCard1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, 190, 40));

        btnCancelCard1.setText("CANCEL");
        btnCancelCard1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelCard1ActionPerformed(evt);
            }
        });
        jPanel7.add(btnCancelCard1, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 40, 190, 40));

        intFrameFoc.getContentPane().add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 450, 100));

        jPanel4.add(intFrameFoc, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 30, 480, 140));

        intFrameCard.setBackground(new java.awt.Color(214, 214, 194));
        intFrameCard.setVisible(true);
        intFrameCard.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel6.setBackground(new java.awt.Color(214, 214, 194));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel12.setForeground(new java.awt.Color(0, 0, 0));
        jLabel12.setText("REFERENCE NUMBER:");
        jPanel6.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 120, 20));
        jLabel12.getAccessibleContext().setAccessibleName("REF NUMBER:");

        jLabel13.setForeground(new java.awt.Color(0, 0, 0));
        jLabel13.setText("CARD NUMBER:");
        jPanel6.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 120, 20));

        txtCardReference.setForeground(new java.awt.Color(0, 0, 0));
        jPanel6.add(txtCardReference, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 40, 290, -1));

        txtCardNumber.setForeground(new java.awt.Color(0, 0, 0));
        jPanel6.add(txtCardNumber, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 10, 290, -1));

        btnProcessCard.setBackground(new java.awt.Color(255, 255, 255));
        btnProcessCard.setForeground(new java.awt.Color(0, 0, 0));
        btnProcessCard.setText("PROCESS CARD");
        btnProcessCard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProcessCardActionPerformed(evt);
            }
        });
        jPanel6.add(btnProcessCard, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, 190, 40));

        btnCancelCard.setBackground(new java.awt.Color(255, 255, 255));
        btnCancelCard.setForeground(new java.awt.Color(0, 0, 0));
        btnCancelCard.setText("CANCEL");
        btnCancelCard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelCardActionPerformed(evt);
            }
        });
        jPanel6.add(btnCancelCard, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 90, 190, 40));

        intFrameCard.getContentPane().add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 450, 160));

        jPanel4.add(intFrameCard, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 20, 490, 210));

        lblChange.setBackground(new java.awt.Color(153, 0, 0));
        lblChange.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblChange.setForeground(new java.awt.Color(255, 255, 255));
        lblChange.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblChange.setOpaque(true);
        jPanel4.add(lblChange, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 190, 160, 40));

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("AMOUNT PAID:");
        jPanel4.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 10, 130, 30));

        bd3.setBackground(new java.awt.Color(255, 255, 255));
        bd3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        bd3.setForeground(new java.awt.Color(0, 0, 0));
        bd3.setText("BD 3");
        bd3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bd3ActionPerformed(evt);
            }
        });
        jPanel4.add(bd3, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 80, 90, 50));

        bd500.setBackground(new java.awt.Color(255, 255, 255));
        bd500.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        bd500.setForeground(new java.awt.Color(0, 0, 0));
        bd500.setText("0.500");
        bd500.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bd500ActionPerformed(evt);
            }
        });
        jPanel4.add(bd500, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 20, 90, 50));

        bd1.setBackground(new java.awt.Color(255, 255, 255));
        bd1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        bd1.setForeground(new java.awt.Color(0, 0, 0));
        bd1.setText("BD 1");
        bd1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bd1ActionPerformed(evt);
            }
        });
        jPanel4.add(bd1, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 80, 100, 50));

        bd2.setBackground(new java.awt.Color(255, 255, 255));
        bd2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        bd2.setForeground(new java.awt.Color(0, 0, 0));
        bd2.setText("BD 2");
        bd2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bd2ActionPerformed(evt);
            }
        });
        jPanel4.add(bd2, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 80, 80, 50));

        bd20.setBackground(new java.awt.Color(255, 255, 255));
        bd20.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        bd20.setForeground(new java.awt.Color(0, 0, 0));
        bd20.setText("BD 20");
        bd20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bd20ActionPerformed(evt);
            }
        });
        jPanel4.add(bd20, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 140, 90, 50));

        bd5.setBackground(new java.awt.Color(255, 255, 255));
        bd5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        bd5.setForeground(new java.awt.Color(0, 0, 0));
        bd5.setText("BD 5");
        bd5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bd5ActionPerformed(evt);
            }
        });
        jPanel4.add(bd5, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 140, 100, 50));

        bd10.setBackground(new java.awt.Color(255, 255, 255));
        bd10.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        bd10.setForeground(new java.awt.Color(0, 0, 0));
        bd10.setText("BD 10");
        bd10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bd10ActionPerformed(evt);
            }
        });
        jPanel4.add(bd10, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 140, 80, 50));

        btnCash.setBackground(new java.awt.Color(255, 255, 255));
        btnCash.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnCash.setForeground(new java.awt.Color(0, 0, 0));
        btnCash.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/cashIcon.png"))); // NOI18N
        btnCash.setText("CASH");
        btnCash.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCash.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCash.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCashActionPerformed(evt);
            }
        });
        jPanel4.add(btnCash, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 20, 70, 70));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("BD");
        jPanel4.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 190, 30, 40));

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 0, 0));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("BD");
        jPanel4.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 50, 30, 40));

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 0, 0));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("CHANGE:");
        jPanel4.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 150, 200, 30));

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 0, 0));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("TOTAL AMOUNT:");
        jPanel4.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 140, 30));

        txtAmountPaid.setBackground(new java.awt.Color(0, 102, 0));
        txtAmountPaid.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtAmountPaid.setForeground(new java.awt.Color(255, 255, 255));
        txtAmountPaid.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtAmountPaid.setOpaque(true);
        jPanel4.add(txtAmountPaid, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 50, 90, 40));

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 0, 0));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("BD");
        jPanel4.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 30, 40));

        lblTotalAmount.setBackground(new java.awt.Color(153, 153, 153));
        lblTotalAmount.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblTotalAmount.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotalAmount.setOpaque(true);
        jPanel4.add(lblTotalAmount, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 50, 90, 40));

        bd100.setBackground(new java.awt.Color(255, 255, 255));
        bd100.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        bd100.setForeground(new java.awt.Color(0, 0, 0));
        bd100.setText("0.100");
        bd100.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bd100ActionPerformed(evt);
            }
        });
        jPanel4.add(bd100, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 20, 80, 50));

        btnClear.setBackground(new java.awt.Color(255, 255, 255));
        btnClear.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnClear.setForeground(new java.awt.Color(0, 0, 0));
        btnClear.setText("CLEAR ");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });
        jPanel4.add(btnClear, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 200, 310, 30));

        bd50.setBackground(new java.awt.Color(255, 255, 255));
        bd50.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        bd50.setForeground(new java.awt.Color(0, 0, 0));
        bd50.setText("0.050");
        bd50.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bd50ActionPerformed(evt);
            }
        });
        jPanel4.add(bd50, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 20, 100, 50));

        btnCard.setBackground(new java.awt.Color(255, 255, 255));
        btnCard.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnCard.setForeground(new java.awt.Color(0, 0, 0));
        btnCard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/creditCardIcon.png"))); // NOI18N
        btnCard.setText("CARD");
        btnCard.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCard.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCardActionPerformed(evt);
            }
        });
        jPanel4.add(btnCard, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 100, 70, 70));

        btnFoc.setBackground(new java.awt.Color(255, 255, 255));
        btnFoc.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnFoc.setForeground(new java.awt.Color(0, 0, 0));
        btnFoc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/freeIcon.png"))); // NOI18N
        btnFoc.setText("FOC");
        btnFoc.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnFoc.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnFoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFocActionPerformed(evt);
            }
        });
        jPanel4.add(btnFoc, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 180, 70, 70));

        jPanel5.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 10, 830, 260));

        jPanel2.setBackground(new java.awt.Color(214, 214, 194));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnPrintLastReceipt.setBackground(new java.awt.Color(255, 255, 255));
        btnPrintLastReceipt.setFont(new java.awt.Font("Tahoma", 1, 17)); // NOI18N
        btnPrintLastReceipt.setForeground(new java.awt.Color(0, 0, 0));
        btnPrintLastReceipt.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/printericon.png"))); // NOI18N
        btnPrintLastReceipt.setText("PRINT LAST RECEIPT");
        btnPrintLastReceipt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintLastReceiptActionPerformed(evt);
            }
        });
        jPanel2.add(btnPrintLastReceipt, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 260, 70));

        jButton1.setBackground(new java.awt.Color(255, 255, 255));
        jButton1.setFont(new java.awt.Font("Tahoma", 1, 17)); // NOI18N
        jButton1.setForeground(new java.awt.Color(0, 0, 0));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/homeIcon.png"))); // NOI18N
        jButton1.setText("MAIN MENU");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 80, 230, 70));

        btnReportZ.setBackground(new java.awt.Color(255, 255, 255));
        btnReportZ.setFont(new java.awt.Font("Tahoma", 1, 17)); // NOI18N
        btnReportZ.setForeground(new java.awt.Color(0, 0, 0));
        btnReportZ.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/receiptIcon.png"))); // NOI18N
        btnReportZ.setText("RECEIPT DB");
        btnReportZ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReportZActionPerformed(evt);
            }
        });
        jPanel2.add(btnReportZ, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 10, 230, 70));

        jButton112.setBackground(new java.awt.Color(255, 255, 255));
        jButton112.setFont(new java.awt.Font("Tahoma", 1, 17)); // NOI18N
        jButton112.setForeground(new java.awt.Color(0, 0, 0));
        jButton112.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/salesIcon.png"))); // NOI18N
        jButton112.setText("SALES REPORT");
        jButton112.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton112ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton112, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 260, 70));

        jButton114.setBackground(new java.awt.Color(255, 255, 255));
        jButton114.setFont(new java.awt.Font("Tahoma", 1, 17)); // NOI18N
        jButton114.setForeground(new java.awt.Color(0, 0, 0));
        jButton114.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/stockIcon.png"))); // NOI18N
        jButton114.setText("STOCK COUNT");
        jButton114.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton114ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton114, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 150, 230, 70));

        btnNew.setBackground(new java.awt.Color(255, 255, 255));
        btnNew.setFont(new java.awt.Font("Tahoma", 1, 17)); // NOI18N
        btnNew.setForeground(new java.awt.Color(0, 0, 0));
        btnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/DefaultImages/NewIcon.png"))); // NOI18N
        btnNew.setText("NEW TRANSACTION");
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });
        jPanel2.add(btnNew, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 260, 70));

        lblDailyFoc.setText("foc");
        jPanel2.add(lblDailyFoc, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 370, -1, -1));

        lblDailyCash.setText("cash");
        jPanel2.add(lblDailyCash, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 370, -1, -1));

        lblDailyCard.setText("card");
        jPanel2.add(lblDailyCard, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 370, -1, -1));

        jPanel5.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 260, 530, 420));

        jPanel3.setBackground(new java.awt.Color(214, 214, 194));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Tahoma", 3, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("UNIT PRICE:");
        jPanel3.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, 130, 30));

        lblUnitPrice.setFont(new java.awt.Font("Tahoma", 3, 14)); // NOI18N
        lblUnitPrice.setForeground(new java.awt.Color(0, 0, 0));
        lblUnitPrice.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel3.add(lblUnitPrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 90, 300, 30));

        jLabel4.setFont(new java.awt.Font("Tahoma", 3, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("PRODUCT NAME:");
        jPanel3.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 130, 30));

        jLabel5.setFont(new java.awt.Font("Tahoma", 3, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("PRODUCT ID:");
        jPanel3.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 130, 30));

        lblProductId.setFont(new java.awt.Font("Tahoma", 3, 14)); // NOI18N
        lblProductId.setForeground(new java.awt.Color(0, 0, 0));
        lblProductId.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel3.add(lblProductId, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 10, 300, 30));

        lblProductName.setFont(new java.awt.Font("Tahoma", 3, 14)); // NOI18N
        lblProductName.setForeground(new java.awt.Color(0, 0, 0));
        lblProductName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel3.add(lblProductName, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 50, 300, 30));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("BARCODE:");
        jPanel3.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, 120, 20));

        txtSearch.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtSearch.setNextFocusableComponent(txtQuantity);
        txtSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchActionPerformed(evt);
            }
        });
        jPanel3.add(txtSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 130, 280, -1));

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 0, 0));
        jLabel11.setText("QUANTITY:");
        jPanel3.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 160, 110, 30));

        txtQuantity.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jPanel3.add(txtQuantity, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 160, 50, 30));

        btnEnter.setBackground(new java.awt.Color(255, 255, 255));
        btnEnter.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        btnEnter.setForeground(new java.awt.Color(0, 0, 0));
        btnEnter.setText("ENTER");
        btnEnter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnterActionPerformed(evt);
            }
        });
        jPanel3.add(btnEnter, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 200, 420, 30));

        jPanel5.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 460, 240));

        jPanel1.setBackground(new java.awt.Color(214, 214, 194));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tableInvoice.setBackground(new java.awt.Color(214, 214, 194));
        tableInvoice.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(tableInvoice);

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 560, 160));

        btnRemoveItem.setBackground(new java.awt.Color(255, 255, 255));
        btnRemoveItem.setForeground(new java.awt.Color(0, 0, 0));
        btnRemoveItem.setText("REMOVE FROM CART");
        btnRemoveItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveItemActionPerformed(evt);
            }
        });
        jPanel1.add(btnRemoveItem, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 170, 30));

        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnFav4.setBackground(new java.awt.Color(255, 255, 255));
        btnFav4.setForeground(new java.awt.Color(0, 0, 0));
        btnFav4.setText("NOTHING SET ON 4");
        btnFav4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFav4ActionPerformed(evt);
            }
        });
        jPanel8.add(btnFav4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 140, 30));

        btnFav1.setBackground(new java.awt.Color(255, 255, 255));
        btnFav1.setForeground(new java.awt.Color(0, 0, 0));
        btnFav1.setText("NOTHING SET ON 1");
        btnFav1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFav1ActionPerformed(evt);
            }
        });
        jPanel8.add(btnFav1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 140, 30));

        btnFav2.setBackground(new java.awt.Color(255, 255, 255));
        btnFav2.setForeground(new java.awt.Color(0, 0, 0));
        btnFav2.setText("NOTHING SET ON 2");
        btnFav2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFav2ActionPerformed(evt);
            }
        });
        jPanel8.add(btnFav2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 140, 30));

        btnFav3.setBackground(new java.awt.Color(255, 255, 255));
        btnFav3.setForeground(new java.awt.Color(0, 0, 0));
        btnFav3.setText("NOTHING SET ON 3");
        btnFav3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFav3ActionPerformed(evt);
            }
        });
        jPanel8.add(btnFav3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 140, 30));

        btnFav8.setBackground(new java.awt.Color(255, 255, 255));
        btnFav8.setForeground(new java.awt.Color(0, 0, 0));
        btnFav8.setText("NOTHING SET ON 8");
        btnFav8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFav8ActionPerformed(evt);
            }
        });
        jPanel8.add(btnFav8, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 130, 140, 30));

        btnFav7.setBackground(new java.awt.Color(255, 255, 255));
        btnFav7.setForeground(new java.awt.Color(0, 0, 0));
        btnFav7.setText("NOTHING SET ON 7");
        btnFav7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFav7ActionPerformed(evt);
            }
        });
        jPanel8.add(btnFav7, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 90, 140, 30));

        btnFav6.setBackground(new java.awt.Color(255, 255, 255));
        btnFav6.setForeground(new java.awt.Color(0, 0, 0));
        btnFav6.setText("NOTHING SET ON 6");
        btnFav6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFav6ActionPerformed(evt);
            }
        });
        jPanel8.add(btnFav6, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 50, 140, 30));

        btnFav5.setBackground(new java.awt.Color(255, 255, 255));
        btnFav5.setForeground(new java.awt.Color(0, 0, 0));
        btnFav5.setText("NOTHING SET ON 5");
        btnFav5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFav5ActionPerformed(evt);
            }
        });
        jPanel8.add(btnFav5, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 10, 140, 30));

        btnFav9.setBackground(new java.awt.Color(255, 255, 255));
        btnFav9.setForeground(new java.awt.Color(0, 0, 0));
        btnFav9.setText("NOTHING SET ON 9");
        btnFav9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFav9ActionPerformed(evt);
            }
        });
        jPanel8.add(btnFav9, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 10, 140, 30));

        btnFav10.setBackground(new java.awt.Color(255, 255, 255));
        btnFav10.setForeground(new java.awt.Color(0, 0, 0));
        btnFav10.setText("NOTHING SET ON 10");
        btnFav10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFav10ActionPerformed(evt);
            }
        });
        jPanel8.add(btnFav10, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 50, 140, 30));

        btnFav11.setBackground(new java.awt.Color(255, 255, 255));
        btnFav11.setForeground(new java.awt.Color(0, 0, 0));
        btnFav11.setText("NOTHING SET ON 11");
        btnFav11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFav11ActionPerformed(evt);
            }
        });
        jPanel8.add(btnFav11, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 90, 140, 30));

        btnFav12.setBackground(new java.awt.Color(255, 255, 255));
        btnFav12.setForeground(new java.awt.Color(0, 0, 0));
        btnFav12.setText("NOTHING SET ON 12");
        btnFav12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFav12ActionPerformed(evt);
            }
        });
        jPanel8.add(btnFav12, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 130, 140, 30));

        jPanel1.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 230, 560, 170));

        jPanel5.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 260, 580, 420));

        lblTime.setBackground(new java.awt.Color(214, 214, 194));
        lblTime.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblTime.setForeground(new java.awt.Color(0, 0, 0));
        lblTime.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTime.setOpaque(true);
        jPanel5.add(lblTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(1140, 640, 160, 40));

        lblDate.setBackground(new java.awt.Color(214, 214, 194));
        lblDate.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblDate.setForeground(new java.awt.Color(0, 0, 0));
        lblDate.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDate.setOpaque(true);
        jPanel5.add(lblDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(1140, 590, 160, 40));

        getContentPane().add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 1320, 690));

        jMenu1.setText("File");

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        jMenuItem2.setText("New Transaction");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        jMenuItem3.setText("New Item Entry");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_1, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem4.setText("Pay Cash");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_2, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem5.setText("Pay Card");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem5);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("Remove Item");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem1);

        jMenuItem6.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem6.setText("Print Last Receipt");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem6);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnEnterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnterActionPerformed
    if (txtQuantity.getText().equals("")){
            JOptionPane.showMessageDialog(this, "ENTER QUANTITY","QUANTITY", JOptionPane.ERROR_MESSAGE);
        }else{
            String prodId = lblProductId.getText();
            String prodName = lblProductName.getText();
            String unPrice = lblUnitPrice.getText();
            String getQty = txtQuantity.getText();
            Double uPrice = Double.valueOf(unPrice);
            DefaultTableModel model = (DefaultTableModel)tableInvoice.getModel();
            if(model.getRowCount() == 0){
            qty = Integer.valueOf(getQty);                
                Object[] adRow = {prodId, prodName,unPrice,qty, String.valueOf(df.format(qty*uPrice))};
                model.addRow(adRow);    
            }else{
            int duplicateRow = -1;
            for(int j=0; j<model.getRowCount();j++){
                Object obj = model.getValueAt(j, 0);
                if(obj.equals(prodId)){
                    duplicateRow = j;
                    break;
                }
            }
             if(duplicateRow == -1){
                qty = Integer.valueOf(txtQuantity.getText());
                Object[] adRow = {prodId, prodName,unPrice,qty, String.valueOf(df.format(qty*uPrice))};
                model.addRow(adRow);
             }else{
                qty = Integer.valueOf(model.getValueAt(duplicateRow, 3).toString()) + 1;
                model.setValueAt(prodId, duplicateRow, 0);
                model.setValueAt(prodName, duplicateRow, 1);
                model.setValueAt(unPrice, duplicateRow, 2);
                model.setValueAt(qty, duplicateRow, 3);
                model.setValueAt(df.format(qty*uPrice), duplicateRow, 4);
             }
            }
            
            float totalPrice=0;
                for(int i=0; i<tableInvoice.getRowCount(); i++){
                    totalPrice += Float.valueOf(tableInvoice.getValueAt(i, 4).toString());
                    lblTotalAmount.setText(String.valueOf(df.format(totalPrice)));
        }
            txtSearch.requestFocus();
            txtSearch.setText("");
            txtQuantity.setText("");
}       
          
    }//GEN-LAST:event_btnEnterActionPerformed

    private void bd20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bd20ActionPerformed
    showAmountPaid +=20.000;
    txtAmountPaid.setText(String.valueOf(df.format(showAmountPaid)));       
    }//GEN-LAST:event_bd20ActionPerformed

    private void bd500ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bd500ActionPerformed
    showAmountPaid +=.500;
    txtAmountPaid.setText(String.valueOf(df.format(showAmountPaid)));
    }//GEN-LAST:event_bd500ActionPerformed

    private void bd1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bd1ActionPerformed
    showAmountPaid +=1.000;
    txtAmountPaid.setText(String.valueOf(df.format(showAmountPaid)));
    }//GEN-LAST:event_bd1ActionPerformed

    private void bd2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bd2ActionPerformed
    showAmountPaid +=2.000;
    txtAmountPaid.setText(String.valueOf(df.format(showAmountPaid)));
    }//GEN-LAST:event_bd2ActionPerformed

    private void bd3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bd3ActionPerformed
    showAmountPaid +=3.000;
    txtAmountPaid.setText(String.valueOf(df.format(showAmountPaid)));   
    }//GEN-LAST:event_bd3ActionPerformed

    private void bd5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bd5ActionPerformed
    showAmountPaid +=5.000;
    txtAmountPaid.setText(String.valueOf(df.format(showAmountPaid)));
    }//GEN-LAST:event_bd5ActionPerformed

    private void bd10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bd10ActionPerformed
    showAmountPaid +=10.00;
    txtAmountPaid.setText(String.valueOf(df.format(showAmountPaid)));
    }//GEN-LAST:event_bd10ActionPerformed
private void clearTexts(){
    lblChange.setText("");
    lblProductId.setText("");
    lblProductName.setText("");
    lblTotalAmount.setText("");
    lblUnitPrice.setText("");
    txtAmountPaid.setText("");
    txtQuantity.setText("");
    txtSearch.setText("");
    
}private void saveReceipt(){
   if (getPaymentMethod.equals("CASH")){
    try{
        String saveReceiptSQL = "INSERT INTO tblreceipt"
                + "(transactionId,productName,quantity,unitPrice,bdPrice,totalAmount,amountPaid,amountChange,cashier,date,time,paymentMethod)"
                + "values(?,?,?,?,?,?,?,?,?,?,?,?)";
        for(int i=0; i<tableInvoice.getRowCount();i++){
        dbConn.pstmt =dbConn.conn.prepareStatement(saveReceiptSQL);
        dbConn.pstmt.setInt(1,getTransactionID);
        dbConn.pstmt.setString(2,tableInvoice.getValueAt(i, 1).toString());
        dbConn.pstmt.setInt(3,Integer.valueOf(tableInvoice.getValueAt(i, 3).toString()));
        dbConn.pstmt.setDouble(4,Double.valueOf(tableInvoice.getValueAt(i, 2).toString()));
        dbConn.pstmt.setDouble(5,Double.valueOf(tableInvoice.getValueAt(i, 4).toString()));
        dbConn.pstmt.setDouble(6, Double.valueOf(lblTotalAmount.getText()));
        dbConn.pstmt.setDouble(7, Double.valueOf(txtAmountPaid.getText()));
        dbConn.pstmt.setDouble(8,Double.valueOf(lblChange.getText()));
        dbConn.pstmt.setString(9,frmLogin.showUserName);
        dbConn.pstmt.setString(10,lblDate.getText());
        dbConn.pstmt.setString(11,lblTime.getText());
        dbConn.pstmt.setString(12,getPaymentMethod);
        dbConn.pstmt.execute();
        dbConn.pstmt.close();
        }
        saveAuditTrail("CASH TRANSACTION: " + String.valueOf(getTransactionID));
    }catch(SQLException e){
        JOptionPane.showMessageDialog(null, e.getMessage());
    }
   }else if(getPaymentMethod.equals("CARD")){
    try{
        String saveReceiptSQL = "INSERT INTO tblreceipt"
                + "(transactionId,productName,quantity,unitPrice,bdPrice,totalAmount,"
                + "amountPaid,amountChange,cashier,date,time,paymentMethod,cardNumber,"
                + "cardReference)"
                + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        for(int i=0; i<tableInvoice.getRowCount();i++){
        dbConn.pstmt =dbConn.conn.prepareStatement(saveReceiptSQL);
        dbConn.pstmt.setInt(1,getTransactionID);
        dbConn.pstmt.setString(2,tableInvoice.getValueAt(i, 1).toString());
        dbConn.pstmt.setInt(3,Integer.valueOf(tableInvoice.getValueAt(i, 3).toString()));
        dbConn.pstmt.setDouble(4,Double.valueOf(tableInvoice.getValueAt(i, 2).toString()));
        dbConn.pstmt.setDouble(5,Double.valueOf(tableInvoice.getValueAt(i, 4).toString()));
        dbConn.pstmt.setDouble(6, Double.valueOf(lblTotalAmount.getText()));
        dbConn.pstmt.setDouble(7, Double.valueOf(lblTotalAmount.getText()));
        dbConn.pstmt.setDouble(8,0.0);
        dbConn.pstmt.setString(9,frmLogin.showUserName);
        dbConn.pstmt.setString(10,lblDate.getText());
        dbConn.pstmt.setString(11,lblTime.getText());
        dbConn.pstmt.setString(12,getPaymentMethod);
        dbConn.pstmt.setString(13, txtCardNumber.getText());
        dbConn.pstmt.setString(14, txtCardReference.getText());
        dbConn.pstmt.execute();
        dbConn.pstmt.close();
        }
        saveAuditTrail("CARD TRANSACTION: " + String.valueOf(getTransactionID));
    }catch(SQLException e){
        JOptionPane.showMessageDialog(this, e.getMessage());
    }
   }else if(getPaymentMethod.equals("FOC")){
    try{
        String saveReceiptSQL = "INSERT INTO tblreceipt"
                + "(transactionId,productName,quantity,unitPrice,bdPrice,totalAmount,"
                + "amountPaid,amountChange,cashier,date,time,paymentMethod,focComment)"
                + "values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
        for(int i=0; i<tableInvoice.getRowCount();i++){
        dbConn.pstmt =dbConn.conn.prepareStatement(saveReceiptSQL);
        dbConn.pstmt.setInt(1,getTransactionID);
        dbConn.pstmt.setString(2,tableInvoice.getValueAt(i, 1).toString());
        dbConn.pstmt.setInt(3,Integer.valueOf(tableInvoice.getValueAt(i, 3).toString()));
        dbConn.pstmt.setDouble(4,Double.valueOf(tableInvoice.getValueAt(i, 2).toString()));
        dbConn.pstmt.setDouble(5,Double.valueOf(tableInvoice.getValueAt(i, 4).toString()));
        dbConn.pstmt.setDouble(6, Double.valueOf(lblTotalAmount.getText()));
        dbConn.pstmt.setDouble(7, 0.000);
        dbConn.pstmt.setDouble(8,0.0000);
        dbConn.pstmt.setString(9,frmLogin.showUserName);
        dbConn.pstmt.setString(10,lblDate.getText());
        dbConn.pstmt.setString(11,lblTime.getText());
        dbConn.pstmt.setString(12,getPaymentMethod);
        dbConn.pstmt.setString(13, txtComment.getText());
        dbConn.pstmt.execute();
        dbConn.pstmt.close();
        }
        saveAuditTrail("FOC TRANSACTION: " + String.valueOf(getTransactionID));
    }catch(SQLException e){
        JOptionPane.showMessageDialog(this, e.getMessage());
    }
   }
    //UPDATE STOCK QTY
    try{
        String deductStockOnHand = "UPDATE tblproduct set stockOnhand=stockOnhand-? where productId=?";
        dbConn.pstmt = dbConn.conn.prepareStatement(deductStockOnHand);
        DefaultTableModel model = (DefaultTableModel)tableInvoice.getModel();
        for(int j=0; j<model.getRowCount();j++){
            dbConn.pstmt.setInt(1,Integer.valueOf(model.getValueAt(j, 3).toString()));
            dbConn.pstmt.setString(2,model.getValueAt(j, 0).toString());
            dbConn.pstmt.executeUpdate();
        }
    }catch(SQLException e){
        JOptionPane.showMessageDialog(this, e.getMessage());
    }
    
}
private void checkTransactionId(){
    try{
        String checkReceiptId = "SELECT * from tblreceipt order by transactionId DESC LIMIT 1";
        dbConn.pstmt =dbConn.conn.prepareStatement(checkReceiptId);
        dbConn.rs = dbConn.pstmt.executeQuery();
        if (dbConn.rs.next()){
            getTransactionID = dbConn.rs.getInt(1);
            getTransactionID++;
        }else{
            getTransactionID=1;
        }
    }catch(SQLException e){
        e.getMessage();
    }
}
    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        DefaultTableModel model =(DefaultTableModel)tableInvoice.getModel();
        while(model.getRowCount()>0){
            model.setRowCount(0);
        }
        clearTexts();
        showAmountPaid=0;
        txtSearch.requestFocus();
    }//GEN-LAST:event_btnNewActionPerformed

    private void btnRemoveItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveItemActionPerformed
        DefaultTableModel model =(DefaultTableModel)tableInvoice.getModel();
        int numRows = tableInvoice.getSelectedRows().length;
        for (int i=0; i<numRows;i++){
            model.removeRow(tableInvoice.getSelectedRow());
        }
        float totalPrice=0;
            for(int i=0; i<tableInvoice.getRowCount(); i++){
                totalPrice += Float.valueOf(tableInvoice.getValueAt(i, 4).toString());
                lblTotalAmount.setText(String.valueOf(df.format(totalPrice)));
        }
    }//GEN-LAST:event_btnRemoveItemActionPerformed
    private void printReceipt(){
    Map param = new HashMap();
        param.put("tranId", getTransactionID);
        try{
            dbConn.conn.close();
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbpos","root","root");
            JasperDesign jd = JRXmlLoader.load(new File("src\\Reports\\repReceipt.jrxml"));
            JasperReport jr = JasperCompileManager.compileReport(jd);
            JasperPrint jp = JasperFillManager.fillReport(jr, param,conn);
            JasperPrintManager.printReport(jp, false);

        }catch(ClassNotFoundException | SQLException | JRException e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
    private void btnReportZActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportZActionPerformed
        Map param = new HashMap();
        param.put("date", lblDate.getText());
        try{
            dbConn.conn.close();
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbpos","root","root");
            JasperDesign jd = JRXmlLoader.load(new File("src\\Reports\\repAllReceipt.jrxml"));
            JasperReport jr = JasperCompileManager.compileReport(jd);
            JasperPrint jp = JasperFillManager.fillReport(jr, param,conn);
            JasperViewer.viewReport(jp,false);
            dbConn.doConnect();
        }catch(ClassNotFoundException | SQLException | JRException e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_btnReportZActionPerformed

    private void bd100ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bd100ActionPerformed
        showAmountPaid +=.100;
    txtAmountPaid.setText(String.valueOf(df.format(showAmountPaid)));
    }//GEN-LAST:event_bd100ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        frmMain obj = new frmMain();
        obj.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        txtAmountPaid.setText("");
        showAmountPaid=0;
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnPrintLastReceiptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintLastReceiptActionPerformed
        Map param = new HashMap();
        param.put("tranId", getTransactionID);
        try{
            dbConn.conn.close();
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbpos","root","root");
            JasperDesign jd = JRXmlLoader.load(new File("src\\Reports\\repReceipt.jrxml"));
            JasperReport jr = JasperCompileManager.compileReport(jd);
            JasperPrint jp = JasperFillManager.fillReport(jr, param,conn);
            JasperViewer.viewReport(jp,false);
            dbConn.doConnect();
        }catch(ClassNotFoundException | SQLException | JRException e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }        
    }//GEN-LAST:event_btnPrintLastReceiptActionPerformed

    private void bd50ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bd50ActionPerformed
        showAmountPaid +=.050;
    txtAmountPaid.setText(String.valueOf(df.format(showAmountPaid)));
    }//GEN-LAST:event_bd50ActionPerformed
private void displayDailySales(){
        try{
            dbConn.pstmt = dbConn.conn.prepareStatement("Select sum(bdPrice) from tblreceipt where date=? and paymentMethod=?");
            dbConn.pstmt.setString(1, lblDate.getText());
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
            dbConn.pstmt.setString(1, lblDate.getText());
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
            dbConn.pstmt.setString(1, lblDate.getText());
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
    private void jButton112ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton112ActionPerformed
        displayDailySales();
        Map param = new HashMap();
        param.put("date", lblDate.getText());
        param.put("cardSales", lblDailyCard.getText());
        param.put("cashSales", lblDailyCash.getText());
        param.put("focData", lblDailyFoc.getText());
        saveAuditTrail("VIEWED SALES REPORT FOR: " + lblDate.getText());
        try{
            dbConn.conn.close();
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbpos","root","root");
            JasperDesign jd = JRXmlLoader.load(new File("src\\Reports\\repSalesSummary.jrxml"));
            JasperReport jr = JasperCompileManager.compileReport(jd);
            JasperPrint jp = JasperFillManager.fillReport(jr, param,conn);
            JasperViewer.viewReport(jp,false);
            dbConn.doConnect();
        }catch(ClassNotFoundException | SQLException | JRException e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_jButton112ActionPerformed

    private void jButton114ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton114ActionPerformed
        Map param = new HashMap();
        param.put("date", lblDate.getText());
        try{
            dbConn.conn.close();
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbpos","root","root");
            JasperDesign jd = JRXmlLoader.load(new File("src\\Reports\\repCurrentStock.jrxml"));
            JasperReport jr = JasperCompileManager.compileReport(jd);
            JasperPrint jp = JasperFillManager.fillReport(jr, param,conn);
            JasperViewer.viewReport(jp,false);
            dbConn.doConnect();
        }catch(ClassNotFoundException | SQLException | JRException e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_jButton114ActionPerformed

    private void btnCashActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCashActionPerformed
        double getAmtPaid, getTotalAmt, showChange;
        int itemCash = JOptionPane.showConfirmDialog(this, "PROCEED WITH CASH PAYMENT?","CASH PAYMENT",JOptionPane.YES_NO_OPTION);
        if (itemCash == JOptionPane.YES_OPTION){
        getPaymentMethod = "CASH";
        if( txtAmountPaid.getText().equals("") || txtAmountPaid.getText().equals("0")){
            JOptionPane.showMessageDialog(null, "ENTER AMOUNT PAID", "NO AMOUNT ENTERED", JOptionPane.ERROR_MESSAGE);
        }else{
            getAmtPaid = Double.valueOf(txtAmountPaid.getText());
            getTotalAmt = Double.valueOf(lblTotalAmount.getText());
            if (getAmtPaid< getTotalAmt){
                JOptionPane.showMessageDialog(null, "INSUFFICIENT PAYMENT", "INSUFFICIENT", JOptionPane.ERROR_MESSAGE);
            }else{
                showChange = getAmtPaid - getTotalAmt;
                lblChange.setText(String.valueOf(df.format(showChange)));
                checkTransactionId();
                saveReceipt();
                int itemSelected = JOptionPane.showConfirmDialog(this, "NEW TRANSACTION?","NEW TRANSACTION",JOptionPane.YES_NO_OPTION);
                if (itemSelected == JOptionPane.YES_OPTION){
                    btnNewActionPerformed(evt);
                }
            }
        }
        }
    }//GEN-LAST:event_btnCashActionPerformed

    private void btnCardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCardActionPerformed
        intFrameCard.setVisible(true);
    }//GEN-LAST:event_btnCardActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        btnNewActionPerformed(evt);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        txtSearch.requestFocus();
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        btnCashActionPerformed(evt);
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        btnCardActionPerformed(evt);
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        btnRemoveItemActionPerformed(evt);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        btnPrintLastReceiptActionPerformed(evt);
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void btnProcessCardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProcessCardActionPerformed
        double getAmtPaid, getTotalAmt;
        getPaymentMethod = "CARD";     
        getAmtPaid = Double.valueOf(lblTotalAmount.getText());
        getTotalAmt = Double.valueOf(lblTotalAmount.getText());
        checkTransactionId();
        saveReceipt(); 
        intFrameCard.setVisible(false);
        txtCardNumber.setText("");
        txtCardReference.setText("");
        JOptionPane.showMessageDialog(this, "PAYMENT PROCESSED SUCCESSFULLY");
        int itemSelected = JOptionPane.showConfirmDialog(this, "NEW TRANSACTION?","NEW TRANSACTION",JOptionPane.YES_NO_OPTION);
        if (itemSelected == JOptionPane.YES_OPTION){
            btnNewActionPerformed(evt);
        }
        
    }//GEN-LAST:event_btnProcessCardActionPerformed

    private void btnCancelCardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelCardActionPerformed
        intFrameCard.setVisible(false);
        txtCardNumber.setText("");
        txtCardReference.setText("");
    }//GEN-LAST:event_btnCancelCardActionPerformed

    private void btnProcessCard1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProcessCard1ActionPerformed
        double getAmtPaid, getTotalAmt;
        getPaymentMethod = "FOC";     
        getAmtPaid = 0.000;
        getTotalAmt = Double.valueOf(lblTotalAmount.getText());
        checkTransactionId();
        saveReceipt(); 
        intFrameCard.setVisible(false);
        txtCardNumber.setText("");
        txtCardReference.setText("");
        JOptionPane.showMessageDialog(this, "FOC PROCESSED SUCCESSFULLY");
        intFrameFoc.setVisible(false);
        int itemSelected = JOptionPane.showConfirmDialog(this, "NEW TRANSACTION?","NEW TRANSACTION",JOptionPane.YES_NO_OPTION);
        if (itemSelected == JOptionPane.YES_OPTION){
            btnNewActionPerformed(evt);
        }
    }//GEN-LAST:event_btnProcessCard1ActionPerformed

    private void btnCancelCard1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelCard1ActionPerformed
        intFrameFoc.setVisible(false);
        txtComment.setText("");
    }//GEN-LAST:event_btnCancelCard1ActionPerformed

    private void btnFocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFocActionPerformed
        intFrameFoc.setVisible(true);
        txtComment.requestFocus();
    }//GEN-LAST:event_btnFocActionPerformed

    private void btnFav1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFav1ActionPerformed
        txtSearch.setText(btnFav1.getText());
    }//GEN-LAST:event_btnFav1ActionPerformed

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed

    }//GEN-LAST:event_txtSearchActionPerformed

    private void btnFav2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFav2ActionPerformed
        txtSearch.setText(btnFav2.getText());
    }//GEN-LAST:event_btnFav2ActionPerformed

    private void btnFav3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFav3ActionPerformed
        txtSearch.setText(btnFav3.getText());
    }//GEN-LAST:event_btnFav3ActionPerformed

    private void btnFav4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFav4ActionPerformed
        txtSearch.setText(btnFav4.getText());
    }//GEN-LAST:event_btnFav4ActionPerformed

    private void btnFav5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFav5ActionPerformed
        txtSearch.setText(btnFav5.getText());
    }//GEN-LAST:event_btnFav5ActionPerformed

    private void btnFav6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFav6ActionPerformed
        txtSearch.setText(btnFav6.getText());
    }//GEN-LAST:event_btnFav6ActionPerformed

    private void btnFav7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFav7ActionPerformed
        txtSearch.setText(btnFav7.getText());
    }//GEN-LAST:event_btnFav7ActionPerformed

    private void btnFav8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFav8ActionPerformed
        txtSearch.setText(btnFav8.getText());
    }//GEN-LAST:event_btnFav8ActionPerformed

    private void btnFav9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFav9ActionPerformed
        txtSearch.setText(btnFav9.getText());
    }//GEN-LAST:event_btnFav9ActionPerformed

    private void btnFav10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFav10ActionPerformed
        txtSearch.setText(btnFav10.getText());
    }//GEN-LAST:event_btnFav10ActionPerformed

    private void btnFav11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFav11ActionPerformed
        txtSearch.setText(btnFav11.getText());
    }//GEN-LAST:event_btnFav11ActionPerformed

    private void btnFav12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFav12ActionPerformed
        txtSearch.setText(btnFav12.getText());
    }//GEN-LAST:event_btnFav12ActionPerformed

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
            java.util.logging.Logger.getLogger(frmCashiering.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frmCashiering.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frmCashiering.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frmCashiering.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frmCashiering().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bd1;
    private javax.swing.JButton bd10;
    private javax.swing.JButton bd100;
    private javax.swing.JButton bd2;
    private javax.swing.JButton bd20;
    private javax.swing.JButton bd3;
    private javax.swing.JButton bd5;
    private javax.swing.JButton bd50;
    private javax.swing.JButton bd500;
    private javax.swing.JButton btnCancelCard;
    private javax.swing.JButton btnCancelCard1;
    private javax.swing.JButton btnCard;
    private javax.swing.JButton btnCash;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnEnter;
    private javax.swing.JButton btnFav1;
    private javax.swing.JButton btnFav10;
    private javax.swing.JButton btnFav11;
    private javax.swing.JButton btnFav12;
    private javax.swing.JButton btnFav2;
    private javax.swing.JButton btnFav3;
    private javax.swing.JButton btnFav4;
    private javax.swing.JButton btnFav5;
    private javax.swing.JButton btnFav6;
    private javax.swing.JButton btnFav7;
    private javax.swing.JButton btnFav8;
    private javax.swing.JButton btnFav9;
    private javax.swing.JButton btnFoc;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnPrintLastReceipt;
    private javax.swing.JButton btnProcessCard;
    private javax.swing.JButton btnProcessCard1;
    private javax.swing.JButton btnRemoveItem;
    private javax.swing.JButton btnReportZ;
    private javax.swing.JInternalFrame intFrameCard;
    private javax.swing.JInternalFrame intFrameFoc;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton112;
    private javax.swing.JButton jButton114;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblChange;
    private javax.swing.JLabel lblDailyCard;
    private javax.swing.JLabel lblDailyCash;
    private javax.swing.JLabel lblDailyFoc;
    private javax.swing.JLabel lblDate;
    private javax.swing.JLabel lblProductId;
    private javax.swing.JLabel lblProductName;
    private javax.swing.JLabel lblTime;
    private javax.swing.JLabel lblTotalAmount;
    private javax.swing.JLabel lblUnitPrice;
    private javax.swing.JTable tableInvoice;
    private javax.swing.JLabel txtAmountPaid;
    private javax.swing.JTextField txtCardNumber;
    private javax.swing.JTextField txtCardReference;
    private javax.swing.JTextField txtComment;
    private javax.swing.JTextField txtQuantity;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
