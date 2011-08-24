#ifndef _IVAOUTLOGFILE_H_
#define _IVAOUTLOGFILE_H_

#include "IvaString.h"
#include <iostream>
#include <fstream>
#include <Mutex.h>
#include <Thread.h>
#include <ConditionVariable.h>
#include <sstream>
using namespace std;

class IvaOutLogFile
: public Runnable
{

 private:
  ofstream logFile_;
  bool opened_;
  Mutex bufferMutex_;
  ConditionVariable * bufferCondVar_;
  stringstream buffer;

  bool running_;

  void initialize();

 public:

  void threadFunction();
  IvaOutLogFile();
  IvaOutLogFile(const IvaOutLogFile & operand);
  ~IvaOutLogFile();

  void print(IvaString & toPrint);

  IvaOutLogFile & operator= (IvaString &operand);

};

#endif
