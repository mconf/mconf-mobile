#include "gtest_Microseconds.h"
#include "gtest_Milliseconds.h"
#include "gtest_Seconds.h"
#include "gtest_Mutex.h"
#include "gtest_Thread.h"
#include <gtest/gtest.h>
#include <iostream>
#include <errno.h>
using namespace std;

int main(int argc, char **argv) {

	::testing::InitGoogleTest(&argc, argv);

	int ret = RUN_ALL_TESTS();

#ifdef _MSC_VER
	if (ret) {
		system("PAUSE");
	};
#endif

	return ret;

}
