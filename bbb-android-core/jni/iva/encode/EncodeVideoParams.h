#ifndef _ENCODE_VIDEO_PARAMS_H_
#define _ENCODE_VIDEO_PARAMS_H_

extern "C" {
#include <libavcodec/avcodec.h>
}
#include <IvaPixFmt.h>
#include <string>
using namespace std;

/** \brief Classe para armazenar os parâmetros para codificação de vídeo
 */
class EncodeVideoParams
{
protected:
    int _codec;                     ///< Id do codec (e.g. mpeg4)
    IvaPixFmt _pixFmt;              ///< Formato dos pixels (e.g. yuv420p)
    int _bitRate;                   ///< Taxa de bits (em kbps)
    int _frameRate;                 ///< Frames por segundo
    int _width;                     ///< Largura
    int _height;                    ///< Altura
    int _gopSize;                   ///< Tamanho do GOP
    string _presetFile;             ///< Arquivo com configurações adicionais (presets)

public:
    EncodeVideoParams();

    void setCodec(int value);
    void setPixelsFormat(IvaPixFmt& value);
    void setBitRate(int value);
    void setFrameRate(int value);
    void setWidth(int value);
    void setHeight(int value);
    void setGopSize(int value);
    void setPresetFile(string filename);

    int getCodec();
    IvaPixFmt& getPixelsFormat();
    int getBitRate();
    int getFrameRate();
    int getWidth();
    int getHeight();
    int getGopSize();
    string getPresetFile();
};

#endif

