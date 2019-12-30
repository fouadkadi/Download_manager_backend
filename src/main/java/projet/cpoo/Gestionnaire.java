package projet.cpoo;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Gestionnaire {
    public List<Telechargement> telechargementList = new ArrayList<Telechargement>();
    public void telecharger(URL url){
        Telechargement tel = new Telechargement(url);
        telechargementList.add(tel);
    }
    public void pause (Telechargement e){
        e.pause();
    }
    public void resume(Telechargement e) {
        e.resume();
    }

    public void cancel(Telechargement e) {
        e.cancel();
    }

    public float getProgress(Telechargement e) {
        return e.getProgress();
    }

    public int getTaille(Telechargement e) {
        return e.getTaille();
    }

    public String getUrl (Telechargement e) {
        return e.getUrl();
    }

    public List<Telechargement> TelechEnCour () {
        return telechargementList.stream()
                .filter(tel -> tel.getStatus() == 0)
                .collect(Collectors.toList());
    }
    public List<Telechargement> TelechEnPause () {
        return telechargementList.stream()
                .filter(tel -> tel.getStatus() == 1)
                .collect(Collectors.toList());

    }

    public List<Telechargement> TelechTermin () {
        return telechargementList.stream()
                .filter(tel -> tel.getStatus() == 2)
                .collect(Collectors.toList());
    }
    public List<Telechargement> TelechAnnul () {
        return telechargementList.stream()
                .filter(tel -> tel.getStatus() == 3)
                .collect(Collectors.toList());
    }


}
