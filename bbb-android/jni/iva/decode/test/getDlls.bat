mkdir Debug
mkdir Release

call ..\..\..\deps\scripts\copyOne.bat \dlls\avcodec-52.dll .\Debug\
call ..\..\..\deps\scripts\copyOne.bat \dlls\avutil-50.dll .\Debug\
call ..\..\..\deps\scripts\copyOne.bat \dlls\avformat-52.dll .\Debug\
call ..\..\..\deps\scripts\copyOne.bat \dlls\pthreadGC2.dll .\Debug\
call ..\..\..\deps\scripts\copyOne.bat \dlls\pthreadVC2.dll .\Debug\
call ..\..\..\deps\scripts\copyOne.bat \dlls\swscale-0.dll .\Debug\

call ..\..\..\deps\scripts\copyOne.bat \dlls\avcodec-52.dll .\Release\
call ..\..\..\deps\scripts\copyOne.bat \dlls\avutil-50.dll .\Release\
call ..\..\..\deps\scripts\copyOne.bat \dlls\avformat-52.dll .\Release\
call ..\..\..\deps\scripts\copyOne.bat \dlls\pthreadGC2.dll .\Release\
call ..\..\..\deps\scripts\copyOne.bat \dlls\pthreadVC2.dll .\Release\
call ..\..\..\deps\scripts\copyOne.bat \dlls\swscale-0.dll .\Release\
