package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

public class Kontroller extends DBConn{

    static User user;
    
    public Kontroller(){
    }
    
    public void login(Scanner credentialScanner) { //usecase 1 (Du kan logge inn), den sjekker om brukeren eksisterer og om passordet er riktig
        System.out.println("Skriv e-post");
        String username = credentialScanner.nextLine(); 
        System.out.println("Skriv passord:");
        String password = credentialScanner.nextLine();
        try{
            PreparedStatement checkUser = conn.prepareStatement("select email, passwd from siteuser");
            ResultSet brukere = checkUser.executeQuery();
            while (brukere.next()) {
                if (brukere.getString("email").equals(username)) {
                    if (brukere.getString("passwd").equals(password)) {
                        user = new User(username, password); // setter brukeren i systemet, slik at poster lett kan linkes uten ny inn logging
                        System.out.println("Du er logget inn");
                        return;
                    }
                    throw new IllegalArgumentException("feil passord");
                }
            }
            throw new IllegalArgumentException("finnes ingen bruker med denne innloggingen");
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("feil i databaseconnection");
        }
    }

    public boolean checkFolder(String folder) { // sjekker om det eksisterer en mappe med et gitt navn
        try {
            PreparedStatement checkFolder = conn.prepareStatement("select foldername from folder");
            ResultSet folders = checkFolder.executeQuery();
            while (folders.next()) {
                if (folders.getString("foldername").equals(folder)) {
                    return true;
                }
            }
            return false;
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("feil i databaseconnection");
        }
        return false;
    }

    public int findThreadID() { //Finner en ID til neste thread(sjekker site ID i databasen)
        try {
            PreparedStatement findThreadID = conn.prepareStatement("select max(threadID) from thread");
            ResultSet threadID_set = findThreadID.executeQuery();
            threadID_set.next();
            int threadID = threadID_set.getInt("max(threadID)") + 1;
            return threadID;
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("feil i databaseconnection");
        }
        return 0;
    }

     public int findPostID() { //finner en ID til neste post(sjekker siste ID i databasen)
        try {
            PreparedStatement findPostID = conn.prepareStatement("select max(postID) from post");
            ResultSet postID_set = findPostID.executeQuery();
            postID_set.next();
            int postID = postID_set.getInt("max(postid)") + 1;
            return postID;
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("feil i databaseconnection");
        }return 0;
    }

    public void newPost(Scanner credentialScanner) { // Usecase 2 (publiserer en post)
        String sqlString = "insert into post values(?,?,?,?,?);";
        String sqlString2 = "insert into thread values(?,?,?,?);";
        System.out.println("Skriv overskrift:");
        String heading = credentialScanner.nextLine();
        System.out.println("Skriv innholdet i posten:");
        String text = credentialScanner.nextLine();
        System.out.println("Skriv mappe:");
        String folder = credentialScanner.nextLine();
        System.out.println("Skriv post-type:");
        String postType = credentialScanner.nextLine();

        try {
            PreparedStatement newPost = conn.prepareStatement(sqlString); //ny post
            newPost.setInt(1, findPostID());
            newPost.setString(2, heading);
            newPost.setString(3, text);
            newPost.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            newPost.setString(5, getName());           
            newPost.executeUpdate(); 
            System.out.println("Posten er skrevet");

            if (!checkFolder(folder)) { //hvis folderen du ønsker å publiserer under ikke eksisterer lager den en ny
                String sqlString3 = "insert into folder values(?,?)";
                PreparedStatement newPost3 = conn.prepareStatement(sqlString3);
                newPost3.setString(1, folder);
                newPost3.setInt(2, 1);
                newPost3.executeUpdate();
                System.out.println("Du har laget en ny mappe med navnet:"+folder);
            }
            
            PreparedStatement newPost2 = conn.prepareStatement(sqlString2); //posten blir satt som tråd
            newPost2.setInt(1, findThreadID());
            newPost2.setInt(2, findPostID() - 1);
            newPost2.setString(3, postType);
            newPost2.setString(4, folder);
            newPost2.executeUpdate(); 
            System.out.println("Posten din er en ny tråd");

        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("feil i databaseconnection");
        }
    }

    public void replyPost(Scanner credentialScanner) { //Usecase 3 (svare på en tråd)
        System.out.println("Skriv overskriften: ");
        String heading = credentialScanner.nextLine(); 
        System.out.println("Skriv tekst: ");
        String text = credentialScanner.nextLine();
        System.out.println("Skriv postID til posten du vil svare: ");
        String postid = credentialScanner.nextLine();
        
        String sqlString = "insert into replyAffiliation values(?,?);";
        String sqlString2 = "insert into post values(?,?,?,?,?)";
        try {
            PreparedStatement newPost2 = conn.prepareStatement(sqlString2); //En ny post
            newPost2.setInt(1, findPostID());
            newPost2.setString(2, heading);
            newPost2.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            newPost2.setString(3, text);
            newPost2.setString(5, getName());
            newPost2.executeUpdate(); 
            System.out.println("Posten er skrevet");

            PreparedStatement newPost = conn.prepareStatement(sqlString); //Blir linket som et svar på tråden
            newPost.setInt(1, Integer.parseInt(postid));
            newPost.setInt(2, findPostID() - 1);
            newPost.executeUpdate(); 
            System.out.println("Du har svart på en tråd");
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("feil i databaseconnection");
        }
    }

    public void replyReply(Scanner credentialScanner) { //Usecase 3 (svar på et svar)
        System.out.println("Overskrift:");
        String heading = credentialScanner.nextLine(); 
        System.out.println("Text:");
        String text = credentialScanner.nextLine();
        System.out.println("Id parentPost:");
        String postid = credentialScanner.nextLine();
        
        String sqlString = "insert into replyOnReply values(?,?);";
        String sqlString2 = "insert into post values(?,?,?,?,?)";
        try {
            PreparedStatement newPost2 = conn.prepareStatement(sqlString2); //ny post
            newPost2.setInt(1, findPostID());
            newPost2.setString(2, heading);
            newPost2.setString(3, text);
            newPost2.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            newPost2.setString(5, getName());
            newPost2.executeUpdate(); 
            System.out.println("Posten er skrevet");

            PreparedStatement newPost = conn.prepareStatement(sqlString); //blir linket som et svar på et svar
            newPost.setInt(1, Integer.parseInt(postid));
            newPost.setInt(2, findPostID()-1);
            newPost.executeUpdate(); 
            System.out.println("Du har svart på et svar");
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("feil i databaseconnection");
        }
    }
    
    public Collection<Integer> searchHeading(Scanner credentialScanner) { //Usecase 4 (Søker med et ord i overskrifter)
        System.out.println("Skriv overskrift du ønsker å søke etter:");
        String heading = credentialScanner.nextLine();

        Collection<Integer> result = new ArrayList<>();
        try {
            PreparedStatement headingSearch = conn.prepareStatement("select postid from post where heading like ?");
            headingSearch.setString(1, "%"+heading+"%");
            ResultSet posts = headingSearch.executeQuery();
            while (posts.next()) {
                result.add(posts.getInt("postID"));
            }
            System.out.println("Søket ga følgende treff:"+result.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("deil i databaseconnection");       
        }
        return result;
    }

    public Collection<Integer> searchText(Scanner credentialScanner) { //Usecase 4 (søker med et ord i teksten til postene)
        System.out.println("Skriv tekst du ønsker å søke etter:");
        String text = credentialScanner.nextLine();

        
        Collection<Integer> result = new ArrayList<>();
        try {
            PreparedStatement textSearch = conn.prepareStatement("select postid from post where postText like ?");
            textSearch.setString(1,"%" + text + "%");
            ResultSet posts = textSearch.executeQuery();
            while (posts.next()){
                result.add(posts.getInt("postid"));
            }
            System.out.println("Søket ga følgende treff: " + result.toString());
        } 
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("feil i databaseconnection");
        }
        return result;
    }

    public Collection<List<String>> stats() { //finner statistikk for hver bruker i databasen
        Collection<List<String>> result = new ArrayList<>();
        try {
            PreparedStatement stats = conn.prepareStatement("select siteUser.email, count(distinct hasread.postID),"+
            " count(distinct post.postID) from ((siteUser left join hasread on siteUser.email = hasread.email)"+
            "left join post on post.authoremail = siteUser.email) group by siteUser.email order by count(hasread.email) desc;");
            
            ResultSet read = stats.executeQuery();
            while (read.next()) { //legger informasjonen om hver bruker som en liste inn i resultatet
                List<String> loop = new ArrayList<>();
                loop.add(read.getString("email"));
                loop.add(read.getString("count(distinct hasread.postID)"));
                loop.add(read.getString("count(distinct post.postID)"));
                result.add(loop);
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("feil i databaseconnection");
        }
        System.out.println("Litt statistikk, sortert på flest antall leste innlegg: ([bruker, antall lest, antall skrevet]):" + result);
        return result;
    }

    private String getName() { //sjekker om du er logget inn og returner navnet
        try {
            return user.getName();
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Bruker ikke logget inn");
        }
    }

    public static void main(String[] args) {
        Scanner application = new Scanner(System.in);
        Kontroller test = new Kontroller();
        test.connect();
        System.out.println("Du må logge inn først:");
        test.login(application);
        System.out.println("Så kan du skrive en post(ny tråd)");
        test.newPost(application);
        System.out.println("Så kan du svare på en post");
        test.replyPost(application);
        System.out.println("Du kan også svare på et svar");
        test.replyReply(application);
        System.out.println("Du kan søke i overskrifter:");
        test.searchHeading(application);
        System.out.println("Du kan også søke på tekst:");
        test.searchText(application);
        System.out.println("Her følger statistikk:");
        test.stats();
        application.close();
    }
}