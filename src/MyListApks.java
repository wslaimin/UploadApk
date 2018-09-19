import com.google.api.services.androidpublisher.AndroidPublisher;
import com.google.api.services.androidpublisher.model.Apk;
import com.google.api.services.androidpublisher.model.ApksListResponse;
import com.google.api.services.androidpublisher.model.AppEdit;

/**
 * Created by 10528 on 2018/9/18.
 */
public class MyListApks {
    public static void main(String[] args) {
        try {
            AndroidPublisher publisher=MyAndroidPublisherHelper.init(ApplicationConfig.PACKAGE_NAME);
            AndroidPublisher.Edits edits=publisher.edits();
            AndroidPublisher.Edits.Insert editRequest = edits
                    .insert(ApplicationConfig.PACKAGE_NAME,
                            null /** no content */);
            AppEdit appEdit = editRequest.execute();

            // Get a list of apks.
            ApksListResponse apksResponse = edits
                    .apks()
                    .list(ApplicationConfig.PACKAGE_NAME,
                            appEdit.getId()).execute();
            for(Apk apk:apksResponse.getApks()){
                System.out.println(apk);
            }

            MyUploadApkToDraft.main(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
