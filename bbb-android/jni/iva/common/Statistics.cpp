#include "CommonLeaks.h"
#include "Statistics.h"
#include "CommonLeaksCpp.h"

Statistics::Statistics(StatisticsType type) :
    _type(type)
{
    int err;
    if (_type == STATISTICS_OVER_PROCESS) {
        err = init(_type, GetCurrentProcessId());
    } else if (_type == STATISTICS_OVER_THREAD) {
        err = init(_type, GetCurrentThreadId());
    } else {
        NEW_ERROR(E_ERROR, "Tipo inválido ao inicializar Statistics");
        throw E_ERROR;
    }
    if (err != E_OK) {
        throw err;
    }
}

Statistics::Statistics(StatisticsType type, uint32_t id) :
    _type(type)
{
    int err = init(_type, id);
    if (err != E_OK) {
        throw err;
    }
}

int Statistics::init(StatisticsType type, uint32_t id)
{
    LogData log;

    _getTimes = NULL;
    _runThread = false;
    _interval = 0;

    SYSTEM_INFO sysinfo;
    GetSystemInfo(&sysinfo);
    _numberOfProcessors = sysinfo.dwNumberOfProcessors;
    log << "Statistics: Número de CPUs = " << _numberOfProcessors << endl;
    if (_numberOfProcessors == 0) {
        log << "Statistics: Número de CPUs 0, assumindo valor padrão 1" << endl;
        _numberOfProcessors = 1;
    }
    log.push();

    if (type == STATISTICS_OVER_PROCESS) {
        _handle = OpenProcess(PROCESS_QUERY_INFORMATION, 0, id);
        _getTimes = &GetProcessTimes;
    } else if (type == STATISTICS_OVER_THREAD) {
        _handle = OpenThread(THREAD_QUERY_INFORMATION, 0, id);
        _getTimes = &GetThreadTimes;
    } else {
        NEW_ERROR(E_ERROR, "Tipo inválido ao inicializar Statistics");
        return E_ERROR;
    }

    if (!_handle) {
        NEW_ERROR(E_ERROR, "Não foi possível abrir o descritor de informações do processo");
        return E_ERROR;
    }
    if (!_getTimes) {
        NEW_ERROR(E_ERROR, "Não foi possível encontrar a função para buscar informações do sistema");
        return E_ERROR;
    }

    return E_OK;
}

Statistics::~Statistics()
{
    stop();

    if (_handle) CloseHandle(_handle);
}

int Statistics::stop()
{
    if (isRunning()) {
        _runThread = false;
        join();
    }

    return E_OK;
}

int Statistics::start(uint32_t msec)
{
    if (!isRunning()) {
        _interval = msec;
        _runThread = true;
        run(true);
    }

    return E_OK;
}

void Statistics::setInterval(uint32_t value)
{
    _interval = value;
}

uint32_t Statistics::getCpuUsage(uint32_t interval)
{
    uint32_t oldUserTime, oldKernelTime, oldTimestamp,
             newUserTime, newKernelTime, newTimestamp;
    uint16_t pu = 0; // process usage
    LogData log;

    if (getCpuTimes(_handle, &oldUserTime, &oldKernelTime, &oldTimestamp) != E_OK) {
        log << "Statistics: não foi possivel ler informações sobre o processo" << endl;
        log.push();
        return 0;
    }

    Milliseconds(interval).sleep();

    if (getCpuTimes(_handle, &newUserTime, &newKernelTime, &newTimestamp) != E_OK) {
        log << "Statistics: não foi possivel ler informações sobre o processo" << endl;
        log.push();
        return 0;
    }

    int userTime = newUserTime - oldUserTime,
        kernelTime = newKernelTime - oldKernelTime,
        timestamp = newTimestamp - oldTimestamp;

    if (timestamp != 0)
        pu = ((userTime + kernelTime) * 100) / timestamp;

    return pu / _numberOfProcessors;
}

uint32_t Statistics::getMemoryUsage()
{
    PROCESS_MEMORY_COUNTERS mu; // memory usage
    GetProcessMemoryInfo(_handle, &mu, sizeof(mu));

    return (uint32_t) mu.WorkingSetSize / 1024;
}

void Statistics::threadFunction()
{
    uint32_t oldUserTime, oldKernelTime, oldTimestamp,
             newUserTime, newKernelTime, newTimestamp;
    uint32_t pu = 0; // process usage
    LogData log;

    if (getCpuTimes(_handle, &oldUserTime, &oldKernelTime, &oldTimestamp) != E_OK) {
        log << "Statistics: não foi possivel ler informações sobre o processo" << endl;
        log.push();
        return;
    }

    while (_runThread) {
        Milliseconds(_interval).sleep();
        
        if (getCpuTimes(_handle, &newUserTime, &newKernelTime, &newTimestamp) != E_OK) {
            log << "Statistics: não foi possivel ler informações sobre o processo" << endl;
            log.push();
            return;
        }

        int userTime = newUserTime - oldUserTime,
            kernelTime = newKernelTime - oldKernelTime,
            timestamp = newTimestamp - oldTimestamp;

        if (timestamp != 0)
            pu = ((userTime + kernelTime) * 100) / timestamp;

        if (pu >= 0)
            callback(pu/_numberOfProcessors, getMemoryUsage());

        oldUserTime = newUserTime;
        oldKernelTime = newKernelTime;
        oldTimestamp = newTimestamp;
    }

    return;
}

int Statistics::filetimeToInt(FILETIME ftTime, uint32_t * iTime)
{
    SYSTEMTIME stTime;
    LogData log;

    if (FileTimeToSystemTime( &ftTime, &stTime ) == 0) {
        log << "Statistics: erro na funcao FileTimeToSystemTime " << GetLastError() << endl;
        log.push();
        return E_ERROR;
    }

    *iTime = stTime.wSecond * 1000;
    *iTime += stTime.wMilliseconds;

    return E_OK;
}

int Statistics::getCpuTimes(HANDLE processHandle, uint32_t * userTime, uint32_t * kernelTime, uint32_t * timestamp)
{
    FILETIME ftCreate, ftExit, ftUser, ftKernel;
    LogData log;

    *timestamp = Milliseconds().getTime();
    *userTime = 0;
    *kernelTime = 0;

    if (!_getTimes( processHandle, &ftCreate, &ftExit, &ftUser, &ftKernel )) {
        log << "Statistics: erro na funcao GetProcessTimes/GetThreadTimes " << GetLastError() << endl;
        log.push();
        return E_ERROR;
    }

    if (filetimeToInt(ftUser, userTime) != E_OK || filetimeToInt(ftKernel, kernelTime) != E_OK) {
        return E_ERROR;
    }

    return E_OK;
}
