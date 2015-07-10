package dao;

import org.springframework.context.support.ApplicationObjectSupport;

/**
 * Created by happysky on 15-7-9.
 */
public class DaoFactory extends ApplicationObjectSupport {
    private static DaoFactory daoFactory;
    public DaoFactory(){
        daoFactory = this;
    }

    public static BaseDao getDao(String name){
        try {
            return (BaseDao) daoFactory.getApplicationContext().getBean(name);
        }catch ( Exception e ){
            return null;
        }
    }
}
