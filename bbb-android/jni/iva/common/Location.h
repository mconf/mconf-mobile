#ifndef _LOCATION_H
#define _LOCATION_H

#include "IvaString.h"

#include <iostream>

using namespace std;

class Location {

 public:

  Location(const Location & cp);
  Location();
  Location(const IvaString& file, const IvaString& function, int line);
  ~Location();
  
  IvaString * getFile();
  IvaString * getLib();
  IvaString * getFunction();
  int getLine();

  IvaString * getDebugLine(); 
  Location & operator=(Location &operand);
  bool operator==(const Location &operand);
  bool operator!=(const Location &operand);

  friend ostream & operator<<(ostream & out, const Location & toPrint);

 private:

  IvaString file_;
  IvaString function_;
  int line_;
  IvaString debugLine_;
  IvaString lib_;

};

#define AT __FILE__,__FUNCTION__,__LINE__

#endif
