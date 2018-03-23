import java.util.*;
import java.lang.*;
import java.io.*;
public class Board
{
Stone[][] brett; //Spielbrett
boolean[][] egroup; //Als Gruppenmarkierung der Teritory Felder
boolean[][] used; //Bereits gezählte Felder
boolean white = false; //Aktueller Spieler
int n; //Spielbrett width
int pointsW = 0; //Punkte white
int pointsB = 0; //Punkte black


public Board(int n) //Übergabe der Spielbrettgröße und erzeugen des Boards
{
        this.n = n;
        this.brett = new Stone[n][n];
        this.egroup = new boolean[n][n];
        this.used = new boolean[n][n];
}

public void move(Scanner sc)
{
        if(white) System.out.println("Weiß ist am Zug");
        else System.out.println("Schwarz ist am Zug");
        System.out.println("Zeile?");
        int zeile = sc.nextInt();
        while(zeile >= n || zeile < 0) //Eingabe check
        {
                System.out.println("Ungültiger Move");
                System.out.println("Zeile?");
                zeile = sc.nextInt();

        }
        System.out.println("Spalte?");
        int spalte = sc.nextInt();
        while(spalte >= n || spalte < 0)
        {
                System.out.println("Ungültiger Move");
                System.out.println("Spalte?");
                spalte = sc.nextInt();

        }
        while(brett[zeile][spalte] != null) //check ob dort bereits ein stein liegt
        {
                System.out.println("Ungültiger Move");
                System.out.println("Zeile?");
                zeile = sc.nextInt();

                while(zeile >= n || zeile < 0)
                {
                        System.out.println("Ungültiger Move");
                        System.out.println("Zeile?");
                        zeile = sc.nextInt();

                }
                System.out.println("Ungültiger Move");
                System.out.println("Spalte?");
                spalte = sc.nextInt();
                while(spalte >= n || spalte < 0)
                {
                        System.out.println("Ungültiger Move");
                        System.out.println("Spalte?");
                        spalte = sc.nextInt();

                }

        }
        System.out.println();

        brett[zeile][spalte] = new Stone(white); //Stelle markieren
}

public void draw()
{
        System.out.print(" ");
        for(int i = 0; i < n; i++) {
                System.out.print(" " + i);
        }
        System.out.println();
        System.out.print(" ");
        for(int i = 0; i < n; i++) {
                System.out.print(" _");
        }
        System.out.println();
        for(int i = 0; i < n; i++)
        {
                System.out.print(i);
                for(int j = 0; j < n; j++)
                {
                        if(brett[i][j] == null) System.out.print("| ");
                        if(brett[i][j] != null && !brett[i][j].isWhite()) System.out.print("|o");
                        if(brett[i][j] != null && brett[i][j].isWhite()) System.out.print("|*");
                        if(j+1 == n) System.out.print("|");
                }
                System.out.println();
        }
        System.out.print(" ");
        for(int i = 0; i < n; i++) {
                System.out.print(" ¯");
        }
        System.out.println();
}

public void kick(){
        for(int i = 0; i < n; i++) {
                for(int j = 0; j < n; j++) {
                        findGroup(i, j);
                        if(freiGroup(i, j) == 0)  {         //wenn die gruppe keine freiheiten hat
                                realkick();         //kick der gruppe
                                reset();          //reset der gruppenauswahl
                        }
                        else reset();
                }
        }
}

public void freiSingle(int i, int j) //subtrahiert freiheiten fuer kanten und gegn. steine
{
        int freiheiten = 4;
        boolean steinFarbe = brett[i][j].isWhite();

        if(i == 0 || i == n-1) freiheiten -= 1; //Abzug fuer Brettkante oben
        if(j == 0 || j == n-1) freiheiten -= 1;

        if(i != 0) //nach oben
        {
                if(brett[i-1][j] != null && brett[i-1][j].isWhite() == !steinFarbe) freiheiten -= 1;
        }
        if(i != n-1) //nach unten
        {
                if(brett[i+1][j] != null && brett[i+1][j].isWhite() == !steinFarbe) freiheiten -= 1;
        }
        if(j != 0) //nach links
        {
                if(brett[i][j-1] != null && brett[i][j-1].isWhite() == !steinFarbe) freiheiten -= 1;
        }
        if(j != n-1) //nach rechts
        {
                if(brett[i][j+1] != null && brett[i][j+1].isWhite() == !steinFarbe) freiheiten -= 1;
        }
        brett[i][j].setFreiheit(freiheiten);

        //Errorcheck
        // if(i == 5 && j == 4) System.out.println("5,4 freiSingle = " +   brett[i][j].getFreiheit());
        // if(i == 5 && j == 5) System.out.println("5,5 freiSingle = " +   brett[i][j].getFreiheit());
}
public void findCon(int i, int j){
        boolean steinFarbe = brett[i][j].isWhite();
        int con = 0;

        if(i != 0) {         //Anzahl der Verbindungen zu anderen gleichen Steinen finden
                if(brett[i-1][j] != null && brett[i-1][j].isWhite() == steinFarbe) con++;
        }
        if(i < n-1) {
                if(brett[i+1][j] != null && brett[i+1][j].isWhite() == steinFarbe) con++;
        }
        if(j != 0) {
                if(brett[i][j-1] != null && brett[i][j-1].isWhite() == steinFarbe) con++;
        }
        if(j < n-1) {
                if(brett[i][j+1] != null && brett[i][j+1].isWhite() == steinFarbe) con++;
        }
        brett[i][j].setCon(con);
}
public void findGroup(int i, int j){ //gruppe finden und jedem stein die anzahl der verbindungen zuweisen

        if(brett[i][j] != null) {
                findCon(i, j);
                brett[i][j].mark(); //jeden Stein auf markieren setzen der teil der grupe ist
                boolean steinFarbe = brett[i][j].isWhite();

                //rekursiv anliegende steine als teil der gruppe markieren
                if(i != 0) { //nach oben
                        if(brett[i-1][j] != null && !brett[i-1][j].group && brett[i-1][j].isWhite() == steinFarbe) findGroup(i-1, j);
                }
                if(i < n-1) { //nach unten
                        if(brett[i+1][j] != null && !brett[i+1][j].group && brett[i+1][j].isWhite() == steinFarbe) findGroup(i+1, j);
                }
                if(j != 0) { //nach links
                        if(brett[i][j-1] != null && !brett[i][j-1].group && brett[i][j-1].isWhite() == steinFarbe) findGroup(i, j-1);
                }
                if(j < n-1) { //nach rechts
                        if(brett[i][j+1] != null && !brett[i][j+1].group && brett[i][j+1].isWhite() == steinFarbe) findGroup(i, j+1);
                }
        }
}

public int freiGroup(int zeile, int spalte){
        int points = 0;
        int con = 0;
        int groupSize = 0;
        for(int i = 0; i < n; i++) {
                for(int j = 0; j < n; j++) {
                        if(brett[i][j] != null && brett[i][j].group) {
                                freiSingle(i, j);
                                points += brett[i][j].getFreiheit();
                                con += brett[i][j].getCon();
                                groupSize++;
                        }
                }
        }
        points -= con;         //Freiheiten der gesamten Gruppe minus deren Verbindungen

        //Error Check
        // if(zeile == 4 && spalte == 4) System.out.println("4,4 freiGroup = " + points);
        // if(zeile == 4 && spalte == 5) System.out.println("4,5 freiGroup = " + points);

        return points;
}

public void Teritory(int i, int j){ //Gruppe der leeren Steine finden und markieren

        if(brett[i][j] == null) {
                egroup[i][j] == true; //jedes Teritory Feld markieren was der umschlossenen Gruppe ist

                //rekursiv anliegende Felder als Teil der Gruppe markieren
                if(i != 0) { //nach oben
                        if(brett[i-1][j] == null && !egroup[i-1][j]) Teritory(i-1, j);
                }
                if(i < n-1) { //nach unten
                        if(brett[i+1][j] == null && !egroup[i+1][j]) Teritory(i+1, j);
                }
                if(j != 0) { //nach links
                        if(brett[i][j-1] == null && !egroup[i][j-1]) Teritory(i, j-1);
                }
                if(j < n-1) { //nach rechts
                        if(brett[i][j+1] != null && !egroup[i][j+1]) Teritory(i, j+1);
                }
        }
}

public void realkick(){ //kickt alle steine der aktuellen gruppe
        for(int i = 0; i < n; i++) {
                for(int j = 0; j < n; j++) {
                        if(brett[i][j] != null && brett[i][j].group) {
                                if(brett[i][j].isWhite()) pointsB++;
                                else pointsW++;
                                remove(i, j);
                        }
                }
        }
}


public void reset(){
        for(int i = 0; i < n; i++) { //Reset der Gruppe
                for(int j = 0; j < n; j++) {
                        if(brett[i][j] != null)
                                brett[i][j].unmark();
                }
        }
}
public void remove(int i, int j){
        brett[i][j] = null;
}
public void next(){
        white = !white;
}
}
