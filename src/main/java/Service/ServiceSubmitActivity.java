package Service;

import dao.BaseDao;
import model.ActivityModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 项目提交服务
 * <p>User: Guo Hong
 * <p>Date: 15-7-8
 * <p>Version: 1.0
 */
@Service
public class ServiceSubmitActivity extends ServiceBase {
    @Autowired
    private BaseDao baseDao;

    public String submitActivity( HttpServletRequest request, HttpServletResponse response ){
        ActivityModel activityModel = createProject(request );
        return "hahha";
    }

    private ActivityModel createProject( HttpServletRequest request ){

        ActivityModel activityModel = new ActivityModel();
        activityModel.setActivityIntroduce("fda;fsdk");


        baseDao.save(activityModel);

        return activityModel;
    }
}
