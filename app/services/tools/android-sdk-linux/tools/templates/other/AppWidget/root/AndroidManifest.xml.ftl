<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <application>

        <receiver android:name="${relativePackage}.${className}" >
            <intent-filter>
                <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
            </intent-filter>

            <meta-data
                android:name="android.appwidget.provider"
                android:resource="@xml/${class_name}_info" />
        </receiver>

    <#if configurable>
        <activity android:name="${relativePackage}.${className}ConfigureActivity" >
            <intent-filter>
                <action android:name="android.appwidget.action.APPWIDGET_CONFIGURE" />
            </intent-filter>
        </activity>
    </#if>
    </application>

</manifest>
