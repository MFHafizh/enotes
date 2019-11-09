package id.ac.ui.cs.mobileprogramming.muhammadfakhruddinhafizh.enotes.models;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

import id.ac.ui.cs.mobileprogramming.muhammadfakhruddinhafizh.enotes.utils.Constant;
import id.ac.ui.cs.mobileprogramming.muhammadfakhruddinhafizh.enotes.utils.DateRoomConverter;

@Entity(tableName = Constant.TABLE_NAME_NOTE)
public class Note implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long note_id;

    @ColumnInfo(name = "note_content") // column name will be "note_content" instead of "content" in table
    private String content;

    private String title;

    private Long dateLong;

    private String imagePath;

    private String location;


    public Note(String content, String title, @Nullable String imagePath, String location) {
        this.content = content;
        this.title = title;
        this.dateLong = System.currentTimeMillis();
        this.imagePath = imagePath;
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getDateLong() {
        return dateLong;
    }

    public void setDateLong(Long dateLong) {
        this.dateLong = dateLong;
    }

    public Date getDate() {
        return DateRoomConverter.toDate(dateLong);
    }

    public void setDate(Date date) {
        this.dateLong = DateRoomConverter.toLong(date);
    }

    public long getNote_id() {
        return note_id;
    }

    public void setNote_id(long note_id) {
        this.note_id = note_id;
    }

    public String getContent() {
        return content;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Note)) return false;

        Note note = (Note) o;

        if (note_id != note.note_id) return false;
        return title != null ? title.equals(note.title) : note.title == null;
    }



    @Override
    public int hashCode() {
        int result = (int)note_id;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Note{" +
                "note_id=" + note_id +
                ", content='" + content + '\'' +
                ", title='" + title + '\'' +
                ", date=" + getDate() +
                '}';
    }
}
