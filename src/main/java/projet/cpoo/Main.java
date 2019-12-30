package projet.cpoo;

import java.net.MalformedURLException;
import java.util.Scanner;
import java.net.URL;

public class Main {
    public static void main (String[] args) throws MalformedURLException {
        Gestionnaire gest = new Gestionnaire() ;
        while(true){
            Scanner sc = new Scanner(System.in);
            String st = sc.nextLine();
            URL url;
            try {
                url = new URL(st);
                gest.telecharger(url);

            }
            catch(Exception e){System.out.println("ey tnaket");}

        }
    }
}
