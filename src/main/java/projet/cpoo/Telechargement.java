package projet.cpoo;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Observable;

class Telechargement extends Observable implements Runnable {

    private static final int MAX_BUFFER_SIZE = 1024;
    
    public static final int EnCoursTelechargement = 0;
    public static final int Pause = 1;
    public static final int Complet = 2;
    public static final int Annul = 3;
    public static final int Ereur = 4;

    private URL url; //l'url de le fichier a telecharger
    private int taille; // la taille de fichier a telecharger
    private int telecharge; // la taille qu'on a telecharger jusqu'au présent
    private int status; // status de telechargement
    private String dossier = "./"; //l'emplacement de fichier

    public Telechargement(URL url) {

        this.url = url;
        taille = -1;
        telecharge = 0;
        status = EnCoursTelechargement;
        telecharger();
    }

    public String getUrl() {
        return url.toString();
    }

    public int getTaille() {
        return taille;
    }

    public float getProgress() {
        return ((float) telecharge / taille) * 100;
    }

    public int getStatus() {
        return status;
    }

    public String getDossier() {return dossier;}

    public void setDossier(String doss) { dossier=doss; }

    public void pause() {
        status = Pause;
        stateChanged();
    }

    public void resume() {
        status = EnCoursTelechargement;
        stateChanged();
        telecharger();
    }

    public void cancel() {
        status = Annul;
        stateChanged();
    }

    private void ereur() {
        status = Ereur;
        stateChanged();
    }

    private void telecharger() {
        Thread thread = new Thread(this);
        thread.start();
    }

    private String getFileName(URL url) {
        String fileName = url.getFile();
        return fileName.substring(fileName.lastIndexOf('/') + 1);
    }

    public static String insertString(
            String originalString,
            String stringToBeInserted,
            int index)
    {
        StringBuffer newString
                = new StringBuffer(originalString);

        newString.insert(index + 1, stringToBeInserted);
        return newString.toString();
    }

    public void run() {
        RandomAccessFile file = null;
        InputStream stream = null;

        try {
            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();

            connection.setRequestProperty("Range",
                    "bytes=" + telecharge + "-");

            connection.connect();

            // Make sure response code is in the 200 range.
            if (connection.getResponseCode() / 100 != 2) {
                ereur();
            }

            // Check for valid content length.
            int contentLength = connection.getContentLength();
            if (contentLength < 1) {
                ereur();
            }

      /* Set the taille for this download if it
         hasn't been already set. */
            if (taille == -1) {
                taille = contentLength;
                stateChanged();
            }
            int num = 0;
            String save = getFileName(url);
            File fileName = new File(dossier, save);
            while(fileName.exists()) {
                if (getFileName(url).lastIndexOf('.') == -1 )  save = getFileName(url) + " ("+String.valueOf(num)+")";
                else save =  insertString(getFileName(url), " ("+String.valueOf(num)+")",getFileName(url).lastIndexOf('.')-1) ;
                fileName = new File(dossier , save);
                num++;
            }

            // Open file and seek to the end of it.
            file = new RandomAccessFile(fileName, "rw");
            file.seek(telecharge);

            stream = connection.getInputStream();
            while (status == EnCoursTelechargement) {
        /* taille buffer according to how much of the
           file is left to download. */
                byte[] buffer;
                if (taille - telecharge > MAX_BUFFER_SIZE) {
                    buffer = new byte[MAX_BUFFER_SIZE];
                } else {
                    buffer = new byte[taille - telecharge];
                }

                // Read from server into buffer.
                int read = stream.read(buffer);
                if (read == -1)
                    break;

                // Write buffer to file.
                file.write(buffer, 0, read);
                telecharge += read;
                stateChanged();
            }

      /* Change status to Complet if this point was
         reached because EnCoursTelechargement has finished. */
            if (status == EnCoursTelechargement) {
                status = Complet;
                stateChanged();
            }
        } catch (Exception e) {
            ereur();
            System.out.println(1);
        } finally {
            // Close file.
            if (file != null) {
                try {
                    file.close();
                } catch (Exception ignored) {}
            }

            // Close connection to server.
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception ignored) {
                    System.out.println(1);
                }
            }
        }
    }

    // Notify observers that this download's status has changed.
    private void stateChanged() {
        setChanged(); // Observable Méthode
        notifyObservers(); // Observable Méthode
    }
}
