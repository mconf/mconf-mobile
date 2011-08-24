#ifndef _IVA_PIXEL_FORMAT_H
#define _IVA_PIXEL_FORMAT_H

#include <string>
#include <vector>
using namespace std;

#ifdef _MSC_VER
#include <guiddef.h>    // requer strmbasd.lib
#include <uuids.h>
#endif

extern "C" {
#include <libavcodec/avcodec.h>
};


class IvaPixFmtItem;
class IvaPixFmtVector;


/** \brief Representa um formato de pixel. Contém também a lista de formatos de pixel
 *         suportados no sistema.
 *
 * Contém uma lista de formatos (IvaPixFmtVector) e funções para buscar, validar e
 * converter (para ffmpeg/guid) os formatos suportados.
 * 
 * \note O formato GUID só é suportado no Windows. É necessário para ser utilizado
 *       juntamente com o DirectX. Em outros SOs não existe e não é necessário.
 */
class IvaPixFmt
{
public:

    /** \brief Lista de formatos internos do sistema.
     */
    enum PixFmt {
        FMT_NONE = 0,  ///< formato de pixel não válido
        FMT_YUV420P,   ///< planar YUV 4:2:0, 12bpp, (1 Cr & Cb sample per 2x2 Y samples)
        FMT_RGB24,     ///< packed RGB 8:8:8, 24bpp, RGBRGB...
        FMT_RGB32,
        FMT_BGR32,
        FMT_YUV422P,   ///< planar YUV 4:2:2, 16bpp, (1 Cr & Cb sample per 2x1 Y samples)
        FMT_YUV411P,   ///< planar YUV 4:1:1, 12bpp, (1 Cr & Cb sample per 4x1 Y samples)
        FMT_RGB555,
        FMT_DVSD,      ///< DV25
        FMT_RGB565,    ///< utilizado na aplicação Android
        FMT_COUNT      ///< para fazer laços com o enum
    };
    static const IvaPixFmtVector Items;     ///< Lista estática com os objetos dos formatos suportados

    IvaPixFmt();
    IvaPixFmt(enum PixFmt format);
    virtual ~IvaPixFmt();

    enum PixFmt get() const;
    int getAsInt() const;
    string getAsStr() const;
    void set(enum PixFmt value);
    IvaPixFmt& clear();

    bool isValid() const;

    enum PixelFormat toFfmpeg() const;
    IvaPixFmt& fromFfmpeg(enum PixelFormat value);

#ifdef _MSC_VER
    GUID toGUID() const;
    IvaPixFmt& fromGUID(const GUID value);
#endif

    bool operator==(const IvaPixFmt& operand) const;
    bool operator!=(const IvaPixFmt& operand) const;

private:
    enum PixFmt _format;

};


/** \brief Item que representa um formato de pixel. Classe simples apenas
 *         para armazenar as informações de um formato de pixel.
 */
class IvaPixFmtItem
{
public:

#ifdef _MSC_VER
    IvaPixFmtItem(enum IvaPixFmt::PixFmt value, enum PixelFormat ffmpeg,
                  const GUID guid, const string name);
#else
    IvaPixFmtItem(enum IvaPixFmt::PixFmt value, enum PixelFormat ffmpeg,
                  const string name);
#endif

    enum IvaPixFmt::PixFmt get() const;
    string getName() const;
    enum PixelFormat getFfmpeg() const;

    #ifdef _MSC_VER
        GUID getGUID() const;
    #endif

private:
    enum IvaPixFmt::PixFmt _value;      ///< Formato interno
    string _name;                       ///< Nome do formato
    enum PixelFormat _ffmpeg;           ///< Identificador do formato no ffmpeg

#ifdef _MSC_VER
    GUID _guid;                         ///< Identificador do formato em GUID (win)
#endif

};


/** \brief Vetor para armazenar formatos de pixels. Usado por IvaPixFmt.
 */
class IvaPixFmtVector : public vector<IvaPixFmtItem>
{
public:
    IvaPixFmtVector();
};



#endif
