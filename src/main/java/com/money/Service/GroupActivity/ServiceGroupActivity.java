package com.money.Service.GroupActivity;

import com.money.Service.ServiceBase;
import com.money.dao.GeneraDAO;
import com.money.model.ActivityGroupModel;
import com.money.model.ActivityGroupRelationshipModel;
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
        ActivityGroupRelationshipModel relationshipModel = new ActivityGroupRelationshipModel();
        relationshipModel.setGroupId( groupId );
        relationshipModel.setActivityId( activityId );
        generaDAO.save(relationshipModel);
        return "";
    }
}
