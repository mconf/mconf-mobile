#include "ImageRotator.h"

ImageRotator::ImageRotator() 
    : _width(0)
    , _height(0)
    , _rotation_buffer(NULL) {
    
}

ImageRotator::~ImageRotator() {
    if (_rotation_buffer != NULL)
        free(_rotation_buffer);
}

void ImageRotator::_checkBufferSize(int width, int height) {
    if (width != _width || height != _height) {
        if (width * height != _width * _height) {
            if (_rotation_buffer != NULL)
                free(_rotation_buffer);
            _rotation_buffer = (uint8_t*) malloc(width * height);
        }
        _width = width;
        _height = height;
        
		_pixels = width * height;
		_halfpixels = _pixels / 2;
		_quarterpixels = _pixels / 4;
		_halfby = _quarterpixels + _pixels;        
    }
}

void ImageRotator::_rotateLayer(uint8_t* data, int width, int height, int rotation) {
    memcpy(_rotation_buffer, data, width * height);
    
    // http://stackoverflow.com/a/1092721
	if (rotation == 90) {
	    for (int i = 0; i < height; ++i)
		    for (int j = 0; j < width; ++j)
			    //ret[j][i] = matrix[h - i - 1][j];
			    data[j * height + i] = _rotation_buffer[(height - i - 1) * width + j];
    } else if (rotation == 180) {
	    for (int i = 0; i < height; ++i)
		    for (int j = 0; j < width; ++j)
			    data[(height - i - 1) * width + width - j - 1] = _rotation_buffer[i * width + j];
    } else if (rotation == 270) {
	    for (int i = 0; i < height; ++i)
		    for (int j = 0; j < width; ++j)
		        //ret[j][i] = matrix[i][w - j - 1];
		        data[j * height + i] = _rotation_buffer[i * width + width - j - 1];
    }
}

void ImageRotator::_convertNV21toYUV420P(uint8_t* data, int width, int height) {
    _checkBufferSize(width, height);
	memcpy(_rotation_buffer, &data[_pixels], _halfpixels);
	int count = -_pixels;
	for (int i = _pixels; i < _halfby; ++i){
		data[i + _quarterpixels] = _rotation_buffer[i + count];
		count++;
		data[i] = _rotation_buffer[i + count];
	}
}

void ImageRotator::process(uint8_t* data, int width, int height, int rotation) {
    _convertNV21toYUV420P(data, width, height);
    
    if (rotation == 0)
        return;

    int y_size = width * height;
    int u_size = y_size / 4;
    int v_size = u_size;
    int y_index = 0;
    int u_index = y_size;
    int v_index = y_size + u_size;
    int half_width = width / 2;
    int half_height = height / 2;

    _rotateLayer(&data[y_index], width, height, rotation);
    _rotateLayer(&data[u_index], half_width, half_height, rotation);
    _rotateLayer(&data[v_index], half_width, half_height, rotation);
}

