package com.money.Service.GroupActivity;

import com.money.Service.ServiceBase;
import com.money.dao.GeneraDAO;
import com.money.model.ActivityGroupModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by happysky on 15-7-15.
 */
@Service("ServiceGroupActivity")
public class ServiceGroupActivity extends ServiceBase {
    @Autowired
    private GeneraDAO generaDAO;

    /**
     * 创建一个项目组
     * @param name
     * @return
     */
    public String createActivityGroup(String name){
        ActivityGroupModel activityGroup = new ActivityGroupModel();
        generaDAO.save(activityGroup);

        return "";
    }

    /**
     * 向组中加入一个项目
     * @param activityId
     * @return
     */
    public String addActivityToGroup(Long groupId, Long activityId){
        ActivityGroupModel activityGroupModel = (ActivityGroupModel)generaDAO.getSession().load(ActivityGroupModel.class, groupId);
        if( activityGroupModel == null ){
            return "";
        }
        return "";
    }

    public void test(){
//        ActivityGroupModel groupModel = new ActivityGroupModel();
//        groupModel.setName("group1");
//
//        ActivityDetailModel detailModel = new ActivityDetailModel();
//        detailModel.setName("123");
//
//        ActivityDetailModel detailModel1 = new ActivityDetailModel();
//        detailModel1.setName("456");
//
//        detailModel.setActivityGroupModel(groupModel);
//        detailModel1.setActivityGroupModel(groupModel);
//
//        groupModel.getActivityDetailModels().add(detailModel);
//        groupModel.getActivityDetailModels().add(detailModel1);
//
//        generaDAO.save(groupModel);

        ActivityGroupModel activityGroupModel = (ActivityGroupModel)generaDAO.load(ActivityGroupModel.class, 1l);
        if( activityGroupModel == null ){
            return;
        }
    }
}
