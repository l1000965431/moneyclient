package com.money.Service.activityPreferential;

import com.google.gson.reflect.TypeToken;
import com.money.MoneyServerMQ.MoneyServerMQManager;
import com.money.MoneyServerMQ.MoneyServerMessage;
import com.money.Service.ServiceBase;
import com.money.Service.ServiceInterface;
import com.money.Service.Wallet.WalletService;
import com.money.Service.user.UserService;
import com.money.config.Config;
import com.money.config.MoneyServerMQ_Topic;
import com.money.dao.TransactionSessionCallback;
import com.money.dao.activityPreferentialDAO.ActivityPreferentialDAO;
import com.money.memcach.MemCachService;
import com.money.model.*;
import org.hibernate.Session;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import until.*;
import until.UmengPush.UMengMessage;
import until.UmengPush.UmengSendParameter;

import java.util.*;

/**
 * Created by liumin on 15/10/14.
 */

@Service("ActivityPreferentialService")
public class ActivityPreferentialService extends ServiceBase implements ServiceInterface {

    @Autowired
    ActivityPreferentialDAO activityPreferentialDAO;

    @Autowired
    WalletService walletService;

    @Autowired
    UserService userService;

    /**
     * 获得特效项目信息
     *
     * @param page    页数
     * @param findNum 每页的数量
     * @return
     */
    public String getactivityPreferentialInfo(final int page, final int findNum) {

        final List[] list = new List[1];
        activityPreferentialDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            @Override
            public boolean callback(Session session) throws Exception {
                list[0] = activityPreferentialDAO.getActivityId(page, findNum);
                return true;
            }
        });


        List<Map> listActivityinfo = new ArrayList();
        for (Object o : list[0]) {
            int ActivityId = Integer.valueOf(o.toString());
            String key = Config.PREFERENTIINFO + Integer.toString(ActivityId);
            String Json = new String(MemCachService.MemCachgGet(key.getBytes()));
            Map map = GsonUntil.jsonToJavaClass(Json, new TypeToken<Map>() {
            }.getType());
            listActivityinfo.add(map);
        }

        return GsonUntil.JavaClassToJson(listActivityinfo);
    }

    /**
     * 获得特效项目信息
     *
     * @param ActivityId  项目Id
     * @return
     */
    public String getactivityPreferentialInfo(int ActivityId) {
        return activityPreferentialDAO.GetActivityInfo( ActivityId );
    }


    /**
     * 参加单个特惠项目
     *
     * @param ActivityId
     * @param userId
     * @return
     * @throws Exception
     */
    public int JoinActivityPreferential(int ActivityId, final String userId, int userExp) throws Exception {

        if (userId == null || userId.length() == 0) {
            return -1;
        }

        String key = Config.PREFERENTIINFO + Integer.toString(ActivityId);
        String jsonInfo = new String(MemCachService.MemCachgGet(key.getBytes()));

        Map map = GsonUntil.jsonToJavaClass(jsonInfo, new TypeToken<Map>() {
        }.getType());

        if (map == null) {
            return 0;
        }

        int Exp = Integer.valueOf(map.get("userEXP").toString().replace(".0", ""));

        if (userExp < Exp) {
            return -1;
        }

        int WinningChance = Integer.valueOf(map.get("winningChance").toString().replace(".0", ""));

        String lockKey = "lock_" + userId;
        int Lines = 0;
        //加锁10S
        if (MemCachService.isExistUpdate(lockKey, "10")) {
            if (IsJoinActivityPreferential(WinningChance)) {
                byte[] data = activityPreferentialDAO.popActivityPreferentialUnBilled(ActivityId);
                if (data != null && data.length > 0) {
                    //购买成功  计算是否中奖
                    PreferentiaLotteryModel temp = GsonUntil.jsonToJavaClass( new String(data),
                            new TypeToken<PreferentiaLotteryModel>(){}.getType());
                    temp.setUserId(userId);
                    activityPreferentialDAO.pushActivityPreferentialBilled(ActivityId, temp);

                    Lines = temp.getLines();

                    final int finalLines = Lines;
                    if (Objects.equals(activityPreferentialDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
                        @Override
                        public boolean callback(Session session) throws Exception {
                            walletService.RechargeWallet(userId, finalLines);
                            return true;
                        }
                    }), Config.SENDCODE_FAILED)) {
                        activityPreferentialDAO.popActivityPreferentialBilled(ActivityId);
                        activityPreferentialDAO.pushActivityPreferentialUnBilled(ActivityId, temp);
                        return -1;
                    }

                    //设置完成
                    if (activityPreferentialDAO.IsActivityPreferentialComplete(ActivityId)) {
                        EndActivityPreferential(ActivityId);
                    }

                    Map<String, String> mapPush = new HashMap<>();
                    mapPush.put("Lines", Integer.toString(Lines));
                    mapPush.put("userId", userId);
                    mapPush.put("ActivityId", Integer.toString(ActivityId));
                    String Json = GsonUntil.JavaClassToJson(mapPush);
                    MoneyServerMQManager.SendMessage(new MoneyServerMessage(MoneyServerMQ_Topic.MONEYSERVERMQ_LOTTERVACTIVITYPREFERENTIAL_TOPIC,
                            MoneyServerMQ_Topic.MONEYSERVERMQ_LOTTERVACTIVITYPREFERENTIAL_TAG, Json, "特惠项目参加_" + userId));


                } else {
                    //奖励已发放完毕
                    UmengSendParameter umengSendParameter = new UmengSendParameter(userId, "微距竞投", "特惠项目", "特惠项目已经结束,请关注其他特惠项目", "特惠项目");
                    String Json = GsonUntil.JavaClassToJson(umengSendParameter);
                    MoneyServerMQManager.SendMessage(new MoneyServerMessage(MoneyServerMQ_Topic.MONEYSERVERMQ_PUSH_TOPIC,
                            MoneyServerMQ_Topic.MONEYSERVERMQ_PUSH_TAG, Json, "特惠项目购买结束"));
                }
            } else {
                //没有中奖
                Lines = 0;
                Map<String, String> mapUmessagebody = new HashMap<>();
                mapUmessagebody.put("ActivityId", Integer.toString(ActivityId));
                mapUmessagebody.put("Lines", Integer.toString(Lines));
                UMengMessage uMengMessage = new UMengMessage(userId, "activityPreferentialLottery", GsonUntil.JavaClassToJson(mapUmessagebody), "特惠项目中奖");

                String Json = GsonUntil.JavaClassToJson(uMengMessage);
                MoneyServerMQManager.SendMessage(new MoneyServerMessage(MoneyServerMQ_Topic.MONEYSERVERMQ_UMENGPUSHCUSTOMMESSAGE_TOPIC,
                        MoneyServerMQ_Topic.MONEYSERVERMQ_UMENGPUSHCUSTOMMESSAGE_TAG, Json, "特惠项目购中奖"));
            }
            MemCachService.unLockRedisKey(lockKey);
        } else {
            return -1;
        }

        return Lines;
    }

    public int JoinActivityPreferentialByList(List<Integer> list, String UserId, int userExp) throws Exception {


        for (Integer ActivityId : list) {
            int reLines = JoinActivityPreferential(ActivityId, UserId, userExp);
            if (reLines < 0) {
                continue;
            }
        }
        return 0;
    }

    /**
     * 是否可以参加特惠项目
     *
     * @return
     */
    public boolean IsJoinActivityPreferential(int WinningChance) {
        int RandomNum = MoneySeverRandom.getRandomNum(1, 100);

        if (RandomNum > WinningChance) {
            return false;
        } else {
            return true;
        }
    }

/*
    */
/**
 * 缓存项目
 *
 * @param ActivityId
 *//*


    public void CacheActivityPreferential(final int ActivityId) {
        activityPreferentialDAO.CacheActivityPreferentialInfo(ActivityId);
    }
*/

    /**
     * 插入一个新的特惠项目
     *
     * @param ActivityLines
     * @param StartTime
     * @param ActivityCompeleteId
     */
    public int InsertActivityPreferential(final int ActivityLines, final Date StartTime,
                                          final String ActivityCompeleteId,
                                          final String LinesEarnings,
                                          final int WinnigChance,final int UserExp) {

        final int[] ActivityId = {0};

        activityPreferentialDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            @Override
            public boolean callback(Session session) throws Exception {

                List<ActivityPreferentialModel> activityPreferentialList =
                        activityPreferentialDAO.GetActivityPreferentinalByCompeleteId( ActivityCompeleteId );

                if( activityPreferentialList.size() == 0 ){
                    ActivityVerifyCompleteModel activityVerifyCompleteModel =
                            (ActivityVerifyCompleteModel) activityPreferentialDAO.loadNoTransaction(ActivityVerifyCompleteModel.class, ActivityCompeleteId);

                    if (activityVerifyCompleteModel == null) {
                        return false;
                    }

                    ActivityId[0] = activityPreferentialDAO.InsertActivityPreferential(ActivityLines,
                            StartTime,activityVerifyCompleteModel,WinnigChance,UserExp);
                }else{
                    ActivityPreferentialModel activityPreferentialModel = activityPreferentialList.get(0);

                    if( activityPreferentialModel.getActivityState() == ActivityVerifyModel.STATUS_START_RAISE ){
                        ActivityId[0] = 0;
                        return false;
                    }

                    activityPreferentialDAO.DeleteSREarning( ActivityCompeleteId );

                    activityPreferentialModel.setActivityState( ActivityVerifyModel.STATUS_AUDITOR_WAIT_START );
                    activityPreferentialModel.setActivityStartTime(StartTime);
                    activityPreferentialModel.setWinningChance(WinnigChance);
                    activityPreferentialModel.setActivityLines(ActivityLines);
                    activityPreferentialModel.setUserEXP( UserExp );
                    activityPreferentialDAO.updateNoTransaction(activityPreferentialModel);
                    ActivityId[0] = activityPreferentialModel.getActivityId();
                }

                //小R发奖
                List<SREarningModel> LinesSREarningList = GsonUntil.jsonToJavaClass(LinesEarnings, new TypeToken<List<SREarningModel>>() {
                }.getType());

                String sql = "insert into srearning (activityStageId, activityId, earningPrice, earningType, num) values ";
                String Vaules = "";
                int rIndex = 0;
                for (SREarningModel LinesSREarning : LinesSREarningList) {
                    String temp = "('activityStageId', 'activityId', earningPrice, earningType, num),";
                    temp = temp.replace("activityStageId", "").replace("activityId", ActivityCompeleteId ).
                            replace("earningPrice", Integer.toString(LinesSREarning.getEarningPrice())).
                            replace("earningType", Integer.toString(LinesSREarning.getEarningType())).
                            replace("num", Integer.toString(LinesSREarning.getNum()));
                    Vaules += temp;
                    rIndex++;
                    if (rIndex % 50 == 0 || rIndex == LinesSREarningList.size()) {
                        Vaules = Vaules.substring(0, Vaules.length() - 1);
                        sql += Vaules;
                        String sql0 = "set @@foreign_key_checks=0; ";
                        String sql1 = "set @@foreign_key_checks=1; ";
                        activityPreferentialDAO.getNewSession().createSQLQuery(sql0).executeUpdate();
                        activityPreferentialDAO.getNewSession().createSQLQuery(sql).executeUpdate();
                        activityPreferentialDAO.getNewSession().createSQLQuery(sql1).executeUpdate();

                        sql = "insert into srearning (activityStageId, activityId, earningPrice, earningType, num) values ";
                        Vaules = "";
                    }
                }
                return true;
            }
        });

        return ActivityId[0];
    }

    /**
     * 特惠项目开始
     *
     * @param ActivityId
     */
    public void StartActivityPreferential(final int ActivityId) {
        final List<PreferentiaLotteryModel> list = new ArrayList<>();

        if( activityPreferentialDAO.IsActivityExist( ActivityId ) ){
            return;
        }

        if (Objects.equals(activityPreferentialDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            @Override
            public boolean callback(Session session) throws Exception {

                activityPreferentialDAO.SetActivityPreferentialState(ActivityId, ActivityVerifyModel.STATUS_START_RAISE);
                activityPreferentialDAO.CacheActivityPreferentialInfo(ActivityId);

                Set<SREarningModel> set = activityPreferentialDAO.getActivityPreferentialEarnings(ActivityId);

                for (SREarningModel sREarningModel : set) {
                    int peoples = sREarningModel.getNum();
                    for (long i = 0; i < peoples; ++i) {
                        PreferentiaLotteryModel preferentiaLotteryModel = new PreferentiaLotteryModel(i,sREarningModel);
                        list.add(preferentiaLotteryModel);
                    }
                }

                return true;
            }
        }), Config.SERVICE_SUCCESS)) {
            Collections.shuffle(list);

            for (PreferentiaLotteryModel temp : list) {
                activityPreferentialDAO.pushActivityPreferentialUnBilled(ActivityId, temp);
            }
        }
    }


    /**
     * 特惠项目完成
     *
     * @param ActivityId
     */
    public void EndActivityPreferential(final int ActivityId) {
        if (Objects.equals(activityPreferentialDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            @Override
            public boolean callback(Session session) throws Exception {
                activityPreferentialDAO.SetActivityPreferentialState(ActivityId, ActivityVerifyModel.STATUS_RAISE_FINISH);
                return true;
            }
        }), Config.SERVICE_SUCCESS)) {
            activityPreferentialDAO.delActivityPreferentialBilled(ActivityId);
            activityPreferentialDAO.delActivityPreferentialUnBilled(ActivityId);
            activityPreferentialDAO.SetActivityTime(ActivityId);
            activityPreferentialDAO.SetCacheActivityEnd(ActivityId);
        }
    }

    /**
     * 特惠项目发奖
     *
     * @param ActivityId
     */
    public void ActivityPreferentialLottery(final int ActivityId, final int Lines, final String UserId) throws Exception {
        activityPreferentialDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            @Override
            public boolean callback(Session session) throws Exception {

                ActivityPreferentialModel activityPreferentialModel =
                        (ActivityPreferentialModel) activityPreferentialDAO.loadNoTransaction(ActivityPreferentialModel.class, ActivityId);

                if (activityPreferentialModel == null) {
                    return false;
                }

                ActivityVerifyCompleteModel activityVerifyCompleteModel = activityPreferentialModel.getActivityVerifyCompleteModel();

                if (activityVerifyCompleteModel == null) {
                    return false;
                }

                UserEarningsModel userEarningsModel = new UserEarningsModel();
                userEarningsModel.setActivityStageId(activityVerifyCompleteModel.getActivityId());
                userEarningsModel.setUserEarningLines(Lines);
                userEarningsModel.setUserEarningsDate(MoneyServerDate.getDateCurDate());
                userEarningsModel.setUserID(UserId);
                userEarningsModel.setUserEarningsType(UserEarningsModel.ACTIVITYPREFERENTIALTYPE);
                userEarningsModel.setPurchaseType( Config.PURCHASEPREFERENTIAL );
                activityPreferentialDAO.saveNoTransaction(userEarningsModel);


                return true;
            }
        });

    }

    public int StopActivityPreferential( final String ActivityCompeleteId ){
        final ActivityPreferentialModel[] activityPreferentialModel = new ActivityPreferentialModel[1];
        activityPreferentialDAO.excuteTransactionByCallback(new TransactionSessionCallback() {
            @Override
            public boolean callback(Session session) throws Exception {
                activityPreferentialModel[0] =
                        (ActivityPreferentialModel)activityPreferentialDAO.GetActivityPreferentinalByCompeleteId( ActivityCompeleteId ).get(0);
                return true;
            }
        });

        int ActivityId = activityPreferentialModel[0].getActivityId();

        ScheduleJob job = new ScheduleJob();
        job.setJobGroup("ActivityPreferential");
        job.setJobId("ActivityPreferential" + Integer.toString(ActivityId));
        job.setJobName("QuartzActivityPreferential_" + Integer.toString(ActivityId));
        job.setDesc(Integer.toString(ActivityId));
        job.setJobStatus("1");

        try {
            QuartzUntil.getQuartzUntil().DeleteJob( job );
            return 1;
        } catch (SchedulerException e) {
            return 0;
        }

    }


    public int UnbillListNum( int ActivityId ){
        String ActivityInfo = Config.PREFERENTIUNBLLLED + Integer.toString( ActivityId );

        return (int)MemCachService.getLen( ActivityInfo.getBytes() );

    }

}
