#pragma once

#include <queue.h>
#include <Thread.h>
#include <QueueExtraDataVideo.h>
#include <QueueExtraDataAudio.h>

extern "C" {
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
}

class VideoLoader : private Runnable
{
private:
    queue_t* qvideo, * qaudio;
    string filename;
    bool stopthread;

public:
    void start(queue_t* qvideo, queue_t* qaudio, const string& filename)
    {
        stop();

        this->qvideo = qvideo;
        this->qaudio = qaudio;
        this->filename = filename;

        stopthread = false;
        run(true);
    }

    void stop()
    {
        if (isRunning()) {
            stopthread = true;
            join();
        }
    }

    void threadFunction()
    {
	    AVFormatContext* formatCtx;
	    AVPacket packet;
        int ret;

        av_register_all();

        ret = av_open_input_file(&formatCtx, filename.c_str(), NULL, 0, NULL);
	    if (ret != 0) {
		    printf("Erro %d ao abrir o arquivo\n", AVERROR(ret));
		    return;
	    }

	    ret = av_find_stream_info(formatCtx);
	    if (ret < 0) {
		    printf("Erro %d ao recuperar informações de stream\n", AVERROR(ret));
		    return;
	    }

        dump_format(formatCtx, 0, filename.c_str(), 0);

        QueueExtraDataVideo extraVideo;
        QueueExtraDataAudio extraAudio;

        int idVideo = -1, idAudio = -1;

        for (int i = 0; i != formatCtx->nb_streams; ++i) {
            AVStream* stream = formatCtx->streams[i];
            if (stream->codec->codec_type == AVMEDIA_TYPE_VIDEO) {
                if (idVideo == -1) {
			        idVideo = i;
                    extraVideo.setBitrate(stream->codec->bit_rate);
                    extraVideo.setCodecId(stream->codec->codec_id);
                    if (stream->r_frame_rate.den)
                        extraVideo.setFps(stream->r_frame_rate.num / stream->r_frame_rate.den);
                    extraVideo.setHeight(stream->codec->height);
                    extraVideo.setWidth(stream->codec->width);
                    extraVideo.setPixelFmt(IvaPixFmt().fromFfmpeg(stream->codec->pix_fmt));
                } else
                    Log("Mais de uma stream de vídeo encontrada - descartada");
            } else if (stream->codec->codec_type == AVMEDIA_TYPE_AUDIO) {
                if (idAudio == -1) {
			        idAudio = i;
                    extraAudio.setBitrate(stream->codec->bit_rate);
                    extraAudio.setCodecId(stream->codec->codec_id);
                } else
                    Log("Mais de uma stream de áudio encontrada - descartada");
            }
	    }

        /// calcula o bitrate
        if (idVideo != -1 
                && extraVideo.getBitrate() == 0
                && formatCtx->bit_rate > 0) {
            extraVideo.setBitrate(formatCtx->bit_rate);
            for (int i = 0; i != formatCtx->nb_streams; ++i)
                extraVideo.setBitrate(extraVideo.getBitrate() - formatCtx->streams[i]->codec->bit_rate);
        }

	    while (av_read_frame(formatCtx, &packet) >= 0) {
            uint32_t mspf = (formatCtx->streams[packet.stream_index]->time_base.num * 1000) / formatCtx->streams[packet.stream_index]->time_base.den;
            queue_t* tmpq = NULL;
            QueueExtraData* tmpextra = NULL;

            if (packet.stream_index == idVideo) {
                tmpq = qvideo;
                tmpextra = &extraVideo;
            } else if (packet.stream_index == idAudio) {
                tmpq = qaudio;
                tmpextra = &extraAudio;
            }

            if (tmpq && packet.size > 0) {
                uint8_t* buffer = (uint8_t*) queue_malloc(packet.size);
                memcpy(buffer, packet.data, packet.size);

                ret = queue_enqueue(tmpq, buffer, packet.size, mspf * packet.dts, tmpextra);
            }

		    av_free_packet(&packet);
	    }
	    av_close_input_file(formatCtx);
    }
};