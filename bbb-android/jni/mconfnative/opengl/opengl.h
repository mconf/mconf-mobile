//#include <algorithm>
#include <stdint.h>

#define ARRAY_DATA_TYPE     GL_UNSIGNED_SHORT_5_6_5
/* escolhe o WDEST e o HDEST ideais */
#define b2(x)   (   (x) | (   (x) >> 1) )
#define b4(x)   ( b2(x) | ( b2(x) >> 2) )
#define b8(x)   ( b4(x) | ( b4(x) >> 4) )
#define b16(x)  ( b8(x) | ( b8(x) >> 8) )
#define b32(x)  (b16(x) | (b16(x) >>16) )
#define next_power_of_2(x)      (b32(x-1) + 1)

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
void aplly_first_texture(uint8_t* pixels, int w, int h);

/* usa chamadas opengl para desenhar o frame
 * parametros:
 * pixels: array de pixels
 * w: largura
 * h: altura */
void update_frame(uint8_t* pixels, int w, int h, int dw, int dh);

/* verifica caracteristicas da versao do opengl que o aparelho suporta */
void checkGLVersion();

/* inicializa o opengl */
int initGL(int w_orig, int h_orig);
