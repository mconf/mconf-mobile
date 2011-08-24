#ifndef _COMMONCLASS_H
#define _COMMONCLASS_H

#include "CommonSock.h"

class Common : 
 public Error 
{
  
 public:
  Common();
  ~Common();

  bool directoryCreation(const char * file);

  unsigned int getTimestamp();

  wchar_t * charToWchar(char * str);

  void logInternal(FILE * file, const char *fmt, ...);

  void log(const char *fmt, ...);
  
  void logAt(char *location, const char *fmt, ...);

  void logPrintf(const char *description, ...);

  void logPrintfAt(char *location, const char *description, ...);
  
  void printfAt(char *location, const char *description, ...);

  void fLog(FILE * file, const char *fmt, ...);

  void fLogAt(FILE * file, char *location, const char *fmt, ...);

  void fLogPrintf(FILE * file, const char *description, ...);

  void fLogPrintfAt(FILE * file, char *location, const char *description, ...);
  
  int logCloseFile(FILE * file);

  FILE * logOpenFile(char *filename);

  int logGetDefaultName(char *name, int namesize);

  void sleep(int msec);

  void usleep(int usec);

  int iToChar(char **str, uint32_t value);

  int validateIPv4(char *ip, uint8_t multicast);
  
  uint16_t getDB(int level, int numLevels);

  int getVULevel(double amp, int numLevels);
  
  uint32_t xToI(const char* xs);

  int strTrim(char *str);

  void strRep(char *str, char oldchar, char newchar);

  char * parseAtStr(const char * location);

  char * formatChatMsg(char *msg, char *user);

  char * formatChatSystemMsg(char *msg, char *user);

 private:
  FILE * logFile;
  pthread_mutex_t logMutex;
  CommonSock * SocketsAdmin;

};


#endif
