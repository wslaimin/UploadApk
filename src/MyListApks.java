import com.google.api.services.androidpublisher.AndroidPublisher;
import com.google.api.services.androidpublisher.model.Apk;
import com.google.api.services.androidpublisher.model.ApksListResponse;
import com.google.api.services.androidpublisher.model.AppEdit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by 10528 on 2018/9/18.
 */
public class MyListApks {
    private static Log log= LogFactory.getLog(MyListApks.class);

    public static void main(String[] args) {
        ApplicationConfig config=new ApplicationConfig();
        config.RESOURCES_CLIENT_SECRETS_JSON=args[0];
        config.PACKAGE_NAME=args[1];

        try {
            log.info("secrets json file:"+new File(config.RESOURCES_CLIENT_SECRETS_JSON).getCanonicalPath());
        } catch (IOException e) {
            log.error("can not found secrets json file");
            e.printStackTrace();
        }
        log.info("packageName:"+config.PACKAGE_NAME);

        try {
            AndroidPublisher publisher=MyAndroidPublisherHelper.init(config.RESOURCES_CLIENT_SECRETS_JSON,config.PACKAGE_NAME);
            AndroidPublisher.Edits edits=publisher.edits();
            AndroidPublisher.Edits.Insert editRequest = edits
                    .insert(config.PACKAGE_NAME,
                            null /** no content */);

            AppEdit appEdit = editRequest.execute();

            // Get a list of apks.
            ApksListResponse apksResponse = edits
                    .apks()
                    .list(config.PACKAGE_NAME,
                            appEdit.getId()).execute();
            for(Apk apk : apksResponse.getApks()){
                log.info(apk.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
