package mad4124.team_sundry.task.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "medias")
public class MediaFile {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private boolean isImage;
    private String path;
    private int taskId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isImage() {
        return isImage;
    }

    public void setImage(boolean image) {
        isImage = image;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }
}
