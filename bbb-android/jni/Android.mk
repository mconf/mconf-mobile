#this directory variable declaration
LOCAL_PATH := $(call my-dir)

#mconfnativeshowvideo module START
include $(CLEAR_VARS)

LOCAL_MODULE    := mconfnativeshowvideo

LOCAL_SRC_FILES := \
				   mconfnative/DrawerManager.cpp \
				   mconfnative/VideoDrawer.cpp
				  
LOCAL_C_INCLUDES := $(LOCAL_PATH)/iva/thread \
					$(LOCAL_PATH)/iva/common \
					$(LOCAL_PATH)/iva/queue \
					$(LOCAL_PATH)/iva/decode
									   
LOCAL_LDLIBS += -llog \
				-lGLESv1_CM \
				$(LOCAL_PATH)/../obj/local/armeabi/libavcodec.so \
				$(LOCAL_PATH)/../obj/local/armeabi/libavformat.so \
				$(LOCAL_PATH)/../obj/local/armeabi/libavutil.so \
				$(LOCAL_PATH)/../obj/local/armeabi/libswscale.so \
				$(LOCAL_PATH)/../obj/local/armeabi/libthread.so \
				$(LOCAL_PATH)/../obj/local/armeabi/libcommon.so \
				$(LOCAL_PATH)/../obj/local/armeabi/libqueue.so \
				$(LOCAL_PATH)/../obj/local/armeabi/libdecode.so
							   
include $(BUILD_SHARED_LIBRARY)
#mconfnativeshowvideo module END

#mconfnativeencodevideo module START
include $(CLEAR_VARS)

LOCAL_MODULE    := mconfnativeencodevideo

LOCAL_SRC_FILES := \
				   mconfnative/EncoderManager.cpp \
				   mconfnative/VideoEncoder.cpp
				  
LOCAL_C_INCLUDES := $(LOCAL_PATH)/iva/thread \
					$(LOCAL_PATH)/iva/common \
					$(LOCAL_PATH)/iva/queue \
					$(LOCAL_PATH)/iva/encode
									   
LOCAL_LDLIBS += -llog \
				-lGLESv1_CM \
				$(LOCAL_PATH)/../obj/local/armeabi/libavcodec.so \
				$(LOCAL_PATH)/../obj/local/armeabi/libavformat.so \
				$(LOCAL_PATH)/../obj/local/armeabi/libavutil.so \
				$(LOCAL_PATH)/../obj/local/armeabi/libswscale.so \
				$(LOCAL_PATH)/../obj/local/armeabi/libthread.so \
				$(LOCAL_PATH)/../obj/local/armeabi/libcommon.so \
				$(LOCAL_PATH)/../obj/local/armeabi/libqueue.so \
				$(LOCAL_PATH)/../obj/local/armeabi/libencode.so
							   
include $(BUILD_SHARED_LIBRARY)
#mconfnativeencodevideo module END


#thread module
include $(CLEAR_VARS)

LOCAL_MODULE    := thread

LOCAL_CPPFLAGS	:= -std=gnu++0x

LOCAL_LDLIBS += -llog

LOCAL_C_INCLUDES := $(LOCAL_PATH)/iva/thread \
					$(LOCAL_PATH)/iva/common

LOCAL_SRC_FILES :=  \
                    iva/thread/ConditionVariable.cpp \
                    iva/thread/Interval.cpp \
                    iva/thread/Interval_linux.cpp \
                    iva/thread/Microseconds.cpp \
                    iva/thread/Milliseconds.cpp \
                    iva/thread/Mutex.cpp \
                    iva/thread/Seconds.cpp \
                    iva/thread/Thread.cpp \
                    iva/thread/Timer.cpp
			   
include $(BUILD_SHARED_LIBRARY)
#end of thread module

#common module
include $(CLEAR_VARS)

LOCAL_MODULE    := common

LOCAL_SHARED_LIBRARIES := thread
 
LOCAL_LDLIBS += -llog \
				$(LOCAL_PATH)/../obj/local/armeabi/libavcodec.so \
				$(LOCAL_PATH)/../obj/local/armeabi/libavutil.so

LOCAL_C_INCLUDES := $(LOCAL_PATH)/iva/thread \
					$(LOCAL_PATH)/iva/common

LOCAL_SRC_FILES :=  \
                    iva/common/AVConfigs.cpp \
                    iva/common/CommonColor.cpp \
                    iva/common/CommonRect.cpp \
                    iva/common/Directory.cpp \
                    iva/common/Directory_linux.cpp \
                    iva/common/ErrorController.cpp \
                    iva/common/ErrorData.cpp \
                    iva/common/ErrorStack.cpp \
                    iva/common/ErrorVector.cpp \
                    iva/common/Folders.cpp \
                    iva/common/IPV4.cpp \
                    iva/common/IvaOutBuffer.cpp \
                    iva/common/IvaOutController.cpp \
                    iva/common/IvaOutLogFile.cpp \
                    iva/common/IvaOutStream.cpp \
                    iva/common/IvaPixFmt.cpp \
                    iva/common/IvaPixFmt_linux.cpp \
                    iva/common/IvaString.cpp \
                    iva/common/IvaTime.cpp \
                    iva/common/IvaVideoFrame.cpp \
                    iva/common/Location.cpp \
                    iva/common/LogData.cpp \
                    iva/common/SysInfo_linux.cpp

include $(BUILD_SHARED_LIBRARY)
#end of common module

#queue module
include $(CLEAR_VARS)

LOCAL_MODULE    := queue

LOCAL_SHARED_LIBRARIES := thread common

LOCAL_LDLIBS += -llog

LOCAL_C_INCLUDES := $(LOCAL_PATH)/iva/thread \
					$(LOCAL_PATH)/iva/common \
					$(LOCAL_PATH)/iva/queue
					
LOCAL_SRC_FILES :=	iva/queue/queue.cpp \
					iva/queue/QueueBuffer.cpp \
					iva/queue/QueueDiscard.cpp \
					iva/queue/QueueDiscardPolicy.cpp \
					iva/queue/QueueExtraData.cpp \
					iva/queue/QueueExtraDataAudio.cpp \
					iva/queue/QueueExtraDataVideo.cpp  
								   
include $(BUILD_SHARED_LIBRARY)
#end of queue module

#decode module
include $(CLEAR_VARS)

LOCAL_MODULE    := decode

LOCAL_SHARED_LIBRARIES := thread common queue 

LOCAL_LDLIBS += -llog \
				$(LOCAL_PATH)/../obj/local/armeabi/libavcodec.so \
				$(LOCAL_PATH)/../obj/local/armeabi/libavutil.so \
				$(LOCAL_PATH)/../obj/local/armeabi/libswscale.so

LOCAL_C_INCLUDES := $(LOCAL_PATH)/iva/thread \
					$(LOCAL_PATH)/iva/common \
					$(LOCAL_PATH)/iva/queue \
					$(LOCAL_PATH)/iva/decode

LOCAL_SRC_FILES :=	iva/decode/Decode.cpp \
					iva/decode/DecodeAudio.cpp \
					iva/decode/DecodeVideo.cpp	
												   
include $(BUILD_SHARED_LIBRARY)
#end of decode module

#encode module
include $(CLEAR_VARS)

LOCAL_MODULE    := encode

LOCAL_SHARED_LIBRARIES := thread common queue

LOCAL_LDLIBS += -llog \
				$(LOCAL_PATH)/../obj/local/armeabi/libavcodec.so \
				$(LOCAL_PATH)/../obj/local/armeabi/libavutil.so \
				$(LOCAL_PATH)/../obj/local/armeabi//libavformat.so \
				$(LOCAL_PATH)/../obj/local/armeabi/libswscale.so

LOCAL_C_INCLUDES := $(LOCAL_PATH)/iva/thread \
					$(LOCAL_PATH)/iva/common \
					$(LOCAL_PATH)/iva/queue \
					$(LOCAL_PATH)/iva/encode

LOCAL_SRC_FILES :=	iva/encode/Encode.cpp \
					iva/encode/EncodeAudio.cpp \
					iva/encode/EncodeAudioParams.cpp \
					iva/encode/EncodeVideo.cpp \
					iva/encode/EncodeVideoH264Opt.cpp \
					iva/encode/EncodeVideoH264Presets.cpp \
					iva/encode/EncodeVideoParams.cpp \
					iva/encode/VideoLoader.cpp	
												   
include $(BUILD_SHARED_LIBRARY)
#end of encode module

#swscale module
include $(CLEAR_VARS)
LOCAL_MODULE    := swscale
LOCAL_ARM_MODE := arm
LOCAL_CFLAGS := -DHAVE_AV_CONFIG_H
LOCAL_SHARED_LIBRARIES := avutil
LOCAL_LDFLAGS := 
LOCAL_SRC_FILES := \
libswscale/options.c \
libswscale/rgb2rgb.c \
libswscale/swscale.c \
libswscale/utils.c \
libswscale/yuv2rgb.c \

include $(BUILD_SHARED_LIBRARY) 
#end of swscale module

#avutil module
include $(CLEAR_VARS)
LOCAL_MODULE := avutil
LOCAL_ARM_MODE := arm
LOCAL_CFLAGS := -DHAVE_AV_CONFIG_H
LOCAL_SRC_FILES := \
libavutil/adler32.c \
libavutil/aes.c \
libavutil/avstring.c \
libavutil/base64.c \
libavutil/crc.c \
libavutil/des.c \
libavutil/error.c \
libavutil/fifo.c \
libavutil/intfloat_readwrite.c \
libavutil/lfg.c \
libavutil/lls.c \
libavutil/log.c \
libavutil/lzo.c \
libavutil/mathematics.c \
libavutil/md5.c \
libavutil/mem.c \
libavutil/pixdesc.c \
libavutil/random_seed.c \
libavutil/rational.c \
libavutil/rc4.c \
libavutil/sha.c \
libavutil/tree.c \
libavutil/utils.c
include $(BUILD_SHARED_LIBRARY)
#end of avutil module

#avformat module
include $(CLEAR_VARS)
LOCAL_MODULE := avformat
LOCAL_ARM_MODE := arm
LOCAL_CFLAGS := -DHAVE_AV_CONFIG_H
LOCAL_SRC_FILES := \
libavformat/4xm.c \
libavformat/aea.c \
libavformat/aiffdec.c \
libavformat/allformats.c \
libavformat/amr.c \
libavformat/anm.c \
libavformat/apc.c \
libavformat/ape.c \
libavformat/apetag.c \
libavformat/asf.c \
libavformat/asfcrypt.c \
libavformat/asfdec.c \
libavformat/assdec.c \
libavformat/au.c \
libavformat/avi.c \
libavformat/avidec.c \
libavformat/aviobuf.c \
libavformat/avio.c \
libavformat/avlanguage.c \
libavformat/avs.c \
libavformat/bethsoftvid.c \
libavformat/bfi.c \
libavformat/bink.c \
libavformat/c93.c \
libavformat/caf.c \
libavformat/cafdec.c \
libavformat/cdg.c \
libavformat/cutils.c \
libavformat/daud.c \
libavformat/dsicin.c \
libavformat/dv.c \
libavformat/dxa.c \
libavformat/eacdata.c \
libavformat/electronicarts.c \
libavformat/ffmdec.c \
libavformat/file.c \
libavformat/filmstripdec.c \
libavformat/flacdec.c \
libavformat/flic.c \
libavformat/flvdec.c \
libavformat/gxf.c \
libavformat/id3v1.c \
libavformat/id3v2.c \
libavformat/idcin.c \
libavformat/idroq.c \
libavformat/iff.c \
libavformat/img2.c \
libavformat/ipmovie.c \
libavformat/isom.c \
libavformat/iss.c \
libavformat/iv8.c \
libavformat/lmlm4.c \
libavformat/matroska.c \
libavformat/matroskadec.c \
libavformat/metadata.c \
libavformat/metadata_compat.c \
libavformat/mm.c \
libavformat/mmf.c \
libavformat/mov.c \
libavformat/mp3.c \
libavformat/mpc8.c \
libavformat/mpc.c \
libavformat/mpeg.c \
libavformat/mpegts.c \
libavformat/msnwc_tcp.c \
libavformat/mtv.c \
libavformat/mvi.c \
libavformat/mxf.c \
libavformat/mxfdec.c \
libavformat/ncdec.c \
libavformat/nsvdec.c \
libavformat/nut.c \
libavformat/nutdec.c \
libavformat/nuv.c \
libavformat/oggdec.c \
libavformat/oggparsedirac.c \
libavformat/oggparseflac.c \
libavformat/oggparseogm.c \
libavformat/oggparseskeleton.c \
libavformat/oggparsespeex.c \
libavformat/oggparsetheora.c \
libavformat/oggparsevorbis.c \
libavformat/oma.c \
libavformat/options.c \
libavformat/os_support.c \
libavformat/psxstr.c \
libavformat/pva.c \
libavformat/qcp.c \
libavformat/r3d.c \
libavformat/raw.c \
libavformat/riff.c \
libavformat/rl2.c \
libavformat/rm.c \
libavformat/rmdec.c \
libavformat/rpl.c \
libavformat/sdp.c \
libavformat/seek.c \
libavformat/segafilm.c \
libavformat/sierravmd.c \
libavformat/siff.c \
libavformat/smacker.c \
libavformat/sol.c \
libavformat/soxdec.c \
libavformat/swfdec.c \
libavformat/thp.c \
libavformat/tiertexseq.c \
libavformat/tmv.c \
libavformat/tta.c \
libavformat/txd.c \
libavformat/utils.c \
libavformat/vc1test.c \
libavformat/voc.c \
libavformat/vocdec.c \
libavformat/vorbiscomment.c \
libavformat/vqf.c \
libavformat/wav.c \
libavformat/wc3movie.c \
libavformat/westwood.c \
libavformat/wv.c \
libavformat/xa.c \
libavformat/yuv4mpeg.c \

LOCAL_SHARED_LIBRARIES := avutil avcodec
LOCAL_LDFLAGS := -L$(SYSROOT)/usr/lib -lz
include $(BUILD_SHARED_LIBRARY)
#end of avformat module

#avcodec module
include $(CLEAR_VARS)
LOCAL_MODULE := avcodec
LOCAL_ARM_MODE := arm
LOCAL_CFLAGS := -DHAVE_AV_CONFIG_H
LOCAL_SRC_FILES := \
libavcodec/4xm.c \
libavcodec/8bps.c \
libavcodec/8svx.c \
libavcodec/aac.c \
libavcodec/aac_ac3_parser.c \
libavcodec/aac_adtstoasc_bsf.c \
libavcodec/aac_parser.c \
libavcodec/aacsbr.c \
libavcodec/aactab.c \
libavcodec/aandcttab.c \
libavcodec/aasc.c \
libavcodec/ac3_parser.c \
libavcodec/ac3.c \
libavcodec/ac3dec_data.c \
libavcodec/ac3dec.c \
libavcodec/ac3tab.c \
libavcodec/acelp_filters.c \
libavcodec/acelp_pitch_delay.c \
libavcodec/acelp_vectors.c \
libavcodec/adpcm.c \
libavcodec/adxdec.c \
libavcodec/alac.c \
libavcodec/allcodecs.c \
libavcodec/alsdec.c \
libavcodec/amrnbdec.c \
libavcodec/anm.c \
libavcodec/apedec.c \
libavcodec/arm/dsputil_arm.S \
libavcodec/arm/dsputil_init_arm.c \
libavcodec/arm/dsputil_init_armv5te.c \
libavcodec/arm/fft_init_arm.c \
libavcodec/arm/h264dsp_init_arm.c \
libavcodec/arm/h264pred_init_arm.c \
libavcodec/arm/jrevdct_arm.S \
libavcodec/arm/mpegvideo_arm.c \
libavcodec/arm/mpegvideo_armv5te_s.S \
libavcodec/arm/mpegvideo_armv5te.c \
libavcodec/arm/simple_idct_arm.S \
libavcodec/arm/simple_idct_armv5te.S \
libavcodec/asv1.c \
libavcodec/atrac.c \
libavcodec/atrac1.c \
libavcodec/atrac3.c \
libavcodec/audioconvert.c \
libavcodec/aura.c \
libavcodec/avfft.c \
libavcodec/avpacket.c \
libavcodec/avs.c \
libavcodec/bethsoftvideo.c \
libavcodec/bfi.c \
libavcodec/bgmc.c \
libavcodec/bink.c \
libavcodec/binkaudio.c \
libavcodec/binkidct.c \
libavcodec/bitstream_filter.c \
libavcodec/bitstream.c \
libavcodec/bmp.c \
libavcodec/c93.c \
libavcodec/cabac.c \
libavcodec/cavs_parser.c \
libavcodec/cavs.c \
libavcodec/cavsdec.c \
libavcodec/cavsdsp.c \
libavcodec/cdgraphics.c \
libavcodec/celp_filters.c \
libavcodec/celp_math.c \
libavcodec/cga_data.c \
libavcodec/cinepak.c \
libavcodec/cljr.c \
libavcodec/cook.c \
libavcodec/cscd.c \
libavcodec/cyuv.c \
libavcodec/dca_parser.c \
libavcodec/dca.c \
libavcodec/dct.c \
libavcodec/dirac_parser.c \
libavcodec/dirac.c \
libavcodec/dnxhd_parser.c \
libavcodec/dnxhddata.c \
libavcodec/dnxhddec.c \
libavcodec/dpcm.c \
libavcodec/dpx.c \
libavcodec/dsicinav.c \
libavcodec/dsputil.c \
libavcodec/dump_extradata_bsf.c \
libavcodec/dv.c \
libavcodec/dvbsub_parser.c \
libavcodec/dvbsubdec.c \
libavcodec/dvdata.c \
libavcodec/dvdsub_parser.c \
libavcodec/dvdsubdec.c \
libavcodec/dwt.c \
libavcodec/dxa.c \
libavcodec/eac3dec_data.c \
libavcodec/eac3dec.c \
libavcodec/eacmv.c \
libavcodec/eaidct.c \
libavcodec/eamad.c \
libavcodec/eatgq.c \
libavcodec/eatgv.c \
libavcodec/eatqi.c \
libavcodec/error_resilience.c \
libavcodec/escape124.c \
libavcodec/eval.c \
libavcodec/faandct.c \
libavcodec/faanidct.c \
libavcodec/faxcompr.c \
libavcodec/fft.c \
libavcodec/ffv1.c \
libavcodec/flac.c \
libavcodec/flacdata.c \
libavcodec/flacdec.c \
libavcodec/flashsv.c \
libavcodec/flicvideo.c \
libavcodec/flvdec.c \
libavcodec/flvenc.c \
libavcodec/fraps.c \
libavcodec/frwu.c \
libavcodec/g726.c \
libavcodec/gifdec.c \
libavcodec/golomb.c \
libavcodec/h261_parser.c \
libavcodec/h261.c \
libavcodec/h261dec.c \
libavcodec/h263_parser.c \
libavcodec/h263.c \
libavcodec/h263dec.c \
libavcodec/h264_cabac.c \
libavcodec/h264_cavlc.c \
libavcodec/h264_direct.c \
libavcodec/h264_loopfilter.c \
libavcodec/h264_mp4toannexb_bsf.c \
libavcodec/h264_parser.c \
libavcodec/h264_ps.c \
libavcodec/h264_refs.c \
libavcodec/h264_sei.c \
libavcodec/h264.c \
libavcodec/h264dsp.c \
libavcodec/h264idct.c \
libavcodec/h264pred.c \
libavcodec/huffman.c \
libavcodec/huffyuv.c \
libavcodec/idcinvideo.c \
libavcodec/iff.c \
libavcodec/imc.c \
libavcodec/imgconvert.c \
libavcodec/imx_dump_header_bsf.c \
libavcodec/indeo2.c \
libavcodec/indeo3.c \
libavcodec/indeo5.c \
libavcodec/intelh263dec.c \
libavcodec/interplayvideo.c \
libavcodec/intrax8.c \
libavcodec/intrax8dsp.c \
libavcodec/ituh263dec.c \
libavcodec/ituh263enc.c \
libavcodec/ivi_common.c \
libavcodec/ivi_dsp.c \
libavcodec/jfdctfst.c \
libavcodec/jfdctint.c \
libavcodec/jpegls.c \
libavcodec/jpeglsdec.c \
libavcodec/jrevdct.c \
libavcodec/kgv1dec.c \
libavcodec/kmvc.c \
libavcodec/lcldec.c \
libavcodec/loco.c \
libavcodec/lsp.c \
libavcodec/lzw.c \
libavcodec/mace.c \
libavcodec/mdct.c \
libavcodec/mdec.c \
libavcodec/mimic.c \
libavcodec/mjpeg_parser.c \
libavcodec/mjpeg.c \
libavcodec/mjpega_dump_header_bsf.c \
libavcodec/mjpegbdec.c \
libavcodec/mjpegdec.c \
libavcodec/mlp_parser.c \
libavcodec/mlp.c \
libavcodec/mlpdec.c \
libavcodec/mlpdsp.c \
libavcodec/mmvideo.c \
libavcodec/motion_est.c \
libavcodec/motionpixels.c \
libavcodec/movsub_bsf.c \
libavcodec/mp3_header_compress_bsf.c \
libavcodec/mp3_header_decompress_bsf.c \
libavcodec/mpc.c \
libavcodec/mpc7.c \
libavcodec/mpc8.c \
libavcodec/mpeg12.c \
libavcodec/mpeg12data.c \
libavcodec/mpeg4audio.c \
libavcodec/mpeg4video_parser.c \
libavcodec/mpeg4video.c \
libavcodec/mpeg4videodec.c \
libavcodec/mpeg4videoenc.c \
libavcodec/mpegaudio_parser.c \
libavcodec/mpegaudio.c \
libavcodec/mpegaudiodata.c \
libavcodec/mpegaudiodec.c \
libavcodec/mpegaudiodecheader.c \
libavcodec/mpegaudioenc.c \
libavcodec/mpegvideo_enc.c \
libavcodec/mpegvideo_parser.c \
libavcodec/mpegvideo.c \
libavcodec/msmpeg4.c \
libavcodec/msmpeg4data.c \
libavcodec/msrle.c \
libavcodec/msrledec.c \
libavcodec/msvideo1.c \
libavcodec/nellymoser.c \
libavcodec/nellymoserdec.c \
libavcodec/noise_bsf.c \
libavcodec/nuv.c \
libavcodec/opt.c \
libavcodec/options.c \
libavcodec/parser.c \
libavcodec/pcm-mpeg.c \
libavcodec/pcm.c \
libavcodec/pcx.c \
libavcodec/pgssubdec.c \
libavcodec/png.c \
libavcodec/pngdec.c \
libavcodec/pnm_parser.c \
libavcodec/pnm.c \
libavcodec/pnmdec.c \
libavcodec/ptx.c \
libavcodec/qcelpdec.c \
libavcodec/qdm2.c \
libavcodec/qdrw.c \
libavcodec/qpeg.c \
libavcodec/qtrle.c \
libavcodec/r210dec.c \
libavcodec/ra144.c \
libavcodec/ra288.c \
libavcodec/rangecoder.c \
libavcodec/ratecontrol.c \
libavcodec/raw.c \
libavcodec/rawdec.c \
libavcodec/rdft.c \
libavcodec/remove_extradata_bsf.c \
libavcodec/resample.c \
libavcodec/resample2.c \
libavcodec/rl2.c \
libavcodec/roqvideo.c \
libavcodec/roqvideodec.c \
libavcodec/rpza.c \
libavcodec/rtjpeg.c \
libavcodec/rv10.c \
libavcodec/rv30.c \
libavcodec/rv30dsp.c \
libavcodec/rv34.c \
libavcodec/rv40.c \
libavcodec/rv40dsp.c \
libavcodec/s3tc.c \
libavcodec/sgidec.c \
libavcodec/shorten.c \
libavcodec/simple_idct.c \
libavcodec/sipr.c \
libavcodec/sipr16k.c \
libavcodec/smacker.c \
libavcodec/smc.c \
libavcodec/snow.c \
libavcodec/sonic.c \
libavcodec/sp5xdec.c \
libavcodec/sunrast.c \
libavcodec/svq1.c \
libavcodec/svq1dec.c \
libavcodec/svq3.c \
libavcodec/synth_filter.c \
libavcodec/targa.c \
libavcodec/tiertexseqv.c \
libavcodec/tiff.c \
libavcodec/tmv.c \
libavcodec/truemotion1.c \
libavcodec/truemotion2.c \
libavcodec/truespeech.c \
libavcodec/tscc.c \
libavcodec/tta.c \
libavcodec/twinvq.c \
libavcodec/txd.c \
libavcodec/ulti.c \
libavcodec/utils.c \
libavcodec/v210dec.c \
libavcodec/v210x.c \
libavcodec/vb.c \
libavcodec/vc1_parser.c \
libavcodec/vc1.c \
libavcodec/vc1data.c \
libavcodec/vc1dec.c \
libavcodec/vc1dsp.c \
libavcodec/vcr1.c \
libavcodec/vmdav.c \
libavcodec/vmnc.c \
libavcodec/vorbis_data.c \
libavcodec/vorbis_dec.c \
libavcodec/vorbis.c \
libavcodec/vp3_parser.c \
libavcodec/vp3.c \
libavcodec/vp3dsp.c \
libavcodec/vp5.c \
libavcodec/vp56.c \
libavcodec/vp56data.c \
libavcodec/vp6.c \
libavcodec/vp6dsp.c \
libavcodec/vqavideo.c \
libavcodec/wavpack.c \
libavcodec/wma.c \
libavcodec/wmadec.c \
libavcodec/wmaprodec.c \
libavcodec/wmavoice.c \
libavcodec/wmv2.c \
libavcodec/wmv2dec.c \
libavcodec/wnv1.c \
libavcodec/ws-snd1.c \
libavcodec/xan.c \
libavcodec/xiph.c \
libavcodec/xl.c \
libavcodec/xsubdec.c \
libavcodec/zmbv.c
LOCAL_SHARED_LIBRARIES := avutil
LOCAL_LDFLAGS := -L$(SYSROOT)/usr/lib -lz -lm
include $(BUILD_SHARED_LIBRARY)
#end of avcodec module

SPEEX	:= speex-1.2rc1
SILK     := silk

include $(CLEAR_VARS)
LOCAL_MODULE    := OSNetworkSystem
LOCAL_SRC_FILES := OSNetworkSystem.cpp
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := speex_jni
LOCAL_SRC_FILES := speex_jni.cpp \
		$(SPEEX)/libspeex/speex.c \
		$(SPEEX)/libspeex/speex_callbacks.c \
		$(SPEEX)/libspeex/bits.c \
		$(SPEEX)/libspeex/modes.c \
		$(SPEEX)/libspeex/nb_celp.c \
		$(SPEEX)/libspeex/exc_20_32_table.c \
		$(SPEEX)/libspeex/exc_5_256_table.c \
		$(SPEEX)/libspeex/exc_5_64_table.c \
		$(SPEEX)/libspeex/exc_8_128_table.c \
		$(SPEEX)/libspeex/exc_10_32_table.c \
		$(SPEEX)/libspeex/exc_10_16_table.c \
		$(SPEEX)/libspeex/filters.c \
		$(SPEEX)/libspeex/quant_lsp.c \
		$(SPEEX)/libspeex/ltp.c \
		$(SPEEX)/libspeex/lpc.c \
		$(SPEEX)/libspeex/lsp.c \
		$(SPEEX)/libspeex/vbr.c \
		$(SPEEX)/libspeex/gain_table.c \
		$(SPEEX)/libspeex/gain_table_lbr.c \
		$(SPEEX)/libspeex/lsp_tables_nb.c \
		$(SPEEX)/libspeex/cb_search.c \
		$(SPEEX)/libspeex/vq.c \
		$(SPEEX)/libspeex/window.c \
		$(SPEEX)/libspeex/high_lsp_tables.c

LOCAL_C_INCLUDES += 
LOCAL_CFLAGS = -DFIXED_POINT -DEXPORT="" -UHAVE_CONFIG_H -I$(LOCAL_PATH)/$(SPEEX)/include
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
BV16     := bx16_fixedp
LOCAL_MODULE    := bv16_jni
LOCAL_SRC_FILES := bv16_jni.cpp \
	$(BV16)/bvcommon/a2lsp.c \
	$(BV16)/bvcommon/allpole.c \
	$(BV16)/bvcommon/allzero.c  \
	$(BV16)/bvcommon/autocor.c \
	$(BV16)/bvcommon/basop32.c \
	$(BV16)/bvcommon/cmtables.c \
	$(BV16)/bvcommon/levdur.c \
	$(BV16)/bvcommon/lsp2a.c \
	$(BV16)/bvcommon/mathtables.c \
	$(BV16)/bvcommon/mathutil.c \
	$(BV16)/bvcommon/memutil.c \
	$(BV16)/bvcommon/ptdec.c \
	$(BV16)/bvcommon/stblzlsp.c \
	$(BV16)/bvcommon/utility.c \
	$(BV16)/bvcommon/vqdecode.c \
	$(BV16)/bv16/bitpack.c \
	$(BV16)/bv16/bv.c \
	$(BV16)/bv16/coarptch.c \
	$(BV16)/bv16/decoder.c \
	$(BV16)/bv16/encoder.c \
	$(BV16)/bv16/excdec.c \
	$(BV16)/bv16/excquan.c \
	$(BV16)/bv16/fineptch.c \
	$(BV16)/bv16/g192.c \
	$(BV16)/bv16/gaindec.c \
	$(BV16)/bv16/gainquan.c \
	$(BV16)/bv16/levelest.c \
	$(BV16)/bv16/lspdec.c \
	$(BV16)/bv16/lspquan.c \
	$(BV16)/bv16/plc.c \
	$(BV16)/bv16/postfilt.c \
	$(BV16)/bv16/preproc.c \
	$(BV16)/bv16/ptquan.c \
	$(BV16)/bv16/tables.c 
	
LOCAL_ARM_MODE := arm
LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog
LOCAL_C_INCLUDES += $(LOCAL_PATH)/$(BV16)/bvcommon $(LOCAL_PATH)/$(BV16)/bv16 $(LOCAL_PATH)/$(BV16)
#LOCAL_CFLAGS = -O3 -marm -march=armv6 -mtune=arm1136j-s -DWMOPS=0 -DG192BITSTREAM=0
#LOCAL_CFLAGS = -O3 -DWMOPS=0 -DG192BITSTREAM=0
include $(BUILD_SHARED_LIBRARY)


SILK     := silk
LOCAL_MODULE    := silkcommon
LOCAL_MODULE_FILENAME   := silkcommon
LOCAL_SRC_FILES :=  $(SILK)/src/SKP_Silk_A2NLSF.c \
	$(SILK)/src/SKP_Silk_CNG.c \
	$(SILK)/src/SKP_Silk_HP_variable_cutoff_FIX.c \
	$(SILK)/src/SKP_Silk_LBRR_reset.c \
	$(SILK)/src/SKP_Silk_LPC_inv_pred_gain.c \
	$(SILK)/src/SKP_Silk_LPC_stabilize.c \
	$(SILK)/src/SKP_Silk_LPC_synthesis_filter.c \
	$(SILK)/src/SKP_Silk_LPC_synthesis_order16.c \
	$(SILK)/src/SKP_Silk_LP_variable_cutoff.c \
	$(SILK)/src/SKP_Silk_LSF_cos_table.c \
	$(SILK)/src/SKP_Silk_LTP_analysis_filter_FIX.c \
	$(SILK)/src/SKP_Silk_LTP_scale_ctrl_FIX.c \
	$(SILK)/src/SKP_Silk_MA.c \
	$(SILK)/src/SKP_Silk_NLSF2A.c \
	$(SILK)/src/SKP_Silk_NLSF2A_stable.c \
	$(SILK)/src/SKP_Silk_NLSF_MSVQ_decode.c \
	$(SILK)/src/SKP_Silk_NLSF_MSVQ_encode_FIX.c \
	$(SILK)/src/SKP_Silk_NLSF_VQ_rate_distortion_FIX.c \
	$(SILK)/src/SKP_Silk_NLSF_VQ_sum_error_FIX.c \
	$(SILK)/src/SKP_Silk_NLSF_VQ_weights_laroia.c \
	$(SILK)/src/SKP_Silk_NLSF_stabilize.c \
	$(SILK)/src/SKP_Silk_NSQ.c \
	$(SILK)/src/SKP_Silk_NSQ_del_dec.c \
	$(SILK)/src/SKP_Silk_PLC.c \
	$(SILK)/src/SKP_Silk_VAD.c \
	$(SILK)/src/SKP_Silk_VQ_nearest_neighbor_FIX.c \
	$(SILK)/src/SKP_Silk_allpass_int.c \
	$(SILK)/src/SKP_Silk_ana_filt_bank_1.c \
	$(SILK)/src/SKP_Silk_apply_sine_window.c \
	$(SILK)/src/SKP_Silk_array_maxabs.c \
	$(SILK)/src/SKP_Silk_autocorr.c \
	$(SILK)/src/SKP_Silk_biquad.c \
	$(SILK)/src/SKP_Silk_biquad_alt.c \
	$(SILK)/src/SKP_Silk_burg_modified.c \
	$(SILK)/src/SKP_Silk_bwexpander.c \
	$(SILK)/src/SKP_Silk_bwexpander_32.c \
	$(SILK)/src/SKP_Silk_code_signs.c \
	$(SILK)/src/SKP_Silk_control_codec_FIX.c \
	$(SILK)/src/SKP_Silk_corrMatrix_FIX.c \
	$(SILK)/src/SKP_Silk_create_init_destroy.c \
	$(SILK)/src/SKP_Silk_dec_API.c \
	$(SILK)/src/SKP_Silk_decode_core.c \
	$(SILK)/src/SKP_Silk_decode_frame.c \
	$(SILK)/src/SKP_Silk_decode_indices_v4.c \
	$(SILK)/src/SKP_Silk_decode_parameters.c \
	$(SILK)/src/SKP_Silk_decode_parameters_v4.c \
	$(SILK)/src/SKP_Silk_decode_pulses.c \
	$(SILK)/src/SKP_Silk_decoder_set_fs.c \
	$(SILK)/src/SKP_Silk_detect_SWB_input.c \
	$(SILK)/src/SKP_Silk_enc_API.c \
	$(SILK)/src/SKP_Silk_encode_frame_FIX.c \
	$(SILK)/src/SKP_Silk_encode_parameters.c \
	$(SILK)/src/SKP_Silk_encode_parameters_v4.c \
	$(SILK)/src/SKP_Silk_encode_pulses.c \
	$(SILK)/src/SKP_Silk_find_LPC_FIX.c \
	$(SILK)/src/SKP_Silk_find_LTP_FIX.c \
	$(SILK)/src/SKP_Silk_find_pitch_lags_FIX.c \
	$(SILK)/src/SKP_Silk_find_pred_coefs_FIX.c \
	$(SILK)/src/SKP_Silk_gain_quant.c \
	$(SILK)/src/SKP_Silk_init_encoder_FIX.c \
	$(SILK)/src/SKP_Silk_inner_prod_aligned.c \
	$(SILK)/src/SKP_Silk_interpolate.c \
	$(SILK)/src/SKP_Silk_k2a.c \
	$(SILK)/src/SKP_Silk_k2a_Q16.c \
	$(SILK)/src/SKP_Silk_lin2log.c \
	$(SILK)/src/SKP_Silk_log2lin.c \
	$(SILK)/src/SKP_Silk_lowpass_int.c \
	$(SILK)/src/SKP_Silk_lowpass_short.c \
	$(SILK)/src/SKP_Silk_noise_shape_analysis_FIX.c \
	$(SILK)/src/SKP_Silk_pitch_analysis_core.c \
	$(SILK)/src/SKP_Silk_pitch_est_tables.c \
	$(SILK)/src/SKP_Silk_prefilter_FIX.c \
	$(SILK)/src/SKP_Silk_process_NLSFs_FIX.c \
	$(SILK)/src/SKP_Silk_process_gains_FIX.c \
	$(SILK)/src/SKP_Silk_pulses_to_bytes.c \
	$(SILK)/src/SKP_Silk_quant_LTP_gains_FIX.c \
	$(SILK)/src/SKP_Silk_range_coder.c \
	$(SILK)/src/SKP_Silk_regularize_correlations_FIX.c \
	$(SILK)/src/SKP_Silk_resample_1_2.c \
	$(SILK)/src/SKP_Silk_resample_1_2_coarse.c \
	$(SILK)/src/SKP_Silk_resample_1_2_coarsest.c \
	$(SILK)/src/SKP_Silk_resample_1_3.c \
	$(SILK)/src/SKP_Silk_resample_2_1_coarse.c \
	$(SILK)/src/SKP_Silk_resample_2_3.c \
	$(SILK)/src/SKP_Silk_resample_2_3_coarse.c \
	$(SILK)/src/SKP_Silk_resample_2_3_coarsest.c \
	$(SILK)/src/SKP_Silk_resample_2_3_rom.c \
	$(SILK)/src/SKP_Silk_resample_3_1.c \
	$(SILK)/src/SKP_Silk_resample_3_2.c \
	$(SILK)/src/SKP_Silk_resample_3_2_rom.c \
	$(SILK)/src/SKP_Silk_resample_3_4.c \
	$(SILK)/src/SKP_Silk_resample_4_3.c \
	$(SILK)/src/SKP_Silk_residual_energy16_FIX.c \
	$(SILK)/src/SKP_Silk_residual_energy_FIX.c \
	$(SILK)/src/SKP_Silk_scale_copy_vector16.c \
	$(SILK)/src/SKP_Silk_scale_vector.c \
	$(SILK)/src/SKP_Silk_schur.c \
	$(SILK)/src/SKP_Silk_schur64.c \
	$(SILK)/src/SKP_Silk_shell_coder.c \
	$(SILK)/src/SKP_Silk_sigm_Q15.c \
	$(SILK)/src/SKP_Silk_solve_LS_FIX.c \
	$(SILK)/src/SKP_Silk_sort.c \
	$(SILK)/src/SKP_Silk_sum_sqr_shift.c \
	$(SILK)/src/SKP_Silk_tables_LTP.c \
	$(SILK)/src/SKP_Silk_tables_NLSF_CB0_10.c \
	$(SILK)/src/SKP_Silk_tables_NLSF_CB0_16.c \
	$(SILK)/src/SKP_Silk_tables_NLSF_CB1_10.c \
	$(SILK)/src/SKP_Silk_tables_NLSF_CB1_16.c \
	$(SILK)/src/SKP_Silk_tables_gain.c \
	$(SILK)/src/SKP_Silk_tables_other.c \
	$(SILK)/src/SKP_Silk_tables_pitch_lag.c \
	$(SILK)/src/SKP_Silk_tables_pulses_per_block.c \
	$(SILK)/src/SKP_Silk_tables_sign.c \
	$(SILK)/src/SKP_Silk_tables_type_offset.c
	
LOCAL_ARM_MODE := arm
LOCAL_CFLAGS = -O3 
LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog
LOCAL_C_INCLUDES += $(LOCAL_PATH)/$(SILK)/src $(LOCAL_PATH)/$(SILK)/interface
include $(BUILD_STATIC_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE    := silk8_jni
LOCAL_SRC_FILES := silk8_jni.cpp 
LOCAL_C_INCLUDES += $(LOCAL_PATH)/$(SILK)/src $(LOCAL_PATH)/$(SILK)/interface
LOCAL_CFLAGS = -O3 
LOCAL_STATIC_LIBRARIES :=  silkcommon
LOCAL_ARM_MODE := arm
LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := silk16_jni
LOCAL_SRC_FILES := silk16_jni.cpp 
LOCAL_C_INCLUDES += $(LOCAL_PATH)/$(SILK)/src $(LOCAL_PATH)/$(SILK)/interface
LOCAL_CFLAGS = -O3 
LOCAL_STATIC_LIBRARIES :=  silkcommon
LOCAL_ARM_MODE := arm
LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := silk24_jni
LOCAL_SRC_FILES := silk24_jni.cpp 
LOCAL_C_INCLUDES += $(LOCAL_PATH)/$(SILK)/src $(LOCAL_PATH)/$(SILK)/interface
LOCAL_CFLAGS = -O3 
LOCAL_STATIC_LIBRARIES :=  silkcommon
LOCAL_ARM_MODE := arm
LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
SPANDSP     := spandsp
LOCAL_MODULE    := g722_jni
LOCAL_SRC_FILES := g722_jni.cpp \
	$(SPANDSP)/g722.c \
	$(SPANDSP)/vector_int.c
LOCAL_ARM_MODE := arm
LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog
LOCAL_C_INCLUDES += $(LOCAL_PATH)/$(SPANDSP)/spandsp $(LOCAL_PATH)/$(SPANDSP)
LOCAL_CFLAGS = -O3
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
SPANDSP     := spandsp
LOCAL_MODULE    := gsm_jni
LOCAL_SRC_FILES := gsm_jni.cpp \
	$(SPANDSP)/gsm0610_decode.c \
	$(SPANDSP)/gsm0610_encode.c \
	$(SPANDSP)/gsm0610_lpc.c \
	$(SPANDSP)/gsm0610_preprocess.c \
	$(SPANDSP)/gsm0610_rpe.c \
	$(SPANDSP)/gsm0610_short_term.c \
	$(SPANDSP)/gsm0610_long_term.c
LOCAL_ARM_MODE := arm
LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog
LOCAL_C_INCLUDES += $(LOCAL_PATH)/$(SPANDSP)/spandsp $(LOCAL_PATH)/$(SPANDSP)
LOCAL_CFLAGS = -O3
include $(BUILD_SHARED_LIBRARY)