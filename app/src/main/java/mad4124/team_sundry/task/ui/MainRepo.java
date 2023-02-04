package mad4124.team_sundry.task.ui;


import javax.inject.Inject;

import mad4124.team_sundry.task.db.DbDao;
import mad4124.team_sundry.task.model.Category;

public class MainRepo {

    DbDao dbDao;

    @Inject
    public MainRepo(DbDao dbDao) {
        this.dbDao = dbDao;
    }

    void addCategory(Category category) {
        dbDao.addCategory(category);
    }

}
