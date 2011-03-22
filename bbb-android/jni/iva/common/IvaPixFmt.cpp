#include "IvaPixFmt.h"

const IvaPixFmtVector IvaPixFmt::Items = IvaPixFmtVector();


IvaPixFmt::IvaPixFmt() :
    _format(FMT_NONE)
{
}

IvaPixFmt::IvaPixFmt(enum PixFmt format)
{
    set(format);
}

IvaPixFmt::~IvaPixFmt()
{
}

enum IvaPixFmt::PixFmt IvaPixFmt::get() const
{
    return _format;
}

int IvaPixFmt::getAsInt() const
{
    return _format;
}

string IvaPixFmt::getAsStr() const
{
    return Items[_format].getName();
}

void IvaPixFmt::set(enum PixFmt value)
{
    _format = value;
}

IvaPixFmt& IvaPixFmt::clear()
{
    set(FMT_NONE);
    return *this;
}

bool IvaPixFmt::isValid() const
{
    return (_format != FMT_NONE) &&
           (_format != FMT_COUNT);
}

enum PixelFormat IvaPixFmt::toFfmpeg() const
{
    return Items[_format].getFfmpeg();
}

IvaPixFmt& IvaPixFmt::fromFfmpeg(enum PixelFormat value)
{
    bool found = false;
    vector<IvaPixFmtItem>::const_iterator it;
    for (it = Items.begin(); it != Items.end(); ++it) {
        if ((*it).getFfmpeg() == value) {
            found = true;
            this->set((*it).get());
            break;
        }
    }
    if (!found) set(FMT_NONE);
    return *this;
}

bool IvaPixFmt::operator==(const IvaPixFmt& operand) const
{
    return (get() == operand.get());
}

bool IvaPixFmt::operator!=(const IvaPixFmt& operand) const
{
    return (get() != operand.get());
}

enum IvaPixFmt::PixFmt IvaPixFmtItem::get() const
{
    return _value;
}

string IvaPixFmtItem::getName() const
{
    return _name;
}

enum PixelFormat IvaPixFmtItem::getFfmpeg() const
{
    return _ffmpeg;
}


