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
            PreparedStatement checkUser = conn.prepareStatement("select email, passwd from siteuser");
            //checkUser.setString(1, "theodor@theodor.no");
            //System.out.println("haha1");
            //checkUser.setString(2, "passwd");
            //checkUser.setString(3, "siteUser");
            //System.out.println("kada");
            //System.out.println(checkUser.toString());
            ResultSet brukere = checkUser.executeQuery();
            while (brukere.next()) {
                if (brukere.getString("email").equals(username)) {
                    if (brukere.getString("passwd").equals(password)) {
                        innlogget.add(new User(username, password));
                        System.out.println("du er logget inn");
                        return;
                    }
                    System.out.println("feil passord");
                    return;
                }
            }
            System.out.println("finnes ingen bruker med denne innloggingen");
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("feil i databaseconnection");
        }
    }
    public static void main(String[] args) {
        Kontroller test = new Kontroller();
        test.connect();
        test.login("jens@jens.no", "admin");
        System.out.println(innlogget);
    }
}
