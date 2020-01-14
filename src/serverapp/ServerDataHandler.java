/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package serverapp;

import java.io.IOException;

/**
 *
 * @author Angelo
 */
public class ServerDataHandler implements Runnable {
    
    RawFileHandler rfh = new RawFileHandler();

    public void initFolders() {
        try {
            if (rfh.FindFileFolder("/Offline/") == false) {
                rfh.CreateNewFolder("/Offline");
            }
            if (rfh.FindFileFolder("/SUBSYSTEMS/") == false) {
                rfh.CreateNewFolder("/SUBSYSTEMS");
            }
            if (rfh.FindFileFolder("C://JTerminals/LOGIN/") == false) {
                rfh.CreateNewFolder("C://JTerminals/LOGIN");
            }
            if (rfh.FindFileFolder("/opt/Errors/") == false) {
                rfh.CreateNewFolder("/opt/Errors");
            }

        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

    private void delfirst() throws IOException {
        //initpath = System.getProperty("java.class.path")+ "/" ;
        String workpath = "C://JTerminals/";
        char[] alphabets = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        int x = 0;
        do {
            String temp = alphabets[x] + "MPP.REC";
            rfh.deleteFile(workpath, temp.toUpperCase());
            rfh.deleteFile(workpath, temp.toLowerCase());
            x++;
        } while (x < 36);
    }

    private void delLoginfirst() throws IOException {
        //initpath = System.getProperty("java.class.path")+ "/" ;
        String workpath = "C://JTerminals/LOGIN/";
        char[] alphabets = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        int x = 0;
        do {
            String temp = alphabets[x] + "LOGIN.DAT";
            rfh.deleteFile(workpath, temp.toUpperCase());
            rfh.deleteFile(workpath, temp.toLowerCase());
            x++;
        } while (x < 10);
    }

    private void readnwrite() throws IOException {
        String serverpath = "C://JTerminals/";
        String workpath = "C://JTerminals/";
        int loop = rfh.getTotalFLines(serverpath, "monthlyf.rec");
        int x = 1;
        String plate1 = "", plate2 = "";
        do {
            String temp = rfh.readFline(serverpath, "monthlyf.rec", x);
            String[] mppdata = new String[4];
            mppdata = temp.split(",");
            if (mppdata[1].equalsIgnoreCase("") == false) {
                plate1 = mppdata[1].substring(0, 1);
            }
            if (mppdata[2].equalsIgnoreCase("") == false) {
                plate2 = mppdata[2].substring(0, 1);
            }

            rfh.appendfile(workpath, plate1 + "MPP.REC", temp);
            if (plate1.compareToIgnoreCase(plate2) != 0) {
                rfh.appendfile(workpath, plate2 + "MPP.REC", temp);
            }

            x++;
        } while (x < loop + 1);

    }

    private void readnwriteLogin() throws IOException {
        String serverpath = "C://JTerminals/LOGIN/";
        String workpath = "C://JTerminals/LOGIN/";
        int loop = rfh.getTotalFLines(serverpath, "LOGIN.DAT");
        int x = 1;

        do {//Use the last digit instead of the first
            String temp = rfh.readFline(serverpath, "LOGIN.DAT", x);
            String logid = temp.substring(0, 8);
            String CharName = "";
            if (logid.equalsIgnoreCase("") == false) {
                CharName = logid.substring(7, 8);
            }
            rfh.appendfile(workpath, CharName + "LOGIN.DAT", temp);
            x++;
        } while (x < loop + 1);

    }

    public boolean checkNewMPP() {
        try {
            if (rfh.CheckFileLastMod("/SYSTEMS/monthlyf.rec") == rfh.CheckFileLastMod("C://JTerminals/monthlyf.rec")) {
                return false;
            } else {
                return true;
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return true;
        }
    }

    public boolean checkNewLOGIN() {
        try {
            if (rfh.CheckFileLastMod("/SYSTEMS/LOGIN.DAT") == rfh.CheckFileLastMod("C://JTerminals/LOGIN/LOGIN.DAT")) {
                return false;
            } else {
                return true;
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return true;
        }
    }

    public boolean UpdateLOGIN() {
        try {

            if (checkNewLOGIN() == true) {
                if (rfh.FindFileFolder("/SYSTEMS/", "LOGIN.DAT") == true) {
                    delLoginfirst();
                    rfh.copySource2Dest("/SYSTEMS/LOGIN.DAT", "C://JTerminals/LOGIN/LOGIN.DAT");
                    readnwriteLogin();
                }
            }

        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return true;
    }

    public boolean UpdateMPP() {
        try {

            if (checkNewMPP() == true) {
                if (rfh.FindFileFolder("/SYSTEMS/", "monthlyf.rec") == true) {
                    delfirst();
                    rfh.copytoworkpath("/SYSTEMS/", "monthlyf.rec");
                    readnwrite();
                }
            }

        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return true;
    }

    public String DeletePrevParker(String DelPrevParker) {
        String plate2del = "";
        try {
            if (rfh.FindFileFolder("/SYSTEMS/" + DelPrevParker + ".crd") == true) {
                String tempcard = rfh.readFline("/SYSTEMS/", DelPrevParker + ".crd", 1);
                plate2del = tempcard.substring(10, 16);
                rfh.deleteFile("/SYSTEMS/", DelPrevParker + ".crd");
                rfh.deleteFile("/SYSTEMS/", plate2del + ".plt");
            }

        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return plate2del;
    }

    @Override
    public void run() {
        UpdateMPP();
    }
}
