#!/bin/sh

HOST_PREBUILT=~/android-ndk-r4-crystax/build/prebuilt/linux-x86/arm-eabi-4.4.0

SYSROOT=~/android-ndk-r4-crystax/build/platforms/android-5/arch-arm
			   
# shared
TARGET_CFLAGS="-I$SYSROOT/usr/include -fpic -mthumb-interwork -ffunction-sections -funwind-tables -fstack-protector -fno-short-enums"
TARGET_CFLAGS="$TARGET_CFLAGS -D__ARM_ARCH_5__ -D__ARM_ARCH_5T__ -D__ARM_ARCH_5E__ -D__ARM_ARCH_5TE__ -DANDROID"
TARGET_CFLAGS="$TARGET_CFLAGS -march=armv5te -mtune=xscale -msoft-float -O2 -fomit-frame-pointer -fstrict-aliasing -funswitch-loops -finline-limit=300"
			   
			 
TARGET_LDFLAGS="-nostdlib -Bdynamic -Wl,-rpath-link=$SYSROOT/usr/lib -L$SYSROOT/usr/lib"
TARGET_LDFLAGS="$TARGET_LDFLAGS -Wl,-T,$HOST_PREBUILT/arm-eabi/lib/ldscripts/armelf.x -Wl,-dynamic-linker,/system/bin/linker -Wl,--gc-sections -Wl,-z,nocopyreloc"
TARGET_LDFLAGS="$TARGET_LDFLAGS $SYSROOT/usr/lib/crtbegin_dynamic.o $HOST_PREBUILT/lib/gcc/arm-eabi/4.4.0/libgcc.a $SYSROOT/usr/lib/crtend_android.o -lc"
			   
			   
./configure                                 \
	--enable-shared                         \
	--disable-static                        \
	--prefix=$HOST_PREFIX                   \
	--disable-ffmpeg                        \
	--disable-ffplay                        \
	--disable-ffserver                      \
	--disable-encoders                      \
	--enable-encoder=mpeg4					\
	--enable-encoder=mp2					\
	--enable-encoder=flv					\
	--disable-muxers                        \
	--disable-devices                       \
	--disable-protocols                     \
	--enable-protocol=file                  \
	--enable-avfilter                       \
	--disable-network                       \
	--disable-mpegaudio-hp                  \
	--cross-prefix=arm-eabi-                \
	--enable-cross-compile                  \
	--sysroot=$SYSROOT                      \
	--sysinclude=$SYSROOT/usr/include            \
	--nm=$HOST_PREBUILT/bin/arm-eabi-nm      \
	--cc=$HOST_PREBUILT/bin/arm-eabi-gcc     \
	--extra-cflags="$TARGET_CFLAGS"               \
	--extra-ldflags="$TARGET_LDFLAGS"             \
	--target-os=linux                       \
	--arch=armv5te                              \
	--disable-stripping                          \
	
	
	
	
