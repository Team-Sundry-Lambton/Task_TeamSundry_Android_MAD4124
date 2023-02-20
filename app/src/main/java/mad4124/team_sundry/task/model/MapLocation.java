package mad4124.team_sundry.task.model;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "locations",
        indices = {@Index("task_id"),@Index("category_id")},
        foreignKeys = {
            @ForeignKey(entity = Task.class,
                    parentColumns = "id",
                    childColumns = "task_id",
                    onDelete = CASCADE),
            @ForeignKey(entity = Category.class,
                    parentColumns = "id",
                    childColumns = "category_id",
                    onDelete = CASCADE)
        }
)
public class MapLocation implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private Double lat;
    private Double lng;
    @ColumnInfo(name = "task_id")
    private long taskId;
    @ColumnInfo(name = "category_id")
    private long categoryID;

    public long getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(long categoryID) {
        this.categoryID = categoryID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    @Override
    public String toString() {
        return "MapLocation{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", taskId=" + taskId +
                ", categoryID=" + categoryID +
                '}';
    }
}
