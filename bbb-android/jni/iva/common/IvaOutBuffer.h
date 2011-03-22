#ifndef _IVAOUTBUFFER_H_
#define _IVAOUTBUFFER_H_

#include <iostream>
#include <fstream>
#include <sstream>
#include "IvaString.h"

using namespace std;

class IvaOutBuffer
: public basic_stringbuf<char, char_traits<char> >
{
  
 private:

  void printCurrent();
    
  IvaString file_;  
  bool fileSetted_;
  
 public:
  
  IvaOutBuffer();
  
  ~IvaOutBuffer();
  
  virtual int overflow(int __c);

  virtual int sync();

  void setFile(IvaString & file);

};

#endif
