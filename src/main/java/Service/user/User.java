package Service.user;

import com.money.Service.ServiceBase;
import com.money.config.Config;
import com.money.memcach.MemCachService;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fisher on 2015/7/6.
 */
@Service("User")
public class User extends ServiceBase implements UserInterface
{

    UserBase userbase=new UserBase();

    //用户注册，判断验证码是否正确，正确则完成用户注册
    public boolean userRegister(String username,String code,String password,String userType)
    {
        //判断手机验证码是否输入正确
        if(userbase.checkTeleCode(username, code)==true)
        {
            userbase.registered(username,password,userType);
            return true;
        }

        else
            return false;


    }
    //用户注册-提交手机号，验证是否已注册，发送短信验证码
    //已注册返回2,发送验证码成功返回1,失败返回0,密码不合法返回3
    public int submitTeleNum(String username,String password)
    {
        //验证用户名是否已注册
        if(userbase.checkUserName(username)==true)
            return Config.USER_IS_REGISTER;
        else
        {
            //验证密码是否合法
            boolean passwordIsRight=userbase.passwordIsRight(password);
            if(passwordIsRight==true)
            {
                //发送手机验证码，并验证是否发送成功
                return userbase.teleCodeIsSend(username);
            }
            else
                return Config.PASSWORD_ILLEGAL;

        }
    }

    //退出登录
    public boolean quitLand(String username)
    {
        return userbase.quitTokenLand(username);
    }
    //使用用户名密码登录
    public String userLand(String username,String password)
    {
        boolean userIsExist=userbase.userIsExist(username);
        if(userIsExist==true)
        {
            String tokenData= userbase.landing(username, password);
            return tokenData;
        }
        else
            return "userName is not exist";
    }

    //用户token登陆,0登录失败，1已登录，2登录成功,3使用用户名密码登录或token不正确
    public int tokenLand(String username,String token)
    {

        //查看缓存中是否含有token,且客户端参数是否与token一样
        boolean tokenExist=userbase.isTokenExist(username, token);
        //若存在，查询用户登录状态，否则,应该使用用户名密码登录，返回3
        if(tokenExist==true)
        {
            //比对缓存token上次更新时间，判断用户是否已登录
            Long orderTime = System.currentTimeMillis();
            String time = Long.toString(orderTime);
            Long timeLong=Long.parseLong(time);
            boolean landFlag=userbase.tokenTime(username,timeLong);
            if(landFlag==true)
            {
                return Config.ALREADLAND;
            }
            else
            {
                return userbase.tokenLand(username,time);
            }
        }
        else
        {
            return Config.USEPASSWORD;
        }

    }

    //完善信息 0未登录；1，修改信息成功；2，信息不合法;3，token不一致;4,userType有问题
    public int perfectInfo(String username,String token,String info)
    {
        //查看缓存中是否含有token,且客户端参数是否与token一样
        boolean tokenExist=userbase.isTokenExist(username, token);

        if(tokenExist==true)
        {
            //比对缓存token上次更新时间，判断用户是否已登录
            Long orderTime = System.currentTimeMillis();
            String time = Long.toString(orderTime);
            Long timeLong=Long.parseLong(time);
            boolean landFlag=userbase.tokenTime(username,timeLong);
            if(landFlag==true) {
                //根据username,查找用户类型
                String userType =userbase.getUserType(username);
                if (userType == "INVESTOR")
                    return userbase.modifyInvestorInfo(username, info);
                if (userType == "BORROWER")
                    return userbase.modifyBorrowerInfo(username, info);
                else
                    return 4;
            }
            else
                return 0;
        }

        else
            return 3;
    }

    //修改信息,0未登录；1，修改信息成功；2，信息不合法;3,tooken不一致;4,userType有问题
    public int changeInfo(String userName,String token,String info)
    {
        //查看缓存中是否含有token,且客户端参数是否与token一样
        boolean tokenExist=userbase.isTokenExist(userName, token);

        if(tokenExist==true)
        {
            //比对缓存token上次更新时间，判断用户是否已登录
            Long orderTime = System.currentTimeMillis();
            String time = Long.toString(orderTime);
            Long timeLong=Long.parseLong(time);
            boolean landFlag=userbase.tokenTime(userName,timeLong);
            if(landFlag==true) {
                //根据username,查找用户类型
                String userType =userbase.getUserType(userName);
                if (userType == "INVESTOR")
                    return userbase.changeInvestorInfo(userName, info);
                if (userType == "BORROWER")
                    return userbase.changeBorrowerInfo(userName, info);
                else
                    return Config.USERTYPE_FAILED;
            }
            else
                return Config.NOT_LAND;
        }

        else
            return Config.TOKEN_FAILED;

    }

    //修改密码发送验证码 3,密码不正确;2,新密码不合法；0短信未发送成功；1成功
    public int sendPasswordCode(String userName,String password,String newPassword)
    {
        //检查旧密码是否正确
        if(userbase.checkPassWord(userName,password)==true)
        {
            //检查新密码是否合法
            boolean passwordIsRight=userbase.passwordIsRight(newPassword);
            //若发送验证码成功
            int sendSuccess=userbase.teleCodeIsSend(userName);
            if(passwordIsRight==true)
            {
                return sendSuccess;
            }
            else
                return Config.NEWPASSWORD_FAILED;
        }
        else
            return Config.PASSWORD_NOTRIGHT;
    }
    //比对验证码，修改密码
    public  boolean changPassword(String userName,String code,String newPassWord)
    {
       if(userbase.checkTeleCode(userName,code)==true) {
           boolean changeOK=userbase.changePassword(userName, newPassWord);
           return changeOK;
       }
        else
           return false;

    }
}
