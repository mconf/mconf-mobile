#include <CommonLeaks.h>
#include <common.h>
#include "AVConfigs.h"
#include <CommonLeaksCpp.h>

AVConfigsPairResBitrate::AVConfigsPairResBitrate(pair<int,int> resolution,
                                                 pair<int,int> bitrates) :
    _resolution(resolution),
    _bitrates(bitrates)
{
}

pair<int,int> AVConfigsPairResBitrate::getResolution() const
{
    return _resolution;
}

string AVConfigsPairResBitrate::getResolutionAsString() const
{
    stringstream ss;
    ss << _resolution.first << "x" << _resolution.second;
    return ss.str();
}

void AVConfigsPairResBitrate::setResolution(const pair<int,int> resolution)
{
    _resolution = resolution;
}

pair<int,int> AVConfigsPairResBitrate::getBitrates() const
{
    return _bitrates;
}

void AVConfigsPairResBitrate::setBitrates(const pair<int,int> bitrates)
{
    _bitrates = bitrates;
}






AVConfigs::AVConfigs()
{
    // pares (resolução, faixa de bitrates) suportados
    _resAndBitrates.push_back(
        AVConfigsPairResBitrate(
            make_pair(180, 120),
            make_pair(50, 350)
        )
    );
    _resAndBitrates.push_back(
        AVConfigsPairResBitrate(
            make_pair(360, 240),
            make_pair(100, 700)
        )
    );
    _resAndBitrates.push_back(
        AVConfigsPairResBitrate(
            make_pair(640, 480),
            make_pair(200, 2500)
        )
    );
    _resAndBitrates.push_back(
        AVConfigsPairResBitrate(
            make_pair(720, 480),
            make_pair(200, 2500)
        )
    );
    _resAndBitrates.push_back(
        AVConfigsPairResBitrate(
            make_pair(1280, 720),
            make_pair(700, 6000)
        )
    );

    _fps = make_pair(1, 60);

    // bitrates são uma lista pois devem ser múltiplos de 32
    // poucos bitrates são suportados, não pode ser uma faixa de valores
    _audioBitrates.push_back(32);
    _audioBitrates.push_back(64);
    _audioBitrates.push_back(128);
    _audioBitrates.push_back(192);
    _audioBitrates.push_back(256);

    //_audioCodecs.push_back(COMMON_CODEC_AUDIO_AAC);
    _audioCodecs.push_back(COMMON_CODEC_AUDIO_MP2);

    _videoCodecs.push_back(COMMON_CODEC_VIDEO_DV25);
    _videoCodecs.push_back(COMMON_CODEC_VIDEO_MPEG4);
    _videoCodecs.push_back(COMMON_CODEC_VIDEO_MPEG2);
    _videoCodecs.push_back(COMMON_CODEC_VIDEO_H264);
}

const list<pair<int,int> > AVConfigs::getResolutions() const
{
    list<pair<int,int> > res;
    for (list<AVConfigsPairResBitrate>::const_iterator it = _resAndBitrates.begin();
         it != _resAndBitrates.end(); ++it) {
             res.push_back((*it).getResolution());
    }
    return res;
}

bool AVConfigs::validateResolution(pair<int,int> resolution) const
{
    for (list<AVConfigsPairResBitrate>::const_iterator it = _resAndBitrates.begin();
         it != _resAndBitrates.end(); ++it) {
        if ((*it).getResolution() == resolution) {
            return true;
        }
    }
    return false;
}

bool AVConfigs::validateResolution(string resolution) const
{
    for (list<AVConfigsPairResBitrate>::const_iterator it = _resAndBitrates.begin();
         it != _resAndBitrates.end(); ++it) {
        if ((*it).getResolutionAsString() == resolution) {
            return true;
        }
    }
    return false;
}

pair<int,int> AVConfigs::getFpsRange() const
{
    return _fps;
}

bool AVConfigs::validateFps(int fps) const
{
    return (fps >= _fps.first) &&
           (fps <= _fps.second);
}

const list<int>& AVConfigs::getAudioBitrates() const
{
    return _audioBitrates;
}

bool AVConfigs::validateAudioBitrate(int bitrate) const
{
    for (list<int>::const_iterator it = _audioBitrates.begin();
         it != _audioBitrates.end(); ++it) {
        if (*it == bitrate) {
            return true;
        }
    }
    return false;
}

pair<int,int> AVConfigs::getVideoBitrateRange(pair<int,int> resolution) const
{
    for (list<AVConfigsPairResBitrate>::const_iterator it = _resAndBitrates.begin();
         it != _resAndBitrates.end(); ++it) {
        if ((*it).getResolution() == resolution) {
            return (*it).getBitrates();
        }
    }
    return make_pair(0, -1); // intervalo inválido
}

bool AVConfigs::validateVideoBitrate(pair<int,int> resolution, int bitrate) const
{
    pair<int, int> videoBitrate = getVideoBitrateRange(resolution);
    return (bitrate >= videoBitrate.first) &&
           (bitrate <= videoBitrate.second);
}

const list<int>& AVConfigs::getAudioCodecs() const
{
    return _audioCodecs;
}

bool AVConfigs::validateAudioCodec(int codec) const
{
    list<int>::const_iterator it;
    for (it = _audioCodecs.begin(); it != _audioCodecs.end(); ++it) {
        if ((*it) == codec) {
            return true;
        }
    }
    return false;
}

const list<int>& AVConfigs::getVideoCodecs() const
{
    return _videoCodecs;
}

bool AVConfigs::validateVideoCodec(int codec) const
{
    list<int>::const_iterator it;
    for (it = _videoCodecs.begin(); it != _videoCodecs.end(); ++it) {
        if ((*it) == codec) {
            return true;
        }
    }
    return false;
}
