package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;


public class Kontroller extends DBConn{

    public Kontroller(){
    }

    static Collection<User> innlogget = new ArrayList<User>();
    

    public void login(String username, String password) {
        try{
            System.out.println("test");
            PreparedStatement checkUser = conn.prepareStatement("SELECT email, passwd FROM siteUser");
            System.out.println("haha");
            ResultSet brukere = checkUser.executeQuery();
            System.out.println("sjekk");
            while (brukere.next()) {
                System.out.println("en linje");
                if (brukere.getString("email").equals(username)) {
                    if (brukere.getString("passwd").equals(password)) {
                        innlogget.add(new User(username, password));
                        System.out.println("du er logget inn");
                        break;
                    }
                    System.out.println("feil passord");
                    break;
                }
            }
            System.out.println("finnes ingen bruker med denne innloggingen");
        }
        catch (Exception e){
            System.out.println("feil i databaseconnection");
        }
    }
    public static void main(String[] args) {
        Kontroller test = new Kontroller();
        test.connect();
        test.login("jens@jens.no", "admin");
    }
}
