#!/bin/bash
#bash script to call ndk-build and copy the original libFFMPEG and libIVA to the libs folder
#Instructions: 
#1- ajust the NDKBUILD variable below
#2- go to the project directory and run ./scriptAndroid.sh (if it doesnt work, run "chmod +x scriptAndroid.sh" first)

#directory and file declarations
NDKBUILD="../../android-ndk-r4-crystax/ndk-build"

LIBAVCODEC="libsFFMPEG/libavcodec.so"
LIBAVFORMAT="libsFFMPEG/libavformat.so"
LIBAVUTIL="libsFFMPEG/libavutil.so"
LIBSWSCALE="libsFFMPEG/libswscale.so"
ARMEABI="libs/armeabi"
LIBSIVA="libsIVA"
LIBCOMMON="libsIVA/libcommon.so"
LIBTHREAD="libsIVA/libthread.so"
LIBSOCKETS="libsIVA/libsockets.so"
LIBQUEUE="libsIVA/libqueue.so"
LIBNET="libsIVA/libnet.so"
LIBDECODE="libsIVA/libdecode.so"

#options to the ndkbuild comand
OPTIONS="-B"

#file to log the compilation
LOGMAKE="logMake.out"

#prints trash to the console to help finding the start of the compilation		
echo "--------------------------------------------------------------------------------"
echo "--------------------------------------------------------------------------------"
echo "--------------------------------------------------------------------------------"
echo "--------------------------------------------------------------------------------"
echo "--------------------------------------------------------------------------------"

#check if directories and files exist
if [ -e $NDKBUILD ]; then
	echo "OK: $NDKBUILD found"
else 
	echo "WARNING: $NDKBUILD not found"
fi 
if [ -e $LIBAVCODEC ]; then
	echo "OK: $LIBAVCODEC found"
else 
	echo "WARNING: $LIBAVCODEC not found"
fi 
if [ -e $LIBAVFORMAT ]; then
	echo "OK: $LIBAVFORMAT found"
else 
	echo "WARNING: $LIBAVFORMAT not found"
fi 
if [ -e $LIBAVUTIL ]; then
	echo "OK: $LIBAVUTIL found"
else 
	echo "WARNING: $LIBAVUTIL not found"
fi 
if [ -e $LIBSWSCALE ]; then
	echo "OK: $LIBSWSCALE found"
else 
	echo "WARNING: $LIBSWSCALE not found"
fi 
if [ -d $ARMEABI ]; then
	echo "OK: $ARMEABI found"
else 
	echo "WARNING: $ARMEABI not found"
fi
if [ -e $LIBCOMMON ]; then
	echo "OK: $LIBCOMMON found"
else 
	echo "WARNING: $LIBCOMMON not found"
fi
if [ -e $LIBTHREAD ]; then
	echo "OK: $LIBTHREAD found"
else 
	echo "WARNING: $LIBTHREAD not found"
fi
if [ -e $LIBSOCKETS ]; then
	echo "OK: $LIBSOCKETS found"
else 
	echo "WARNING: $LIBSOCKETS not found"
fi
if [ -e $LIBQUEUE ]; then
	echo "OK: $LIBQUEUE found"
else 
	echo "WARNING: $LIBQUEUE not found"
fi
if [ -e $LIBNET ]; then
	echo "OK: $LIBNET found"
else 
	echo "WARNING: $LIBNET not found"
fi
if [ -e $LIBDECODE ]; then
	echo "OK: $LIBDECODE found"
else 
	echo "WARNING: $LIBDECODE not found"
fi

#prints the command to the screen
echo "${NDKBUILD} ${OPTIONS} > ${LOGMAKE}"

#runs the command to compile the native code
$NDKBUILD $OPTIONS > $LOGMAKE

echo "copying the .so iva files to the libs directory"
echo cp $LIBCOMMON $ARMEABI
cp $LIBCOMMON $ARMEABI
echo cp $LIBTHREAD $ARMEABI
cp $LIBTHREAD $ARMEABI
echo cp $LIBSOCKETS $ARMEABI
cp $LIBSOCKETS $ARMEABI
echo cp $LIBQUEUE $ARMEABI
cp $LIBQUEUE $ARMEABI
echo cp $LIBNET $ARMEABI
cp $LIBNET $ARMEABI
echo cp $LIBDECODE $ARMEABI
cp $LIBDECODE $ARMEABI
	
#copy the .so files to the libs directory
echo "copying the .so ffmpeg files to the libs directory"
echo cp $LIBAVCODEC $ARMEABI
cp $LIBAVCODEC $ARMEABI
echo cp $LIBAVFORMAT $ARMEABI
cp $LIBAVFORMAT $ARMEABI
echo cp $LIBAVUTIL $ARMEABI
cp $LIBAVUTIL $ARMEABI
echo cp $LIBSWSCALE $ARMEABI
cp $LIBSWSCALE $ARMEABI
