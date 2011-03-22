#include <CommonLeaks.h>
#include "QueueBuffer.h"
#include <CommonLeaksCpp.h>

QueueBufferItem::QueueBufferItem(uint8_t *buffer, uint32_t buffersize, uint32_t timestamp,
                     QueueExtraData * extraData)
{
    //buffer_ = buffer;
    buffer_ = (uint8_t *)malloc(sizeof(uint8_t)*buffersize);
    memcpy(buffer_, buffer, buffersize);
    buffersize_ = buffersize;
    timestamp_ = timestamp;
    extraData_ = extraData;
}

QueueBufferItem::~QueueBufferItem()
{
    free(buffer_);
}

uint8_t * QueueBufferItem::getBuffer()
{
    return buffer_;
}
uint32_t QueueBufferItem::getBuffersize()
{
    return buffersize_;
}

uint32_t QueueBufferItem::getTimestamp()
{
    return timestamp_;
}

QueueExtraData * QueueBufferItem::getExtraData()
{
    return extraData_;
}

QueueBuffer::QueueBuffer(int size)
{
    size_ = size;
    flagWait_ = true;
    queueItem_ = NULL;
}

QueueBuffer::~QueueBuffer()
{
}

int QueueBuffer::getSize()
{
    return size_;
}

void QueueBuffer::setSize(int value)
{
    size_ = value;
}

bool QueueBuffer::isFull()
{
    return buffer_.size() == size_;
}

void QueueBuffer::freeItem(queue_consumer_t *consumer)
{
    // modo não bufferizado
    if (size_ <= 0) {
        queue_free(consumer);

    // modo com buffer
    } else if (queueItem_) {
        delete queueItem_;
        queueItem_ = NULL;
    }
}

int QueueBuffer::tryFillBuffer(queue_consumer_t *consumer)
{
    uint8_t * tmpBuffer;
    uint32_t tmpBuffersize;
    uint32_t tmpTimestamp;
    QueueExtraData * tmpExtraData;
    QueueBufferItem * queueItem;
    int ret = E_OK;

    do {
        ret = queue_dequeue(consumer, &tmpBuffer, &tmpBuffersize, &tmpTimestamp, &tmpExtraData);
        if (ret == E_OK) {
            queueItem = new QueueBufferItem(tmpBuffer, tmpBuffersize, tmpTimestamp, tmpExtraData);
            buffer_.push(queueItem);
            queue_free(consumer);
        }
    } while (ret == E_OK && !isFull());

    if (flagWait_ && isFull()) {
        flagWait_ = false; // buffer cheio, não precisa mais esperar
        //printf("QueueBuffer: \t\t ========= encheu o buffer ========== \n");
    }

    return ret;
}

int QueueBuffer::dequeue(queue_consumer_t *consumer, uint8_t ** oBuffer, uint32_t * oBuffersize,
                         uint32_t *oTimestamp, QueueExtraData ** oExtraData)
{
    QueueBufferItem * queueItem;
    int ret = E_ERROR;

    // se o size é <= 0, não faz buffer nenhum, chama a queue direto
    if (size_ <= 0) {
        ret = queue_dequeue(consumer, oBuffer, oBuffersize, oTimestamp, oExtraData);

    } else {

        // sempre tenta manter o bufer cheio
        ret = tryFillBuffer(consumer);

        if (!flagWait_) {

            // pega a primeira posição do buffer pra retornar
            if (buffer_.size() > 0) {
                queueItem = buffer_.front();
                buffer_.pop();
                if (queueItem) {
                    *oBuffer = queueItem->getBuffer();
                    *oBuffersize = queueItem->getBuffersize();
                    *oTimestamp = queueItem->getTimestamp();
                    if (oExtraData) {
                        *oExtraData = queueItem->getExtraData();
                    }
                    queueItem_ = queueItem;
                    ret = E_OK;
                }
                //printf("QueueBuffer: \t\t ==== buffer size %d, queue size %d\n", buffer_.size(),
                       //queue_lengthCons(consumer));
            } else {
                //printf("QueueBuffer: \t\t ==== nada no buffer, seta pra esperar encher \n");
                // buffer e queue estão vazios, começa esperar até o buffer encher
                flagWait_ = true;
            }
        }
    }

    return ret;
}

