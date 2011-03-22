#include <error.h>
#include "gtest_DecodeVideo.h"

void DecodeVideoTest::_FreeQueueElement()
{
    uint32_t timestamp, buffersize;
    uint8_t * buffer;

    queue_dequeue(_consumer, &buffer, &buffersize, &timestamp, NULL);
    queue_free(_consumer);
}

TEST_F(DecodeVideoTest, Close)
{
    _decode->close();
}

TEST_F(DecodeVideoTest, OpenThenClose)
{
    int err;

    err = _decode->open(CODEC_BASE);
    ASSERT_TRUE(err == E_OK);

    _decode->close();
}

TEST_F(DecodeVideoTest, OpenStartStopClose)
{
    int err;

    err = _decode->open(CODEC_BASE);
    ASSERT_TRUE(err == E_OK);

    err = _decode->start(_queue, _queue);
    ASSERT_TRUE(err == E_OK);

    err = _decode->stop();
    ASSERT_TRUE(err == E_OK);

    _decode->close();
}

TEST_F(DecodeVideoTest, OpenStartClose)
{
    int err;

    err = _decode->open(CODEC_BASE);
    ASSERT_TRUE(err == E_OK);

    err = _decode->start(_queue, _queue);
    ASSERT_TRUE(err == E_OK);

    // sem chamar stop!

    _decode->close();
}

TEST_F(DecodeVideoTest, EncodeWithNullParameters)
{
    int err;
    bool gotFrame;
    _frame = (uint8_t *)malloc(sizeof(uint8_t));

    err = _decode->open(CODEC_BASE);
    ASSERT_TRUE(err == E_OK);

    // input = NULL
    err = _decode->decode(NULL, 1, TIMESTAMP, _queue, &gotFrame, NULL);
    ASSERT_TRUE(err != E_OK);

    // size = 0
    err = _decode->decode(_frame, 0, TIMESTAMP, _queue, &gotFrame, NULL);
    ASSERT_TRUE(err != E_OK);

    // queue = NULL
    err = _decode->decode(_frame, 1, TIMESTAMP, NULL, &gotFrame, NULL);
    ASSERT_TRUE(err != E_OK);

    _decode->close();
}

TEST_F(DecodeVideoTest, DecodeWithoutOpening)
{
    int err;
    bool gotFrame;
    _frame = (uint8_t *)malloc(sizeof(uint8_t));

    err = _decode->decode(_frame, 1, TIMESTAMP, _queue, &gotFrame, NULL);
    ASSERT_TRUE(err != E_OK);
    EXPECT_EQ(err, -1);
}

void DecodeVideoTest::_TestDecoding(string filename, int codecId)
{
    int err, size, used, read;
    bool gotFrame;
    FILE * arq;
    uint8_t frameTmp[FRAME_TEST_SIZE];
    _frame = (uint8_t *)malloc(sizeof(uint8_t) * FRAME_TEST_SIZE);

    // abre o arquivo com o arquivo codificado
    err = fopen_s(&arq, filename.c_str(), "rb");
    ASSERT_TRUE(err == 0) << "Não encontrou o arquivo " << filename.c_str();

    err = _decode->open(codecId);
    ASSERT_TRUE(err == E_OK);

    // carrega dados no buffer e vai decodificando
    read = fread_s(_frame, FRAME_TEST_SIZE, 1, FRAME_TEST_SIZE, arq);
    size = read;
    while (read && read != EOF) {
        used = _decode->decode(_frame, size, TIMESTAMP, _queue, &gotFrame);
        EXPECT_GT(used, -1);

        if (used > 0) {
            EXPECT_EQ(queue_length(_queue), 1) << "Dados decodificados não foram colocados na queue";
            EXPECT_GT(queue_size(_queue), used) << "Dados podem não ter sido decodificados"; // pequena garantia que os dados foram decodificados
            _FreeQueueElement();
        } else {
            used = FRAME_TEST_SIZE;
        }

        // copia os dados ainda não decodificados para um buffer temporário e lê mais um pouco do arquivo
        int bytesLeft = FRAME_TEST_SIZE - used;
        memcpy(frameTmp, _frame + used, bytesLeft);
        read = fread_s(frameTmp + bytesLeft, FRAME_TEST_SIZE - bytesLeft, 1,
                       FRAME_TEST_SIZE - bytesLeft, arq);
        size = bytesLeft + read;
        memcpy(_frame, frameTmp, size);
    }

    _decode->close();
}

TEST_F(DecodeVideoTest, TestDecodingMPEG4)
{
    _TestDecoding(FILE_MPEG4_PATH, COMMON_CODEC_VIDEO_MPEG4);
}

/*
/// \todo Este do H.264 ainda não funcional. Por algum motivo não funciona
///       direito a decodificação quando se lê os dados do arquivo .h264
TEST_F(DecodeVideoTest, TestDecodingH264)
{
    _TestDecoding(FILE_H264_PATH, COMMON_CODEC_VIDEO_H264);
}
*/

void DecodeVideoTest::_TestAutomaticDecoding(string filename, int codecId)
{
    int err, size, used, read, elapsedTime;
    int maxTime = 10000;                    // Tempo máximo de espera no loop (em ms)
    bool gotFrame;
    FILE * arq;
    uint8_t frameTmp[FRAME_TEST_SIZE];
    queue_t * queueOut;
    queue_consumer_t * queueOutConsumer;

    queueOut = queue_create(); // cria uma queue de entrada

    // abre o arquivo com o arquivo codificado
    err = fopen_s(&arq, filename.c_str(), "rb");
    ASSERT_TRUE(err == 0) << "Não encontrou o arquivo " << filename.c_str();

    // coloca vários frames na queue de entrada para serem codificados
    _frame = (uint8_t *)queue_malloc(sizeof(uint8_t) * FRAME_TEST_SIZE);
    read = fread_s(_frame, FRAME_TEST_SIZE, 1, FRAME_TEST_SIZE, arq);
    while (read && read != EOF) {
        queue_enqueue(_queue, _frame, read, TIMESTAMP, NULL);

        // novo buffer da queue...
        _frame = (uint8_t *)queue_malloc(sizeof(uint8_t) * FRAME_TEST_SIZE);
        read = fread_s(_frame, FRAME_TEST_SIZE, 1, FRAME_TEST_SIZE, arq);
    }
    _frame = NULL;

    err = _decode->open(codecId);
    ASSERT_TRUE(err == E_OK);

    err = _decode->start(_queue, queueOut);
    ASSERT_TRUE(err == E_OK) << "Erro durante o start()";

    queue_unregisterConsumer(&_consumer); // tira o consumidor antigo da _queue

    // fica num loop por um tempo esperando que tenha decodificado todos itens da queue
    size = queue_length(_queue);
    elapsedTime = 0;
    while (size > 0 && elapsedTime < maxTime) {
        common_sleep(50);
        elapsedTime += 50;
        size = queue_length(_queue);
    }
    EXPECT_EQ(queue_length(_queue), 0) << "Não foram decodificados todos os frames";

    _decode->stop();
    EXPECT_TRUE(err == E_OK) << "Erro durante o stop()";

    _decode->close();

    queue_destroy(&queueOut);
}

TEST_F(DecodeVideoTest, TestAutomaticDecodingMPEG4)
{
    _TestAutomaticDecoding(FILE_MPEG4_PATH, COMMON_CODEC_VIDEO_MPEG4);
}

