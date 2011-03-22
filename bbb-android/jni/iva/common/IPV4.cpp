#include "CommonLeaks.h"
#include "IPV4.h"
#include "CommonLeaksCpp.h"

IPv4::IPv4() throw (int)
    : bytes(4,0)
{
}

IPv4::IPv4(int byte1, int byte2, int byte3, int byte4) throw (int)
    : bytes(4,0)
{

    if (byte1 >= 0 && byte1 <= 255) bytes[0] = byte1; else throw E_COMMON_INVALID_PARAM;
    if (byte2 >= 0 && byte2 <= 255) bytes[1] = byte2; else throw E_COMMON_INVALID_PARAM;
    if (byte3 >= 0 && byte3 <= 255) bytes[2] = byte3; else throw E_COMMON_INVALID_PARAM;
    if (byte4 >= 0 && byte4 <= 255) bytes[3] = byte4; else throw E_COMMON_INVALID_PARAM;

}

IPv4::IPv4(const IPv4 & ent)
{

	bytes = ent.bytes;

};

IPv4::IPv4(unsigned int in) : bytes(4,0)
{

    setInt(in);

};

IPv4::IPv4(const IvaString& in) throw (int) : bytes(4,0)
{

    int r = setString(in);
    if (r != E_OK)
        throw r;

};

IPv4::~IPv4()
{
};

void IPv4::getString(IvaString& out) const
{

    out = getString();

};

IvaString IPv4::getString() const
{

    return IvaString(bytes[0]) + "." + IvaString(bytes[1]) + "." + IvaString(bytes[2]) + "." + IvaString(bytes[3]);

}

int IPv4::setString(const IvaString& in)
{
    vector<IvaString> strBytes;

    in.split('.',strBytes);

    if (strBytes.size() != 4)
        return E_COMMON_INVALID_PARAM;

    for (int i = 0; i < 4; ++i) {
        IvaString s(strBytes.at(i));
        if (!s.isNumber())
            return E_COMMON_INVALID_PARAM;

        int tmp = s.getInt();
        if (tmp >= 0 && tmp <= 255)
            bytes[i] = tmp;
        else
            return E_COMMON_INVALID_PARAM;
    }

    return E_OK;
}

unsigned int IPv4::getInteger() const
{

    return ((((bytes[0]*256) + bytes[1])*256) + bytes[2])*256 + bytes[3];

};

void IPv4::setInt(unsigned int in)
{

    for (int i = 3; i >= 0; --i) {
        bytes[i] = in%256;
        in = in / 256;
    }

};

void IPv4::getBytes(int * byte1, int * byte2, int * byte3, int * byte4)
{

    *byte1 = bytes[0];
    *byte2 = bytes[1];
    *byte3 = bytes[2];
    *byte4 = bytes[3];

};

IPv4::IPv4Type IPv4::getType() const
{
    if (bytes[0] >= 224 && bytes[0] <= 239)
        return TYPE_MULTICAST;
    if (getString() == "255.255.255.255")
        return TYPE_BROADCAST;
    if (getInteger() == 0)
        return TYPE_UNDEFINED;
    else
        return TYPE_UNICAST;
}

IPv4 & IPv4::operator=(const IPv4 & operand)
{

	bytes = operand.bytes;

	return *this;

};

bool IPv4::operator>(const IPv4& operand) const
{
    return (getInteger() > operand.getInteger());
}

bool IPv4::operator>=(const IPv4& operand) const
{
    return (operator>(operand) || operator==(operand));
}

bool IPv4::operator==(const IPv4& operand) const
{
    return (getInteger() == operand.getInteger());
}

bool IPv4::operator<=(const IPv4& operand) const
{
    return (operator<(operand) || operator==(operand));
}
bool IPv4::operator<(const IPv4& operand) const
{
    return (getInteger() < operand.getInteger());
}

