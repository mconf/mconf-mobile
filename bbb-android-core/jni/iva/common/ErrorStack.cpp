#include "CommonLeaks.h"
#include "ErrorStack.h"
#include "CommonLeaksCpp.h"

/*************************************************************
 * Internas
 *************************************************************/ 

ErrorStack::ErrorStack() :
  stack<ErrorData>()
{

}

ErrorStack::~ErrorStack()
{
  
  clear();
  
}

void ErrorStack::clean() {
  
  mutex_.lock();

  clear();

  mutex_.unlock();
    
};

void ErrorStack::clear() {
  
  while (!(stack<ErrorData>::empty())) {
    stack<ErrorData>::pop();
  };
  
};

ostream & operator<<(ostream & out, const ErrorStack & print)
{

  ErrorStack & toPrint = (ErrorStack &) print;

  while (!(toPrint.empty())) {

    out << toPrint.pop();
    
    if (!(toPrint.empty())) {
      out << endl;
    };

  };

  return out;

};


bool ErrorStack::empty() {

  bool ret;

  mutex_.lock();
  ret = stack<ErrorData>::empty();
  mutex_.unlock();

  return ret;

};

void ErrorStack::push(ErrorData & data)
{
  
  mutex_.lock();
  
  stack<ErrorData>::push(data);
  
  mutex_.unlock();
  
};

ErrorData & ErrorStack::pop()
{

//  int ret;
//  int lastItem;
  
  mutex_.lock();
  
  if (size() > 0) {
    toPop = stack<ErrorData>::top();
    stack<ErrorData>::pop();
  }
  
  mutex_.unlock();
  
  return toPop; // código do erro

};
