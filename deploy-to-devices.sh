#!/bin/bash

cd bbb-android/
ant installd
if [ $$ -ne 0 ]
then
    adb uninstall org.mconf.android.bbbandroid
    ant installd
fi

cd ../mconf-mobile/
ant installd
if [ $$ -ne 0 ]
then
    adb uninstall org.mconf.android.mconfmobile
    ant installd
fi
