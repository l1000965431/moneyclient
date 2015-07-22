package com.money.dao.userDAO;

import com.cloopen.rest.sdk.CCPRestSmsSDK;
import com.money.Service.user.Token;
import com.money.Service.user.UserBaseInterface;
import com.money.config.Config;
import com.money.dao.BaseDao;
import com.money.memcach.MemCachService;
import com.money.model.UserBorrowModel;
import com.money.model.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import until.GsonUntil;
import until.MoneyServerMd5Utils;
import until.MoneySeverRandom;
import java.util.*;

/**
 * Created by fisher on 2015/7/9.
 */
    @Repository
    public class UserDAO extends BaseDao implements UserBaseInterface {
    private static Logger logger = LoggerFactory.getLogger(UserDAO.class);
    static MoneyServerMd5Utils md5=new MoneyServerMd5Utils();
    Token moneyServerToken=new Token();



    //投资者完善个人信息,1，修改信息成功；2，信息不合法
    public int modifyInvestorInfo(String userName,String info)
    {
        //将信息转换为map形式
        Map<String,String> map = new HashMap<String, String>();
        map = GsonUntil.jsonToJavaClass(info, map.getClass());
        //有空信息标志位
        boolean infoFlag=true;
        //获取MAP的第一个值，开始遍历，判断信息是否为空
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            String value=entry.getValue();
            if(value==null)
                infoFlag=false;

        }
        //查看用户昵称是否合法
        String user=map.get("user");
        boolean userIsRight=userIsRight(user);

        if((userIsRight==true)&&(infoFlag==true))
        {
            //写数据库信息
            writeInfo(userName,map);
            return Config.MODIFYINFO_SUCCESS;
        }
        else
            return Config.MODIFYINFO_FAILED;

    }

    //借贷者完善个人信息
    public int  modifyBorrowerInfo(String userName,String info)
    {
        //将信息转换为map形式
        Map<String,String> map = new HashMap<String, String>();
        map = GsonUntil.jsonToJavaClass(info, map.getClass());
        //有空信息标志位
        boolean infoFlag=true;
        //获取MAP的第一个值，开始遍历，判断信息是否为空
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            String value=entry.getValue();
            if(value==null)
                infoFlag=false;

        }
        //查看用户昵称是否合法
        String user=map.get("user");
        boolean userIsRight=userIsRight(user);

        if((userIsRight==true)&&(infoFlag==true))
        {
            //写数据库信息
            writeBorrowInfo(userName, map);
            return Config.MODIFYINFO_SUCCESS;
        }
        else
            return Config.MODIFYINFO_FAILED;
    }

    //投资者修改个人信息
    public int changeInvestorInfo(String userName,String info)
    {
        //将信息转换为map形式
        Map<String,String> map = new HashMap<String, String>();
        map = GsonUntil.jsonToJavaClass(info, map.getClass());
        //有空信息标志位
        boolean infoFlag=true;
        //获取MAP的第一个值，开始遍历，判断信息是否为空
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            String value=entry.getValue();
            if(value==null)
                infoFlag=false;

        }
        //查看用户昵称是否合法
        String user=map.get("user");
        boolean userIsRight=userIsRight(user);

        if((userIsRight==true)&&(infoFlag==true))
        {
            //写数据库信息
            writeInfo(userName, map);
            return Config.MODIFYINFO_SUCCESS;
        }
        else
            return Config.MODIFYINFO_FAILED;
    }

    //借贷者修改个人信息
    public int changeBorrowerInfo(String userName,String info)
    {
        //将信息转换为map形式
        Map<String,String> map = new HashMap<String, String>();
        map = GsonUntil.jsonToJavaClass(info, map.getClass());
        //有空信息标志位
        boolean infoFlag=true;
        //获取MAP的第一个值，开始遍历，判断信息是否为空
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            String value=entry.getValue();
            if(value==null)
                infoFlag=false;

        }
        //查看用户昵称是否合法
        String user=map.get("user");
        boolean userIsRight=userIsRight(user);

        if((userIsRight==true)&&(infoFlag==true))
        {
            //写数据库信息
            writeBorrowInfo(userName, map);
            return Config.MODIFYINFO_SUCCESS;
        }
        else
            return Config.MODIFYINFO_FAILED;
    }

    //修改密码
    public boolean changePassword(String userName,String newPassWord) {
        UserModel userModel = (UserModel)this.load(UserModel.class, userName);
        userModel.setPassword(newPassWord);
        this.save(userModel);

        if(userModel.getPassword()==newPassWord)
            return true;
        else
            return false;
    }

    //注册
    public void registered(String userName,String passWord,String userType) {
        UserModel userModel=new UserModel();
        //用户注册，存入数据库
        userModel.setUserId(userName);
        String passWordHash=md5.hash( passWord );
        userModel.setPassword(passWordHash);
        userModel.setUserType(userType);
        this.save(userModel);


    }

    //验证用户名是否已注册
    public boolean checkUserName(String userName) {
        return userIsExist(userName);

    }

    //验证短信验证码是否正确
    public boolean checkTeleCode(String userName,String code)
    {
        //根据userName,寻找缓存中的code，并判断是否相等
        if(code== MemCachService.MemCachgGet(userName))
            return true;
        else
            return false;
    }

    //发送手机验证码，并验证手机短信是否发送成功 1为成功，0为失败............验证码内容待改，输出待改
    public int teleCodeIsSend(String userName) {
        HashMap<String, Object> result = null;
        //初始化SDK
        CCPRestSmsSDK restAPI = new CCPRestSmsSDK();
        //******************************注释*********************************************
        //*初始化服务器地址和端口                                                       *
        //*沙盒环境（用于应用开发调试）：restAPI.init("sandboxapp.cloopen.com", "8883");*
        //*生产环境（用户应用上线使用）：restAPI.init("app.cloopen.com", "8883");       *
        //*******************************************************************************
        restAPI.init("sandboxapp.cloopen.com", "8883");
        restAPI.setAccount("aaf98f894e7826aa014e852a878f08a6", "86d1a1277811460bb4c4e7ec7520e490");
        //******************************注释*********************************************
        //*初始化应用ID                                                                 *
        //*测试开发可使用“测试Demo”的APP ID，正式上线需要使用自己创建的应用的App ID     *
        //*应用ID的获取：登陆官网，在“应用-应用列表”，点击应用名称，看应用详情获取APP ID*
        //*******************************************************************************
        restAPI.setAppId("8a48b5514e7c2193014e852b72d405c8");
        //获取一个随机数
        int random = MoneySeverRandom.getRandomNum(1, 10000);
        String code=String.valueOf(random);
        //将发送的验证码存入缓存
        MemCachService.InsertValue(userName,code);
        result = restAPI.sendTemplateSMS(userName, "1", new String[]{code, "5"});
        if("000000".equals(result.get("statusCode"))){
            //正常返回输出data包体信息（map）
            HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
            Set<String> keySet = data.keySet();
            for(String key:keySet){
                Object object = data.get(key);
                System.out.println(key +" = "+object);
            }
            return Config.SENDCODE_SUCESS;

        }else{
            //异常返回输出错误码和错误信息
            System.out.println("错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
            return Config.SENDCODE_FAILED;
        }
    }

    //登录，查询DB
    public String landing(String userName, String passWord)
    {
        //根据用户名查找数据库中密码并解密,比对密码是否正确
        boolean passWordIsRight=checkPassWord(userName, passWord);
        //登陆成功返回true，否则false
        if(passWordIsRight==true) {
            String tokenData = moneyServerToken.creat(userName);
            Long orderTime = System.currentTimeMillis();
            String time = Long.toString(orderTime);
            Map<String, String> map = new HashMap<String, String>();
            map.put("token",tokenData);
            map.put("time",time);
            //存入缓存
            MemCachService.MemCachSetMap(userName, Config.FAILUER_TIME, map);
            return tokenData;
        }
        else
        {
            return null;
        }
    }

    //查询用户名是否存在
    public boolean userIsExist(String userName)
    {
        UserModel userModel = (UserModel)this.load(UserModel.class, userName);
        if(userModel!=null)
            return true;
        else
            return false;
    }

    //查询数据库，比对用户密码是否正确
    public boolean checkPassWord(String userName,String passWord)
    {
        UserModel userModel = (UserModel)this.load(UserModel.class, userName);
        String passWordSql=userModel.getPassword();
        String decodePassWord=md5.hash(md5.hash(passWordSql));
        if(passWord==decodePassWord)
            return true;
        else
            return false;
    }

    //登录，2成功，0失败
    public int tokenLand(String userName,String time)
    {
        MemCachService.SetMemCachMapByMapKey(userName, "time", time);
        String tokenTime=MemCachService.GetMemCachMapByMapKey(userName, "time");
        if (tokenTime!=time)
            return Config.TOKENLAND_SUCESS;
        else
            return Config.TOKENLAND_FAILED;
    }

    //根据userName查找缓存中上次token更新时间,判断是否为登录状态
    public boolean tokenTime(String userName,Long time)
    {
        String tokenTime=MemCachService.GetMemCachMapByMapKey(userName, "time");
        Long tokenUpdTime=Long.parseLong(tokenTime);
        //在登录状态
        if(time-tokenUpdTime<5)
            return true;
        else
            return false;

    }

    //查询缓存中是否有token字符串,并验证token字符串是否与客户端传来的相等
    public boolean isTokenExist(String userName,String token)
    {
        boolean tokenIsExist=MemCachService.KeyIsExists(userName);
        //若存在
        if(tokenIsExist==true) {
            String memToken=MemCachService.GetMemCachMapByMapKey(userName, "token");
            if(token==memToken)
                return true;
            else
                return false;
        }
        else
            return false;
    }
    //退出登录
    public boolean quitTokenLand(String userName)
    {
        //清楚缓存中token
        MemCachService.RemoveValue(userName);
        return true;
    }
    //获取用户类型
    public String getUserType(String userName)
    {
        UserModel userModel = (UserModel)this.load(UserModel.class, userName);
        String userType=userModel.getUserType();
        return userType;

    }

    //信息完善，写数据库信息
    private void writeInfo(String userName, Map<String,String> map)
    {

        UserModel userModel = (UserModel)this.load(UserModel.class, userName);

        String user=map.get("user");
        String mail=map.get("mail");
        String sex=map.get("sex");
        String location=map.get("location");
        String realName=map.get("realName");
        userModel.setUserName(user);
        userModel.setMail(mail);
        userModel.setSex(sex);
        userModel.setLocation(location);
        userModel.setRealName(realName);
        this.save(userModel);


    }
    private void writeBorrowInfo(String userName, Map<String,String> map)
    {
        writeInfo(userName,map);
        UserBorrowModel userBorrowModel=(UserBorrowModel)this.load(UserBorrowModel.class, userName);
        String identity=map.get("identity");
        String selfIntroduce=map.get("selfIntroduce");
        String goodAtField = map.get("goodAtField");
        String education = map.get("education");
        String personalProfile = map.get("personalProfile");
        userBorrowModel.setIdentity(identity);
        userBorrowModel.setSelfIntroduce(selfIntroduce);
        userBorrowModel.setGoodAtField(goodAtField);
        userBorrowModel.setEducation(education);
        userBorrowModel.setPersonalProfile(personalProfile);
        this.save(userBorrowModel);
    }
    //查看用户昵称是否合法
    private boolean userIsRight(String user)
    {
        byte[] temp=user.getBytes();
        boolean result=true;
        int length;
        if(temp[0]>47 && temp[0]<58){     //判断用户名第一个字符不能为数字
            result=false;
        }else{
            length=temp.length;
            if(length>5 && length<20){    //判断用户名的长度
                for(int i=0;i<temp.length;i++){
                    if(!((temp[i]>47 && temp[i]<58)||(temp[i]>64 && temp[i]<91)
                            ||(temp[i]>96 && temp[i]<123))){ //判断用户名是否在合法字符内
                        result=false;
                        break;                              //终止循环退出
                    }
                }
            }else{
                result=false;
            }
        }

        return result;
    }
    //检查登录密码是否合法
    public boolean passwordIsRight(String password)
    {
        byte[] temp=password.getBytes();
        boolean result=true;
        int length=temp.length;
            if(length>6 && length<16)
            {    //判断用户名的长度
                for(int i=0;i<temp.length;i++){
                    if(!(temp[i]>33 && temp[i]<126)){ //判断用户名是否在合法字符内
                        result=false;
                        break;                              //终止循环退出
                    }
                }
            }else
            {
                result=false;
            }


        return result;
    }




}
