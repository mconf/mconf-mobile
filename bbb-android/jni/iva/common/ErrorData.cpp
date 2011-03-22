#include "CommonLeaks.h"
#include "ErrorData.h"
#include "ErrorController.h"
#include "CommonLeaksCpp.h"

ErrorData::ErrorData(const ErrorData &operand)
{

  code_ = ((ErrorData &) operand).getCode();
  ocurrence_ = ((ErrorData &) operand).getLocation();
  str(((ErrorData &) operand).getMsg());
  type_ = ((ErrorData &) operand).getType();
  time_ = new IvaTime(((ErrorData &) operand).getMoment());

};

ErrorData::ErrorData()
{

  str("");
  code_ = 0;
  type_ = ERROR_TYPE_NONE;
  time_ = NULL;

};


ErrorData::ErrorData(int code, Location ocurrence)
{

  setCode(code);
  ocurrence_ = ocurrence;
  type_ = ERROR_TYPE_NONE;
  str("");
  time_ = NULL;
  
};

ErrorData::ErrorData(int code, Location ocurrence, const IvaString& msg)
{

  setCode(code);
  ocurrence_ = ocurrence;
  type_ = ERROR_TYPE_NONE;
  str(msg);
  time_ = NULL;
  
};

void ErrorData::setLocation(Location newLocation)
{

  ocurrence_ = newLocation;

};

Location & ErrorData::getLocation()
{

  return ocurrence_;

};

ErrorData & ErrorData::operator= (ErrorData &operand)
{

  code_ = operand.getCode();
  ocurrence_ = operand.getLocation();
  this->str(operand.getMsg());
  type_ = operand.getType();
  deleteTime();
  time_ = new IvaTime(operand.getMoment());
  timestamp_ = operand.timestamp_;

  return *this;

};

void ErrorData::setCode(int code_in) {
  code_ = code_in;
};

int ErrorData::getCode() {
  return code_;
};

void ErrorData::getLast() {
  
  ErrorData operand;
 
  operand = errorContext.getLast();

  this->operator=(operand);

};

ErrorData::~ErrorData() 
{

  deleteTime();
  
};

IvaString ErrorData::getMsg()
{
  
  return IvaString(str());
};

void ErrorData::deleteTime()
{
  if (time_) {
    delete time_;
  };
  time_ = NULL;
};

void ErrorData::setTime()
{
  deleteTime();
  time_ = new IvaTime;
  timestamp_.setTimestamp();
};

void ErrorData::pushWarning()
{
  
  setTime();
  type_ = ERROR_TYPE_WARNING;
  errorContext.newWarning(*this);

};

void ErrorData::pushError()
{

  setTime();
  type_ = ERROR_TYPE_ERROR;
  errorContext.newError(*this);

};

int ErrorData::getType()
{

  return type_;

};

IvaTime & ErrorData::getMoment()
{

  if (time_) {
    return *time_;
  } else {
    setTime();
    return *time_;
  };

};

Interval & ErrorData::getTimestamp()
{
    return timestamp_;
}

IvaString ErrorData::getCompleteMsg()
{

  return errorContext.getDefaultMsg(*this) + IvaString(": ") + getMsg();

};

ostream & operator<<(ostream & out, const ErrorData & print)
{

  IvaString type;
  ErrorData & toPrint = (ErrorData &) print;
  switch (toPrint.getType()) {
  case ERROR_TYPE_WARNING:
    {
      type = "warning";
      break;
    };
  case ERROR_TYPE_ERROR:
    {
      type = "ERROR";
      break;
    };
  default:
    {
      type = "OK";
    };
  };

  IvaString spaces(41,' ');
  
//  out << toPrint.getMoment() << " ";
  out.fill(' ');
  out.width(7);
  out << left << type;
  out << ": " << "(";
  out.fill('0');
  out.width(5);
  out << right << toPrint.getCode();
  out << ") " << toPrint.getCompleteMsg() << endl;
  out << spaces << toPrint.getLocation();

  return out;

};
