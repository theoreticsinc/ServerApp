/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverapp;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Theoretics Inc
 */
public class Main {

    public DataBaseHandler DBH;

    public Main() {
        DBH = new DataBaseHandler();
        OnlineUpdater oc = new OnlineUpdater();
        Thread ThrUpdaterClock = new Thread(oc);
        ThrUpdaterClock.start();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Main m = new Main();
    }

    private void processFailedCopy() {
        try {

        } catch (Exception ex) {

        }
    }

    private void processCRDPLTonServer() {
        try {
            SystemStatus ss = new SystemStatus();
            List<String> clients = DBH.getClientsIP();

            Iterator<String> iterator = clients.iterator();
            while (iterator.hasNext()) {
                String ip = iterator.next();
//                System.out.println(ip);
                String date_lk = DBH.getLastKnown("forduplication", ip);
//                System.out.println(date_lk);
                if (ss.checkPING(ip)) {
                    System.out.println(ip + " is Online");
                    if (null != date_lk) {
                        DBH.copyCardsServer2POS("forduplication", date_lk, ip, clients);
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    class OnlineUpdater implements Runnable {

        @Override
        public void run() {
            try {
                while (true) {
                    Thread.sleep(1000);
//                    SystemStatus ss = new SystemStatus();
//                    DataBaseHandler DBH = new DataBaseHandler();
//                    DBH.connect();
                    //online = ss.checkPING(BackupMainServer);
////                    if (ss.checkPING("") == true) {
                    //sdh.UpdateLOGIN();
                    //sdh.UpdateMPP();
                    processCRDPLTonServer();
                    //DBH.copyCRDPLTfromServer("crdplt.main", "crdplt.main");
//                        DBH.copyExitTransfromLocal("carpark.exit_trans", "carpark.exit_trans");
//                        DBH.copyColltrainfromLocal("colltrain.main", "colltrain.main");
//                        DBH.copyZReadfromLocal("zread.main", "zread.main");
//                    }
                    Thread.sleep(10000);
                }
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        }
    }

}
