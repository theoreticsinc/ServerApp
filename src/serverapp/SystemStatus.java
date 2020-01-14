/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package serverapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author Administrator Combine Exit with Entrance - must have ping and online
 * checking
 *
 */
public class SystemStatus {

    RawFileHandler rfh = new RawFileHandler();

    public boolean checkPING(String ip) {
        //System.out.println(inputLine);        
        boolean status;
        try {
            String pingCmd = "ping " + ip + "";

            Runtime r = Runtime.getRuntime();
            Process p = r.exec(pingCmd);

            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String inputLine;
            inputLine = in.readLine(); //LINUX ONLY - needs the second line as the result of the ping
            //System.out.println(inputLine);
            inputLine = in.readLine();
            inputLine = in.readLine();
            //System.out.println(inputLine);
            if (inputLine != null) {
                //System.out.println(inputLine);
                if (inputLine.compareTo("") == 0) {
                    //System.out.println(inputLine);
                    //System.out.println("Offline");
                    in.close();
                    status = false;
                    return status;
                } else if (inputLine != null && inputLine.contains("unreachable")) {
                    //System.out.println(inputLine);
                    //System.out.println("Offline");
                    in.close();
                    status = false;
                    return status;

                } else if (inputLine != null && inputLine.compareToIgnoreCase("Request timed out.") == 0) {
                    //System.out.println(inputLine);
                    //System.out.println("Offline");
                    in.close();
                    status = false;
                    return status;
                } else if (inputLine != null && inputLine.substring(0, 1).compareToIgnoreCase("-") == 0) {
                    //System.out.println(inputLine);
                    //System.out.println("Offline");
                    in.close();
                    status = false;
                    return status;
                }
                //System.out.println("Online");
                //System.out.println(inputLine);
                in.close();
                status = true;
                return status;
            }
            in.close();
            status = false;
            return status;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return false;

    }

    public boolean checkOnline() {
        //boolean found = rfh.FindFileFolder("/SYSTEMS/", "online.aaa");
        return true;
    }

    public void updateServerCRDPLT() {
        try {
            File pathName = new File("/Offline/");
            String[] fileNames = pathName.list();
            String ext = null;
            for (int i = 0; i < fileNames.length; i++) {
                File tf = new File(pathName.getPath(), fileNames[i]);
                ext = fileNames[i].substring(fileNames[i].lastIndexOf('.') + 1, fileNames[i].length());

                if (tf.isDirectory() == false) {
                    // System.out.println(tf.getCanonicalPath());
                    if (ext.compareToIgnoreCase("CRD") == 0) {
                        if (rfh.copy2server("/Offline/", fileNames[i]) == true) {
                            rfh.deleteFile("/Offline/", fileNames[i]);
                        }
                        System.out.println(fileNames[i] + "   :: EXT=" + ext);
                    } else if (ext.compareToIgnoreCase("PLT") == 0) {
                        if (rfh.copy2server("/Offline/", fileNames[i]) == true) {
                            rfh.deleteFile("/Offline/", fileNames[i]);
                        }
                        System.out.println(fileNames[i] + "   :: EXT=" + ext);
                    } else //if (ext.compareToIgnoreCase("008") == 0) 
                    {
                        if (rfh.copy2server("/Offline/", fileNames[i]) == true) {
                            rfh.deleteFile("/Offline/", fileNames[i]);
                        }
                        System.out.println(fileNames[i] + "   :: EXT=" + ext);
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }

    }

    public static void main(String args[]) throws Exception {
        SystemStatus ss = new SystemStatus();
        //System.out.println(ss.checkOnline());

        //ss.updateServerCRDPLT();
    }
}
