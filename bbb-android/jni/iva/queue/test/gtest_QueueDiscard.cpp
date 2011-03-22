#include <common.h>
#include "gtest_QueueDiscard.h"

TEST_F(QueueDiscardTest, Initialization)
{
    QueueDiscard * discard;

    ASSERT_NO_THROW(discard = new QueueDiscard());
    ASSERT_NO_THROW(delete discard);

    ASSERT_NO_THROW(discard = new QueueDiscard(_policy));
    ASSERT_NO_THROW(delete discard);
}

TEST_F(QueueDiscardTest, SetAndGetPolicy)
{
    QueueDiscard discard(_policy);
    QueueDiscard discard2;
    QueueDiscardPolicy policyEmpty;

    // atribuindo o objeto durante o construtor
    QueueDiscardPolicy &policySetted = discard.getPolicy();
    ASSERT_TRUE(policySetted == _policy);

    // inicialmente deve ter um objeto vazio
    policySetted = discard2.getPolicy();
    ASSERT_TRUE(policySetted == policyEmpty);
    // agora seta objeto e espera que esteja ok
    discard2.setPolicy(_policy);
    policySetted = discard2.getPolicy();
    ASSERT_TRUE(policySetted == _policy);
}

TEST_F(QueueDiscardTest, UsageWithConsumer)
{
    int i, j;
    QueueDiscard discard(_policy);

    // no início não pode descartar nenhum
    for (i = 0; i < 10; i++)
        EXPECT_FALSE(discard.discard(_consumer));
    _QueueInsert(5);
    for (i = 0; i < 10; i++)
        EXPECT_FALSE(discard.discard(_consumer));

    // testando primeiro nível: (6, 4)
    _QueueInsert(1);
    ASSERT_EQ(6, queue_lengthCons(_consumer));
    EXPECT_TRUE(discard.discard(_consumer)); // subiu de nível, terá que descartar o primeiro
    for (j = 0; j < 10; j++) {
        // usa 4, descarta 1
        for (i = 0; i < 4; i++)
            EXPECT_FALSE(discard.discard(_consumer));
        EXPECT_TRUE(discard.discard(_consumer));
    }

    // testando segundo nível: (8, 3)
    _QueueInsert(2);
    ASSERT_EQ(8, queue_lengthCons(_consumer));
    EXPECT_TRUE(discard.discard(_consumer)); // subiu de nível, terá que descartar o primeiro
    for (j = 0; j < 10; j++) {
        // usa 3, descarta 1
        for (i = 0; i < 3; i++)
            EXPECT_FALSE(discard.discard(_consumer));
        EXPECT_TRUE(discard.discard(_consumer));
    }

    // testando terceiro nível: (11, 2)
    _QueueInsert(3);
    ASSERT_EQ(11, queue_lengthCons(_consumer));
    EXPECT_TRUE(discard.discard(_consumer)); // subiu de nível, terá que descartar o primeiro
    for (j = 0; j < 10; j++) {
        // usa 2, descarta 1
        for (i = 0; i < 2; i++)
            EXPECT_FALSE(discard.discard(_consumer));
        EXPECT_TRUE(discard.discard(_consumer));
    }

    // testando quarto nível: (15, 1)
    _QueueInsert(4);
    ASSERT_EQ(15, queue_lengthCons(_consumer));
    EXPECT_TRUE(discard.discard(_consumer)); // subiu de nível, terá que descartar o primeiro
    for (j = 0; j < 10; j++) {
        // usa 1, descarta 1
        EXPECT_FALSE(discard.discard(_consumer));
        EXPECT_TRUE(discard.discard(_consumer));
    }

    // testando quinto nível: (20, 0)
    _QueueInsert(5);
    ASSERT_EQ(20, queue_lengthCons(_consumer));
    for (j = 0; j < 10; j++) {
        // descarta tudo
        EXPECT_TRUE(discard.discard(_consumer));
    }

    // testando sexto nível: (30, FLAG_CLEAR)
    _QueueInsert(10);
    ASSERT_EQ(30, queue_lengthCons(_consumer));
    // deve retornar que é para descartar tudo, até que a queue seja zerada
    for (j = 0; j < 10; j++) {
        EXPECT_TRUE(discard.discard(_consumer));
    }
    _QueueRemove(10);
    ASSERT_EQ(20, queue_lengthCons(_consumer));
    for (j = 0; j < 10; j++) {
        EXPECT_TRUE(discard.discard(_consumer));
    }
    _QueueRemove(15);
    ASSERT_EQ(5, queue_lengthCons(_consumer));
    for (j = 0; j < 10; j++) {
        EXPECT_TRUE(discard.discard(_consumer));
    }

    // limpa a queue e espera que pare de descartar
    queue_flush(_consumer);
    ASSERT_EQ(0, queue_lengthCons(_consumer));
    for (i = 0; i < 10; i++)
        EXPECT_FALSE(discard.discard(_consumer));
}

void QueueDiscardTest::_QueueInsert(int quant)
{
    for (int i = 0; i < quant; i++) {
        uint8_t *data = (uint8_t*)queue_malloc(10);
        queue_enqueue(_queue, data, 10, 0, NULL);
    }
}

void QueueDiscardTest::_QueueRemove(int quant)
{
    uint8_t *data;
    uint32_t buffersize, timestamp;
    for (int i = 0; i < quant; i++) {
        queue_dequeue(_consumer, &data, &buffersize, &timestamp, NULL);
        queue_free(_consumer);
    }
}

