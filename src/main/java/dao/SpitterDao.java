package dao;

/**
 * Created by fisher on 2015/6/29.
 */
public interface SpitterDao
{
    public Spitter getSpitterByID(long id);

    public  void saveSpitter(Spitter spitter);

   // public void saveSpittle(final Spittle spittle);
}
