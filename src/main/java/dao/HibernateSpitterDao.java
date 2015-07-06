package dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Created by fisher on 2015/6/29.
 */
public class HibernateSpitterDao implements SpitterDao
{
    private SessionFactory sessionFactory;
    private TransactionTemplate transactionTemplate;

    @Autowired
    public  HibernateSpitterDao(SessionFactory sessionFactory)
    {
        this.sessionFactory=sessionFactory;
    }
    private Session currentSession()
    {
        return sessionFactory.getCurrentSession();
    }
    public void addSpitter(Spitter spitter)
    {
        currentSession().save(spitter);
    }
    public Spitter getSpitterByID(long id)
    {
        return (Spitter)currentSession().get(Spitter.class,id);
    }
    public  void saveSpitter(Spitter spitter)
    {
        currentSession().update(spitter);

    }


    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    public TransactionTemplate getTransactionTemplate() {
        return transactionTemplate;
    }
}
