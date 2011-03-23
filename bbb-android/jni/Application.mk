#INSTRUCTIONS:
#To compile the mconfnative module only:
#use this APP_MODULES uncommented:
#APP_MODULES := mconfnative
#and run ./scriptAndroid.sh from the bbb-android directory

#To compile any or all modules of ffmpeg or of iva libs:
#add only the modules you want to compile to the APP_MODULES variable, separated by space 
#APP_MODULES := avutil avformat avcodec swscale thread common queue decode
#don't use the scriptAndroid.sh. Instead do this:
#run ../../android-ndk-r4-crystax/ndk-build from the bbb-android directory
#and copy the resulting .so from the libs/armeabi folder to the libsFFMPEG or to the libsIVA folder accordingly


APP_MODULES := mconfnative
#APP_MODULES := thread common queue decode
#APP_MODULES := avutil avformat avcodec swscale
