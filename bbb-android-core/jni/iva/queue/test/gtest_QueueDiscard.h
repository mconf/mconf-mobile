#ifndef _QUEUE_DISCARD_H_
#define _QUEUE_DISCARD_H_

#include <gtest/gtest.h>
#include <queue.h>
#include <QueueDiscard.h>
#include <QueueDiscardPolicy.h>

class QueueDiscardTest : public ::testing::Test
{
protected:
    QueueDiscardPolicy _policy;
    queue_t *_queue;
    queue_consumer_t *_consumer;

    void _QueueInsert(int quant);
    void _QueueRemove(int quant);

    virtual void SetUp()
    {
        _policy.add(6, 4);
        _policy.add(8, 3);
        _policy.add(11, 2);
        _policy.add(15, 1);
        _policy.add(20, 0);
        _policy.add(30, QueueDiscardPolicy::FLAG_CLEAR);

        _queue = queue_create();
        _consumer = queue_registerConsumer(_queue);
    }

    virtual void TearDown()
    {
        queue_unregisterConsumer(&_consumer);
        queue_destroy(&_queue);
    }

};

#endif
