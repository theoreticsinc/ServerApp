package serverapp;

/**
 *
 * @author Angelo
 */
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.LineNumberInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.text.SimpleDateFormat;

public class RawFileHandler {
    
    public String ConvertFile2Date(String temp) {
        //EN010214.007 >> 20070214
        String Dateconv = "";
        if (temp.length() == 12) {
            Dateconv = "2" + temp.substring(9) + temp.substring(4, 6) + temp.substring(6, 8);
        }
        return Dateconv;
    }

    public String ConvertDate2File(String temp) {
        String Fileconv = "";
        //20081117 >> 1117.008
        Fileconv = temp.substring(4, 6) + temp.substring(6, 8) + "." + temp.substring(1, 4);
        return Fileconv;
    }

    public String ConvertFile2Month(String temp) {
        //CASH0112.007 >> 200712
        String Dateconv = "";
        if (temp.length() == 12) {
            Dateconv = "2" + temp.substring(9) + temp.substring(6, 8);
        }
        return Dateconv;
    }

    public String ConvertMonth2File(String temp) {
        //200712 >> 12.007
        String Fileconv = "";
        Fileconv = temp.substring(4, 6) + "." + temp.substring(1, 4);
        return Fileconv;
    }

    public String reformatDate(String oldDate) {
        String newDate = this.getRstring(oldDate, 4) + this.getLstring(oldDate, 4);
        return newDate;
    }

    public String reformatTime(String oldTime) {
        String newTime = oldTime + "00";
        return newTime;
    }

    public String FindFileLastMod(String path) throws IOException {
        File chkfile = new File(path);

        if (chkfile.exists() == true) {
            String Nday = null;
            RawFileHandler rh = new RawFileHandler();
            //System.out.println(chkfile.lastModified());
            Date theTime = new Date(chkfile.lastModified());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd H:mm:s");
            Nday = formatter.format(theTime);
            //System.out.println(Nday);
            return Nday;
        } else {
            Date fileNow = new Date(2000, 0, 1);
            DateFormat df2 = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
            //System.out.println(df2.format(fileNow));
            return df2.format(fileNow).toString();
        }
    }

    public long CheckFileLastMod(String path) throws IOException {
        File chkfile = new File(path);

        if (chkfile.exists() == true) {
            return chkfile.lastModified();
        } else {
            return 0;
        }
    }

    public boolean CreateNewFolder(String path) {
        File chkfile = new File(path);
        chkfile.mkdir();
        return false;
    }

    public boolean FindFileFolder(String path) {
        //File chkfile = new File();
        try {
            File chkfile = new File(path);
            if (chkfile.exists() == true) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean FindFileFolder(String path, String fname) {
        //File chkfile = new File();
        try {
            File chkfile = new File(path + fname);
            if (chkfile.exists() == true) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }
    }

    public String readFline(String path, String fname, int LineX) {
        FileInputStream inFile = null;
        String inputLine = "";
        try {
            inFile = new FileInputStream(path + fname);
            LineNumberInputStream inLines = new LineNumberInputStream(inFile);
            DataInputStream inStream = new DataInputStream(inLines);

            int z;
            for (z = 0; z < LineX - 1; z++) {
                inputLine = inStream.readLine();
            }
            inputLine = inStream.readLine();
            inStream.close();
            inFile.close();
            return inputLine;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        } finally {
            try {
                inFile.close();
            } catch (Exception ex) {
                //System.err.println(ex.getMessage());
            }
        }
        return inputLine;
    }

    public int getTotalFLines(String path, String fname) {
        int z = 0;
        try {
            FileInputStream inFile = new FileInputStream(path + fname);
            LineNumberInputStream inLines = new LineNumberInputStream(inFile);
            DataInputStream inStream = new DataInputStream(inLines);
            z = 0;
            while (inStream.readLine() != null) {
                z++;
            }
            inStream.close();
            inFile.close();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        return z;
    }

    public boolean putfile(String path, String fname, String output) {
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(path + fname);
            String s = output;
            for (int i = 0; i < s.length(); ++i) {
                outStream.write(s.charAt(i));
            }
            outStream.close();
            return true;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return false;
        }
    }

    public boolean appendfile(String path, String fname, String output) throws IOException {
        BufferedWriter bw = null;
        //System.setSecurityManager(new SecurityManager());
        try {
            bw = new BufferedWriter(new FileWriter(path + fname, true));
            bw.write(output);
            bw.newLine();
            bw.flush();
        } catch (Exception ioe) {
            System.err.println(ioe);
            return false;
        } finally {                       // always close the file
            if (bw != null) {
                try {
                    bw.close();
                } catch (Exception ioe2) {
                    // just ignore it
                    return false;
                }
            }
        }
        return true;
    }

    public boolean deleteFile(String path, String fname) {
        //File chkfile = new File();
        File chkfile = new File(path + fname);
        if (chkfile.exists() == true) {
            chkfile.delete();
            return true;
        } else {
            return false;
        }
    }

    public String getMstring(String inString, int Start, int Length) {
        char tempo[] = new char[Length]; //pointer = constant.9
        inString.getChars(Start, Start + Length, tempo, 0);
        int z;
        inString = "";
        for (z = 0; z < Length; z++) {
            inString = inString + tempo[z];
        }
        return inString;
    }

    public String getRstring(String inString, int R) {
        char tempo[] = new char[inString.length() - R]; //pointer = constant.9
        int tempolength = inString.length();
        int actualvaluelength = inString.length() - R; //because of index 0
        inString.getChars(R, tempolength, tempo, 0);
        int z;
        inString = "";
        for (z = 0; z < actualvaluelength; z++) {
            inString = inString + tempo[z];
        }
        return inString;
    }

    public String getLstring(String inString, int L) {
        char tempo[] = new char[L];
        inString.getChars(0, L, tempo, 0);
        int z;
        inString = "";
        for (z = 0; z < L; z++) {
            inString = inString + tempo[z];
        }
        return inString;
    }

    public boolean copytoserver(String path, String fname) {
//            String bundleName2 = "jdataconverter.mapping.system";
//            ResourceBundle myResources2 = ResourceBundle.getBundle(bundleName2,Locale.getDefault());
//         
        String SERVER = "/SYSTEMS/";
        try {
            FileInputStream inStream = new FileInputStream(path + fname);
            int inBytes = inStream.available();
            byte inBuf[] = new byte[inBytes];
            int bytesRead = inStream.read(inBuf, 0, inBytes);

            inStream.close();
            String serverpath = SERVER;
            FileOutputStream outStream = new FileOutputStream(serverpath + fname);
            String s = new String(inBuf, 0);
            for (int i = 0; i < s.length(); ++i) {
                outStream.write(s.charAt(i));
            }
            outStream.close();
            return true;
        } catch (Exception x) {
            System.out.println(x.getMessage());
            return false;
        }

    }

    public boolean copy2server(String path, String fname) {

        String SERVER = "/SYSTEMS/";

        try {
            int loopread = this.getTotalFLines(path, fname);
            int i = 0;
            while (i != loopread) {
                i++;
                String templine = this.readFline(path, fname, i);
                if (i == 1) {
                    this.putfile(SERVER, fname, templine);
                } else {
                    this.appendfile(SERVER, fname, templine);
                }
            }

            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public void copytoworkpath(String path, String fname) throws IOException {

        String WORKPATH = "C://JTerminals/";

        try {
            FileInputStream inStream = new FileInputStream(path + fname);
            int inBytes = inStream.available();
            byte inBuf[] = new byte[inBytes];
            int bytesRead = inStream.read(inBuf, 0, inBytes);

            inStream.close();
            FileOutputStream outStream = new FileOutputStream(WORKPATH + fname);
            String s = new String(inBuf, 0);
            for (int i = 0; i < s.length(); ++i) {
                outStream.write(s.charAt(i));
            }
            outStream.close();
        } catch (Exception x) {
            System.out.println(x.getMessage());
        }
    }

    public boolean copy2workpath(String path, String fname) {

        String WORKPATH = "C://JTerminals/";

        try {
            int loopread = this.getTotalFLines(path, fname);
            int i = 0;
            while (i != loopread) {
                i++;
                String templine = this.readFline(path, fname, i);
                if (i == 1) {
                    this.putfile(WORKPATH, fname, templine);
                } else {
                    this.appendfile(WORKPATH, fname, templine);
                }
            }

            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public void copytodatapath(String path, String fname) throws IOException {
        String bundleName2 = "jdataconverter.mapping.system";
        ResourceBundle myResources2 = ResourceBundle.getBundle(bundleName2, Locale.getDefault());

        String DATAPATH = myResources2.getString("DATAPATH");

        try {
            FileInputStream inStream = new FileInputStream(path + fname);
            int inBytes = inStream.available();
            byte inBuf[] = new byte[inBytes];
            int bytesRead = inStream.read(inBuf, 0, inBytes);

            inStream.close();
            FileOutputStream outStream = new FileOutputStream(DATAPATH + fname);
            String s = new String(inBuf, 0);
            for (int i = 0; i < s.length(); ++i) {
                outStream.write(s.charAt(i));
            }
            outStream.close();
        } catch (Exception x) {
            System.out.print(x + " =");
            System.out.println(x.getMessage());
        }
    }

    public void copySource2Dest(String Source, String Destination) {
        FileInputStream inStream = null;
        try {
            inStream = new FileInputStream(Source);
            int inBytes = inStream.available();
            byte[] inBuf = new byte[inBytes];
            inStream.read(inBuf, 0, inBytes);
            inStream.close();
            FileOutputStream outStream = new FileOutputStream(Destination);
            String s = new String(inBuf, 0);
            for (int i = 0; i < s.length(); ++i) {
                outStream.write(s.charAt(i));
            }
            outStream.close();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        } finally {
            try {
                inStream.close();
            } catch (Exception ex) {
                //System.err.println(ex.getMessage());
            }
        }

    }

    public void CheckFileFolderFailSafe(String path, String file) {
        try {
            if (this.FindFileFolder(path + file) == false) {
                this.putfile(path, file, "");
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

}
