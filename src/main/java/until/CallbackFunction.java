package until;

/**
 * Created by liumin on 15/7/14.
 */
public interface CallbackFunction {

    void callback() throws Exception;

    void callback( Object object ) throws Exception;
}
