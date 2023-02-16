package mad4124.team_sundry.task.model;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "tasks",
        indices = {@Index("category_id")},
        foreignKeys = {
            @ForeignKey(entity = Category.class,
                    parentColumns = "id",
                    childColumns = "category_id",
                    onDelete = CASCADE)
        }
)
public class Task implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;
    private String title;
    private Long createdDate;
    private Long dueDate;
    private boolean isTask;
    private boolean status;
    private String description;
    private String categoryName;

    @ColumnInfo(name = "category_id")
    private int parentCategoryId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public Long getDueDate() {
        return dueDate;
    }

    public void setDueDate(Long dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isTask() {
        return isTask;
    }

    public void setTask(boolean task) {
        isTask = task;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setParentCategoryId(int parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public int getParentCategoryId() {
        return parentCategoryId;
    }
}
