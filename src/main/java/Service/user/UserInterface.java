package Service.user;

/**
 * Created by lele on 2015/7/6.
 */
public interface UserInterface {

    //用户注册，判断验证码是否正确，正确则完成用户注册
    public boolean userRegister(String username,String code,String password,String userType);

    //用户注册-提交手机号，验证是否已注册，发送短信验证码
    //已注册返回2,发送验证码成功返回1,失败返回0,密码不合法返回3
    public int submitTeleNum(String username,String password);

    //退出登录
    public boolean quitLand(String username);

    //使用用户名密码登录
    public String userLand(String username,String password);

    //用户token登陆,0登录失败，1已登录，2登录成功,3使用用户名密码登录或token不正确
    public int tokenLand(String username,String token);

    //完善信息 0未登录；1，修改信息成功；2，信息不合法;3，token不一致;4,userType有问题
    public int perfectInfo(String username,String token,String info);

    //修改信息,0未登录；1，修改信息成功；2，信息不合法;3,tooken不一致;4,userType有问题
    public int changeInfo(String userName,String token,String info);

    //修改密码发送验证码 3,密码不正确;2,新密码不合法；0短信未发送成功；1成功
    public int sendPasswordCode(String userName,String password,String newPassword);

    //比对验证码，修改密码
    public  boolean changPassword(String userName,String code,String newPassWord);


}
