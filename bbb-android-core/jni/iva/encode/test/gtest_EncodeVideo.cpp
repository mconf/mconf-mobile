#include <error.h>
#include <fstream>
using namespace std;

#include "gtest_EncodeVideo.h"


void EncodeVideoTest::_CreateFrame()
{
    _buffer = (unsigned char *)malloc(sizeof(unsigned char) * FRAME_TEST_SIZE);
    memset(_buffer, 'a', FRAME_TEST_SIZE);
    avpicture_fill((AVPicture *)_frame, _buffer,
                   _params.getPixelsFormat().toFfmpeg(),
                   _params.getWidth(),
                   _params.getHeight());
}

TEST_F(EncodeVideoTest, Close)
{
    _encode->close();
}

TEST_F(EncodeVideoTest, EncodeWithoutOpening)
{
    int ret;

    ret = _encode->encode(_frame, 10, _queue);
    EXPECT_EQ(ret, -1);
}

TEST_F(EncodeVideoTest, OpenThenClose)
{
    int ret;

    ret = _encode->open(&_params);
    ASSERT_TRUE(ret == E_OK);

    _encode->close();
}

TEST_F(EncodeVideoTest, OpenStartStopClose)
{
    int ret;

    ret = _encode->open(&_params);
    ASSERT_TRUE(ret == E_OK);

    ret = _encode->start(_queue, _queue);
    ASSERT_TRUE(ret == E_OK);

    ret = _encode->stop();
    ASSERT_TRUE(ret == E_OK);

    _encode->close();
}

TEST_F(EncodeVideoTest, EncodeWithNullParameters)
{
    int ret;

    _encode->open(&_params);

    ret = _encode->encode(NULL, TIMESTAMP, NULL);
    EXPECT_EQ(ret, -1);

    ret = _encode->encode(_frame, TIMESTAMP, NULL);
    EXPECT_EQ(ret, -1);

    ret = _encode->encode(NULL, TIMESTAMP, _queue);
    EXPECT_EQ(ret, -1);

    _encode->close();
}


TEST_F(EncodeVideoTest, GetParams)
{
    EncodeVideoParams * params;

    ASSERT_TRUE(_encode->getParams() == NULL);

    _encode->open(&_params);
    params = _encode->getParams();
    ASSERT_FALSE(params == NULL);
    EXPECT_EQ(params->getCodec(), _params.getCodec());
    EXPECT_TRUE(params->getPixelsFormat() == _params.getPixelsFormat());
    EXPECT_EQ(params->getBitRate(), _params.getBitRate());
    EXPECT_EQ(params->getFrameRate(), _params.getFrameRate());
    EXPECT_EQ(params->getWidth(), _params.getWidth());
    EXPECT_EQ(params->getHeight(), _params.getHeight());
    EXPECT_EQ(params->getGopSize(), _params.getGopSize());

    _encode->close();

    ASSERT_TRUE(_encode->getParams() == NULL);
}

void EncodeVideoTest::_2FramesEncoding()
{
    int ret;
    _CreateFrame();

    ret = _encode->open(&_params);
    ASSERT_TRUE(ret == E_OK);

    // função de codificação que recebe um AVFrame
    ret = _encode->encode(_frame, TIMESTAMP, _queue);
    ASSERT_NE(ret, -1) << "Erro durante a codificação (AVFrame)";
    ASSERT_EQ(queue_length(_queue), 1) << "Dados codificados não foram colocados na queue";

    // pequena garantia que os dados foram codificados
    EXPECT_LT(queue_size(_queue), FRAME_TEST_SIZE) << "Dados podem não ter sido codificados";

    // função de codificação que recebe um buffer (unsigned char *)
    ret = _encode->encode(_buffer, FRAME_TEST_SIZE, TIMESTAMP, _queue);
    ASSERT_NE(ret, -1) << "Erro durante a codificação (unsigned char *)";
    ASSERT_EQ(queue_length(_queue), 2) << "Dados codificados não foram colocados na queue";

    // pequena garantia que os dados foram codificados
    EXPECT_LT(queue_size(_queue), FRAME_TEST_SIZE) << "Dados podem não ter sido codificados";

    _encode->close();
}

TEST_F(EncodeVideoTest, TestEncoding2FramesWithDefaultParams)
{
    _2FramesEncoding();
}

TEST_F(EncodeVideoTest, TestEncodingwFramesWithH264)
{
    _params.setCodec(COMMON_CODEC_VIDEO_H264);
    _2FramesEncoding();
}

void EncodeVideoTest::_AutomaticEncoding()
{
    int ret;
    queue_t * queueIn;
    int numFrames = 30;     // Número de frames que serão codificados
    int maxTime = 3000;     // Tempo máximo de espera (em ms)
    int elapsedTime;
    unsigned char * data;

    // cria uma queue de entrada
    queueIn = queue_create();

    ret = _encode->open(&_params);
    ASSERT_TRUE(ret == E_OK);

    ret = _encode->start(queueIn, _queue);
    ASSERT_TRUE(ret == E_OK) << "Erro durante o start()";

    // coloca vários frames na queue de entrada para serem codificados
    for (int i = 0; i < numFrames; i++) {
        data = (unsigned char *)queue_malloc(sizeof(unsigned char) * FRAME_TEST_SIZE);
        memset(data, 'a', FRAME_TEST_SIZE);
        queue_enqueue(queueIn, data, FRAME_TEST_SIZE, TIMESTAMP + i, NULL);
    }

    // fica num loop por um tempo esperando que tenha codificado todos os frames
    int size = queue_length(_queue);
    elapsedTime = 0;
    while (size < numFrames && elapsedTime < maxTime) {
        common_sleep(50);
        elapsedTime += 50;
        size = queue_length(_queue);
    }
    EXPECT_EQ(size, numFrames) << "Não codificou todos os frames como deveria";

    ret = _encode->stop();
    EXPECT_TRUE(ret == E_OK) << "Erro durante o stop()";

    queue_destroy(&queueIn);
    _encode->close();
}

TEST_F(EncodeVideoTest, TestAutomaticEncodingWithH264)
{
    _params.setCodec(COMMON_CODEC_VIDEO_H264);
    _AutomaticEncoding();
}

TEST_F(EncodeVideoTest, TestAutomaticEncodingWithH264AndPresets)
{
    string preset = "..\\..\\..\\..\\deps\\test\\encode\\libx264-default.ffpreset";

    // pra garantir que o preset existe
    ifstream myfile;
    myfile.open(preset.c_str());
    ASSERT_FALSE(myfile.fail());

    _params.setCodec(COMMON_CODEC_VIDEO_H264);
    _params.setPresetFile(preset);
    _AutomaticEncoding();
}



