package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;


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

    public boolean checkFolder(Folder folder) {
        try {
            PreparedStatement checkFolder = conn.prepareStatement("select foldername from folder");
            ResultSet folders = checkFolder.executeQuery();
            while (folders.next()) {
                if (folders.getString("foldername").equals(folder.getName())) {
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

    public int findThreadID() {
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

     public int findPostID() {
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


    public void newPost(User user, String heading, String text, Folder folder, String postType) {
        String sqlString = "insert into post values(?,?,?,?,?);";
        String sqlString2 = "insert into thread values(?,?,?,?);"; 
        try {
            PreparedStatement newPost = conn.prepareStatement(sqlString);
            System.out.println("test");
            newPost.setInt(1, findPostID());
            newPost.setString(2, heading);
            newPost.setString(3, text);
            newPost.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            newPost.setString(5, user.getName());
            newPost.toString();            
            newPost.executeUpdate();

            if (!checkFolder(folder)) {
                String sqlString3 = "insert into folder values(?,?)";
                PreparedStatement newPost3 = conn.prepareStatement(sqlString3);
                newPost3.setString(1, folder.getName());
                newPost3.setInt(2, 1);
                newPost3.executeUpdate();
            }
            
            PreparedStatement newPost2 = conn.prepareStatement(sqlString2);
            newPost2.setInt(1, findThreadID());
            newPost2.setInt(2, findPostID() - 1);
            newPost2.setString(3, postType);
            newPost2.setString(4, folder.getName());
            System.out.println("test2");
            newPost2.executeUpdate();

        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("feil i databaseconnection");
        }
    }

    public void replyPost(User user, String heading, String text, int postid) {
        String sqlString = "insert into replyAffiliation values(?,?);";
        String sqlString2 = "insert into post values(?,?,?,?,?)";
        try {
            PreparedStatement newPost2 = conn.prepareStatement(sqlString2);
            newPost2.setInt(1, findPostID());
            newPost2.setString(2, heading);
            newPost2.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            newPost2.setString(3, text);
            newPost2.setString(5, user.getName());
            newPost2.executeUpdate();

            PreparedStatement newPost = conn.prepareStatement(sqlString);
            newPost.setInt(1, postid);
            newPost.setInt(2, findPostID() - 1);
            newPost.executeUpdate();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("feil i databaseconnection");
        }
    }

    public void replyReply(User user, String heading, String text, int postid) {
        String sqlString = "insert into replyOnReply values(?,?);";
        String sqlString2 = "insert into post values(?,?,?,?,?)";
        try {
            PreparedStatement newPost2 = conn.prepareStatement(sqlString2);
            newPost2.setInt(1, findPostID());
            newPost2.setString(2, heading);
            newPost2.setString(3, text);
            newPost2.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            newPost2.setString(5, user.getName());
            System.out.println(newPost2.toString());
            newPost2.executeUpdate();

            PreparedStatement newPost = conn.prepareStatement(sqlString);
            newPost.setInt(1, postid);
            newPost.setInt(2, findPostID()-1);
            newPost.executeUpdate();

        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("feil i databaseconnection");
        }
    }
    
    public static void main(String[] args) {
        Kontroller test = new Kontroller();
        test.connect();
        test.login("jens@jens.no", "admin");
        test.newPost(user, "test1", "test teste teste", new Folder("test"), "Question");
        System.out.println(user);
        //test.replyReply(user, "testtest", "kultest", 1);
        //System.out.println(test.findPostID());
    }
}
