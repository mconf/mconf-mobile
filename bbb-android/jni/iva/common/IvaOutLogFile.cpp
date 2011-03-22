#include "CommonLeaks.h"
#include "IvaOutLogFile.h"
#include "IvaTime.h"
#include "errorDefs.h"
#include "CommonLeaksCpp.h"

void IvaOutLogFile::initialize()
{
    opened_ = false;
    bufferCondVar_ = new ConditionVariable(bufferMutex_);
    if (!bufferCondVar_) {
	  throw E_COMMON_MEMORY_ERROR;
    }

    buffer.str("");
}

IvaOutLogFile::IvaOutLogFile()
{

	initialize();

};

IvaOutLogFile::IvaOutLogFile(const IvaOutLogFile & operand)
{

  initialize();

};

IvaOutLogFile::~IvaOutLogFile()
{

  if (opened_) {

	running_ = false;

	bufferCondVar_->notify();

	join();

    IvaTime current;

    logFile_ << endl << "+-------------------------------------------------------------+" << endl;
    logFile_         << "| Fim da Sessao " << current << IvaString(23,' ')         << "|" << endl;
    logFile_         << "+-------------------------------------------------------------+" << endl;
    
    logFile_.close();
    opened_ = false;

  };
  delete bufferCondVar_;

};

void IvaOutLogFile::print(IvaString & toPrint)
{

  bufferMutex_.lock();
  if (opened_) {
	int preLength = buffer.str().size();
    buffer << toPrint;
    if (!preLength) {
    	bufferCondVar_->notify();
    };
  };
  bufferMutex_.unlock();

};

void IvaOutLogFile::threadFunction()
{

	while (running_ || (buffer.str().size() != 0))
	{

		bufferMutex_.lock();
		while ((buffer.str().size() == 0) && running_) {
			bufferCondVar_->wait();
		};

		logFile_ << buffer.str();
		logFile_.flush();
        //logFile_ << endl;
		buffer.str("");

		bufferMutex_.unlock();

	}

}

IvaOutLogFile & IvaOutLogFile::operator= (IvaString &operand)
{

  if (!opened_){

    logFile_.open(operand.c_str(),ios_base::app);

    IvaTime current;

    logFile_ << endl << "+-------------------------------------------------------------+" << endl;
    logFile_         << "| Nova Sessao " << current << IvaString(25,' ')           << "|" << endl;
    logFile_         << "+-------------------------------------------------------------+" << endl;

    opened_ = true;

    running_ = true;

    run(true);

  };

  return *this;

};
