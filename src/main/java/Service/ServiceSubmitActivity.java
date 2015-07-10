package Service;

import dao.BaseDao;
import dao.DaoFactory;
import dao.TestBaseDao;
import model.ActivityModel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
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
public class ServiceSubmitActivity {
    @Autowired
    private TestBaseDao testBaseDao;

    public String submitActivity( HttpServletRequest request, HttpServletResponse response ){
        ActivityModel activityModel = createProject(request );
        return "hahha";
    }

    private ActivityModel createProject( HttpServletRequest request ){

        ActivityModel activityModel = new ActivityModel();
        activityModel.setActivityIntroduce("fda;fsdk");

        testBaseDao.save(activityModel);

        return activityModel;
    }
}
