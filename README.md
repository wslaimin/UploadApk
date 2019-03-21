# 简介

上传apk到google play draft或alpha频道

# 使用

```
上传draft
java -cp android-publish.jar MyUploadApkToDraft yourApiKey.json yourPackageName yourApk.apk apkVersionName
```

```
上传到alpha
java -cp android-publish.jar MyUploadApkToAlpha yourApiKey.json yourPackageName yourApk.apk apkVersionName
```