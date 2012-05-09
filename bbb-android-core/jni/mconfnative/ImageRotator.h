#ifndef _IMAGE_ROTATOR_H_
#define _IMAGE_ROTATOR_H_

#include <cstdlib>
#include <iostream>
#include <inttypes.h>

class ImageRotator {

private:
    int _width, _height;
    int _pixels,
        _halfpixels,
        _quarterpixels,
        _halfby;
    uint8_t* _rotation_buffer;

    void _checkBufferSize(int width, int height);
    void _convertNV21toYUV420P(uint8_t* data, int width, int height);
    void _rotateLayer(uint8_t* data, int width, int height, int rotation);

public:
    ImageRotator();
    ~ImageRotator();

    void process(uint8_t* data, int width, int height, int rotation);
};

#endif
