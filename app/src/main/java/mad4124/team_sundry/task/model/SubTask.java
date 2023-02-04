package mad4124.team_sundry.task.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "subTasks")
public class SubTask {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private boolean status;
    private String descriptionSubTask;
    private int parentTaskId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public int getParentTaskId() {
        return parentTaskId;
    }

    public void setParentTaskId(int parentTaskId) {
        this.parentTaskId = parentTaskId;
    }
}
