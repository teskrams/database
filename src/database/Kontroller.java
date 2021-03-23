package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


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
            newPost.setInt(1, findPostID());
            newPost.setString(2, heading);
            newPost.setString(3, text);
            newPost.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            newPost.setString(5, user.getName());
            newPost.toString();            
            newPost.executeUpdate();
            System.out.println("Posten er skrevet");

            if (!checkFolder(folder)) {
                String sqlString3 = "insert into folder values(?,?)";
                PreparedStatement newPost3 = conn.prepareStatement(sqlString3);
                newPost3.setString(1, folder.getName());
                newPost3.setInt(2, 1);
                newPost3.executeUpdate();
                System.out.println("Du har laget en ny mappe med navnet:"+folder.getName());
            }
            
            PreparedStatement newPost2 = conn.prepareStatement(sqlString2);
            newPost2.setInt(1, findThreadID());
            newPost2.setInt(2, findPostID() - 1);
            newPost2.setString(3, postType);
            newPost2.setString(4, folder.getName());
            newPost2.executeUpdate();
            System.out.println("Posten din er en ny tråd");

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
            System.out.println("Posten er skrevet");

            PreparedStatement newPost = conn.prepareStatement(sqlString);
            newPost.setInt(1, postid);
            newPost.setInt(2, findPostID() - 1);
            newPost.executeUpdate();
            System.out.println("Du har svart på en tråd");
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
            newPost2.executeUpdate();
            System.out.println("Posten er skrevet");

            PreparedStatement newPost = conn.prepareStatement(sqlString);
            newPost.setInt(1, postid);
            newPost.setInt(2, findPostID()-1);
            newPost.executeUpdate();
            System.out.println("Du har svart på et svar");

        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("feil i databaseconnection");
        }
    }
    
    public Collection<Integer> searchHeading(String heading) {
        Collection<Integer> result = new ArrayList<>();
        try {
            PreparedStatement headingSearch = conn.prepareStatement("select postid from post where heading like ?");
            headingSearch.setString(1, "%"+heading+"%");
            ResultSet posts = headingSearch.executeQuery();
            while (posts.next()) {
                result.add(posts.getInt("postID"));
            }
            System.out.println("Søket ga følgende treff:"+result.toString());
            return result;
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("feil i databaseconnection");
            return result;
        }
    }

    public Collection<Integer> searchText(String text) {
        Collection<Integer> result = new ArrayList<>();
        try {
            PreparedStatement textSearch = conn.prepareStatement("select postid from post where postText like ?");
            textSearch.setString(1,"%" + text + "%");
            ResultSet posts = textSearch.executeQuery();
            while (posts.next()){
                result.add(posts.getInt("postid"));
            }
            System.out.println("Søket ga følgende treff: " + result.toString());
            return result;
        } 
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("feil i databaseconnection");
            return result;
        }
    }

    public Collection<List<String>> stats() {
        Collection<List<String>> result = new ArrayList<>();
        try {
            PreparedStatement stats = conn.prepareStatement("select siteUser.email, count(hasread.email)"+
                                                            "from (siteUser left outer join hasread on siteUser.email = hasread.Email)"+
                                                            "group by hasread.email order by count(hasread.email) desc;");
            ResultSet read = stats.executeQuery();
            Collection<List<String>> readlist = new ArrayList<>();
            while (read.next()) {
                List<String> løkke = new ArrayList<>();
                løkke.add(read.getString("email"));
                løkke.add(read.getString("count(hasread.email)"));
                readlist.add(løkke);
            }
            PreparedStatement stats2 = conn.prepareStatement("select siteUser.email, count(post.authoremail)"+
                                                            "from (siteUser left outer join post on siteUser.email = post.authorEmail)"+
                                                            "group by post.authoremail order by count(post.authoremail) desc;");
            ResultSet write = stats2.executeQuery();
            List<List<String>> writelist = new ArrayList<>();
            while (write.next()) {
                List<String> løkke = new ArrayList<>();
                løkke.add(write.getString("email"));
                løkke.add(write.getString("count(post.authoremail)"));
                writelist.add(løkke);
            }
            result.addAll(readlist);
            for (List<String> list : writelist) {
                for (List<String> list2 : result){
                    if (list.get(0).equals(list2.get(0))) {
                        list2.add(list.get(1));
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("feil i databaseconnection");
            return result;
        }
        System.out.println("litt statistikk([bruker, antall lest, antall skrevet]):" + result);
        return result;
    }
    
    
    public static void main(String[] args) {
        Kontroller test = new Kontroller();
        test.connect();
        test.login("jens@jens.no", "admin");
        //test.newPost(user, "test1", "test teste teste", new Folder("test"), "Question");
        //System.out.println(user);
        //test.searchHeading("tt");
        //test.searchText("teste");
        test.stats();
        //test.replyReply(user, "testtest", "kultest", 1);
        //System.out.println(test.findPostID());
    }
}
