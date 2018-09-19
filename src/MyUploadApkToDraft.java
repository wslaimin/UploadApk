import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.FileContent;
import com.google.api.services.androidpublisher.AndroidPublisher;
import com.google.api.services.androidpublisher.model.Apk;
import com.google.api.services.androidpublisher.model.AppEdit;
import com.google.api.services.androidpublisher.model.Track;
import com.google.api.services.androidpublisher.model.TrackRelease;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by 10528 on 2018/9/18.
 */
public class MyUploadApkToDraft {
    public static void main(String[] args) {
        try {
            AndroidPublisher publisher = MyAndroidPublisherHelper.init(ApplicationConfig.PACKAGE_NAME);
            AndroidPublisher.Edits edits = publisher.edits();
            AndroidPublisher.Edits.Insert editRequest = edits.insert(ApplicationConfig.PACKAGE_NAME, null);
            AppEdit edit = editRequest.execute();
            String editId = edit.getId();

            String apkPath = MyUploadApkToDraft.class.getResource(ApplicationConfig.APK_FILE_PATH).toURI().getPath();
            AbstractInputStreamContent apkFile = new FileContent(MyAndroidPublisherHelper.MIME_TYPE_APK, new File(apkPath));
            AndroidPublisher.Edits.Apks.Upload uploadRequest = edits.apks().upload(ApplicationConfig.PACKAGE_NAME, editId, apkFile);
            Apk apk = uploadRequest.execute();
            System.out.println(apk.getVersionCode());

            List<Long> apkVersionCodes = new ArrayList<>();
            apkVersionCodes.add(Long.valueOf(apk.getVersionCode()));
            AndroidPublisher.Edits.Tracks.Update updateRequest = edits.tracks().update(ApplicationConfig.PACKAGE_NAME, editId,
                    "production", new Track().setReleases(
                            Collections.singletonList(
                                    new TrackRelease()
                                            .setName("draft")
                                            .setVersionCodes(apkVersionCodes)
                                            .setStatus("draft"))));
            Track updatedTrack=updateRequest.execute();
            System.out.println(updatedTrack.getTrack());
            AndroidPublisher.Edits.Commit commitRequest=edits.commit(ApplicationConfig.PACKAGE_NAME,editId);
            AppEdit appEdit=commitRequest.execute();
            System.out.println(appEdit.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
