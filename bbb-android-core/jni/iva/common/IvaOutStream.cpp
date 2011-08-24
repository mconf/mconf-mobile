#include "CommonLeaks.h"
#include <Mutex.h>
#include "IvaOutStream.h"
#include "IvaOutController.h"
#include "CommonLeaksCpp.h"

IvaOutStream::IvaOutStream(IvaOutStream & copy)
  : basic_stringstream<char, char_traits<char> >()
{

  this->init(&(_streambuffer));
  str(copy.str());

};

IvaOutStream::IvaOutStream()
{
  this->init(&(_streambuffer));

};

IvaOutStream::IvaOutStream(IvaString & file)
{

  this->init(&(_streambuffer));
  _streambuffer.setFile(file);

};

IvaOutStream::~IvaOutStream()
{
};

IvaOutBuffer * IvaOutStream::rdbuf() const
{ 
  return const_cast<IvaOutBuffer*> (&_streambuffer); 
};

IvaString IvaOutStream::str() const
{ 
  return _streambuffer.str(); 
};

void IvaOutStream::str(const IvaString& __s)
{ 
  _streambuffer.str(__s); 
};

void IvaOutStream::start(void)
{

	ivaOutControllerCtx.lock();

}

void IvaOutStream::stop(void)
{

	ivaOutControllerCtx.unlock();

}

IvaOutStream Log_;
