//#include <algorithm>
#include <stdint.h>

#define ARRAY_DATA_TYPE     GL_UNSIGNED_SHORT_5_6_5

/* verifica se houve algum erro no opengl */
#define CHECK_GL_ERROR() {\
	GLint err;\
	if ((err = glGetError()) != GL_NO_ERROR) {\
		__android_log_print(ANDROID_LOG_DEBUG,  "(opengl.h)checkGLError", "OpenGL error %d (%04x) line %d\n", err, err, __LINE__);\
	}\
}

/* aplica a textura para poder usar subimage
 * parametros:
 * pixels: array de pixels
 * w: largura
 * h: altura */
void apply_first_texture(uint8_t* pixels, int w, int h);

/* verifica caracteristicas da versao do opengl que o aparelho suporta */
void checkGLVersion();

/* inicializa o opengl */
int initGL(int w_orig, int h_orig);
