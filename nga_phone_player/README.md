NGA客户端播放器
=====================

NGA客户端播放器基于VitamioMediaPlayerDemo,可作为插件或库使用

怎么用?
==========
导入视频解码库 [VitamioBundle](https://github.com/yixia/VitamioBundle).
作为库:
1. 复制权限及Activity的说明(AndroidManifest.xml中)到你的项目
2. 使用.

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
}catch(Exception e){
	//TODO
}
```

# License

    Copyright 2014 Shiori Takei

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
