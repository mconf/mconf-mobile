#include <android/log.h>
#include <GLES/gl.h>
#include <GLES/glext.h>
#include "opengl.h"

void apply_first_texture(uint8_t* pixels, int w, int h){
	glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, w, h, 0, GL_RGB, ARRAY_DATA_TYPE, pixels);
	CHECK_GL_ERROR();
}

void update_frame(uint8_t* pixels, int w, int h, int dw, int dh){
	glClear(GL_COLOR_BUFFER_BIT);
	glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, w, h, GL_RGB, ARRAY_DATA_TYPE, pixels);
	glDrawTexiOES(dw, dh, 0, w, h);
	CHECK_GL_ERROR();
}

void checkGLVersion(){
	__android_log_print(ANDROID_LOG_DEBUG,  "(opengl.h)checkGLVersion", "GL_VENDOR = %s\n", glGetString(GL_VENDOR));
	__android_log_print(ANDROID_LOG_DEBUG,  "(opengl.h)checkGLVersion", "GL_RENDERER = %s\n", glGetString(GL_RENDERER));
	__android_log_print(ANDROID_LOG_DEBUG,  "(opengl.h)checkGLVersion", "GL_VERSION = %s\n", glGetString(GL_VERSION));
	__android_log_print(ANDROID_LOG_DEBUG,  "(opengl.h)checkGLVersion", "GL_EXTENSIONS = %s\n", glGetString(GL_EXTENSIONS));
}

int initGL(int w_orig, int h_orig)
{
	checkGLVersion();
	GLint crop[4] = { 0, h_orig, w_orig, -h_orig };
	glBindTexture(GL_TEXTURE_2D, 0);
	glTexParameteriv(GL_TEXTURE_2D, GL_TEXTURE_CROP_RECT_OES, crop);
	glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
	glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
	glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	glTexEnvx(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);
	glEnable(GL_TEXTURE_2D);
	glColor4f(1,1,1,1);
	CHECK_GL_ERROR();
	return 1;
}
