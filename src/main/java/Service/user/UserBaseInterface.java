package Service.user;

import java.util.Map;

/**
 * Created by fisher on 2015/7/9.
 */
public interface UserBaseInterface {

    //投资者完善个人信息,1，修改信息成功；2，信息不合法
    public int modifyInvestorInfo(String userName,String info);

    //借贷者完善个人信息
    public int  modifyBorrowerInfo(String userName,String info);

    //投资者修改个人信息
    public int changeInvestorInfo(String userName,String info);

    //借贷者修改个人信息
    public int changeBorrowerInfo(String userName,String info);

    //修改密码
    public boolean changePassword(String userName,String newPassWord);

    //注册
    public void registered(String userName,String passWord,String userType);

    //验证用户名是否已注册
    public boolean checkUserName(String userName);

    //验证短信验证码是否正确
    public boolean checkTeleCode(String userName,String code);

    //发送手机验证码，并验证手机短信是否发送成功 1为成功，0为失败............验证码内容待改，输出待改
    public int teleCodeIsSend(String userName);

    //登录，查询DB
    public String landing(String userName, String passWord);

    //查询用户名是否存在
    public boolean userIsExist(String userName);

    //查询数据库，比对用户密码是否正确
    public boolean checkPassWord(String userName,String passWord);

    //登录，2成功，0失败
    public int tokenLand(String userName,String time);

    //根据userName查找缓存中上次token更新时间,判断是否为登录状态
    public boolean tokenTime(String userName,Long time);

    //查询缓存中是否有token字符串,并验证token字符串是否与客户端传来的相等
    public boolean isTokenExist(String userName,String token);

    //退出登录
    public boolean quitTokenLand(String userName);

    //获取用户类型
    public String getUserType(String userName);

    //检查登录密码是否合法
    public boolean passwordIsRight(String password);
}
