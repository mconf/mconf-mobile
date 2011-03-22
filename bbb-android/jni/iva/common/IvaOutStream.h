#ifndef _IVAOUTSTREAM_H_
#define _IVAOUTSTREAM_H_

#include "IvaOutBuffer.h"
#include "IvaTime.h"

class IvaOutStream
: public basic_stringstream<char>
{

 private:
  IvaOutBuffer _streambuffer;

 public:

  IvaOutStream();

  IvaOutStream(IvaString & file);

  ~IvaOutStream();
  
  IvaOutStream(IvaOutStream & copy);

  IvaOutBuffer * rdbuf() const;

  IvaString str() const;
  
  void str(const IvaString & __s);

  void start(void);

  void stop(void);

};

extern IvaOutStream Log_;

//#define Log (Log_ << IvaTime() << " ")
/**
 *  Insere no arquivo de log e escreve na tela a string passada como parâmetro.
 *  O \n já é adicionado automaticamente.
 */
#define Log(x) (LogData(x).push())

#endif
