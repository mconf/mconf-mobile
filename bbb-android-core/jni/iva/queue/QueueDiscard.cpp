#include <CommonLeaks.h>
#include "QueueDiscard.h"
#include <CommonLeaksCpp.h>

QueueDiscard::QueueDiscard(void) :
    _framesUsed(0), _lastLevel(0), _clearing(false)
{
}

QueueDiscard::QueueDiscard(QueueDiscardPolicy &policy) :
    _policy(policy), _framesUsed(0), _lastLevel(0), _clearing(false)
{
}

QueueDiscard::~QueueDiscard(void)
{
}

QueueDiscardPolicy &QueueDiscard::getPolicy()
{
    return _policy;
}

void QueueDiscard::setPolicy(const QueueDiscardPolicy &value)
{
    _policy = value;
}

string QueueDiscard::getLevelAsStr()
{
    pair<int, int> item = _policy.getItemByLevel(_lastLevel);

    stringstream ss;
    ss << _lastLevel << " (" << item.first << ", ";
    switch (item.second) {
        case QueueDiscardPolicy::FLAG_ALL:
            ss << "ALL";
            break;
        case QueueDiscardPolicy::FLAG_CLEAR:
            ss << "CLEAR";
            break;
        default:
            ss << item.second;
    }
    ss << ")";
    return ss.str();
}

bool QueueDiscard::clear()
{
    pair<int, int> item = _policy.getItemByLevel(_lastLevel);
    return (item.second == QueueDiscardPolicy::FLAG_CLEAR);
}

bool QueueDiscard::discard(queue_consumer_t *consumer)
{
    if (!consumer) return false;

    int len = queue_lengthCons(consumer);
    int framesToUse = _policy.getFramesToUse(len);
    int level = _policy.getLevel(len);
    int oldLevel = _lastLevel;

    _lastLevel = level;

    // se em fase de zerar a queue, vai removendo até chegar em <= 1 frames
    if (_clearing) {
        if (len <= 1) {
            _clearing = false;
        } else {
            return true;
        }
    }

    if (framesToUse == QueueDiscardPolicy::FLAG_ALL) {
        return false;
    } else if (framesToUse == QueueDiscardPolicy::FLAG_CLEAR) {
        _clearing = true;
        return true;
    }

    // mudou de nível
    if (level != oldLevel) {
        _framesUsed = 0; // volta pro início
        if (level > oldLevel) { // se piorou o nível já começa descartando
            return true;
        }
    }

    // usou todos os que devia, hora de descartar
    if (_framesUsed >= framesToUse) {
        _framesUsed = 0;
        return true;
    }

    _framesUsed++;
    return false;
}
