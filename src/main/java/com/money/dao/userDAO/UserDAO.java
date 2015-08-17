package com.money.dao.userDAO;

import com.money.Service.user.Token;
import com.money.config.Config;
import com.money.config.ServerReturnValue;
import com.money.dao.BaseDao;
import com.money.dao.TransactionCallback;
import com.money.memcach.MemCachService;
import com.money.model.UserEarningsModel;
import com.money.model.UserModel;
import com.money.model.WalletModel;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import until.Base32;
import until.GsonUntil;
import until.MoneySeverRandom;
import until.PRestSmsSDKUntil;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by fisher on 2015/7/9.
 */

@Repository
public class UserDAO extends BaseDao {

    private static Logger logger = LoggerFactory.getLogger(UserDAO.class);


    //投资者完善个人信息,1，修改信息成功；2，信息不合法
    public int modifyInvestorInfo(String userId, String info) {
        //将信息转换为map形式
        Map<String, String> map = new HashMap<String, String>();
        map = GsonUntil.jsonToJavaClass(info, map.getClass());
        //有空信息标志位
        boolean infoFlag = true;
        //获取MAP的第一个值，开始遍历，判断信息是否为空
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            String value = entry.getValue();
            if (value == null)
                infoFlag = false;
        }
        //查看用户昵称是否合法
        boolean userIsRight = userIsRight(userId);

        if ((userIsRight) && (infoFlag)) {
            //写数据库信息
            writeInfo(userId, map);
            return Config.MODIFYINFO_SUCCESS;
        } else
            return Config.MODIFYINFO_FAILED;

    }

    //投资者修改个人信息
    public int changeInvestorInfo(String userName, String info) {
        //将信息转换为map形式
        Map<String, String> map = new HashMap<String, String>();
        map = GsonUntil.jsonToJavaClass(info, map.getClass());
        //有空信息标志位
        boolean infoFlag = true;
        //获取MAP的第一个值，开始遍历，判断信息是否为空
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            String value = entry.getValue();
            if (value == null)
                infoFlag = false;

        }
        //查看用户昵称是否合法
        String user = map.get("user");
        boolean userIsRight = userIsRight(user);

        if ((userIsRight) && (infoFlag)) {
            //写数据库信息
            writeInfo(userName, map);
            return Config.MODIFYINFO_SUCCESS;
        } else
            return Config.MODIFYINFO_FAILED;
    }

    //修改密码
    public boolean changePassword(String userID, String newPassWord, String oldPassWord) {
        UserModel userModel = this.getUSerModel(userID);

        if (userModel == null) {
            return false;
        }

        String CurPassWord = userModel.getPassword();

        String decodePassword = new String(Base32.decode(CurPassWord));

        if (!oldPassWord.equals(decodePassword)) {
            return false;
        }

        userModel.setPassword(newPassWord);
        this.update(userModel);
        return true;
    }

    //注册
    public int registered(final String userID, final String passWord, final int userType) {
        if (this.excuteTransactionByCallback(new TransactionCallback() {
            public void callback(BaseDao basedao) throws Exception {
                UserModel userModel = new UserModel();
                //用户注册，存入数据库
                userModel.setUserId(userID);
                userModel.setPassword(passWord);
                userModel.setUserType(userType);
                basedao.getNewSession().save(userModel);

                if( userType == Config.INVESTOR ){
                    WalletModel walletModel = new WalletModel();
                    walletModel.setUserID( userID );
                    basedao.getNewSession().save(walletModel);

                    UserEarningsModel userEarningsModel = new UserEarningsModel();
                    userEarningsModel.setUserID( userID );
                    basedao.getNewSession().save(userEarningsModel);
                }
            }
        }).equals(Config.SERVICE_SUCCESS)) {
            return ServerReturnValue.REQISTEREDSUCCESS;
        } else {
            return ServerReturnValue.REQISTEREDFAILED;
        }
    }

    //验证用户名是否已注册
    public boolean checkUserName(String userName) {
        return userIsExist(userName);

    }

    //验证短信验证码是否正确
    public boolean checkTeleCode(String userName, String code) {
        //根据userName,寻找缓存中的code，并判断是否相等
        return true;
        /*String UserCodeName = Config.CODE + userName;
        if (code == MemCachService.MemCachgGet(UserCodeName))
            return true;
        else
            return true;*/
    }

    //发送手机验证码，并验证手机短信是否发送成功 1为成功，0为失败............验证码内容待改，输出待改
    public int teleCodeIsSend(String userName) {
        HashMap<String, Object> result = null;

        //获取一个随机数
        int random = MoneySeverRandom.getRandomNum(1, 10000);
        String code = String.valueOf(random);
        //将发送的验证码存入缓存
        String UserCodeName = Config.CODE + userName;
        MemCachService.InsertValueWithTime(UserCodeName, Config.USERCODETIME, code);
        result = PRestSmsSDKUntil.getRestAPI().sendTemplateSMS(userName, "1", new String[]{code, "5"});
        if ("000000".equals(result.get("statusCode"))) {
            //正常返回输出data包体信息（map）
            HashMap<String, Object> data = (HashMap<String, Object>) result.get("data");
            Set<String> keySet = data.keySet();
            for (String key : keySet) {
                Object object = data.get(key);
                System.out.println(key + " = " + object);
            }
            return Config.SENDCODE_SUCESS;

        } else {
            //异常返回输出错误码和错误信息
            System.out.println("错误码=" + result.get("statusCode") + " 错误信息= " + result.get("statusMsg"));
            return Config.SENDCODE_FAILED;
        }
    }

    //登录，查询DB
    public String landing(String userName, String passWord) {
        String tokenData = Token.create(userName);
        Long orderTime = System.currentTimeMillis();
        String time = Long.toString(orderTime);
        Map<String, String> map = new HashMap<String, String>();
        map.put("token", tokenData);
        map.put("time", time);
        //存入缓存
        MemCachService.MemCachSetMap(userName, Config.FAILUER_TIME, map);
        return tokenData;

    }

    //查询用户名是否存在
    public boolean userIsExist(String userID) {
        UserModel userModel = getUSerModel(userID);
        if (userModel != null)
            return true;
        else
            return false;
    }

    //查询数据库，比对用户密码是否正确
    public boolean checkPassWord(String userID, String passWord) {
        UserModel userModel = this.getUSerModel(userID);

        if( userModel == null ){
            return false;
        }

        String passWordSql = userModel.getPassword();

        String decodePassWord = new String(Base32.decode(passWordSql));

        if (passWord.equals(decodePassWord))
            return true;
        else
            return false;
    }

    //登录，2成功，0失败
    public int tokenLand(String userName, String time) {
        MemCachService.SetMemCachMapByMapKey(userName, "time", time);
        String tokenTime = MemCachService.GetMemCachMapByMapKey(userName, "time");
        if (tokenTime != time) {
            return Config.TOKENLAND_SUCESS;
        } else {
            return Config.TOKENLAND_FAILED;
        }
    }

    //根据userName查找缓存中上次token更新时间,判断是否为登录状态
    public boolean tokenTime(String userName, Long time) {
        String tokenTime = MemCachService.GetMemCachMapByMapKey(userName, "time");
        Long tokenUpdTime = Long.parseLong(tokenTime);
        //在登录状态
        if ((time - tokenUpdTime) / 1000 < 3600)
            return true;
        else
            return false;

    }

    //查询缓存中是否有token字符串,并验证token字符串是否与客户端传来的相等
    public boolean isTokenExist(String userID, String token) {
        boolean tokenIsExist = MemCachService.KeyIsExists(userID);

        Map map = MemCachService.GetMemCachMap(userID);

        //若存在
        if (tokenIsExist) {
            String memToken = MemCachService.GetMemCachMapByMapKey(userID, "token");
            if (token.equals(memToken))
                return true;
            else
                return false;
        } else
            return false;
    }

    //退出登录
    public boolean quitTokenLand(String userId) {
        //清楚缓存中token
        MemCachService.RemoveValue(userId);
        return true;
    }

    //获取用户类型
    public int getUserType(String userName) {
        UserModel userModel = this.getUSerModel(userName);
        int userType = userModel.getUserType();
        return userType;

    }

    //信息完善，写数据库信息
    private void writeInfo(String userName, Map<String, String> map) {

        UserModel userModel = this.getUSerModel(userName);

        String user = map.get("userName");
        String mail = map.get("mail");
        int sex = Integer.valueOf(map.get("sex"));
        String location = map.get("location");
        String education = map.get("education");
        String identity = map.get("identity");
        String personalProfile = map.get("personalProfile");
        String selfIntroduce = map.get("selfintroduce");
        String goodAtField = map.get("goodAtField");
        String RealName = map.get("realName");
        userModel.setUserName(user);
        userModel.setMail(mail);
        userModel.setSex(sex);
        userModel.setLocation(location);
        userModel.setRealName(RealName);
        userModel.setEduInfo(education );
        userModel.setIdentityId( identity );
        userModel.setCareer( personalProfile );
        userModel.setIntroduction( selfIntroduce );
        userModel.setExpertise( goodAtField );
        userModel.setIsPerfect(true);
        this.update(userModel);
    }

    //查看用户昵称是否合法
    public boolean userIsRight(String user) {
        if( user == null ){
            return false;
        }

        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(user);
        return m.find();
    }

    //检查登录密码是否合法
    public boolean passwordIsRight(String password) {
        if( password == null ){
            return false;
        }

        Pattern p = Pattern.compile("^[0-9a-zA-Z]{6,16}$");
        Matcher m = p.matcher(password);
        return m.find();
    }

    public UserModel getUSerModel(final String UserID) {
        final UserModel[] userModel = {null};

        this.excuteTransactionByCallback(new TransactionCallback() {
            public void callback(BaseDao basedao) throws Exception {
                userModel[0] = (UserModel) basedao.getNewSession().createCriteria(UserModel.class)
                        .setMaxResults(1)
                        .add(Restrictions.eq("userId", UserID))
                        .uniqueResult();
            }
        });

        return userModel[0];
    }
}
