import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.FileContent;
import com.google.api.services.androidpublisher.AndroidPublisher;
import com.google.api.services.androidpublisher.model.Apk;
import com.google.api.services.androidpublisher.model.AppEdit;
import com.google.api.services.androidpublisher.model.Track;
import com.google.api.services.androidpublisher.model.TrackRelease;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by 10528 on 2018/9/18.
 */
public class MyUploadApkToDraft {
    private static Log log= LogFactory.getLog(MyUploadApkToDraft.class);

    public static void main(String[] args) {
        ApplicationConfig config=new ApplicationConfig();
        config.RESOURCES_CLIENT_SECRETS_JSON=args[0];
        config.PACKAGE_NAME=args[1];
        config.APK_PATH=args[2];
        config.APK_NAME=args[3];

        try {
            log.info("secrets json file:"+new File(config.RESOURCES_CLIENT_SECRETS_JSON).getCanonicalPath());
        } catch (IOException e) {
            log.error("can not found secrets json file");
            e.printStackTrace();
        }
        log.info("packageName:"+config.PACKAGE_NAME);
        try {
            log.info("apk file:"+new File(config.APK_PATH).getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
            log.error("can not found apk file");
        }
        log.info("apkName:"+config.APK_NAME);

        try {
            log.info("secrets json file:"+new File(config.RESOURCES_CLIENT_SECRETS_JSON).getCanonicalPath());
        } catch (IOException e) {
            log.error("can not found secrets json file");
            e.printStackTrace();
        }
        log.info("packageName:"+config.PACKAGE_NAME);

        try {
            AndroidPublisher publisher = MyAndroidPublisherHelper.init(config.RESOURCES_CLIENT_SECRETS_JSON,config.PACKAGE_NAME);
            AndroidPublisher.Edits edits = publisher.edits();
            AndroidPublisher.Edits.Insert editRequest = edits.insert(config.PACKAGE_NAME, null);
            AppEdit edit = editRequest.execute();
            String editId = edit.getId();

            String apkPath = config.APK_PATH;
            AbstractInputStreamContent apkFile = new FileContent(MyAndroidPublisherHelper.MIME_TYPE_APK, new File(apkPath));
            AndroidPublisher.Edits.Apks.Upload uploadRequest = edits.apks().upload(config.PACKAGE_NAME, editId, apkFile);
            Apk apk = uploadRequest.execute();
            List<Long> apkVersionCodes = new ArrayList<>();
            apkVersionCodes.add(Long.valueOf(apk.getVersionCode()));
            AndroidPublisher.Edits.Tracks.Update updateRequest = edits.tracks().update(config.PACKAGE_NAME, editId,
                    "production", new Track().setReleases(
                            Collections.singletonList(
                                    new TrackRelease()
                                            .setName(config.APK_NAME)
                                            .setVersionCodes(apkVersionCodes)
                                            .setStatus("draft"))));
            updateRequest.execute();

            AndroidPublisher.Edits.Commit commitRequest=edits.commit(config.PACKAGE_NAME,editId);
            commitRequest.execute();

            log.info("upload apk successful");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
