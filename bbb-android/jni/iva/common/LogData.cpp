/*
 * LogData.cpp
 *
 *  Created on: 11/05/2010
 *      Author: lmborba
 */

#include "CommonLeaks.h"
#include "LogData.h"
#include "IvaOutStream.h"
#include "CommonLeaksCpp.h"
#include <android/log.h>

LogData::LogData()
{

	str("");
	time_ = NULL;

}

LogData::LogData(LogData & operand)
{

	this->time_ = operand.time_;
	str(operand.str());

}

LogData::LogData(const IvaString& s)
{
    str(s + "\n");
    time_ = NULL;
}

LogData::~LogData() {

	deleteTime();

}

void LogData::clear()
{
    str("");
    setTime();
}

void LogData::push()
{

#ifdef ANDROID
	__android_log_print(ANDROID_LOG_INFO,"IVA Log","%s\n",this->getCompleteMsg().c_str());
#else
	Log_.start();
	Log_ << *this;
	Log_.sync();
	Log_.stop();
#endif

}

IvaTime & LogData::getMoment()
{

	if (time_) {
		return *time_;
	} else {
		setTime();
		return *time_;
	};

}


void LogData::deleteTime()
{

	if (time_) {
		delete time_;
	};
	time_ = NULL;

}

ostream & operator <<(ostream & out, const LogData & print)
{

	LogData & toPrint = (LogData &) print;

	out << toPrint.getMoment() << " " << toPrint.getCompleteMsg();

	return out;

}

LogData & LogData::operator= (LogData & operand)
{

	str(operand.str());
	this->time_ = operand.time_;

	return *this;

}

void LogData::setTime()
{

	deleteTime();
	time_ = new IvaTime;

}

IvaString LogData::getCompleteMsg()
{

	return getMsg();

}

IvaString LogData::getMsg()
{

	return IvaString(str());

}


