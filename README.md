Scene Recon
==========

Scene Recon aims to guide users to take a suitable set of pictures 
in a scene such that it is possible to reconstruct a 3D model of that scene.

The original author ([Andrew Orobator](https://github.com/AOrobator)) has
logged his findings [here](RESEARCH_LOG.md).

To build this gradle project, please follow these instructions.
<ol>
<li>Clone this repository and change to the cloned directory.</li>
<li>Create a file by name <code>local.properties</code> and add the line <code>sdk.dir=&lt;/path/to/android/sdk&gt;</code> after replacing the token with the path to the android sdk in your file system.</li>
<li>Run <code>./gradlew :SceneReconAndroidApp:assemble</code> on UNIX based systems or <code>gradlew.bat :SceneReconAndroidApp:assemble</code> on Windows based systems.</li>
<li>Change to the directory <code>SceneReconAndroidApp/build/outputs/apk</code>.</li>
<li>Connect an android device to the usb port.</li>
<li>Run <code>adb install SceneReconAndroidApp-debug.apk</code></li>
<li>Run the app titled <code>Scene Recon</code> in your android device.</li>
</ol>
