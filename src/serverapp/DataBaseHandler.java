/*
 * DB.java
 *
 * Created on December 4, 2007, 9:46 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */
package serverapp;

/**
 *
 * @author amd
 */
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
//import org.apache.commons.httpclient.util.HttpURLConnection;

import sun.misc.BASE64Encoder;

//import com.sun.jersey.core.util.Base64;
import sun.net.www.protocol.http.HttpURLConnection;

public class DataBaseHandler extends Thread {

    private String DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";
//    private String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
    private String MainServer_URL = "jdbc:mysql://localhost/";
    private String BackupMainServer_URL = "jdbc:mysql://localhost/";
    private String SubServer_URL = "jdbc:mysql://localhost/";
    private String BackupSubServer_URL = "jdbc:mysql://localhost/";
    private String USERNAME = "base";
    private String PASSWORD = "theoreticsinc";
//    private String USERNAME = "root";
//    private String PASSWORD = "sa";
    public String EX_SentinelID;
    private Connection connection = null;
    private Connection backupConnection = null;
    private Statement st;
    public boolean mainorder;
    private boolean timeoutnow = false;
    private String dateTimeIN;
    private String dateTimeINStamp;
    private String dateTimePaid;
    private String dateTimePaidStamp;
    private String activeRateParameter;
    public static JFrame frameX = new JFrame();

    public DataBaseHandler() {
        try {
            XMLreader xr = new XMLreader();
            //MainServer_URL = "jdbc:mysql://" + xr.getElementValue("net.xml", "main1") + "";
            //SubServer_URL = "jdbc:mysql://" + xr.getElementValue("net.xml", "sub1") + "";
//            MainServer_URL = "jdbc:mysql://192.168.100.240";
//            SubServer_URL = "jdbc:mysql://192.168.100.240";
            //BackupMainServer_URL = "jdbc:mysql://" + xr.getElementValue("net.xml", "backupmain1") + "";
            //BackupSubServer_URL = "jdbc:mysql://" + xr.getElementValue("net.xml", "backupsub1") + "";
            //EX_SentinelID = xr.getElementValue("initH.xml", "HXterminal_id");
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

    public void connect() {
        try {
            Class.forName(DRIVER_CLASS_NAME);
            connection = DriverManager.getConnection("jdbc:mysql://localhost/carpark?autoReconnect=true&serverTimezone=UTC&useSSL=False&allowPublicKeyRetrieval=true", USERNAME, PASSWORD);
            //connection = DriverManager.getConnection("jdbc:mysql://localhost", USERNAME, PASSWORD);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

    public static void saveImage(String imageUrl, String destinationFile) throws IOException {
        URL url = new URL(imageUrl);
        InputStream is = url.openStream();
        OutputStream os = new FileOutputStream(destinationFile);

        byte[] b = new byte[2048];
        int length;

        while ((length = is.read(b)) != -1) {
            os.write(b, 0, length);
        }

        is.close();
        os.close();
    }
////
////    protected static void downloadDigest(URL url, FileOutputStream fos)
////            throws IOException {
////        HttpHost targetHost = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
////        CloseableHttpClient httpClient = HttpClients.createDefault();
////        HttpClientContext context = HttpClientContext.create();
////
////        String credential = url.getUserInfo();
////        if (credential != null) {
////            String user = credential.split(":")[0];
////            String password = credential.split(":")[1];
////
////            CredentialsProvider credsProvider = new BasicCredentialsProvider();
////            credsProvider.setCredentials(AuthScope.ANY,
////                    new UsernamePasswordCredentials(user, password));
////            AuthCache authCache = new BasicAuthCache();
////            DigestScheme digestScheme = new DigestScheme();
////            authCache.put(targetHost, digestScheme);
////
////            context.setCredentialsProvider(credsProvider);
////            context.setAuthCache(authCache);
////        }
////
////        HttpGet httpget = new HttpGet(url.getPath());
////
////        CloseableHttpResponse response = httpClient.execute(targetHost, httpget, context);
////
////        try {
////            ReadableByteChannel rbc = Channels.newChannel(response.getEntity().getContent());
////            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
////        } finally {
////            response.close();
////        }
////    }

    private void test() {
        try {
            String encodedAuthorizedUser = getAuthantication("username", "password");
            URL url = new URL("http://admin:user1234@192.168.1.64/Streaming/channels/1/picture");
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setRequestProperty("Authorization", "Basic " + encodedAuthorizedUser);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(DataBaseHandler.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

    }

    public String getAuthantication(String username, String password) {
        String loginPassword = "admin:user1234";
        String auth = new sun.misc.BASE64Encoder().encode(loginPassword.getBytes());
//        String auth = new String(sun.misc.BASE64Encoder().encode(username + ":" + password));
        return auth;
    }

    public void insertImageFromURLToDB(String ipaddress, String username, String password) {
        Connection connection = null;
        PreparedStatement statement = null;
        URLConnection uc1 = null;
        URLConnection uc2 = null;
        InputStream is1 = null;
        InputStream is2 = null;
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
                new javax.net.ssl.HostnameVerifier() {

            public boolean verify(String hostname,
                    javax.net.ssl.SSLSession sslSession) {
                return hostname.equals(ipaddress);
            }
        });
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, "user1234".toCharArray());
            }
        });
        try {
            String loginPassword = username + ":" + password;
            String encoded = new sun.misc.BASE64Encoder().encode(loginPassword.getBytes());

            //URL url = new URL("http://www.avajava.com/images/avajavalogo.jpg");
            //URL url = new URL("http://admin:user1234@192.168.1.64/Streaming/channels/1/picture");
            //HIKVISION IP Cameras
            URL url = new URL("http://" + username + ":" + password + "@" + ipaddress + "/onvif-http/snapshot?Profile_1");
            //URL url = new URL("http://192.168.100.220/onvifsnapshot/media_service/snapshot?channel=1&subtype=1");

            //URL url = new URL("http://admin:user1234@192.168.100.220/cgi-bin/snapshot.cgi?loginuse=admin&loginpas=user1234");
            //HttpURLConnection yc = (HttpURLConnection) url.openConnection();
            //yc.setRequestProperty("Authorization", "Basic " + encoded);
            //InputStream is = url.openStream();
            //**********************
            uc1 = url.openConnection();
            uc2 = url.openConnection();
            String userpass = username + ":" + password;
            //String userpass = "root" + ":" + "Th30r3t1cs";
            String basicAuth = "Basic " + new String(new sun.misc.BASE64Encoder().encode(userpass.getBytes()));
            uc1.setRequestProperty("Authorization", basicAuth);
            uc2.setRequestProperty("Authorization", basicAuth);
            //InputStream in = uc.getInputStream();

//if (yc.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
//     // Step 3. Create a authentication object from the challenge...
//     DigestAuthentication auth = DigestAuthentication.fromResponse(connection);
//     // ...with correct credentials
//     auth.username("user").password("passwd");
//
//     // Step 4 (Optional). Check if the challenge was a digest challenge of a supported type
//     if (!auth.canRespond()) {
//         // No digest challenge or a challenge of an unsupported type - do something else or fail
//         return;
//     }
//
//     // Step 5. Create a new connection, identical to the original one.
//     yc = (HttpURLConnection) url.openConnection();
//     // ...and set the Authorization header on the request, with the challenge response
//     yc.setRequestProperty(DigestChallengeResponse.HTTP_HEADER_AUTHORIZATION,
//         auth.getAuthorizationForRequest("GET", yc.getURL().getPath()));
// }
            is1 = (InputStream) uc1.getInputStream();
            is2 = (InputStream) uc2.getInputStream();
            connection = getConnection(true);
            statement = connection.prepareStatement("insert into unidb.timeindb(CardCode, Plate, PIC2, PIC) " + "values(?,?,?,?)");
            statement.setString(1, "HFJ93230");
            statement.setString(2, "ABCDEFG");
            statement.setBinaryStream(3, is1, 1024 * 96); //Last Parameter has to be bigger than actual 
            statement.setBinaryStream(4, is2, 1024 * 96); //Last Parameter has to be bigger than actual 
            statement.executeUpdate();
        } catch (FileNotFoundException e) {
//            System.out.println("FileNotFoundException: - " + e);
        } catch (Exception e) {
//            System.out.println("Exception: - " + e);
        } finally {

            try {
                connection.close();
                statement.close();
                is1.close();
                is2.close();
            } catch (Exception e) {
                //System.out.println("Exception Finally: - " + e);
            }
        }
    }

    public BufferedImage getImageFromCamera(String ipAdd, String username, String password) {
        Connection connection = null;
        BufferedImage buff = null;
        URLConnection uc1 = null;
        URLConnection uc2 = null;
        InputStream is1 = null;
        InputStream is2 = null;
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
                new javax.net.ssl.HostnameVerifier() {

            public boolean verify(String hostname,
                    javax.net.ssl.SSLSession sslSession) {
                return hostname.equals(ipAdd);
            }
        });
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password.toCharArray());
            }
        });
        try {
            String loginPassword = username + ":" + password;
            String encoded = new sun.misc.BASE64Encoder().encode(loginPassword.getBytes());

            //URL url = new URL("http://www.avajava.com/images/avajavalogo.jpg");
            //OLD HIKVISION IP Cameras
            //URL url = new URL("http://admin:user1234@192.168.1.64/Streaming/channels/1/picture");
            //HIKVISION DVR
            //URL url = new URL("http://"+username+":"+password+"@"+ipAdd+"/onvifsnapshot/media_service/snapshot?channel=1&subtype=0");
            //HIKVISION IP Cameras
//            URL url = new URL("http://"+username+":"+password+"@"+ipAdd+"/onvif-http/snapshot?Profile_1");
            //GWSecurity IP Cameras
//          URL url = new URL("http://"+username+":"+password+"@"+ipAdd+"/onvif/device_service");
            URL url = new URL("http://" + username + ":" + password + "@" + ipAdd + "/cgi-bin/snapshot.cgi?stream=0");

            //HttpURLConnection yc = (HttpURLConnection) url.openConnection();
            //yc.setRequestProperty("Authorization", "Basic " + encoded);
            //InputStream is = url.openStream();
            //**********************
            uc1 = url.openConnection();
            uc2 = url.openConnection();
            String userpass = username + ":" + password;
            //String userpass = "root" + ":" + "Th30r3t1cs";
            String basicAuth = "Basic " + new String(new sun.misc.BASE64Encoder().encode(userpass.getBytes()));
            uc1.setRequestProperty("Authorization", basicAuth);
            uc2.setRequestProperty("Authorization", basicAuth);
            //InputStream in = uc.getInputStream();

//if (yc.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
//     // Step 3. Create a authentication object from the challenge...
//     DigestAuthentication auth = DigestAuthentication.fromResponse(connection);
//     // ...with correct credentials
//     auth.username("user").password("passwd");
//
//     // Step 4 (Optional). Check if the challenge was a digest challenge of a supported type
//     if (!auth.canRespond()) {
//         // No digest challenge or a challenge of an unsupported type - do something else or fail
//         return;
//     }
//
//     // Step 5. Create a new connection, identical to the original one.
//     yc = (HttpURLConnection) url.openConnection();
//     // ...and set the Authorization header on the request, with the challenge response
//     yc.setRequestProperty(DigestChallengeResponse.HTTP_HEADER_AUTHORIZATION,
//         auth.getAuthorizationForRequest("GET", yc.getURL().getPath()));
// }
            is1 = (InputStream) uc1.getInputStream();
            is2 = (InputStream) uc2.getInputStream();
            if (is1 != null) {
                buff = ImageIO.read(is1);
            }

        } catch (FileNotFoundException e) {
            //System.out.println("FileNotFoundException: - " + e);
        } catch (Exception e) {
            //System.out.println("Exception: - " + e);
        } finally {

            try {

                is1.close();
                is2.close();
            } catch (Exception e) {
                //System.out.println("Exception Finally: - " + e);
            }
            return buff;
        }
    }

    public void insertImageToDB() {
        Connection connection = null;
        PreparedStatement statement = null;
        FileInputStream inputStream = null;

        try {
            File fileimage = new File("C:/Users/Theoretics Inc/Pictures/20190423_114857.jpg");
            //BufferedImage img = new BufferedImage(200, 200, BufferedImage.TYPE_BYTE_INDEXED);
            //URL url = new URL("http://admin:user1234@192.168.100.220/cgi-bin/snapshot.cgi?loginuse=admin&loginpas=user1234");
            //Image image = ImageIO.read(url);
            inputStream = new FileInputStream(fileimage);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            InputStream is = null;
            connection = getConnection(true);
            statement = connection.prepareStatement("insert into unidb.timeindb(CardCode, Plate, PIC) " + "values(?,?,?)");
            statement.setString(1, "HFJ93230");
            statement.setString(2, "ABCDEFG");
            statement.setBinaryStream(3, (InputStream) inputStream, (int) (fileimage.length()));
            //statement.setBinaryStream(3, (InputStream) is);
            statement.executeUpdate();

        } catch (FileNotFoundException e) {
            //System.out.println("FileNotFoundException: - " + e);
        } catch (SQLException e) {
            //System.out.println("SQLException: - " + e);
        } finally {

            try {
                connection.close();
                statement.close();
            } catch (SQLException e) {
                //System.out.println("SQLException Finally: - " + e);
            }
        }
    }

    public void ShowImageFromDB() {
        try {
            connection = getConnection(true);
            String sql = "SELECT CardCode, Plate, PIC FROM unidb.timeindb";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet resultSet = stmt.executeQuery();

            BufferedImage img = new BufferedImage(400, 400,
                    BufferedImage.TYPE_BYTE_INDEXED);

            while (resultSet.next()) {
                String name = resultSet.getString(1);
                String description = resultSet.getString(2);
                //File image = new File("C:\\card" + name + ".jpg");
                //FileOutputStream fos = new FileOutputStream(image);

                byte[] buffer = new byte[1];
                InputStream is = resultSet.getBinaryStream(3);
                //while (is.read(buffer) > 0) {
                //    fos.write(buffer);
                //}
                //is.close();

                //InputStream in = new FileInputStream("C:\\card" + name + ".jpg");
                img = ImageIO.read(is);
                is.close();
                //fos.close();
                //show(name, img, 7);
            }

            //Kernel kernel = new Kernel(3, 3, new float[] { -1, -1, -1, -1, 9, -1, -1,
            //    -1, -1 });
            //BufferedImageOp op = new ConvolveOp(kernel);
            //img = op.filter(img, null);
//        JFrame frame = new JFrame();
//        frame.getContentPane().setLayout(new FlowLayout());
//        frame.getContentPane().add(new JLabel(new ImageIcon(img)));
//        frame.pack();
//        frame.setVisible(true);
            //mediaPlayer.controls().stop();
            show("Captured", img, 7);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(DataBaseHandler.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    public BufferedImage GetImageFromDB(String cardNumber) {
        BufferedImage img = null;
        try {
            connection = getConnection(true);
            //String sql = "SELECT CardCode, Plate, PIC FROM unidb.timeindb WHERE CardCode = '" + cardNumber + "'";
            String sql = "SELECT cardNumber, plateNumber, PIC FROM crdplt.main WHERE cardNumber = '" + cardNumber + "'";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet resultSet = stmt.executeQuery();

            img = new BufferedImage(400, 400,
                    BufferedImage.TYPE_BYTE_INDEXED);

            while (resultSet.next()) {
                String name = resultSet.getString(1);
                String description = resultSet.getString(2);
                //File image = new File("C:\\card" + name + ".jpg");
                //FileOutputStream fos = new FileOutputStream(image);

                byte[] buffer = new byte[1];
                InputStream is = resultSet.getBinaryStream(3);
                //while (is.read(buffer) > 0) {
                //    fos.write(buffer);
                //}
                //is.close();

                //InputStream in = new FileInputStream("C:\\card" + name + ".jpg");
                try {
                    img = ImageIO.read(is);
                    is.close();
                } catch (Exception ex) {

                }

                //fos.close();
                //show(name, img, 7);
            }

            //Kernel kernel = new Kernel(3, 3, new float[] { -1, -1, -1, -1, 9, -1, -1,
            //    -1, -1 });
            //BufferedImageOp op = new ConvolveOp(kernel);
            //img = op.filter(img, null);
//        JFrame frame = new JFrame();
//        frame.getContentPane().setLayout(new FlowLayout());
//        frame.getContentPane().add(new JLabel(new ImageIcon(img)));
//        frame.pack();
//        frame.setVisible(true);
            //mediaPlayer.controls().stop();
            //show("Captured", img, 7);
            return img;

        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(DataBaseHandler.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return img;
    }

    public BufferedImage Get2ndImageFromDB(String cardNumber) {
        BufferedImage img = null;
        try {
            connection = getConnection(true);
            //String sql = "SELECT CardCode, Plate, PIC FROM unidb.timeindb WHERE CardCode = '" + cardNumber + "'";
            String sql = "SELECT cardNumber, plateNumber, PIC2 FROM crdplt.main WHERE cardNumber = '" + cardNumber + "'";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet resultSet = stmt.executeQuery();

            img = new BufferedImage(400, 400,
                    BufferedImage.TYPE_BYTE_INDEXED);

            while (resultSet.next()) {
                String name = resultSet.getString(1);
                String description = resultSet.getString(2);
                //File image = new File("C:\\card" + name + ".jpg");
                //FileOutputStream fos = new FileOutputStream(image);

                byte[] buffer = new byte[1];
                InputStream is = resultSet.getBinaryStream(3);
                //while (is.read(buffer) > 0) {
                //    fos.write(buffer);
                //}
                //is.close();

                //InputStream in = new FileInputStream("C:\\card" + name + ".jpg");
                try {
                    img = ImageIO.read(is);
                    is.close();
                } catch (Exception ex) {

                }

                //fos.close();
                //show(name, img, 7);
            }

            //Kernel kernel = new Kernel(3, 3, new float[] { -1, -1, -1, -1, 9, -1, -1,
            //    -1, -1 });
            //BufferedImageOp op = new ConvolveOp(kernel);
            //img = op.filter(img, null);
//        JFrame frame = new JFrame();
//        frame.getContentPane().setLayout(new FlowLayout());
//        frame.getContentPane().add(new JLabel(new ImageIcon(img)));
//        frame.pack();
//        frame.setVisible(true);
            //mediaPlayer.controls().stop();
            //show("Captured", img, 7);
            return img;

        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(DataBaseHandler.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return img;
    }

    public BufferedImage GetImageFromEXTCRDDB(String CardCode) {
        BufferedImage img = null;
        try {
            connection = getConnection(true);
            String sql = "SELECT PIC FROM extcrd.main WHERE cardNumber = '" + CardCode + "'";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet resultSet = stmt.executeQuery();

            img = new BufferedImage(400, 400,
                    BufferedImage.TYPE_BYTE_INDEXED);

            while (resultSet.next()) {
                InputStream is = resultSet.getBinaryStream(1);
                try {
                    img = ImageIO.read(is);
                    is.close();
                } catch (Exception ex) {

                }
            }
            return img;

        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(DataBaseHandler.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return img;
    }

    public int GetImageCountFromDB_byDate(String BeginDate, String EndDate) {

        try {
            connection = getConnection(true);
            //String sql = "SELECT COUNT(CardCode) AS Count FROM unidb.timeindb WHERE Timein BETWEEN '" + BeginDate + "' AND '" + EndDate + "'";
            String sql = "SELECT COUNT(cardNumber) AS Count FROM crdplt.main WHERE datetimeIN BETWEEN '" + BeginDate + "' AND '" + EndDate + "'";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet resultSet = stmt.executeQuery();

            int count = 0;
            while (resultSet.next()) {
                count = resultSet.getInt(1);
            }
            return count;

        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(DataBaseHandler.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return 0;
    }

    public BufferedImage[] GetImageFromDB_byDate(String BeginDate, String EndDate) {
        BufferedImage[] img = new BufferedImage[10];
        try {
            connection = getConnection(true);
            //String sql = "SELECT CardCode, Plate, PIC FROM unidb.timeindb WHERE Timein BETWEEN '" + BeginDate + "' AND '" + EndDate + "'";
            String sql = "SELECT cardNumber, plateNumber, PIC FROM crdplt.main WHERE datetimeIN BETWEEN '" + BeginDate + "' AND '" + EndDate + "'";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet resultSet = stmt.executeQuery();

            img[0] = new BufferedImage(400, 400,
                    BufferedImage.TYPE_BYTE_INDEXED);
            int i = 0;
            while (resultSet.next()) {
                String name = resultSet.getString(1);
                String description = resultSet.getString(2);
                //File image = new File("C:\\card" + name + ".jpg");
                //FileOutputStream fos = new FileOutputStream(image);

                byte[] buffer = new byte[1];
                InputStream is = resultSet.getBinaryStream(3);
                //while (is.read(buffer) > 0) {
                //    fos.write(buffer);
                //}
                //is.close();

                //InputStream in = new FileInputStream("C:\\card" + name + ".jpg");
                img[i] = ImageIO.read(is);
                i++;
                is.close();
                //fos.close();
                //show(name, img, 7);
            }

            //Kernel kernel = new Kernel(3, 3, new float[] { -1, -1, -1, -1, 9, -1, -1,
            //    -1, -1 });
            //BufferedImageOp op = new ConvolveOp(kernel);
            //img = op.filter(img, null);
//        JFrame frame = new JFrame();
//        frame.getContentPane().setLayout(new FlowLayout());
//        frame.getContentPane().add(new JLabel(new ImageIcon(img)));
//        frame.pack();
//        frame.setVisible(true);
            //mediaPlayer.controls().stop();
            //show("Captured", img, 7);
            return img;

        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(DataBaseHandler.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return img;
    }

    @SuppressWarnings("serial")
    private static void show(String title, final BufferedImage img, int i) {
        if (null != img) {
            JFrame f = new JFrame(title);
            frameX.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frameX.setContentPane(new JPanel() {
                @Override
                protected void paintChildren(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.drawImage(img, null, 0, 0);
                }

                @Override
                public Dimension getPreferredSize() {
                    return new Dimension(img.getWidth(), img.getHeight());
                }
            });
            frameX.pack();
            frameX.setLocation(50 + (i * 5), 50 + (i * 5));
            frameX.setVisible(true);
        } else {
            System.out.println("No Image Captured");
        }
    }

    public ResultSet selectDatabyFields(String sql) {
        ResultSet res = null;
        try {
            st = (Statement) connection.createStatement();
            res = st.executeQuery(sql);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return res;
    }

    public ResultSet selectDatabyFields(String sql, Connection connection) {
        ResultSet res = null;
        try {
            st = (Statement) connection.createStatement();
            res = st.executeQuery(sql);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return res;
    }

    public void getActiveRatesParameter() throws SQLException {
        connection = getLocalConnection(true);
        ResultSet rs = selectDatabyFields("SELECT * FROM ratesparam.types WHERE ACTIVE=1");
        String typeName = "";
        // iterate through the java resultset
        while (rs.next()) {
            typeName = rs.getString("typename");
        }
        activeRateParameter = typeName;
        st.close();
        connection.close();
    }

    public void getEntranceCard() throws SQLException {
        connection = getConnection(true);
        ResultSet rs = selectDatabyFields("SELECT * FROM carpark.entrance");

        // iterate through the java resultset
        while (rs.next()) {
            int id = rs.getInt(1);
            String firstName = rs.getString("CardNumber");
            String lastName = rs.getString("PlateNumber");
            Date dateCreated = rs.getDate("TimeIN");

            // print the results
            System.out.format("%s, %s, %s, %s\n", id, firstName, lastName, dateCreated);
        }
        st.close();
        connection.close();
    }

    public String getLoginUsername(String loginCode, String password) {
        String username = "";
        try {

            connection = getConnection(true);
            ResultSet rs = selectDatabyFields("SELECT * FROM pos_users.main WHERE usercode='" + loginCode + "' AND password = MD5('" + password + "')");

            // iterate through the java resultset
            while (rs.next()) {
                int id = rs.getInt(1);
                username = rs.getString("username");

            }
            st.close();
            connection.close();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return username;
    }

    public boolean getLoginPassword(String loginCode, String password) {
        boolean found = false;
        try {

            connection = getConnection(true);
            ResultSet rs = selectDatabyFields("SELECT password FROM pos_users.main WHERE usercode='" + loginCode + "' AND password = MD5('" + password + "')");

            // iterate through the java resultset
            while (rs.next()) {
                found = true;
            }
            st.close();
            connection.close();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return found;
    }

    public ResultSet getAllPtypes() {
        try {
            ResultSet rs = selectDatabyFields("SELECT * FROM parkertypes.main");
            return rs;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return null;
    }

    public ResultSet getAllActivePtypes() {
        try {
            ResultSet rs = selectDatabyFields("SELECT * FROM parkertypes.main WHERE ACTIVE = '1' ORDER BY ptypeID");
            return rs;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return null;
    }

    public boolean findEntranceCard(String cardNumber) throws SQLException {
        boolean found = false;
        connection = getConnection(true);
        //ResultSet rs = selectDatabyFields("SELECT Timein FROM unidb.timeindb WHERE CardCode = '" + cardNumber + "'");
        ResultSet rs = selectDatabyFields("SELECT datetimeIN"
                + " FROM crdplt.main WHERE cardNumber = '" + cardNumber + "'");
        // iterate through the java resultset
        while (rs.next()) {
            dateTimeIN = rs.getString("datetimeIN");
            found = true;
        }
        st.close();
        connection.close();
        return found;
    }

    public boolean findExitCard(String cardNumber) throws SQLException {
        boolean found = false;
        connection = getConnection(true);
        ResultSet rs = selectDatabyFields("SELECT datetimeIN FROM extcrd.main WHERE cardNumber = '" + cardNumber + "'");
        // iterate through the java resultset
        while (rs.next()) {
            dateTimeIN = rs.getString("datetimeIN");
            found = true;
        }
        st.close();
        connection.close();
        return found;
    }

    public boolean eraseEntryCard(String cardNumber) throws SQLException {
        boolean found = false;
        connection = getConnection(true);
        st = (Statement) connection.createStatement();
        //st.execute("DELETE FROM unidb.timeindb WHERE CardCode = '" + cardNumber + "'");
        st.execute("DELETE FROM crdplt.main WHERE cardNumber = '" + cardNumber + "'");
        st.close();
        connection.close();
        found = findEntranceCard(cardNumber);
        return !found;
    }

    public boolean eraseExitCard(String cardNumber) throws SQLException {
        boolean found = false;
        connection = getConnection(true);
        st = (Statement) connection.createStatement();
        st.execute("DELETE FROM extcrd.main WHERE CardNumber = '" + cardNumber + "'");
        st.close();
        connection.close();
        found = findExitCard(cardNumber);
        return !found;
    }

    public String getDateTimeINStamp() {
        return dateTimeINStamp;
    }

    public String[] getHourParams(String trtype, int newArraySize) {
        if (newArraySize < 30) {
            newArraySize = 30;
        }
        String data[] = new String[newArraySize];
        try {
            connection = getLocalConnection(true);
            ResultSet rs = selectDatabyFields("SELECT * FROM ratesparam." + activeRateParameter + " WHERE trtype = '" + trtype + "'");
            // iterate through the java resultset
            while (rs.next()) {
                //String gracePeriod = rs.getString("GracePeriod");
                //String otCutoff = rs.getString("OTCutoff");
                String Hr0 = rs.getString("Hr0");
                String Hr1 = rs.getString("Hr1");
                String Hr2 = rs.getString("Hr2");
                String Hr3 = rs.getString("Hr3");
                String Hr4 = rs.getString("Hr4");
                String Hr5 = rs.getString("Hr5");
                String Hr6 = rs.getString("Hr6");
                String Hr7 = rs.getString("Hr7");
                String Hr8 = rs.getString("Hr8");
                String Hr9 = rs.getString("Hr9");
                String Hr10 = rs.getString("Hr10");
                String Hr11 = rs.getString("Hr11");
                String Hr12 = rs.getString("Hr12");
                String Hr13 = rs.getString("Hr13");
                String Hr14 = rs.getString("Hr14");
                String Hr15 = rs.getString("Hr15");
                String Hr16 = rs.getString("Hr16");
                String Hr17 = rs.getString("Hr17");
                String Hr18 = rs.getString("Hr18");
                String Hr19 = rs.getString("Hr19");
                String Hr20 = rs.getString("Hr20");
                String Hr21 = rs.getString("Hr21");
                String Hr22 = rs.getString("Hr22");
                String Hr23 = rs.getString("Hr23");
                String Hr24 = rs.getString("Hr24");

                // print the results
                //System.out.format("%s, %s, %s\n", Hr0, Hr2, Hr3);
                data[0] = Hr0;
                data[1] = Hr1;
                data[2] = Hr2;
                data[3] = Hr3;
                data[4] = Hr4;
                data[5] = Hr5;
                data[6] = Hr6;
                data[7] = Hr7;
                data[8] = Hr8;
                data[9] = Hr9;
                data[10] = Hr10;
                data[11] = Hr11;
                data[12] = Hr12;
                data[13] = Hr13;
                data[14] = Hr14;
                data[15] = Hr15;
                data[16] = Hr16;
                data[17] = Hr17;
                data[18] = Hr18;
                data[19] = Hr19;
                data[20] = Hr20;
                data[21] = Hr21;
                data[22] = Hr22;
                data[23] = Hr23;
                data[24] = Hr24;

            }
            st.close();
            connection.close();
            return data;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return data;
    }

    public String[] getHourPlusParams(String trtype, int newArraySize) {
        if (newArraySize < 30) {
            newArraySize = 30;
        }
        String data[] = new String[newArraySize];
        try {
            connection = getLocalConnection(true);
            ResultSet rs = selectDatabyFields("SELECT * FROM ratesparam." + activeRateParameter + " WHERE trtype = '" + trtype + "'");
            // iterate through the java resultset
            while (rs.next()) {
                //String gracePeriod = rs.getString("GracePeriod");
                //String otCutoff = rs.getString("OTCutoff");
                String Hr0 = rs.getString("Hr0plus");
                String Hr1 = rs.getString("Hr1plus");
                String Hr2 = rs.getString("Hr2plus");
                String Hr3 = rs.getString("Hr3plus");
                String Hr4 = rs.getString("Hr4plus");
                String Hr5 = rs.getString("Hr5plus");
                String Hr6 = rs.getString("Hr6plus");
                String Hr7 = rs.getString("Hr7plus");
                String Hr8 = rs.getString("Hr8plus");
                String Hr9 = rs.getString("Hr9plus");
                String Hr10 = rs.getString("Hr10plus");
                String Hr11 = rs.getString("Hr11plus");
                String Hr12 = rs.getString("Hr12plus");
                String Hr13 = rs.getString("Hr13plus");
                String Hr14 = rs.getString("Hr14plus");
                String Hr15 = rs.getString("Hr15plus");
                String Hr16 = rs.getString("Hr16plus");
                String Hr17 = rs.getString("Hr17plus");
                String Hr18 = rs.getString("Hr18plus");
                String Hr19 = rs.getString("Hr19plus");
                String Hr20 = rs.getString("Hr20plus");
                String Hr21 = rs.getString("Hr21plus");
                String Hr22 = rs.getString("Hr22plus");
                String Hr23 = rs.getString("Hr23plus");

                // print the results
                //System.out.format("%s, %s, %s\n", Hr0, Hr2, Hr3);
                data[0] = Hr0;
                data[1] = Hr1;
                data[2] = Hr2;
                data[3] = Hr3;
                data[4] = Hr4;
                data[5] = Hr5;
                data[6] = Hr6;
                data[7] = Hr7;
                data[8] = Hr8;
                data[9] = Hr9;
                data[10] = Hr10;
                data[11] = Hr11;
                data[12] = Hr12;
                data[13] = Hr13;
                data[14] = Hr14;
                data[15] = Hr15;
                data[16] = Hr16;
                data[17] = Hr17;
                data[18] = Hr18;
                data[19] = Hr19;
                data[20] = Hr20;
                data[21] = Hr21;
                data[22] = Hr22;
                data[23] = Hr23;
                data[24] = "+0";

            }
            st.close();
            connection.close();
            return data;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return data;
    }

    public Boolean getOTWaived1st(String trtype) {

        Boolean data = false;
        try {
            connection = getLocalConnection(true);
            ResultSet rs = selectDatabyFields("SELECT OTCutoff1stWaived FROM ratesparam." + activeRateParameter + " WHERE trtype = '" + trtype + "'");
            // iterate through the java resultset
            while (rs.next()) {
                //String gracePeriod = rs.getString("GracePeriod");
                //String otCutoff = rs.getString("OTCutoff");
                data = rs.getBoolean("OTCutoff1stWaived");

            }
            st.close();
            connection.close();
            return data;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return data;
    }

    public Boolean getFractionThereOf(String trtype) {

        Boolean data = false;
        try {
            connection = getLocalConnection(true);
            ResultSet rs = selectDatabyFields("SELECT FractionThereOf FROM ratesparam." + activeRateParameter + " WHERE trtype = '" + trtype + "'");
            // iterate through the java resultset
            while (rs.next()) {
                //String gracePeriod = rs.getString("GracePeriod");
                //String otCutoff = rs.getString("OTCutoff");
                data = rs.getBoolean("FractionThereOf");

            }
            st.close();
            connection.close();
            return data;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return data;
    }

    public Boolean[] getHourWaived1st(String trtype, int newArraySize) {
        if (newArraySize < 30) {
            newArraySize = 30;
        }
        Boolean data[] = new Boolean[newArraySize];
        try {
            connection = getLocalConnection(true);
            ResultSet rs = selectDatabyFields("SELECT * FROM ratesparam." + activeRateParameter + " WHERE trtype = '" + trtype + "'");
            // iterate through the java resultset
            while (rs.next()) {
                //String gracePeriod = rs.getString("GracePeriod");
                //String otCutoff = rs.getString("OTCutoff");
                Boolean Hr0 = rs.getBoolean("Hr0Waived1st");
                Boolean Hr1 = rs.getBoolean("Hr1Waived1st");
                Boolean Hr2 = rs.getBoolean("Hr2Waived1st");
                Boolean Hr3 = rs.getBoolean("Hr3Waived1st");
                Boolean Hr4 = rs.getBoolean("Hr4Waived1st");
                Boolean Hr5 = rs.getBoolean("Hr5Waived1st");
                Boolean Hr6 = rs.getBoolean("Hr6Waived1st");
                Boolean Hr7 = rs.getBoolean("Hr7Waived1st");
                Boolean Hr8 = rs.getBoolean("Hr8Waived1st");
                Boolean Hr9 = rs.getBoolean("Hr9Waived1st");
                Boolean Hr10 = rs.getBoolean("Hr10Waived1st");
                Boolean Hr11 = rs.getBoolean("Hr11Waived1st");
                Boolean Hr12 = rs.getBoolean("Hr12Waived1st");
                Boolean Hr13 = rs.getBoolean("Hr13Waived1st");
                Boolean Hr14 = rs.getBoolean("Hr14Waived1st");
                Boolean Hr15 = rs.getBoolean("Hr15Waived1st");
                Boolean Hr16 = rs.getBoolean("Hr16Waived1st");
                Boolean Hr17 = rs.getBoolean("Hr17Waived1st");
                Boolean Hr18 = rs.getBoolean("Hr18Waived1st");
                Boolean Hr19 = rs.getBoolean("Hr19Waived1st");
                Boolean Hr20 = rs.getBoolean("Hr20Waived1st");
                Boolean Hr21 = rs.getBoolean("Hr21Waived1st");
                Boolean Hr22 = rs.getBoolean("Hr22Waived1st");
                Boolean Hr23 = rs.getBoolean("Hr23Waived1st");
                Boolean Hr24 = rs.getBoolean("Hr24Waived1st");

                // print the results
                //System.out.format("%s, %s, %s\n", Hr0, Hr2, Hr3);
                data[0] = Hr0;
                data[1] = Hr1;
                data[2] = Hr2;
                data[3] = Hr3;
                data[4] = Hr4;
                data[5] = Hr5;
                data[6] = Hr6;
                data[7] = Hr7;
                data[8] = Hr8;
                data[9] = Hr9;
                data[10] = Hr10;
                data[11] = Hr11;
                data[12] = Hr12;
                data[13] = Hr13;
                data[14] = Hr14;
                data[15] = Hr15;
                data[16] = Hr16;
                data[17] = Hr17;
                data[18] = Hr18;
                data[19] = Hr19;
                data[20] = Hr20;
                data[21] = Hr21;
                data[22] = Hr22;
                data[23] = Hr23;
                data[24] = Hr24;

            }
            st.close();
            connection.close();
            return data;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return data;
    }

    public Boolean[] getHourPlusWaived1st(String trtype, int newArraySize) {
        if (newArraySize < 30) {
            newArraySize = 30;
        }
        Boolean data[] = new Boolean[newArraySize];
        try {
            connection = getLocalConnection(true);
            ResultSet rs = selectDatabyFields("SELECT * FROM ratesparam." + activeRateParameter + " WHERE trtype = '" + trtype + "'");
            // iterate through the java resultset
            while (rs.next()) {
                //Boolean gracePeriod = rs.getBoolean("GracePeriod");
                //Boolean otCutoff = rs.getBoolean("OTCutoff");
                Boolean Hr0 = rs.getBoolean("Hr0plusWaived1st");
                Boolean Hr1 = rs.getBoolean("Hr1plusWaived1st");
                Boolean Hr2 = rs.getBoolean("Hr2plusWaived1st");
                Boolean Hr3 = rs.getBoolean("Hr3plusWaived1st");
                Boolean Hr4 = rs.getBoolean("Hr4plusWaived1st");
                Boolean Hr5 = rs.getBoolean("Hr5plusWaived1st");
                Boolean Hr6 = rs.getBoolean("Hr6plusWaived1st");
                Boolean Hr7 = rs.getBoolean("Hr7plusWaived1st");
                Boolean Hr8 = rs.getBoolean("Hr8plusWaived1st");
                Boolean Hr9 = rs.getBoolean("Hr9plusWaived1st");
                Boolean Hr10 = rs.getBoolean("Hr10plusWaived1st");
                Boolean Hr11 = rs.getBoolean("Hr11plusWaived1st");
                Boolean Hr12 = rs.getBoolean("Hr12plusWaived1st");
                Boolean Hr13 = rs.getBoolean("Hr13plusWaived1st");
                Boolean Hr14 = rs.getBoolean("Hr14plusWaived1st");
                Boolean Hr15 = rs.getBoolean("Hr15plusWaived1st");
                Boolean Hr16 = rs.getBoolean("Hr16plusWaived1st");
                Boolean Hr17 = rs.getBoolean("Hr17plusWaived1st");
                Boolean Hr18 = rs.getBoolean("Hr18plusWaived1st");
                Boolean Hr19 = rs.getBoolean("Hr19plusWaived1st");
                Boolean Hr20 = rs.getBoolean("Hr20plusWaived1st");
                Boolean Hr21 = rs.getBoolean("Hr21plusWaived1st");
                Boolean Hr22 = rs.getBoolean("Hr22plusWaived1st");
                Boolean Hr23 = rs.getBoolean("Hr23plusWaived1st");
                //Boolean Hr24 = rs.getBoolean("Hr24plusWaived1st");

                // print the results
                //System.out.format("%s, %s, %s\n", Hr0, Hr2, Hr3);
                data[0] = Hr0;
                data[1] = Hr1;
                data[2] = Hr2;
                data[3] = Hr3;
                data[4] = Hr4;
                data[5] = Hr5;
                data[6] = Hr6;
                data[7] = Hr7;
                data[8] = Hr8;
                data[9] = Hr9;
                data[10] = Hr10;
                data[11] = Hr11;
                data[12] = Hr12;
                data[13] = Hr13;
                data[14] = Hr14;
                data[15] = Hr15;
                data[16] = Hr16;
                data[17] = Hr17;
                data[18] = Hr18;
                data[19] = Hr19;
                data[20] = Hr20;
                data[21] = Hr21;
                data[22] = Hr22;
                data[23] = Hr23;
                data[24] = false;
            }
            st.close();
            connection.close();
            return data;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return data;
    }

    public int getGracePeriod(String trtype) {
        int data = 0;
        try {
            connection = getLocalConnection(true);
            ResultSet rs = selectDatabyFields("SELECT GracePeriod FROM ratesparam." + activeRateParameter + " WHERE trtype = '" + trtype + "'");
            // iterate through the java resultset
            while (rs.next()) {
                //String gracePeriod = rs.getString("GracePeriod");
                //String otCutoff = rs.getString("OTCutoff");
                int GracePeriod = rs.getInt("GracePeriod");
                // print the results
                //System.out.format("%s, %s, %s\n", Hr0, Hr2, Hr3);
                data = GracePeriod;

            }
            st.close();
            connection.close();
            return data;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return data;
    }

    public String getLostPrice(String trtype) {
        String data = "";
        try {
            connection = getLocalConnection(true);
            ResultSet rs = selectDatabyFields("SELECT LostPrice FROM ratesparam." + activeRateParameter + " WHERE trtype = '" + trtype + "'");
            // iterate through the java resultset
            while (rs.next()) {
                //String gracePeriod = rs.getString("GracePeriod");
                //String otCutoff = rs.getString("OTCutoff");
                String LostPrice = rs.getString("LostPrice");
                // print the results
                //System.out.format("%s, %s, %s\n", Hr0, Hr2, Hr3);
                data = LostPrice;

            }
            st.close();
            connection.close();
            return data;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return data;
    }

    public int getEveryNthHour(String trtype) {
        int data = 0;
        try {
            connection = getLocalConnection(true);
            ResultSet rs = selectDatabyFields("SELECT EveryNthHour FROM ratesparam." + activeRateParameter + " WHERE trtype = '" + trtype + "'");
            // iterate through the java resultset
            while (rs.next()) {
                //String gracePeriod = rs.getString("GracePeriod");
                //String otCutoff = rs.getString("OTCutoff");
                int GracePeriod = rs.getInt("EveryNthHour");
                // print the results
                //System.out.format("%s, %s, %s\n", Hr0, Hr2, Hr3);
                data = GracePeriod;

            }
            st.close();
            connection.close();
            return data;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return data;
    }

    public String getOTPrice(String ParkerType) {
        String data = "+0";
        try {
            connection = getLocalConnection(true);
            ResultSet rs = selectDatabyFields("SELECT OTPrice FROM ratesparam." + activeRateParameter + " WHERE trtype = '" + ParkerType + "'");
            // iterate through the java resultset
            while (rs.next()) {
                //String gracePeriod = rs.getString("GracePeriod");
                //String otCutoff = rs.getString("OTCutoff");
                String OTCutoff = rs.getString("OTPrice");
                // print the results
                //System.out.format("%s, %s, %s\n", Hr0, Hr2, Hr3);
                data = OTCutoff;

            }
            st.close();
            connection.close();
            return data;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return data;
    }

    public String getNthHourRate(String ParkerType) {
        String data = "+0";
        try {
            connection = getLocalConnection(true);
            ResultSet rs = selectDatabyFields("SELECT NthHourRate FROM ratesparam." + activeRateParameter + " WHERE trtype = '" + ParkerType + "'");
            // iterate through the java resultset
            while (rs.next()) {
                //String gracePeriod = rs.getString("GracePeriod");
                //String otCutoff = rs.getString("OTCutoff");
                String OTCutoff = rs.getString("NthHourRate");
                // print the results
                //System.out.format("%s, %s, %s\n", Hr0, Hr2, Hr3);
                data = OTCutoff;

            }
            st.close();
            connection.close();
            return data;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return data;
    }

    public int getOTCutoff(String trtype) {
        int data = 0;
        try {
            connection = getLocalConnection(true);
            ResultSet rs = selectDatabyFields("SELECT OTCutoff FROM ratesparam." + activeRateParameter + " WHERE trtype = '" + trtype + "'");
            // iterate through the java resultset
            while (rs.next()) {
                //String gracePeriod = rs.getString("GracePeriod");
                //String otCutoff = rs.getString("OTCutoff");
                int OTCutoff = rs.getInt("OTCutoff");
                // print the results
                //System.out.format("%s, %s, %s\n", Hr0, Hr2, Hr3);
                data = OTCutoff;

            }
            st.close();
            connection.close();
            return data;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return data;
    }

    public String getSucceedingRate(String trtype) {
        String data = "";
        try {
            connection = getLocalConnection(true);
            ResultSet rs = selectDatabyFields("SELECT SucceedingRate FROM ratesparam." + activeRateParameter + " WHERE trtype = '" + trtype + "'");
            // iterate through the java resultset
            while (rs.next()) {
                //String gracePeriod = rs.getString("GracePeriod");
                //String otCutoff = rs.getString("OTCutoff");
                String SucceedingRate = rs.getString("SucceedingRate");
                // print the results
                //System.out.format("%s, %s, %s\n", Hr0, Hr2, Hr3);
                data = SucceedingRate;

            }
            st.close();
            connection.close();
            return data;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return data;
    }

    public int getTreatNextDayAsNewDay(String trtype) {
        int data = 0;
        try {
            connection = getLocalConnection(true);
            ResultSet rs = selectDatabyFields("SELECT TreatNextDayAsNewDay FROM ratesparam." + activeRateParameter + " WHERE trtype = '" + trtype + "'");
            // iterate through the java resultset
            while (rs.next()) {
                //String gracePeriod = rs.getString("GracePeriod");
                //String otCutoff = rs.getString("OTCutoff");
                int OTCutoff = rs.getInt("TreatNextDayAsNewDay");
                // print the results
                //System.out.format("%s, %s, %s\n", Hr0, Hr2, Hr3);
                data = OTCutoff;

            }
            st.close();
            connection.close();
            return data;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return data;
    }

    public String getEntDatefromCard(String cardNumber) throws SQLException {
        String dateIN = "";
        connection = getConnection(true);
        //ResultSet rs = selectDatabyFields("SELECT Timein FROM unidb.timeindb WHERE CardCode = '" + cardNumber + "'");
        ResultSet rs = selectDatabyFields("SELECT datetimeIN FROM crdplt.main WHERE cardNumber = '" + cardNumber + "'");

        while (rs.next()) {
            dateIN = rs.getString("TimeIN");
        }

        st.close();
        connection.close();
        return dateIN;
    }

    public String getEntDatefromExitCardDB(String cardNumber) throws SQLException {
        String dateIN = "";
        connection = getConnection(true);
        ResultSet rs = selectDatabyFields("SELECT datetimeIN FROM extcrd.main WHERE CardNumber = '" + cardNumber + "'");

        while (rs.next()) {
            dateIN = rs.getString("datetimeIN");
        }

        st.close();
        connection.close();
        return dateIN;
    }

    public String getExtDatefromCard(String cardNumber) throws SQLException {
        String dateIN = "";
        connection = getConnection(true);
        //ResultSet rs = selectDatabyFields("SELECT Timein FROM unidb.timeindb WHERE CardCode = '" + cardNumber + "'");
        ResultSet rs = selectDatabyFields("SELECT datetimeIN FROM crdplt.main WHERE cardNumber = '" + cardNumber + "'");

        while (rs.next()) {
            dateIN = rs.getString("datetimePaid");
        }

        st.close();
        connection.close();
        return dateIN;
    }

    public boolean copyCRDPLTfromServer(String tableNameServer, String tableNameLocal) {
        try {
            connection = getConnection(true);
            backupConnection = getBACKUPConnection();
            String lTime = "2018-1-1 00:00:00";
            ResultSet res = selectDatabyFields("SELECT * FROM netmanager.main WHERE tableName = 'crdplt'", connection);
            while (res.next()) {
                lTime = res.getString("lastTime");
            }
            String SQL = "SELECT * FROM " + tableNameServer + " where datetimeIN > '" + lTime + "' ORDER BY datetimeIN ASC";
            ResultSet rs = selectDatabyFields(SQL, backupConnection);
            while (rs.next()) {
                String a = rs.getString(1);
                String b = rs.getString(2);
                String c = rs.getString(3);
                String d = rs.getString(4);
                String e = rs.getString(5);
                String f = rs.getString(6);
                String g = rs.getString(7);
                String h = rs.getString(8);
                System.out.println(a);
                System.out.println(b);
                System.out.println(c);
                System.out.println(d);
                System.out.println(e);
                System.out.println(f);
                System.out.println(g);
                System.out.println(h);
                String lastTime = rs.getString("datetimeIN");
                System.out.println(lastTime);

                st = (Statement) connection.createStatement();
                st.execute("INSERT INTO " + tableNameLocal + " (areaID, entranceID, cardNumber, plateNumber, trtype, isLost, datetimeIN, datetimeINStamp) VALUES ('" + a + "', '" + b + "', '" + c + "', '" + d + "', '" + e + "', '" + f + "', '" + g + "', '" + h + "')");
                st.close();

                st = (Statement) connection.createStatement();
                st.execute("UPDATE netmanager.main SET lastTime = '" + lastTime + "' WHERE main.tableName = 'crdplt';");
                st.close();

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println(ex.getMessage());
        }
        return false;
    }

    public void saveCRDPLTEntry() throws SQLException {
        connection = getConnection(true);
        ResultSet rs = selectDatabyFields("INSERT * FROM carpark.entrance");

        // iterate through the java resultset
        while (rs.next()) {
            int id = rs.getInt(1);
            String firstName = rs.getString("CardNumber");
            String lastName = rs.getString("PlateNumber");
            Date dateCreated = rs.getDate("TimeIN");

            // print the results
            System.out.format("%s, %s, %s, %s\n", id, firstName, lastName, dateCreated);
        }
        st.close();
        connection.close();
    }

    public boolean copyExitTransfromLocal(String tableNameLocal, String tableNameServer) {
        try {
            connection = getConnection(true);
            backupConnection = getBACKUPConnection();
            String lTime = "2018-1-1 00:00:00";
            ResultSet res = selectDatabyFields("SELECT * FROM netmanager.main WHERE tableName = 'exit_trans'", connection);
            while (res.next()) {
                lTime = res.getString("lastTime");
            }
            String SQL = "SELECT * FROM " + tableNameLocal + " where DateTimeOUT > '" + lTime + "' AND DateTimeOUT <> '0000-00-00 00:00:00' AND DateTimeIN <> '0000-00-00 00:00:00' ORDER BY DateTimeOUT ASC";
            ResultSet rs = selectDatabyFields(SQL, connection);
            while (rs.next()) {
                String a = rs.getString(1);
                String b = rs.getString(2);
                String c = rs.getString(3);
                String d = rs.getString(4);
                String e = rs.getString(5);
                String f = rs.getString(6);
                String g = rs.getString(7);
                String h = rs.getString(8);
                String i = rs.getString(9);
                String j = rs.getString(10);//DateTimeIN
                String k = rs.getString(11);//DateTimeOUT
                String l = rs.getString(12);
                String m = rs.getString(13);
                String n = rs.getString(14);
                String lastTime = rs.getString("DateTimeOUT");
                System.out.println("ExitTrans" + lastTime);
                st = (Statement) backupConnection.createStatement();
                st.execute("INSERT INTO " + tableNameServer + " VALUES (NULL, '" + b + "', '" + c + "', '" + d + "', '" + e + "', '" + f + "', '" + g + "', '" + h + "', '" + i + "', '" + j + "', '" + k + "', '" + l + "', '" + m + "', '" + n + "')");
                st.close();

                st = (Statement) connection.createStatement();
                st.execute("UPDATE netmanager.main SET lastTime = '" + lastTime + "' WHERE main.tableName = 'exit_trans';");
                st.close();

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println(ex.getMessage());
        }
        return false;
    }

    public boolean copyZReadfromLocal(String tableNameLocal, String tableNameServer) {
        try {
            connection = getConnection(true);
            backupConnection = getBACKUPConnection();
            String lTime = "2018-1-1 00:00:00";
            ResultSet res = selectDatabyFields("SELECT * FROM netmanager.main WHERE tableName = 'zread'", connection);
            while (res.next()) {
                lTime = res.getString("lastTime");
            }
            ResultSet rs = selectDatabyFields("SELECT * FROM " + tableNameLocal + " where datetimeOut > '" + lTime + "' AND datetimeOut <> '0000-00-00 00:00:00' AND datetimeIn <> '0000-00-00 00:00:00' ORDER BY datetimeOut ASC", connection);
            while (rs.next()) {
                String a = rs.getString(1);
                String b = rs.getString(2);
                String c = rs.getString(3);
                String d = rs.getString(4);
                String e = rs.getString(5);
                String f = rs.getString(6);
                String g = rs.getString(7);
                String h = rs.getString(8);
                String i = rs.getString(9);
                String j = rs.getString(10);//DateTimeIN
                String k = rs.getString(11);//DateTimeOUT
                String l = rs.getString(12);
                String m = rs.getString(13);
                String n = rs.getString(14);
                String o = rs.getString(15);
                String lastTime = rs.getString("datetimeOut");
                System.out.println("Zread" + lastTime);
                st = (Statement) backupConnection.createStatement();
                st.execute("INSERT INTO " + tableNameServer + " VALUES ('" + a + "', '" + b + "', '" + c + "', '" + d + "', '" + e + "', '" + f + "', '" + g + "', '" + h + "', '" + i + "', '" + j + "', '" + k + "', '" + l + "', '" + m + "', '" + n + "', '" + o + "')");
                st.close();

                st = (Statement) connection.createStatement();
                st.execute("UPDATE netmanager.main SET lastTime = '" + lastTime + "' WHERE main.tableName = 'zread';");
                st.close();

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println(ex.getMessage());
        }
        return false;
    }

    public boolean copyColltrainfromLocal(String tableNameLocal, String tableNameServer) {
        try {
            connection = getConnection(true);
            backupConnection = getBACKUPConnection();
            String lTime = "2018-1-1 00:00:00";
            ResultSet res = selectDatabyFields("SELECT * FROM netmanager.main WHERE tableName = 'colltrain'", connection);
            while (res.next()) {
                lTime = res.getString("lastTime");
            }
            ResultSet rs = selectDatabyFields("SELECT * FROM " + tableNameLocal + " where logoutStamp > '" + lTime + "' AND logoutStamp <> '0000-00-00 00:00:00' ORDER BY logoutStamp ASC", connection);
            while (rs.next()) {
                String a = rs.getString(1);
                String b = rs.getString(2);
                String c = rs.getString(3);
                String d = rs.getString(4);
                String e = rs.getString(5);
                String f = rs.getString(6);
                String g = rs.getString(7);
                String h = rs.getString(8);
                String i = rs.getString(9);
                String j = rs.getString(10);
                String k = rs.getString(11);
                String l = rs.getString(12);
                String m = rs.getString(13);
                String n = rs.getString(14);
                String o = rs.getString(15);
                String p = rs.getString(16);
                String q = rs.getString(17);
                String r = rs.getString(18);
                String s = rs.getString(19);
                String t = rs.getString(20);
                String u = rs.getString(21);
                String v = rs.getString(22);
                String w = rs.getString(23);
                String x = rs.getString(24);
                String y = rs.getString(25);
                String z = rs.getString(26);
                String A = rs.getString(27);
                String B = rs.getString(28);
                String C = rs.getString(29);
                String D = rs.getString(30);
                String E = rs.getString(31);
                String F = rs.getString(32);
                String G = rs.getString(33);
                String H = rs.getString(34);

                String lastTime = rs.getString("logoutStamp");
                System.out.println("CollTrain" + lastTime);
                st = (Statement) backupConnection.createStatement();
                st.execute("INSERT INTO " + tableNameServer + " VALUES ('" + a + "', '" + b + "', '" + c + "', '" + d + "', '" + e + "', '" + f + "', '" + g + "', '" + h + "', '" + i + "', '" + j + "', '" + k + "', '" + l + "', '" + m + "', '" + n + "', '" + o + "', '" + p + "', '" + q + "', '" + r + "', '" + s + "', '" + t + "', '" + u + "', '" + v + "', '" + w + "', '" + x + "', '" + y + "', '" + z
                        + "', '" + A + "', '" + B + "', '" + C + "', '" + D + "', '" + E + "', '" + F + "', '" + G + "', '" + H + "')");
                st.close();

                st = (Statement) connection.createStatement();
                st.execute("UPDATE netmanager.main SET lastTime = '" + lastTime + "' WHERE main.tableName = 'colltrain';");
                st.close();

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println(ex.getMessage());
        }
        return false;
    }

    public Connection getConnection(boolean mainorder)
            throws SQLException {
        try {
            Class.forName(DRIVER_CLASS_NAME);
        } catch (ClassNotFoundException ex) {
            System.err.println(ex.getMessage());
        }
        DriverManager.setLoginTimeout(30000);
        //Connection connection=null;
        if (mainorder == true) {
            try {
                connection = DriverManager.getConnection(MainServer_URL,
                        USERNAME, PASSWORD);
                return (connection);
            } catch (Exception ex) {
                connection = DriverManager.getConnection(SubServer_URL,
                        USERNAME, PASSWORD);
                return (connection);
            }
        } else {
            try {
                connection = DriverManager.getConnection(SubServer_URL,
                        USERNAME, PASSWORD);
                return (connection);
            } catch (Exception ex) {
                connection = DriverManager.getConnection(MainServer_URL,
                        USERNAME, PASSWORD);
                return (connection);
            }
        }
    }

    public Connection getLocalConnection(boolean mainorder)
            throws SQLException {
        try {
            Class.forName(DRIVER_CLASS_NAME);
        } catch (ClassNotFoundException ex) {
            System.err.println(ex.getMessage());
        }
        DriverManager.setLoginTimeout(3000);
        //Connection connection=null;
        if (mainorder == true) {
            try {
                connection = DriverManager.getConnection("jdbc:mysql://localhost/carpark?autoReconnect=true&serverTimezone=UTC&useSSL=False&allowPublicKeyRetrieval=true",
                        USERNAME, PASSWORD);
                return (connection);
            } catch (Exception ex) {
                connection = DriverManager.getConnection("jdbc:mysql://localhost/carpark?autoReconnect=true&serverTimezone=UTC&useSSL=False&allowPublicKeyRetrieval=true",
                        USERNAME, PASSWORD);
                return (connection);
            }
        } else {
            try {
                connection = DriverManager.getConnection("jdbc:mysql://localhost/",
                        USERNAME, PASSWORD);
                return (connection);
            } catch (Exception ex) {
                connection = DriverManager.getConnection("jdbc:mysql://localhost/",
                        USERNAME, PASSWORD);
                return (connection);
            }
        }
    }

    public Connection getRemoteConnection(String address)
            throws SQLException {
        try {
            Class.forName(DRIVER_CLASS_NAME);
        } catch (ClassNotFoundException ex) {
            System.err.println(ex.getMessage());
        }
        DriverManager.setLoginTimeout(3000);
        //Connection connection=null;
        try {
            Connection newConnection = DriverManager.getConnection("jdbc:mysql://" + address + "/carpark?autoReconnect=true&serverTimezone=UTC&useSSL=False&allowPublicKeyRetrieval=true",
                    USERNAME, PASSWORD);
            return (newConnection);
        } catch (Exception ex) {
            System.err.println(address + " Cannot Connect");
            Connection newConnection = DriverManager.getConnection("jdbc:mysql://" + address + "/carpark?autoReconnect=true&serverTimezone=UTC&useSSL=False&allowPublicKeyRetrieval=true",
                    USERNAME, PASSWORD);
            return (newConnection);
        }
    }

    public Connection getBACKUPConnection()
            throws SQLException {
        try {
            Class.forName(DRIVER_CLASS_NAME);
        } catch (ClassNotFoundException ex) {
            System.err.println(ex.getMessage());

        }
        DriverManager.setLoginTimeout(3);

        try {
            backupConnection = DriverManager.getConnection(BackupMainServer_URL,
                    USERNAME, PASSWORD);
            return (backupConnection);
        } catch (Exception ex) {
            backupConnection = DriverManager.getConnection(BackupSubServer_URL,
                    USERNAME, PASSWORD);
            return (backupConnection);
        }

    }

    public Connection getConnection1(boolean order)
            throws SQLException, Exception {
        mainorder = order;

        prewait pw = new prewait();
        Thread pt = new Thread(pw);
        pt.start();
        while (true) {
            if (timeoutnow == true) {
                pt.stop();
                return (connection);
            }
        }
    }

    public String getTimeIN() {
        return dateTimeIN;
    }

    public String getDateTimePaid() {
        return dateTimePaid;
    }

    public String getTimeINStamp() {
        return dateTimeINStamp;
    }

    public String getDateTimePaidStamp() {
        return dateTimePaidStamp;
    }

    public boolean writeManualEntrance(String CardNumber, String trtype, String DateIN, long DateInStamp, boolean isLost) {
        try {
            if (CardNumber.length() > 8) {
                CardNumber = CardNumber.substring(0, 8);
            }
            connection = getConnection(true);
            st = (Statement) connection.createStatement();
            String isLoststr;
            if (isLost) {
                isLoststr = "1";
            } else {
                isLoststr = "0";
            }
            //New LOST Input
            st.execute("INSERT INTO crdplt.main (areaID, entranceID, cardNumber, plateNumber, trtype, isLost, datetimeIN, datetimeINStamp) "
                    + "VALUES ('P1', 'EN01', '" + CardNumber + "', '' , '" + trtype + "', " + isLost + ", '" + DateIN + "','" + DateInStamp + "')");
            st.close();
            connection.close();
            return true;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return false;
        }
    }

    public boolean getCarServed(String loginID) {
        try {
            connection = getConnection(true);
            st = (Statement) connection.createStatement();

            st.execute("SELECT carServed FROM colltrain.main WHERE logINID = '" + loginID + "'");

            st.close();
            connection.close();
            return true;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return false;
        }
    }

    public boolean setCarServed(String loginID, String carServed) {
        try {
            connection = getConnection(true);
            st = (Statement) connection.createStatement();

            st.execute("UPDATE colltrain.main SET carServed = '" + carServed + "' WHERE logINID = '" + loginID + "'");

            st.close();
            connection.close();
            return true;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return false;
        }
    }

    public boolean setCarServed(String trtype, String loginID, String carServed, String totalAmount, String extendedCount, String extendedAmount, String overnightCount, String overnightAmount) {
        try {
            connection = getConnection(true);
            st = (Statement) connection.createStatement();

            st.execute("UPDATE colltrain.main SET carServed = '" + carServed + "', totalAmount = '" + totalAmount + "', extendedCount = '" + extendedCount + "', extendedAmount = '" + extendedAmount + "', overnightCount = '" + overnightCount + "', overnightAmount = '" + overnightAmount + "' WHERE logINID = '" + loginID + "'");

            st.close();
            connection.close();
            return true;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return false;
        }
    }

    public String[] findXReadings(String date2check) {
        String data[] = null;
        try {
            connection = getConnection(true);
            String SQL = "";
            SQL = "SELECT * FROM colltrain.main WHERE DATE(logoutStamp) = '" + date2check + "' ORDER BY logoutStamp  DESC";
            ResultSet rs = selectDatabyFields(SQL);

            List<String> dataList2Show = new ArrayList<String>();
            int rowcount = 0;
            if (rs.last()) {
                rowcount = rs.getRow();
                rs.beforeFirst(); // not rs.first() because the rs.next() below will move on, missing the first element
            }
            while (rs.next()) {
                dataList2Show.add(rs.getString("logoutStamp"));
                //name = rs.getString("DateTimeOUT");
            }

            String[] d = new String[rowcount];
            dataList2Show.toArray(d);
            data = d;
            connection.close();
            return data;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return null;
        }
    }

    public String[] findReceipts(String plate2check) {
        String data[] = null;
        try {
            connection = getConnection(true);
            String SQL = "";
            if (plate2check.compareToIgnoreCase("*") == 0) {
                SQL = "SELECT * FROM carpark.exit_trans ORDER BY DateTimeOUT DESC";
            } else {
                SQL = "SELECT * FROM carpark.exit_trans WHERE PlateNumber = '" + plate2check + "' ORDER BY DateTimeOUT DESC";
            }
            ResultSet rs = selectDatabyFields(SQL);

            List<String> dataList2Show = new ArrayList<String>();
            int rowcount = 0;
            if (rs.last()) {
                rowcount = rs.getRow();
                rs.beforeFirst(); // not rs.first() because the rs.next() below will move on, missing the first element
            }
            while (rs.next()) {
                dataList2Show.add(rs.getString("DateTimeOUT"));
                //name = rs.getString("DateTimeOUT");
            }

            String[] d = new String[rowcount];
            dataList2Show.toArray(d);
            data = d;
            connection.close();
            return data;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return null;
        }
    }

    public String[] findReceiptsByRNos(String receipt2check) {
        String data[] = null;
        try {
            connection = getConnection(true);
            String SQL = "";
            if (receipt2check.compareToIgnoreCase("*") == 0) {
                SQL = "SELECT * FROM carpark.exit_trans ORDER BY DateTimeOUT DESC";
                //SQL = "SELECT * FROM unidb.incomereport WHERE TRno LIKE '%" + plate2check + "' ORDER BY TimeOut DESC";
            } else {
                SQL = "SELECT * FROM carpark.exit_trans WHERE ReceiptNumber LIKE '%" + receipt2check + "' ORDER BY DateTimeOUT DESC";
                //SQL = "SELECT * FROM unidb.incomereport WHERE TRno LIKE '%" + plate2check + "' ORDER BY TimeOut DESC";
            }
            ResultSet rs = selectDatabyFields(SQL);

            List<String> dataList2Show = new ArrayList<String>();
            int rowcount = 0;
            if (rs.last()) {
                rowcount = rs.getRow();
                rs.beforeFirst(); // not rs.first() because the rs.next() below will move on, missing the first element
            }
            while (rs.next()) {
                dataList2Show.add(rs.getString("ReceiptNumber"));
                //name = rs.getString("DateTimeOUT");
            }

            String[] d = new String[rowcount];
            dataList2Show.toArray(d);
            data = d;
            connection.close();
            return data;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return null;
        }
    }

    public String[] findReceiptsByCategory(int categ, String data2Check) {
        String data[] = null;
        try {
            connection = getConnection(true);
            String SQL = "";
            if (data2Check.compareToIgnoreCase("*") == 0) {
                SQL = "SELECT * FROM carpark.exit_trans ORDER BY DateTimeOUT DESC";
            } else {
                switch (categ) {
                    case 0:
                        SQL = "SELECT * FROM carpark.exit_trans WHERE VOID = 0 AND CardNumber = '" + data2Check + "' ORDER BY DateTimeOUT DESC";
                        break;
                    case 1:
                        SQL = "SELECT * FROM carpark.exit_trans WHERE VOID = 0 AND PlateNumber = '" + data2Check + "' ORDER BY DateTimeOUT DESC";
                        break;
                    case 2:
                        SQL = "SELECT * FROM carpark.exit_trans WHERE VOID = 0 AND ReceiptNumber LIKE '%" + data2Check + "' ORDER BY DateTimeOUT DESC";
                        break;
                    default:
                        break;
                }

            }
            ResultSet rs = selectDatabyFields(SQL);

            List<String> dataList2Show = new ArrayList<String>();
            int rowcount = 0;
            if (rs.last()) {
                rowcount = rs.getRow();
                rs.beforeFirst(); // not rs.first() because the rs.next() below will move on, missing the first element
            }
            int counter = 0;

            while (rs.next()) {
                //ACTS AS HEADER
                if (counter == 0) {
                    switch (categ) {
                        case 0:
                            dataList2Show.add(rs.getString("CardNumber"));
                            break;
                        case 1:
                            dataList2Show.add(rs.getString("PlateNumber"));
                            break;
                        case 2:
                            dataList2Show.add(rs.getString("ReceiptNumber"));
                            break;
                        default:
                            break;
                    }
                }

                //SHOW THE EXIT TRANSACTION DATA HERE
                counter++;
                switch (categ) {
                    case 2:
                        dataList2Show.add(rs.getString("DateTimeOUT"));
                        break;
                    default:
                        //dataList2Show.add(counter + ") " + rs.getString("DateTimeOUT"));
                        dataList2Show.add(rs.getString("DateTimeOUT"));
                        break;
                }
                //name = rs.getString("DateTimeOUT");
            }

            String[] d = new String[rowcount + 1]; // Plus 1 for header
            dataList2Show.toArray(d);
            data = d;
            connection.close();
            return data;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return null;
        }
    }

    public ResultSet getReceipt4ReprintByCategory(int categ, String plate2check, String date2check) {
        ResultSet rs;
        String SQL = "";
        if (plate2check.compareToIgnoreCase("*") == 0) {
            SQL = "SELECT * FROM carpark.exit_trans AS x INNER JOIN pos_users.main AS p ON x.CashierName = p.usercode WHERE x.DateTimeOUT = '" + date2check + "'";
        } else {
            switch (categ) {
                case 0:
                    SQL = "SELECT * FROM carpark.exit_trans AS x INNER JOIN pos_users.main AS p ON x.CashierName = p.usercode WHERE x.void = 0 AND x.CardNumber = '" + plate2check + "' AND x.DateTimeOUT = '" + date2check + "'";
                    break;
                case 1:
                    SQL = "SELECT * FROM carpark.exit_trans AS x INNER JOIN pos_users.main AS p ON x.CashierName = p.usercode WHERE x.void = 0 AND x.PlateNumber = '" + plate2check + "' AND x.DateTimeOUT = '" + date2check + "'";
                    break;
                case 2:
                    SQL = "SELECT * FROM carpark.exit_trans AS x INNER JOIN pos_users.main AS p ON x.CashierName = p.usercode WHERE x.void = 0 AND x.ReceiptNumber = '" + plate2check + "' AND x.DateTimeOUT = '" + date2check + "'";
                    break;
                default:
                    break;
            }
        }
        try {
            rs = selectDatabyFields(SQL);
            return rs;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return null;
        }
    }

    public ResultSet getReceipt4Reprint(String plate2check, String receipt2check) {
        ResultSet rs;
        String SQL;
        if (plate2check.compareToIgnoreCase("*") == 0) {
            SQL = "SELECT * FROM carpark.exit_trans AS x INNER JOIN pos_users.main AS p ON x.CashierName = p.usercode WHERE x.ReceiptNumber = '" + receipt2check + "'";
        } else {
            SQL = "SELECT * FROM carpark.exit_trans AS x INNER JOIN pos_users.main AS p ON x.CashierName = p.usercode WHERE x.ReceiptNumber = '" + receipt2check + "'";
        }
        //SQL = "SELECT * FROM unidb.incomereport WHERE TRno = '" + date2check + "'";

        try {
            rs = selectDatabyFields(SQL);
            return rs;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return null;
        }
    }

    public String getRandomCard() {
        String card2Exit = "";
        try {
            connection = getConnection(true);
            ResultSet rs = selectDatabyFields("SELECT cardNumber FROM crdplt.main");

            if (rs.next()) {
                card2Exit = rs.getString("cardNumber");
            }

            st.close();
            connection.close();
            return card2Exit;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return card2Exit;
    }

    public boolean saveZReadLogOut(String loginID, String Exitpoint, String endingReceiptNos, String endingGrandTotal, String endingGrandGrossTotal, String lastTransaction, String logcode, String totalAmount, String grossAmount, String vatSale, String vat12Sale, String vatExemptedSales, String discounts, String voids) {
        try {
            connection = getConnection(true);
            st = (Statement) connection.createStatement();

            st.execute("UPDATE zread.main SET endOR = '" + endingReceiptNos + "', endTrans = '" + lastTransaction + "', newGrand = '" + endingGrandTotal + "', newGrossTotal = '" + endingGrandGrossTotal + "', todaysGross = '" + grossAmount + "', datetimeOut = CURRENT_TIMESTAMP, voids =" + voids + ", todaysale =" + totalAmount + ", discounts =" + discounts + ",  vatablesale =" + vatSale + ", 12vat =" + vat12Sale + ", vatExemptedSales =" + vatExemptedSales + " WHERE logINID = '" + loginID + "'");

            st.close();
            connection.close();
            return true;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return false;
        }
    }

    public boolean saveZReadLogOut(String loginID, String Exitpoint, String beginningReceiptNos, String endingReceiptNos, String endingGrandTotal, String endingGrandGrossTotal, String lastTransaction, String logcode, String totalAmount, String grossAmount, String vatSale, String vat12Sale, String vatExemptedSales, String discounts, String voids) {
        try {
            connection = getConnection(true);
            st = (Statement) connection.createStatement();

            st.execute("UPDATE zread.main SET beginOR = '" + beginningReceiptNos + "', endOR = '" + endingReceiptNos + "', endTrans = '" + lastTransaction + "', newGrand = '" + endingGrandTotal + "', newGrossTotal = '" + endingGrandGrossTotal + "', todaysGross = '" + grossAmount + "', datetimeOut = CURRENT_TIMESTAMP, voids =" + voids + ", todaysale =" + totalAmount + ", discounts =" + discounts + ",  vatablesale =" + vatSale + ", 12vat =" + vat12Sale + ", vatExemptedSales =" + vatExemptedSales + " WHERE logINID = '" + loginID + "'");

            st.close();
            connection.close();
            return true;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return false;
        }
    }

    public String getLastTransaction(String EX_SentinelID) {
        String transactionID = "";
        try {
            connection = getConnection(true);
            ResultSet rs = selectDatabyFields("SELECT LPAD(MAX(transactionID),16,0) AS transactionID FROM logs.audit WHERE sentinelID = '" + EX_SentinelID + "'");

            while (rs.next()) {
                transactionID = rs.getString("transactionID");
            }

            st.close();
            connection.close();
            return transactionID;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return transactionID;
    }

    public void updateZReadLastDate(String sentinel) {
        try {
            connection = getConnection(true);
            st = (Statement) connection.createStatement();

            st.execute("UPDATE zread.lastdate SET sentinelID = '" + sentinel + "', date = CURRENT_TIMESTAMP WHERE sentinelID = '" + sentinel + "'");

            st.close();
            connection.close();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

    public java.util.Date getZReadLastDate(String sentinel) {
        java.util.Date zreadLastDate = null;

        try {
            connection = getConnection(true);
            ResultSet rs = selectDatabyFields("SELECT date FROM zread.lastdate WHERE sentinelID = '" + sentinel + "'");

            if (rs.next()) {
                zreadLastDate = new Date(rs.getDate("date").getTime());
            }

            st.close();
            connection.close();
            return zreadLastDate;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return zreadLastDate;
    }

    public boolean wasReceiptGenerated(String logID, String endingReceiptNos) {
        try {
            Integer bOR = 0;
            Integer eRN = Integer.parseInt(endingReceiptNos);
            connection = getConnection(true);
            ResultSet rs = selectDatabyFields("SELECT beginOR FROM zread.main WHERE logINID = '" + logID + "'");

            if (rs.next()) {
                bOR = rs.getInt("beginOR");
            }
            st.close();
            connection.close();
            if (eRN >= bOR) {
                return true;
            } else {
                return false;
            }

        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return false;
    }

    public boolean isZreadActive(String sentinel, String lastZread) {
        boolean zreadActive = false;

        try {
            connection = getConnection(true);
            //SELECT CURDATE(), DATE(date), IF(CURDATE()>DATE(date), true, false) as active FROM zread.lastdate
            ResultSet rs = selectDatabyFields("SELECT CURDATE(), DATE(date), IF(CURDATE()>DATE(date), true, false) as active FROM zread.lastdate");

            if (rs.next()) {
                zreadActive = rs.getBoolean("active");
            }

            st.close();
            connection.close();
            return zreadActive;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return zreadActive;
    }

    public String getVoidPkid() {
        String data = "";
        try {
            //connection = getConnection(true);
            ResultSet rs = selectDatabyFields("SELECT LPAD(MAX(pkid),12,0) AS voidid FROM carpark.void_trans");
            // iterate through the java resultset
            while (rs.next()) {
                Integer count = rs.getInt("voidid");
                if (count == 0) {
                    data = "000000000001";
                } else {
                    count++;
                    data = formatNos(count.toString());

                }

            }
            //st.close();
            //connection.close();
            return data;
        } catch (Exception ex) {
            System.err.println("Still Empty in Void Table" + ex.getMessage());
        }
        return data;
    }

    public String formatNos(String newReceipt) {
        int stoploop = 12 - newReceipt.length();
        int i = 0;
        do {
            newReceipt = "0" + newReceipt;
            i++;
        } while (i != stoploop);

        return newReceipt;
    }

    public String formatSpaces(String newString) {
        int totalCharacters = 24;
        int stoploop = totalCharacters - newString.length();
        int i = 0;
        do {
            newString = newString + " ";
            i++;
        } while (i != stoploop);

        return newString;
    }

    class prewait extends Thread {

        Thread Tc1;
        Thread Tc2;
        connection1Thread Tconnect1 = new connection1Thread();
        connection2Thread Tconnect2 = new connection2Thread();
        public int count = 0;

        @Override
        public void run() {
            if (mainorder == true) {
                Tc1 = new Thread(Tconnect1);
                Tc2 = new Thread(Tconnect2);
            } else {
                Tc2 = new Thread(Tconnect1);
                Tc1 = new Thread(Tconnect2);
            }
            Tc1.start();
            try {
                while (count < 2) {
                    count++;
                    Thread.sleep(3000);
                }
                if (count == 2) {
                    Tc2.start();
                    count++;
                    Tc1.stop();
                    Thread.sleep(3000);
                }
                if (count == 3) {
                    Tc2.stop();
                    timeoutnow = true;
                }
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
                System.err.println(ex.getMessage());
            }
        }
    }

    class connection1Thread extends Thread {

        @Override
        public void run() {
            try {
                Class.forName(DRIVER_CLASS_NAME);
            } catch (ClassNotFoundException ex) {
                System.err.println(ex.getMessage());
            }
            DriverManager.setLoginTimeout(3);
            try {
                //System.out.println("connecting to Mainserver..");
                connection = DriverManager.getConnection(MainServer_URL, USERNAME, PASSWORD);
                if (connection != null) {//System.out.println(connection + "connected to Mainserver..");
                    timeoutnow = true;
                }
                Thread.sleep(1000);
            } catch (Exception ex) {
                try {
                    connection = DriverManager.getConnection(SubServer_URL, USERNAME, PASSWORD);
                    if (connection != null) {
                        timeoutnow = true;
                    }
                } catch (SQLException ex1) {
                    System.err.println(ex.getMessage());
                }
            }

        }
    }

    class connection2Thread extends Thread {

        @Override
        public void run() {
            try {
                Class.forName(DRIVER_CLASS_NAME);
            } catch (ClassNotFoundException ex) {
                System.err.println(ex.getMessage());
            }
            DriverManager.setLoginTimeout(3);
            try {
                //System.out.println("connecting to Subserver..");
                connection = DriverManager.getConnection(SubServer_URL, USERNAME, PASSWORD);
                if (connection != null) {//System.out.println(connection + "connected to subserver..");
                    timeoutnow = true;
                }
                Thread.sleep(1000);
            } catch (Exception ex) {
                try {
                    connection = DriverManager.getConnection(MainServer_URL, USERNAME, PASSWORD);
                    if (connection != null) {
                        timeoutnow = true;
                    }
                } catch (SQLException ex1) {
                    System.err.println(ex.getMessage());
                }

            }

        }
    }

    public boolean saveLogin(String logID, String userCode, String logname, String SentinelID) throws SQLException {
        try {
            connection = getConnection(true);
            st = (Statement) connection.createStatement();
            st.execute("INSERT INTO colltrain.main (logINID, userCode, userName, SentinelID, loginStamp) VALUES ('" + logID + "', '" + userCode + "', '" + logname + "', '" + SentinelID + "', CURRENT_TIMESTAMP)");

            st.close();
            connection.close();
            return true;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return false;
        }
    }

    public String getPtypeName(String ptype) {
        String name = "";
        try {
            connection = getConnection(true);
            ResultSet rs = selectDatabyFields("SELECT ptypename FROM parkertypes.main WHERE parkertype = '" + ptype + "'");

            while (rs.next()) {
                name = rs.getString("ptypename");
            }

            st.close();
            connection.close();
            return name;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return name;
    }

    public int getDupReceipt(String ptype) {
        int dup = 0;
        try {
            connection = getConnection(true);
            ResultSet rs = selectDatabyFields("SELECT numOfReceipts FROM parkertypes.main WHERE parkertype = '" + ptype + "'");

            while (rs.next()) {
                dup = rs.getInt("numOfReceipts");
            }

            st.close();
            connection.close();
            return dup;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return dup;
    }

    public int getDiscounted(String ptype) {
        int dup = 0;
        try {
            connection = getConnection(true);
            ResultSet rs = selectDatabyFields("SELECT Discounted FROM parkertypes.main WHERE parkertype = '" + ptype + "'");

            while (rs.next()) {
                dup = rs.getInt("Discounted");
            }

            st.close();
            connection.close();
            return dup;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return dup;
    }

    public float getdiscountPercentage(String ptype) {
        float dsc = 0;
        try {
            connection = getConnection(true);
            ResultSet rs = selectDatabyFields("SELECT DiscountPercentage FROM parkertypes.main WHERE parkertype = '" + ptype + "'");

            while (rs.next()) {
                dsc = rs.getFloat("DiscountPercentage");
            }

            st.close();
            connection.close();
            return dsc;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return dsc;
    }

    public String getPtypecount(String parkerName, String logCode) throws SQLException {
        String data = "";
        try {
            connection = getConnection(true);
            ResultSet rs = selectDatabyFields("SELECT " + parkerName.toLowerCase().trim() + "Count FROM colltrain.main WHERE logINID = '" + logCode + "'");
            // iterate through the java resultset
            while (rs.next()) {
                String count = rs.getString(parkerName.toLowerCase().trim() + "Count");
                data = count;
            }
            st.close();
            connection.close();
            return data;
        } catch (Exception ex) {
            System.err.println("getPtypecount from colltrain.main" + ex.getMessage());
        }
        return data;
    }

    public boolean updateRecord(String fieldName, String value, String logCode) {
        try {
            connection = getConnection(true);
            st = (Statement) connection.createStatement();

            st.execute("UPDATE colltrain.main SET " + fieldName + " = '" + value + "' WHERE logINID = '" + logCode + "'");

            st.close();
            connection.close();
            return true;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return false;
        }
    }

    public boolean updateTimeRecord(String fieldName, String value, String logCode) {
        try {
            connection = getConnection(true);
            st = (Statement) connection.createStatement();

            st.execute("UPDATE colltrain.main SET " + fieldName + " = " + value + " WHERE logINID = '" + logCode + "'");

            st.close();
            connection.close();
            return true;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return false;
        }
    }

    public String getImptCount(String fieldName, String logCode) throws SQLException {
        String data = "";
        try {
            connection = getConnection(true);
            ResultSet rs = selectDatabyFields("SELECT " + fieldName + " FROM colltrain.main WHERE logINID = '" + logCode + "'");
            // iterate through the java resultset
            while (rs.next()) {
                String count = rs.getString(fieldName);
                data = count;
            }
            st.close();
            connection.close();
            return data;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return data;
    }

    public Float getImptAmount(String fieldName, String logCode) throws SQLException {
        float data = 0;
        try {
            connection = getConnection(true);
            ResultSet rs = selectDatabyFields("SELECT " + fieldName + " FROM colltrain.main WHERE logINID = '" + logCode + "'");
            // iterate through the java resultset
            while (rs.next()) {
                float count = rs.getFloat(fieldName);
                data = count;
            }
            st.close();
            connection.close();
            return data;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return data;
    }

    public double getPtypeAmount(String parkerName, String logCode) throws SQLException {
        double data = 0;
        try {
            connection = getConnection(true);
            ResultSet rs = selectDatabyFields("SELECT " + parkerName.toLowerCase().trim() + "Amount FROM colltrain.main WHERE logINID = '" + logCode + "'");
            // iterate through the java resultset
            while (rs.next()) {
                double count = rs.getDouble(parkerName.toLowerCase().trim() + "Amount");
                data = count;
            }
            st.close();
            connection.close();
            return data;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return data;
    }

    public boolean setImptCount(String fieldName, String logCode, int newCount) throws SQLException {
        try {
            connection = getConnection(true);
            st = (Statement) connection.createStatement();

            st.execute("UPDATE colltrain.main SET " + fieldName + " = '" + newCount + "' WHERE logINID = '" + logCode + "'");

            st.close();
            connection.close();
            return true;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return false;
        }
    }

    public boolean setPtypecount(String parkerName, String logCode, int newCount) throws SQLException {
        try {
            connection = getConnection(true);
            st = (Statement) connection.createStatement();

            st.execute("UPDATE colltrain.main SET " + parkerName.toLowerCase().trim() + "Count = '" + newCount + "' WHERE logINID = '" + logCode + "'");

            st.close();
            connection.close();
            return true;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return false;
        }
    }

    public boolean setImptAmount(String fieldName, String logCode, double newAmount) throws SQLException {
        try {
            connection = getConnection(true);
            st = (Statement) connection.createStatement();

            st.execute("UPDATE colltrain.main SET " + fieldName + " = '" + newAmount + "' WHERE logINID = '" + logCode + "'");

            st.close();
            connection.close();
            return true;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return false;
        }
    }

    public boolean setPtypeAmount(String parkerName, String logCode, double newAmount) throws SQLException {
        try {
            connection = getConnection(true);
            st = (Statement) connection.createStatement();

            st.execute("UPDATE colltrain.main SET " + parkerName.toLowerCase().trim() + "Amount = '" + newAmount + "' WHERE logINID = '" + logCode + "'");

            st.close();
            connection.close();
            return true;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return false;
        }
    }

    public ResultSet getSummaryCollbyLogCode(String logCode) {
        ResultSet rs = null;
        try {
            connection = getConnection(true);
            rs = selectDatabyFields("SELECT * FROM colltrain.main WHERE logINID = '" + logCode + "'");
            // iterate through the java resultset
            //while (rs.next()) {
            //    String r = rs.getString("retailCount");
            //data = r;
            //}
            st.close();
            connection.close();
            return rs;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return rs;
    }

    public ResultSet getSummaryCollbydateColl(String dateColl) {
        ResultSet rs = null;
        try {
            connection = getConnection(true);
            String sql = "SELECT * FROM colltrain.main WHERE logoutStamp = '" + dateColl + "'";
            rs = selectDatabyFields(sql);
            // iterate through the java resultset
            //while (rs.next()) {
            //    String r = rs.getString("retailCount");
            //data = r;
            //}

            //connection.close();
            return rs;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return rs;
    }

    public ResultSet getSummaryCollbyLoginID(String LoginID) {
        ResultSet rs = null;
        try {
            connection = getConnection(true);
            String sql = "SELECT * FROM colltrain.main WHERE logINID = '" + LoginID + "'";
            rs = selectDatabyFields(sql);
            // iterate through the java resultset
            //while (rs.next()) {
            //    String r = rs.getString("retailCount");
            //data = r;
            //}

            //connection.close();
            return rs;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return rs;
    }

    public ResultSet getCGHIncomeSummaryCollbydateColl(String teller, String dateColl) {
        ResultSet rs = null;
        try {
            connection = getConnection(true);
            String sql = "SELECT * FROM unidb.incomereport WHERE Operator = '" + teller + "' AND BusnessDate = DATE('" + dateColl + "')";
            rs = selectDatabyFields(sql);
            // iterate through the java resultset
            //while (rs.next()) {
            //    String r = rs.getString("retailCount");
            //data = r;
            //}

            //connection.close();
            return rs;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return rs;
    }

    public ResultSet getZReadbylogINID(String logINID) {
        ResultSet rs = null;
        try {
            connection = getConnection(true);
            //SELECT terminalnum, datetimeOut, SUM(todaysale), min(beginOR), max(endOR), min(beginTrans), MIN(endTrans), MIN(oldGrand), MAX(newGrand) FROM `main` dataList2Show date(datetimeOut) = "2018-09-14"
            String sql = "SELECT terminalnum, DATE(datetimeOut) AS datetimeOut, CAST(SUM(todaysale) AS decimal(20,2)) AS TODAYSALE, CAST(SUM(todaysGross) AS decimal(20,2)) AS TODAYSGROSS, "
                    + "CAST(SUM(vatablesale) AS decimal(20,2)) AS VATABLESALE, CAST(SUM(12VAT) AS decimal(20,2)) AS VAT12, CAST(SUM(vatExemptedSales) AS decimal(20,2)) AS vatExemptedSales, "
                    + "CAST(SUM(discounts) AS decimal(20,2)) AS DISCOUNTS, CAST(SUM(voids) AS decimal(20,2)) AS VOIDS, "
                    + "LPAD(MIN(beginOR),12,0) AS BEGINOR, LPAD(MAX(endOR),12,0) AS ENDOR, "
                    + "LPAD(MIN(beginTrans),20,0) AS beginTrans, LPAD(MAX(endTrans),20,0) AS endTrans, "
                    + "CAST(MIN(oldGrand) AS decimal(11,2)) AS oldGrand, CAST(MAX(newGrand) AS decimal(11,2)) AS newGrand,  "
                    + "CAST(MIN(oldGrossTotal) AS decimal(11,2)) AS oldGrossTotal, CAST(MAX(newGrossTotal) AS decimal(11,2)) AS newGrossTotal,  "
                    + "zCount, MAX(zCount) AS endZCount FROM zread.main "
                    + "where logINID = '" + logINID + "'";
            rs = selectDatabyFields(sql);
            // iterate through the java resultset
            //while (rs.next()) {
            //    String r = rs.getString("retailCount");
            //data = r;
            //}

            //connection.close();
            return rs;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return rs;
    }

    public ResultSet getZReadbydateColl(String dateColl) {
        ResultSet rs = null;
        try {
            connection = getConnection(true);
            //SELECT terminalnum, datetimeOut, SUM(todaysale), min(beginOR), max(endOR), min(beginTrans), MIN(endTrans), MIN(oldGrand), MAX(newGrand) FROM `main` dataList2Show date(datetimeOut) = "2018-09-14"
            String sql = "SELECT terminalnum, datetimeOut, CAST(SUM(todaysale) AS decimal(20,2)) AS TODAYSALE, CAST(SUM(todaysGross) AS decimal(20,2)) AS TODAYSGROSS, "
                    + "CAST(SUM(vatablesale) AS decimal(20,2)) AS VATABLESALE, CAST(SUM(12VAT) AS decimal(20,2)) AS VAT12, CAST(SUM(vatExemptedSales) AS decimal(20,2)) AS vatExemptedSales, "
                    + "CAST(SUM(discounts) AS decimal(20,2)) AS DISCOUNTS, CAST(SUM(voids) AS decimal(20,2)) AS VOIDS, "
                    + "LPAD(MIN(beginOR),12,0) AS BEGINOR, LPAD(MAX(endOR),12,0) AS ENDOR, "
                    + "LPAD(MIN(beginTrans),16,0) AS beginTrans, LPAD(MAX(endTrans),16,0) AS endTrans, "
                    + "CAST(MIN(oldGrossTotal) AS decimal(11,2)) AS oldGrossTotal, CAST(MAX(newGrossTotal) AS decimal(11,2)) AS newGrossTotal,  "
                    + "CAST(MIN(oldGrand) AS decimal(11,2)) AS oldGrand, CAST(MAX(newGrand) AS decimal(11,2)) AS newGrand, "
                    + "zCount, MAX(zCount) AS endZCount FROM zread.main "
                    + "where date(datetimeOut) = '" + dateColl + "'";
            rs = selectDatabyFields(sql);
            // iterate through the java resultset
            //while (rs.next()) {
            //    String r = rs.getString("retailCount");
            //data = r;
            //}

            //connection.close();
            return rs;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return rs;
    }

    public ResultSet getTodaysZReadbydateColl(Float totalCollected, Double Sale12Vat, Double vatSale) {
        ResultSet rs = null;
        try {
            connection = getConnection(true);
            //SELECT terminalnum, datetimeOut, SUM(todaysale), min(beginOR), max(endOR), min(beginTrans), MIN(endTrans), MIN(oldGrand), MAX(newGrand) FROM `main` dataList2Show date(datetimeOut) = "2018-09-14"
            String sql = "SELECT terminalnum, CURRENT_TIMESTAMP, CAST(SUM(todaysale) AS decimal(20,2)) AS TODAYSALE, CAST(SUM(todaysGross) AS decimal(20,2)) AS TODAYSGROSS, "
                    + "CAST(SUM(vatablesale) AS decimal(20,2)) AS VATABLESALE, CAST(SUM(12VAT) AS decimal(20,2)) AS VAT12, CAST(SUM(vatExemptedSales) AS decimal(20,2)) AS vatExemptedSales, "
                    + "CAST(SUM(discounts) AS decimal(20,2)) AS DISCOUNTS, CAST(SUM(voids) AS decimal(20,2)) AS VOIDS, "
                    + "LPAD(MIN(beginOR),12,0) AS BEGINOR, LPAD(MAX(endOR),12,0) AS ENDOR, LPAD(MIN(beginTrans),16,0) AS beginTrans, LPAD(MAX(endTrans),16,0) AS endTrans, "
                    + "CAST(MIN(oldGrossTotal) AS decimal(11,2)) AS oldGrossTotal, CAST(MAX(newGrossTotal) AS decimal(11,2)) AS newGrossTotal,  "
                    + "CAST(MIN(oldGrand) AS decimal(11,2)) AS oldGrand, CAST(MAX(newGrand) AS decimal(11,2)) AS newGrand, zCount, MAX(zCount) AS endZCount "
                    + "FROM zread.main where date(datetimeOut) = date(CURRENT_TIMESTAMP)";
            rs = selectDatabyFields(sql);
            // iterate through the java resultset
            //while (rs.next()) {
            //    String r = rs.getString("retailCount");
            //data = r;
            //}

            //connection.close();
            return rs;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return rs;
    }

    public ResultSet getTodaysZReadbyloginID(String loginID) {
        ResultSet rs = null;
        try {
            connection = getConnection(true);
            //SELECT terminalnum, datetimeOut, SUM(todaysale), min(beginOR), max(endOR), min(beginTrans), MIN(endTrans), MIN(oldGrand), MAX(newGrand) FROM `main` dataList2Show date(datetimeOut) = "2018-09-14"
            String sql = "SELECT * "
                    + "FROM zread.main where logINID = " + loginID;
            rs = selectDatabyFields(sql);
            // iterate through the java resultset
            //while (rs.next()) {
            //    String r = rs.getString("retailCount");
            //data = r;
            //}

            //connection.close();
            return rs;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return rs;
    }

    public ResultSet getTotalCollectionBydateColl(String datetimeOut) {
        ResultSet rs = null;
        try {
            Map<String, String> parkerTypeNames = new HashMap<String, String>();

            manualOpen();
            ResultSet types = getAllActivePtypes();
            while (types.next()) {
                parkerTypeNames.put(types.getString("ptypename"), types.getString("ptypename"));
            }
            manualClose();
            List<String> ptypesByKey = new ArrayList<>(parkerTypeNames.keySet());
            Collections.sort(ptypesByKey);
            Iterator itr0 = ptypesByKey.iterator();
            Iterator itr1 = ptypesByKey.iterator();

            connection = getConnection(true);
            String sql = "SELECT logoutStamp,"
                    + "SUM(carServed) AS carServed, SUM(totalAmount) AS totalAmount, ";

            while (itr0.hasNext()) {
                String entry = (String) itr0.next();
//                    System.out.println(entry);
                String dataCount = parkerTypeNames.get(entry).toLowerCase().trim() + "Count";
                String dataAmount = parkerTypeNames.get(entry).toLowerCase().trim() + "Amount";
                //      COUNT
//                        System.out.print(dataCount);
                //      AMOUNT
//                        System.out.print(dataAmount);
                sql = sql + "SUM(" + dataCount + ") as " + dataCount + ", SUM(" + dataAmount + ") as " + dataAmount + ",";

            }
            sql = sql + "SUM(extendedCount) as extendedCount, SUM(extendedAmount) as extendedAmount,"
                    + "SUM(overnightCount) as overnightCount, SUM(overnightAmount) as overnightAmount"
                    + " FROM colltrain.main"
                    + " WHERE DATE(logoutStamp) = DATE('" + datetimeOut + "')";
            rs = selectDatabyFields(sql);

        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(DataBaseHandler.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return rs;
    }

    private String getAmountDue(float AmountDue) {
        if (AmountDue == 0) {
            return "0.00";
        }
        DecimalFormat df2 = new DecimalFormat("#.00");
        return df2.format(AmountDue);

    }

    public ResultSet getTodaysTotalCollectionBydateColl() {
        ResultSet rs = null;
        try {
            Map<String, String> parkerTypeNames = new HashMap<String, String>();

            manualOpen();
            ResultSet types = getAllActivePtypes();
            while (types.next()) {
                parkerTypeNames.put(types.getString("ptypename"), types.getString("ptypename"));
            }
            manualClose();
            List<String> ptypesByKey = new ArrayList<>(parkerTypeNames.keySet());
            Collections.sort(ptypesByKey);
            Iterator itr0 = ptypesByKey.iterator();
            Iterator itr1 = ptypesByKey.iterator();

            connection = getConnection(true);
            String sql = "SELECT logoutStamp,"
                    + "SUM(carServed) AS carServed, SUM(totalAmount) AS totalAmount, ";

            while (itr0.hasNext()) {
                String entry = (String) itr0.next();
//                    System.out.println(entry);
                String dataCount = parkerTypeNames.get(entry).toLowerCase().trim() + "Count";
                String dataAmount = parkerTypeNames.get(entry).toLowerCase().trim() + "Amount";
                //      COUNT
//                        System.out.print(dataCount);
                //      AMOUNT
//                        System.out.print(dataAmount);
                sql = sql + "SUM(" + dataCount + ") as " + dataCount + ", SUM(" + dataAmount + ") as " + dataAmount + ",";

            }
            sql = sql + "SUM(extendedCount) as extendedCount, SUM(extendedAmount) as extendedAmount,"
                    + "SUM(overnightCount) as overnightCount, SUM(overnightAmount) as overnightAmount"
                    + " FROM colltrain.main"
                    + " WHERE DATE(logoutStamp) = CURDATE()";
            rs = selectDatabyFields(sql);

        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(DataBaseHandler.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return rs;
    }

    public int getLoginSeries() throws SQLException {
        int data = 0;
        try {
            connection = getConnection(true);
            ResultSet rs = selectDatabyFields("SELECT DES_DECRYPT(logNum,'Th30r3t1cs') AS logNum FROM logs.main WHERE pkID = 1");
            // iterate through the java resultset
            while (rs.next()) {
                int count = rs.getInt("logNum");
                data = count;
            }
            st.close();
            connection.close();
            return data;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return data;
    }

    public int setLoginSeries(int logNumber) throws SQLException {
        int data = 0;
        try {
            connection = getConnection(true);
            st = (Statement) connection.createStatement();

            st.execute("UPDATE logs.main SET logNum = DES_ENCRYPT('" + logNumber + "','Th30r3t1cs') WHERE pkID = 1");
            st.close();
            connection.close();
            return data;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return data;
    }

    public boolean saveLog(String activityCode, String activityOwner) {
        try {
            connection = getConnection(true);
            st = (Statement) connection.createStatement();
            st.execute("INSERT INTO logs.audit (sentinelID, activityCode, activityOwner, activityDate) VALUES ('" + EX_SentinelID + "', '" + activityCode + "', '" + activityOwner + "', CURRENT_TIMESTAMP)");

            st.close();
            connection.close();
            return true;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return false;
        }
    }

    public boolean saveLog(String activityCode, String activityOwner, String activityDetails) {
        try {
            connection = getConnection(true);
            st = (Statement) connection.createStatement();
            st.execute("INSERT INTO logs.audit (sentinelID, activityCode, activityOwner, activityDate, activityDetails) "
                    + "VALUES ('" + EX_SentinelID + "', '" + activityCode + "', '" + activityOwner + "', CURRENT_TIMESTAMP, '" + activityDetails + "')");

            st.close();
            connection.close();
            return true;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return false;
        }
    }

    public void manualOpen() {
        try {
            connection = getLocalConnection(true);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

    public void manualClose() {
        try {
            st.close();
            connection.close();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

    //----Slots
    public int getSlotAvail(String type) {
        int avail = 0;
        try {
            /*
            connection = getConnection(true);
            ResultSet rs = selectDatabyFields("SELECT * FROM slotsavailable.main WHERE type = '" + type + "'");

            // iterate through the java resultset
            while (rs.next()) {
                avail = rs.getInt("available");

            }
            st.close();
            connection.close();
             */
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return avail;
    }

    public boolean Slotsminus1(String type) {
        try {
            int temp = 0;
            connection = getConnection(true);
            st = (Statement) connection.createStatement();

            connection = getConnection(true);
            ResultSet rs = selectDatabyFields("SELECT * FROM slotsavailable.main WHERE type = '" + type + "'");
            if (rs.next()) {
                temp = rs.getInt("available");
                temp--;
            }

            boolean status = st.execute("UPDATE slotsavailable.main SET available = '" + temp + "' WHERE type = '" + type + "'");

            st.close();
            connection.close();
            return status;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return false;
        }
    }

    public boolean Slotsplus1(String type) {
        try {
            int temp = 0;
            connection = getConnection(true);
            st = (Statement) connection.createStatement();

            connection = getConnection(true);
            ResultSet rs = selectDatabyFields("SELECT * FROM slotsavailable.main WHERE type = '" + type + "'");
            if (rs.next()) {
                temp = rs.getInt("available");
                temp++;
            }

            boolean status = st.execute("UPDATE slotsavailable.main SET available = '" + temp + "' WHERE type = '" + type + "'");

            st.close();
            connection.close();
            return status;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        try {
            DataBaseHandler DBH = new DataBaseHandler();

            //DBH.copyCRDPLTfromServer("server_crdplt.main", "crdplt.main");
            //DBH.copyCRDPLTfromServer("crdplt.main", "server_crdplt.main");    // For Testing ONLY
            //DBH.copyExitTransfromLocal("carpark.exit_trans", "server_carpark.exit_trans");
            //DBH.copyColltrainfromLocal("colltrain.main", "server_colltrain.main");
            //DBH.copyZReadfromLocal("zread.main", "server_zread.main");
            //DBH.getEntranceCard();
//            DBH.insertImageToDB();
            DBH.insertImageFromURLToDB("192.168.100.220", "admin", "admin888888");
            DBH.ShowImageFromDB();

//            String imageUrl = "http://www.avajava.com/images/avajavalogo.jpg";
//            String destinationFile = "C:/avaimage.jpg";
//
//            saveImage(imageUrl, destinationFile);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

    ///////////////////////////Server Copy to POS///////////////////////////
    public List<String> getClientsIP() throws SQLException {
        List<String> ipaddresses = new ArrayList<>();
        connection = getLocalConnection(true);
        st = (Statement) connection.createStatement();
        String sql = "SELECT * FROM forduplication.listofip";
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            ipaddresses.add(rs.getString("ipaddress"));
        }
        st.close();
        connection.close();
        return ipaddresses;
    }

    public List<String> getCards() throws SQLException {
        List<String> ipaddresses = new ArrayList<>();
        connection = getLocalConnection(true);
        st = (Statement) connection.createStatement();
        String sql = "SELECT * FROM crdplt.main";
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            ipaddresses.add(rs.getString("ipaddress"));
        }
        st.close();
        connection.close();
        return ipaddresses;
    }

    public String getLastKnown(String tablename, String ipaddress) throws SQLException {
        String dateOfLastKnown = null;
        connection = getLocalConnection(true);
        st = (Statement) connection.createStatement();
        String sql = "SELECT date FROM " + tablename + ".lastknown WHERE ipaddress = '" + ipaddress + "'";
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            dateOfLastKnown = rs.getString("date");
        }
        st.close();
        connection.close();
        return dateOfLastKnown;
    }

    public boolean setLastKnown(String tablename, String ipaddress, String lastDate) throws SQLException {
//        connection = getLocalConnection(true);
        Statement st = (Statement) connection.createStatement();
        String sql = "UPDATE " + tablename + ".lastknown SET date = '" + lastDate + "' WHERE ipaddress = '" + ipaddress + "'";
        boolean result = st.execute(sql);
        st.close();
//        connection.close();
        return result;
    }

    public ResultSet getLatestCards(String date_lk) throws SQLException {
        ResultSet cardNumbers = null;
        connection = getLocalConnection(true);
        st = (Statement) connection.createStatement();
        String sql = "SELECT cardNumber FROM crdplt.main WHERE datetimeIN >= '" + date_lk + "'";
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            System.out.println(rs.getString("cardNumber"));
        }
        st.close();
        connection.close();
        return cardNumbers;
    }

    public void copyCardsServer2POS(String tablename, String date_lk, String POSaddress, List<String> clients) throws SQLException {
        BufferedImage[] img = new BufferedImage[15];
        connection = getLocalConnection(true);
        //Connection posConnection = getRemoteConnection(POSaddress);
        st = (Statement) connection.createStatement();
        //Statement remoteST = (Statement) posConnection.createStatement();
        String sql = "SELECT * FROM crdplt.main WHERE datetimeIN > '" + date_lk + "' ORDER BY datetimeIN ASC";
        String insertSQL = "";
        ResultSet rs = st.executeQuery(sql);
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnNumber = rsmd.getColumnCount();
        while (rs.next()) {
            int x = 1;
            Connection posConnection = null;
            try {
                posConnection = getRemoteConnection(POSaddress);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (null != posConnection) {
//            boolean res = remoteST.execute(insertSQL);
                insertSQL = "INSERT INTO crdplt.main (";
                while (x <= columnNumber) {
                    insertSQL = insertSQL + rsmd.getColumnName(x);
                    if (x != columnNumber) {
                        insertSQL = insertSQL + ", ";
                    }
                    x++;
                }
                insertSQL = insertSQL + ") VALUES (";
                x = 1;
                while (x <= columnNumber) {
                    if (x != columnNumber) {
                        insertSQL = insertSQL + "?, ";
                    } else {
                        insertSQL = insertSQL + "?)";
                    }
                    x++;
                }
                System.out.println("INSERT SQL: " + insertSQL);
                PreparedStatement remotePST = (PreparedStatement) posConnection.prepareStatement(insertSQL);
                x = 1;
                while (x <= columnNumber) {
                    int type = rsmd.getColumnType(x);
                    if (type == 12) {
                        String cardNumber = rs.getString(rsmd.getColumnName(x));
                        remotePST.setString(x, rs.getString(rsmd.getColumnName(x)));
                        System.out.print(cardNumber + " ");
                    } else if (type == -6) {
                        int cardNo = rs.getInt(rsmd.getColumnName(x));
                        remotePST.setInt(x, rs.getInt(rsmd.getColumnName(x)));
                        System.out.print(cardNo + " ");
                    } else if (type == 93) {
                        Timestamp dt = rs.getTimestamp(rsmd.getColumnName(x));
                        remotePST.setTimestamp(x, rs.getTimestamp(rsmd.getColumnName(x)));
                        System.out.print(dt + " ");
                    } else if (type == -7) {
                        boolean isLost = rs.getBoolean(rsmd.getColumnName(x));
                        remotePST.setBoolean(x, rs.getBoolean(rsmd.getColumnName(x)));
                        System.out.print(isLost + " ");
                    } else if (type == -4) {
                        remotePST.setBlob(x, rs.getBlob(rsmd.getColumnName(x)));
//                    InputStream is = rs.getBinaryStream(rsmd.getColumnName(x));
//                    try {
//                        if (null != is) {
//                            img[x] = ImageIO.read(is);
//                            show(x + "", img[x], x);
//                        }
//                    } catch (Exception ex) {
//                        Logger.getLogger(DataBaseHandler.class.getName()).log(Level.SEVERE, null, ex);
//                    }
                    } else {
                        System.out.print(" type:[" + type + "] = ");
                    }

                    x++;
                }
                try {
                    remotePST.executeUpdate();
                    this.setLastKnown(tablename, POSaddress, rs.getString("datetimeIN"));
                    System.out.println("\n" + rs.getString("cardNumber") + " Copied Successfully");
                } catch (Exception ex) {
                    try {
                        Statement remoteST = (Statement) posConnection.createStatement();
                        ////////////REMOTE DELETE//////////////////////
                        String delSQL = "DELETE FROM crdplt.main WHERE cardNumber = '" + rs.getString("cardNumber") + "'";
                        ////////////REMOTE DELETE//////////////////////
                        remoteST.execute(delSQL);
                        remotePST.executeUpdate();
                        this.setLastKnown(tablename, POSaddress, rs.getString("datetimeIN"));
                        System.out.println("\n" + rs.getString("cardNumber") + " Overwritten Successfully");
                    } catch (Exception ex2) {
                        Statement saveST = (Statement) connection.createStatement();
                        String insSQL = "INSERT INTO " + tablename + ".failed (cardNumber, ipaddress, sentinelID) VALUES ('" + rs.getString("cardNumber") + "', '" + POSaddress + "', '')";
                        saveST.execute(insSQL);
                        saveST.close();
                        ex2.printStackTrace();
                    }
                }
                System.out.println("");
                remotePST.close();
                posConnection.close();
            }
        }

        st.close();
        connection.close();
    }

    public String getFailedCards(String tablename, String ipaddress) throws SQLException {
        String cardNumber = null;
        connection = getLocalConnection(true);
        st = (Statement) connection.createStatement();
        String sql = "SELECT cardNumber FROM " + tablename + ".main WHERE ipaddress = '" + ipaddress + "'";
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            cardNumber = rs.getString("cardNumber");
            System.out.println(cardNumber);
        }
        st.close();
        connection.close();
        return cardNumber;
    }
    
    public HashMap<String, String> getFailedCards(String tableName) throws SQLException {
        HashMap<String, String> ipaddresses = new HashMap<String, String>();
        connection = getLocalConnection(true);
        st = (Statement) connection.createStatement();
        String sql = "SELECT * FROM " + tableName + ".failed";
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            ipaddresses.put(rs.getString("ipaddress"), rs.getString("cardNumber"));
        }
        st.close();
        connection.close();
        return ipaddresses;
    }

}
