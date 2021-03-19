package database;

import java.time.LocalDateTime;


public class Post {

    int postid;
    String heading;
    String text;
    LocalDateTime dateTime;
    User author;
    Folder folder;

    public Post(int postid, String heading, String text, LocalDateTime dateTime, User author, Folder folder) {
        this.author = author;
        this.dateTime = dateTime;
        this.heading = heading;
        this.postid = postid;
        this.text = text;
        this.folder = folder;
    }

}
