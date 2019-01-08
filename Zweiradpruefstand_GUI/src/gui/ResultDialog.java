package gui;

import data.Config;
import data.Config.Velocity;
import data.Database;
import java.awt.Color;
import java.awt.Dimension;

/**
 *
 * @author emil
 */
public class ResultDialog extends javax.swing.JDialog {

    private final Config config = Config.getInstance();

    private double power;
    private double velocity;
    private double torque;

    /**
     * Creates new form ResultDialog
     */
    public ResultDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setTitle("Maximalwerte");
        setSize(new Dimension(700, 400));
        setLocationRelativeTo(null);
    }

    public void setValues() {
        
        power = Database.getInstance().getBikePower();
        velocity = Database.getInstance().getBikeVelo();
        torque = Database.getInstance().getBikeTorque();

        if (config.isPs() && config.getVelocity() == Velocity.MPS) {
            jLabelResult.setText(String.format("<html>%.2f PS<br/>%.1f m/s<br/>%.1f Nm</html>", power, velocity, torque));
        } else if (config.isPs() && config.getVelocity() == Velocity.KMH) {
            jLabelResult.setText(String.format("<html>%.2f PS<br/>%.1f Km/h<br/>%.1f Nm</html>", power, velocity, torque));
        } else if (config.isPs() && config.getVelocity() == Velocity.MIH) {
            jLabelResult.setText(String.format("<html>%.2f PS<br/>%.1f mi/h<br/>%.1f Nm</html>", power, velocity, torque));
        } else if (!config.isPs() && config.getVelocity() == Velocity.MPS) {
            jLabelResult.setText(String.format("<html>%.2f kW<br/>%.1f m/s<br/>%.1f Nm</html>", power, velocity, torque));
        } else if (!config.isPs() && config.getVelocity() == Velocity.KMH) {
            jLabelResult.setText(String.format("<html>%.2f kW<br/>%.1f Km/h<br/>%.1f Nm</html>", power, velocity, torque));
        } else if (!config.isPs() && config.getVelocity() == Velocity.MIH) {
            jLabelResult.setText(String.format("<html>%.2f kW<br/>%.1f mi/h<br/>%.1f Nm</html>", power, velocity, torque));
        }
    }

    public void setAppearance(boolean dark) {
        if (dark) {
            jPanBut.setBackground(Color.DARK_GRAY);
            jPanResult.setBackground(Color.DARK_GRAY);
            jLabelResult.setForeground(Color.WHITE);
        } else {
            jPanBut.setBackground(Color.WHITE);
            jPanResult.setBackground(Color.WHITE);
            jLabelResult.setForeground(Color.BLACK);
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

        jPanResult = new javax.swing.JPanel();
        jLabelResult = new javax.swing.JLabel();
        jPanBut = new javax.swing.JPanel();
        jbutOk = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanResult.setBackground(new java.awt.Color(255, 255, 255));
        jPanResult.setLayout(new java.awt.GridBagLayout());

        jLabelResult.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabelResult.setText("<html>1000 PS<br/>200 mi/h<br/>500 Nm</html>");
        jLabelResult.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 3), javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        jPanResult.add(jLabelResult, new java.awt.GridBagConstraints());

        getContentPane().add(jPanResult, java.awt.BorderLayout.CENTER);

        jPanBut.setBackground(new java.awt.Color(255, 255, 255));

        jbutOk.setText("OK");
        jbutOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbutOkActionPerformed(evt);
            }
        });
        jPanBut.add(jbutOk);

        getContentPane().add(jPanBut, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbutOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbutOkActionPerformed
        dispose();
    }//GEN-LAST:event_jbutOkActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the MAC OS X look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if (System.getProperty("os.name").contains("Mac OS X")) {
                    if ("MAC OS X".equals(info.getName())) {
                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                } else {
                    if ("Nimbus".equals(info.getName())) {
                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ResultDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ResultDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ResultDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ResultDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(() -> {
            ResultDialog dialog = new ResultDialog(new javax.swing.JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });
            dialog.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelResult;
    private javax.swing.JPanel jPanBut;
    private javax.swing.JPanel jPanResult;
    private javax.swing.JButton jbutOk;
    // End of variables declaration//GEN-END:variables
}
