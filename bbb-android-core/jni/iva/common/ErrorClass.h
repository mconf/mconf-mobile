#ifndef _ERRORCLASS_H_
#define _ERRORCLASS_H_

#include "ErrorQueue.h"

class Error : 
  public ErrorQueue
{

 public:
  Error();
  ~Error();
  void newError(const char * location, int code, const char * description, ...);
  void newWarning(const char * location, int code, const char * description, ...);
  int getLast(string & msg, int & level);

};

#endif
