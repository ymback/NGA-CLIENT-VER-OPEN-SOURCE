NGA客户端播放器
=====================

NGA客户端播放器基于VitamioMediaPlayerDemo,可作为插件或库使用

怎么用?
==========
作为库:
1. 导入视频解码库 [VitamioBundle](https://github.com/yixia/VitamioBundle).
2. 复制权限及Activity的说明(AndroidManifest.xml中)到你的项目
3. 使用.

```
    Uri uri = Uri.parse("xxxx");
    VideoActivity.openVideo(this, uri, "video title");
```

作为插件：
在你的项目中使用如下代码
```
try{
    Intent mIntent = new Intent();
    ComponentName comp = new ComponentName("gov.anzong.mediaplayer","gov.anzong.mediaplayer.ReceiveIntentActivity");
    mIntent.setComponent(comp);
    mIntent.putExtra("uri", "video url");//http://192.168.2.64/1.flv，不要转化为URI
    mIntent.putExtra("title", "video title");
    startActivity(mIntent);
}casth(Exception e){
	//TODO
}
```
