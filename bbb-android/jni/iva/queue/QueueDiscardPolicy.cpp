#include <CommonLeaks.h>
#include "QueueDiscardPolicy.h"
#include <CommonLeaksCpp.h>

QueueDiscardPolicy::QueueDiscardPolicy(void)
{
}

QueueDiscardPolicy::~QueueDiscardPolicy(void)
{
}

int QueueDiscardPolicy::add(int queueSize, int framesToUse)
{
    if (exists(queueSize)) {
        return E_ERROR;
    }
    _items[queueSize] = framesToUse;
    return E_OK;
}

void QueueDiscardPolicy::remove(int queueSize)
{
    if (exists(queueSize)) {
        _items.erase(queueSize);
    }
}

int QueueDiscardPolicy::size()
{
    return _items.size();
}

int QueueDiscardPolicy::getFramesToUse(int queueSize)
{
    int lastFramesToUse = FLAG_ALL;

    map<int, int>::iterator it;
    for (it = _items.begin(); it != _items.end(); ++it) {
        if (queueSize < (*it).first) {
            return lastFramesToUse;
        }
        lastFramesToUse = (*it).second;
    }

    return lastFramesToUse;
}

int QueueDiscardPolicy::getLevel(int queueSize)
{
    int level = 0;

    map<int, int>::iterator it;
    for (it = _items.begin(); it != _items.end(); ++it) {
        if (queueSize < (*it).first) {
            return level;
        }
        level++;
    }

    return level;
}

pair<int, int> QueueDiscardPolicy::getItemByLevel(int level)
{
    if (size() == 0 || level <= 0) {
        return make_pair(0, FLAG_ALL);
    }

    map<int, int>::iterator it;
    int count = 0;
    for (it = _items.begin(); it != _items.end(); ++it) {
        count++;
        if (level == count) {
            return (*it);
        }
    }

    it = _items.end();
    --it;
    return (*it);
}

bool QueueDiscardPolicy::exists(int queueSize)
{
    return (_items.find(queueSize) != _items.end());
}

bool QueueDiscardPolicy::operator==(const QueueDiscardPolicy &operand)
{
    return (_items == operand._items);
}

bool QueueDiscardPolicy::operator!=(const QueueDiscardPolicy &operand)
{
    return !(operator==(operand));
}
