package mad4124.team_sundry.task.model;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "subTasks"
//        ,
//        indices = {@Index("task_id")},
//        foreignKeys = @ForeignKey(entity = Task.class,
//                parentColumns = "id",
//                childColumns = "task_id",
//                onDelete = CASCADE)
)
public class SubTask {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private boolean status;
    private String descriptionSubTask;

    @ColumnInfo(name = "task_id")
    private long parentTaskId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getDescriptionSubTask() {
        return descriptionSubTask;
    }

    public void setDescriptionSubTask(String descriptionSubTask) {
        this.descriptionSubTask = descriptionSubTask;
    }

    public long getParentTaskId() {
        return parentTaskId;
    }

    public void setParentTaskId(long parentTaskId) {
        this.parentTaskId = parentTaskId;
    }
}
