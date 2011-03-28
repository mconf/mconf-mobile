/*
 * common.h
 *
 *  Created on: Apr 22, 2010
 *      Author: lmborba
 */

#ifndef COMMON_H_
#define COMMON_H_

#include "AVConfigs.h"
#include "commonDefs.h"
#include "CommonColor.h"
#include "CommonRect.h"
#include "Directory.h"
#include "ErrorController.h"
#include "ErrorData.h"
#include "errorDefs.h"
#include "ErrorStack.h"
#include "ErrorVector.h"
#include "Folders.h"
#include "IPV4.h"
#include "IvaOutBuffer.h"
#include "IvaOutController.h"
#include "IvaOutLogFile.h"
#include "IvaOutStream.h"
#include "IvaPixFmt.h"
#include "IvaRandom.h"
#include "IvaString.h"
#include "IvaTime.h"
#include "IvaVideoFrame.h"
#include "Location.h"
#include "LogData.h"
#include "CodecClass.h"
#include "SysInfo.h"
#include <Interval.h>
#include <Microseconds.h>
#include <Milliseconds.h>
#include <Mutex.h>
#include <Seconds.h>
#include <Thread.h>
#include <stdint.h>

// "compatibility" só é válido no Windows pois em breve será removido
// desenvolvimento no linux não deve usá-lo!
#ifdef _MSC_VER
#include "common_compatibility.h"
#endif

#endif /* COMMON_H_ */
