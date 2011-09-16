#include "CommonLeaks.h"
#include "IvaOutBuffer.h"
#include "IvaOutController.h"
#include "CommonLeaksCpp.h"

void IvaOutBuffer::printCurrent() 
{
  
  IvaString getStr(this->str());
  
  this->str("");

  if (fileSetted_) {
    ivaOutControllerCtx.print(file_,getStr);
  } else {
    ivaOutControllerCtx.print(getStr);
  };
    
};

IvaOutBuffer::IvaOutBuffer() : 
	basic_stringbuf<char, char_traits<char> >()
{

  fileSetted_ = false;

};

IvaOutBuffer::~IvaOutBuffer()
{
};

int IvaOutBuffer::overflow(int __c = char_traits<char>::eof())
{
  int ret;

  ret = basic_stringbuf<char, char_traits<char> >::overflow(__c);

  printCurrent();

  return ret;
};

int IvaOutBuffer::sync()
{

  int ret;

  ret = basic_stringbuf<char, char_traits<char> >::sync();

  printCurrent();

  return ret;

};

void IvaOutBuffer::setFile(IvaString & file)
{
  
  file_ = file;

  fileSetted_ = true;

};

