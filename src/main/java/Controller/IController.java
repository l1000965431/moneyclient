package Controller;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by liumin on 15/7/6.
 */
public interface IController {

    String getClientType( HttpServletRequest request );
    String getClientOS( HttpServletRequest request );
    int getClientVersion( HttpServletRequest request );
    String getClientToken( HttpServletRequest request );
    Long getClientTimestamp( HttpServletRequest request );
}
