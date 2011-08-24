/*
 * LogData.h
 *
 *  Created on: 11/05/2010
 *      Author: lmborba
 */

#ifndef LOGDATA_H_
#define LOGDATA_H_

#include <sstream>
#include <iostream>
#include "IvaTime.h"
#include "IvaString.h"
using namespace std;

class LogData
: public basic_stringstream<char, char_traits<char> >
{

	basic_stringbuf<char, char_traits<char> > _streambuffer;

 public:

	LogData(LogData & operand);

    LogData(const IvaString& s);

	LogData();

	/**
	 * Destrutor padrão.
	 */
	~LogData();

    void clear();

	IvaString getMsg();

	LogData & operator= (LogData &operand);

	void push();

	IvaTime & getMoment();

	friend ostream & operator<< (ostream & out, const LogData & print);

	IvaString getCompleteMsg();

 private:

	void deleteTime();

	void setTime();

	IvaTime * time_; ///< \brief Momento em que ocorreu o erro.

};

#endif /* LOGDATA_H_ */
