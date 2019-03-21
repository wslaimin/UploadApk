import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.FileContent;
import com.google.api.services.androidpublisher.AndroidPublisher;
import com.google.api.services.androidpublisher.model.Apk;
import com.google.api.services.androidpublisher.model.AppEdit;
import com.google.api.services.androidpublisher.model.Track;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.io.File;
import java.io.IOException;

/**
 * Created by 10528 on 2019/1/18.
 */
public class MyUploadApkToAlpha {
    private static Log log = LogFactory.getLog(MyUploadApkToDraft.class);

    public static void main(String[] args) {
        ApplicationConfig config = new ApplicationConfig();
        config.RESOURCES_CLIENT_SECRETS_JSON = args[0];
        config.PACKAGE_NAME = args[1];
        config.APK_PATH = args[2];
        config.APK_NAME = args[3];
        try {
            AndroidPublisher publisher = MyAndroidPublisherHelper.init(config.RESOURCES_CLIENT_SECRETS_JSON, config.PACKAGE_NAME);
            AndroidPublisher.Edits edits = publisher.edits();
            AndroidPublisher.Edits.Insert editRequest = edits.insert(config.PACKAGE_NAME, null);
            AppEdit edit = editRequest.execute();
            String editId = edit.getId();

            String apkPath = config.APK_PATH;
            AbstractInputStreamContent apkFile = new FileContent(MyAndroidPublisherHelper.MIME_TYPE_APK, new File(apkPath));
            AndroidPublisher.Edits.Apks.Upload uploadRequest = edits.apks().upload(config.PACKAGE_NAME, editId, apkFile);
            uploadRequest.execute();
            AndroidPublisher.Edits.Tracks.Update updateRequest = edits.tracks().update(config.PACKAGE_NAME, editId,
                    "alpha", new Track().setTrack("alpha"));
            updateRequest.execute();

            AndroidPublisher.Edits.Commit commitRequest = edits.commit(config.PACKAGE_NAME, editId);
            commitRequest.execute();

            log.info("UPLOAD APK SUCCESSFULLY");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("UPLOAD APK FAILED");
        }
    }
}
