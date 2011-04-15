#include <error.h>
#include "gtest_EncodeAudio.h"

TEST_F(EncodeAudioTest, Close)
{
    _encode->close();
}

TEST_F(EncodeAudioTest, EncodeWithoutOpening)
{
    int ret;
    _frame = (unsigned char *)malloc(sizeof(unsigned char));

    ret = _encode->encode(_frame, TIMESTAMP, 10, _queue);
    ASSERT_EQ(ret, -1);
}

TEST_F(EncodeAudioTest, OpenThenClose)
{
    int ret;

    ret = _encode->open(&_params);
    ASSERT_TRUE(ret == E_OK);

    _encode->close();
}

TEST_F(EncodeAudioTest, OpenStartStopClose)
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

TEST_F(EncodeAudioTest, EncodeWithNullParameters)
{
    int ret;
    _frame = (unsigned char *)malloc(sizeof(unsigned char));

    _encode->open(&_params);

    ret = _encode->encode(NULL, 0, TIMESTAMP, NULL);
    EXPECT_EQ(ret, -1);

    ret = _encode->encode(_frame, 0, TIMESTAMP, NULL);
    EXPECT_EQ(ret, -1);

    ret = _encode->encode(_frame, 10, TIMESTAMP, NULL);
    EXPECT_EQ(ret, -1);

    ret = _encode->encode(_frame, 0, TIMESTAMP, _queue);
    EXPECT_EQ(ret, -1);

    ret = _encode->encode(NULL, 0, TIMESTAMP, _queue);
    EXPECT_EQ(ret, -1);

    _encode->close();
}

TEST_F(EncodeAudioTest, TestEncoding)
{
    int ret;
    _frame = (unsigned char *)malloc(sizeof(unsigned char)*FRAME_TEST_SIZE);
    memset(_frame, 'a', FRAME_TEST_SIZE);

    ret = _encode->open(&_params);
    ASSERT_TRUE(ret == E_OK);

    _encode->encode(_frame, TIMESTAMP, FRAME_TEST_SIZE, _queue);
    ASSERT_GT(ret, -1) << "Erro durante a codificação";
    ASSERT_EQ(queue_length(_queue), 1) << "Dados codificados não foram colocados na queue";

    // pequena garantia que os dados foram codificados
    EXPECT_LT(queue_size(_queue), FRAME_TEST_SIZE) << "Dados podem não ter sido codificados";

    _encode->close();
}

TEST_F(EncodeAudioTest, GetParams)
{
    int ret;
    EncodeAudioParams * params;

    ASSERT_TRUE(_encode->getParams() == NULL);

    _encode->open(&_params);
    params = _encode->getParams();
    ASSERT_FALSE(params == NULL);
    EXPECT_EQ(params->getCodec(), _params.getCodec());
    EXPECT_EQ(params->getBitRate(), _params.getBitRate());
    EXPECT_EQ(params->getChannels(), _params.getChannels());
    EXPECT_EQ(params->getSampleRate(), _params.getSampleRate());

    _encode->close();

    ASSERT_TRUE(_encode->getParams() == NULL);
}

TEST_F(EncodeAudioTest, TestAutomaticEncoding)
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

