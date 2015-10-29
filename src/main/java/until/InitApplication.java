package until;


import com.money.Service.GlobalConifg.GlobalConfigService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by liumin on 15/10/29.
 */
@Component
public class InitApplication implements InitializingBean {

    @Autowired
    GlobalConfigService globalConfigService;

    @Override
    public void afterPropertiesSet() throws Exception {
        if( globalConfigService == null ){
            return;
        }

        globalConfigService.initConfigVaule();
    }
}
