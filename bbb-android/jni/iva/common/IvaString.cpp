#include "CommonLeaks.h"
#include "IvaString.h"
#include <stdlib.h>
#include <sstream>
#include "CommonLeaksCpp.h"

using namespace std;

void IvaString::Init()
{

    wString_ = NULL;

};

IvaString::IvaString() :
string()
{
    Init();
};

IvaString::IvaString(const string& text) :
string(text)
{
    Init();
};

IvaString::IvaString(const char * text) :
string(text)
{
    Init();
};

IvaString::IvaString(int numchars, char character) :
string(numchars,character)
{
    Init();
};

IvaString::IvaString(const IvaString& operand) 
: string(operand)
{
    Init();  
};

IvaString::IvaString(int number) :
string()
{

    stringstream nmbstream;

    nmbstream << number;

    append(nmbstream.str());

    Init();

};

IvaString::~IvaString()
{

    deleteWString();

};

void IvaString::deleteWString() {

    if (wString_) {
        free(wString_);
    };

};

void IvaString::translate(char oldC, char newC)
{

    for (string::iterator iter = begin(); iter != end(); iter++) {

        if (*iter == oldC) {
            *iter = newC;
        };

    };

};

void IvaString::rTrim()
{

    string::iterator iter;

    for (iter = end()-1; iter != begin(); iter--) {

        if (!(isspace(*iter))) {
            break;
        };

    };

    if (iter == begin()) {
        if (!(isspace(*iter))) {
            iter++;
        };
    } else {
        iter++;
    };

    erase(iter,end());

};

void IvaString::lTrim() {

    string::iterator iter;

    for (iter = begin(); iter != end(); iter++) {

        if (!(isspace(*iter))) {
            break;
        };

    };

    erase(begin(),iter);

};

void IvaString::trim()
{
    if (empty())
        return;

    rTrim();
    lTrim();

};

wchar_t * IvaString::toWchar()
{

    deleteWString();

    wString_ = (wchar_t *) malloc(sizeof(wchar_t)*(size()+1));

    int a = 0;

    for (string::iterator iter = begin(); iter != end(); iter++) {

        wString_[a] = btowc(*iter);

        a++;

    };

    wString_[a] = 0;

    return wString_;

};

void IvaString::fromWchar(wchar_t * value)
{
    int len = wcslen(value);

    char * dest = (char *) malloc(sizeof(char)*(len+1));
    for (int i = 0; i < len; i++) {
        dest[i] = wctob(value[i]);
    }
    dest[len] = 0;
    *this = dest;

    deleteWString();
    wString_ = (wchar_t *) malloc(sizeof(wchar_t)*(len+1));


#ifdef _MSC_VER
    wcscpy_s(wString_, len+1, value);
#else
    wcsncpy(wString_, value, len+1);
#endif


};

void IvaString::split(char c, vector<IvaString> & out) const
{

    char chars[2];
    chars[0] = c;
    chars[1] = 0;

    split(chars,out);

};

void IvaString::split(char * c, vector<IvaString> & out) const
{

    size_t current;
    size_t pre;

    pre = 0;
    current = find_first_of(c);
    IvaString str;
    str = substr(pre,current-pre);
    out.push_back(str);
    while (current < size()) {
        pre = current + 1;
        current = find_first_of(c,pre);
        str = substr(pre,current-pre);
        out.push_back(str);
    };


};

bool IvaString::isNumber() const
{
    if (empty())
        return false;

    IvaString tmp(*this);
    tmp.trim();

    bool hasComma = false;
    for (size_t i = 0; i != tmp.size(); ++i) {
        if ((tmp.at(i) >= '0' && tmp.at(i) <= '9')
            || (tmp.at(i) == '-' && i == 0))
            continue;
        else {
            if ((tmp.at(i) == ',' && !hasComma)
                || (tmp.at(i) == '.' && !hasComma)) {
                    hasComma = true;
                    continue;
            }
            return false;
        }
    }

    return true;
}

int IvaString::getInt() const
{

    stringstream stream;

    stream.str(*this);

    int out;

    stream >> out;

    return out;

};
