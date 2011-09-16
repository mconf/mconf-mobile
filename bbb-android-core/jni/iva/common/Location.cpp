#include "CommonLeaks.h"
#include "Location.h"
#include "CommonLeaksCpp.h"

Location::Location()
{

  file_ = "";
  function_ = "";
  line_ = 0;
  lib_ = "";

};

Location::Location(const IvaString& file, const IvaString& function, int line)
{

  vector<IvaString> direction;

#ifdef _MSC_VER
  file.split((char *) "/\\",direction);
#else
  file.split((char *) "/",direction);
#endif

  vector<IvaString>::reverse_iterator iter = direction.rbegin();
  vector<IvaString>::reverse_iterator iter2 = iter + 1;

  if (direction.size() >= 2) {
    file_ = *iter2 + "/" + *iter;
    lib_ = *iter2;
  } else {
    file_ = *iter;
    lib_ = "";
  };
  
  function_ = function;
  line_ = line;

};

Location::Location(const Location & cp)
{

  file_ = cp.file_;
  lib_ = cp.lib_;
  
  function_ = cp.function_;
  line_ = cp.line_;

};

Location & Location::operator= (Location &operand)
{

  file_ = *operand.getFile();
  function_ = *operand.getFunction();
  line_ = operand.getLine();
  lib_ = *operand.getLib();

  return *this;

};

Location::~Location()
{

  

};

IvaString * Location::getFile()
{

  return &file_;

};


IvaString * Location::getFunction()
{
  
  return &function_;

}

int Location::getLine()
{

  return line_;

};

IvaString * Location::getDebugLine()
{

  debugLine_ = file_ + ":" + function_ + ":" + IvaString(line_);

  return &debugLine_;

};

IvaString * Location::getLib()
{

  return &lib_;

};

ostream & operator<<(ostream & out, const Location & toPrint)
{

  //  out << toPrint.file_ + ":" + toPrint.function_ + ":";
  //+ IvaString(toPrint.line_);

  out << *(((Location &) toPrint).getDebugLine());

  return out;

};

bool Location::operator==(const Location &operand)
{
    return (file_ == operand.file_) &&
           (function_ == operand.function_) &&
           (line_ == operand.line_);
}

bool Location::operator!=(const Location &operand)
{
    return !(operator==(operand));
}

