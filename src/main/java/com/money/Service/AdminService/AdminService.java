package com.money.Service.AdminService;

import com.money.Service.ServiceBase;
import com.money.Service.ServiceInterface;
import com.money.dao.GeneraDAO;
import com.money.model.AdministratorModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by happysky on 15-7-27.
 *
 * 后台管理员服务
 */
@Service("AdminService")
public class AdminService extends ServiceBase implements ServiceInterface {
    @Autowired
    GeneraDAO generaDAO;

    public boolean isAdminExits(String userId){
        AdministratorModel administratorModel = (AdministratorModel)generaDAO.load(AdministratorModel.class, userId);
        return !(administratorModel == null);
    }

    public String login(String userId, String userPassword){
        AdministratorModel administratorModel = (AdministratorModel)generaDAO.load(AdministratorModel.class, userId);
        if( administratorModel != null ){
            if( administratorModel.getPassword().compareTo(userPassword) == 0 ){
                return String.valueOf(administratorModel.getAdminType());
            }
            else {
                return "passwordIncorrect";
            }
        }else {
            return "userNotExist";
        }
    }

    public String addAdminstrator(String userId, String userPassword, String userName, int userType){
        if( isAdminExits(userId) ){
            return "userExisted";
        }
        AdministratorModel administratorModel = new AdministratorModel();
        administratorModel.setUserId( userId );
        administratorModel.setPassword( userPassword );
        administratorModel.setName( userName );
        administratorModel.setAdminType( userType );
        return "success";
    }
}
