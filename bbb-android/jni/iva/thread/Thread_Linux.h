#ifndef _THREAD_LINUX_
#define _THREAD_LINUX_

#include <unistd.h>
#include <asm/unistd.h>
#include <sys/syscall.h>

#define GetCurrentThreadId() syscall(__NR_gettid)

#include <stdint.h>

#endif
