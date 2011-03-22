#include "CommonLeaks.h"
#include "ErrorController.h"
#include "IvaOutStream.h"
#include "LogData.h"
#include <Seconds.h>
#include "CommonLeaksCpp.h"

//error_context_t errors;

ErrorController::ErrorController() :
  ErrorStack()
{

  
  
};

ErrorController::~ErrorController() 
{

  
  
};

void ErrorController::newError(ErrorData & newErr)
{
    // se repetiu o mesmo erro em um intervalo muito pequeno, ignora-o
    ErrorData &last = getLast();
    if (last.getType() == newErr.getType() &&
        last.getCode() == newErr.getCode() &&
        last.getLocation() == newErr.getLocation() &&
        newErr.getTimestamp() <= last.getTimestamp() + Seconds(ERROR_SUPRESS_SECS)
        ) {
        push(last);
    } else {
        push(newErr);
        LogData log;
        log << newErr << endl;
        log.push();
    }
}

void ErrorController::newWarning(ErrorData & newWarn)
{
    // se repetiu o mesmo erro em um intervalo muito pequeno, ignora-o
    ErrorData &last = getLast();
    if (last.getType() == newWarn.getType() &&
        last.getCode() == newWarn.getCode() &&
        last.getLocation() == newWarn.getLocation() &&
        newWarn.getTimestamp() <= last.getTimestamp() + Seconds(ERROR_SUPRESS_SECS)
        ) {
        push(last);
    } else {
        push(newWarn);
        LogData log;
        log << newWarn << endl;
        log.push();
    }
}

ErrorData & ErrorController::getLast()
{

  return pop();

}

IvaString & ErrorController::getDefaultMsg(ErrorData & error)
{

  return messagesVector[error.getCode()];

};

ErrorController errorContext;
