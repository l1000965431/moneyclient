package com.money.dao.activityPreferentialDAO;

import com.google.gson.reflect.TypeToken;
import com.money.config.Config;
import com.money.dao.BaseDao;
import com.money.memcach.MemCachService;
import com.money.model.*;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import until.BeanTransfersUntil;
import until.GsonUntil;
import until.MoneyServerDate;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by liumin on 15/10/14.
 */

@Repository
public class ActivityPreferentialDAO extends BaseDao {

    /**
     * 插入特惠项目
     *
     * @param ActivityLines
     * @param StatTime
     * @param activityVerifyCompleteModel
     */
    public int InsertActivityPreferential(int ActivityLines, Date StatTime,
                                          ActivityVerifyCompleteModel activityVerifyCompleteModel,
                                          int WinnigChance, int UserExp) {
        ActivityPreferentialModel activityPreferentialModel = new ActivityPreferentialModel();
        activityPreferentialModel.setActivityLines(ActivityLines);
        activityPreferentialModel.setActivityStartTime(StatTime);
        activityVerifyCompleteModel.setStatus(ActivityVerifyModel.STATUS_AUDITOR_WAIT_START);
        activityPreferentialModel.setActivityVerifyCompleteModel(activityVerifyCompleteModel);
        activityPreferentialModel.setActivityState(ActivityVerifyModel.STATUS_AUDITOR_WAIT_START);
        activityPreferentialModel.setWinningChance(WinnigChance);
        activityPreferentialModel.setUserEXP(UserExp);
        this.saveNoTransaction(activityPreferentialModel);

        return activityPreferentialModel.getActivityId();
    }

    /**
     * 缓存特惠项目信息
     *
     * @param ActitvityId
     */
    public void CacheActivityPreferentialInfo(int ActitvityId) {
        ActivityPreferentialModel activityPreferentialModel =
                (ActivityPreferentialModel) this.loadNoTransaction(ActivityPreferentialModel.class, ActitvityId);
        if (activityPreferentialModel == null) {
            return;
        }

        Map map = BeanTransfersUntil.TransBean2Map(activityPreferentialModel);
        map.remove("activityVerifyCompleteModel");

        ActivityVerifyCompleteModel activityVerifyCompleteModel = activityPreferentialModel.getActivityVerifyCompleteModel();
        if (activityVerifyCompleteModel == null) {
            return;
        }

        ActivityPreferentiaInfo activityPreferentiaInfo = new ActivityPreferentiaInfo(activityVerifyCompleteModel);
        Map mapactivty = BeanTransfersUntil.TransBean2Map(activityPreferentiaInfo);
        map.putAll(mapactivty);


        String ActivityInfoKey = Config.PREFERENTIINFO + Integer.toString(ActitvityId);
        MemCachService.MemCachSet(ActivityInfoKey.getBytes(), GsonUntil.JavaClassToJson(map).getBytes());

        String ActivityBoundsKey = Config.PREFERENTIBOUNDS + Integer.toString(ActitvityId);
        MemCachService.MemCachSet(ActivityBoundsKey.getBytes(),
                Integer.toString(activityPreferentialModel.getActivityLines()).getBytes());
    }

    /**
     * 获取缓存中项目信息
     *
     * @param ActivityId
     * @return
     */
    public Map getAcActivityPreferentialInfo(int ActivityId) {
        String ActivityInfoKey = Config.PREFERENTIINFO + Integer.toString(ActivityId);
        return MemCachService.GetMemCachMap(ActivityInfoKey);
    }

    /**
     * 获得已经分发的特惠项目奖项队列
     *
     * @param ActivityId
     * @return
     */
    public void getActivityPreferentialBilled(int ActivityId) {
        String ActivityInfoKey = Config.PREFERENTIBLLLED + Integer.toString(ActivityId);
        //return MemCachService.getRedisHash(ActivityInfoKey);
    }

    /**
     * 获得已未分发的特惠项目奖项队列
     *
     * @param ActivityId
     * @return
     */
    public void getActivityPreferentialUnBilled(int ActivityId) {
        String ActivityInfoKey = Config.PREFERENTIUNBLLLED + Integer.toString(ActivityId);
        //return MemCachService.getRedisHash( ActivityInfoKey );
    }

    /**
     * 压入已经分发的特惠项目奖项队列
     *
     * @param ActivityId
     * @param o
     */
    public void pushActivityPreferentialBilled(int ActivityId, PreferentiaLotteryModel o) {
        String ActivityInfoKey = Config.PREFERENTIBLLLED + Integer.toString(ActivityId);
        try {
            String json = GsonUntil.JavaClassToJson(o);
            MemCachService.lpush(ActivityInfoKey.getBytes(), json.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 压入未分发的特惠项目奖项队列
     *
     * @param ActivityId
     * @param o
     */
    public void pushActivityPreferentialUnBilled(int ActivityId, PreferentiaLotteryModel o) {
        String ActivityInfoKey = Config.PREFERENTIUNBLLLED + Integer.toString(ActivityId);
        try {
            String json = GsonUntil.JavaClassToJson(o);
            MemCachService.lpush(ActivityInfoKey.getBytes(), json.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从未分发的奖项队列取出一个奖项
     *
     * @param ActivityId
     */
    public byte[] popActivityPreferentialUnBilled(int ActivityId) {
        String ActivityInfoKey = Config.PREFERENTIUNBLLLED + Integer.toString(ActivityId);
        try {
            return MemCachService.lpop(ActivityInfoKey.getBytes());
        } catch (Exception e) {
            return null;
        }
    }

    public byte[] popActivityPreferentialBilled(int ActivityId) {
        String ActivityInfoKey = Config.PREFERENTIBLLLED + Integer.toString(ActivityId);
        try {
            return MemCachService.lpop(ActivityInfoKey.getBytes());
        } catch (Exception e) {
            return null;
        }
    }

    public void delActivityPreferentialUnBilled(int ActivityId) {
        String ActivityInfoKey = Config.PREFERENTIUNBLLLED + Integer.toString(ActivityId);
        MemCachService.RemoveValue(ActivityInfoKey);

    }

    public void delActivityPreferentialBilled(int ActivityId) {
        String ActivityInfoKey = Config.PREFERENTIBLLLED + Integer.toString(ActivityId);
        MemCachService.RemoveValue(ActivityInfoKey);
    }

    public boolean IsActivityPreferentialComplete(int ActivityId) {
        String ActivityInfoKey = Config.PREFERENTIUNBLLLED + Integer.toString(ActivityId);
        return MemCachService.getLen(ActivityInfoKey.getBytes()) <= 0;
    }

    public void delActivityPreferentialBounds( int ActivityId ){
        String ActivityInfoKey = Config.PREFERENTIBOUNDS + Integer.toString(ActivityId);
        MemCachService.RemoveValue(ActivityInfoKey);
    }

    public void SetActivityPreferentialState(int ActivityId, int State) {

        ActivityPreferentialModel activityPreferentialModel =
                (ActivityPreferentialModel) this.loadNoTransaction(ActivityPreferentialModel.class, ActivityId);
        if (activityPreferentialModel == null) {
            return;
        }

        activityPreferentialModel.setActivityState(State);

        if (State == ActivityVerifyModel.STATUS_RAISE_FINISH) {
            try {
                activityPreferentialModel.setActivityEndTime(MoneyServerDate.getDateCurDate());
            } catch (ParseException e) {

            }
        }


        ActivityVerifyCompleteModel activityVerifyCompleteModel = activityPreferentialModel.getActivityVerifyCompleteModel();
        if (activityVerifyCompleteModel == null) {
            return;
        }

        activityVerifyCompleteModel.setStatus(State);

        this.updateNoTransaction(activityPreferentialModel);
    }

    /**
     * 获得中奖信息
     *
     * @param ActivityId
     * @return
     */
    public Set getActivityPreferentialEarnings(int ActivityId) {
        ActivityPreferentialModel activityPreferentialModel =
                (ActivityPreferentialModel) this.loadNoTransaction(ActivityPreferentialModel.class, ActivityId);
        if (activityPreferentialModel == null) {
            return null;
        }


        ActivityVerifyCompleteModel activityVerifyCompleteModel = activityPreferentialModel.getActivityVerifyCompleteModel();
        if (activityVerifyCompleteModel == null) {
            return null;
        }

        return activityVerifyCompleteModel.getSrEarningModels();
    }

    /**
     * 查询当前活动的特惠项目Id
     *
     * @return
     */
    public List getActivityId(int page, int num) {

        String sql = "select Activityid from activitypreferential " +
                "where ActivityState = 5 or ActivityEndTime=NULL or TIMESTAMPDIFF(DAY,ActivityEndTime,now())<1 limit ?,? ;";
        Session session = this.getNewSession();

        SQLQuery sqlQuery = session.createSQLQuery(sql);
        sqlQuery.setParameter(0, page * num);
        sqlQuery.setParameter(1, num);
        return sqlQuery.list();
    }

    /**
     * 获得单个项目的信息
     *
     * @param ActivityId
     * @return
     */
    public String GetActivityInfo(int ActivityId) {
        String ActivityKey = Config.PREFERENTIINFO + Integer.toString(ActivityId);

        byte[] bytes = MemCachService.MemCachgGet(ActivityKey.getBytes());
        if( bytes == null || bytes.length == 0 ){
            return null;
        }
        return new String(bytes);
    }


    /**
     * 设置特惠项目结束消失时间
     *
     * @param ActivityId
     */
    public void SetActivityTime(int ActivityId) {
        String ActivityInfoKey = Config.PREFERENTIINFO + Integer.toString(ActivityId);
        MemCachService.SetTimeOfKey(ActivityInfoKey.getBytes(), 24 * 3600);

    }

    public boolean IsActivityExist(int ActivityId) {
        String ActivityInfoKey = Config.PREFERENTIINFO + Integer.toString(ActivityId);
        return MemCachService.KeyIsExists(ActivityInfoKey);
    }


    public void SetCacheActivityEnd(int ActivityId) {
        String ActivityInfoKey = Config.PREFERENTIINFO + Integer.toString(ActivityId);
        String json = new String(MemCachService.MemCachgGet(ActivityInfoKey.getBytes()));
        Map<String, String> map = GsonUntil.jsonToJavaClass(json, new TypeToken<Map<String, String>>() {
        }.getType());
        map.put("ActivityState", Integer.toString(ActivityVerifyModel.STATUS_RAISE_FINISH));

        String writeJson = GsonUntil.JavaClassToJson(map);
        MemCachService.MemCachSet(ActivityInfoKey.getBytes(), writeJson.getBytes());
    }

    public List GetActivityPreferentinalByCompeleteId(String CompeleteId) {
        String sql = "select * from activitypreferential where activityVerifyCompleteModel_activityId = ?;";

        Session session = this.getNewSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql).addEntity(ActivityPreferentialModel.class);
        sqlQuery.setString(0, CompeleteId);
        return sqlQuery.list();
    }


    public void DeleteSREarning(String CompeleteId) {
        String sql = "delete from srearning where activityid=?;";
        Session session = this.getNewSession();
        SQLQuery sqlQuery = session.createSQLQuery(sql);
        sqlQuery.setString(0, CompeleteId);
        sqlQuery.executeUpdate();
    }
}
