#ifndef _IVA_STRING_H
#define _IVA_STRING_H

#include <wchar.h>
#include <string>
#include <vector>

using namespace std;

class IvaString : public string
{

 public:

  IvaString();
  IvaString(const char * text);
  IvaString(const string& text);
  IvaString(int numchars, char character);
  IvaString(const IvaString& operand);
  wchar_t * toWchar();
  void fromWchar(wchar_t * value);
  ~IvaString();
  IvaString(int number);
  void translate(char oldC, char newC);
  void trim();
  void rTrim();
  void lTrim();
  void split(char c, vector<IvaString> & out) const;
  void split(char * c, vector<IvaString> & out) const;
  bool isNumber() const;
  int getInt() const;

 private:

  wchar_t * wString_;

  void Init();
  void deleteWString();

};

#endif

