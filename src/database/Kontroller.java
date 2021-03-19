package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class Kontroller extends DBConn{

    public Kontroller(){
    }

    static User user;
    

    public void login(String username, String password) {
        try{
            PreparedStatement checkUser = conn.prepareStatement("select email, passwd from siteuser");
            ResultSet brukere = checkUser.executeQuery();
            while (brukere.next()) {
                if (brukere.getString("email").equals(username)) {
                    if (brukere.getString("passwd").equals(password)) {
                        user = new User(username, password);
                        System.out.println("Du er logget inn");
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
        System.out.println(user);
    }
}
